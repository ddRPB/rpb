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

package de.dktk.dd.rpb.portal.audit;

import static org.apache.commons.lang.StringUtils.trimToNull;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import de.dktk.dd.rpb.core.context.UserContext;

/**
 * Enable/disable {@link PreUpdate} and {@link PrePersist} actions and/or force the username to be used.
 * Please note that you are in charge of resetting the context properties if you use them directly.
 */
public class AuditContextHolder {

    private static final ThreadLocal<Boolean> audit = new ThreadLocal<Boolean>();
    private static final ThreadLocal<String> username = new ThreadLocal<String>();

    public static void setAudit(boolean auditing) {
        audit.set(auditing);
    }

    public static void setUsername(String user) {
        username.set(trimToNull(user));
    }

    public static boolean audit() {
        return audit.get() == null || audit.get();
    }

    public static String username() {
        return username.get() == null ? UserContext.getUsername() : username.get();
    }

}