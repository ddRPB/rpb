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

import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;

import static de.dktk.dd.rpb.core.service.EnvironmentService.Environment.*;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

@Named
public class EnvironmentService {

    public enum Environment {
        Development, Integration, Production;

        boolean is(String value) {
            return name().equalsIgnoreCase(trimToEmpty(value));
        }

        public static Environment toEnvironment(String value) {
            for (Environment environment : values()) {
                if (environment.is(value)) {
                    return environment;
                }
            }
            return Development;
        }
    }

    //region Members

    // Value annotation is used to specify the default value
    @Value("${build.environment}")
    private String environmentName; // environmentName hold the name of env (e.g. development, production, etc.)

    //endregion

    //region Properties

    /***
     * Get the name of environment where the app is running
     * @return the name of environment where tha app is running
     */
    public Environment getEnvironment() {
        return toEnvironment(this.environmentName);
    }

    //endregion

    //region Helpers

    @SuppressWarnings("unused")
    public boolean isDevelopment() {
        return Development.is(environmentName);
    }

    @SuppressWarnings("unused")
    public boolean isIntegration() {
        return Integration.is(environmentName);
    }

    @SuppressWarnings("unused")
    public boolean isProduction() {
        return Production.is(environmentName);
    }

    //endregion

}
