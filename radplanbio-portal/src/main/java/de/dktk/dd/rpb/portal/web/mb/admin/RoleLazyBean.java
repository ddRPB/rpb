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
import de.dktk.dd.rpb.core.domain.admin.Role;
import de.dktk.dd.rpb.core.domain.admin.Role_;
import de.dktk.dd.rpb.core.repository.admin.RoleRepository;
import de.dktk.dd.rpb.portal.web.mb.support.GenericLazyDataModel;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bean for administration of RadPlanBio roles (permissions)
 *
 * @author tomas@skripcak.net
 * @since 01 Apr 2016
 */
@Named("mbRoleLazy")
@Scope(value="view")
public class RoleLazyBean extends GenericLazyDataModel<Role, Integer> {

    //region Constructors

    @Inject
    public RoleLazyBean(RoleRepository repository) {
        super(repository);
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
        results.add(Boolean.FALSE); // Id
        results.add(Boolean.TRUE); // Name
        results.add(Boolean.TRUE); // Description

        return results;
    }

    @Override
    protected Role getEntity(Map<String, Object> filters) {
        Role example = this.repository.getNew();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            switch (entry.getKey()) {
                case "name":
                    example.setName(entry.getValue().toString());
                    break;
                case "description":
                    example.setDescription(entry.getValue().toString());
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
        return searchParameters.orderBy(Role_.name, OrderByDirection.ASC);
    }

    //endregion

}
