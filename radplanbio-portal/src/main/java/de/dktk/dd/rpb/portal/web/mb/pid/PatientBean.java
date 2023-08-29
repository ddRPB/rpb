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

package de.dktk.dd.rpb.portal.web.mb.pid;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.bio.AbstractSpecimen;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.repository.ctms.IPersonRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.PatientIdentifierUtil;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.json.JSONObject;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
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
 * @author tomas@skripcak.net
 * @since 21 August 2013
 */
@Named("mbPatient")
@Scope(value="view")
public class PatientBean extends CrudEntityViewModel<Person, Integer> {

    //region Injects

    private MainBean mainBean;
    private StudyIntegrationFacade studyIntegrationFacade;
    private AuditLogService auditLogService;

    //region Repository - Dummy

    private IPersonRepository repository;
    /**
     * Get StudyRepository
     * @return StudyRepository
     */
    @Override
    public IPersonRepository getRepository() {
        return this.repository;
    }
    
    //endregion

    //endregion

    //region Members

    private Boolean idatSearchMode;
    private Person newSearchPerson;
    private Boolean isSure;
    private TreeNode specimenRoot;

    //endregion

    //region Constructors

    @Inject
    public PatientBean(MainBean mainBean, StudyIntegrationFacade studyIntegrationFacade, IPersonRepository repository, AuditLogService auditLogService) {

        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.repository = repository;
        this.auditLogService = auditLogService;
    }

    //endregion

    //region Properties

    //region IDAT SearchMode

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

    //region Specimen Root

    public TreeNode getSpecimenRoot() {
        return specimenRoot;
    }

    public void setSpecimenRoot(TreeNode specimenRoot) {
        this.specimenRoot = specimenRoot;
    }

    //endregion

    //endregion

    //region Init

    /**
     * Initialisation of the bean
     */
    @PostConstruct
    public void init() {
        // Search according to IDAT
        this.idatSearchMode = Boolean.TRUE;

        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );

        // Initialise patient search object
        this.newSearchPerson = new Person();

