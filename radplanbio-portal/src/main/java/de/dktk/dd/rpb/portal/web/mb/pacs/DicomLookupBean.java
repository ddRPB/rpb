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

package de.dktk.dd.rpb.portal.web.mb.pacs;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.repository.support.Repository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.EnvironmentService;
import de.dktk.dd.rpb.core.service.ICtpService;
import de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.pacs.support.*;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * ViewModel bean for PACS (User PartnerSite PACS) centric DICOM lookup
 * <p>
 * Copyright (C) 2013-2019 RPB Team
 *
 * @since 22 September 2015
 */
@Named("mbDicomLookup")
@Scope("view")
public class DicomLookupBean extends CrudEntityViewModel<StagedSubject, Integer> {

    //region Finals

    private static final Logger log = Logger.getLogger(DicomLookupBean.class);
    private final MainBean mainBean;
    private final StudyIntegrationFacade studyIntegrationFacade;
    private final EnvironmentService environmentService;
    private final ICtpService ctpService;
    private final HashMap<String, Boolean> visibleComponents = new HashMap<>();
    private final TreeNode root = new DefaultTreeNode();

    //endregion
    //region Members
    protected List<SortMeta> preSortOrder;
    private AuditLogService auditLogService;
    private String viewType;
    private String idString;
    private Study rpbStudy;
    private List<StudySubject> studySubjectsList;
    private StudySubject inputStudySubject;
    private List<Subject> resultSubjectList;
    private List<StagedSubject> stagedSubjects;
    private Subject selectedSubject;
    private StagedDicomStudy selectedDicomStudy;
    private List<DicomSeries> selectedDicomSeries = new ArrayList<>();
    private List<StagedDicomSeries> stagedDicomSeries;
    private IStagedSubjectRepository stagedSubjectRepository;
    private DicomStudyDescriptionEdcCodeUtil service;

    //endregion

    // region constructor

    @Inject
    public DicomLookupBean(MainBean mainBean, StudyIntegrationFacade studyIntegrationFacade,
                           AuditLogService auditLogService, EnvironmentService environmentService, ICtpService ctpService) {
        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.auditLogService = auditLogService;
        this.environmentService = environmentService;
        this.ctpService = ctpService;
    }

    // endregion

    //region get/set

    public List<StagedSubject> getStagedSubjects() {
        return stagedSubjects;
    }

    public void setStagedSubjects(List<StagedSubject> stagedSubjects) {
        this.stagedSubjects = stagedSubjects;
    }

