/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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
 * Bean for user administration of CTMS study tag types
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
     * Get StudyTagTypeRepository
     * @return StudyTagTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IStudyTagTypeRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StudyTagTypeRepository
     * @param repository StudyTagTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IStudyTagTypeRepository repository) {
        this.repository = repository;
    }

    //endregion

    //endregion

    //region Members

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

        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        this.load();

        // Show All TagTypes
        this.tagTypeVisibilityList = new ArrayList<>(); // All tag types should be hidden in studies list (in columns)
        for (int i = 0; i < this.entityList.size(); i++) {
            this.tagTypeVisibilityList.add(Boolean.FALSE);
        }

        // Which tags are required
        StudyTagType requiredTagTypeExample = new StudyTagType();
        requiredTagTypeExample.setIsRequired(Boolean.TRUE);
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

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colTagTypeName", "colTagTypeName", SortOrder.ASCENDING);
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

        result.add(Boolean.TRUE); // Name
        result.add(Boolean.FALSE); // Description
        result.add(Boolean.TRUE); // isRequired
        result.add(Boolean.TRUE); // isBoolean
        result.add(Boolean.FALSE); // regEx
        result.add(Boolean.FALSE); // max occurrence
        
        return result;
    }

    //endregion

}