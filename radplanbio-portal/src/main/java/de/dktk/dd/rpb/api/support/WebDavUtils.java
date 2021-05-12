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

package de.dktk.dd.rpb.api.support;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.util.Constants;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.*;

public class WebDavUtils {

    //region Finals

    private static final Logger log = Logger.getLogger(WebDavUtils.class);

    //endregion

    //region Static Methods

    public static boolean isWindowsFileSystemIdentifier(String... identifiers) {

        boolean result = false;

        String[] reservedIdentifier = new String[] { "desktop.ini", "desktop.jpg", "desktop.gif", "folder.jpg", "folder.gif" };

        if (identifiers != null) {
            for (String identifier: identifiers) {
                if (Arrays.asList(reservedIdentifier).contains(identifier)) {
                    log.info("Windows specific filesystem identifier is not defined in RPB: " + identifier);
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    public static String buildStudySubjectIdentifier(de.dktk.dd.rpb.core.domain.edc.StudySubject studySubject) {
        return studySubject.getStudySubjectId() + "-(" + studySubject.getPid() + ")";
    }

    public static String buildEventIdentifier(EventData eventData) {
        // Clear the event name identifier from white spaces
        String studyEventNameIdentifier = eventData.getEventName().replaceAll(" " , Constants.RPB_IDENTIFIERSEP).trim();
        // No suffix for non-repeating events
        String suffix = "";
        // Check whether the event is repeating
        if (eventData.getEventDefinition().getIsRepeating() &&
            eventData.getStudyEventRepeatKey() != null &&
            !eventData.getStudyEventRepeatKey().isEmpty()) {
            
            suffix = Constants.RPB_IDENTIFIERSEP + eventData.getStudyEventRepeatKey();
        }

        return studyEventNameIdentifier + suffix;
    }

    public static String trimStudySubjectIdentifier(String studySubjectNameIdentifier) {
        return studySubjectNameIdentifier.substring(0, studySubjectNameIdentifier.lastIndexOf("(") - 1);
    }

    public static String trimDicomPatientPseudonymIdentifier(String dicomPatientNameIdentifier) {
        return dicomPatientNameIdentifier.substring(dicomPatientNameIdentifier.lastIndexOf("(") + 1, dicomPatientNameIdentifier.lastIndexOf(")"));
    }

    public static String trimDicomStudyNameIdentifier(String dicomStudyNameIdentifier) {
        String result = "";

        List<String> studyTypeToTest = new ArrayList<>();
        studyTypeToTest.add(Constants.RPB_CONTOURING);
        studyTypeToTest.add(Constants.RPB_TREATMENTPLAN);
        studyTypeToTest.add(Constants.RPB_PETCT);
        studyTypeToTest.add(Constants.RPB_PETMRI);
        studyTypeToTest.add(Constants.RPB_PET);
        studyTypeToTest.add(Constants.RPB_MRI);
        studyTypeToTest.add(Constants.RPB_CT);
        studyTypeToTest.add(Constants.RPB_US);
        studyTypeToTest.add(Constants.RPB_SPECT);
        studyTypeToTest.add(Constants.RPB_OTH);

        for (String test : studyTypeToTest) {
            if (dicomStudyNameIdentifier.startsWith(test + Constants.RPB_IDENTIFIERSEP)) {
                result = dicomStudyNameIdentifier.substring(dicomStudyNameIdentifier.indexOf(test + Constants.RPB_IDENTIFIERSEP) + (test + Constants.RPB_IDENTIFIERSEP).length());
                break;
            }
        }

        return result;
    }

    public static String trimDicomSeriesNameIdentifier(String dicomSeriesNameIdentifier) {
        return dicomSeriesNameIdentifier.substring(dicomSeriesNameIdentifier.indexOf(Constants.RPB_IDENTIFIERSEP) + 1);
    }

    public static String trimDicomInstanceNameIdentifier(String dicomInstanceNameIdentifier) {
        // remove .dcm extensions
        return dicomInstanceNameIdentifier.substring(0, dicomInstanceNameIdentifier.length() - 3);
    }

    public static String encodeDicomUid(String uid) {
        // UID separator
        String uidSep = ".";

        // How many dots are within UID
        String dotCount =  String.format("%02d", StringUtils.countMatches(uid, uidSep));

        // Dot positions
        StringBuilder dotPositions = new StringBuilder();
        int i = uid.indexOf(uidSep);
        while (i >= 0) {
            dotPositions.append(String.format("%02d", i));
            i = uid.indexOf(uidSep, i + 1);
        }
        
        // Remove dots from identifiers
        String trimmedUid = uid.replace(uidSep,"");

        // Convert into big integer to encode identifier
        String combined =  "1" + dotCount + dotPositions + trimmedUid;
        BigInteger convertToNumber = new BigInteger(combined);

        // Base64
        String encoded = new String(Base64.encodeBase64(convertToNumber.toByteArray()));
        // Base63
        encoded = encoded.replace("/", "_");
        // Base62
        return encoded.replace("+", "-");
    }
    
    public static String decodeDicomUid(String encodedUid) {
        // Base63
        encodedUid = encodedUid.replace("-", "+");
        // Base64
        encodedUid = encodedUid.replace("_", "/");
        BigInteger decodedNumber = new BigInteger(Base64.decodeBase64(encodedUid));
        String decodedString = decodedNumber.toString();

        String withoutPrefix = decodedString.substring(1);
        // How many dots are within UID
        int dotCount = Integer.parseInt(withoutPrefix.substring(0, 2));

        String withoutDotCount = withoutPrefix.substring(2);
        String withoutDotPositions = withoutDotCount.substring(dotCount * 2);

        // Extract positions of dots and build UID
        StringBuilder uid = new StringBuilder(withoutDotPositions);
        for (int i = 0; i < dotCount; i++) {
            int dotPosition = Integer.parseInt(withoutDotCount.substring(i * 2, (i * 2) + 2));
            uid.insert(dotPosition, ".");
        }

        return uid.toString();
    }

    public static Date combineDateTime(Date dateStudy, Date seriesTime) {
        if (seriesTime == null) {
            seriesTime=dateStudy;
        }

        Calendar result = Calendar.getInstance();
        result.setTime(dateStudy);

        Calendar calendarSeries = Calendar.getInstance();
        calendarSeries.setTime(seriesTime);

        result.set(Calendar.HOUR_OF_DAY, calendarSeries.get(Calendar.HOUR_OF_DAY));
        result.set(Calendar.MINUTE, calendarSeries.get(Calendar.MINUTE));
        result.set(Calendar.SECOND, calendarSeries.get(Calendar.SECOND));

        return result.getTime();
    }

    //endregion

}
