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

import de.dktk.dd.rpb.core.domain.edc.StudySubject;

import de.dktk.dd.rpb.core.repository.edc.IStudySubjectRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean for StudySbuject
 *
 * @author tomas@skripcak.net
 * @since 02 Nov 2015
 */
@Named("mbStudySubject")
@Scope(value="view")
public class StudySubjectBean extends CrudEntityViewModel<StudySubject, Integer> {

    //region Injects

    //region Repository - Dummy

    private IStudySubjectRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IStudySubjectRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IStudySubjectRepository repository) {
        this.repository = repository;
    }

    //endregion

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

    //region Members

    private static Map<String, Object> genderValues;

    //endregion

    //region Properties

    //region Gender Values

    static {
        genderValues = new LinkedHashMap<String,Object>();
        genderValues.put("Male", "m"); //label, value
        genderValues.put("Female", "f");
    }

    public Map<String,Object> getGenderValues() {
        return genderValues;
    }

    //endregion

    //endregion

    //region Overrides

    @Override
    public void load() {
        // NOOP
    }

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        // NOOP
    }

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtStudySubjects:colStudySubjectId");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colStudySubjectId");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<Boolean>();

        // Initial visibility of columns
        results.add(Boolean.TRUE); // SSID
        results.add(Boolean.TRUE); // Gender
        results.add(Boolean.TRUE); // EnrollmentDate
        results.add(Boolean.TRUE); // StudyName

        return results;
    }

    //endregion

}