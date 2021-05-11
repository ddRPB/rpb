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

package de.dktk.dd.rpb.core.context;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

/**
 * Get Spring security context to access user data security information
 */
public class UserContext {

    // Declare string representing anonymous user
    public static final String ANONYMOUS_USER = "anonymousUser";

    /**
     * Get the current username. Note that it may not correspond to a username that
     * currently exists in your accounts' repository; it could be a spring security
     * 'anonymous user'.
     *
     * @see org.springframework.security.web.authentication.AnonymousAuthenticationFilter
     * @return the current user's username, or 'anonymousUser'.
     */
    public static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            Object principal = auth.getPrincipal();

            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }

            return principal.toString();
        }

        return ANONYMOUS_USER;
    }

    /**
     * Get the current user password. Note to work the authentication manager
     * has to set property erase-credentials="false" in spring application context xml file
     *
     * @return the current user's password or empty string
     */
    public static String getPassword() {
        String result = "";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof LdapUserDetails) {
                result = DigestUtils.shaHex(auth.getCredentials().toString());
            }
            else if (principal instanceof UserDetails) {
                result = ((UserDetails) principal).getPassword();
            }
        }

        return result;
    }

    /**
     * Get the current user clear password. Note to work the authentication manager
     * has to set property erase-credentials="false" in spring application context xml file
     *
     * @return the current user's clear password or empty string
     */
    public static String getClearPassword() {
        String result = "";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof LdapUserDetails) {
                result = auth.getCredentials().toString();
            }
            else if (principal instanceof UserDetails) {
                result = ((UserWithId) principal).getClearPassword();
            }
        }

        return result;
    }

    /**
     * Get the current user email/ LDAP username. Not to work the authentication provider has to set this up.
     * (necessary for some external systems as login name)
     *
     * @return the current user's email address or LDAP username for LDAP users or emtpy string
     */
    public static String getEmail() {
        String result = "";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            Object principal = auth.getPrincipal();
            // For LDAP users return the LDAP username
            if (principal instanceof LdapUserDetails) {
                result= ((LdapUserDetailsImpl) principal).getUsername();
            }
            // Otherwise for RPB local accounts return the email
            else if (principal instanceof UserDetails) {
                result = ((UserWithId) principal).getEmail();
            }
        }

        return result;
    }

    /**
     *
     */
    public static Serializable getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            Object principal = auth.getPrincipal();

            if (principal instanceof UserWithId) {
                return ((UserWithId) principal).getId();
            }
        }

        return null;
    }

    /**
     * Return the current locale from spring LocaleContextHolder
     */
    public static Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * Retrieve the current UserDetails bound to the current thread by Spring Security, if any.
     */
    public static UserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal());
        }

        return null;
    }

    /**
     * Return the current roles bound to the current thread by Spring Security.
     */
    public static List<String> getRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            return toStringList(auth.getAuthorities());
        }

        return Collections.emptyList();
    }

    /**
     * Determine whether user as anonymous user name
     * @return true if anonymous user otherwise false
     */
    public static boolean isAnonymous() {
        return getUsername().equals(ANONYMOUS_USER);
    }

    /**
     * Determine whether the passed role is assigned
     *
     * @return true if the passed role is present, false otherwise.
     */
    public static boolean hasRole(String roleName) {
        for (String role : getRoles()) {
            if (role.equalsIgnoreCase(roleName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return the string list of provided authorities
     * @param grantedAuthorities granted authorities
     * @return authorities as list of string
     */
    public static List<String> toStringList(Iterable<? extends GrantedAuthority> grantedAuthorities) {
        List<String> result = newArrayList();

        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            result.add(grantedAuthority.getAuthority());
        }

        return result;
    }

}