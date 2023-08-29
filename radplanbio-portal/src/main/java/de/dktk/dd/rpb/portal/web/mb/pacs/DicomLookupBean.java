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

package de.dktk.dd.rpb.portal.web.mb.pacs;

import de.dktk.dd.rpb.core.builder.pacs.StagedDicomStudyBuilder;
import de.dktk.dd.rpb.core.builder.pacs.StagedSubjectPacsResultBuilder;
import de.dktk.dd.rpb.core.converter.DicomSeriesToStagedDicomSeriesConverter;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.domain.edc.sorter.StudyListSorter;
import de.dktk.dd.rpb.core.domain.pacs.DicomImage;
import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeriesVirtualSeries;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomStudy;
import de.dktk.dd.rpb.core.domain.pacs.StagedSubject;
import de.dktk.dd.rpb.core.facade.ParallelConquestRequestsFacade;
import de.dktk.dd.rpb.core.repository.support.Repository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.ICtpService;
import de.dktk.dd.rpb.core.util.DicomUidReGeneratorUtil;
import de.dktk.dd.rpb.core.util.PatientIdentifierUtil;
import de.dktk.dd.rpb.core.util.StudySubjectListUtil;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.builder.DicomStudyVirtualNodeTreeBuilder;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import de.dktk.dd.rpb.portal.web.util.PrimeFacesTreeHelper;
import org.apache.commons.lang.SerializationUtils;
import org.json.JSONException;
import org.omnifaces.util.Faces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTDOSE;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTIMAGE;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTPLAN;
import static de.dktk.dd.rpb.core.util.Constants.DICOM_RTSTRUCT;
import static de.dktk.dd.rpb.core.util.Constants.RPB_IDENTIFIERSEP;
import static de.dktk.dd.rpb.core.util.Constants.study0EdcCode;
import static de.dktk.dd.rpb.core.util.Constants.study0Identifier;

/**
 * ViewModel bean for PACS (User PartnerSite PACS) centric DICOM lookup
 * <p>
 *
 * @since 22 September 2015
 */
@Named("mbDicomLookup")
@Scope("view")
public class DicomLookupBean extends CrudEntityViewModel<StagedSubject, Integer> {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(DicomLookupBean.class);

    private final MainBean mainBean;
    private final StudyIntegrationFacade studyIntegrationFacade;
    private final ICtpService ctpService;

    //endregion

    //region Members

    private final AuditLogService auditLogService;
    private Study rpbStudy;
    private List<StudySubject> studySubjectsList;
    private StudySubject inputStudySubject;
    private String inputStudySubjectBulkString;
    private List<StudySubject> inputStudySubjectList = new ArrayList<>();
    private List<Subject> resultSubjectList;
    private List<Subject> clinicalSubjectList;
    private List<StagedSubject> stagedSubjects;
    private StagedSubject selectedSubject;
    private de.dktk.dd.rpb.core.domain.edc.Study selectedEdcStudy;
    private List<de.dktk.dd.rpb.core.domain.edc.Study> edcStudyList;
    private StagedDicomStudy selectedDicomStudy;
    private StagedDicomSeries selectedStagedDicomSeries;
    private List<StagedDicomSeries> selectedDicomSeries = new ArrayList<>();
    private List<StagedDicomSeries> stagedDicomSeries;

    private DicomStudyVirtualNodeTreeBuilder dicomStudyVirtualNodeTreeBuilder;
    private TreeNode dicomSeriesTree;
    private TreeNode[] selectedDicomSeriesNodes;
    private StagedDicomSeries selectedStagedSeries;

    private int progress = 0;
    // limits the count of subjects per search in ddlStudySubjects component
    private int maxSubjectsPerSearch = 10;

    private List<String> availableSeriesModalities = new ArrayList<>();
    private String[] queriedDicomSeriesModality;

    //endregion

    //region Constructor

    @Inject
    public DicomLookupBean(MainBean mainBean, StudyIntegrationFacade studyIntegrationFacade,
                           AuditLogService auditLogService, ICtpService ctpService) {
        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.auditLogService = auditLogService;
        this.ctpService = ctpService;
    }

    // endregion

    //region Properties

    public List<StagedSubject> getStagedSubjects() {
        return stagedSubjects;
    }

    public void setStagedSubjects(List<StagedSubject> stagedSubjects) {
        this.stagedSubjects = stagedSubjects;
    }

