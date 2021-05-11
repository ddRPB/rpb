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

package de.dktk.dd.rpb.portal.web.mb.admin;

import de.dktk.dd.rpb.core.dao.support.OrderByDirection;
import de.dktk.dd.rpb.core.dao.support.SearchParameters;
import de.dktk.dd.rpb.core.domain.admin.RtStruct;
import de.dktk.dd.rpb.core.domain.admin.RtStructType;
import de.dktk.dd.rpb.core.domain.admin.RtStruct_;
import de.dktk.dd.rpb.core.repository.admin.RtStructRepository;
import de.dktk.dd.rpb.core.repository.admin.RtStructTypeRepository;
import de.dktk.dd.rpb.portal.web.mb.support.GenericLazyDataModel;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bean for administration of RadPlanBio RtStruct domain entities
 *
 * @author tomas@skripcak.net
 * @since 01 Apr 2016
 */
@Named("mbStructLazy")
@Scope(value="view")
public class StructLazyBean extends GenericLazyDataModel<RtStruct, Integer> {

    //region Members

    private RtStructTypeRepository rtStructTypeRepository;

    //endregion

    //region Constructors

    @Inject
    public StructLazyBean(RtStructRepository repository, RtStructTypeRepository rtStructTypeRepository) {
        super(repository);
        this.rtStructTypeRepository = rtStructTypeRepository;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.columnVisibilityList = this.buildColumnVisibilityList();
    }

    //endregion

    //region Overrides

    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        // Initial visibility of columns
        results.add(Boolean.TRUE); // Identifier
        results.add(Boolean.TRUE); // Name
        results.add(Boolean.FALSE); // Description
        results.add(Boolean.TRUE); // Type

        return results;
    }

    @Override
    protected RtStruct getEntity(Map<String, Object> filters) {
        RtStruct example = this.repository.getNew();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            switch (entry.getKey()) {
                case "identifier":
                    example.setIdentifier(entry.getValue().toString());
                    break;
                case "name":
                    example.setName(entry.getValue().toString());
                    break;
                case "description":
                    example.setDescription(entry.getValue().toString());
                    break;
                case "type":
                    example.setType(this.rtStructTypeRepository.getNew());
                    example.getType().setName(((RtStructType) entry.getValue()).getName());
                    break;
            }
        }

        return example;
    }

    @Override
    protected SearchParameters searchParameters() {
        return new SearchParameters() //
                .anywhere() //
                .caseInsensitive();
    }

    @Override
    protected SearchParameters defaultOrder(SearchParameters searchParameters) {
        return searchParameters.orderBy(RtStruct_.name, OrderByDirection.ASC);
    }

    //endregion

}
