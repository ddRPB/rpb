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

package de.dktk.dd.rpb.core.service;

import java.util.List;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;

import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.UserAccount;
import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.ocsoap.types.ScheduledEvent;
import de.dktk.dd.rpb.core.ocsoap.types.StudySubject;

import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.openclinica.ws.beans.StudyType;

import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.ocsoap.types.Event;

/**
 * OpenClinica service helps to access SOAP and REST based web-services provided by EDC system
 *
 * @author tomas@skripcak.net
 * @since 05 Jun 2013
 */
public interface IOpenClinicaService {

    //region Properties

    Boolean isConnected();

    Boolean getCacheIsEnabled();

    void setCacheIsEnabled(Boolean value);

    //endregion

    //region Setup

    /**
     * Setup connection information for using OpenClinica web service
     * @param username - OC username
     * @param password - OC user password, will be changed to SHA1 hash before trying to connect
     * @param wsBaseUrl - the url where the instance of OpenClinica SOAP web service are living
     * @param restBaseUrl - the url address for REST access
     */
    void connect(String username, String password, String wsBaseUrl, String restBaseUrl);

    /**
     * Setup connection information for using OpenClinica web service
     * @param username - OC username
     * @param passwordHash - OC user SHA1 password hash
     * @param wsBaseUrl - the url where the instance of OpenClinica SOAP web service are living
     * @param restBaseUrl - the url address for REST access
     */
    void connectWithHash(String username, String passwordHash, String wsBaseUrl, String restBaseUrl);

    //endregion

    //region SOAP

    /**
     * Get metadata for specified study
     * @param study - Study entity
     * @return StudyODM metadata
     */
    MetadataODM getStudyMetadata(Study study);

    /**
     * Get metadata for specified study
     * @param identifier - study identifier
     * @return StudyODM metadata
     */
    MetadataODM getStudyMetadata(String identifier);

    /**
     * Get list of all studies available for user form OpenClinica EDC
     *
     * @return List - StudyType list
     */
    List<StudyType> listAllStudies();

    /**
     * Get list of all OC study subjects in a specified study
     *
     * @param study - Study entity
     * @return List - StudySubjectWithEventsType list
     */
    List<StudySubjectWithEventsType> listAllStudySubjectsByStudy(Study study) throws OCConnectorException;

    /**
     * Get list of all OC study event definitions in a specified study
     *
     * @param study - Study entity
     * @return List - Event list
     */
    List<Event> listAllEventDefinitionsByStudy(Study study);

    void scheduleStudyEvent(StudySubject subject, ScheduledEvent event) throws OCConnectorException;

    /**
     * Create a new study subject in a study
     *
     * @param studySubject - Study subject entity
     * @return successful
     */
    Boolean createNewStudySubject(StudySubject studySubject);

    Boolean studySubjectExistsInStudy(StudySubject studySubject, Study study);

    List<StudySubject> getSubjectsByStudy(de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy) throws OCConnectorException;

    void importData(String odmXmlData) throws OCConnectorException;

    //endregion

    //region REST

    //region EDC User Account

    UserAccount loginUserAccount(String username, String password);

    UserAccount createUserAccount();

    UserAccount loadUserAccount();

    UserAccount loadParticipantUserAccount();

    UserAccount loadParticipantUserAccountByAccessCode();

    //endregion

    //region ODM

    /**
     * Load ODM entity as resource based on provided query string
     * @param format
     * @param method
     * @param queryOdmXmlPath
     * @return ODM entity
     */
    Odm getStudyCasebookOdm(OpenClinicaService.CasebookFormat format, OpenClinicaService.CasebookMethod method, String queryOdmXmlPath);

    //endregion

    //region Study

    //endregion

    //region Site

    //endregion

    //region Study Subject

    List<StudySubject> getStudyCasebookSubjects(String studyOid);

    StudySubject getStudyCasebookSubject(String studyOid, String studySubjectIdentifier);

    List<de.dktk.dd.rpb.core.domain.edc.StudySubject> getStudyCasebookSubjects(OpenClinicaService.CasebookFormat format, OpenClinicaService.CasebookMethod method, String queryOdmXmlPath);

    //endregion

    //region Study Event

    List<EventDefinition> getStudyCasebookEvents(String studyOid, String subjectOid);

    //endregion

    //region ePRO

    List<EventData> loadParticipateEvents(String studyOid, String studySubjectOid);

    String loadEditableUrl(String ocUrl, String returnUrl);

    boolean completeParticipantEvent(String studySubjectIdentifier, String studyEventDefinitionOid, String eventRepeatKey, String apiKey);

    //endregion

    //endregion

}