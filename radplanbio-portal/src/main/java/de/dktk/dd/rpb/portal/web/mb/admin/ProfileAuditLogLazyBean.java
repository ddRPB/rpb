/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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
import de.dktk.dd.rpb.core.domain.admin.AuditLog;
import de.dktk.dd.rpb.core.domain.admin.AuditLog_;
import de.dktk.dd.rpb.core.repository.admin.AuditLogRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;

import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.GenericLazyDataModel;

import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lazy loading Bean for specific user profile AuditLog presentation
 *
 * @author tomas@skripcak.net
 * @since 07 Oct 2021
 */
@Named("mbProfileAuditLogLazy")
@Scope(value="view")
public class ProfileAuditLogLazyBean extends GenericLazyDataModel<AuditLog, Integer> {

    //region Injects

    private final MainBean mainBean;

    //endregion

    //region Properties

    //region EnumAuditEvents

    public AuditEvent[] getAuditEvents() {
        return AuditEvent.values();
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public ProfileAuditLogLazyBean(MainBean mainbean, AuditLogRepository repository) {
        super(repository);

        this.mainBean = mainbean;
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
        results.add(Boolean.TRUE); // eventDate
        results.add(Boolean.TRUE); // username
        results.add(Boolean.TRUE); // event
        results.add(Boolean.TRUE); // attribute 1
        results.add(Boolean.TRUE); // attribute 2
        results.add(Boolean.FALSE); // attribute 3

        return results;
    }

    @Override
    protected AuditLog getEntity(Map<String, Object> filters) {
        AuditLog example = this.repository.getNew();
        // Audit log shown for logged user
        example.setUsername(this.mainBean.getMyAccount().getUsername());

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            switch (entry.getKey()) {
                case "eventDate": // Do not filter according date
                    break;
                case "event":
                    example.setEvent(entry.getValue().toString());
                    break;
                case "username": // Do not filter username -> it is always the active user
                    break;
                case "stringAttribute1":
                    example.setStringAttribute1(entry.getValue().toString());
                    break;
                case "stringAttribute2":
                    example.setStringAttribute2(entry.getValue().toString());
                    break;
                case "stringAttribute3":
                    example.setStringAttribute3(entry.getValue().toString());
                    break;
            }
        }

        return example;
    }

    @Override
    protected SearchParameters searchParameters() {
        return new SearchParameters()
                .anywhere()
                .equals()
                .caseInsensitive();
    }

    @Override
    protected SearchParameters defaultOrder(SearchParameters searchParameters) {
        return searchParameters.orderBy(AuditLog_.eventDate, OrderByDirection.DESC);
    }

    //endregion

}