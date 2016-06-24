/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
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
public class RandomisationBean  extends CrudEntityViewModel<Subject, Integer> {

    //region Injects

    //region Main bean

    @Inject
    private MainBean mainBean;

    /**
     * Set MainBean
     *
     * @param bean MainBean
     */
    @SuppressWarnings("unused")
    public void setMainBean(MainBean bean) {
        this.mainBean = bean;
    }

    //endregion

    //region Study integration facade

    @Inject
    private StudyIntegrationFacade studyIntegrationFacade;

    @SuppressWarnings("unused")
    public void setStudyIntegrationFacade(StudyIntegrationFacade value) {
        this.studyIntegrationFacade = value;
    }

    //endregion

    //region Study repository

    @Inject
    private IStudyRepository studyRepository;

    @SuppressWarnings("unused")
    public void setStudyRepository(IStudyRepository value) {
        this.studyRepository = value;
    }

    //endregion

    //region TrialSubjectRepository

    @Inject
    private ITrialSubjectRepository trialSubjectRepository;

    @SuppressWarnings("unused")
    public void setTrialSubjectRepository(ITrialSubjectRepository repository) {
        this.trialSubjectRepository = repository;
    }


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

                // Local active RPB study
                this.studyIntegrationFacade.init(this.mainBean);

                this.rpbActiveStudy = this.studyIntegrationFacade.loadStudy();

                if (this.rpbActiveStudy == null) {
                    throw new Exception("There is no RPB study defined for your current active study in OpenClinica.");
                }
                if (!this.rpbActiveStudy.getIsRandomisedClinicalTrial()) {
                    throw new Exception("To access RadPlanBio subject randomisation feature the active study have to be randomised clinical trial.");
                }

                this.reload();
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void reload() {
        try {
            if (this.rpbActiveStudy == null) {
                throw new Exception("There is no RPB study defined for your current active study in OpenClinica.");
            }
            if (!this.rpbActiveStudy.getIsRandomisedClinicalTrial()) {
                throw new Exception("To access RadPlanBio subject randomisation feature the active study have to be randomised clinical trial.");
            }

            this.studyIntegrationFacade.init(this.mainBean);
            // Faster load with new OC REST APIs
            if (new ComparableVersion(
                    this.mainBean.getMyAccount().getPartnerSite().getEdc().getVersion()
                    )
                    .compareTo(
                            new ComparableVersion("3.7")
                    ) >= 0) {
                this.studyIntegrationFacade.setRetreiveStudySubjectOID(Boolean.FALSE);
            }

            List<Subject> ocSubjects = this.studyIntegrationFacade.loadSubjects();

            // Subject which have been randomised for RadPlanBio study in specific site
            List<TrialSubject> randomisedSubjects;

            if (this.isPrincipalSite()) {
                randomisedSubjects = this.trialSubjectRepository.find(); //TODO: Make the select accroding to study and site
            }
            else {
                throw new Exception("Cannot load randomised subjects because this is not the principal partner site and decentral randomisation is not implemented.");
            }

            // Compose subjects aggregate RadPlanBio subjects association with trial subject for randomisation
            // This is a good candidate for moving the code to study integration facade
            this.entityList = new ArrayList<Subject>();
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

            // Prepare new data for UI data binding
            this.prepareNewEntity();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Private Methods

    private boolean isPrincipalSite() {
        // TODO: when decentral randomisation will be implemented enable checking for principal site
        return true;
        //return this.rpbActiveStudy.getPartnerSite().getIdentifier().equals(this.rpbAccount.getPartnerSite().getIdentifier());
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
//        else {
//            // I have to contact principal study site over REST and get the RPB study from there
//        }

        this.newTrialSubject = this.trialSubjectRepository.getNew();
        this.newTrialSubject.setTrialSite(this.mainBean.getMyAccount().getPartnerSite());

        List<PrognosticVariable<?>> variables = new ArrayList<PrognosticVariable<?>>();

        for (AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> criterion : principalSiteStudySubjectCriteria) {

            PrognosticVariable<String> variable = new PrognosticVariable<String>((AbstractCriterion<String,? extends AbstractConstraint<String>>) criterion);
            variables.add(variable);
            variable.setSubject(newTrialSubject);
        }
        this.newTrialSubject.setPrognosticVariables(variables);
    }

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtSubjects:colStudySubjectId");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colStudySubjectId");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    //endregion

}
