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

package de.dktk.dd.rpb.core.service;

import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;

/**
 * {@inheritDoc}
 */
@Named("pacsConfigService")
public class PacsConfigService implements IPacsConfigService {
    public PacsConfigService() {
    }

    @Value("${pacs.clinicalUrl:''}")
    private String clinicalUrl;

    @Value("${pacs.auth:''}")
    private boolean auth;

    @Value("${pacs.user:''}")
    private String pacsUser;

    @Value("${pacs.password:''}")
    private String pacsPassword;

    @Value("${pacs.threadPoolSize:1}")
    private int threadPoolSize;

    public String getClinicalUrl() {
        return clinicalUrl;
    }

    public void setClinicalUrl(String clinicalUrl) {
        this.clinicalUrl = clinicalUrl;
    }

    public boolean isAuth() {
        return auth;
    }

    public String getPacsUser() {
        return pacsUser;
    }

    public String getPacsPassword() {
        return pacsPassword;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }
}
