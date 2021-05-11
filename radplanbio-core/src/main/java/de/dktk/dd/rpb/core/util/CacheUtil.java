/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.context.annotation.Lazy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * CacheUtil allows you to retrieve cached objects
 * It can be used from both Spring beans (simple dependency injection) and from non spring beans. In the later case,
 * you obtain the reference thanks to the static method CacheUtil.getInstance()
 */
@Named
@Singleton
@Lazy(false)
public class CacheUtil {

    private static CacheUtil instance;

    private CacheManager cacheManager;

    private Cache subjectsCache;
    private Cache metadataCache;

    @Inject
    public CacheUtil(CacheManager cacheManager) {
        this.cacheManager = cacheManager;

        this.subjectsCache = this.cacheManager.getCache("wsClientStudySubjectsCache");
        this.metadataCache = this.cacheManager.getCache("wsClientStudyMetadataCache");

        instance = this;
    }

    /**
     * Call it from non spring aware code to obtain a reference to the CacheUtil singleton instance.
     */
    public static CacheUtil getInstance() {
        return instance;
    }

    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    public Cache getSubjectsCache() {
        return this.subjectsCache;
    }

    public Element getSubjectsCacheElement(String key) {
        if (this.subjectsCache != null && key != null && !key.isEmpty()) {
            return this.subjectsCache.get(key);
        }

        return null;
    }

    public void setSubjectsCacheElement(Element element) {
        if (this.subjectsCache != null && element != null) {
            this.subjectsCache.put(element);
        }
    }

    public Cache getMetadataCache() {
        return this.metadataCache;
    }

    public Element getMetadataCacheElement(String key) {
        if (this.metadataCache != null && key != null && !key.isEmpty()) {
            return this.metadataCache.get(key);
        }

        return null;
    }

    public void setMetadataCacheElement(Element element) {
        if (this.metadataCache != null && element != null) {
            this.metadataCache.put(element);
        }
    }
}
