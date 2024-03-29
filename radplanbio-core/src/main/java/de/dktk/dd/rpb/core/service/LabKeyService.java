package de.dktk.dd.rpb.core.service;

import com.github.sardine.impl.SardineException;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.handler.lab.ILabKeyWebdavHandler;
import de.dktk.dd.rpb.core.util.labkey.StudyUpdater;

import javax.inject.Named;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


@Named
public class LabKeyService {

    public LabKeyService() {
    }

    // region static methods

    public static String removeExportFromPath(String pathWithExport) {
        return pathWithExport.replace("/export/", "/");
    }

    public static String removeLabKeyFromPath(String pathWithLabKey) {
        return pathWithLabKey.replace("/labkey/", "/");
    }

    // endregion

    // region object methods

    public boolean checkExportPathExists(ILabKeyWebdavHandler labKeyWebdavHandler) throws IOException {
        return labKeyWebdavHandler.hasExportPath();
    }

    public String analyseLabKeyConnection(ILabKeyWebdavHandler webdavHandler) {
        boolean studyPathExists;
        try {
            studyPathExists = this.checkStudyFilePathExists(webdavHandler);
        } catch (IOException e) {
            if (e instanceof SardineException) {
                int status = ((SardineException) e).getStatusCode();
                String labkeyUrl = webdavHandler.getLabkeyUrl();

                return getErrorBasedOnStatusResponse(status, labkeyUrl);

            } else {
                return "There is a problem connecting to LabKey. Got an exception: " + e.toString();
            }
        }

        if (studyPathExists) {
            return "Found corresponding path on webdav.";
        } else {
            String basicWebDavUrl = webdavHandler.getBasicWebDavUrl();
            String pathMissingOnLabKey = "The server response indicates that there is no corresponding folder on the LabKey Webdav interface. "
                    + "Please verify the study configuration on the LabKey server to support the path: "
                    + basicWebDavUrl
                    + " .";
            return pathMissingOnLabKey;
        }
    }

    public boolean checkStudyFilePathExists(ILabKeyWebdavHandler labKeyWebdavHandler) throws IOException {
        return labKeyWebdavHandler.exists("");
    }

    private String getErrorBasedOnStatusResponse(int status, String labKeyUrl) {
        String basicErrorMessage = "There was a problem connecting to Labkey. Got response code: "
                + Integer.toString(status)
                + " from server : "
                + labKeyUrl + ". ";
        String errorMessage = "";

        switch (status) {
            case 401:
                errorMessage = "The server response code indicates that the authentication failed.";
                break;

            case 403:
                errorMessage = "The server response code indicates that you need additional permissions to perform the request";
                break;
        }
        return basicErrorMessage + errorMessage;
    }

    public String analyseExportState(ILabKeyWebdavHandler webdavHandler) {
        boolean pathExists;

        try {
            pathExists = webdavHandler.hasExportPath();
        } catch (IOException e) {
            if (e instanceof SardineException) {
                int status = ((SardineException) e).getStatusCode();
                String labKeyUrl = webdavHandler.getLabkeyUrl();

                return getErrorBasedOnStatusResponse(status, labKeyUrl);

            } else {
                return "There is a problem connecting to LabKey. Got an exception: " + e.toString();
            }
        }

        if (pathExists) {
            // UI logic could redirect if the export is too old -> getStepIndex() function
            return "The latest export is probably too old. ";
        } else {
            return "Could not find an export path. ";
        }
    }

    public Date getExportPathModificationDate(ILabKeyWebdavHandler webdavHandler) throws IOException {
        return webdavHandler.getExportPathModificationDate();
    }

    /**
     * Packages and uploads project files to the Labkey server.
     *
     * @param webdavHandler    Handler for interacting with Labkey
     * @param studySubjectList List of StudySubjects from the EDC system. Includes all participants.
     * @param odm              ODM object that includes MetaDataVersion and ClinicalData. Includes only participants with event data.
     * @throws IOException, JAXBException exceptions that are supposed to generate error messages  on the UI for the user.
     */
    public void handleUpdate(
            ILabKeyWebdavHandler webdavHandler,
            List<StudySubject> studySubjectList,
            Odm odm,
            LabKeyExportConfiguration labKeyExportConfiguration
    ) throws IOException, JAXBException, MissingPropertyException, ParseException {
        StudyUpdater studyUpdater = new StudyUpdater(webdavHandler, studySubjectList, odm, labKeyExportConfiguration);
        studyUpdater.runUpdate();
    }

    // endregion
}