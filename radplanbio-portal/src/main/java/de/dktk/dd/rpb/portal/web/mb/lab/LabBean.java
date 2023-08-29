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

package de.dktk.dd.rpb.portal.web.mb.lab;

import com.github.sardine.impl.SardineException;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.domain.edc.StudyParameterConfiguration;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.Lab;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.handler.lab.ILabKeyWebdavHandler;
import de.dktk.dd.rpb.core.handler.lab.LabKeyWebDavSardineHandler;
import de.dktk.dd.rpb.core.repository.lab.ILabRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.LabKeyService;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.FilePathUtil;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Bean for administration of LAB entities
 */
@Named("mbLab")
@Scope(value="view")
public class LabBean extends CrudEntityViewModel<Lab, Integer> {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(LabBean.class);

    //endregion

    //region Injects

    private final MainBean mainBean;
    private final LabKeyService labKeyService;
    private final StudyIntegrationFacade studyIntegrationFacade;

    private final AuditLogService auditLogService;

    // endregion

    //region Repository

    private final ILabRepository repository;

    /**
     * Get LabRepository
     *
     * @return LabRepository
     */
    @Override
    public ILabRepository getRepository() {
        return this.repository;
    }

    // endregion

    //region Members

    private Study activeStudy;
    private Study parentStudy;

    private LabKeyExportConfiguration labKeyExportConfiguration;

    private String siteIdentifier;
    private String siteModifier;
    private String edcCode;
    private StudyParameterConfiguration studyConfiguration;
    // path where the portal expects odm files
    private String dataPath;
    private File odmFolder;

    //endregion

    //region Constructors

    @Inject
    public LabBean(ILabRepository repository,
                   MainBean mainBean,
                   LabKeyService labKeyService,
                   StudyIntegrationFacade studyIntegrationFacade, AuditLogService auditLogService) {

        this.repository = repository;
        this.mainBean = mainBean;
        this.labKeyService = labKeyService;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.auditLogService = auditLogService;
    }

    //endregion

    // region Properties

    public LabKeyExportConfiguration getLabKeyExportConfiguration() {
        return labKeyExportConfiguration;
    }

    public void setLabKeyExportConfiguration(LabKeyExportConfiguration labKeyExportConfiguration) {
        this.labKeyExportConfiguration = labKeyExportConfiguration;
    }

    // endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        // Do not trigger load when is AJAX post back that refreshes the partial component view
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.load();
        }
    }

    //endregion

    //region Commands

    public void prepareNewEntity(PartnerSite site) {
        this.prepareNewEntity();
        if (site != null) {
            site.setLab(this.newEntity);
        }
    }

    /**
     * Initializes the properties in post construct phase of the bean
     */
    public void load() {
        // Init Facade to use service that are valid for logged user PartnerSite (from mainBean)
        this.studyIntegrationFacade.init(this.mainBean);
        this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);
        de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy = this.studyIntegrationFacade.loadStudyWithMetadata();

        // prepare study properties that are relevant for the export
        this.edcCode = rpbStudy.getTagValue("EDC-code");
        this.dataPath = mainBean.getLocalPortal().getDataPath();
        this.siteIdentifier = rpbStudy.getEdcStudy().extractPartnerSiteIdentifier();
        this.siteModifier = rpbStudy.getEdcStudy().extractStudySiteModifier();
        this.studyConfiguration = rpbStudy.getEdcStudy().getMetaDataVersion().getStudyDetails().getStudyParameterConfiguration();

        this.labKeyExportConfiguration = new LabKeyExportConfiguration(this.studyConfiguration);

        this.activeStudy = this.mainBean.getActiveStudy();
        this.parentStudy = this.activeStudy.getParentStudy() != null ?
                this.activeStudy.getParentStudy() : this.activeStudy;

        this.odmFolder = getFirstMostSpecificOdmFilePathThatExistsOrNull();

    }

    /**
     * Iterates on an array of possible ODM file path and returns the first one that exists.
     *
     * @return File file path to the ODM export files of the study
     */
    private File getFirstMostSpecificOdmFilePathThatExistsOrNull() {
        for (String candidatePath : this.getRelativeOdmFilePathCandidates()) {
            File candidate = this.getOdmFilePathObject(candidatePath);
            if (candidate.exists()) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Triggers the upload of data for the active study to the LabKey pipeline folder.
     */
    public void updateLabKeyData() {

        List<de.dktk.dd.rpb.core.domain.edc.StudySubject> studySubjectList = this.getStudySubjects();
        //filterEventsThatAreNotAvailable(studySubjectList);
        ILabKeyWebdavHandler webdavHandler = this.getLabKeyWebDavHandler();
        Odm odm = null;

        try {
            odm = getOdmFileContentFromZip();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            this.messageUtil.error(e);
        }

        if (odm != null) {

            try {
                odm.updateHierarchy();
                odm.setParentStudy(this.parentStudy);
                checkOdmProperties(odm);
                labKeyService.handleUpdate(webdavHandler, studySubjectList, odm, this.labKeyExportConfiguration);
                this.auditLogService.event(AuditEvent.LABOdmReload, Constants.ODM, this.activeStudy.getUniqueIdentifier(), odm.getFileOid());
                RequestContext.getCurrentInstance().execute("PF('reloadLabkeyStudyDataWizard').loadStep(PF('reloadLabkeyStudyDataWizard').cfg.steps[3])");
            } catch (MissingPropertyException | ParseException e) {
                log.error(e.getMessage(), e);
                this.messageUtil.error(e);
            } catch (JAXBException e) {
                String message = "There is a problem unmarshalling the ODM.";
                log.error(message, e);
                this.messageUtil.error(message, e);
            } catch (IOException e) {
                String message = "There is a problem reading a file or folder.";
                log.error(message, e);
                this.messageUtil.error(message, e);
            }
        } else {
            // loading ODM failed - go back to the original tab.
            RequestContext.getCurrentInstance().execute("PF('reloadLabkeyStudyDataWizard').loadStep(PF('reloadLabkeyStudyDataWizard').cfg.steps[2])");
        }
    }


    /***
     * This function iterates on the list of study subjects and filters out all
     * events that do not have the status "available".
     * @param studySubjectList studySubjectList
     */
    private void filterEventsThatAreNotAvailable(List<StudySubject> studySubjectList) {
        for (StudySubject studySubject : studySubjectList) {
            if (studySubject.getStudyEventDataList() != null) {
                List<EventData> eventList = new ArrayList<>();
                for (EventData event : studySubject.getStudyEventDataList()) {
                    if (event.getSystemStatus().equalsIgnoreCase("available")) {
                        eventList.add(event);
                    }
                }
                studySubject.setStudyEventDataList(eventList);
            }
        }
    }

    // TODO: Create a class that checks all necessary properties
    private void checkOdmProperties(Odm odm) throws MissingPropertyException {
        if (odm.findFirstStudyOrNone() == null) {
            throw new MissingPropertyException("The ODM file does not contain study information.");
        }
        if (odm.findFirstStudyOrNone().getMetaDataVersion() == null) {
            throw new MissingPropertyException("The ODM file does not contain MetaDataVersion information.");
        }
        if (odm.findFirstStudyOrNone().getMetaDataVersion().getStudyEventDefinitions() == null) {
            throw new MissingPropertyException("The ODM file does not contain StudyEventDefinitions information.");
        }
        if (odm.getClinicalDataList() == null) {
            throw new MissingPropertyException("The ODM file does not contain ClinicalData information.");
        }
    }

    /***
     * Tries to read an ODM export (zip) file from a predefined study specific folder. It picks the latest file
     * if there are more than one.
     *
     * @return Odm object with data from export
     */
    private Odm getOdmFileContentFromZip() throws IOException {
        if (this.odmFolder == null) {
            String message = "Update failed. Odm folder is not defined.";
            this.messageUtil.error(message);
            log.error(message);
            return null;
        }

        File odmExportFile = getLatestZipFileExport(this.odmFolder);

        log.debug("Open ODM file: " + odmExportFile.getAbsoluteFile());

        if (odmExportFile == null) {
            String message = "Update failed. Probably, there is no appropriate ODM export file (zip) in the import folder." + this.odmFolder;
            this.messageUtil.error(message);
            log.error(message);
            return null;
        }

        String zipFilePath = odmExportFile.getAbsolutePath();

        InputStream inputStream = getOdmFileInputStream(zipFilePath);

        if (inputStream == null) {
            String message = "There is a problem with the file. \"" + zipFilePath + "\".";
            log.debug(message);
            this.messageUtil.warning(message);
            return null;
        } else {
            return unmarshalOdmFromInputStream(inputStream, zipFilePath);
        }

    }

    private File getOdmFilePathObject(String odmFilePath) {
        return new File(FilePathUtil.verifyTrailingSlash(this.dataPath) + odmFilePath);
    }

    /**
     * Creates an array of possible relative file path that could consist the ODM file
     *
     * @return List<String> list of relative paths that could consist the ODM
     */
    private List<String> getRelativeOdmFilePathCandidates() {

        // start the array with the most specific path
        List<String> pathArray = new ArrayList<>();

        // multi centric study with additional site modifier
        if (!this.siteIdentifier.isEmpty() && !this.siteModifier.isEmpty()) {
            pathArray.add(
                    Constants.DATASETS_FOLDER + "/" +
                            this.edcCode + "-" +
                            this.siteIdentifier + "-" +
                            this.siteModifier +
                            Constants.EXPORT_ALL_SUFFIX + "/" +
                            Constants.ODM_FULL_EXPORT_FOLDER
            );
        }

        // multi centric study with site identifier only
        if (!this.siteIdentifier.isEmpty()) {
            pathArray.add(
                    Constants.DATASETS_FOLDER + "/" +
                            this.edcCode + "-" +
                            this.siteIdentifier +
                            Constants.EXPORT_ALL_SUFFIX + "/" +
                            Constants.ODM_FULL_EXPORT_FOLDER
            );
        }

        // parent study
        if (this.siteIdentifier.isEmpty()) {
            pathArray.add(
                    Constants.DATASETS_FOLDER + "/" +
                            this.edcCode + Constants.EXPORT_ALL_SUFFIX + "/" +
                            Constants.ODM_FULL_EXPORT_FOLDER
            );
        }

        return pathArray;
    }

    private InputStream getOdmFileInputStream(String zipFilePath) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getSingleOdmFileInputStream(this.edcCode, this.siteIdentifier, zipFilePath);
        } catch (IOException e) {
            String message = "There was a problem getting the ODM xml file input stream from the file: " + zipFilePath;
            this.messageUtil.error(message, e);
            log.error(e.getMessage(), e);
            closeStreamIfNotNull(inputStream);
        }
        return inputStream;
    }

    private void closeStreamIfNotNull(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private File getLatestZipFileExport(File file) {

        String[] fileList = {};
        if (file != null) {
            fileList = file.list();
        }

        File odmExportFile = null;
        long maxLastModified = 0L;

        for (String fileOrDirectoryName : fileList) {
            if (fileOrDirectoryName.contains(this.edcCode + "-" + this.siteIdentifier) && fileOrDirectoryName.endsWith(".zip")) {
                File odmFile = new File(FilePathUtil.verifyTrailingSlash(this.odmFolder.getPath()) + fileOrDirectoryName);
                if (odmFile.lastModified() > maxLastModified) {
                    maxLastModified = odmFile.lastModified();
                    odmExportFile = odmFile;
                }
            }
        }
        return odmExportFile;
    }

    private Odm unmarshalOdmFromInputStream(InputStream inputStream, String zipFilePath) throws IOException {
        Odm result = null;

        try {
            result = JAXBHelper.unmarshalInputstream2(Odm.class, inputStream);

        } catch (JAXBException e) {
            String message = "There was a problem during unmarshalling the xml content of the file: " + zipFilePath;
            log.error(message, e);
            this.messageUtil.error(message, e);
        } finally {
            closeStreamIfNotNull(inputStream);
        }
        return result;
    }

    private InputStream getSingleOdmFileInputStream(String edcCode, String identifier, String zipFilePath) throws
            IOException {
        ZipFile zip = new ZipFile(zipFilePath);
        InputStream inputStream = null;

        for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            String fileName = entry.getName();
            if (fileName.contains(edcCode + Constants.RPB_IDENTIFIERSEP + identifier) && fileName.endsWith(".xml"))
                inputStream = zip.getInputStream(entry);
        }

        if (inputStream == null) {
            String message = "The file \"" + zipFilePath + "\" does not contain an ODM export file file with \"" +
                    edcCode + Constants.RPB_IDENTIFIERSEP + identifier + "\" in the name and ends with .xml";

            log.debug(message);
            this.messageUtil.warning(message);
        }

        return inputStream;
    }

    private List<de.dktk.dd.rpb.core.domain.edc.StudySubject> getStudySubjects() {
        // Setup EDC study as query parameter
        de.dktk.dd.rpb.core.domain.edc.Study edcStudy = this.createEdcStudyQuery();

        if (activeStudy.getParentStudy() != null) {
            return this.studyIntegrationFacade.loadStudySubjectsWithEventsAndTreatmentGroups(edcStudy);
        } else { // Study is parent
            return this.studyIntegrationFacade.loadStudySubjectsOfChildrenStudiesWithEventsAndTreatmentGroups(edcStudy);
        }
    }

    private de.dktk.dd.rpb.core.domain.edc.Study createEdcStudyQuery() {

        // Setup EDC study as query parameter
        de.dktk.dd.rpb.core.domain.edc.Study edcStudy = new de.dktk.dd.rpb.core.domain.edc.Study();

        edcStudy.setUniqueIdentifier(this.activeStudy.getUniqueIdentifier());

        return edcStudy;
    }

    public Date getExportPathModificationDate() {
        try {
            return labKeyService.getExportPathModificationDate(this.getLabKeyWebDavHandler());
        } catch (IOException e) {
            String errorMessage = "There was a problem getting the date of the last export path modification on LabKey.";
            messageUtil.error(errorMessage, e);
            log.error(errorMessage, e);
        }
        return null;
    }

    /**
     * Validates that there is a corresponding export folder on the LabKey server.
     *
     * @return boolean true if export path exists
     */
    public boolean checkExportPathExistsOnLabKey() {
        if (checkProjectExistsOnLabKey()) {
            try {
                return labKeyService.checkExportPathExists(this.getLabKeyWebDavHandler());
            } catch (IOException e) {
                String errorMessage = "There was a problem checking that the export path exists on LabKey.";
                messageUtil.error(errorMessage, e);
                log.error(errorMessage, e);
            }
        }
        return false;
    }

    /**
     * Verifies that an ODM export file exists
     *
     * @return boolean - ODM export file exists in path
     */
    public boolean checkLocalOdmExportFile() {

        // this.odmFolder is null if path does not exist
        if (this.odmFolder != null) {
            // check if the path contains at least a file that matches the file name pattern for an ODM file export
            for (String pathName : this.odmFolder.list()) {
                if (pathName.contains(this.edcCode + "-" + this.siteIdentifier) && pathName.endsWith(".zip")) {
                    return true;
                }
            }
        }
        // default is false
        return false;
    }

    /**
     * Validates that there is a corresponding export folder on the LabKey server.
     *
     * @return boolean true if export path exists
     */
    public long getLatestExportPathUpdateIntervalInHours() {
        Date today = new Date();
        long diff = today.getTime() - getExportPathModificationDate().getTime();
        return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
    }

    /**
     * Validates that there is a corresponding project setup for the study on the LabKey server.
     *
     * @return boolean true if project path exists
     */
    public boolean checkProjectExistsOnLabKey() {
        try {
            return labKeyService.checkStudyFilePathExists(this.getLabKeyWebDavHandler());
        } catch (IOException e) {
            if (e instanceof SardineException) {
                int status = ((SardineException) e).getStatusCode();

                String basicErrorMessage = "There was a problem connecting to LabKey. Got response code: "
                        + status + ". ";
                String errorMessage = "";

                switch (status) {
                    case 401:
                        errorMessage = "The server response code indicates that the authentication failed.";
                        break;
                    case 403:
                        errorMessage = "The server response code indicates that you need additional permissions to perform the request";
                        break;
                }

                messageUtil.errorText(basicErrorMessage + errorMessage);
            } else {
                messageUtil.error(e);
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    private ILabKeyWebdavHandler getLabKeyWebDavHandler() {
        String labKeyUrl = this.mainBean.getMyAccount().getPartnerSite().getLab().getBaseUrl();
        return new LabKeyWebDavSardineHandler(this.activeStudy, labKeyUrl);
    }

    public String onFlowProcess(FlowEvent event) {
        return event.getNewStep();
    }

    /**
     * Logic for the "reloadLabKeyStudyDataWizard"
     *
     * @return String Id of the active wizard tab depending on the state on LabKey side.
     */
    public String getStepIndex() {
        if (!checkProjectExistsOnLabKey() || !checkLocalOdmExportFile()) return "studyExistsTab";

        // the export should not be older than one hour
        if (checkExportPathExistsOnLabKey() && this.getLatestExportPathUpdateIntervalInHours() >= 1) {
            return "exportPathExistsTab";
        }

        if (!checkExportPathExistsOnLabKey()) return "exportPathExistsTab";

        return "uploadDataTab";
    }

    public String analyseLabKeyConnection() {
        ILabKeyWebdavHandler webdavHandler = this.getLabKeyWebDavHandler();
        return labKeyService.analyseLabKeyConnection(webdavHandler);
    }

    public String analyseOdmExportFileStatus() {
        if (this.odmFolder == null) {
            return "There is no appropriate file path with an ODM export file. We checked for: " +
                    String.join(" or ", this.getRelativeOdmFilePathCandidates()) +
                    " in " + this.dataPath + ".";
        }

        for (String pathName : this.odmFolder.list()) {
            if (pathName.contains(this.edcCode + "-" + this.siteIdentifier) && pathName.endsWith(".zip")) {
                return "";
            }
        }

        return "There is no local ODM export file in \"" +
                this.odmFolder.getAbsolutePath() +
                "\". Please verify that there is an appropriate ODM export zip file.";
    }

    public String analyseExportState() {
        ILabKeyWebdavHandler webdavHandler = this.getLabKeyWebDavHandler();
        return labKeyService.analyseExportState(webdavHandler);
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        return new ArrayList<>();
    }

    //endregion

}
