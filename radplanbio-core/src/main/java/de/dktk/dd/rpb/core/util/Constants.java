/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

/**
 * RPB constants
 */
public class Constants {

    // Study identifier for study that aggregates data from all patieents
    public static final String study0Identifier = "study-0";
    // StudySubject prefix for study that aggregates data from all patients
    public static final String HISprefix = "HIS-";

    //region EDC

    //region OpenClinica

    // EDC specific root user
    public static final String OC_ROOT = "root";

    // EDC specific data types
    public static final String OC_INTEGER = "integer";
    public static final String OC_DECIMAL = "float";
    public static final String OC_STRING = "text";
    public static final String OC_DATE = "date";
    public static final String OC_PDATE = "partialDate";

    public static final String OC_YES = "Yes";
    public static final String OC_NO = "No";
    public static final String OC_NA = "N/A";

    // EDC specific date format string
    public static final String OC_DATEFORMAT = "yyyy-MM-dd";
    public static final String OC_TIMEFORMAT = "HH:mm";

    // EDC specific multi value separator
    public static final String OC_MULTIVALSEP = ",";

    // EDC specific password hash for LDAP users
    public static final String OC_LDAPPASSHASH = "*";

    // EDC UNGROUPED ItemGroupDefinition OID
    public static final String OC_IG_UNGROUPED = "UNGROUPED";

    // EDC web services
    public static final String OC_SUCCESS = "Success";
    public static final String OC_LOGINMETHOD = "j_spring_security_check";
    public static final String OC_UNAMEPARAM = "j_username";
    public static final String OC_PASSWDPARAM = "j_password";

    //endregion

    //endregion

    // Constants for mapping to EDC StudySubject fields and PID IDAT fileds
    public static final String SS_PERSONID = "SS_PERSONID";
    public static final String SS_STUDYSUBJECTID = "SS_STUDYSUBJECTID";
    public static final String SS_SECONDARYID = "SS_SECONDARYID";
    public static final String SS_GENDER = "SS_GENDER";
    public static final String SS_DATEOFBIRTH = "SS_DATEOFBIRTH";
    public static final String SS_YEAROFBIRTH = "SS_YEAROFBIRTH";
    public static final String SS_FIRSTNAME = "SS_FIRSTNAME";
    public static final String SS_LASTNAME = "SS_LASTNAME";
    public static final String SS_BIRTHNAME ="SS_BIRTHNAME";
    public static final String SS_CITY = "SS_CITY";
    public static final String SS_ZIP = "SS_ZIP";

    // PACS DICOM specific date and time format strings
    public static final String DICOM_DATEFORMAT = "yyyyMMdd";
    public static final String DICOM_TIMEFORMAT = "HHmmss";

    // RPB specific date and time format strings
    public static final String RPB_DATEFORMAT = "dd.MM.yyyy";
    public static final String RPB_TIMEFORMAT = "HH:mm:ss";

    // RPB specific password hash for LDAP users
    public static final String RPB_LDAPPASSHASH = "LDAP";

    private Constants() {
        // NOOP
    }

}