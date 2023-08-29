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

package de.dktk.dd.rpb.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for PatientId handling
 */
public class PatientIdentifierUtil {
    /**
     * Removes prefix based on convention that a prefix consists of capitalized letters followed by a hyphen
     *
     * @param patientIdWithPrefix String patient identifier with prefix
     * @return String patient identifier without prefix
     */
    public static String removePatientIdPrefix(String patientIdWithPrefix) {
        int pos = 0;
        String pattern = "^[A-Z]+" + Constants.RPB_IDENTIFIERSEP;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(patientIdWithPrefix);

        if (m.find()) {
            pos = m.end(0);
        }

        return patientIdWithPrefix.substring(pos);
    }

    /**
     * Returns the prefix of an identifier
     *
     * @param patientIdWithPrefix String patient identifier with prefix
     * @return String prefix of the identifier
     */
    public static String getPatientIdPrefix(String patientIdWithPrefix) {
        int pos = 0;
        String pattern = "^[A-Z]+" + Constants.RPB_IDENTIFIERSEP;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(patientIdWithPrefix);

        if (m.find()) {
            pos = m.end(0);
        }

        // remove separator from result
        if (pos > 0) {
            pos = pos - 1;
        }

        return patientIdWithPrefix.substring(0, pos);
    }

    /**
     * Patient identifier of the hospital system have a specific format.
     * This method checks if the string fulfills the criteria.
     *
     * @param id String to be validated
     * @return boolean valid or invalid
     */
    public static boolean validateHospitalPatientId(String id) {
        if (id == null) {
            return false;
        }
        return id.matches("^[0-9]+");
    }

}
