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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;

/**
 * Integration Engine service user service
 *
 * @author tomas@skripcak.net
 * @since 21 Mar 2018
 */
@Named
public class EngineService {

    //region Finals

    private static final Logger log = Logger.getLogger(EngineService.class);

    //endregion

    //region Members

    @Value("${iengine.username:}")
    private String username;
    @Value("${iengine.password:}")
    private String password;

    //endregion

    //region Constructors

    public EngineService() {
        // NOOP
    }

    //endregion

    //region Properties

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return password;
    }

    //endregion
}
