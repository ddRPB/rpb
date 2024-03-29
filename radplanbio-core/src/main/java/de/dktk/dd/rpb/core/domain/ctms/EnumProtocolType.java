/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2021 RPB Team
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

package de.dktk.dd.rpb.core.domain.ctms;

/**
 * Enumeration of all supported study protocol types classes inherited from AbstractProtocolType
 *
 * @author tomas@skripcak.net
 * @since 07 Jun 2015
 */
public enum EnumProtocolType {

    OBSERVATIONAL("Observational"),
    INTERVENTIONAL("Interventional"),
    REGISTRY("Registry"),
    PILOT("Pilot"),
    METAANALYSIS("Meta-Analysis"),
    INSILICO("In-Silico");

    private String label;

    EnumProtocolType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}