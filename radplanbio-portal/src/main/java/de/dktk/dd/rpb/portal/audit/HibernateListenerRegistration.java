/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

package de.dktk.dd.rpb.portal.audit;

import static de.dktk.dd.rpb.core.service.AuditEvent.ApplicationShutdown;
import static de.dktk.dd.rpb.core.service.AuditEvent.ApplicationStartup;

import de.dktk.dd.rpb.core.service.AuditLogService;

import static org.hibernate.event.spi.EventType.POST_DELETE;
import static org.hibernate.event.spi.EventType.POST_INSERT;
import static org.hibernate.event.spi.EventType.PRE_UPDATE;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;

/**
 * HibernateListenerRegistration
 *
 * Here the hibernate entity listener is registered
 * it also triggers app startup and shutdown audit log event
 *
 * @author tomas@skripcak.net
 * @since 27 January 2015
 */
@Named
@Lazy(false)
public class HibernateListenerRegistration {

    //region Finals

    private static final Logger log = Logger.getLogger(HibernateListenerRegistration.class);

    //endregion

    //region Injects

    @Inject
    private AuditLogService auditLogService;
    @Inject
    private SessionFactory sessionFactory;
    @Inject
    private AuditLogListener auditLogListener;

    //endregion

    @PostConstruct
    public void registerListeners() {
        register(POST_DELETE, auditLogListener);
        register(POST_INSERT, auditLogListener);
        register(PRE_UPDATE, auditLogListener);
        //TODO: maybe it is possible to load the string from pom or from msg resource
        this.auditLogService.event(ApplicationStartup, "radplanbio-portal");
    }

    private <T> void register(EventType<T> eventType, T t) {
        EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        log.info("Registering " + t.getClass() + " listener on " + eventType + " events.");
        registry.getEventListenerGroup(eventType).appendListener(t);
    }

    @PreDestroy
    public void destroy() {
        //TODO: maybe it is possible to load the string from pom or from msg resource
        this.auditLogService.event(ApplicationShutdown, "radplanbio-portal");
    }

}