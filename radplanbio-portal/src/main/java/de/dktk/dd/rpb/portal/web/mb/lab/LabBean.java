/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.Lab;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.handler.lab.ILabKeyWebdavHandler;
import de.dktk.dd.rpb.core.handler.lab.LabKeyWebDavSardineHandler;
import de.dktk.dd.rpb.core.repository.admin.IPartnerSiteRepository;
import de.dktk.dd.rpb.core.repository.lab.ILabRepository;
import de.dktk.dd.rpb.core.service.LabKeyService;
import de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil;
import de.dktk.dd.rpb.core.util.FilePathUtil;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.SortMeta;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static de.dktk.dd.rpb.core.util.Constants.*;

/**
 * Bean for administration of LAB entities
 *
 * @author tomas@skripcak.net
 * @since 30 Aug 2017
 */
@Named("mbLab")
@Scope(value = "view")
public class LabBean extends CrudEntityViewModel<Lab, Integer> {

    private static final Logger log = Logger.getLogger(LabBean.class);

    //region Injects
    private final MainBean mainBean;
    private final LabKeyService labkeyService;
    private final StudyIntegrationFacade studyIntegrationFacade;
    private final IPartnerSiteRepository partnerSiteRepository;

    // endregion

    //region Repository

    private final ILabRepository repository;

    // endregion

    private de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy;

    /**
     * Get LabRepository
     *
     * @return LabRepository
     */
    @Override
    public ILabRepository getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public LabBean(ILabRepository repository, MainBean mainBean, IPartnerSiteRepository partnerSiteRepository, LabKeyService labkeyService, StudyIntegrationFacade studyIntegrationFacade) {
        this.repository = repository;
        this.mainBean = mainBean;
        this.partnerSiteRepository = partnerSiteRepository;
        this.labkeyService = labkeyService;
        this.studyIntegrationFacade = studyIntegrationFacade;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        this.load();

    }

    //endregion

    //region Commands

    public void prepareNewEntity(PartnerSite site) {
        this.prepareNewEntity();
        if (site != null) {
            site.setLab(this.newEntity);
        }
    }

