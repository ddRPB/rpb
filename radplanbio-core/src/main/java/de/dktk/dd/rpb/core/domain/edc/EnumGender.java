/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.dktk.dd.rpb.core.domain.edc;

import de.dktk.dd.rpb.core.exception.StringNotConvertibleToEnumException;

public enum EnumGender {

    Male("m"),
    Female("f");

    private final String value;

    EnumGender(String value) {
        this.value = value;
    }

    public static EnumGender fromString(String value) throws StringNotConvertibleToEnumException {
        for (EnumGender enumGender : EnumGender.values()) {
            if (enumGender.value.equalsIgnoreCase(value)) {
                return enumGender;
            }
        }

        String options = "";
        for (EnumGender enumGender : EnumGender.values()) {
            options += ", " + enumGender.value;
        }
        // remove first comma and space occurrence
        options = options.substring(2);
        throw new StringNotConvertibleToEnumException("Could not convert " + value + " to EnumGender. Please use the options: " + options);
    }

    @Override
    public String toString() {
        return this.value;
    }

}
