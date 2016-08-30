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

package de.dktk.dd.rpb.participate.web.util;

import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;

import de.dktk.dd.rpb.core.context.UserContext;

/**
 * Simple pass over to access static 'userContext' from EL.
 * Keeps also the user's preferred locale.
 *
 * @see org.springframework.security.web.authentication.AnonymousAuthenticationFilter
 */
@Named("userContext")
@Scope("session")
public class UserContextUtil {

    //region Members

    private Locale locale;

    //endregion

    //region Methods

    /**
     * It is invoked from the f:view tag. 
     * It defaults to the default JSF local.
     * 
     * @return the current user's locale.
     */
    public String getLocale() {
        if (locale == null) {
            locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        }
        LocaleContextHolder.setLocale(locale);
        return locale.getLanguage();
    }

    public String getEnglishLanguage() {
        return Locale.ENGLISH.getLanguage();
    }

    public String getGermanLanguage() {
        return Locale.GERMAN.getLanguage();
    }

    public String getFrenchLanguage() {
        return Locale.FRENCH.getLanguage();
    }

    public String switchToEnglish() {
        return switchToLocale(Locale.ENGLISH);
    }

    public String switchToGerman() {
        return switchToLocale(Locale.GERMAN);
    }

    public String switchToFrech() {
        return switchToLocale(Locale.FRANCE);
    }

    private String switchToLocale(Locale locale) {
        this.locale = locale;
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        LocaleContextHolder.setLocale(locale);

        return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true";
    }

    public String getUsername() {
        return UserContext.getUsername();
    }

    public String getPassword() {
        return UserContext.getPassword();
    }

    public String getClearPassword() {
        return UserContext.getClearPassword();
    }

    public boolean isAnonymousUser() {
        return UserContext.ANONYMOUS_USER.equalsIgnoreCase(getUsername());
    }

    public boolean isLoggedIn() {
        return !isAnonymousUser();
    }

    public boolean isEnglish() {
        if (locale != null) {
            return "en".equals(locale.getLanguage());
        }
        else {
            // just in case, but should not happen
            return "en".equals(LocaleContextHolder.getLocale().getLanguage());
        }
    }

    public boolean isGerman() {
        if (locale != null) {
            return "de".equals(locale.getLanguage());
        }
        else {
            // just in case, but should not happen
            return "de".equals(LocaleContextHolder.getLocale().getLanguage());
        }
    }

    public boolean isFrench() {
        if (locale != null) {
            return "fr".equals(locale.getLanguage());
        }
        else {
            // just in case, but should not happen
            return "fr".equals(LocaleContextHolder.getLocale().getLanguage());
        }
    }

    public List<String> getRoles() {
        return UserContext.getRoles();
    }

    public boolean hasRole(String role) {
        return UserContext.getRoles().contains(role);
    }

    //endregion

}