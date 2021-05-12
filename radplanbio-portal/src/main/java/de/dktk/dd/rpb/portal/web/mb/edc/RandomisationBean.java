/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.criteria.AbstractCriterion;
import de.dktk.dd.rpb.core.domain.criteria.constraints.AbstractConstraint;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.domain.randomisation.PrognosticVariable;
import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;
import de.dktk.dd.rpb.core.domain.randomisation.TrialSubject;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;
import de.dktk.dd.rpb.core.repository.ctms.ITrialSubjectRepository;
import de.dktk.dd.rpb.core.repository.edc.ISubjectRepository;

import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.apache.maven.artifact.versioning.ComparableVersion;

import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Randomisation view model
 *
 * @author tomas@skripcak.net
 * @since 31 Jan 2013
 */
@Named("mbRandomisation")
@Scope("view")
public class RandomisationBean extends CrudEntityViewModel<Subject, Integer> {

    //region Injects

    //region Main bean

    @Inject
    private MainBean mainBean;

    //endregion

    //region Study integration facade

    @Inject
    private StudyIntegrationFacade studyIntegrationFacade;

    //endregion

    //region Study repository

    @Inject
    private IStudyRepository studyRepository;

    //endregion

    //region TrialSubjectRepository

    @Inject
    private ITrialSubjectRepository trialSubjectRepository;

    //endregion

    //region Repository - Dummy

    public ISubjectRepository getRepository() {
        return null;
    }

    //endregion

    //endregion

    //region Members

    private Study rpbActiveStudy;
    private TrialSubject newTrialSubject;

    private BarChartModel armsBarModel;

    //endregion

    //region Properties

    //region New trial subject

    public TrialSubject getNewTrialSubject() {
        return this.newTrialSubject;
    }

    public void setNewTrialSubject(TrialSubject subject) {
        this.newTrialSubject = subject;
    }

    //endregion

    //region RadPlanBio active study

    public Study getRpbActiveStudy() {
        return this.rpbActiveStudy;
    }

    public void setRpbActiveStudy(Study study) {
        this.rpbActiveStudy = study;
    }

    //endregion

    //region BarChartModel

    public BarChartModel getArmsBarModel() {
        return armsBarModel;
    }

    public void setArmsBarModel(BarChartModel armsBarModel) {
        this.armsBarModel = armsBarModel;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setPreSortOrder(
            this.buildSortOrder()
        );
    }

    //endregion

    //region Event Handlers

