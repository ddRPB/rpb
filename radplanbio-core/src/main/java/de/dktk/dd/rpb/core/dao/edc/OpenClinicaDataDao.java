/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.exception.DataBaseItemNotFoundException;
import de.dktk.dd.rpb.core.util.Constants;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudyType;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.inject.Named;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JPA 2 Data Access Object for direct EDC database queries
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
     *
     * @param username OC username
     * @return OC hash
     */
    public String getUserAccountHash(String username) {
        String result = "";

        String sql = "SELECT ua.passwd AS PasswordHash FROM user_account ua WHERE ua.user_name = ?";
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

    //region Study

    /**
     * Get specified Study
     *
     * @param id primary key of study
     * @return Study entity
     */
    public Study getStudyById(String id) {
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
                "WHERE s.study_id = ?";

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, Integer.parseInt(id));
        List<Study> studies = convertDbResultsToStudyList(rows);

        return studies.size() == 1 ? studies.get(0) : null;
    }

    /**
     * Get specified Study
     *
     * @param studyIdentifier study unique identifier
     * @return Study entity
     */
    public Study getStudyByIdentifier(String studyIdentifier) {
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

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, studyIdentifier);
        List<Study> studies = convertDbResultsToStudyList(rows);

        return studies.size() == 1 ? studies.get(0) : null;
    }

    /**
     * Get specified user active Study
     *
     * @param username user
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

        List<Study> studies = new ArrayList<>();
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
     * Update specified user active Study
     *
     * @param username       user
     * @param newActiveStudy new active Study
     * @return true if user active Study updated
     */
    public boolean changeUserActiveStudy(String username, Study newActiveStudy) {

        String sql = "UPDATE USER_ACCOUNT\n" +
                "SET active_study = ?\n" +
                "WHERE user_name = ?";

        Object[] params = {newActiveStudy.getId(), username};
        int rowsUpdated = getJdbcTemplate().update(sql, params);

        return rowsUpdated > 0;
    }

    // TODO: implement so that we dont have to use old SOAP access
    public List<Study> findUserStudies(String username) {
        List<Study> results = new ArrayList<>();


        return results;
    }

    //endregion

    //region StudySubject

    /**
     * Get specified StudySubject enrolled in Study
     *
     * @param studyIdentifier identifier of study where subject is enrolled
     * @param studySubjectId  study subject ID of enrolled subject
     * @return StudySubject entity
     */
    public StudySubject getStudySubject(String studyIdentifier, String studySubjectId) {

        String sql = "SELECT ss.study_subject_id AS id,\n" +
                "       ss.label AS studySubjectId,\n" +
                "       ss.secondary_label AS secondaryId,\n" +
                "       ss.oc_oid AS subjectKey,\n" +
                "       ss.enrollment_date AS enrollmentDate,\n" +
                "       status.name AS studySubjectStatus,\n" +
                "       s.gender AS sex,\n" +
                "       s.unique_identifier AS pid,\n" +
                "       study.unique_identifier AS studyIdentifier,\n" +
                "       parent.unique_identifier AS parentStudyIdentifier\n" +
                "  FROM study_subject ss\n" +
                "  LEFT JOIN subject s ON ss.subject_id = s.subject_id\n" +
                "  LEFT JOIN study study ON ss.study_id = study.study_id\n" +
                "  LEFT JOIN study parent ON study.parent_study_id = parent.study_id\n" +
                "  LEFT JOIN status status ON ss.status_id = status.status_id\n" +
                "  WHERE study.unique_identifier = ? AND ss.label = ?";

        Object[] params = {studyIdentifier, studySubjectId};
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, params);

        List<StudySubject> studySubjects = convertDbResultsToStudySubjectList(rows);

        return studySubjects.size() == 1 ? studySubjects.get(0) : null;
    }

    /**
     * Get specified StudySubject with StudyEvents in specified Study
     *
     * @param studyIdentifier identifier of study where subject is enrolled
     * @param studySubjectId  study subject ID of enrolled subject
     * @return StudySubject entity
     */
    public StudySubject getStudySubjectWithEvents(String studyIdentifier, String studySubjectId) {

        StudySubject result = getStudySubject(studyIdentifier, studySubjectId);

        String sqlEventData = "SELECT ss.label as studySubjectId,\n" +
                "  ed.study_event_id as eventId,\n" +
                "  sed.oc_oid as studyEventOid,\n" +
                "  sed.study_event_definition_id as eventDefinitionId,\n" +
                "  sed.name as eventName,\n" +
                "  sed.description as eventDescription,\n" +
                "  sed.type as eventType,\n" +
                "  sed.category as eventCategory,\n" +
                "  sed.repeating as isRepeating,\n" +
                "  sed.ordinal as eventDefinitionOrdinal,\n" +
                "  ed.sample_ordinal as studyEventRepeatKey,\n" +
                "  ed.date_start as startDate,\n" +
                "  ed.date_end as endDate,\n" +
                "  st.name as eventSystemStatus,\n" +
                "  ses.name as eventStatus\n" +
                "  FROM STUDY_EVENT ed\n" +
                "  LEFT JOIN STUDY_EVENT_DEFINITION sed on sed.study_event_definition_id = ed.study_event_definition_id\n" +
                "  LEFT JOIN STATUS st on st.status_id = ed.status_id\n" +
                "  LEFT JOIN SUBJECT_EVENT_STATUS ses on ses.subject_event_status_id = ed.subject_event_status_id\n" +
                "  LEFT JOIN STUDY_SUBJECT ss on ss.study_subject_id = ed.study_subject_id\n" +
                "  LEFT JOIN STUDY s on s.study_id = ss.study_id\n" +
                "  WHERE s.unique_identifier = ? and ss.label = ?\n" +
                "  ORDER BY sed.ordinal, ed.sample_ordinal";

        if (result != null) {
            Object[] params = {studyIdentifier, studySubjectId};
            List<Map<String, Object>> rowsStudySubjectEvents = getJdbcTemplate().queryForList(sqlEventData, params);

            HashMap<String, List<EventData>> mapStudySubjectEvents = convertDbResultsToStudyEventHashMap(rowsStudySubjectEvents);

            // Assign events
            result.setStudyEventDataList(
                    mapStudySubjectEvents.get(result.getStudySubjectId())
            );
        }

        return result;
    }

    /**
     * Get specified StudySubject with StudyEvents with Forms in specified Study
     *
     * @param studyIdentifier identifier of study where subject is enrolled
     * @param studySubjectId  study subject ID of enrolled subject
     * @return StudySubject entity
     */
    public StudySubject getStudySubjectWithEventsWithForms(String studyIdentifier, String studySubjectId) {

        StudySubject result = getStudySubjectWithEvents(studyIdentifier, studySubjectId);

        // Subject with events exist
        if (result != null) {

            String inStatement = generateInStatement(result.getStudyEventDataList());

            // All form data agnostic from study metadata configuration
            String sqlFormData = "SELECT ss.label as studySubjectId,\n" +
                    "  ec.event_crf_id as crfId,\n" +
                    "  ec.interviewer_name as interviewerName,\n" +
                    "  ec.date_interviewed as dateInterviewed,\n" +
                    "  cv.oc_oid as crfVersionOid,\n" +
                    "  cv.name as crfVersion,\n" +
                    "  cv.description as crfVersionDescription,\n" +
                    "  c.crf_id as crfDefinitionId,\n" +
                    "  c.name as crfName,\n" +
                    "  c.oc_oid as crfOid,\n" +
                    "  ed.study_event_id as eventId,\n" +
                    "  sed.oc_oid as studyEventOid,\n" +
                    "  sed.ordinal as eventDefinitionOrdinal\n" +
                    "  FROM EVENT_CRF ec\n" +
                    "  LEFT JOIN CRF_VERSION cv on cv.crf_version_id = ec.crf_version_id\n" +
                    "  LEFT JOIN CRF c on c.crf_id = cv.crf_id\n" +
                    "  LEFT JOIN STUDY_EVENT ed on ed.study_event_id = ec.study_event_id\n" +
                    "  LEFT JOIN STUDY_EVENT_DEFINITION sed on sed.study_event_definition_id = ed.study_event_definition_id\n" +
                    "  LEFT JOIN STUDY_SUBJECT ss on ss.study_subject_id = ed.study_subject_id\n" +
                    "  LEFT JOIN STUDY s on s.study_id = ss.study_id\n" +
                    "  WHERE s.unique_identifier = ? and ss.label = ? and ec.study_event_id in " + inStatement + "\n" +
                    "  ORDER BY sed.ordinal, ed.sample_ordinal";

            // Check metadata configuration on study/site level - overrides
            // (e.g. form is hidden for site, form version not enabled for site)
            String sqlFormOverrides = "SELECT edc.parent_id, edc.study_event_definition_id as eventDefinitionId, edc.crf_id as crfId,\n" +
                    "  edc.default_version_id, edc.selected_version_ids, edc.hide_crf\n" +
                    "  FROM EVENT_DEFINITION_CRF edc" +
                    "  LEFT JOIN STUDY s on s.study_id = edc.study_id\n" +
                    "  WHERE s.unique_identifier = ? and edc.hide_crf = TRUE";

            Object[] params = new Object[2 + result.getStudyEventDataList().size()];
            params[0] = studyIdentifier;
            params[1] = studySubjectId;
            int i = 0;
            for (EventData ed : result.getStudyEventDataList()) {
                params[2 + i] = ed.getId();
                i++;
            }

            List<Map<String, Object>> rowsStudyEventsForms = getJdbcTemplate().queryForList(sqlFormData, params);
            List<Map<String, Object>> rowsFormOverrides = getJdbcTemplate().queryForList(sqlFormOverrides, studyIdentifier);

            HashMap<Integer, List<FormData>> mapStudyEventsForms = convertDbResultsToFormHashMap(rowsStudyEventsForms);
            HashMap<Integer, List<FormDefinition>> mapDefinitionsHiddenForms = convertDbResultsToFormDefinitionHashMap(rowsFormOverrides);

            // Assign forms to events (consider the metadata configuration)
            for (EventData ed : result.getStudyEventDataList()) {
                for (FormData fd : mapStudyEventsForms.get(ed.getId())) {

                    // Lookup based on event definition id
                    boolean isHidden = false;

                    List<FormDefinition> listHiddenForms = mapDefinitionsHiddenForms.get(ed.getEventDefinition().getId());
                    if (listHiddenForms != null) {
                        for (FormDefinition hiddenFormDefinition : listHiddenForms) {
                            if (fd.getFormDefinition().getId().equals(hiddenFormDefinition.getId())) {
                                isHidden = true;
                                break;
                            }
                        }
                    }

                    if (!isHidden) {
                        ed.addFormData(fd);
                    }
                }
            }
        }

        return result;
    }

    public StudySubject getStudySubjectWithEventsWithFormsWithItems(
            String studyIdentifier,
            String studySubjectId,
            List<CrfFieldAnnotation> annotatedItems) {

        StudySubject result = this.getStudySubjectWithEventsWithForms(studyIdentifier, studySubjectId);

        if (result != null && result.getStudyEventDataList() != null) {

            // Lookup data from in statements of SQL query
            List<Integer> formIdList = new ArrayList<>();
            List<String> itemOidList = new ArrayList<>();
            for (CrfFieldAnnotation annotation : annotatedItems) {

                boolean annotatedItemFound = false;

                for (EventData ed : result.getStudyEventDataList()) {
                    if (ed.getFormDataList() != null) {
                        for (FormData fd : ed.getFormDataList()) {
                            if (annotation.getEventDefinitionOid().equals(ed.getStudyEventOid()) &&
                                    annotation.getFormOid().equals(fd.getFormOid())) {
                                if (!formIdList.contains(fd.getId())) {
                                    formIdList.add(fd.getId());

                                    annotatedItemFound = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (annotatedItemFound) {
                        break;
                    }
                }

                if (annotatedItemFound) {
                    if (!itemOidList.contains(annotation.getCrfItemOid())) {
                        itemOidList.add(annotation.getCrfItemOid());
                    }
                }
            }

            // Generate parametrized IN statement
            String formInStatement = generateInStatement(formIdList);
            String itemInStatement = generateInStatement(itemOidList);

            String sqlItemData = "SELECT ss.label as studySubjectId,\n" +
                    "  id.item_data_id as itemDataId,\n" +
                    "  id.value as itemValue,\n" +
                    "  i.item_id as itemId,\n" +
                    "  i.oc_oid as itemOid,\n" +
                    "  ec.event_crf_id as crfId,\n" +
                    "  ec.interviewer_name as interviewerName,\n" +
                    "  ec.date_interviewed as dateInterviewed,\n" +
                    "  cv.oc_oid as crfVersionOid,\n" +
                    "  cv.name as crfVersion,\n" +
                    "  cv.description as crfVersionDescription,\n" +
                    "  c.crf_id as crfDefinitionId,\n" +
                    "  c.name as crfName,\n" +
                    "  c.oc_oid as crfOid,\n" +
                    "  ifm.left_item_text as leftItemText,\n" +
                    "  ifm.ordinal as itemOrdinal\n," +
                    "  igm.repeating_group as groupIsRepeating\n," +
                    "  ig.oc_oid as itemGroupOid\n" +
                    "  FROM ITEM_DATA id\n" +
                    "  LEFT JOIN ITEM i ON i.item_id = id.item_id\n" +
                    "  LEFT JOIN EVENT_CRF ec ON ec.event_crf_id = id.event_crf_id\n" +
                    "  LEFT JOIN CRF_VERSION cv on cv.crf_version_id = ec.crf_version_id\n" +
                    "  LEFT JOIN CRF c on c.crf_id = cv.crf_id\n" +
                    "  LEFT JOIN ITEM_FORM_METADATA ifm on ifm.item_id = i.item_id AND ifm.crf_version_id = cv.crf_version_id\n" +
                    "  LEFT JOIN ITEM_GROUP_METADATA igm on igm.item_id = i.item_id AND igm.crf_version_id = cv.crf_version_id\n" +
                    "  LEFT JOIN ITEM_GROUP ig on ig.item_group_id = igm.item_group_id\n" +
                    "  WHERE s.unique_identifier = ? and ss.label = ? and\n" +
                    "  ec.event_crf_id in " + formInStatement + " and\n" +
                    "  i.oc_oid in " + itemInStatement + "\n" +
                    "  ORDER BY cv.oc_oid, ig.oc_oid, ifm.ordinal";

            Object[] params = new Object[2 + formIdList.size() + itemOidList.size()];
            int i = 0;
            for (Integer id : formIdList) {
                params[2 + i] = id;
                i++;
            }
            for (String oid : itemOidList) {
                params[2 + i] = oid;
                i++;
            }

            List<Map<String, Object>> rowsFormsItemData = getJdbcTemplate().queryForList(sqlItemData, params);

            HashMap<Integer, List<ItemGroupData>> mapFormsItemGroupData = new HashMap<>();

            // Assign groups with data to forms
            for (EventData ed : result.getStudyEventDataList()) {
                for (FormData fd : ed.getFormDataList()) {
                    fd.setItemGroupDataList(mapFormsItemGroupData.get(fd.getId()));
                }
            }
        }

        return result;
    }

    /**
     * Sets the Pid on an existing StudySubject
     *
     * @param studySubject StudySubject that will be updated
     * @param ocUserName   String user_name in the user_account table
     * @return int changed items
     * @throws DataBaseItemNotFoundException if necessary data are not found in the database
     */
    public int setPidOnExistingStudySubject(StudySubject studySubject, String ocUserName) throws DataBaseItemNotFoundException {

        int userId = getUserId(ocUserName);
        int subject_id = getSubjectId(studySubject.getSubjectKey());

        String updateSubjectQuery = "UPDATE subject \n" +
                "SET unique_identifier = ?, update_id = ?, date_updated = now() \n" +
                "WHERE subject_id = ?";
        Object[] updateSubjectQueryParameter = {studySubject.getPid(), userId, subject_id};
        return getJdbcTemplate().update(updateSubjectQuery, updateSubjectQueryParameter);
    }

    /**
     * Sets the SecondaryId on an existing StudySubject
     *
     * @param studySubject StudySubject that will be updated
     * @param ocUserName   String user_name in the user_account table
     * @return int changed items
     * @throws DataBaseItemNotFoundException if necessary data are not found in the database
     */
    public int setSecondaryIdOnExistingStudySubject(StudySubject studySubject, String ocUserName) throws DataBaseItemNotFoundException {
        int userId = getUserId(ocUserName);

        String updateStudySubjectQuery = "UPDATE study_subject \n" +
                "SET secondary_label = ?, update_id = ?, date_updated = now() \n" +
                "WHERE oc_oid = ?";
        Object[] updateQueryParameter = {studySubject.getSecondaryId(), userId, studySubject.getSubjectKey()};
        return getJdbcTemplate().update(updateStudySubjectQuery, updateQueryParameter);
    }

    /**
     * Queries the user_id from the user_account table to set the foreign key on other database operations
     *
     * @param ocUserName String user_name in the user_account table
     * @return int user_id
     * @throws DataBaseItemNotFoundException if the user was not found within the database or the result is more than one item
     */
    private int getUserId(String ocUserName) throws DataBaseItemNotFoundException {
        String userIdQuerySql = "SELECT user_id \n" +
                "  FROM user_account\n" +
                "  WHERE user_name = ?";

        Object[] userIdQueryParams = {ocUserName};
        List<Map<String, Object>> userIdResultRows = getJdbcTemplate().queryForList(userIdQuerySql, userIdQueryParams);


        if (userIdResultRows.size() == 1) {
            return (int) userIdResultRows.get(0).get("user_id");
        } else {
            throw new DataBaseItemNotFoundException("User id not found for " + ocUserName);
        }
    }

    /**
     * Queries the getSubjectId from the study_subject table to set the foreign key on other database operations
     *
     * @param oc_oid String oc_oid / studySubjectKey of the StudySubject in study_subject table
     * @return int subject_id
     * @throws DataBaseItemNotFoundException if the subject was not found within the database or the result is more than one item
     */
    private int getSubjectId(String oc_oid) throws DataBaseItemNotFoundException {
        String userIdQuerySql = "SELECT subject_id \n" +
                "  FROM study_subject\n" +
                "  WHERE oc_oid = ?";

        Object[] userIdQueryParams = {oc_oid};
        List<Map<String, Object>> subjectIdResultRows = getJdbcTemplate().queryForList(userIdQuerySql, userIdQueryParams);


        if (subjectIdResultRows.size() == 1) {
            return (int) subjectIdResultRows.get(0).get("subject_id");
        } else {
            throw new DataBaseItemNotFoundException("StudySubject not found for " + oc_oid);
        }
    }

    /**
     * Find all StudySubjects for Subject pseudonym within the list of user available Studies
     *
     * @param pid           subject pseudonym (PersonID)
     * @param studyTypeList studies where user has access (filter)
     * @return list of StudySubject entities
     */
    // TODO: when no SOAP is used anymore we can switch from List<StudyType> to List<Study>
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
            studySubject.setEnrollmentDate(convertDbDateToString(sqlStartDate));

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

    /**
     * Find all StudySubjects in specified Study
     *
     * @param studyIdentifier identifier of study where subject is enrolled
     * @return list of StudySubject entities
     */
    public List<StudySubject> findStudySubjects(String studyIdentifier) {

        String sql = "SELECT ss.study_subject_id as id,\n" +
                "  ss.label as studySubjectId,\n" +
                "  ss.secondary_label as secondaryId,\n" +
                "  sub.unique_identifier as pid,\n" +
                "  ss.oc_oid as subjectKey,\n" +
                "  sub.gender as sex,\n" +
                "  sub.date_of_birth as dateOfBirth,\n" +
                "  ss.enrollment_date as enrollmentDate,\n" +
                "  st.name as studySubjectStatus\n" +
                "  FROM STUDY_SUBJECT ss\n" +
                "  LEFT JOIN STATUS st on st.status_id = ss.status_id\n" +
                "  LEFT JOIN SUBJECT sub on sub.subject_id = ss.subject_id\n" +
                "  LEFT JOIN STUDY s on s.study_id = ss.study_id\n" +
                "  WHERE s.unique_identifier = ?\n" +
                "  ORDER BY ss.label";

        Object[] params = {studyIdentifier};
        List<Map<String, Object>> rowsStudySubjects = getJdbcTemplate().queryForList(sql, params);
        List<StudySubject> results = convertDbResultsToStudySubjectList(rowsStudySubjects);

        return results;
    }

    /**
     * Find all StudySubjects with StudyEvents in specified Study
     *
     * @param studyIdentifier identifier of study where subject is enrolled
     * @return list of StudySubject entities
     */
    public List<StudySubject> findStudySubjectsWithEvents(String studyIdentifier) {

        List<StudySubject> results = this.findStudySubjects(studyIdentifier);

        String sqlEventData = "SELECT ss.label as studySubjectId,\n" +
                "  ed.study_event_id as eventId,\n" +
                "  sed.oc_oid as studyEventOid,\n" +
                "  sed.study_event_definition_id as eventDefinitionId,\n" +
                "  sed.name as eventName,\n" +
                "  sed.description as eventDescription,\n" +
                "  sed.type as eventType,\n" +
                "  sed.category as eventCategory,\n" +
                "  sed.repeating as isRepeating,\n" +
                "  sed.ordinal as eventDefinitionOrdinal,\n" +
                "  ed.sample_ordinal as studyEventRepeatKey,\n" +
                "  ed.date_start as startDate,\n" +
                "  ed.date_end as endDate,\n" +
                "  st.name as eventSystemStatus,\n" +
                "  ses.name as eventStatus\n" +
                "  FROM STUDY_EVENT ed\n" +
                "  LEFT JOIN STUDY_EVENT_DEFINITION sed on sed.study_event_definition_id = ed.study_event_definition_id\n" +
                "  LEFT JOIN STATUS st on st.status_id = ed.status_id\n" +
                "  LEFT JOIN SUBJECT_EVENT_STATUS ses on ses.subject_event_status_id = ed.subject_event_status_id\n" +
                "  LEFT JOIN STUDY_SUBJECT ss on ss.study_subject_id = ed.study_subject_id\n" +
                "  LEFT JOIN STUDY s on s.study_id = ss.study_id\n" +
                "  WHERE s.unique_identifier = ?\n" +
                "  ORDER BY ss.label, sed.ordinal, ed.sample_ordinal";

        Object[] params = {studyIdentifier};
        List<Map<String, Object>> rowsStudySubjectEvents = getJdbcTemplate().queryForList(sqlEventData, params);

        HashMap<String, List<EventData>> mapStudySubjectEvents = convertDbResultsToStudyEventHashMap(rowsStudySubjectEvents);

        // Assign events
        for (StudySubject studySubject : results) {
            studySubject.setStudyEventDataList(
                    mapStudySubjectEvents.get(studySubject.getStudySubjectId())
            );
        }

        return results;
    }

    //endregion

    //region EventData

    /**
     * Get list of StudySubject EventData occurrences for specified EventDefinition
     *
     * @param studySubject    specified StudySubject - OC OID
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

        Object[] params = {studySubject.getSubjectKey(), eventDefinition.getOid()};
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, params);

        for (Map<String, Object> row : rows) {
            EventData event = new EventData();

            event.setId((Integer) row.get("id"));
            event.setStudyEventOid(eventDefinition.getOid());
            event.setEventName((String) row.get("eventName"));
            Timestamp timestamp = (Timestamp) row.get("dateStart");
            event.setStartDate(convertTimestampToString(timestamp));
            int repeatKey = (Integer) row.get("repeatKey");
            event.setStudyEventRepeatKey(String.valueOf(repeatKey));

            results.add(event);
        }

        return results;
    }

    /**
     * Update OC study event repeat key
     *
     * @param eventData     study event data occurrence - OC ID
     * @param sampleOrdinal new repeat key
     * @return true if rows updated
     */
    public boolean changeStudyEventRepeatKey(EventData eventData, int sampleOrdinal) {

        String sql = "UPDATE STUDY_EVENT\n" +
                "SET sample_ordinal = ?\n" +
                "WHERE study_event_id = ?";

        Object[] params = {sampleOrdinal, eventData.getId()};
        int rowsUpdated = getJdbcTemplate().update(sql, params);

        return rowsUpdated > 0;
    }

    //endregion

    //region ItemData

    // Used by web service that python upload client is using - for compatibility purpose
    public ItemData findItemData(String studyOid,
                                 String subjectPid,
                                 String studyEventOid,
                                 String studyEventRepeatKey,
                                 String formOid,
                                 String itemOid) {

        ItemData result = null;

        String sql = "SELECT s.study_id as StudyId\n" +
                "FROM Study s\n" +
                "WHERE s.oc_oid = ?";

        int studyId = getJdbcTemplate().queryForObject(sql, new Object[]{studyOid}, Integer.class);

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
                "WHERE ss.study_id = ? and\n" +
                "s.unique_identifier = ? and\n" +
                "sed.oc_oid = ? and\n" +
                "se.sample_ordinal = ? and\n" +
                "cv.oc_oid = ? and\n" +
                "i.oc_oid = ?\n" +
                "ORDER BY\n" +
                "  ss.study_subject_id\n" +
                ", sed.ordinal\n" +
                ", se.sample_ordinal\n" +
                ", edc.ordinal\n" +
                ", id.ordinal\n" +
                ", ifm.ordinal";

        int seRepeatKey = Integer.parseInt(studyEventRepeatKey);
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, studyId, subjectPid, studyEventOid, seRepeatKey, formOid, itemOid);

        if (rows != null) {
            for (Map<String, Object> row : rows) {
                result = new ItemData();
                result.setItemOid(itemOid);
                result.setValue((String) row.get("ItemValue"));

                break;
            }
        }

        return result;
    }

    /**
     * Query all data item in a study
     *
     * @param uniqueIdentifier study
     * @return list of item definition with values
     */
    // TODO: can be re-factored or removed, is not used anywhere
    public List<ItemDefinition> findAllItemDataForStudy(String uniqueIdentifier) {

        String sql = "SELECT s.study_id as StudyId\n" +
                "FROM Study s\n" +
                "WHERE s.unique_identifier = ?";

        int studyId = getJdbcTemplate().queryForObject(sql, new Object[]{uniqueIdentifier}, Integer.class);

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
     *
     * @param uniqueIdentifier study
     * @param query            list of query items
     * @return list of queried item definitions
     */
    // TODO: can be re-factored, DataQueryResult is not used anywhere (can be removed)
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
                "INNER JOIN CRF_VERSION cv\n" + // Associate EventCRFs with CRF versions
                "ON ec.crf_version_id = cv.crf_version_id\n" +
                "INNER JOIN CRF c\n" + // Associate CRF versions with CRF definition
                "ON cv.crf_id = c.crf_id\n" +
                "INNER JOIN EVENT_DEFINITION_CRF edc\n" + // Associate CRF versions with EventDefinitionCRFs
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
        List<DataQueryResult> queryResults = new ArrayList<>();
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

    public static List<Study> convertDbResultsToStudyList(List<Map<String, Object>> rows) {
        List<Study> studies = new ArrayList<>();

        if (rows != null) {
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
        }

        return studies;
    }

    public static List<StudySubject> convertDbResultsToStudySubjectList(List<Map<String, Object>> rows) {
        List<StudySubject> results = new ArrayList<>();

        if (rows != null) {
            for (Map<String, Object> row : rows) {

                StudySubject ss = new StudySubject();

                ss.setId((Integer) row.get("id"));
                ss.setStudySubjectId((String) row.get("studySubjectId"));
                ss.setSecondaryId((String) row.get("secondaryId"));
                ss.setPid((String) row.get("pid"));
                ss.setSubjectKey((String) row.get("subjectKey"));
                ss.setSex((String) row.get("sex"));
                java.sql.Date sqlDob = (java.sql.Date) row.get("dateOfBirth");
                ss.setDateOfBirth(convertDbDateToString(sqlDob));
                java.sql.Date sqlEnrollment = (java.sql.Date) row.get("enrollmentDate");
                ss.setEnrollmentDate(convertDbDateToString(sqlEnrollment));
                ss.setStatus((String) row.get("studySubjectStatus"));

                Person person = new Person();
                person.setPid(ss.getPid());

                ss.setPerson(person);

                results.add(ss);
            }
        }

        return results;
    }

    public static HashMap<String, List<EventData>> convertDbResultsToStudyEventHashMap(List<Map<String, Object>> rows) {

        HashMap<String, List<EventData>> results = new HashMap<>();

        String label = "";
        List<EventData> subjectEventDataList = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {

                String currentLabel = (String) row.get("studySubjectId");

                if (!currentLabel.equals(label) && !label.isEmpty()) {
                    results.put(label, subjectEventDataList);
                    subjectEventDataList = new ArrayList<>();
                }

                EventData eventData = new EventData();
                eventData.setId((Integer) row.get("eventId"));
                String eventOid = (String) row.get("studyEventOid");
                eventData.setStudyEventOid(eventOid);
                String eventName = (String) row.get("eventName");
                eventData.setEventName(eventName);
                Timestamp startDateTimestamp = (Timestamp) row.get("startDate");
                eventData.setStartDate(convertTimestampToString(startDateTimestamp));
                Timestamp endDateTimestamp = (Timestamp) row.get("endDate");
                eventData.setEndDate(convertTimestampToString(endDateTimestamp));
                int repeatKey = (Integer) row.get("studyEventRepeatKey");
                eventData.setStudyEventRepeatKey(String.valueOf(repeatKey));
                eventData.setSystemStatus((String) row.get("eventSystemStatus"));
                eventData.setStatus((String) row.get("eventStatus"));

                EventDefinition eventDefinition = new EventDefinition();
                eventDefinition.setId((Integer) row.get("eventDefinitionId"));
                eventDefinition.setOid(eventOid);
                eventDefinition.setName(eventName);
                eventDefinition.setDescription((String) row.get("eventDescription"));
                eventDefinition.setType((String) row.get("eventType"));
                eventDefinition.setCategory((String) row.get("eventCategory"));
                eventDefinition.setIsRepeating((Boolean) row.get("isRepeating"));
                eventDefinition.setOrdinal((Integer) row.get("eventDefinitionOrdinal"));

                eventData.setEventDefinition(eventDefinition);

                subjectEventDataList.add(eventData);

                label = currentLabel;
            }

            // The last one
            if (!label.isEmpty()) {
                results.put(label, subjectEventDataList);
            }
        }

        return results;
    }

    public static HashMap<Integer, List<FormData>> convertDbResultsToFormHashMap(List<Map<String, Object>> rows) {

        HashMap<Integer, List<FormData>> results = new HashMap<>();

        int index = -1;
        List<FormData> eventFormDataList = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {

                int currentIndex = (Integer) row.get("eventId");

                if (currentIndex != index && index != -1) {
                    results.put(index, eventFormDataList);
                    eventFormDataList = new ArrayList<>();
                }

                FormData formData = new FormData();
                formData.setId((Integer) row.get("crfId"));
                String crfVersionOid = (String) row.get("crfVersionOid");
                formData.setFormOid(crfVersionOid);
                String crfName = (String) row.get("crfName");
                formData.setFormName(crfName);
                String crfVersion = (String) row.get("crfVersion");
                formData.setVersion(crfVersion);
                formData.setVersionDescription((String) row.get("crfVersionDescription"));
                formData.setInterviewerName((String) row.get("interviewerName"));
                java.sql.Date sqlInterviewedDate = (java.sql.Date) row.get("dateInterviewed");
                formData.setInterviewDate(convertDbDateToString(sqlInterviewedDate));

                // TODO: how to figure out how is status determined
                // OpenClinica:Status="data entry complete"

                FormDefinition formDefinition = new FormDefinition();
                formDefinition.setId((Integer) row.get("crfDefinitionId"));
                formDefinition.setOid(crfVersionOid);
                formDefinition.setName(crfName + " - " + crfVersion);
                formDefinition.setIsRepeating(Boolean.FALSE);

                FormDetails formDetails = new FormDetails();
                formDetails.setFormOid(crfVersionOid);
                formDetails.setParentFormOid((String) row.get("crfOid"));

                // TODO: settings for CRF used in events - should be list
                //PresentInEventDefinition presentInEventDefinition = new PresentInEventDefinition();

                formDefinition.setFormDetails(formDetails);

                formData.setFormDefinition(formDefinition);

                eventFormDataList.add(formData);

                index = currentIndex;
            }
        }

        // The last one
        if (index != -1) {
            results.put(index, eventFormDataList);
        }

        return results;
    }

    public static HashMap<Integer, List<FormDefinition>> convertDbResultsToFormDefinitionHashMap(List<Map<String, Object>> rows) {

        HashMap<Integer, List<FormDefinition>> results = new HashMap<>();

        int index = -1;
        List<FormDefinition> definitionHiddenFormList = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {

                int currentIndex = (Integer) row.get("eventDefinitionId");

                if (currentIndex != index && index != -1) {
                    results.put(index, definitionHiddenFormList);
                    definitionHiddenFormList = new ArrayList<>();
                }

                FormDefinition form = new FormDefinition();
                form.setId((Integer) row.get("crfId"));

                definitionHiddenFormList.add(form);

                index = currentIndex;
            }
        }

        // The last one
        if (index != -1) {
            results.put(index, definitionHiddenFormList);
        }

        return results;
    }

    public static HashMap<Integer, List<ItemGroupData>> convertDbResultsToItemsHashMap(List<Map<String, Object>> rows) {

        HashMap<Integer, List<ItemGroupData>> results = new HashMap<>();

        int index = -1;
        List<ItemGroupData> formItemGroupDataList = new ArrayList<>();
        List<ItemData> groupItemDataList = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {

            }
        }

        return results;
    }


    public static String convertTimestampToString(Timestamp timestamp) {
        String result = "";
        if (timestamp != null) {
            Date date = new Date(timestamp.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.OC_TIMESTAMPFORMAT);
            result = sdf.format(date);
        }
        return result;
    }

    public static String convertDbDateToString(java.sql.Date dbDate) {
        String result = "";
        if (dbDate != null) {
            Date date = new Date(dbDate.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.OC_DATEFORMAT);
            result = sdf.format(date);
        }
        return result;
    }


    public static DataQueryResult getByStudySubject(List<DataQueryResult> list, StudySubject studySubject) {
        for (DataQueryResult queryResult : list) {
            if (queryResult.getStudySubject().getId() != null && queryResult.getStudySubject().getId().equals(studySubject.getId())) {
                return queryResult;
            }
        }
        return null;
    }


    public static <T> String generateInStatement(List<T> objectList) {
        StringBuilder inStatement = new StringBuilder();

        if (objectList != null) {
            inStatement.append("(");
            int i = 0;
            for (T o : objectList) {
                if (i < objectList.size() - 1) {
                    inStatement.append("?,");
                } else {
                    inStatement.append("?)");
                }
                i++;
            }
        }

        return inStatement.toString();
    }

    //endregion

    //endregion

}