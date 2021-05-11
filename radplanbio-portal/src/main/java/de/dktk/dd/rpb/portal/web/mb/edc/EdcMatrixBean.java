/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.EnumFormDataStatus;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.repository.edc.IStudySubjectRepository;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel bean for study centric patient EDC matrix view for monitoring of eCRF data collection progress
 *
 * @author tomas@skripcak.net
 * @since 23 Aug 2016
 */
@Named("mbEdcMatrix")
@Scope("view")
public class EdcMatrixBean extends CrudEntityViewModel<StudySubject, Integer> {

    //region Members

    @SuppressWarnings("unused")
    private IStudySubjectRepository repository; // Just dummy to satisfy CrudEntityViewModel (not actually used)

    private MainBean mainBean;
    private StudyIntegrationFacade studyIntegrationFacade;

    private Study rpbStudy;

    //endregion

    //region Constructors

    @Inject
    public EdcMatrixBean(MainBean mainBean, StudyIntegrationFacade studyIntegrationFacade) {
        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
    }

    //endregion

    //region Properties

    @Override
    public IStudySubjectRepository getRepository() {
        return this.repository;
    }

    public Study getRpbStudy() {
        return this.rpbStudy;
    }

    public void setRpbStudy(Study rpbStudy) {
        this.rpbStudy = rpbStudy;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        this.load();
    }

    //endregion

    //region Commands

    public String loadFormDataStatus(EventData eventData, String formDefinitionOid) {
        String result = EnumFormDataStatus.NOT_STARTED.toString();

        FormData formData = eventData.findFormData(formDefinitionOid);
        if (formData != null) {
            result = formData.getStatus();
        }

        return result;
    }

    public String loadFormDataLegend(EventData eventData, String formDefinitionOid) {
        String result = "";

        FormData formData = eventData.findFormData(formDefinitionOid);
        if (formData != null) {
            if (formData.getInterviewerName() != null) {
                result += formData.getInterviewerName() + " ";
            }
            if (formData.getInterviewDate() != null) {
                result += "[" + formData.getInterviewDate() + "]";
            }
        }

        return result;
    }

    public String loadFormDataColourStatus(EventData eventData, String formDefinitionOid) {
        String result = "";

        FormData formData = eventData.findFormData(formDefinitionOid);
        if (formData != null) {
            if (formData.getStatus().equals(EnumFormDataStatus.COMPLETE.toString())) {
                result = "SUCCESS";
            }
            else if (formData.getStatus().equals(EnumFormDataStatus.INITIAL.toString())) {
                result = "PARTIAL_SUCCESS";
            }
            else {
                result = "FAILURE";
            }
        }

        return result;
    }

    //endregion

    //region Overrides

    @Override
    public void load() {
        try {
            // Init Facade to use service that are valid for logged user PartnerSite (from mainBean)
            this.studyIntegrationFacade.init(this.mainBean);
            this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);

            this.rpbStudy = this.studyIntegrationFacade.loadStudyWithMetadata();
            this.entityList = this.studyIntegrationFacade.loadOdmStudySubjects();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        // NOOP
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return list of sort meta elements for data table sorting
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colStudySubjectId", "colStudySubjectId", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<>();

        result.add(Boolean.TRUE); // StudySubjectID
        result.add(Boolean.FALSE); // PID
        result.add(Boolean.FALSE); // SecondaryID
        result.add(Boolean.FALSE); // Gender

        return result;
    }

    //endregion

}