    public StagedSubject getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(StagedSubject selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    public de.dktk.dd.rpb.core.domain.edc.Study getSelectedEdcStudy() {
        return selectedEdcStudy;
    }

    public void setSelectedEdcStudy(de.dktk.dd.rpb.core.domain.edc.Study selectedEdcStudy) {
        this.selectedEdcStudy = selectedEdcStudy;
    }

    public List<de.dktk.dd.rpb.core.domain.edc.Study> getEdcStudyList() {
        return edcStudyList;
    }

    public void setEdcStudyList(List<de.dktk.dd.rpb.core.domain.edc.Study> edcStudyList) {
        this.edcStudyList = StudyListSorter.sortStudyList(edcStudyList);
    }

    public StagedDicomSeries getSelectedStagedDicomSeries() {
        return selectedStagedDicomSeries;
    }

    public void setSelectedStagedDicomSeries(StagedDicomSeries selectedStagedDicomSeries) {
        this.selectedStagedDicomSeries = selectedStagedDicomSeries;
    }

    public List<StagedDicomSeries> getSelectedDicomSeries() {
        return selectedDicomSeries;
    }

    public void setSelectedDicomSeries(List<StagedDicomSeries> selectedDicomSeries) {
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

    public String getInputStudySubjectBulkString() {
        return inputStudySubjectBulkString;
    }

    public void setInputStudySubjectBulkString(String inputStudySubjectBulkString) {
        this.inputStudySubjectBulkString = inputStudySubjectBulkString;
    }

    public List<Subject> getResultSubjectList() {
        return resultSubjectList;
    }

    public Study getRpbStudy() {
        return rpbStudy;
    }

    public void setRpbStudy(Study rpbStudy) {
        this.rpbStudy = rpbStudy;
    }

    public StagedDicomStudy getSelectedDicomStudy() {
        return selectedDicomStudy;
    }

    public void setSelectedDicomStudy(StagedDicomStudy selectedDicomStudy) {
        this.selectedDicomStudy = selectedDicomStudy;
    }

    public TreeNode getDicomSeriesTree() {
        return dicomSeriesTree;
    }

    public void setDicomSeriesTree(TreeNode dicomSeriesTree) {
        this.dicomSeriesTree = dicomSeriesTree;
    }

    public TreeNode[] getSelectedDicomSeriesNodes() {
        return selectedDicomSeriesNodes;
    }

    public void setSelectedDicomSeriesNodes(TreeNode[] selectedDicomSeriesNodes) {
        this.selectedDicomSeriesNodes = selectedDicomSeriesNodes;
    }

    public StagedDicomSeries getSelectedStagedSeries() {
        return selectedStagedSeries;
    }

    public void setSelectedStagedSeries(StagedDicomSeries selectedStagedSeries) {
        this.selectedStagedSeries = selectedStagedSeries;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxSubjectsPerSearch() {
        return maxSubjectsPerSearch;
    }

    public List<String> getAvailableSeriesModalities() {
        return availableSeriesModalities;
    }

    public String[] getQueriedDicomSeriesModality() {
        return queriedDicomSeriesModality;
    }

    public void setQueriedDicomSeriesModality(String[] queriedDicomSeriesModality) {
        this.queriedDicomSeriesModality = queriedDicomSeriesModality;
    }

    /**
     * Get Repository
     *
     * @return repository
     */
    @Override
    protected Repository<StagedSubject, Integer> getRepository() {
        return null;
    }

    //endregion

    //region Init

    /**
     * Initialisation of the bean
     */
    @PostConstruct
    public void init() {

        this.setPreSortOrder(
                this.buildSortOrder()
        );

        createAvailableSeriesModalities();
        this.queriedDicomSeriesModality = new String[]{DICOM_RTSTRUCT, DICOM_RTPLAN, DICOM_RTDOSE};

        this.studyIntegrationFacade.init(this.mainBean);
        this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);
        this.initializeVisibleComponentMap();


        try {
            this.rpbStudy = this.studyIntegrationFacade.loadStudyWithMetadata();
        } catch (Exception e) {
            String errorMessage = "There was a problem loading the study.";
            String userAdvice = "Please try to reload the study and then reload the page.";
            log.error("errorMessage", e);
            messageUtil.errorText(errorMessage + " " + userAdvice);
        }
    }

    private void createAvailableSeriesModalities() {
        this.availableSeriesModalities.add(DICOM_RTSTRUCT);
        this.availableSeriesModalities.add(DICOM_RTPLAN);
        this.availableSeriesModalities.add(DICOM_RTDOSE);
        this.availableSeriesModalities.add(DICOM_RTIMAGE);
    }

    /**
     * After initialisation
     */
    public void onLoad() {
        // Do not trigger load when is is AJAX post back that refreshes the partial component views
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (this.collectsDicomData()) {

                DicomStudyVirtualNodeTreeBuilder dicomStudyVirtualNodeTreeBuilder = new DicomStudyVirtualNodeTreeBuilder();
                this.dicomSeriesTree = dicomStudyVirtualNodeTreeBuilder.build();

                try {
                    this.studySubjectsList = this.studyIntegrationFacade.loadStudySubjects();
                    if (this.isInStudyZero()) {
                        this.setEdcStudyList(this.getAllEdcStudySitesExceptStudyZero());
                    }
                } catch (Exception e) {
                    String errorMessage = "There was a problem loading the StudySubjects and Sites.";
                    String userAdvice = "Please try to reload the study.";
                    log.error(errorMessage, e);
                    messageUtil.errorText(errorMessage + " " + userAdvice);
                }
            } else {
                String message = "DICOM lookup is not available for this study, because the the \"DICOM\" tag is set to false.";
                String userAdvice = "Please contact the owner of the study to change that.";
                messageUtil.infoText(message + " " + userAdvice);
                log.info(message);
            }
        }
    }

    //endregion

    //region Methods

    public boolean collectsDicomData() {
        return Boolean.parseBoolean(this.rpbStudy.getTagValue("DICOM"));
    }

    /**
     * Iterates on the studySubjectList and returns a list of subjects where the StudySubjectId or PID
     * matches the queryString. The count of results depends in the this.maxSubjectsPerSearch property and is
     * maxSubjectsPerSearch + 1 to trigger in the UI that the information is shows that there are more results.
     *
     * @param studySubjectIdOrPidQuery partial string of the StudySubjectId or PID
     * @return Ordered list of matching StudySubjects
     */
    public List<StudySubject> filterMatchingStudySubjects(String studySubjectIdOrPidQuery) {

        List<StudySubject> filteredResults = new ArrayList<>();

        if (this.studySubjectsList == null || this.studySubjectsList.isEmpty()) {
            String errorMessage = "The property studySubjectsList is Null or empty.";
            String userAdvice = "Please try to reload the study and then the page.";
            log.warn(errorMessage);
            messageUtil.warningText(errorMessage + " " + userAdvice);
            return filteredResults;
        }


        for (StudySubject ss : this.studySubjectsList) {
            // the UI shows a maximum count of subjects -> stop adding more results if we have 11
            // one more, because the UI then shows that there are more results
            if (filteredResults.size() > this.maxSubjectsPerSearch) {
                break;
            }

            if (ss.getStudySubjectId().toUpperCase().contains(studySubjectIdOrPidQuery.toUpperCase())) {
                filteredResults.add(ss);
            } else {

                if (ss.getPid() != null) {
                    if (!ss.getPid().isEmpty()) {
                        if (ss.getPid().toUpperCase().contains(studySubjectIdOrPidQuery.toUpperCase())) {
                            filteredResults.add(ss);
                        }
                    }
                }

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

    /**
     * Triggers a search for DICOM data, based on the PID of the StudySubject stored in inputStudySubject.
     * The results will be stored in stagedSubjects which includes information about staged studies.
     * Additional information from the clinical system will be loaded if Study0 is loaded, because then we expect that
     * the user wants to stage information from a clinical study to Study0.
     */
    public void searchDicomDataByStudySubjectId() {
        try {
            this.inputStudySubjectList.clear();
            this.inputStudySubjectList.add(this.inputStudySubject);
            this.searchDicomDataByStudySubjectList();
        } catch (Exception e) {
            handleDicomSearchException(e);
        }
    }

    /**
     * Triggers a search for DICOM data, based on a list of PIDs of the StudySubject stored in inputStudySubjectBulkString.
     * The results will be stored in stagedSubjects which includes information about staged studies.
     * Additional information from the clinical system will be loaded if Study0 is loaded, because then we expect that
     * the user wants to stage information from a clinical study to Study0.
     */
    public void searchDicomDataFromBulkTextWithPids() {
        if (this.inputStudySubjectBulkString == null ||
                this.inputStudySubjectBulkString.isEmpty() || this.inputStudySubjectBulkString.equals("*")) {

            if (isInStudyZero()) {
                String errorMessage = "We do not allow a search on all subjects in this context, since this study includes too many subjects";
                String apologyMessage = "Sorry about that.";
                String userAdvice = "Please use the bulk search that allows to search, based on a list of identifiers.";
                messageUtil.warningText(errorMessage + " " + apologyMessage + " " + userAdvice);
                return;
            }
            messageUtil.infoText("Searching for all subjects of this study");
            this.inputStudySubjectList.clear();
            this.inputStudySubjectList.addAll(this.studySubjectsList);

            try {
                this.searchDicomDataByStudySubjectList();
            } catch (Exception e) {
                handleDicomSearchException(e);
            }
        } else {
            List<String> dividedIdValues = splitBulkInputBySeparatorValues(";|,|\\t|\0|\\s|\\r|\\n");
            Set<StudySubject> subjects = getMatchingStudySubjects(dividedIdValues);

            if (subjects.isEmpty()) {
                String errorMessage = "There is no matching subject.";
                String userAdvice = "Please provide a list of valid identifiers separated by comma, semicolon, space or line break";
                messageUtil.warningText(errorMessage + " " + userAdvice);
            } else {
                try {
                    performDicomBulkSearch(subjects);
                } catch (Exception e) {
                    handleDicomSearchException(e);
                }
            }
        }
    }

    /**
     * Loads data from the Pacs - based on the inputStudySubjectList. Then it creates a list of stagedSubjects which
     * reflects if Studies of a subject are present in two different stages and there identifiers. Results will be
     * stored in the stagedSubjects property.
     */
    public void searchDicomDataByStudySubjectList() {
        try {
            if (isInStudyZero()) {
                requirePatientDataFromClinicalPacs();
            }
            requirePatientDataFromResearchPacs();

        } catch (Exception e) {
            handleDicomSearchException(e);
        }
        buildStagedStudySubjectList(this.inputStudySubjectList);

        String uidPrefix = ctpService.getDicomUidPrefix();

        for (StagedSubject researchSubject : this.stagedSubjects) {
            StagedDicomStudyBuilder stagedDicomStudyBuilder = getStagedDicomStudyInformation(uidPrefix, researchSubject);
            researchSubject.setStagedStudies(stagedDicomStudyBuilder.getStagedStudies());
        }
    }

    /**
     * Get the EDC code for the current active study
     *
     * @return String
     */
    public String getCurrentStudyEdcCode() {
        return this.rpbStudy.getTagValue("EDC-code");
    }

    /**
     * Loads DicomSeries information from the research pacs (PacsService) based on the selectedSubject and stores the
     * results in stagedDicomSeries. Additionally, it queries the clinical pacs if an ClinicalStudyInstanceUid is
     * defined in the selectedSubject. This happens usually in context of Study0
     */
    public void searchDicomSeriesOnPacs() {
        String dicomPatientId = selectedSubject.getUniqueIdentifier();
        String clinicalStudyUid = selectedDicomStudy.getClinicalStudyInstanceUid();

        DicomStudy stageOneStudy = getStageOneDicomStudyFromPacs(dicomPatientId);
        DicomStudy stageTwoStudy = getStageTwoDicomStudyFromPacs(dicomPatientId);

        String uidPrefix = ctpService.getDicomUidPrefix();
        String edcCode = this.getCurrentStudyEdcCode();
        String partnerSideCode = this.mainBean.getMyAccount().getPartnerSite().getIdentifier();

        if (!clinicalStudyUid.isEmpty()) {
            DicomStudy clinicalStudy = getClinicalDicomStudyFromPacs(clinicalStudyUid);
            this.stagedDicomSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                    clinicalStudy.getStudySeries(), stageOneStudy.getStudySeries(), uidPrefix);
            updateSelectedStagedStudyWithSeriesInformation(clinicalStudy);
        } else {
            this.stagedDicomSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                    stageOneStudy.getStudySeries(), stageTwoStudy.getStudySeries(), uidPrefix, partnerSideCode, edcCode);
            updateSelectedStagedStudyWithSeriesInformation(stageOneStudy);
        }

        if (minOneStagedSeriesHasRtStructImage()) {
            this.dicomStudyVirtualNodeTreeBuilder = new DicomStudyVirtualNodeTreeBuilder(this.stagedDicomSeries);
            this.dicomSeriesTree = this.dicomStudyVirtualNodeTreeBuilder.build();
        } else {
            this.dicomSeriesTree.getChildren().clear();
        }
    }

    private boolean minOneStagedSeriesHasRtStructImage() {
        for (StagedDicomSeries series : this.stagedDicomSeries) {
            if (series.getSeriesModality().equals(DICOM_RTSTRUCT)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a tree of StagedDicomSeries, based on the relations from series details if details are not loaded yet.
     * The tree will be stored on this.dicomSeriesTree.
     *
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws JSONException
     */
    public void updateSeriesTreeWithDetailsIfNecessary() throws InterruptedException, ExecutionException, JSONException {
        if (this.selectedDicomStudy == null) {
            return;
        }

        if (this.dicomStudyVirtualNodeTreeBuilder.hasDicomSeriesDetailData()) {
            return;
        }
        this.updateSeriesTreeWithDetails();

    }

    /**
     * Creates a tree of StagedDicomSeries, based on the relations from series details.
     * The tree will be stored on this.dicomSeriesTree.
     *
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws JSONException
     */
    public void updateSeriesTreeWithDetails() throws InterruptedException, ExecutionException, JSONException {

        if (this.selectedDicomStudy == null) {
            return;
        }

        ParallelConquestRequestsFacade parallelConquestRequests = new ParallelConquestRequestsFacade(
                this.mainBean.getClinicalPacsService(),
                this.mainBean.getPacsService(),
                this.selectedDicomStudy,
                this.selectedSubject
        );

        // Clone to avoid side effect when requesting details again with fewer details
        List<StagedDicomSeries> updatedStagedDicomSeries = new ArrayList<>();
        for (StagedDicomSeries series : this.stagedDicomSeries
        ) {
            updatedStagedDicomSeries.add((StagedDicomSeries) SerializationUtils.clone(series));
        }

        List<StagedDicomSeries> requestImagesList = new ArrayList<>();

        // limit the requests of DicomSeries instances to improve the performance
        if (queriedDicomSeriesModality.length < this.availableSeriesModalities.size()) {
            addStagedDicomSeriesBasedOnModalityList(requestImagesList, updatedStagedDicomSeries);
        } else {
            requestImagesList.addAll(updatedStagedDicomSeries);
        }

        parallelConquestRequests.addDicomImagesToDicomSeriesList(requestImagesList);

        this.dicomStudyVirtualNodeTreeBuilder = new DicomStudyVirtualNodeTreeBuilder(updatedStagedDicomSeries);
        this.dicomSeriesTree = this.dicomStudyVirtualNodeTreeBuilder.build();
    }

    /**
     * Adds StagedDicomSeries to the list that have a Modality that is not on the filter list.
     *
     * @param filteredStagedDicomSeriesList List where StagedDicomSeries will be added
     */
    private void addStagedDicomSeriesBasedOnModalityList(List<StagedDicomSeries> filteredStagedDicomSeriesList, List<StagedDicomSeries> updatedStagedDicomSeries) {
        for (StagedDicomSeries stagedDicomSeries : updatedStagedDicomSeries
        ) {
            if (Arrays.asList(this.queriedDicomSeriesModality).contains(stagedDicomSeries.getSeriesModality())) {
                filteredStagedDicomSeriesList.add(stagedDicomSeries);
            }
        }
    }

    /**
     * Iterates on the tree of StagedDicomSeries nodes and copies selected nodes and its parents into
     * the selectedDicomSeries List to be staged in the same way as in the table view.
     */
    public void stageChosenDicomSeriesFromTreeView() {

        if (this.selectedDicomSeriesNodes == null) {
            return;
        }

        try {
            this.selectedDicomSeries = PrimeFacesTreeHelper.extractAllDataIncludingParents(this.selectedDicomSeriesNodes);
        } catch (Exception e) {
            String message = "There was a problem to get StagedDicomSeries from a node that was selected in tree view.";
            log.warn(message, e);
            messageUtil.warning(message, e);
        }

        this.stageChosenDicomSeries();
    }

    /**
     * Triggers a move request for the DicomSeries on the corresponding Pacs system, based on the selectedDicomSeries property.
     * The destination is build by convention, based on the ctp.baseAetName of the ctp properties file and the
     * edc code of the study. It reflects a pipeline of a ClinicalTrialProcessor (CTP) system that will process the data.
     */
    public void stageChosenDicomSeries() {
        if (this.selectedDicomSeries.size() == 0) {
            return;
        }

        String dicomPatientId;
        String patientPseudonym;
        String dicomStudyUid;
        boolean update;

        if (isInStudyZero()) {
            dicomPatientId = PatientIdentifierUtil.removePatientIdPrefix(this.selectedSubject.getStudySubjectId());
            patientPseudonym = this.selectedSubject.getUniqueIdentifier();
            dicomStudyUid = this.selectedDicomStudy.getClinicalStudyInstanceUid();

            if (dicomStudyUid.isEmpty()) {
                String errorMessage = "The clinical study identifier is empty. " +
                        "The study was probably uploaded from another system to study zero.";
                this.messageUtil.errorText(errorMessage);
            }

            update = this.selectedDicomStudy.hasStageOneRepresentation();
        } else {
            dicomPatientId = this.selectedSubject.getUniqueIdentifier();
            patientPseudonym = this.selectedSubject.getUniqueIdentifier();
            dicomStudyUid = this.selectedDicomStudy.getStudyInstanceUID();
            update = this.selectedDicomStudy.hasStageTwoRepresentation();
        }

        AuditEvent auditEvent = getPacsDataAuditEvent(update);

        String aetPrefix = this.ctpService.getBaseAetName();
        String edcCode = this.getCurrentStudyEdcCode();
        String uidPrefix = ctpService.getDicomUidPrefix();
        String partnerSideCode = this.mainBean.getMyAccount().getPartnerSite().getIdentifier();

        for (StagedDicomSeries series : this.selectedDicomSeries) {
            boolean moveSuccess;
            /**
             * StagedDicomSeriesVirtualSeries represent just a part of a series with the DicomImages that have
             * a reference to the same DICOM Series. Here we need to move image by image to exclude unwanted
             * images that would come with the full series.
             */
            if (isMoveCompleteSeries(series)) {
                if (isInStudyZero()) {
                    moveSuccess = triggerDicomSeriesMoveOnClinicalPacs(patientPseudonym, dicomPatientId, dicomStudyUid, auditEvent, aetPrefix, edcCode, uidPrefix, series);
                } else {
                    moveSuccess = triggerDicomSeriesMoveOnPacsService(patientPseudonym, dicomPatientId, dicomStudyUid, auditEvent, aetPrefix, edcCode, uidPrefix, partnerSideCode, series);
                }
                handleStagingSeriesErrors(series, moveSuccess);
            } else {
                if (series.getAvailableDicomImages() != null) {
                    for (DicomImage image : series.getAvailableDicomImages()) {

                        if (isInStudyZero()) {
                            moveSuccess = triggerDicomSopInstanceMoveOnClinicalPacs(patientPseudonym, dicomPatientId, dicomStudyUid, series.getSeriesInstanceUID(), image.getSopInstanceUID(), auditEvent, aetPrefix, edcCode, uidPrefix, partnerSideCode);
                        } else {
                            moveSuccess = triggerDicomSopInstanceMoveOnPacsService(patientPseudonym, dicomPatientId, dicomStudyUid, series.getSeriesInstanceUID(), image.getSopInstanceUID(), auditEvent, aetPrefix, edcCode, uidPrefix, partnerSideCode);
                        }
                        handleStagingImageErrors(image, moveSuccess);
                    }
                }
            }
        }

    }

    /**
     * Decides if a series can be moved complete as it is or if it needs to be moved image by image
     *
     * @param stagedDicomSeries series that should be moved
     * @return boolean true if the series can be moved complete
     */
    private boolean isMoveCompleteSeries(StagedDicomSeries stagedDicomSeries) {

        if (stagedDicomSeries instanceof StagedDicomSeriesVirtualSeries) {
            return ((StagedDicomSeriesVirtualSeries) stagedDicomSeries).isOriginal();
        }

        return true;
    }

    /**
     * Verifies that all identifier are present to allow to stage the selected study
     *
     * @return Study can be staged
     */
    public boolean selectedStudyCanBeStaged() {

        if (this.selectedDicomStudy == null) {
            return false;
        }

        String dicomStudyUid = "";

        if (isInStudyZero()) {
            dicomStudyUid = this.selectedDicomStudy.getClinicalStudyInstanceUid();

            if (dicomStudyUid.isEmpty()) {
                String errorMessage = "The clinical study identifier is empty. " +
                        "The study was probably uploaded from another system to study zero.";
                messageUtil.errorText(errorMessage);
                return false;
            } else {
                dicomStudyUid = this.selectedDicomStudy.getStudyInstanceUID();
                if (dicomStudyUid.isEmpty()) {
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * Triggers a move request for the DicomStudy on the corresponding Pacs system, based on the selectedDicomStudy property.
     * The destination is build by convention, based on the ctp.baseAetName of the ctp properties file and the
     * edc code of the study. It reflects a pipeline of a ClinicalTrialProcessor (CTP) system that will process the data.
     */
    public void stageCompleteDicomStudy() {
        String aetPrefix = this.ctpService.getBaseAetName();
        String uidPrefix = ctpService.getDicomUidPrefix();
        String edcCode = this.getCurrentStudyEdcCode();
        String partnerSideCode = this.mainBean.getMyAccount().getPartnerSite().getIdentifier();
        boolean moveSuccess;

        if (isInStudyZero()) {
            moveSuccess = triggerDicomStudyMoveOnClinicalPacs(aetPrefix, uidPrefix, edcCode);
        } else {
            moveSuccess = triggerDicomStudyMoveOnPacsService(aetPrefix, uidPrefix, edcCode, partnerSideCode);
        }

        handleStagingStudyErrors(moveSuccess);

    }

    /**
     * Checks if the active study is Study-0
     *
     * @return boolean
     */
    public boolean isInStudyZero() {
        if (this.getCurrentStudyEdcCode() == null) {
            return false;
        }
        return this.getCurrentStudyEdcCode().equals(study0EdcCode);
    }

    /**
     * On tab change event listener
     *
     * @param event TabChangeEvent triggered on changing the tab in the view
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws JSONException
     */
    public void onDicomStagedSeriesDialogFormTabViewTabChange(TabChangeEvent event) throws
            InterruptedException, ExecutionException, JSONException {
        if (event != null) {
            Tab tab = event.getTab();
            if (tab.getId().equals("dicomStagedSeriesDialogFormTreeTableTab")) {
                this.updateSeriesTreeWithDetailsIfNecessary();
            }
        }
    }

    /**
     * UI helper
     *
     * @return true if one or more DicomSeriesNodes are selected
     */
    public boolean hasSelectedDicomSeriesNodes() {
        if (this.selectedDicomSeriesNodes == null) {
            return false;
        }

        if (this.selectedDicomSeriesNodes.length == 0) {
            return false;
        }

        return true;
    }

    /**
     * Triggers creating and returning an archive of Dicom study data, based on the selected StagedDicomStudy
     * It will use the stage one representation if the user is in Study Zero. Otherwise it will use the study specific representation
     * which is also called stage two representation.
     */
    public void downloadDicomStudy() {
        String studyUid;
        if (isInStudyZero()) {
            studyUid = this.selectedDicomStudy.getStageOneStudyInstanceUid();
        } else {
            studyUid = this.selectedDicomStudy.getStageTwoStudyInstanceUid();
        }

        this.searchDicomSeriesOnPacs();
        Integer size = this.stagedDicomSeries.size();

        String dicomPatientId = this.selectedSubject.getUniqueIdentifier();

        this.downloadDicomStudy(dicomPatientId, studyUid, size);


    }

    /**
     * Triggers creating and returning an archive of Dicom series data, based on the selected StagedDicomSeries.
     * It will use the stage one representation if the user is in Study Zero. Otherwise it will use the study specific
     * representation which is also called stage two representation.
     */
    public void downloadDicomSeries() {
        String studyUid;
        String seriesUid;

        if (isInStudyZero()) {
            studyUid = this.selectedDicomStudy.getStageOneStudyInstanceUid();
            seriesUid = this.selectedStagedDicomSeries.getStageOneSeriesUid();
        } else {
            studyUid = this.selectedDicomStudy.getStageTwoStudyInstanceUid();
            seriesUid = this.selectedStagedDicomSeries.getStageTwoSeriesUid();
        }

        String dicomPatientId = this.selectedSubject.getUniqueIdentifier();

        this.downloadDicomSeries(dicomPatientId, studyUid, seriesUid);
    }

    /**
     * Loads Studies from EDC system and filters for Study sites. The result excludes the Study 0.
     *
     * @return List<de.dktk.dd.rpb.core.domain.edc.Study> EDC StudySite List
     */
    public List<de.dktk.dd.rpb.core.domain.edc.Study> getAllEdcStudySitesExceptStudyZero() {
        String partnerSideIdentifier = this.mainBean.getMyAccount().getPartnerSite().getIdentifier();
        List<Study> studyList = this.mainBean.getStudyIntegrationFacade().loadStudies();
        List<de.dktk.dd.rpb.core.domain.edc.Study> studySites = new ArrayList<>();
        for (Study study : studyList) {
            if (!study.getOcStudyIdentifier().equals(study0Identifier) && study.getPartnerSite().getIdentifier().equals(partnerSideIdentifier)) {
                de.dktk.dd.rpb.core.domain.edc.Study edcStudy = study.getEdcStudy();
                List<de.dktk.dd.rpb.core.domain.edc.Study> sites = study.getEdcStudy().getStudySites();

                if (edcStudy.isMultiCentre()) {
                    for (de.dktk.dd.rpb.core.domain.edc.Study site : sites) {
                        if (site.getUniqueIdentifier().startsWith(partnerSideIdentifier + RPB_IDENTIFIERSEP)) {
                            studySites.add(site);
                        }
                    }
                } else {
                    studySites.addAll(sites);
                }
            }
        }
        return studySites;
    }

    /**
     * Loads all subjects of a specific study site, based on the selectedEdcStudy property
     */
    public void searchStudySpecificSubjects() {
        if (this.selectedEdcStudy != null) {
            List<StudySubject> subjects = this.mainBean.getStudyIntegrationFacade().loadStudySubjects(this.selectedEdcStudy);
            List<StudySubject> filteredSubjects = StudySubjectListUtil.filterByStudySubjectList(this.studySubjectsList, subjects);
            this.performDicomBulkSearch(filteredSubjects);
        }

    }

    //endregion

    //region Overrides

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
            DataTableUtil.addSortOrder(results, colPath, "seriesUidColumn", SortOrder.ASCENDING);
        }

        return results;
    }

    // endregion

    //region Private

    private void initializeVisibleComponentMap() {
        this.columnVisibilityList = new ArrayList<>();
        this.columnVisibilityList.add(Boolean.TRUE);  // RowIndex
        this.columnVisibilityList.add(Boolean.TRUE);  // PID
        this.columnVisibilityList.add(Boolean.TRUE);  // StudySubjectId
        this.columnVisibilityList.add(Boolean.FALSE); // SecondaryId
        this.columnVisibilityList.add(Boolean.TRUE);  // Sex
        this.columnVisibilityList.add(Boolean.TRUE);  // EntryDate
    }

    private void handleDicomSearchException(Exception e) {
        String errorMessage = "There was a problem processing the search request.";
        String userAdvice = "Please try again";
        messageUtil.error(errorMessage + " " + userAdvice, e);
        log.error(errorMessage, e);
    }

    private void performDicomBulkSearch(Set<StudySubject> subjects) {
        this.inputStudySubjectList.clear();
        this.inputStudySubjectList.addAll(subjects);
        this.searchDicomDataByStudySubjectList();
    }

    private void performDicomBulkSearch(List<StudySubject> subjects) {
        this.inputStudySubjectList.clear();
        this.inputStudySubjectList.addAll(subjects);
        this.searchDicomDataByStudySubjectList();
    }

    private Set<StudySubject> getMatchingStudySubjects(List<String> dividedIdValues) {
        Set<StudySubject> subjects = new HashSet<>();
        List<String> notMatchingItems = new ArrayList<>();

        for (String id : dividedIdValues) {
            id = id.trim();
            if (!id.isEmpty()) {
                List<StudySubject> filteredSubjects = this.filterMatchingStudySubjects(id);
                if (filteredSubjects.isEmpty()) {
                    notMatchingItems.add(id);
                } else {
                    subjects.addAll(filteredSubjects);
                }
            }
        }

        if (!notMatchingItems.isEmpty()) {
            messageUtil.warningText("Could not find matching subjects for: " + notMatchingItems.toString());
        }
        return subjects;
    }

    private List<String> splitBulkInputBySeparatorValues(String regex) {
        String[] parts = this.inputStudySubjectBulkString.split(regex);
        // divided id values
        return new ArrayList<>(Arrays.asList(parts));
    }

    private StagedDicomStudyBuilder getStagedDicomStudyInformation(String uidPrefix, StagedSubject researchSubject) {
        StagedDicomStudyBuilder stagedDicomStudyBuilder;
        stagedDicomStudyBuilder = StagedDicomStudyBuilder.getInstance(
                researchSubject.getDicomStudyList(), uidPrefix, this.getCurrentStudyEdcCode());
        stagedDicomStudyBuilder.filterFirstStageStudies();

        if (!isInStudyZero()) {
            stagedDicomStudyBuilder.filterSecondStageStudiesByEdcCode();
        }

        if (this.clinicalSubjectList != null && this.clinicalSubjectList.size() > 0) {
            String clinicalPid = PatientIdentifierUtil.removePatientIdPrefix(researchSubject.getStudySubjectId());
            Subject clinicalSubject = getFirstMatchingSubjectByPid(this.clinicalSubjectList, clinicalPid);
            if (clinicalSubject != null) {
                stagedDicomStudyBuilder.setClinicalStudyList(clinicalSubject.getDicomStudyList());
            }
        }
        return stagedDicomStudyBuilder;
    }

    private void buildStagedStudySubjectList(List<StudySubject> queriedStudySubjectsList) {
        StagedSubjectPacsResultBuilder stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(
                queriedStudySubjectsList, resultSubjectList);
        stagedSubjectPacsResultBuilder.
                filterResultsByStudySubjectList().
                addSubjectsWithoutResultsFromStudySubjectList();
        this.stagedSubjects = stagedSubjectPacsResultBuilder.getStagedSubjects();
    }

    private void requirePatientDataFromResearchPacs() throws Exception {
        log.debug("Query patient data from PacsService");
        this.resultSubjectList = this.mainBean.getPacsService().loadPatients(this.inputStudySubjectList);
    }

    private void requirePatientDataFromClinicalPacs() throws Exception {
        List<StudySubject> requestedSubjects = new ArrayList<>();
        for (StudySubject subject : this.inputStudySubjectList) {
            StudySubject requestSubject = new StudySubject();
            requestSubject.setPid(PatientIdentifierUtil.removePatientIdPrefix(subject.getStudySubjectId()));
            requestedSubjects.add(requestSubject);
        }
        log.debug("Query patient data from clinical PacsService");
        this.clinicalSubjectList = this.mainBean.getClinicalPacsService().loadPatients(requestedSubjects);
    }

    private Subject getFirstMatchingSubjectByPid(List<Subject> subjectList, String pid) {
        for (Subject subject : subjectList) {
            if (pid.equals(subject.getUniqueIdentifier())) {
                return subject;
            }
        }
        return null;
    }

    private DicomStudy getStageTwoDicomStudyFromPacs(String dicomPatientId) {
        String secondStageStudyUid = selectedDicomStudy.getStageTwoStudyInstanceUid();
        if (!secondStageStudyUid.isEmpty()) {
            if (validatePatientId(dicomPatientId)) {
                return this.mainBean.getPacsService().
                        loadPatientStudy(dicomPatientId, secondStageStudyUid);
            }
        }
        return getEmptyDicomStudy();
    }

    private DicomStudy getStageOneDicomStudyFromPacs(String dicomPatientId) {
        String firstStageStudyUid = selectedDicomStudy.getStageOneStudyInstanceUid();
        if (!firstStageStudyUid.isEmpty()) {
            if (validatePatientId(dicomPatientId)) {
                return this.mainBean.getPacsService().loadPatientStudy(dicomPatientId, firstStageStudyUid);
            }
        }
        return getEmptyDicomStudy();
    }

    private DicomStudy getClinicalDicomStudyFromPacs(String clinicalStudyUid) {
        String studySubjectId = selectedSubject.getStudySubjectId();
        String hospitalPatientId = PatientIdentifierUtil.removePatientIdPrefix(studySubjectId);
        if (PatientIdentifierUtil.validateHospitalPatientId(hospitalPatientId)) {
            return this.mainBean.getClinicalPacsService().loadPatientStudy(hospitalPatientId, clinicalStudyUid);
        }
        return getEmptyDicomStudy();
    }

    private boolean validatePatientId(String patientId) {
        return !patientId.isEmpty();
    }

    private DicomStudy getEmptyDicomStudy() {
        DicomStudy stageTwoStudy;
        stageTwoStudy = new DicomStudy();
        stageTwoStudy.setStudySeries(new ArrayList<DicomSeries>());
        return stageTwoStudy;
    }

    private void updateSelectedStagedStudyWithSeriesInformation(DicomStudy stageOneStudy) {
        if (stageOneStudy.getStudySeries() != null && this.selectedDicomStudy != null) {
            this.selectedDicomStudy.setStudySeries(stageOneStudy.getStudySeries());
        }
    }

    private void handleStagingSeriesErrors(StagedDicomSeries series, boolean moveSuccess) {
        if (!moveSuccess) {
            String errorMessage = "There was a problem staging the series";
            if (series != null) {
                errorMessage += series.toString();
            }
            log.error(errorMessage);
            messageUtil.errorText(errorMessage);
        }
    }

    private void handleStagingImageErrors(DicomImage image, boolean moveSuccess) {
        if (!moveSuccess) {
            String errorMessage = "There was a problem staging the image";
            if (image != null) {
                errorMessage += image.toString();
            }
            log.error(errorMessage);
            messageUtil.errorText(errorMessage);
        }
    }

    private boolean triggerDicomSeriesMoveOnPacsService(String patientPseudonym, String dicomPatientId, String
            dicomStudyUid, AuditEvent auditEvent, String aetPrefix, String edcCode, String uidPrefix, String
                                                                partnerSideCode, StagedDicomSeries series) {
        this.auditLogService.event(auditEvent, "DicomSeries", this.rpbStudy.getProtocolId() + "/" + patientPseudonym, DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, dicomStudyUid) + " / " + DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, series.getSeriesInstanceUID()));
        return this.mainBean.getPacsService().moveDicomSeries(dicomPatientId, dicomStudyUid, series.getSeriesInstanceUID(), aetPrefix + edcCode);
    }

    private boolean triggerDicomSeriesMoveOnClinicalPacs(String patientPseudonym, String dicomPatientId, String
            dicomStudyUid, AuditEvent auditEvent, String aetPrefix, String edcCode, String uidPrefix, StagedDicomSeries
                                                                 series) {
        this.auditLogService.event(auditEvent, "DicomSeries", this.rpbStudy.getProtocolId() + "/" + patientPseudonym, DicomUidReGeneratorUtil.generateStageOneUid(uidPrefix, dicomStudyUid) + " / " + DicomUidReGeneratorUtil.generateStageOneUid(uidPrefix, series.getSeriesInstanceUID()));
        return this.mainBean.getClinicalPacsService().moveDicomSeries(dicomPatientId, dicomStudyUid, series.getSeriesInstanceUID(), aetPrefix + edcCode);
    }

    private boolean triggerDicomSopInstanceMoveOnPacsService(String patientPseudonym, String dicomPatientId, String dicomStudyUid, String dicomSeriesUid, String dicomSopInstanceId, AuditEvent auditEvent, String aetPrefix, String edcCode, String uidPrefix, String partnerSideCode) {
        this.auditLogService.event(auditEvent, "SOP Instance", this.rpbStudy.getProtocolId() + "/" + patientPseudonym, DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, dicomStudyUid) + " / " + DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, dicomSeriesUid) + " / " + DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, dicomSopInstanceId));
        return this.mainBean.getPacsService().moveDicomSopInstance(dicomPatientId, dicomStudyUid, dicomSeriesUid, dicomSopInstanceId, aetPrefix + edcCode);
    }

    private boolean triggerDicomSopInstanceMoveOnClinicalPacs(String patientPseudonym, String dicomPatientId, String dicomStudyUid, String dicomSeriesUid, String dicomSopInstanceId, AuditEvent auditEvent, String aetPrefix, String edcCode, String uidPrefix, String partnerSideCode) {
        this.auditLogService.event(auditEvent, "SOP Instance", this.rpbStudy.getProtocolId() + "/" + patientPseudonym, DicomUidReGeneratorUtil.generateStageOneUid(uidPrefix, dicomStudyUid) + " / " + DicomUidReGeneratorUtil.generateStageOneUid(uidPrefix, dicomSeriesUid) + " / " + DicomUidReGeneratorUtil.generateStageOneUid(uidPrefix, dicomSopInstanceId));
        return this.mainBean.getClinicalPacsService().moveDicomSopInstance(dicomPatientId, dicomStudyUid, dicomSeriesUid, dicomSopInstanceId, aetPrefix + edcCode);
    }

    private AuditEvent getPacsDataAuditEvent(Boolean update) {
        if (update) {
            return AuditEvent.PACSDataModification;
        }
        return AuditEvent.PACSDataCreation;
    }

    private boolean triggerDicomStudyMoveOnPacsService(String aetPrefix, String uidPrefix, String edcCode, String
            partnerSideCode) {
        String dicomPatientId = selectedSubject.getUniqueIdentifier();
        String dicomStudyUid = selectedDicomStudy.getStudyInstanceUID();
        Boolean update = selectedDicomStudy.hasStageTwoRepresentation();
        AuditEvent auditEvent = getPacsDataAuditEvent(update);
        this.auditLogService.event(auditEvent, "DicomStudy", this.rpbStudy.getProtocolId() + "/" + dicomPatientId, DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, dicomStudyUid));
        return this.mainBean.getPacsService().moveDicomStudy(dicomPatientId, dicomStudyUid, aetPrefix + edcCode);
    }

    private boolean triggerDicomStudyMoveOnClinicalPacs(String aetPrefix, String uidPrefix, String edcCode) {
        String dicomPatientId = PatientIdentifierUtil.removePatientIdPrefix(selectedSubject.getStudySubjectId());
        String patientPseudonym = selectedSubject.getUniqueIdentifier();
        String dicomStudyUid = selectedDicomStudy.getClinicalStudyInstanceUid();
        boolean update = selectedDicomStudy.hasStageOneRepresentation();
        AuditEvent auditEvent = getPacsDataAuditEvent(update);
        this.auditLogService.event(auditEvent, "DicomStudy", this.rpbStudy.getProtocolId() + "/" + patientPseudonym, DicomUidReGeneratorUtil.generateStageOneUid(uidPrefix, dicomStudyUid));
        return this.mainBean.getClinicalPacsService().moveDicomStudy(dicomPatientId, dicomStudyUid, aetPrefix + edcCode);
    }


    private void handleStagingStudyErrors(boolean moveSuccess) {
        if (!moveSuccess) {
            String errorMessage = "There was a problem staging the study";
            log.error(errorMessage);
            messageUtil.errorText(errorMessage);
        }
    }

    private void downloadDicomStudy(String dicomPatientId, String dicomStudyUid, Integer verifySeriesCount) {
        try {
            InputStream is = this.mainBean.getSvcWebApi().pacsCreateStudyArchive(
                    dicomPatientId,
                    dicomStudyUid,
                    verifySeriesCount,
                    mainBean.getMyAccount().getApiKey()
            );

            if (is != null) {
                String filename = dicomPatientId + "-" + dicomStudyUid + ".zip";
                Faces.sendFile(is, filename, true);
            } else {
                String errorMessage = "Download of DICOM study failed." + " PatientId: " + dicomPatientId +
                        " StudyUid: " + dicomStudyUid;
                log.error(errorMessage);
                this.messageUtil.error(errorMessage);
            }

        } catch (Exception err) {
            log.error(err.getMessage(), err);
            this.messageUtil.error(err);
        }
    }

    private void downloadDicomSeries(String dicomPatientId, String studyUid, String seriesUid) {
        try {
            InputStream is = this.mainBean.getSvcWebApi().pacsCreateSeriesArchive(
                    dicomPatientId,
                    studyUid,
                    seriesUid,
                    mainBean.getMyAccount().getApiKey()
            );

            if (is != null) {
                String filename = dicomPatientId + "-" + seriesUid + ".zip";
                Faces.sendFile(is, filename, true);
            } else {
                String errorMessage = "Download of DICOM series failed. " + " PatientId: " + dicomPatientId +
                        " StudyUid: " + studyUid + " SeriesUid: " + seriesUid;
                this.log.error(errorMessage);
                this.messageUtil.error(errorMessage);
            }

        } catch (Exception err) {
            this.log.error(err.getMessage(), err);
            this.messageUtil.error(err);
        }

    }

    //endregion

}
