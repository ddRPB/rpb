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

import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.dktk.dd.rpb.core.util.Constants.study0EdcCode;

public class DicomStudyDescriptionEdcCodeUtil {

    /**
     * Returns a prefix string that includes the EDC-Code according to the pattern used in the studyDescription
     * property to differentiate between the different possible stages of a study copy on the research  pacs
     *
     * @param edcCode EDC-Code of the study
     * @return String prefix with EDC-Code
     */
    public static String getEdcCodePattern(String edcCode) {
        return "(" + edcCode + ")-";
    }

    /**
     * Removes the first prefix that includes the EDC-Code
     *
     * @param descriptionWithPrefix String with EDC-Code prefix from study description
     * @return String without the EDC-Code prefix
     * @throws NullPointerException throws if parameter is null
     */
    public static String removeEdcCodePrefix(String descriptionWithPrefix) throws NullPointerException {

        String result = "";

        if (descriptionWithPrefix != null) {
            int pos = 0;
            Pattern p = Pattern.compile("^\\([^)-]+\\)-");
            Matcher m = p.matcher(descriptionWithPrefix);

            if (m.find()) {
                pos = m.end(0);
            }

            result = descriptionWithPrefix.substring(pos);
        }

        return result;
    }

    /**
     * Removes the first prefix that includes the EDC-Code
     *
     * @param idWithPrefix String with EDC-Code prefix from study description
     * @return EDC-Code prefix
     * @throws NullPointerException throws if parameter is null
     */
    public static String getSiteCodePrefix(String idWithPrefix) throws NullPointerException {

        String result = "";

        if (idWithPrefix != null) {
            int pos = 0;
            Pattern p = Pattern.compile("^[^)-]+-");
            Matcher m = p.matcher(idWithPrefix);

            if (m.find()) {
                pos = m.end(0);
            }

            result = idWithPrefix.substring(0, pos - 1);
        }

        return result;
    }

    /**
     * Compares two Study object if they correspond to each other.
     * In that case we assume they are simply in different stages.
     * One is in Study 0 state and the other is staged, based on the study 0 copy.
     *
     * @param stagedStudy DicomStudy candidate to be a staged version of the study 0 copy
     * @param studyZero   DicomStudy study 0 copy
     * @return boolean true if the studies correspond from the description and date
     * @throws NullPointerException throws if properties are missing or parameter is null
     */
    public static boolean isCorrespondingStudyZero(DicomStudy stagedStudy, DicomStudy studyZero) {
        boolean isCorresponding = false;

        String stagedStudyDescriptionWithoutPrefix = removeEdcCodePrefix(stagedStudy.getStudyDescription());
        String studyZeroDescriptionWithoutPrefix = removeEdcCodePrefix(studyZero.getStudyDescription());

        if (stagedStudyDescriptionWithoutPrefix.equals(studyZeroDescriptionWithoutPrefix) && !stagedStudyDescriptionWithoutPrefix.isEmpty()) {
            if (stagedStudy.getStudyDate().equals(studyZero.getStudyDate())) {
                isCorresponding = true;
            }
        }

        return isCorresponding;
    }

    /**
     * Searches for the specific study zero prefix within a String
     *
     * @param descriptionWithPrefix String that needs to be checked
     * @return boolean true if prefix exists
     */
    public static boolean hasStudyZeroPrefix(String descriptionWithPrefix) {
        Pattern p = Pattern.compile("^\\(" + study0EdcCode + "\\)-");
        Matcher m = p.matcher(descriptionWithPrefix);
        return m.find();
    }

}
