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

import de.dktk.dd.rpb.core.domain.ctms.StudyTagType;
import de.dktk.dd.rpb.core.repository.admin.ctms.IStudyTagTypeRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for user administration of CTMS study phases
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbStudyTagType")
@Scope(value="view")
public class StudyTagTypeBean extends CrudEntityViewModel<StudyTagType, Integer> {

    //region Injects

    //region Repository

    @Inject
    private IStudyTagTypeRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IStudyTagTypeRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IStudyTagTypeRepository repository) {
        this.repository = repository;
    }

    //endregion

    //endregion

    //region members

    private List<StudyTagType> requiredStudyTagTypes;
    private List<Boolean> tagTypeVisibilityList;

    //endregion

    //region Properties

    public List<Boolean> getTagTypeVisibilityList() {
        return this.tagTypeVisibilityList;
    }

    public List<StudyTagType> getRequiredStudyTagTypes() {
        return this.requiredStudyTagTypes;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {

        this.columnVisibilityList = new ArrayList<Boolean>();
        this.columnVisibilityList.add(Boolean.TRUE); // Name
        this.columnVisibilityList.add(Boolean.FALSE); // Description
        this.columnVisibilityList.add(Boolean.TRUE); // isRequired
        this.columnVisibilityList.add(Boolean.TRUE); // isBoolean
        this.columnVisibilityList.add(Boolean.FALSE); // regEx
        this.columnVisibilityList.add(Boolean.FALSE); // max occurence

        this.setPreSortOrder(
                this.buildSortOrder()
        );

        this.load();

        // Show All TagTypes
        this.tagTypeVisibilityList = new ArrayList<Boolean>(); // All tag types should be hidden in studies list (in columns)
        for (int i = 0; i < this.entityList.size(); i++) {
            this.tagTypeVisibilityList.add(Boolean.FALSE);
        }

        // Which tags are required
        StudyTagType requiredTagTypeExample = new StudyTagType();
        requiredTagTypeExample.setIsBoolean(Boolean.TRUE);
        this.requiredStudyTagTypes = repository.find(requiredTagTypeExample);
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
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
        UIComponent column = viewRoot.findComponent(":form:tabView:dtEntities:colTagTypeName");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colTagTypeName");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    //endregion

}