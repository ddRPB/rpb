/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

package de.dktk.dd.rpb.core.domain.pacs;

/**
 * ConquestMode enumeration
 * Conquest mode represents possibility for definition of web-hooks for custom lua scripts. This are limited to GET
 * requests, however they could be utilised for implementation of elementary web services for Conquest.
 *
 * RadPlanBio specific lua extensions
 * ?mode=rpb{operation}{entities/entity}
 *
 * @author tomas@skripcak.net
 * @since 18 Jan 2018
 */
public enum EnumConquestMode {

    // Ping
    HELLO("hello"),

    //TODO: could be harmonised to rpbexistfile
    FILE_EXISTS("rpbfileexists"),

    // DICOM JSON
    JSON_PATIENTS("rpbjsondicompatients"),
    JSON_STUDIES("rpbjsondicomstudies"),
    JSON_SERIES("rpbjsondicomseries"),

    // DICOM cache
    CACHE_PATIENTS("rpbcachedicompatients"),
    CACHE_STUDIES("rpbcachedicomstudies"),
    CACHE_SERIES("rpbcachedicomseries"),

    // DICOM C-MOVE
    MOVE_PATIENTS("rpbmovedicompatients"),
    MOVE_STUDIES("rpbmovedicomstudies"),
    MOVE_SERIES("rpbmovedicomseries"),
    MOVE_IMAGES("rpbmovedicomimages"),

    // DICOM update
    //TODO: script needs harmonisation

    // DICOM fix
    FIX_RTSTRUCT("rpbfixdicomrtstruct"),
    FIX_RTPLAN("rpbfixdicomrtplan"),

    // DICOM archive
    ZIP_STUDY("zipstudy"),
    ZIP_SERIES("zipseries");

    private final String value;

    EnumConquestMode(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
    
}