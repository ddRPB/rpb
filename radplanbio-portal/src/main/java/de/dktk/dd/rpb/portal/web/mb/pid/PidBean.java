/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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

package de.dktk.dd.rpb.portal.web.mb.pid;

import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.repository.ctms.IPersonRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.json.JSONObject;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ViewModel bean for browsing patient identity database (patient centric module)
 *
 * Each user is assigned to one partner site.
 * Each site has just one patient identity database. According to the user partner site
 * the appropriated connection to specific PID generator is established.
 *
 * This is not a conventional CRUD because we are dealing here with entities from external system (Mainzelliste)
 *
 *
 * @author tomas@skripcak.net
 * @since 21 August 2013
 */
@Named("mbPid")
@Scope(value="view")
public class PidBean extends CrudEntityViewModel<Person, Integer> {

    //region Injects

    //region Main bean

    @Inject
    private MainBean mainBean;

    /**
     * Set MainBean
     *
     * @param bean MainBean
     */
    @SuppressWarnings("unused")
    public void setMainBean(MainBean bean) {
        this.mainBean = bean;
    }

    //endregion

    //region Repository

    @Inject
    private IPersonRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @SuppressWarnings("unused")
    @Override
    public IPersonRepository getRepository() {
        return this.repository;
    }

    /**
     * Set StructTypeRepository
     * @param repository StructTypeRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IPersonRepository repository) {
        this.repository = repository;
    }

    //endregion

    //region Study integration facade

    @Inject
    private StudyIntegrationFacade studyIntegrationFacade;

    @SuppressWarnings("unused")
    public void setStudyIntegrationFacade(StudyIntegrationFacade value) {
        this.studyIntegrationFacade = value;
    }

    //endregion

    //region AuditService

    @Inject
    private AuditLogService auditLogService;

    @SuppressWarnings("unused")
    public void setAuditLogService(AuditLogService svc) {
        this.auditLogService = svc;
    }

    //endregion

    //endregion

    //region Members

    private Boolean idatSearchMode;
    private Person newSearchPerson;
    private Boolean isSure;

    //endregion

    //region Properties

    //region IDAT searchMode

    public Boolean getIdatSearchMode() {
        return this.idatSearchMode;
    }

    public void setIdatSearchMode(Boolean value) {
        this.idatSearchMode = value;
    }

    //endregion

    //region IsSure

    public Boolean getIsSure() {
        return this.isSure;
    }

    public void setIsSure(Boolean value) {
        this.isSure = value;
    }

    //endregion

    //region NewPerson search

    public Person getNewSearchPerson() {
        return this.newSearchPerson;
    }

    public void setNewSearchPerson(Person person) {
        this.newSearchPerson = person;
    }

    //endregion

    //endregion

    //region Init

    /**
     * Initialisation of the bean
     */
    @PostConstruct
    public void init() {

        // Normally I am sure that I am providing new patient data
        this.isSure = Boolean.TRUE;
        // By default I search according to IDAT
        this.idatSearchMode = Boolean.TRUE;

        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        // Initialise patient search object
        this.newSearchPerson = new Person();
    }

    //endregion

    //region Commands

