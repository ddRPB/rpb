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

package de.dktk.dd.rpb.core.domain.edc;

/**
 * EnumCollectSubjectDob enum
 *
 * @author tomas@skripcak.net
 * @since 15 Apr 2019
 */
public enum EnumCollectSubjectDob {

    YES("1"),
    ONLY_YEAR("2"),
    NO("3");

    private final String value;

    EnumCollectSubjectDob(final String v) {
        this.value = v;
    }

    public static EnumCollectSubjectDob fromString(String value) {
        for (EnumCollectSubjectDob dob : EnumCollectSubjectDob.values()) {
            if (dob.value.equalsIgnoreCase(value)) {
                return dob;
            }
        }
        
        return null;
    }

    @Override
    public String toString() {
        return this.value;
    }

}