    /**
     * Randomise subject and create a randomised trial subject
     */
    public void doRandomise() {
        try {
            // Randomly generate arm
            // This should depend on the decentralisation and principal site setting
            TreatmentArm assignedArm = this.rpbActiveStudy.getRandomisationConfiguration().getScheme().randomise(newTrialSubject);

            this.newTrialSubject.setOcStudySubjectId(this.selectedEntity.getStudySubjectId());
            this.newTrialSubject.setTreatmentArm(assignedArm);
            assignedArm.addSubject(this.newTrialSubject);

            if (this.isPrincipalSite()) {

                // I have two domain aggregates I need to merge... have to do it in one transaction
                this.studyRepository.merge(rpbActiveStudy);
                this.trialSubjectRepository.merge(this.newTrialSubject);
            }
            else {
                throw new Exception("Randomisation cannot be done because this is not the principal partner site and decentral randomisation is not implemented.");
            }

            this.messageUtil.infoEntity("status_saved_ok", this.newTrialSubject);

            this.reload();
            this.prepareNewEntity();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

//    /**
//     * Import randomised arm into EDC system
//     */
//    public void doImportArm() {
//        try {
//
//            // Find the CRF annotation for this study where the arm should be imported
//            // Construct valid ODM XML for import
//            // Use OC WS to import the data
//
//        }
//        catch (Exception err) {
//            this.messageUtil.error(err);
//        }
//    }

    //endregion

    //region Commands

    /**
     * onLoad is associated with preRenderView and it will execute the first time (ignoring post backs)
     */
    public void onLoad() {
        try {
            if (!FacesContext.getCurrentInstance().isPostback()) {

                // Init empty model
                this.armsBarModel = new BarChartModel();

                // Local active RPB study
                this.studyIntegrationFacade.init(this.mainBean);
                this.rpbActiveStudy = this.studyIntegrationFacade.loadStudy();

                if (this.isRandomisedTrial()) {
                    this.reload();
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void reload() {
        try {
            if (this.isRandomisedTrial()) {

                this.studyIntegrationFacade.init(this.mainBean);
                // Faster load with new OC REST APIs
                if (new ComparableVersion(
                        this.mainBean.getMyAccount().getPartnerSite().getEdc().getVersion()
                )
                        .compareTo(
                                new ComparableVersion("3.7")
                        ) >= 0) {
                    this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);
                }

                List<Subject> ocSubjects = this.studyIntegrationFacade.loadSubjects();

                // Subject which have been randomised for RadPlanBio study in specific site
                List<TrialSubject> randomisedSubjects;

                if (this.isPrincipalSite()) {

                    //TODO: Make the search for subject according to study as well

                    // Search for existing trial subject for logged user partner site
                    TrialSubject trialSubjectQuery = this.trialSubjectRepository.getNew();
                    trialSubjectQuery.setTrialSite(
                            this.mainBean.getMyAccount().getPartnerSite()
                    );

                    randomisedSubjects = this.trialSubjectRepository.find(trialSubjectQuery);
                }
                else {
                    throw new Exception("Cannot load randomised subjects because this is not the principal partner site and decentral randomisation is not implemented.");
                }

                // Compose subjects aggregate RadPlanBio subjects association with trial subject for randomisation
                // This is a good candidate for moving the code to study integration facade
                this.entityList = new ArrayList<>();
                for (Subject ocSubject : ocSubjects) {

                    Subject subject = new Subject();
                    subject.setUniqueIdentifier(ocSubject.getUniqueIdentifier());
                    subject.setStudySubjectId(ocSubject.getStudySubjectId());
                    subject.setSecondaryId(ocSubject.getSecondaryId());
                    subject.setGender(ocSubject.getGender());

                    TrialSubject alreadyRandomisedSubject = null;
                    for (TrialSubject ts : randomisedSubjects) {
                        if (ts.getOcStudySubjectId().equals(ocSubject.getStudySubjectId())) {
                            alreadyRandomisedSubject = ts;
                            break;
                        }
                    }

                    if (alreadyRandomisedSubject != null) {
                        subject.setArm(alreadyRandomisedSubject.getTreatmentArm());
                    }

                    entityList.add(subject);
                }

                // Reload plot visualisation
                this.reloadArmsBarModel();

                // Prepare new data for UI data binding
                this.prepareNewEntity();
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Prepares bar plot model for arms and subjects
     */
    public void reloadArmsBarModel() {
        this.armsBarModel = new BarChartModel();

        this.armsBarModel.setTitle(this.rpbActiveStudy.getName() + " Randomisation Progress");
        this.armsBarModel.setLegendPosition("ne");
        this.armsBarModel.setShowPointLabels(Boolean.TRUE);

        Axis xAxis = this.armsBarModel.getAxis(AxisType.X);
        xAxis.setMin(1);
        xAxis.setMax(1);

        Axis yAxis = this.armsBarModel.getAxis(AxisType.Y);
        yAxis.setLabel("Subjects");
        yAxis.setMin(0);

        // If we know expected total enrolment we can better calculate the axis range
        if (this.rpbActiveStudy.getExpectedTotalEnrollment() != null) {

            // Exact treatment arm sizes;
            int yAxisMax = this.rpbActiveStudy.getExpectedTotalEnrollment() / this.rpbActiveStudy.getTreatmentArms().size();

            // Multi-centre
            if (this.rpbActiveStudy.getParticipatingSites() != null) {
                yAxisMax /=  this.rpbActiveStudy.getParticipatingSites().size();
            }

            yAxis.setMax(yAxisMax);
        }
        yAxis.setTickFormat("%d");

        for (TreatmentArm arm : this.rpbActiveStudy.getTreatmentArms()) {
            ChartSeries armSeries = new ChartSeries();
            armSeries.setLabel(arm.getName());
            armSeries.set("Arms", arm.getSubjects().size());

            this.armsBarModel.addSeries(armSeries);
        }
    }

    //endregion

    //region Private Methods

    private boolean isPrincipalSite() {
        // TODO: when decentral randomisation will be implemented enable checking for principal site
        return true;
        //return this.rpbActiveStudy.getPartnerSite().getIdentifier().equals(this.rpbAccount.getPartnerSite().getIdentifier());
    }

    private boolean isRandomisedTrial() {
        if (this.rpbActiveStudy == null) {
            this.messageUtil.info("There is no RPB study defined for your current EDC active study.");
            return false;
        }
        else if (!this.rpbActiveStudy.getIsRandomisedClinicalTrial()) {
            this.messageUtil.info("To access RPB subject randomisation the active study have to be randomised clinical trial.");
            return false;
        }

        return true;
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        List<AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>>> principalSiteStudySubjectCriteria = null;
        if (this.isPrincipalSite()) {
            principalSiteStudySubjectCriteria = this.rpbActiveStudy.getSubjectCriteria();
        }
        else {
            // I have to contact principal study site over REST and get the RPB study from there
            // NOOP
        }

        this.newTrialSubject = this.trialSubjectRepository.getNew();

        //TODO: maybe better load site according to selected subject PID (in case somebody gets idea that our DD users will randomise their subjects)
        this.newTrialSubject.setTrialSite(this.mainBean.getMyAccount().getPartnerSite());

        List<PrognosticVariable<?>> variables = new ArrayList<>();
        for (AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> criterion : principalSiteStudySubjectCriteria) {
            PrognosticVariable<String> variable = new PrognosticVariable<>((AbstractCriterion<String, ? extends AbstractConstraint<String>>) criterion);
            variables.add(variable);
            variable.setSubject(newTrialSubject);
        }
        this.newTrialSubject.setPrognosticVariables(variables);
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtSubjects:colStudySubjectId", "colStudySubjectId", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    //endregion

}
