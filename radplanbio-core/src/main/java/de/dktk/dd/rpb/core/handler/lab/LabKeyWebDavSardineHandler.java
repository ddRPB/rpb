package de.dktk.dd.rpb.core.handler.lab;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.service.LabKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.LABKEY_SHARED_STUDY_PREFIX;

public class LabKeyWebDavSardineHandler implements ILabKeyWebdavHandler {
    // region final properties

    private static final Logger log = LoggerFactory.getLogger(LabKeyWebDavSardineHandler.class);

    private static final String WEB_DAV_URL_PART = "_webdav/";
    private static final String FILES_URL_PART = "@files/";
    private static final String PIPELINE_URL_PART = "@pipeline/";
    private static final String EXPORT_URL_PART = "export/";

    private final Sardine sardine;

    private String labkeyUrl;
    private String studyIdentifier;
    private String siteIdentifier;
    private String pipelineUrlPart;


    public LabKeyWebDavSardineHandler(Study study, String labkeyUrl, Sardine sardine) {
        this.sardine = sardine;
        init(study, labkeyUrl);
    }

    public LabKeyWebDavSardineHandler(Study study, String labkeyUrl) {
        this.sardine = SardineFactory.begin(UserContext.getEmail(), UserContext.getClearPassword());
        init(study, labkeyUrl);
    }


    private void init(Study study, String labkeyUrl) {
        this.siteIdentifier = study.getUniqueIdentifier();
        if (study.getParentStudy() == null) {
            this.studyIdentifier = study.getUniqueIdentifier();
            // Study is parent -> Shared Study in LabKey
            this.siteIdentifier = LABKEY_SHARED_STUDY_PREFIX + this.studyIdentifier;
        } else {
            this.studyIdentifier = study.getParentStudy().getUniqueIdentifier();
        }
        this.labkeyUrl = labkeyUrl;
        this.setPipelineUrl();
    }

    private void setPipelineUrl() {
        try {
            if (this.hasPipelineFolder()) {
                this.pipelineUrlPart = PIPELINE_URL_PART;
            } else {
                this.pipelineUrlPart = FILES_URL_PART;
            }
        } catch (IOException e) {
            // Fallback to default setting
            this.pipelineUrlPart = FILES_URL_PART;
        }
    }

    public boolean hasPipelineFolder() throws IOException {
        String exportUrl = this.getBasicWebDavUrl() + PIPELINE_URL_PART;
        return sardine.exists(exportUrl);
    }


    public String getStudyIdentifier() {
        return this.studyIdentifier;
    }

    public String getLabkeyUrl() {
        return labkeyUrl;
    }

    public String getSiteIdentifier() {
        return this.siteIdentifier;
    }

    public boolean exists(String path) throws IOException {
        return sardine.exists(this.getBasicWebDavUrl() + path);
    }

    public boolean hasExportPath() throws IOException {
        return sardine.exists(this.getBasicWebDavUrl() + this.pipelineUrlPart + EXPORT_URL_PART);
    }

    public Date getModificationDate(String path) throws IOException {
        if (this.exists(path)) {
            List<DavResource> resourceList = sardine.list(this.getBasicWebDavUrl() + path, 0);
            if (resourceList.size() == 1) {
                return resourceList.get(0).getModified();
            }

        }
        return null;
    }

    public Date getExportPathModificationDate() throws IOException {
        return this.getModificationDate(this.pipelineUrlPart + EXPORT_URL_PART + "folder.xml");
    }

    public void copyExportPathToPipelineRoot() throws IOException {
        String exportUrl = this.getBasicWebDavUrl() + this.pipelineUrlPart + EXPORT_URL_PART;
        sardine.exists(exportUrl);
        this.deleteStudyFolder();
        String labkeyServerRootPath = LabKeyService.removeLabKeyFromPath(labkeyUrl);
        List<DavResource> resources = sardine.list(exportUrl, 5);
        for (DavResource res : resources) {

            String sourcePath = labkeyServerRootPath + res.getPath();
            String destinationPath = labkeyServerRootPath + LabKeyService.removeExportFromPath(res.getPath());

            if (res.getPath().endsWith("/")) {
                if (!sardine.exists(labkeyServerRootPath + LabKeyService.removeExportFromPath(res.getPath()))) {
                    sardine.createDirectory(labkeyServerRootPath + LabKeyService.removeExportFromPath(res.getPath()));
                }
            } else {
                sardine.copy(sourcePath, destinationPath);
            }
        }
    }

    public String getBasicWebDavUrl() {
        return labkeyUrl + WEB_DAV_URL_PART + this.studyIdentifier + "/" + this.siteIdentifier + "/";
    }

    public InputStream getStudyFile() throws IOException {
        return sardine.get(this.getBasicWebDavUrl() + this.pipelineUrlPart + "study/study.xml");
    }

    public InputStream getVisitMapFile() throws IOException {
        return sardine.get(this.getBasicWebDavUrl() + this.pipelineUrlPart + "study/visit_map.xml");
    }

    public void putVisitMapFile(byte[] fileContent) throws IOException {
        sardine.put(this.getBasicWebDavUrl() + this.pipelineUrlPart + "study/visit_map.xml", fileContent);
    }

    public InputStream getDatasetsMetadataFile() {
        String url = this.getBasicWebDavUrl() + this.pipelineUrlPart + "study/datasets/datasets_metadata.xml";
        try {
            return sardine.get(url);
        } catch (IOException e) {
            this.log.debug("There was a problem to get the file from: " + url, e);
            return null;
        }
    }

    public void putDatasetsMetadataFile(byte[] fileContent) throws IOException {
        sardine.put(this.getBasicWebDavUrl() + this.pipelineUrlPart + "study/datasets/datasets_metadata.xml", fileContent);
    }

    public InputStream getDatasetsManifestFile() throws IOException {
        String url = this.getBasicWebDavUrl() + this.pipelineUrlPart + "study/datasets/datasets_manifest.xml";
        try {
            return sardine.get(url);
        } catch (IOException e) {
            this.log.debug("There was a problem to get the file from: " + url, e);
            return null;
        }
    }

    public void putDatasetsManifestFile(byte[] fileContent) throws IOException {
        sardine.put(this.getBasicWebDavUrl() + this.pipelineUrlPart + "study/datasets/datasets_manifest.xml", fileContent);
    }

    public void uploadDataSet(String datasetId, byte[] fileContent) throws IOException {
        sardine.put(this.getBasicWebDavUrl() + this.pipelineUrlPart + "study/datasets/dataset" + datasetId + ".tsv", fileContent);
    }

    public void createStudyLoadFile() throws IOException {
        String updateMessage = this.getBasicWebDavUrl() + this.pipelineUrlPart + " updated by RadplanBio";
        byte[] data = updateMessage.getBytes(StandardCharsets.UTF_8);
        sardine.put(this.getBasicWebDavUrl() + this.pipelineUrlPart + "studyload.txt", data);
    }

    public void deleteStudyFolder() throws IOException {
        if (sardine.exists(this.getBasicWebDavUrl() + this.pipelineUrlPart + "study")) {
            sardine.delete(this.getBasicWebDavUrl() + this.pipelineUrlPart + "study");
        }
    }
}
