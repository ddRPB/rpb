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

package de.dktk.dd.rpb.core.context;

import org.apache.log4j.MDC;

/**
 * Used to store to use global info for logging purposes. This method prevents passing all the data you want to see in your logs in all the methods.
 */
public class LogContext {

    /**
     * parameter name that holds logins
     */
    public static final String LOGIN = "login";

    /**
     * parameter name that holds web session ids
     */
    public static final String SESSION_ID = "session_id";

    /**
     * set the given login in the map
     */
    public static void setLogin(String login) {
        put(LOGIN, login);
    }

    /**
     * set the given web session in the map
     */
    public static void setSessionId(String sessionId) {
        put(SESSION_ID, sessionId);
    }

    /**
     * Get the context identified by the key parameter.
     */
    public static Object get(String key) {
        return MDC.get(key);
    }

    /**
     * Put a context value (the o parameter) as identified with the key parameter into the current thread's context map.
     */
    public static void put(String key, Object o) {
        MDC.put(key, o);
    }

    /**
     * Remove the the context identified by the key parameter.
     */
    public static void remove(String key) {
        MDC.remove(key);
    }

    /**
     * Remove all the object put in this thread context.
     */
    @SuppressWarnings("unused")
    public static void resetLogContext() {
        if (MDC.getContext() != null) {
            MDC.getContext().clear();
        }
    }

}
