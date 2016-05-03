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

package de.dktk.dd.rpb.core.util;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * ResourcesUtil allows you to retrieve localized resources for the locale present in the current thread of execution.
 * It can be used from both Spring beans (simple dependency injection) and from non spring beans. In the later case,
 * you obtain the reference thanks to the static method ResourcesUtil.getInstance()
 */
@Named
@Singleton
@Lazy(false)
public class ResourcesUtil {

    private static ResourcesUtil instance;
    private MessageSource messageSource;

    @Inject
    public ResourcesUtil(MessageSource ms) {
        messageSource = ms;
        instance = this;
    }

    /**
     * Call it from non spring aware code to obtain a reference to the ResourcesUtil singleton instance.
     */
    public static ResourcesUtil getInstance() {
        return instance;
    }

    /**
     * Return the MessageSource that backs this ResourcesUtil.
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * Return the property value for the contextual locale.
     * If no value is found, the passed key is returned.
     */
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * Return the property value for the contextual locale.
     * If no value is found, the passed key is returned.
     */
    public String getProperty(String key, Object[] args) {
        if (key == null) {
            return "";
        }

        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * Same as getProperty() but use the count to choose the proper key.
     * Used when the message varies depending on the context. For example: 'there is no result' vs 'there is one result' vs 'there are n results'
     * @param key the base key
     */
    public String getPluralableProperty(String key, int count) {
        if (key == null) {
            return "";
        }

        if (count == 0) {
            return getProperty(key + "_0");
        }

        if (count == 1) {
            return getProperty(key + "_1");
        }

        return getProperty(key + "_n", new Object[] { count });
    }

}