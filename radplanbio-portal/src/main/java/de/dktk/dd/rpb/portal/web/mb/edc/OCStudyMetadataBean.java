/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.repository.edc.IItemDefinitionRepository;
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
 * Bean for EDC study metadata view
 *
 * @author tomas@skripcak.net
 * @since 12 Aug 2016
 */
@Named("mbMetadata")
@Scope("view")
public class OCStudyMetadataBean extends CrudEntityViewModel<ItemDefinition, Integer> {

    //region Members

    private MainBean mainBean;
    private Study rpbStudy;

    //endregion

    //region Properties

    //region Repository - Dummy

    public IItemDefinitionRepository getRepository() {
        return null;
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public OCStudyMetadataBean(MainBean mainBean) {

        this.mainBean = mainBean;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init(){
        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        this.reload();
    }

    //endregion

    //region Commands

    /**
     * Reload study metadata from EDC OpenClinica
     */
    public void reload() {
        try {
            this.rpbStudy = this.mainBean.getStudyIntegrationFacade().loadStudyWithMetadata();
            if (this.rpbStudy != null) {
                this.entityList = this.rpbStudy.getEdcStudy().findItemDefinitions();
            }
            else {
                messageUtil.warning("There is no RPB study defined for your current EDC active study.");
            }
        }
        catch (Exception err) {
            messageUtil.error(err);
        }
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        // NOOP
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colItemDefinitionOid", "colItemDefinitionOid", SortOrder.ASCENDING);
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

        result.add(Boolean.FALSE); // item OID
        result.add(Boolean.TRUE); // name
        result.add(Boolean.TRUE); // description
        result.add(Boolean.FALSE); // units
        result.add(Boolean.FALSE); // left text (label)
        result.add(Boolean.TRUE); // data type
        result.add(Boolean.FALSE); // length
        result.add(Boolean.FALSE); // response type
        result.add(Boolean.FALSE); // response layout
        result.add(Boolean.TRUE); // response
        result.add(Boolean.FALSE); // default value
        result.add(Boolean.FALSE); // phi
        result.add(Boolean.FALSE); // shown
        result.add(Boolean.FALSE); // group
        result.add(Boolean.FALSE); // form
        result.add(Boolean.FALSE); // event

        return result;
    }

    //endregion

}
