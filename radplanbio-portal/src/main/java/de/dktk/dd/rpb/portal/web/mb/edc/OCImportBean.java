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

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.edc.mapping.Mapping;
import de.dktk.dd.rpb.core.service.DataTransformationService;
import de.dktk.dd.rpb.core.service.OdmService;
import de.dktk.dd.rpb.core.service.OpenClinicaService;
import de.dktk.dd.rpb.core.util.FileUtil;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;

import org.json.JSONObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.*;

import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
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

    //endregion

    //region StudyIntegrationFacade

    @Inject
    private StudyIntegrationFacade studyIntegrationFacade;

    //endregion

    //region Messages

    @Inject
    protected MessageUtil messageUtil;

    //endregion

    //region DataTransformation

    @Inject
    protected DataTransformationService svcDataTransformation;

    //endregion

    //region OdmService

    @Inject
    protected OdmService svcOdmService;

    //endregion

    //endregion

    //region  Members

    private static Map<String, Object> genderValues;

    private Study study;

    private Odm metadataOdm;
    private Odm importDataOdm;
    private UploadedFile uploadedFile;

    private StudySubject selectedSubject;
    private List<StudySubject> subjectList;
    private List<StudySubject> filteredSubjects;

    private OdmMatch selectedSubjectOdmMatch;

    private Mapping selectedMapping;

    private StreamedContent zippedImportDataFile;

    private Integer subjectPerDataset;

    private List<Boolean> subjectsColumnVisibilityList;
    private List<SortMeta> subjectsPreSortOrder;

    //endregion

    //region Properties

    //region Study

    public Study getStudy() {
        return this.study;
    }

    //endregion

    //region MetadataODM

    public Odm getMetadataOdm() {
        return this.metadataOdm;
    }

    public void setMetadataOdm(Odm odm) {
        this.metadataOdm = odm;
    }

    //endregion

    //region ImportDataModel

    public Odm getImportDataOdm() {
        return this.importDataOdm;
    }

    public void setImportDataOdm(Odm odm) {
        this.importDataOdm = odm;
    }

    //endregion

    //region SourceDataFile

    public UploadedFile getUploadedFile() {
        return this.uploadedFile;
    }

    public void setUploadedFile(UploadedFile file) {
        this.uploadedFile = file;
    }

    //endregion

    //region SelectedSubject

    public StudySubject getSelectedSubject() {
        return this.selectedSubject;
    }

    public void setSelectedSubject(StudySubject subject) {
        this.selectedSubject = subject;
        // Reset the match object on subject change
        this.selectedSubjectOdmMatch = null;
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

    //region SelectedSubjectOdmMatch

    public OdmMatch getSelectedSubjectOdmMatch() {
        return selectedSubjectOdmMatch;
    }

    public void setSelectedSubjectOdmMatch(OdmMatch selectedSubjectOdmMatch) {
        this.selectedSubjectOdmMatch = selectedSubjectOdmMatch;
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
        genderValues = new LinkedHashMap<>();
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

    //region ColumnVisibilityList

    public List<Boolean> getSubjectsColumnVisibilityList() {
        return subjectsColumnVisibilityList;
    }

    public void setSubjectsColumnVisibilityList(List<Boolean> columnVisibilityList) {
        this.subjectsColumnVisibilityList = columnVisibilityList;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        // Default one subject per ODM XML file
        this.subjectPerDataset = 1;

        this.setSubjectsColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setSubjectsPreSortOrder(
            this.buildSubjectsSortOrder()
        );

        // Load initial data
        this.load();
    }

    //endregion

    //region EventHandlers

    public void onSubjectEntityToggle(ToggleEvent e) {
        try {
            this.subjectsColumnVisibilityList.set(((Integer) e.getData() - 1), e.getVisibility() == Visibility.VISIBLE);
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
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
            List<StudySubject> enrolledSubjects = this.studyIntegrationFacade.loadStudySubjects();
            this.importDataOdm.updateSubjectKeys(enrolledSubjects, this.metadataOdm.getStudyDetails().getStudyParameterConfiguration().getStudySubjectIdGeneration());
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
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Generate PIDs for detected subjects
     */
    public void generatePseudonyms() {
        try {
            if (this.subjectList != null) {
                int generatedPids = 0;
                for (StudySubject subject : this.subjectList) {
                    // Only for patient without pseudonym
                    if (subject.getPid() == null || subject.getPid().equals("")) {

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

                        if (subject.getPerson().getIsSure()) {
                            finalResult = this.mainBean.getSvcPid().createSurePatientJson(tokenId, subject.getPerson());
                        }
                        else {
                            finalResult = this.mainBean.getSvcPid().getCreatePatientJsonResponse(tokenId, subject.getPerson());
                        }

                        String newId;
                        if (finalResult != null) {
                            newId = finalResult.optString("newId");

                            String rpbPid = this.mainBean.constructMySubjectFullPid(newId);

                            subject.setPid(rpbPid);
                            subject.getPerson().setPid(rpbPid);
                            generatedPids++;
                        }

                        // Delete session if it exists (however session cleanup can be also automatically done by Mainzelliste)
                        this.mainBean.getSvcPid().deleteSession(sessionId);
                    }
                }

                this.messageUtil.info("PIDs for " + Integer.toString(generatedPids) + " subjects successfully generated.");
            }
        }
        catch (Exception err) {
            this.messageUtil.error("PID generation failed: ", err.getMessage());
        }
    }

    /**
     * Enroll subjects into study in OC EDC study
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

            this.messageUtil.info("Import successfully finished.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Import subject source data from import model to target system
     * @param studySubject StudySubject
     */
    public void importSubjectData(StudySubject studySubject) {
        try {
            if (this.importDataOdm != null && studySubject != null) {

                // Find ODM resource for selected study subject from source data
                Odm sourceSubjectOdm = this.importDataOdm.findUniqueOdmOrNoneForSubject(
                        studySubject.getSubjectKey()
                );

                this.studyIntegrationFacade.importData(sourceSubjectOdm, 1);
                this.messageUtil.info("Import successfully finished.");
            }
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
     * Compare source data import model with extract from target system for all subjects
     */
    public void compareData() {
        boolean allEqual = true;
        for (StudySubject studySubject : this.subjectList) {
            this.compareSubjectData(studySubject);
            if (!this.selectedSubjectOdmMatch.getMatch()) {
                allEqual = false;
                break;
            }
        }

        if (allEqual) {
            this.messageUtil.info("Source data after mapping equals target data.");
        }
        else {
            this.messageUtil.warning("Non equality found for subject: " + this.selectedSubjectOdmMatch.getClinicalDataMatchList().get(0).getSubjectDataMatchList().get(0).getSubjectKey());
        }
    }

    /**
     * Compare source data import model with extract from target system
     * @param studySubject StudySubject
     */
    public void compareSubjectData(StudySubject studySubject) {
        try {
            if (this.importDataOdm != null && studySubject != null) {

                // Find ODM resource for selected study subject from source data
                Odm sourceSubjectOdm = this.importDataOdm.findUniqueOdmOrNoneForSubject(
                        studySubject.getSubjectKey()
                );

                // Load ODM resource for selected study subject from target data
                String queryOdmXmlPath = this.mainBean.getActiveStudy().getOcoid() + "/" + studySubject.getSubjectKey() + "/*/*";
                Odm targetSubjectOdm = this.mainBean.getOpenClinicaService().getStudyCasebookOdm(
                        OpenClinicaService.CasebookFormat.XML,
                        OpenClinicaService.CasebookMethod.VIEW,
                        queryOdmXmlPath
                );

                this.selectedSubjectOdmMatch = this.svcOdmService.compareOdm(sourceSubjectOdm, targetSubjectOdm);
            }
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
     * Need to build an initial sort order for data table multi sort
     * @return List of SortMeta objects
     */
    private List<SortMeta> buildSubjectsSortOrder() {
        List<SortMeta> results =  DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colStudySubjectId", "colStudySubjectId", SortOrder.ASCENDING);

        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<>();

        result.add(Boolean.TRUE); // IsEnabled
        result.add(Boolean.TRUE); // StudySubjectID
        result.add(Boolean.TRUE); // PID
        result.add(Boolean.FALSE); // SubjectKey
        result.add(Boolean.TRUE); // Gender
        result.add(Boolean.TRUE); // Firstname
        result.add(Boolean.TRUE); // Surname
        result.add(Boolean.TRUE); // Birthdate
        result.add(Boolean.FALSE); // Birthname
        result.add(Boolean.FALSE); // City of residence
        result.add(Boolean.FALSE); // ZIP

        return result;
    }

    //endregion

}