    public void load() {
        // Init Facade to use service that are valid for logged user PartnerSite (from mainBean)
        this.studyIntegrationFacade.init(this.mainBean);
        this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);
        this.rpbStudy = this.studyIntegrationFacade.loadStudyWithMetadata();
    }

    /**
     * Triggers the upload of data for the active study to the Labkey pipeline folder.
     */
    public void updateLabkeyData() {

        List<de.dktk.dd.rpb.core.domain.edc.StudySubject> studySubjectList = this.getStudySubjects();
        filterEventsThatAreNotAvailable(studySubjectList);

        ILabKeyWebdavHandler webdavHandler = this.getLabkeyWebDavHandler();
        Odm odm = getOdmFileContentFromZip();

        if (odm != null) {

            try {
                checkOdmProperties(odm);
                labkeyService.handleUpdate(webdavHandler, studySubjectList, odm);
            } catch (MissingPropertyException e) {
                log.error(e);
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
        }
    }


    /***
     * This function iterates on the list of study subjects and filters out all
     * events that do not have the status "available".
     * @param studySubjectList
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

    //    TODO: Create a class that checks all necessary properties
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
    private Odm getOdmFileContentFromZip() {
        String identifierActiveStudy = this.mainBean.getActiveStudy().getUniqueIdentifier();

        String edcCode = this.rpbStudy.getTagValue("EDC-code");
        String identifier = DicomStudyDescriptionEdcCodeUtil.getSiteCodePrefix(identifierActiveStudy);

        String dataPath = mainBean.getLocalPortal().getDataPath();

        String odmFilePath = DATASETS_FOLDER + "/" + edcCode + "-" + identifier + EXPORT_ALL_SUFFIX + "/" + ODM_FULL_EXPORT_FOLDER;
        File filePathObject = new File(FilePathUtil.verifyTrailingSlash(dataPath) + odmFilePath);

        File odmExportFile = getLatestZipFileExport(edcCode, identifier, dataPath, filePathObject);

        if (odmExportFile == null) {
            String message = "Update failed. Probably, there is no appropriate ODM export file (zip) in the import folder." + odmFilePath;
            this.messageUtil.error(message);
            log.error(message);
            return null;
        }

        String zipFilePath = odmExportFile.getAbsolutePath();

        InputStream inputStream = null;
        try {
            inputStream = getSingleOdmFileInputStream(edcCode, identifier, zipFilePath);
        } catch (IOException e) {
            String message = "There was a problem getting the ODM xml file input stream from the file: " + zipFilePath;
            this.messageUtil.error(message, e);
        }

        try {
            return unmarshalOdmFromInputStream(inputStream);
        } catch (JAXBException e) {
            String message = "There was a problem during unmarshalling the xml content of the file: " + zipFilePath;
            log.error(message, e);
            this.messageUtil.error(message, e);
        }
        return null;
    }

    private File getLatestZipFileExport(String edcCode, String identifier, String dataPath, File file) {

        if (file == null) return null;

        File odmExportFile = null;
        Long maxLastModified = 0L;

        for (String pathName : file.list()) {
            if (pathName.contains(edcCode + "-" + identifier) && pathName.endsWith(".zip")) {
                File odmFile = new File(dataPath + "/" + DATASETS_FOLDER + "/" + edcCode + "-" + identifier + EXPORT_ALL_SUFFIX + "/" + ODM_FULL_EXPORT_FOLDER + "/" + pathName);
                if (odmFile.lastModified() > maxLastModified) {
                    maxLastModified = odmFile.lastModified();
                    odmExportFile = odmFile;
                }
            }
        }
        return odmExportFile;
    }


    private Odm unmarshalOdmFromInputStream(InputStream inputStream) throws JAXBException {
        return JAXBHelper.unmarshalInputstream2(Odm.class, inputStream);
    }

    private InputStream getSingleOdmFileInputStream(String edcCode, String identifier, String zipFilePath) throws IOException {
        ZipFile zip = new ZipFile(zipFilePath);
        InputStream inputStream = null;

        for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            String fileName = entry.getName();
            if (fileName.contains(edcCode + RPB_IDENTIFIERSEP + identifier) && fileName.endsWith(".xml"))
                inputStream = zip.getInputStream(entry);
        }
        return inputStream;
    }

    private List<de.dktk.dd.rpb.core.domain.edc.StudySubject> getStudySubjects() {
        // Setup EDC study as query parameter
        de.dktk.dd.rpb.core.domain.edc.Study edcStudy = this.createEdcStudyQuery();
        return this.studyIntegrationFacade.loadStudySubjectsWithEvents(edcStudy);
    }

    private de.dktk.dd.rpb.core.domain.edc.Study createEdcStudyQuery() {
        Study activeStudy = this.mainBean.getActiveStudy();

        // Setup EDC study as query parameter
        de.dktk.dd.rpb.core.domain.edc.Study edcStudy = new de.dktk.dd.rpb.core.domain.edc.Study();

        edcStudy.setUniqueIdentifier(activeStudy.getUniqueIdentifier());

        return edcStudy;
    }

    public Date getExportPathModificationDate() {
        try {
            return labkeyService.getExportPathModificationDate(this.getLabkeyWebDavHandler());
        } catch (IOException e) {
            String errorMessage = "There was a problem getting the date of the last export path modification on LabKey.";
            messageUtil.error(errorMessage, e);
            log.error(errorMessage, e);
        }
        return null;
    }

    /**
     * Validates that there is a corresponding export folder on the Labkey server.
     *
     * @return boolean true if export path exists
     */
    public boolean checkExportPathExistsOnLabKey() {
        if (checkProjectExistsOnLabKey()) {
            try {
                return labkeyService.checkExportPathExists(this.getLabkeyWebDavHandler());
            } catch (IOException e) {
                String errorMessage = "There was a problem checking that the export path exists on Labkey.";
                messageUtil.error(errorMessage, e);
                log.error(errorMessage, e);
            }
        }
        return false;
    }

    /**
     * Validates that there is a corresponding export folder on the Labkey server.
     *
     * @return boolean true if export path exists
     */
    public long getLatestExportPathUpdateIntervalInHours() {
        Date today = new Date();
        long diff = today.getTime() - getExportPathModificationDate().getTime();
        return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
    }

    /**
     * Validates that there is a corresponding project setup for the study on the Labkey server.
     *
     * @return boolean true if project path exists
     */
    public boolean checkProjectExistsOnLabKey() {
        try {
            return labkeyService.checkStudyFilePathExists(this.getLabkeyWebDavHandler());
        } catch (IOException e) {
            if (e instanceof SardineException) {
                int status = ((SardineException) e).getStatusCode();

                String basicErrorMessage = "There was a problem connecting to Labkey. Got response code: "
                        + status + ". ";
                String errorMessage = "";

                switch (status) {
                    case 401:
                        errorMessage = "The server response code indicates that the authentication failed.";

                    case 403:
                        errorMessage = "The server response code indicates that you need additional permissions to perfom the request";
                        break;
                }


                messageUtil.errorText(basicErrorMessage + errorMessage);
            } else {
                messageUtil.error(e);
                log.error(e);
            }
        }
        return false;
    }

    private ILabKeyWebdavHandler getLabkeyWebDavHandler() {
        String labkeyUrl = this.mainBean.getMyAccount().getPartnerSite().getLab().getBaseUrl();
        Study activeStudy = this.mainBean.getActiveStudy();
        return new LabKeyWebDavSardineHandler(activeStudy, labkeyUrl);
    }

    public String onFlowProcess(FlowEvent event) {
        return event.getNewStep();
    }

    /**
     * Logic for the "reloadLabkeyStudyDataWizard"
     *
     * @return String Id of the active wizard tab depending on the state on Labkey side.
     */
    public String getStepIndex() {
        if (!checkProjectExistsOnLabKey()) return "studyExistsTab";

        // the export should not be older than one hour
        if (checkExportPathExistsOnLabKey() && this.getLatestExportPathUpdateIntervalInHours() >= 1) {
            return "exportPathExistsTab";
        }

        if (!checkExportPathExistsOnLabKey()) return "exportPathExistsTab";

        return "uploadDataTab";
    }

    public String analyseLabkeyConnection() {
        ILabKeyWebdavHandler webdavHandler = this.getLabkeyWebDavHandler();
        return labkeyService.analyseLabkeyConnection(webdavHandler);
    }

    public String analyseExportState() {
        ILabKeyWebdavHandler webdavHandler = this.getLabkeyWebDavHandler();
        return labkeyService.analyseExportState(webdavHandler);
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
        List<SortMeta> results = new ArrayList<>();

        return results;
    }

    //endregion

}