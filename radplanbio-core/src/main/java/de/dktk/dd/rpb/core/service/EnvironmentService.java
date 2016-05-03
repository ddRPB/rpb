/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2014  Tomas Skripcak
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

package de.dkfz.core.service;

import static org.apache.commons.lang.StringUtils.trimToEmpty;
import static de.dkfz.core.service.EnvironmentService.Environment.Development;
import static de.dkfz.core.service.EnvironmentService.Environment.Integration;
import static de.dkfz.core.service.EnvironmentService.Environment.Production;
import static de.dkfz.core.service.EnvironmentService.Environment.toEnvironment;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;

@Named
public class EnvironmentService {

    // environmentName hold the name of env (e.g. development, production, etc.)
    // Value annotation is used to specify the default value
    @Value("${env_name:developement}")
    private String environmentName;

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

    public boolean isDevelopment() {
        return Development.is(environmentName);
    }

    public boolean isIntegration() {
        return Integration.is(environmentName);
    }

    public boolean isProduction() {
        return Production.is(environmentName);
    }

    /***
     * Get the name of environment where the app is running
     * @return the name of environment where tha app is running
     */
    public Environment getEnvironmentName() {
        return toEnvironment(this.environmentName);
    }
}
