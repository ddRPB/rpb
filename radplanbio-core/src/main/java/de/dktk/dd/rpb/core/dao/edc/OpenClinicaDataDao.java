/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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

package de.dktk.dd.rpb.core.dao.edc;

import java.lang.Object;
import java.lang.String;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.util.Constants;

import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudyType;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * JPA 2 Data Access Object for OpenClinica direct database queries
 *
 * @author tomas@skripcak.net
 * @since 14 Jun 2013
 */
@Named
@Singleton
public class OpenClinicaDataDao extends JdbcDaoSupport {

    //region Methods

    //region UserAccount

    /**
     * Get hash of OC standard user (no LDAP) for working with SOAP web services
     * @param username OC username
     * @return OC hash
     */
    public String getUserAccountHash(String username) {
        String result = "";

        String sql = "SELECT ua.passwd as PasswordHash from user_account ua where ua.user_name = ?";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, username);

        // Can be multiple users with the same name but I want to have not LDAP user that is used for SOAP WS
        for (Map<String, Object> row : rows) {
            String hash = (String) row.get("PasswordHash");

            // Skip LDAP and participate accounts
            if (!hash.equals(Constants.OC_LDAPPASSHASH)) {
                result = hash;
                break;
            }
        }

        return result;
    }

    public String getUserId(String username) {

        String result = "";

        String sql = "SELECT ua.user_id as ID from user_account ua where ua.user_name = ?";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, username);

        if (rows.size() > 0) {
            result = rows.get(0).get("ID").toString();
        }

        return result;
    }

    public String getUserName(int id) {

        String result = "";

        String sql = "SELECT ua.user_name as UserName from user_account ua where ua.user_id = ?";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, id);

        if (rows.size() > 0) {
            result = rows.get(0).get("UserName").toString();
        }

        return result;
    }

    //endregion

    //region Study

    /**
     * Get Study according to unique identifier
     * @param identifier of study
     * @return Study entity
     */
    public Study getStudyByIdentifier(String identifier) {
        String sql = "SELECT s.study_id as id,\n" +
                "st.status_id as statusId,\n" +
                "st.name as statusName,\n" +
                "st.description as statusDescription,\n" +
                "s.parent_study_id as parentStudyId,\n" +
                "s.unique_identifier as uniqueIdentifier,\n" +
                "s.secondary_identifier as secondaryIdentifier,\n" +
                "s.name as name\n" +
                "FROM STUDY s\n" +
                "LEFT JOIN STATUS st on st.status_id = s.status_id\n" +
                "WHERE s.unique_identifier = ?";

        List<Study> studies = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, identifier);

        for (Map<String, Object> row : rows) {
            Study study = new Study();

            study.setId((Integer) row.get("id"));
            study.setUniqueIdentifier((String) row.get("uniqueIdentifier"));
            study.setSecondaryIdentifier((String) row.get("secondaryIdentifier"));
            study.setName((String) row.get("name"));

            Status status = new Status();
            status.setId((Integer) row.get("statusId"));
            status.setName((String) row.get("statusName"));
            status.setDescription((String) row.get("statusDescription"));

            study.setStatus(status);

            studies.add(study);
        }

        return studies.size() == 1 ? studies.get(0) : null;
    }

    /**
     * Get active study of specified user
     * @param username of user whose active study I am looking for
     * @return Study entity
     */
    public Study getUserActiveStudy(String username) {
        String sql = "SELECT s.study_id as id,\n" +
                "s.parent_study_id as parentStudyId,\n" +
                "s.unique_identifier as uniqueIdentifier,\n" +
                "s.secondary_identifier as secondaryIdentifier,\n" +
                "s.name as name,\n" +
                "s.oc_oid as ocoid\n" +
                "FROM USER_ACCOUNT ua\n" +
                "LEFT JOIN STUDY s\n" +
                "ON ua.active_study = s.study_id\n" +
                "WHERE ua.user_name = ?";

        List<Study> studies = new ArrayList<Study>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, username);

        for (Map<String, Object> row : rows) {

            Study parentStudy = null;
            if (row.get("parentStudyId") != null) {
                String innerSql = "SELECT s.study_id as id,\n" +
                        "s.unique_identifier as uniqueIdentifier,\n" +
                        "s.secondary_identifier as secondaryIdentifier,\n" +
                        "s.name as name,\n" +
                        "s.oc_oid as ocoid\n" +
                        "FROM STUDY s\n" +
                        "WHERE s.study_id = ?";

                List<Map<String, Object>> parentRows = getJdbcTemplate().queryForList(innerSql, (Integer) row.get("parentStudyId"));

                for (Map<String, Object> prow : parentRows) {
                    parentStudy = new Study();
                    parentStudy.setId((Integer) prow.get("id"));
                    parentStudy.setUniqueIdentifier((String) prow.get("uniqueIdentifier"));
                    parentStudy.setSecondaryIdentifier((String) prow.get("secondaryIdentifier"));
                    parentStudy.setName((String) prow.get("name"));
                    parentStudy.setOcoid((String) prow.get("ocoid"));
                }
            }

            Study study = new Study();

            study.setId((Integer) row.get("id"));
            study.setUniqueIdentifier((String) row.get("uniqueIdentifier"));
            study.setSecondaryIdentifier((String) row.get("secondaryIdentifier"));
            study.setName((String) row.get("name"));
            study.setOcoid((String) row.get("ocoid"));

            if (parentStudy != null) {
                study.setParentStudy(parentStudy);
            }

            studies.add(study);
        }

        return studies.size() == 1 ? studies.get(0) : null;
    }

    /**
     * Update OC active study for specified user account
     * @param username user account
     * @param newActiveStudy new active study
     * @return true if rows updated
     */
    public boolean changeUserActiveStudy(String username, Study newActiveStudy) {

        String sql = "UPDATE USER_ACCOUNT\n" +
            "SET active_study = ?\n" +
            "WHERE user_name = ?";

        Object[] params = { newActiveStudy.getId(), username };
        int rowsUpdated = getJdbcTemplate().update(sql, params);

        return rowsUpdated > 0;
    }

    //endregion

    //region StudySubject
    
    public List<StudySubject> findStudySubjectsByPseudonym(String pid, List<StudyType> studyTypeList) {
        List<StudySubject> results = new ArrayList<>();

        String sql = "SELECT ss.study_subject_id AS studySubjectId,\n" +
                "       ss.label AS studySubjectLabel,\n" +
                "       ss.secondary_label AS studySubjectSecondaryId,\n" +
                "       ss.oc_oid AS studySujectOid,\n" +
                "       ss.enrollment_date AS studySubjectEnrollmentDate,\n" +
                "       status.name AS studySubjectStatus,\n" +
                "       s.gender AS subjectSex,\n" +
                "       s.unique_identifier AS subjectPid,\n" +
                "       study.unique_identifier AS studyIdentifier,\n" +
                "       parent.unique_identifier AS parentStudyIdentifier\n" +
                "  FROM study_subject ss\n" +
                "  LEFT JOIN subject s ON ss.subject_id = s.subject_id\n" +
                "  LEFT JOIN study study ON ss.study_id = study.study_id\n" +
                "  LEFT JOIN study parent ON study.parent_study_id = parent.study_id\n" +
                "  LEFT JOIN status status ON ss.status_id = status.status_id\n" +
                "  WHERE s.unique_identifier = ?";

        Object[] params = {pid};
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, params);

        for (Map<String, Object> row : rows) {
            StudySubject studySubject = new StudySubject();

            studySubject.setId((Integer) row.get("studySubjectId"));
            studySubject.setStudySubjectId((String) row.get("studySubjectLabel"));
            studySubject.setSecondaryId((String) row.get("studySubjectSecondaryId"));
            studySubject.setSubjectKey((String) row.get("studySubjectOid"));
            studySubject.setStatus((String) row.get("studySubjectStatus"));

            java.sql.Date sqlStartDate = (java.sql.Date) row.get("studySubjectEnrollmentDate");
            Date startDate = new Date(sqlStartDate.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            studySubject.setEnrollmentDate(sdf.format(startDate));

            studySubject.setSex((String) row.get("subjectSex"));
            studySubject.setPid((String) row.get("subjectPid"));

            // Setup also person (may be needed)
            Person person = new Person();
            person.setPid(studySubject.getPid());
            studySubject.setPerson(person);

            String studyIdentifier = (String) row.get("studyIdentifier");
            String parentStudyIdentifier = (String) row.get("parentStudyIdentifier");

            // Associate only with studies accessible for requesting user (passed as argument)
            if (studyTypeList != null) {
                for (StudyType st : studyTypeList) {

                    boolean isStudySiteSubject = false;

                    if (studyIdentifier != null && !studyIdentifier.isEmpty() &&
                        parentStudyIdentifier != null && !parentStudyIdentifier.isEmpty()) {

                        isStudySiteSubject = true;
                    }

                    // Processing multi centre study
                    if (st.getSites() != null &&
                        st.getSites().getSite() != null &&
                        st.getSites().getSite().size() > 0) {

                        // Retrieved studySubject is enrolled to study site
                        if (isStudySiteSubject) {
                            if (parentStudyIdentifier.equalsIgnoreCase(st.getIdentifier())) {

                                for (SiteType site : st.getSites().getSite()) {

                                    if (studyIdentifier.equalsIgnoreCase(site.getIdentifier())) {
                                        Study siteStudy = new Study();

                                        siteStudy.setOid(site.getOid());
                                        siteStudy.setName(site.getName());
                                        siteStudy.setUniqueIdentifier(site.getIdentifier());

                                        // Assign site to parent study
                                        siteStudy.setParentStudy(new Study(st));

                                        // Assign site study to studySubject
                                        studySubject.setStudy(siteStudy);

                                        // Study Subject can be displayed
                                        results.add(studySubject);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // Processing mono centre study
                    else {
                        if (studyIdentifier.equalsIgnoreCase(st.getIdentifier())) {

                            // Assign study to studySubject
                            studySubject.setStudy(new Study(st));

                            // Study Subject can be displayed
                            results.add(studySubject);
                            break;
                        }
                    }
                }
            }
        }
        
        return results;
    }

    //endregion

    //region EventData

    /**
     * Get list of StudySubject EventData occurrences for specified EventDefinition
     * @param studySubject specified StudySubject - OC OID
     * @param eventDefinition specified EventDefinition - OC OID
     * @return List of StudySubject EventData occurrences
     */
    public List<EventData> getEventData(StudySubject studySubject, EventDefinition eventDefinition) {
        List<EventData> results = new ArrayList<>();

        String sql = "SELECT se.study_event_id AS id, sed.name AS eventName, se.sample_ordinal AS repeatKey, " +
                "se.date_start AS dateStart\n" +
                "FROM STUDY_EVENT se\n" +
                "JOIN STUDY_EVENT_DEFINITION sed\n" +
                "ON se.study_event_definition_id = sed.study_event_definition_id\n" +
                "JOIN STUDY_SUBJECT ss\n" +
                "ON se.study_subject_id = ss.study_subject_id\n" +
                "WHERE ss.oc_oid = ? AND sed.oc_oid = ?";

        Object[] params = { studySubject.getSubjectKey(), eventDefinition.getOid() };
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, params);

        for (Map<String, Object> row : rows) {
            EventData event = new EventData();

            event.setStudyEventOid(eventDefinition.getOid());
            event.setId((Integer) row.get("id"));
            event.setEventName((String) row.get("eventName"));

            Timestamp timestamp = (Timestamp) row.get("dateStart");
            Date startDate = new Date(timestamp.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.0'");
            event.setStartDate(sdf.format(startDate));

            int repeatKey = (Integer) row.get("repeatKey");
            event.setStudyEventRepeatKey(String.valueOf(repeatKey));

            results.add(event);
        }

        return results;
    }

    /**
     * Update OC study event repeat key
     * @param eventData study event data occurrence - OC ID
     * @param sampleOrdinal new repeat key
     * @return true if rows updated
     */
    public boolean changeStudyEventRepeatKey(EventData eventData, int sampleOrdinal) {

        String sql = "UPDATE STUDY_EVENT\n" +
            "SET sample_ordinal = ?\n" +
            "WHERE study_event_id = ?";

        Object[] params = { sampleOrdinal, eventData.getId() };
        int rowsUpdated = getJdbcTemplate().update(sql, params);

        return rowsUpdated > 0;
    }

    //endregion

    //region ItemData

    /**
     * Query all data item in a study
     * @param uniqueIdentifier study
     * @return list of item definition with values
     */
    public List<ItemDefinition> findAllItemDataForStudy(String uniqueIdentifier) {

        String sql = "SELECT s.study_id as StudyId\n" +
                "FROM Study s\n" +
                "WHERE s.unique_identifier = ?";

        int studyId = getJdbcTemplate().queryForObject(sql, new Object[] { uniqueIdentifier }, Integer.class);

        sql = "SELECT ss.study_subject_id\n" +
                ", ss.label AS StudySubjectLabel\n" +
                ", s.date_of_birth AS BrthDat\n" +
                ", s.gender AS Sex\n" +
                ", s.unique_identifier AS Inits\n" +
                ", sed.study_event_definition_id AS SEID\n" +
                ", sed.name AS SECode\n" +
                ", sed.description AS SEName\n" +
                ", se.sample_ordinal AS SERepeat\n" +
                ", cv.crf_id AS FormID\n" +
                ", ec.crf_version_id AS FormVersID\n" +
                ", c.name AS FormName\n" +
                ", cv.name AS VersionName\n" +
                ", 1 AS FormRepeat\n" +
                ", i.item_id AS ItemID\n" +
                ", i.name AS ItemCode\n" +
                ", id.ordinal AS IGRepeat\n" +
                ", i.description AS ItemDescription\n" +
                ", ifm.show_item AS Visible\n" +
                ", id.value AS ItemValue\n" +
                ", decode AS ItemDecode\n" +
                "FROM STUDY_SUBJECT ss\n" +
                "INNER JOIN SUBJECT s\n" +
                "ON ss.subject_id = s.subject_id\n" +
                "inner join study_event se\n" +
                "on ss.study_subject_id = se.study_subject_id\n" +
                "inner join study_event_definition sed\n" +
                "on se.study_event_definition_id = sed.study_event_definition_id\n" +
                "inner join event_crf ec\n" +
                "on se.study_event_id = ec.study_event_id\n" +
                "inner join crf_version cv\n" +
                "on ec.crf_version_id = cv.crf_version_id\n" +
                "inner join crf c\n" +
                "on cv.crf_id = c.crf_id\n" +
                "inner join event_definition_crf edc\n" +
                "on cv.crf_id = edc.crf_id\n" +
                "and se.study_event_definition_id = edc.study_event_definition_id\n" +
                "inner join item_form_metadata ifm\n" +
                "on cv.crf_version_id = ifm.crf_version_id\n" +
                "inner join item i\n" +
                "on ifm.item_id = i.item_id\n" +
                "left join item_data id\n" +
                "on ec.event_crf_id = id.event_crf_id\n" +
                "and i.item_id = id.item_id\n" +
                "left join\n" +
                "(\n" +
                "\tSELECT\n" +
                "\t  version_id as crf_version_id\n" +
                "\t, response_set_id as set_id\n" +
                "\t, label\n" +
                "\t, options_values as value\n" +
                "\t, options_text as decode\n" +
                "\tFROM response_set\n" +
                "\tWHERE response_type_id = 3\n" +
                "\t UNION\n" +
                "\tSELECT\n" +
                "\t  version_id as crf_version_id\n" +
                "\t, response_set_id as set_id\n" +
                "\t, label\n" +
                "\t, trim (both from regexp_split_to_table(options_values, E',')) as value\n" +
                "\t, trim (both from regexp_split_to_table(options_text, E',')) as decode\n" +
                "\tFROM response_set\n" +
                "\tWHERE response_type_id = 6\n" +
                "\t) cls\n" +
                "ON ifm.response_set_id = cls.set_id\n" +
                "AND ifm.crf_version_id = cls.crf_version_id\n" +
                "AND id.value = cls.value\n" +
                "\n" +
                "WHERE ss.study_id = ?\n" +
                "ORDER BY\n" +
                "  ss.study_subject_id\n" +
                ", sed.ordinal\n" +
                ", se.sample_ordinal\n" +
                ", edc.ordinal\n" +
                ", id.ordinal\n" +
                ", ifm.ordinal";

        List<ItemDefinition> dataItemDefinitions = new ArrayList<ItemDefinition>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, studyId);

        for (Map<String, Object> row : rows) {
            ItemDefinition itemDefinition = new ItemDefinition();

            itemDefinition.setId((Integer) row.get("ItemID"));
            itemDefinition.setLabel((String) row.get("ItemDescription"));
            itemDefinition.setValue((String) row.get("ItemValue"));

            dataItemDefinitions.add(itemDefinition);
        }

        return dataItemDefinitions;
    }

    /**
     * Execute query for specific set of item definitions
     * @param uniqueIdentifier study
     * @param query list of query items
     * @return list of queried item definitions
     */
    public List<DataQueryResult> findItemData(String uniqueIdentifier, List<ItemDefinition> query, Boolean decodeItemValues) {

        // Prepare content of IN statement for big SELECT based on query parameter list
        String inQuery = "";
        for (ItemDefinition i : query) {
            inQuery += "'" + i.getOid() + "', ";
        }
        // Remove last comma space
        inQuery = inQuery.substring(0, inQuery.length() - 2);

        // Get main studyId
        String sql = "SELECT s.study_id as StudyId\n" +
                "FROM Study s\n" +
                "WHERE s.unique_identifier = ?";

        int studyId = getJdbcTemplate().queryForObject(sql, new Object[]{uniqueIdentifier}, Integer.class);

        // Also involve children studies
        sql = "SELECT s.study_id AS StudyId\n" +
                "FROM Study s\n" +
                "WHERE s.study_id = ? OR s.parent_study_id = ?";

        // Private key of study in OC database
        String inStudy = "";
        List<Map<String, Object>> studies = getJdbcTemplate().queryForList(sql, studyId, studyId);
        for (Map<String, Object> study : studies) {
            inStudy += "'" + study.get("StudyId") + "', ";
        }
        // Remove last comma space
        inStudy = inStudy.substring(0, inStudy.length() - 2);

        sql = "SELECT ss.study_subject_id AS Id,\n" +
                "ss.label AS StudySubjectLabel,\n" +
                "s.date_of_birth AS BirthDate,\n" +
                "s.gender AS Gender,\n" +
                "s.unique_identifier AS PersonId,\n" +
                "sed.name AS EventName,\n" +
                "sed.description AS EventDescription,\n" +
                "se.sample_ordinal AS ScheduledEventRepeatKey,\n" +
                "c.name AS CrfName,\n" +
                "cv.name AS CrfVersionName,\n" +
                "i.item_id AS ItemID,\n" +
                "i.oc_oid AS ItemOID,\n" +
                "i.name AS ItemName,\n" +
                "i.units AS ItemUnits,\n" +
                "i.phi_status AS ItemPhi,\n" +
                "idt.code AS ItemType,\n" +
                "id.ordinal AS RepeatItemRow,\n" +
                "i.description AS ItemDescription,\n" +
                "ifm.show_item AS ItemVisible,\n" +
                "ifm.left_item_text AS ItemLabel,\n" +
                "ifm.right_item_text AS ItemRightText,\n" +
                "ifm.required AS ItemRequired,\n" +
                "id.value AS ItemValue,\n" +
                "decode AS ItemDecode\n" +
                "FROM STUDY_SUBJECT ss\n" + // I want to see items for enrolled StudySubjects
                "INNER JOIN SUBJECT s\n" + // Associate StudySubjects with Subjects (relationship has to exists, each study subject is a subject)
                "ON ss.subject_id = s.subject_id\n" +
                "INNER JOIN STUDY_EVENT se\n" + // Associate StudySubjects with scheduled StudyEvents
                "ON ss.study_subject_id = se.study_subject_id\n" +
                "INNER JOIN STUDY_EVENT_DEFINITION sed\n" + // Associate scheduled StudyEvents with StudyEventDefinitions
                "ON se.study_event_definition_id = sed.study_event_definition_id\n" +
                "INNER JOIN EVENT_CRF ec\n" + // Associate scheduled StudyEvents with EventCRFs
                "ON se.study_event_id = ec.study_event_id\n" +
                "INNER JOIN CRF_VERSION cv\n" + // Associate EventCRFs with CRF verions
                "ON ec.crf_version_id = cv.crf_version_id\n" +
                "INNER JOIN CRF c\n" + // Associate CRF versions with CRF definition
                "ON cv.crf_id = c.crf_id\n" +
                "INNER JOIN EVENT_DEFINITION_CRF edc\n" + // Associate CRF versions with EventDefinitionCrfs
                "ON cv.crf_id = edc.crf_id\n" +
                "AND se.study_event_definition_id = edc.study_event_definition_id\n" +
                "INNER JOIN ITEM_FORM_METADATA ifm\n" +
                "ON cv.crf_version_id = ifm.crf_version_id\n" +
                "INNER JOIN ITEM i\n" +
                "ON ifm.item_id = i.item_id\n" +
                "INNER JOIN ITEM_DATA_TYPE idt\n" +
                "ON i.item_data_type_id = idt.item_data_type_id\n" +
                "LEFT JOIN ITEM_DATA id\n" + // Associate all of this with ItemData when exists (otherwise null)
                "ON ec.event_crf_id = id.event_crf_id\n" +
                "AND i.item_id = id.item_id\n" +
                "LEFT JOIN\n" + // Associate with decoded value for specific ItemTypes -> responseSetType (3, 5, 6, 7)
                    "(\n" +
                    "\tSELECT\n" + // Make union of ResponseSets (for decoding)
                    "\t  version_id as crf_version_id\n" +
                    "\t, response_set_id as set_id\n" +
                    "\t, label\n" +
                    "\t, options_values as value\n" +
                    "\t, options_text as decode\n" +
                    "\tFROM RESPONSE_SET\n" +
                    "\tWHERE response_type_id = 3\n" + // check box (select one from many options)
                    "\t UNION\n" +
                    "\tSELECT\n" +
                    "\t  version_id as crf_version_id\n" +
                    "\t, response_set_id as set_id\n" +
                    "\t, label\n" +
                    "\t, trim (both from regexp_split_to_table(options_values, E',')) as value\n" +
                    "\t, trim (both from regexp_split_to_table(options_text, E',')) as decode\n" +
                    "\tFROM RESPONSE_SET\n" +
                    "\tWHERE response_type_id = 5\n" + // radio (select one from many options)
                    "\t UNION\n" +
                    "\tSELECT\n" +
                    "\t  version_id as crf_version_id\n" +
                    "\t, response_set_id as set_id\n" +
                    "\t, label\n" +
                    "\t, trim (both from regexp_split_to_table(options_values, E',')) as value\n" +
                    "\t, trim (both from regexp_split_to_table(options_text, E',')) as decode\n" +
                    "\tFROM RESPONSE_SET\n" +
                    "\tWHERE response_type_id = 6\n" + // drop down list (select one from many options)
                    "\t UNION\n" +
                    "\tSELECT\n" +
                    "\t  version_id as crf_version_id\n" +
                    "\t, response_set_id as set_id\n" +
                    "\t, label\n" +
                    "\t, trim (both from regexp_split_to_table(options_values, E',')) as value\n" +
                    "\t, trim (both from regexp_split_to_table(options_text, E',')) as decode\n" +
                    "\tFROM RESPONSE_SET\n" +
                    "\tWHERE response_type_id = 7\n" + // list box (select one or more from many options)
                    "\t) cls\n" +
                "ON ifm.response_set_id = cls.set_id\n" +
                "AND ifm.crf_version_id = cls.crf_version_id\n" +
                "AND id.value = cls.value\n" +
                "\n" +
                "WHERE ss.study_id IN (" + inStudy + ") AND i.oc_oid IN (" + inQuery + ") \n" +
                "ORDER BY\n" +
                "ss.study_subject_id,\n" +
                "s.unique_identifier,\n" +
                "sed.ordinal,\n" +
                "se.sample_ordinal,\n" +
                "edc.ordinal,\n" +
                "id.ordinal,\n" +
                "ifm.ordinal";

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);

        // I need to create an object structure for displaying of query result
        List<DataQueryResult> queryResults = new ArrayList<DataQueryResult>();
        StudySubject studySubject;
        DataQueryResult data;
        for (Map<String, Object> row : rows) {

            // Subject as entity to group the results according to
            studySubject = new StudySubject();
            studySubject.setId((Integer) row.get("Id"));
            studySubject.setStudySubjectId((String) row.get("StudySubjectLabel"));
            studySubject.setPid((String) row.get("PersonId"));

            data = OpenClinicaDataDao.getByStudySubject(queryResults, studySubject);
            // Results for such subject does not exist (need to create a new one)
            if (data == null) {
                data = new DataQueryResult();
                queryResults.add(data);
            }

            data.setStudySubject(studySubject);

            // Data Item
            ItemDefinition itemDefinition = new ItemDefinition(decodeItemValues);
            itemDefinition.setId((Integer) row.get("ItemID"));
            itemDefinition.setOid((String) row.get("ItemOID"));
            itemDefinition.setName((String) row.get("ItemName"));
            itemDefinition.setLabel((String) row.get("ItemLabel"));
            itemDefinition.setRightText((String) row.get("ItemRightText"));
            itemDefinition.setDescription((String) row.get("ItemDescription"));
            itemDefinition.setValue((String) row.get("ItemValue"));
            if (row.get("ItemDecode") != null) {
                itemDefinition.setDecodedValue((String) row.get("ItemDecode"));
            }
            itemDefinition.setDataType((String) row.get("ItemType"));
            itemDefinition.setRepeatItemRow((Integer) row.get("RepeatItemRow"));
            itemDefinition.setUnits((String) row.get("ItemUnits"));
            itemDefinition.setIsPhi((Boolean) row.get("ItemPhi"));
            itemDefinition.setIsRequired((Boolean) row.get("ItemRequired"));
            data.addDataItem(itemDefinition);
        }

        return queryResults;
    }

    //endregion

    //region Helpers

    public static DataQueryResult getByStudySubject(List<DataQueryResult> list, StudySubject studySubject) {
        for(DataQueryResult queryResult : list) {
            if (queryResult.getStudySubject().getId() != null && queryResult.getStudySubject().getId().equals(studySubject.getId())) {
                return queryResult;
            }
        }
        return null;
    }

    //endregion

    //endregion

}