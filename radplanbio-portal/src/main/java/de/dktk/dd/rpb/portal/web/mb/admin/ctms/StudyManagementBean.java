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

package de.dktk.dd.rpb.portal.web.mb.admin.ctms;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;

import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import org.openclinica.ws.beans.StudyType;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.context.FacesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for RadPlanBio study management.
 *
 * This is basically the implementation of ViewModel from MVVM architecture
 * It abstract model to properties which are used to hold View data and
 * and functional part of domain model is accessed via commands which are
 * mapped to action in UI View.
 *
 * @author tomas@skripcak.net
 * @since 13 Sep 2013
 */
@Named("mbStudyManagement")
@Scope("view")
public class StudyManagementBean extends CrudEntityViewModel<Study, Integer> {

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

    //region Repository

    @Inject
    private IStudyRepository repository;

    /**
     * Get StudyRepository
     * @return repository
     */
    public IStudyRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StudyRepository
     * @param value - StudyRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IStudyRepository value) {
        this.repository = value;
    }

    //endregion

    //endregion

    //region Members

    private List<StudyType> studyTypeList; // OC studies
    private String selectedStudyName;

    //endregion

    //region Properties

    //region StudyTypeList

    /**
     * Get Study Type List
     *
     * @return List - Study Type List
     */
    public List<StudyType> getStudyTypeList() {
        return this.studyTypeList;
    }

    /**
     * Set Study Type List
     *
     * @param value - Study Type List
     */
    @SuppressWarnings("unused")
    public void setStudyType(List<StudyType> value) {
        this.studyTypeList = value;
    }

    //endregion

    //region SelectedStudyName

    public String getSelectedStudyName() {
        return this.selectedStudyName;
    }

    public void setSelectedStudyName(String value) {
        this.selectedStudyName = value;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    /**
     * Load data from injected dependencies which are not available in bean constructor
     */
    public void init() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        // Preparation ready now you can execute reload command
        this.reload();
    }

    //endregion

    //region Commands

    /**
     * Reload fresh data which should be presented in this view
     * from data storage repository
     */
    public void reload() {
        try {
            // Load study entities
            super.load();

            // Reload from web service
            this.studyTypeList = this.mainBean.getOpenClinicaService().listAllStudies();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Create new RadPlanBio study with association to OpenClinica study
     */
    public void doCreateStudy() {
        try {
            if (this.selectedStudyName != null) {
                for (StudyType st : this.getStudyTypeList()) {
                    if (st.getName().equals(this.selectedStudyName)) {
                        de.dktk.dd.rpb.core.ocsoap.types.Study study = new de.dktk.dd.rpb.core.ocsoap.types.Study(st);
                        this.newEntity.setOcStudyIdentifier(study.getStudyIdentifier());
                    }
                }
            }
            else {
                throw new Exception("You have to choose OC identifier if you want to create a new RadPlanBio study.");
            }

            // Create a BusinessRulesValidator which will be checking business rules which need database
            Study existing = this.repository.getByOcStudyIdentifier(this.newEntity.getOcStudyIdentifier());
            if (existing != null) {
                throw new Exception("RadPlanBiostudy with this OC identifier: " + existing.getOcStudyIdentifier() + " already exists!");
            }

            // Business rules OK so persist the entity
            this.repository.save(this.newEntity);

            this.messageUtil.info("status_saved_ok", this.newEntity);

            // Reload and prepare new
            this.reload();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtStudies:colStudyOcIdentifier");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colStudyOcIdentifier");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    //endregion

}
