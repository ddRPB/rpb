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

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;

/**
 * Re-generator for DICOM UIDs in the context of research pacs.
 * This component allows to generate DICOM UIDs for copies of studies on stage 1 and stage 2 level.
 */
public class DicomUidReGeneratorUtil {

    /**
     * Generates a DICOM UID for stage 1, based on the clinical DICOM uid
     *
     * @param dicomUidPrefix   String Prefix, assigned according to the DICOM standard to the organisation
     * @param clinicalStudyUid String clinical DICOM UID
     * @return String DICOM UID for stage 1 representation
     */
    public static String generateStageOneUid(String dicomUidPrefix, String clinicalStudyUid) {
        String result = "";
        if (clinicalStudyUid != null) {
            result = getDicomUidString(dicomUidPrefix, clinicalStudyUid);
        }
        return result;
    }

    private static String getDicomUidString(String dicomUidPrefix, String originUidAndSalt) {
        String hashHex = DigestUtils.md5Hex(originUidAndSalt);
        String hashBigintString = new BigInteger(hashHex, 16).toString();
        String extra = hashBigintString.startsWith("0") ? "9" : "";
        String combined = dicomUidPrefix + extra + hashBigintString;
        return combined.length() < 64 ? combined : combined.substring(0, 64);
    }

    /**
     * Generates a DICOM UID for stage 2, based on the DICOM uid for stage 2
     *
     * @param dicomUidPrefix  String Prefix, assigned according to the DICOM standard to the organisation
     * @param partnerSideCode String references the specific partner side, for example: DD
     * @param edcCode         String EDC Code for the Study
     * @param stageOneUid     String DICOM UID of the stage 1 representation of the data
     * @return String DICOM UID for the stage 2 representation
     */
    public static String generateStageTwoUid(String dicomUidPrefix, String partnerSideCode, String edcCode, String stageOneUid) {
        String result = "";
        if (stageOneUid != null) {
            String salt = edcCode + partnerSideCode;
            result = getDicomUidString(dicomUidPrefix, stageOneUid + salt);
        }
        return result;
    }
}