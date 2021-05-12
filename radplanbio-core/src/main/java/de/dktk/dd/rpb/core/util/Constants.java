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

package de.dktk.dd.rpb.core.util;

/**
 * RPB constants
 */
public class Constants {

    //region RPB

    // Study identifier for study that aggregates data from all patients
    public static final String study0Identifier = "study-0";
    public static final String study0EdcCode = "S0";
    // StudySubject prefix for study that aggregates data from all patients
    public static final String HISprefix = "HIS-";
    // PartnerSite identifier for virtual DELETED site
    public static final String DELETED_IDENTIFIER = "DEL";
    // PartnerSite identifier for virtual DUMMY site
    public static final String DUMMY_IDENTIFIER = "DUM";
    // PartnerSite identifier for virtual DROPOUT site
    public static final String DROPOUT_IDENTIFIER = "DRO";
    
    // RPB specific date and time format strings
    //TODO: would be nice to have those configurable
    public static final String RPB_DATEFORMAT = "dd.MM.yyyy";
    public static final String RPB_TIMEFORMAT = "HH:mm:ss";

    // RPB Spring Security user name and password parameter
    public static final String RPB_UNAMEPARAM = "j_username";
    public static final String RPB_PASSWDPARAM = "j_password";

    // RPB specific password hash for LDAP users
    public static final String RPB_LDAPPASSHASH = "LDAP";

    // RPB specific separator for identifier prefixes
    public static final String RPB_IDENTIFIERSEP = "-";

    // Pseudo date to indicate an artificial date
    public static final String PSEUDO_DATE = "2030-01-01";

    //endregion

    //region DATA

    // Constants for mapping to EDC StudySubject fields and PID IDAT fields
    public static final String SS_PERSONID = "SS_PERSONID";
    public static final String SS_STUDYSUBJECTID = "SS_STUDYSUBJECTID";
    public static final String SS_SECONDARYID = "SS_SECONDARYID";
    public static final String SS_ENROLLMENTDATE = "SS_ENROLLMENTDATE";
    public static final String SS_GENDER = "SS_GENDER";
    public static final String SS_DATEOFBIRTH = "SS_DATEOFBIRTH";
    public static final String SS_YEAROFBIRTH = "SS_YEAROFBIRTH";
    public static final String SS_FIRSTNAME = "SS_FIRSTNAME";
    public static final String SS_LASTNAME = "SS_LASTNAME";
    public static final String SS_BIRTHNAME ="SS_BIRTHNAME";
    public static final String SS_CITY = "SS_CITY";
    public static final String SS_ZIP = "SS_ZIP";
    
    // Constants for mapping to EDC StudyEvent
    public static final String SE_STARTDATE = "STARTDATE";

    //endregion

    //region EDC

    //region OpenClinica

    // EDC specific data types
    public static final String OC_INTEGER = "integer";
    public static final String OC_DECIMAL = "float";
    public static final String OC_STRING = "text";
    public static final String OC_DATE = "date";
    public static final String OC_PDATE = "partialDate";
    public static final String OC_FILE = "file";

    public static final String OC_YES = "Yes";
    public static final String OC_NO = "No";
    public static final String OC_NA = "N/A";

    // EDC specific date format string
    public static final String OC_DATEFORMAT = "yyyy-MM-dd";
    public static final String OC_TIMEFORMAT = "HH:mm";
    public static final String OC_TIMESTAMPFORMAT = "yyyy-MM-dd HH:mm:ss'.0'";

    // EDC specific multi value separator
    public static final String OC_MULTIVALSEP = ",";

    // EDC specific password hash for LDAP users
    public static final String OC_LDAPPASSHASH = "*";

    // EDC UNGROUPED ItemGroupDefinition OID
    public static final String OC_IG_UNGROUPED = "UNGROUPED";

    // EDC web services
    public static final String OC_SUCCESS = "Success";
    public static final String OC_FAIL = "Fail";
    public static final String OC_ERROR_WRONGSTATUS = "has wrong status";
    public static final String OC_LOGINMETHOD = "j_spring_security_check";
    public static final String OC_UNAMEPARAM = "j_username";
    public static final String OC_PASSWDPARAM = "j_password";

    // OpenClinica Export - LabKey import

    public static final String DATASETS_FOLDER = "datasets";
    public static final String ODM_FULL_EXPORT_FOLDER = "ODM_1.3_Full";
    public static final String EXPORT_ALL_SUFFIX = "-Export-All";

    //endregion


    //endregion

    //region BIO

    //region Centraxx

    public static final String CXX_CODESEP = "_";

    public static final String CXX_HIS = "MPI";
    public static final String CXX_RPB = "RPB";

    // TODO: these two should be configurable
    public static final String CXX_UKD = "UKD";
    public static final String CXX_PRINCIPAL_SITE = "STR";

    public static final String CXX_ID = "ID";

    public static final String CXX_PATIENTID = "PATIENTID";
    public static final String CXX_PATIENT_PSEUDONYM = "PATIENT_PSEUDONYM";

    public static final String CXX_SAMPLEID = "SAMPLEID";
    public static final String CXX_EXTSAMPLEID = "EXTSAMPLEID";

    public static final String CXX_EPISODEID = "EPISODEID";
    public static final String CXX_EPISODENR = "EPISODENO";

    public static final String CXX_TYPE_LIQUID = "LIQUID";
    public static final String CXX_TYPE_TISSUE = "TISSUE";

    public static final String CXX_CONSENT = "CONSENT";

    public static final String CXX_MEASUREMENT_PROFILE = "PROFILE";
    public static final String CXX_OCCURRENCE = "TIME_OF_COLLECTION_WITHIN_STUDY";
    public static final String CXX_INTERFERENCE_BLOOD = "INTERFERENCE_BLOOD";

    //endregion

    //endregion

    // region CCP

    public static final String CCP_PREFIX_PART = "L";

    // endregion
    //region PACS

    // PACS DICOM specific date and time format strings
    public static final String DICOM_DATEFORMAT = "yyyyMMdd";
    public static final String DICOM_TIMEFORMAT = "HHmmss";

    // PACS DICOM series modalities
    public static final String DICOM_RTSTRUCT = "RTSTRUCT";
    public static final String DICOM_RTPLAN = "RTPLAN";
    public static final String DICOM_RTDOSE = "RTDOSE";
    public static final String DICOM_CT = "CT";
    public static final String DICOM_MR = "MR";
    public static final String DICOM_PT = "PT";
    public static final String DICOM_US = "US";
    public static final String DICOM_ST = "ST";

    // RPB DICOM study type (which depends on series modalities)
    public static final String RPB_CONTOURING = "Contouring";
    public static final String RPB_TREATMENTPLAN = "TreatmentPlan";
    public static final String RPB_CT = "CT";
    public static final String RPB_MRI = "MRI";
    public static final String RPB_PET = "PET";
    public static final String RPB_PETCT = "PET-CT";
    public static final String RPB_PETMRI = "PET-MRI";
    public static final String RPB_US = "Ultrasonography";
    public static final String RPB_SPECT = "SPECT";
    public static final String RPB_OTH = "OTH";

    //endregion
    
    private Constants() {
        // NOOP
    }
    
}