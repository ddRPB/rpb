/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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

package de.dktk.dd.rpb.core.service;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.admin.AuditLog;

//TODO: It looks like this Singleton exist twice - it may be that there are two spring IoC contexts
/**
 * AuditLogService
 *
 * It is implemented as singleton and this also means that there is just one instance of this service which is used in portal
 * as well as wepApi (new webApi is part of portal)
 *
 * @author tomas@skripcak.net
 * @since 27 January 2015
 */
@Named
@Singleton
@Lazy(false)
public class AuditLogService {

    //region Finals

    private static final Logger log = Logger.getLogger(AuditLogService.class);
    private static final int DEFAULT_BATCH_INSERT_SIZE = 50;

    //endregion

    //region Injects

    @Inject
    private SessionFactory sessionFactory;

    //endregion

    //region Members

    protected BlockingQueue<AuditLog> queue = new LinkedBlockingQueue<>(1000);
    protected int batchInsertSize = DEFAULT_BATCH_INSERT_SIZE;
    protected String username;

    //endregion

    //region Properties

    public void setUsername(String username) {
        this.username = username;
    }

    //endregion

    //region Methods

    /**
     * Batch insert of collected audit logs (in ms)
     */
    @Scheduled(fixedDelay = 5000)
    public void batchInsert() {
        List<AuditLog> httpEvents = newArrayList();
        int size = this.queue.drainTo(httpEvents, batchInsertSize);
        if (size != 0) {
            this.batchInsert(httpEvents);
        }
    }

    /**
     * Create new audit log based on event data
     * @param auditEvent audit event
     */
    public void event(AuditEvent auditEvent) {
        this.event(auditEvent, null);
    }

    /**
     * Create new audit log based on event data
     * @param auditEvent audit event
     * @param string1 string1
     */
    public void event(AuditEvent auditEvent, String string1) {
        this.event(auditEvent, string1, null);
    }

    /**
     * Create new audit log based on event data
     * @param auditEvent audit event
     * @param string1 string1
     * @param string2 string2
     */
    public void event(AuditEvent auditEvent, String string1, String string2) {
        this.event(auditEvent, string1, string2, null);
    }

    /**
     * Create new audit log based on event data
     * @param auditEvent audit event
     * @param string1 string1
     * @param string2 string2
     * @param string3 string3
     */
    public void event(AuditEvent auditEvent, String string1, String string2, String string3) {
        AuditLog auditLog = new AuditLog();
        // For this events I put the username into string1 because UserContext is not created yet
        if (auditEvent.equals(AuditEvent.LoginSuccessful) || auditEvent.equals(AuditEvent.LogoutSuccessful)) {
            auditLog.setUsername(string1);
            this.username = string1;
            string1 = null;
        }
        // For those events force to use anonymous user
        else if (auditEvent.equals(AuditEvent.LoginFailed) || auditEvent.equals(AuditEvent.ApplicationStartup) || auditEvent.equals(AuditEvent.ApplicationShutdown)) {
            this.username = UserContext.ANONYMOUS_USER;
        }
        else {
            auditLog.setUsername(UserContext.getUsername());
        }
        auditLog.setEvent(auditEvent.name());
        auditLog.setStringAttribute1(string1);
        auditLog.setStringAttribute2(string2);
        auditLog.setStringAttribute3(string3);
        this.log(auditLog);
    }

    /**
     * Log the auditLog and add it into persist queue
     * @param auditLog auditLog
     */
    public void log(AuditLog auditLog) {
        this.setupDefaults(auditLog);
        this.queue.add(auditLog);
    }

    //endregion

    //region Private methods

    /**
     * Setup defaults for each audit log
     * @param auditLog auditLog
     */
    private void setupDefaults(AuditLog auditLog) {
        // Username was not set within event method
        if (auditLog.getUsername() == null) {
            auditLog.setUsername(UserContext.getUsername());
        }
        // There is not user in the UserContext (e.g. when SpringSecurity context does not exist, webApi is using audit log service)
        // And username was provided in property (within webApi method)
        if (UserContext.isAnonymous() && this.username != null && !this.username.equals("")) {
            auditLog.setUsername(this.username);
        }
        // Date was not set within event method
        if (auditLog.getEventDate() == null) {
            auditLog.setEventDate(new Date());
        }
    }

    /**
     * Persist audit logs in a batch
     * @param auditLogs auditLogs
     */
    private void batchInsert(List<AuditLog> auditLogs) {
        Session session = null;
        try {
            session = this.sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            for (AuditLog auditLog : auditLogs) {
                session.save(auditLog);
            }
            session.flush();
            transaction.commit();
            log.debug("Added " + auditLogs.size() + " AuditLog in database");
        }
        catch (Exception e) {
            log.error("Error while inserting AuditLog", e);
        }
        finally {
            if (session != null) {
                session.close();
            }
        }
    }

    //endregion

}