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

import static com.google.common.collect.Lists.newArrayList;

import static de.dktk.dd.rpb.core.service.AuditEvent.Creation;
import static de.dktk.dd.rpb.core.service.AuditEvent.Deletion;
import static de.dktk.dd.rpb.core.service.AuditEvent.Modification;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.admin.AuditLog;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

import org.hibernate.persister.entity.EntityPersister;

/**
 * AuditLogListener
 *
 * This listener implements auditing for CRUD entity operations
 *
 * @author tomas@skripcak.net
 * @since 27 January 2015
 */
@Named
public class AuditLogListener implements PreUpdateEventListener, PostDeleteEventListener, PostInsertEventListener {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    @Inject
    private AuditLogService auditLogService;

    //endregion

    //region Members

    protected List<String> skipProperties = newArrayList("version", "lastModificationAuthor", "lastModificationDate", "creationDate", "creationAuthor");
    protected List<String> skipClasses = newArrayList("AuditLog");

    //endregion

    //region Overrides

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        audit(event, Deletion, event.getEntity());
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        audit(event, Creation, event.getEntity());
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        String updateMessage = buildUpdateMessage(event);
        if (!updateMessage.isEmpty()) {
            audit(event, Modification, event.getEntity(), updateMessage);
        }
        return false;
    }

    //endregion

    //region Private methods

    private String buildUpdateMessage(PreUpdateEvent event) {
        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
        Object[] oldStates = event.getOldState();
        Object[] newStates = event.getState();
        int index = 0;
        StringBuilder message = new StringBuilder(128);
        for (String propertyName : propertyNames) {
            message.append(message(propertyName, oldStates[index], newStates[index]));
            index++;
        }
        return message.toString();
    }

    private String message(String propertyName, Object oldState, Object newState) {
        if (newState instanceof Identifiable) {
            // no need to track objects
            return "";
        } else if (skipProperties.contains(propertyName)) {
            // no need to track version and lastModificationDates as they add no value
            return "";
        } else if (oldState == null && newState == null) {
            return "";
        } else if (oldState == null && newState != null) {
            return propertyName + " set to [" + newState + "]\n";
        } else if (oldState != null && newState == null) {
            return propertyName + " reseted to null\n";
        } else if (!oldState.toString().equals(newState.toString())) {
            return propertyName + " changed from [" + oldState.toString() + "] to [" + newState.toString() + "]\n";
        } else {
            return "";
        }
    }

    private void audit(AbstractEvent hibernateEvent, AuditEvent auditEvent, Object object) {
        audit(hibernateEvent, auditEvent, object, null);
    }

    private void audit(AbstractEvent hibernateEvent, AuditEvent auditEvent, Object object, String attribute) {
        String className = object.getClass().getSimpleName();
        if (skipClasses.contains(className)) {
            return;
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUsername(UserContext.getUsername());
        auditLog.setEvent(auditEvent.name());
        auditLog.setEventDate(new Date());
        auditLog.setStringAttribute1(className);
        auditLog.setStringAttribute2(((Identifiable<?>) object).getId().toString());
        auditLog.setStringAttribute3(attribute);
        audit(hibernateEvent, auditLog);
    }

    private void audit(AbstractEvent hibernateEvent, final AuditLog auditLog) {
        hibernateEvent.getSession().getActionQueue().registerProcess(new AfterTransactionCompletionProcess() {
            @Override
            public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
                if (success) {
                    auditLogService.log(auditLog);
                }
            }
        });
    }

    //endregion

}