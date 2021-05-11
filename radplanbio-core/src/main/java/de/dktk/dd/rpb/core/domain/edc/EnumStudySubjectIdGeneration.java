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
 * StudySubjectIdGeneration enum
 *
 * @author tomas@skripcak.net
 * @since 21 Nov 2017
 */
public enum EnumStudySubjectIdGeneration {

    MANUAL("manual"),
    AUTO_EDITABLE("auto_editable"),
    AUTO_NONEDITABLE("auto non-editable"),
    AUTO("auto");

    private final String value;

    EnumStudySubjectIdGeneration(final String v) {
        this.value = v;
    }

    public static EnumStudySubjectIdGeneration fromString(String value) {
        for (EnumStudySubjectIdGeneration idGeneration : EnumStudySubjectIdGeneration.values()) {
            if (idGeneration.value.equalsIgnoreCase(value)) {
                return idGeneration;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.value;
    }

}