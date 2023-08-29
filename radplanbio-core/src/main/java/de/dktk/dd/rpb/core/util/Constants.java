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

    // RPB Spring Security username and password parameter
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

    public static final String ODM = "ODM";
    public static final String DATASETS_FOLDER = "datasets";
    public static final String ODM_FULL_EXPORT_FOLDER = "ODM_1.3_Full";
    public static final String EXPORT_ALL_SUFFIX = "-Export-All";

    // ODM data formats
    // https://wiki.cdisc.org/display/ODM2/Data+Formats
    public static final String ODM_PARTIAL_DATE = "partialDate";

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

    // region LabKey
    public static final String LABKEY_IDENTIFIERSEP = "-";
    public static final Integer LABKEY_DEFAULT_REPEAT_KEY = 1;

    public static final String LABKEY_SHARED_STUDY_PREFIX = "SHA-";

    public static final String LABKEY_DECODED_VALUE_SUFFIX = "_DECODED";
    public static final String LABKEY_MULTISELECT_VALUE_SUFFIX = "_MS_CODE_";
    public static final String LABKEY_PARTIAL_DATE_MIN_SUFFIX = "_MIN";
    public static final String LABKEY_PARTIAL_DATE_MAX_SUFFIX = "_MAX";

    public static final String LABKEY_EDC_ATTRIBUTES = "EDC-Attributes";
    public static final String LABKEY_EDC_FORMS = "EDC-Forms";
    public static final String LABKEY_EDC_FORM_VERSIONS = "EDC-FormVersions";
    public static final String LABKEY_EDC_ITEM_GROUPS = "EDC-ItemGroups";

    public static final String LABKEY_SUBJECT_ATTRIBUTES = "SubjectAttributes";
    public static final String LABKEY_SUBJECT_GROUP_ATTRIBUTES = "SubjectGroupAttributes";
    public static final String LABKEY_EVENT_ATTRIBUTES = "EventAttributes";
    public static final String LABKEY_FORM_ATTRIBUTES = "FormAttributes";

    // all LabKey tables

    public static final String LABKEY_STUDY_EVENT_OID = "StudyEventOID";
    public static final String LABKEY_STUDY_EVENT_REPEAT_KEY = "StudyEventRepeatKey";

    // SubjectAttributes; EventAttributes; CRFAttributes and FormAttributes

    public static final String LABKEY_PARTICIPANT_ID = "ParticipantId";
    public static final String LABKEY_SEQUENCE_NUMBER = "SequenceNum";

    // SubjectAttributes
    public static final String LABKEY_PATIENT_ID = "PID";
    public static final String LABKEY_SECONDARY_ID = "SecondaryID";
    public static final String LABKEY_GENDER = "Gender";
    public static final String LABKEY_DATE = "Date";
    public static final String LABKEY_BIRTHDATE = "BirthDate";
    public static final String LABKEY_BIRTHYEAR = "BirthYear";
    public static final String LABKEY_ENROLLMENT = "Enrollment";

    // SubjectAttributes and FormAttributes

    public static final String LABKEY_STATUS = "Status";

    // SubjectGroup attributes

    public static final String LABKEY_STUDY_GROUP_CLASS_ID = "StudyGroupClassID";
    public static final String LABKEY_STUDY_GROUP_CLASS_NAME = "StudyGroupClassName";
    public static final String LABKEY_STUDY_GROUP_NAME = "StudyGroupName";


    // EventAttributes

    public static final String LABKEY_SYSTEM_STATUS= "SystemStatus";
    public static final String LABKEY_TYPE= "Type";
    public static final String LABKEY_EVENT_NAME= "EventName";
    public static final String LABKEY_START_DATE= "StartDate";
    public static final String LABKEY_END_DATE= "EndDate";

    // CRFAttributes

    public static final String LABKEY_STUDY_OID= "StudyOID";
    public static final String LABKEY_STUDY_SITE_IDENTIFIER= "StudySiteIdentifier";
    public static final String LABKEY_FORM_OID= "FormOID";
    public static final String LABKEY_FORM_REPEAT_KEY= "FormRepeatKey";
    public static final String LABKEY_ITEM_GROUP_OID= "ItemGroupOID";
    public static final String LABKEY_ITEM_GROUP_REPEAT_KEY= "ItemGroupRepeatKey";
    public static final String LABKEY_FORM_ITEM_GROUP_COMBINED_ID = "CombinedID";

    // FormAttributes

    public static final String LABKEY_FORM_VERSION= "Version";
    public static final String LABKEY_INTERVIEWER_NAME= "InterviewerName";
    public static final String LABKEY_INTERVIEW_DATE= "InterviewDate";


    // endregion

    // region CCP

    public static final String CCP_PREFIX_PART = "L";

    // endregion

    //region PACS

    // PACS DICOM specific date and time format strings
    public static final String DICOM_DATEFORMAT = "yyyyMMdd";
    public static final String DICOM_TIMEFORMAT = "HHmmss";

    // PACS DICOM SERIES PROPERTIES
    public static final String DICOM_SERIES_INSTANCE_UID = "SeriesInstanceUID";
    public static final String DICOM_FRAME_OF_REFERENCE_UID = "FrameOfReferenceUID";
    public static final String DICOM_SERIES_DESCRIPTION = "SeriesDescription";
    public static final String DICOM_MODALITY = "Modality";
    public static final String DICOM_SERIES_TIME = "SeriesTime";
    public static final String DICOM_SERIES_DATE = "SeriesDate";


    // PACS DICOM series modalities
    public static final String DICOM_RTSTRUCT = "RTSTRUCT";
    public static final String DICOM_RTPLAN = "RTPLAN";
    public static final String DICOM_RTDOSE = "RTDOSE";
    public static final String DICOM_RTIMAGE = "RTIMAGE";
    public static final String DICOM_CT = "CT";
    public static final String DICOM_MR = "MR";
    public static final String DICOM_PT = "PT";
    public static final String DICOM_US = "US";
    public static final String DICOM_ST = "ST";

    // PACS DICOM IMAGE PROPERTIES
    public static final String DICOM_IMAGE_SOP_INSTANCE_UID = "SOPInstanceUID";
    public static final String DICOM_IMAGE_SIZE = "Size";
    public static final String DICOM_IMAGE_ROIs = "ROIs";
    public static final String DICOM_IMAGE_ROI_NAME = "ROIName";
    public static final String DICOM_IMAGE_REFERENCED_FRAME_OF_REFERENCE_UID = "ReferencedFrameOfReferenceUID";
    public static final String DICOM_IMAGE_ROI_GENERATION_ALGORITHM = "ROIGenerationAlgorithm";
    public static final String DICOM_IMAGE_RT_ROI_OBSERVATIONS = "RTROIObservations";
    public static final String DICOM_IMAGE_ROI_NUMBER = "ROINumber";
    public static final String DICOM_IMAGE_RT_ROI_INTERPRETED_TYPE = "RTROIInterpretedType";
    public static final String DICOM_IMAGE_REFERENCED_ROI_NUMBER = "ReferencedROINumber";
    public static final String DICOM_IMAGE_OBSERVATION_NUMBER = "ObservationNumber";
    public static final String DICOM_IMAGE_APPROVAL_STATUS = "ApprovalStatus";


    // PACS DICOM RTStruct PROPERTIES
    public static final String RTSTRUCT_STRUCTURE_SET_LABEL = "StructureSetLabel";
    public static final String RTSTRUCT_STRUCTURE_SET_NAME = "StructureSetName";
    public static final String RTSTRUCT_STRUCTURE_SET_DESCRIPTION = "StructureSetDescription";
    public static final String RTSTRUCT_STRUCTURE_SET_DATE = "StructureSetDate";
    public static final String RTSTRUCT_REFERENCED_FRAME_OF_REFERENCE_UID = "ReferencedFrameOfReferenceUID";
    public static final String RTSTRUCT_REFERENCED_RTSERIES_UID = "ReferencedRTSeriesUID";

    // PACS DICOM RTPLAN PROPERTIES
    public static final String RTPLAN_LABEL = "RTPlanLabel";
    public static final String RTPLAN_MANUFACTURER_MODEL_NAME = "ManufacturerModelName";
    public static final String RTPLAN_NAME = "RTPlanName";
    public static final String RTPLAN_DATE = "RTPlanDate";
    public static final String RTPLAN_MANUFACTURER = "Manufacturer";
    public static final String RTPLAN_DESCRIPTION = "RTPlanDescription";
    public static final String RTPLAN_GEOMETRY = "RTPlanGeometry";
    public static final String SERIES_NUMBER = "SeriesNumber";
    public static final String REFERENCED_RTSTRUCT_UID = "ReferencedRTStructUID";

    // PACS DICOM RTDOSE AND IMAGE PROPERTIES
    public static final String IMAGE_TYPE = "ImageType";
    public static final String REFERENCED_RTPLAN_UID = "ReferencedRTPlanUID";
    public static final String INSTANCE_CREATION_DATE = "InstanceCreationDate";

    // PACS DICOM RTDOSE PROPERTIES
    public static final String DOSE_UNIT = "DoseUnits";
    public static final String DOSE_TYPE = "DoseType";
    public static final String DOSE_COMMENT = "DoseComment";
    public static final String DOSE_SUMMATION_TYPE = "DoseSummationType";

    // PACS DICOM IMAGE PROPERTIES
    public static final String RTIMAGE_LABEL = "RTImageLabel";
    public static final String RTIMAGE_NAME= "RTImageName";
    public static final String RTIMAGE_DESCRIPTION= "RTImageDescription";

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

    //region UPLOADER

    public static final String UPLOADER_URL_PATH = "/uploader";
    public static final String UPLOADER_URL_STUDY_IDENTIFIER = "studyidentifier";
    public static final String UPLOADER_URL_SITE_IDENTIFIER = "siteidentifier";
    public static final String UPLOADER_URL_STUDY_OID = "studyoid";
    public static final String UPLOADER_URL_STUDY_INSTANCE_ITEM_OID = "studyinstanceitemoid";
    public static final String UPLOADER_URL_STUDY_EDC_CODE = "studyedccode";

    public static final String UPLOADER_URL_EVENT_IDENTIFIER = "eventoid";
    public static final String UPLOADER_URL_EVENT_REPEATKEY = "eventrepeatkey";
    public static final String UPLOADER_URL_EVENT_START_DATE = "eventstartdate";
    public static final String UPLOADER_URL_EVENT_END_DATE = "eventenddate";
    public static final String UPLOADER_URL_EVENT_NAME = "eventname";
    public static final String UPLOADER_URL_EVENT_DESCRIPTION = "eventdescription";

    public static final String UPLOADER_URL_FORM_IDENTIFIER = "formoid";
    public static final String UPLOADER_URL_ITEM_GROUP_IDENTIFIER = "itemgroupoid";
    public static final String UPLOADER_URL_ITEM_GROUP_REPEATKEY = "itemgrouprepeatkey";
    public static final String UPLOADER_URL_ITEM_LABEL = "itemlabel";
    public static final String UPLOADER_URL_ITEM_DESCRIPTION = "itemdescription";

    public static final String UPLOADER_URL_SUBJECT_IDENTIFIER = "studysubjectid";
    public static final String UPLOADER_URL_SUBJECT_KEY = "subjectkey";
    public static final String UPLOADER_URL_PATIENT_IDENTIFIER = "pid";
    public static final String UPLOADER_URL_PATIENT_IDENTIFIER_ITEM_OID = "dicompatientiditemoid";
    public static final String UPLOADER_URL_DATE_OF_BIRTH = "dob";
    public static final String UPLOADER_URL_YEAR_OF_BIRTH = "yob";
    public static final String UPLOADER_URL_GENDER = "gender";

    // endregion


    
    private Constants() {
        // NOOP
    }
    
}