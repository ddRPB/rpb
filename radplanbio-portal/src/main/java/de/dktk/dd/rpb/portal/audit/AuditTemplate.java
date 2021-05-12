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

import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;

/**
 * Execute an {@link AuditCallback} to enable/disable audit in {@link PreUpdate} and {@link PostUpdate} actions and/or force username to be used.
 * The {@link AuditTemplate} will be in charge of cleaning up the {@link AuditContextHolder} state.
 */
public class AuditTemplate {

    private final boolean auditing;
    private final String username;

    public interface AuditCallback<T> {
        T doInAudit() throws Exception;
    }

    /**
     * Enable or not audit
     */
    public AuditTemplate(boolean auditing) {
        this(auditing, null);
    }

    /**
     * Enable audit, and specify a username to be used
     */
    public AuditTemplate(String username) {
        this(true, username);
    }

    private AuditTemplate(boolean auditing, String username) {
        this.auditing = auditing;
        this.username = username;
    }

    public <T> T execute(AuditCallback<T> callback) throws Exception {
        boolean previousState = AuditContextHolder.audit();
        String previousUsername = AuditContextHolder.username();
        AuditContextHolder.setAudit(auditing);
        AuditContextHolder.setUsername(username);
        try {
            return callback.doInAudit();
        }
        finally {
            AuditContextHolder.setAudit(previousState);
            AuditContextHolder.setUsername(previousUsername);
        }
    }

}