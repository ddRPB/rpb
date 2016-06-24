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

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.admin.*;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.edc.mapping.Mapping;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.core.service.DataTransformationService;
import de.dktk.dd.rpb.core.service.IMainzellisteService;
import de.dktk.dd.rpb.core.service.MainzellisteService;
import de.dktk.dd.rpb.core.util.FileUtil;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;

import org.json.JSONObject;
import org.primefaces.component.api.UIColumn;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.*;

import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Bean for importing data into RadPlanBio
 *
 * @author tomas@skripcak.net
 * @since 26 Jan 2015
 * @version 1.0.0
 */
@Named("mbImportData")
@Scope("view")
public class OCImportBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Injects

    //region Main bean

    @Inject
    private MainBean mainBean;

    public MainBean getMainBean() {
        return this.mainBean;
    }

    /**
     * Set MainBean
     * @param bean MainBean
     */
    @SuppressWarnings("unused")
    public void setMainBean(MainBean bean) {
        this.mainBean = bean;
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

    //region Mainzelliste service

    @Inject
    private IMainzellisteService pidService;

    @SuppressWarnings("unused")
    public void setPidService(IMainzellisteService pidService) {
        this.pidService = pidService;
    }

    //endregion

    //region Messages

    @Inject
    protected MessageUtil messageUtil;

    //endregion

    //region DataTransformation

    @Inject
    protected DataTransformationService svcDataTransformation;

    //endregion

    //endregion

    //region  Members

    private static Map<String, Object> genderValues;

    private Study study;

    private Odm metadataOdm;
    private Odm importDataOdm;
    private String selectedImportType;
    private UploadedFile uploadedFile;

    private List<String> importTypes;

    private StudySubject selectedSubject;
    private List<StudySubject> subjectList;
    private List<StudySubject> filteredSubjects;

    private Mapping selectedMapping;

    private StreamedContent zippedImportDataFile;

    private Integer subjectPerDataset;

    private List<SortMeta> subjectsPreSortOrder;

    //endregion

    //region Properties

    public Study getStudy() {
        return this.study;
    }

    public Odm getImportDataOdm() {
        return this.importDataOdm;
    }

    public void setImportDataOdm(Odm odm) {
        this.importDataOdm = odm;
    }

    public UploadedFile getUploadedFile() {
        return this.uploadedFile;
    }

    public void setUploadedFile(UploadedFile file) {
        this.uploadedFile = file;
    }

    public String getSelectedImportType() {
        return this.selectedImportType;
    }

    public void setSelectedImportType(String value) {
        this.selectedImportType = value;
    }

    public List<String> getImportTypes() {
        return this.importTypes;
    }

    public void setImportTypes(List<String> list) {
        this.importTypes = list;
    }

    //region SelectedSubject

    public StudySubject getSelectedSubject() {
        return this.selectedSubject;
    }

    public void setSelectedSubject(StudySubject subject) {
        this.selectedSubject = subject;
    }

    public Boolean getIsSure() {
        return this.selectedSubject != null && this.selectedSubject.getPerson() != null && this.selectedSubject.getPerson().getIsSure();
    }

    public void setIsSure(Boolean value) {
        if (this.selectedSubject != null && this.selectedSubject.getPerson() != null) {
            this.selectedSubject.getPerson().setIsSure(value);
        }
    }

    //endregion

    //region SubjectsList

    public List<StudySubject> getSubjectList() {
        return this.subjectList;
    }

    public void setSubjectList(List<StudySubject> list) {
        this.subjectList = list;
    }

    //endregion

    //region FilteredSubjects

    public List<StudySubject> getFilteredSubjects() {
        return this.filteredSubjects;
    }

    public void setFilteredSubjects(List<StudySubject> list) {
        this.filteredSubjects = list;
    }

    //endregion

    //region SelectedMapping

    public Mapping getSelectedMapping() {
        return this.selectedMapping;
    }

    public void setSelectedMapping(Mapping mapping) {
        this.selectedMapping = mapping;
    }

    //endregion

    //region Gender Values

    static {
        genderValues = new LinkedHashMap<String,Object>();
        genderValues.put("Male", "m"); //label, value
        genderValues.put("Female", "f");
    }

    public Map<String,Object> getGenderValues() {
        return genderValues;
    }

    //endregion

    //region ZippedImportDataFile

    public StreamedContent getZippedImportDataFile() {
        return this.zippedImportDataFile;
    }

    //endregion

    //region SubjectsPerDataset

    public Integer getSubjectPerDataset() {
        return this.subjectPerDataset;
    }

    public void setSubjectPerDataset(Integer subjectPerDataset) {
        this.subjectPerDataset = subjectPerDataset;
    }

    //endregion

    //region Sorting

    public List<SortMeta> getSubjectsPreSortOrder() {
        return this.subjectsPreSortOrder;
    }

    public void setSubjectsPreSortOrder(List<SortMeta> sortList) {
        this.subjectsPreSortOrder = sortList;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        // These are data import types which we want to support
        this.importTypes = new ArrayList<String>();
        this.importTypes.add("Complete study data");
        this.importTypes.add("Quantitative image analysis");

        // By default select the first type = Complete study data
        this.selectedImportType = this.importTypes.get(0);

        // Default one subject per ODM XML file
        this.subjectPerDataset = 1;

        this.setSubjectsPreSortOrder(
                this.buildSubjectsSortOrder()
        );

        // Load initial data
        this.load();

        // Init service
        this.initPidService();
    }

    //endregion

    //region Commands

    /**
     * Reload user's active study metadata metadata
     */
    public void load() {
        try {
            // Reload study metadata
            this.studyIntegrationFacade.init(this.mainBean);
            this.study = this.studyIntegrationFacade.loadStudy();
            this.metadataOdm = this.studyIntegrationFacade.getMetadataOdm();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * When enrollment is complete update the study subject list, e.g. retrieve the subjectKey values
     */
    public void reloadSubjects() {
        try {
            this.studyIntegrationFacade.init(this.mainBean);
            List<StudySubject> enrolledSubjects = this.studyIntegrationFacade.loadStudySubjects();
            this.importDataOdm.updateSubjectKeys(enrolledSubjects);
            this.subjectList = this.importDataOdm.getClinicalDataList().get(0).getStudySubjects();
            this.messageUtil.infoText("Enrolled study subject [" + this.subjectList.size() + "] details reloaded from EDC." );
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Transform data according to selected mapping
     */
    public void transformData() {
        try {
            if (this.selectedImportType.equals("Complete study data")) {

                // Almost correct import data (Subject key has to be loaded after study subject enrollment)
                this.importDataOdm  = this.svcDataTransformation.transformToOdm(
                        this.metadataOdm,
                        this.selectedMapping,
                        this.uploadedFile.getInputstream(),
                        this.uploadedFile.getFileName()
                );

                // Load detected subjects
                if (this.importDataOdm != null &&
                    this.importDataOdm.getClinicalDataList() != null &&
                    this.importDataOdm.getClinicalDataList().size() == 1) {

                    this.subjectList = this.importDataOdm.getClinicalDataList().get(0).getStudySubjects();
                }
                else {
                    throw new Exception("Failed to create clinical data model.");
                }

                this.messageUtil.infoText("Import ODM data model created: " + this.importDataOdm.getDescription());
            }
//            else if (this.selectedImportType.equals("Quantitative image analysis")) {
//                // TODO: questionable when Robert leaves oncoray who knows whether we are going to do it
//            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Generate PIDs for detected subjects
     */
    @SuppressWarnings("unused")
    public void generatePseudonyms() {
        try {
            if (this.subjectList != null) {
                int generatedPids = 0;
                for (StudySubject subject : this.subjectList) {
                    // Only for patient without pseudonym
                    if (subject.getPid() == null || subject.getPid().equals("")) {

                        JSONObject finalResult = this.pidService.newSession();

                        String sessionId = "";
                        if (finalResult != null) {
                            sessionId = finalResult.getString("sessionId");
                        }

                        finalResult = this.pidService.newPatientToken(sessionId);

                        String tokenId = "";
                        if (finalResult != null) {
                            tokenId = finalResult.getString("tokenId");
                        }

                        if (subject.getPerson().getIsSure()) {
                            finalResult = this.pidService.createSurePatienJson(tokenId, subject.getPerson());
                        } else {
                            finalResult = this.pidService.createPatientJson(tokenId, subject.getPerson());
                        }

                        String newId;
                        Boolean tentative = false;
                        Boolean unsure = false;

                        if (finalResult != null) {
                            newId = finalResult.optString("newId");
                            subject.setPid(newId);
                            subject.getPerson().setPid(newId);
                            generatedPids++;
                        }

                        // Delete session if it exists (however session cleanup can be also automaticaly done by Mainzelliste)
                        this.pidService.deleteSession(sessionId);
                    }
                }

                this.messageUtil.info("PIDs for " + Integer.toString(generatedPids) + " subjects sucesfully generated.");
            }
        }
        catch (Exception err) {
            this.messageUtil.error("PID generation failed: ", err.getMessage());

            // Unsure patient
            if (err.getMessage().contains("Failed : HTTP error code: 409")) {
                this.messageUtil.error("Failed because of unsure patient identity.");
            }
        }
    }

    /**
     * Enroll subjects into study in OC EDC
     */
    public void enrolSubjects() {
        //TODO: before enrollment check if subject comply the study configuration PID, SS ID, Gender, Date of Birth... etc.

        try {
            // Enroll all study subject to EDC study
            List<StudySubject> notImported = this.studyIntegrationFacade.enrolSubjectsReturnFailed(
                    this.subjectList,
                    this.metadataOdm.getStudyDetails().getStudyParameterConfiguration()
            );

            // Update subjects keys in odm so that it correspond to keys of newly imported subjects
            if (notImported.size() == 0) {
                this.reloadSubjects();
            }
            else {
                this.messageUtil.warning("For some of provided subjects enrolment failed."); //TODO: do something with not imported subject ideally show them
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Schedule all study events for all subjects
     */
    public void scheduleEvents() {
        try {
            this.studyIntegrationFacade.scheduleStudyEvents(
                    this.importDataOdm
                        .getClinicalDataList()
                        .get(0)
                        .getStudySubjects(),
                    this.metadataOdm
            );

            this.messageUtil.info("All events scheduled.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Import ODM data for all subject and all eCRFs into OC EDC
     */
    public void importData() {
        try {
            this.studyIntegrationFacade.importData(
                    this.importDataOdm,
                    this.subjectPerDataset
            );

            this.messageUtil.info("Import sucessfuly finished.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Download ODM data files in XML form
     */
    public void downloadData() {
        try {
            List<File> odmFiles = this.studyIntegrationFacade.createImportFiles(
                    this.importDataOdm,
                    this.subjectPerDataset
            );

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(out);

            byte[] buffer = new byte[1024];

            for (File file : odmFiles) {
                ZipEntry ze = new ZipEntry(file.getName());
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(file.getName());

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            zos.close();

            this.zippedImportDataFile = new DefaultStreamedContent(
                    new ByteArrayInputStream(out.toByteArray()),
                    FileUtil.MimeType.zip.toString(),
                    this.study.getOcStudyIdentifier() + "-import-data.zip"
            );
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Update selected subject
     */
    public void doUpdateSubject() {
        try {
            this.messageUtil.infoEntity("status_edited_ok", this.selectedSubject);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Event Handlers

    /**
     * Upload input data for import
     * @param event upload event
     */
    public void handleUpload(FileUploadEvent event) {
        try {
            this.uploadedFile = event.getFile();
            this.messageUtil.infoText("File name: " +
                    event.getFile().getFileName() + " file size: " +
                    event.getFile().getSize() / 1024 + " Kb content type: " +
                    event.getFile().getContentType() + " The document file was uploaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Private methods

    /**
     * Construct PID service for communication with Mainzelliste server associated to logged user partner site
     */
    private void initPidService() {
        try {
            DefaultAccount myAccount = this.mainBean.getMyAccount();

            String adminUsername = myAccount.getPartnerSite().getPid().getAdminUsername();
            String adminPassword = myAccount.getPartnerSite().getPid().getAdminPassword();

            String apiKey = myAccount.getPartnerSite().getPid().getApiKey();
            String genratorBaseUrl = myAccount.getPartnerSite().getPid().getGeneratorBaseUrl();
            String callback = myAccount.getPartnerSite().getPortal().getPortalBaseUrl();

            this.pidService = new MainzellisteService();
            this.pidService.setupConnectionInfo(genratorBaseUrl, apiKey, callback);
            this.pidService.setupAdminConnectionInfo(adminUsername, adminPassword);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    private List<SortMeta> buildSubjectsSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtEntities:colStudySubjectId");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colStudySubjectId");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }


    //endregion

}
