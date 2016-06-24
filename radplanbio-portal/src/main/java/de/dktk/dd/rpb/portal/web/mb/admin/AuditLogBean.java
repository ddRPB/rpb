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

package de.dktk.dd.rpb.portal.web.mb.admin;

import de.dktk.dd.rpb.core.dao.support.OrderByDirection;
import de.dktk.dd.rpb.core.dao.support.SearchParameters;
import de.dktk.dd.rpb.core.domain.admin.AuditLog;
import de.dktk.dd.rpb.core.domain.admin.AuditLog_;
import de.dktk.dd.rpb.core.repository.admin.AuditLogRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;

import de.dktk.dd.rpb.portal.web.mb.support.GenericLazyDataModel;

import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Map;

/**
 * Bean for AuditLog presentation
 *
 * @author tomas@skripcak.net
 * @since 29 Jan 2015
 */
@Named("mbAuditLog")
@Scope(value="view")
public class AuditLogBean extends GenericLazyDataModel<AuditLog, Integer> {

    //region Properties

    //region EnumAuditEvents

    public AuditEvent[] getAuditEvents() {
        return AuditEvent.values();
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public AuditLogBean(AuditLogRepository repository) {
        super(repository);
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.columnVisibilityList = new ArrayList<Boolean>();
        this.columnVisibilityList.add(Boolean.TRUE); // eventDate
        this.columnVisibilityList.add(Boolean.TRUE); // username
        this.columnVisibilityList.add(Boolean.TRUE); // event
        this.columnVisibilityList.add(Boolean.TRUE); // attribute 1
        this.columnVisibilityList.add(Boolean.TRUE); // attribute 2
        this.columnVisibilityList.add(Boolean.FALSE); // attribute 3
    }

    //endregion

    //region Overrides

    @Override
    protected AuditLog getEntity(Map<String, Object> filters) {
        AuditLog example = this.repository.getNew();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();

            if (key.equals("event")) {
                example.setEvent(value);
            }
            else if (key.equals("username")) {
                example.setUsername(value);
            }
            else if (key.equals("stringAttribute1")) {
                example.setStringAttribute1(value);
            }
            else if (key.equals("stringAttribute2")) {
                example.setStringAttribute2(value);
            }
            else if (key.equals("stringAttribute3")) {
                example.setStringAttribute3(value);
            }
        }

        return example;
    }

    @Override
    protected SearchParameters defaultOrder(SearchParameters searchParameters) {
        return searchParameters.orderBy(AuditLog_.eventDate, OrderByDirection.DESC);
    }

    //endregion

}