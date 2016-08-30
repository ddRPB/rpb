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

package de.dktk.dd.rpb.core.dao.edc;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.util.Constants;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * OpenClinica Database Dao - data layer abstraction
 * Should be deprecated in the future (use only web services)
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

    //endregion

    //region Studies

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

        List<Study> studies = new ArrayList<Study>();
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
     * Update open clinica active study for specified user account
     * @param username user account
     * @param newActiveStudy new active study
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

    //region CRF item values

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