    public Subject getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(Subject selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    public List<DicomSeries> getSelectedDicomSeries() {
        return selectedDicomSeries;
    }

    public void setSelectedDicomSeries(List<DicomSeries> selectedDicomSeries) {
        this.selectedDicomSeries = selectedDicomSeries;
    }

    public void clearSelectedDicomSeries() {
        this.selectedDicomSeries.clear();
    }

    public List<StagedDicomSeries> getStagedDicomSeries() {
        return stagedDicomSeries;
    }

    public void setStagedDicomSeries(List<StagedDicomSeries> stagedDicomSeries) {
        this.stagedDicomSeries = stagedDicomSeries;
    }

    @Override
    public TreeNode getRoot() {
        return root;
    }

    @Override
    public List<SortMeta> getPreSortOrder() {
        return this.preSortOrder;
    }

    @Override
    public void setPreSortOrder(List<SortMeta> preSortOrder) {
        this.preSortOrder = preSortOrder;
    }

    @Override
    public void load() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );
    }

    /**
     * Get Repository
     *
     * @return repository
     */
    @Override
    protected Repository<StagedSubject, Integer> getRepository() {
        return stagedSubjectRepository;
    }

    //endregion

    /**
     * Prepare new entity
     */
    @Override
    public void prepareNewEntity() {
        // NOOP
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<>();
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();

        String colPath = ":form:tabView:dtPacsResults:colSubjectUid";
        UIComponent column = viewRoot.findComponent(colPath);
        if (column != null) {
            DataTableUtil.addSortOrder(results, colPath, "colSubjectUid", SortOrder.ASCENDING);
        }

        colPath = ":dicomStagedSeriesDialogForm:DicomStudyDetailTable:seriesUidColumn";
        column = viewRoot.findComponent(colPath);
        if (column != null) {
            DataTableUtil.addSortOrder(results, colPath, "colSubjectUid", SortOrder.ASCENDING);
        }

        DataTableUtil.addSortOrder(results, colPath, "seriesUidColumn", SortOrder.ASCENDING);
        return results;
    }

    public DicomStudyDescriptionEdcCodeUtil getService() {
        return service;
    }

    public void setService(DicomStudyDescriptionEdcCodeUtil service) {
        this.service = service;
    }

    public List<StudySubject> getStudySubjectsList() {
        return studySubjectsList;
    }

    public void setStudySubjectsList(List<StudySubject> studySubjectsList) {
        this.studySubjectsList = studySubjectsList;
    }

    public StudySubject getInputStudySubject() {
        return inputStudySubject;
    }

    public void setInputStudySubject(StudySubject inputStudySubject) {
        this.inputStudySubject = inputStudySubject;
    }

    public List<Subject> getResultSubjectList() {
        return resultSubjectList;
    }

    public void setResultSubjectList(List<Subject> resultSubjectList) {
        this.resultSubjectList = resultSubjectList;
    }


    public Study getRpbStudy() {
        return rpbStudy;
    }

    public void setRpbStudy(Study rpbStudy) {
        this.rpbStudy = rpbStudy;
    }

    public String getViewType() {
        return this.viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public StagedDicomStudy getSelectedDicomStudy() {
        return selectedDicomStudy;
    }

    public void setSelectedDicomStudy(StagedDicomStudy selectedDicomStudy) {
        this.selectedDicomStudy = selectedDicomStudy;
    }

    public String getIdString() {
        return this.idString;
    }

    public void setIdString(String patientsString) {
        this.idString = patientsString;
    }

    // endregion

    @PostConstruct
    public void init() {
        this.viewType = "PATIENT";
        this.studyIntegrationFacade.init(this.mainBean);
        this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);

        initializeVisibleComponentMap();

        this.rpbStudy = this.studyIntegrationFacade.loadStudyWithMetadata();

        if (this.collectsDicomData()) {
            try {
                this.studySubjectsList = this.studyIntegrationFacade.loadStudySubjects();
            } catch (OCConnectorException e) {
                log.error("There was a problem loading the StudySubjects: " + e.getMessage());
                log.debug(e.getStackTrace().toString());
            }
            this.service = new DicomStudyDescriptionEdcCodeUtil();
        } else {
            String message = "Dicom lookup is not available for the study, because the the \"DICOM\" tag is false";
            messageUtil.info(message);
            log.info(message);
        }

        this.load();
    }

    private void initializeVisibleComponentMap() {
        visibleComponents.put("uniqueIdentifier", true);
        visibleComponents.put("studySubjectId", true);
        visibleComponents.put("gender", true);
        visibleComponents.put("enrollmentDate", true);
    }

    public boolean collectsDicomData() {
        return Boolean.parseBoolean(this.rpbStudy.getTagValue("DICOM"));
    }

    public List<StudySubject> filterMatchingStudySubjects(String query) {

        List<StudySubject> filteredResults = new ArrayList<>();

        if (this.studySubjectsList == null) {
            log.warn("studySubjectsList is Null - cannot filter from empty data set");
            return filteredResults;
        }

        for (StudySubject ss : this.studySubjectsList) {
            if (ss.getStudySubjectId().startsWith(query)) {
                filteredResults.add(ss);
            }
        }

        Collections.sort(filteredResults, new Comparator<StudySubject>() {
            @Override
            public int compare(StudySubject o1, StudySubject o2) {
                return o1.getStudySubjectId().compareTo(o2.getStudySubjectId());
            }
        });

        return filteredResults;
    }

    //endregion

    public void resetStudySubjectSearchComponents() {
        this.inputStudySubject = null;
    }

    /**
     * Triggers a search for DICOM data, based on the PID of the StudySubject stored in inputStudySubject.
     * It will search for all patients ("*") if the property inputStudySubject is null.
     */
    public void searchDicomDataByStudySubjectId() throws Exception {
        String dicomQueryPatientId;
        List<StudySubject> queriedStudySubjectsList;

        if (this.noStudySubjectChosen()) {
            log.debug("No specific participant chosen. Query for all StudySubjects of the study");
            resultSubjectList = this.mainBean.getPacsService().loadPatients(this.studySubjectsList);
            queriedStudySubjectsList = studySubjectsList;
        } else {
            dicomQueryPatientId = this.inputStudySubject.getPid();
            log.info("Query patient data from PacsService for Pid: " + dicomQueryPatientId);
            resultSubjectList = this.mainBean.getPacsService().loadPatient(dicomQueryPatientId);
            queriedStudySubjectsList = new ArrayList<>();
            queriedStudySubjectsList.add(inputStudySubject);
        }

        StagedSubjectPacsResultBuilder stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjectsList, resultSubjectList);
        stagedSubjectPacsResultBuilder.
                filterResultsByStudySubjectList().
                addSubjectsWithoutResultsFromStudySubjectList();
        this.stagedSubjects = stagedSubjectPacsResultBuilder.getStagedSubjects();

        String uidPrefix = ctpService.getDicomUidPrefix();

        for (StagedSubject researchSubject : this.stagedSubjects) {
            StagedDicomStudyBuilder dicomStudyInResearchContextBuilder = StagedDicomStudyBuilder.getInstance(researchSubject.getDicomStudyList(), uidPrefix, this.getCurrentStudyEdcCode());
            dicomStudyInResearchContextBuilder.filterFirstStageStudies();
            dicomStudyInResearchContextBuilder.filterSecondStageStudiesByEdcCode();
            researchSubject.setStagedStudies(dicomStudyInResearchContextBuilder.getStagedStudies());
        }
    }

    private boolean noStudySubjectChosen() {
        return this.inputStudySubject == null;
    }

    /**
     * Get the EDC code for the current active study
     *
     * @return String
     */
    private String getCurrentStudyEdcCode() {
        return this.rpbStudy.getTagValue("EDC-code");
    }

    public void searchDicomSeriesOnPacs() {
        String dicomPatientId = selectedSubject.getUniqueIdentifier();
        String dicomStudyUid = selectedDicomStudy.getStudyInstanceUID();
        String secondStageUid = selectedDicomStudy.getStageTwoStudyInstanceUID();
        DicomStudy stageOneStudy;
        DicomStudy stageTwoStudy;

        String uidPrefix = ctpService.getDicomUidPrefix();
        String edcCode = this.getCurrentStudyEdcCode();
        String partnerSideCode = this.mainBean.getMyAccount().getPartnerSite().getIdentifier();

        if (!"".equals(dicomPatientId) && !"".equals(dicomStudyUid)) {
            stageOneStudy = this.mainBean.getPacsService().
                    loadPatientStudy(dicomPatientId, dicomStudyUid);

            if (secondStageUid.length() > 1) {
                stageTwoStudy = this.mainBean.getPacsService().
                        loadPatientStudy(dicomPatientId, secondStageUid);
            } else {
                stageTwoStudy = new DicomStudy();
                stageTwoStudy.setStudySeries(new ArrayList<DicomSeries>());
            }
            if (stageOneStudy == null || stageTwoStudy == null) {
                String errorMessage = "There was a problem loading data from the PACS.";
                log.warn(errorMessage);
                this.messageUtil.errorText(errorMessage);
                return;
            }

            this.stagedDicomSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(stageOneStudy.getStudySeries(), stageTwoStudy.getStudySeries(), uidPrefix, partnerSideCode, edcCode);
            updateSelectedStagedStudyWithSeriesInformation(stageOneStudy);
        }
    }

    private void updateSelectedStagedStudyWithSeriesInformation(DicomStudy stageOneStudy) {
        if (stageOneStudy.getStudySeries() != null && this.selectedDicomStudy != null) {
            this.selectedDicomStudy.setStudySeries(stageOneStudy.getStudySeries());
        }
    }

    public void stageChosenDicomSeries() {
        if (this.selectedDicomSeries.size() > 0) {
            String dicomPatientId = selectedSubject.getUniqueIdentifier();
            String dicomStudyUid = selectedDicomStudy.getStudyInstanceUID();
            Boolean update = selectedDicomStudy.isHasStageTwoRepresentation();
            AuditEvent auditEvent;

            if (update) {
                auditEvent = AuditEvent.PACSDataModification;
            } else {
                auditEvent = AuditEvent.PACSDataCreation;
            }

            String aetPrefix = this.ctpService.getBaseAetName();
            String edcCode = this.getCurrentStudyEdcCode();
            String uidPrefix = ctpService.getDicomUidPrefix();

            String partnerSideCode = this.mainBean.getMyAccount().getPartnerSite().getIdentifier();

            if (this.selectedDicomSeries.size() == this.stagedDicomSeries.size()) {
                this.auditLogService.event(auditEvent, dicomPatientId, this.rpbStudy.getProtocolId(), dicomPatientId + "/" + DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, dicomStudyUid));
                this.mainBean.getPacsService().moveDicomStudy(dicomPatientId, dicomStudyUid, aetPrefix + edcCode);
            } else {
                for (DicomSeries series : this.selectedDicomSeries) {
                    this.auditLogService.event(auditEvent, dicomPatientId, this.rpbStudy.getProtocolId(), dicomPatientId + "/" + DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, dicomStudyUid) + "/" + DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, series.getSeriesInstanceUID()));
                    this.mainBean.getPacsService().moveDicomSeries(dicomPatientId, dicomStudyUid, series.getSeriesInstanceUID(), aetPrefix + edcCode);
                }
            }
        }
    }

    public void stageCompleteDicomStudy() {
        String dicomPatientId = selectedSubject.getUniqueIdentifier();
        String dicomStudyUid = selectedDicomStudy.getStudyInstanceUID();
        String aetPrefix = this.ctpService.getBaseAetName();
        String uidPrefix = ctpService.getDicomUidPrefix();
        String edcCode = this.getCurrentStudyEdcCode();
        String partnerSideCode = this.mainBean.getMyAccount().getPartnerSite().getIdentifier();

        AuditEvent auditEvent;
        Boolean update = selectedDicomStudy.isHasStageTwoRepresentation();
        if (update) {
            auditEvent = AuditEvent.PACSDataModification;
        } else {
            auditEvent = AuditEvent.PACSDataCreation;
        }

        this.auditLogService.event(auditEvent, dicomPatientId, this.rpbStudy.getProtocolId(), dicomPatientId + "/" + DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, dicomStudyUid));
        this.mainBean.getPacsService().moveDicomStudy(dicomPatientId, dicomStudyUid, aetPrefix + edcCode);
    }

    public void checkDicomDataFlag() {
        if (!collectsDicomData()) {
            String message = "Dicom lookup is not available for the study, because the the \"DICOM\" tag is false";
            messageUtil.info(message);
        }
    }


    /**
     * Returns true if component name is marked as visible in visibleComponents map
     *
     * @param componentName String
     * @return boolean
     */
    public boolean componentIsVisible(String componentName) {
        if (visibleComponents.containsKey(componentName)) {
            return visibleComponents.get(componentName);
        } else {
            return false;
        }
    }

    /**
     * Returns true if input string matches the naming schema for a study0 studyDescription value
     *
     * @param descriptionWithPrefix String
     * @return boolean
     */
    public boolean descriptionHasStudyZeroPrefix(String descriptionWithPrefix) {
        return DicomStudyDescriptionEdcCodeUtil.hasStudyZeroPrefix(descriptionWithPrefix);
    }

}