        // Initialise RPB study facade
        this.studyIntegrationFacade.init(this.mainBean);
    }

    /**
     * After initialisation, in case of pid query parameter present initialise the search
     */
    public void onLoad() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            // If it is possible to search
            if (this.newSearchPerson != null && this.newSearchPerson.getPid() != null && UserContext.hasRole("ROLE_PID_SEARCH")) {
                if (!"".equals(this.newSearchPerson.getPid())) {
                    this.idatSearchMode = false;
                    this.searchPatient();
                }
            }
        }

        // Postponed until vendor neutral interfaces are available for Biobank
        //this.loadSpecimensTree();
    }

    //endregion

    //region Commands

    //region Patient

    /**
     * Create a list of matching persons
     */
    public void searchPatient() {
        List<Person> matchingPersons = new ArrayList<>();

        try {
            // IDAT search requires re-identification of all patients (for now)
            if (this.idatSearchMode) {
                this.depseudonymiseAllPids(false);
                for (Person p : this.entityList) {
                    if (p.patientIdatEquals(this.newSearchPerson)) {
                        matchingPersons.add(p);
                    }
                }
            }
            // PID search requires re-identification of one patient pseudonym
            else {
                String pid = PatientIdentifierUtil.removePatientIdPrefix(this.newSearchPerson.getPid());
                Person person = this.reidentifyPseudonymToPerson(pid);
                if (person != null) {
                    person.clearIdentity();
                    matchingPersons.add(person);
                }
            }
            this.selectedEntity = null;
            this.entityList = matchingPersons;

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
            this.entityList = this.mainBean.getSvcPid().getAllPatientPIDs();

            this.auditLogService.event(AuditEvent.PIDLoad);
            this.messageUtil.info("status_loaded_ok");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //TODO: add parameter to force the reload of cache for admin call (re-identify all)
    /**
     * Re-identify all pseudonyms in order to show clear IDAT
     */
    public void depseudonymiseAllPids(boolean forceRefreshPatientsIdatCache) {
        try {
            List<Person> pseudonymisedPatients = this.mainBean.getSvcPid().getAllPatientPIDs();
            this.entityList = this.mainBean.getSvcPid().getPatientListByPIDs(pseudonymisedPatients, forceRefreshPatientsIdatCache);

            this.auditLogService.event(AuditEvent.PIDDepseudonymisation, "All PIDs");
            this.messageUtil.infoText("Patient identity data for " + entityList.size() + " pseudonyms loaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Re-identify provided pseudonym
     */
    public void depseudonymiseSelectedPid(String pid) {
        this.selectedEntity = this.reidentifyPseudonymToPerson(pid);
        if (this.selectedEntity != null) {
            this.messageUtil.infoText("Patient identity data for " + this.selectedEntity.getPid() + " loaded.");
        }
    }

    /**
     * Load study subjects (EDC) and DICOM studies (PACS) for selected patient entity
     */
    public void loadPatientDetails() {

        this.loadPatientStudySubjects();
        this.loadPatientDicomStudies();

        // Postponed until vendor neutral interfaces are available for Biobank
        //this.loadPatientSpecimens();
    }

    //endregion

    //region StudySubject

    /**
     * Load study subjects (EDC) for selected patient entity
     */
    public void loadPatientStudySubjects() {
        try {
            // Populate StudySubject list for selected Patient from EDC
            this.setSelectedEntity(
                    this.studyIntegrationFacade.fetchPatientStudySubjects(
                            this.selectedEntity
                    )
            );

            this.messageUtil.infoText(
                "Study Subjects data for " +
                this.mainBean.getMyAccount().getPartnerSite().getIdentifier() +
                Constants.RPB_IDENTIFIERSEP +
                this.selectedEntity.getPid() +
                " loaded."
            );
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Redirect to selected study subject view
     * @param studySubject studySubject to load
     */
    public void redirectToEdc(StudySubject studySubject) {
        try {
            String studyIdentifier = studySubject.getStudy().getOcStudyUniqueIdentifier();
            String studySubjectIdentifier = studySubject.getStudySubjectId();

            // EDC module URL
            String url = "/edc/ecrfStudies.faces?study=" + studyIdentifier + "&studySubject=" + studySubjectIdentifier;

            // Redirect to PID form
            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .redirect(url);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region DICOM

    /**
     * Load DICOM studies (PACS) for selected patient entity
     */
    public void loadPatientDicomStudies() {
        try {
            // Load DICOM studies
            this.setSelectedEntity(
                    this.studyIntegrationFacade.fetchPatientDicomStudies(
                            this.selectedEntity
                    )
            );

            this.messageUtil.infoText(
                "DICOM data for " +
                this.mainBean.constructMySubjectFullPid(this.selectedEntity.getPid()) +
                " loaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Specimen

    // Postponed until vendor neutral interfaces are available for Biobank
    public void loadPatientSpecimens() {
//        try {
//            // Load Specimens
//            this.setSelectedEntity(
//                    this.studyIntegrationFacade.fetchPatientSpecimens(
//                            this.selectedEntity
//                    )
//            );
//
//            // Create root
//            if (this.selectedEntity != null) {
//                this.loadSpecimensTree();
//            }
//
//            // Audit BIO lookup
//            this.auditLogService.event(
//                    AuditEvent.BIOPatientLoad,
//                    this.mainBean.constructMySubjectFullPid(this.selectedEntity.getPid()), // RPB pseudonym
//                    Constants.CXX_RPB // RPB
//            );
//
//            this.messageUtil.infoText(
//                    "BIO specimens data for " +
//                            this.mainBean.constructMySubjectFullPid(this.selectedEntity.getPid()) +
//                            " loaded.");
//        }
//        catch (Exception err) {
//            this.messageUtil.error(err);
//        }
    }

    // Postponed until vendor neutral interfaces are available for Biobank
    public void loadSpecimensTree() {
//        try {
//            this.specimenRoot = new DefaultTreeNode();
//            if (this.selectedEntity != null) {
//                this.buildSpecimensChildrenNodes(this.specimenRoot, this.selectedEntity.getBioSpecimens());
//            }
//        }
//        catch (Exception err) {
//            this.messageUtil.error(err);
//        }
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = new Person();

        // Normally I am sure that I am providing new patient data
        this.isSure = Boolean.TRUE;
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
                finalResult = this.mainBean.getSvcPid().createSurePatientJson(tokenId, this.newEntity);
            } else {
                finalResult = this.mainBean.getSvcPid().getCreatePatientJsonResponse(tokenId, this.newEntity);
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
                this.auditLogService.event(AuditEvent.PIDUnsure, this.newEntity.toString());
                
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
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colLastName", "colLastName", SortOrder.ASCENDING);
        if (results != null) {

            List<SortMeta> results1 = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colFirstName", "colFirstName", SortOrder.ASCENDING);
            if (results1 != null) {
                results.addAll(results1);

                List<SortMeta> results2 = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colDateOfBirth", "colDateOfBirth", SortOrder.ASCENDING);
                if (results2 != null) {
                    results.addAll(results2);
                }
            }

            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Need to build visibility list
     * @return boolean list of columns visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {

        List<Boolean> result = new ArrayList<>();

        result.add(Boolean.TRUE); // PID
        result.add(Boolean.TRUE); // Surname
        result.add(Boolean.TRUE); // FirstName
        result.add(Boolean.TRUE); // DateOfBirth

        result.add(Boolean.FALSE); // BirthName
        result.add(Boolean.FALSE); // City of residence
        result.add(Boolean.FALSE); // ZIP

        return result;
    }

    //endregion

    //region Private

    /**
     * Recursively build the tree structure
     * @param parent parent node in graph
     */
    // Postponed until vendor neutral interfaces are available for Biobank
    private void buildSpecimensChildrenNodes(TreeNode parent, List<AbstractSpecimen> children) {

//        for (AbstractSpecimen specimen : children) {
//            TreeNode tn = new DefaultTreeNode(specimen, parent);
//
//            if (specimen.getChildren() != null) {
//                this.buildSpecimensChildrenNodes(tn, specimen.getChildren());
//            }
//        }
    }

    /**
     * Fetch Person with IDAT according to provided pseudonym
     * @param pid pseudonym
     * @return Person with IDAT
     */
    private Person reidentifyPseudonymToPerson(String pid) {
        Person result = null;

        try {
            result = this.mainBean.getSvcPid().loadPatient(pid);
            result.setPid(pid);

            this.auditLogService.event(AuditEvent.PIDDepseudonymisation, pid);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }

        return result;
    }

    //endregion

}