    /**
     * Create a list of matching persons
     */
    public void searchPatient() {
        List<Person> matchingPersons = new ArrayList<Person>();

        try {
            // Load what is necessary depending on search mode
            if (this.idatSearchMode) {
                this.depseudonymiseAllPids();
            }
            else {
                this.loadAllPids();
            }

            for (Person p : this.entityList) {
                // Search according IDAT
                if (this.idatSearchMode) {
                    if ((p.getFirstname().toUpperCase().equals(this.newSearchPerson.getFirstname().toUpperCase())) &&
                            (p.getSurname().toUpperCase().equals(this.newSearchPerson.getSurname().toUpperCase()))) {

                        Date pDate = p.getBirthdate();
                        Date nDate = this.newSearchPerson.getBirthdate();

                        Calendar pCal = Calendar.getInstance();
                        pCal.setTime(pDate);

                        Calendar nCal = Calendar.getInstance();
                        nCal.setTime(nDate);

                        if (pCal.get(Calendar.YEAR) == nCal.get(Calendar.YEAR) &&
                                pCal.get(Calendar.MONTH) == nCal.get(Calendar.MONTH) &&
                                pCal.get(Calendar.DAY_OF_MONTH) == nCal.get(Calendar.DAY_OF_MONTH)) {

                            matchingPersons.add(p);
                        }
                    }
                }
                // Search according PID
                else {
                    if (p.getPid().toUpperCase().equals(this.newSearchPerson.getPid().toUpperCase()) ||
                            p.getPid().toUpperCase().equals(this.newSearchPerson.getPid().toUpperCase().replace(this.mainBean.getMyAccount().getPartnerSite().getIdentifier() + "-", ""))) {
                        matchingPersons.add(p);
                        break;
                    }
                }
            }

            this.entityList = matchingPersons;
            this.selectedEntity = null;
            this.auditLogService.event(AuditEvent.PIDSearch, this.newSearchPerson.toString());

            if (this.entityList.size() > 0) {
                this.messageUtil.infoText("Patients matching the search criteria found: " + this.entityList.size());
            }
            else {
                this.messageUtil.infoText("No patient matching the search criteria.");
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }

        // Reset search object
        this.newSearchPerson = new Person();
    }

    /**
     * Load all patient pseudonyms from PID server
     */
    public void loadAllPids() {
        try {
            this.entityList = this.mainBean.getSvcPid().getAllPatients();
            this.auditLogService.event(AuditEvent.PIDLoad);
            this.messageUtil.info("status_loaded_ok");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Re-identify all pseudonyms in order to show clear IDAT
     */
    public void depseudonymiseAllPids() {
        try {
            List<String> pids = new ArrayList<String>();
            if (this.entityList == null || this.entityList.size() == 0) {
                this.entityList = this.mainBean.getSvcPid().getAllPatients();
            }
            for (Person p : this.entityList) {
                pids.add(p.getPid());
            }

            this.entityList = this.getPersonalInformationList(pids);
            this.auditLogService.event(AuditEvent.PIDDepseudonymisation, "All PIDs");

            this.messageUtil.infoText("Patient idenity data for " + entityList.size() + " pseudonyms loaded.");
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Depseudonymise selected PID
     */
    public void depseudonymisePid() {
        try {
            JSONObject finalResult = this.mainBean.getSvcPid().newSession();

            String sessionId = "";
            if (finalResult != null) {
                sessionId = finalResult.getString("sessionId");
            }

            String pid = this.selectedEntity.getPid();
            finalResult = this.mainBean.getSvcPid().readPatientToken(
                    sessionId,
                    pid
            );

            String tokenId = "";
            if (finalResult != null) {
                tokenId = finalResult.getString("id");
            }

            this.selectedEntity = this.mainBean.getSvcPid().getPatient(tokenId);
            this.selectedEntity.setPid(pid);

            this.auditLogService.event(AuditEvent.PIDDepseudonymisation, pid);

            this.messageUtil.infoText("Patient idenity data for " + this.selectedEntity.getPid() + " loaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Load study subjects (EDC) and DICOM studies (PACS) for selected patient entity
     */
    public void loadPatientDetails() {
        this.loadPatientStudySubjects();
        this.loadPatientDicomStudies();
    }

    /**
     * Load study subjects (EDC) for selected patient entity
     */
    public void loadPatientStudySubjects() {
        try {
            this.studyIntegrationFacade.init(this.mainBean);

            // Load StudySubject
            this.setSelectedEntity(
                    this.studyIntegrationFacade.fetchPatientStudySubjects(
                            this.selectedEntity
                    )
            );

            this.messageUtil.infoText("Study Subjects for " + this.selectedEntity.getPid() + " loaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Load DICOM studies (PACS) for selected patient entity
     */
    public void loadPatientDicomStudies() {
        try {
            this.studyIntegrationFacade.init(this.mainBean);

            //Load DICOM studies
            this.setSelectedEntity(
                    this.studyIntegrationFacade.fetchPatientDicomStudies(
                            this.selectedEntity
                    )
            );

            this.messageUtil.infoText("DICOM data for " + this.selectedEntity.getPid() + " loaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = new Person();
    }

    /**
     * Generate register new Patient with PID
     */
    @Override
    public void doCreateEntity() {
        try {
            JSONObject finalResult = this.mainBean.getSvcPid().newSession();

            String sessionId = "";
            if (finalResult != null) {
                sessionId = finalResult.getString("sessionId");
            }

            finalResult = this.mainBean.getSvcPid().newPatientToken(sessionId);

            String tokenId = "";
            if (finalResult != null) {
                tokenId = finalResult.getString("tokenId");
            }

            if (this.newEntity.getIsSure()) {
                finalResult = this.mainBean.getSvcPid().createSurePatienJson(tokenId, this.newEntity);
            } else {
                finalResult = this.mainBean.getSvcPid().createPatientJson(tokenId, this.newEntity);
            }

            String newId;
            Boolean tentative;

            if (finalResult != null) {
                newId = finalResult.optString("newId");
                if (!newId.isEmpty()) {
                    tentative = finalResult.getBoolean("tentative");

                    this.auditLogService.event(AuditEvent.PIDCreation, newId);

                    this.messageUtil.infoText("PID generated value: " + newId + " tentative: " + tentative);
                }

                this.newEntity.setPid(newId);
            }

            // Delete session if it exists (however session cleanup can be also automatically done by Mainzelliste)
            this.mainBean.getSvcPid().deleteSession(sessionId);
        }
        catch (Exception err) {
            this.messageUtil.error(err);

            // Unsure patient = possible match with already registered patient
            if (err.getMessage().contains("Failed : HTTP error code: 409")) {
                this.newEntity.setIsSure(Boolean.FALSE);
                this.setIsSure(Boolean.FALSE);
            }
        }
    }

    /**
     * Update patient details in patient identity management system
     */
    @Override
    public void doUpdateEntity() {
        try {
            JSONObject finalResult = this.mainBean.getSvcPid().newSession();

            String sessionId = "";
            if (finalResult != null) {
                sessionId = finalResult.getString("sessionId");
            }

            finalResult = this.mainBean.getSvcPid().editPatientToken(
                    sessionId,
                    this.selectedEntity.getPid()
            );

            String tokenId = "";
            if (finalResult != null) {
                tokenId = finalResult.getString("id");
            }

            Boolean editResult = this.mainBean.getSvcPid().editPatientJson(tokenId, this.selectedEntity);

            if (editResult) {
                this.auditLogService.event(AuditEvent.PIDModification, this.selectedEntity.getPid());
                this.messageUtil.infoEntity("status_saved_ok", this.selectedEntity);
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colPersonPid", "colPersonPid", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<SortMeta>();
    }

    /**
     * Need to build visibility list
     * @return boolean list of columns visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {

        List<Boolean> result = new ArrayList<Boolean>();

        result.add(Boolean.TRUE); // PID
        result.add(Boolean.TRUE); // FirstName
        result.add(Boolean.TRUE); // Surname
        result.add(Boolean.TRUE); // DateOfBrith

        result.add(Boolean.FALSE); // BirthName
        result.add(Boolean.FALSE); // City of residence
        result.add(Boolean.FALSE); // ZIP

        return result;

    }

    //endregion

    //region Private Methods

    /**
     * Get list of Person (with IDAT) entities with given list of PIDs
     * @param pids list of pseudonyms
     * @return list of Person entities
     */
    private List<Person> getPersonalInformationList(List<String> pids) {
        List<Person> p = null;

        try {
            JSONObject finalResult = this.mainBean.getSvcPid().newSession();

            String sessionId = "";
            if (finalResult != null) {
                sessionId = finalResult.getString("sessionId");
            }

            finalResult = this.mainBean.getSvcPid().readPatientsToken(sessionId, pids);

            String tokenId = "";
            if (finalResult != null) {
                tokenId = finalResult.getString("id");
            }

            p = this.mainBean.getSvcPid().getPatientList(tokenId);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }

        return p;
    }

    //endregion

}
