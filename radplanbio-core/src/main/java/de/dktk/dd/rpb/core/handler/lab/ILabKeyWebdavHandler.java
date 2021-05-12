package de.dktk.dd.rpb.core.handler.lab;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public interface ILabKeyWebdavHandler {

    public boolean exists(String path) throws IOException;

    public boolean hasExportPath() throws IOException;

    public Date getModificationDate(String path) throws IOException;

    public Date getExportPathModificationDate() throws IOException;

    public void copyExportPathToPipelineRoot() throws IOException;

    public String getBasicWebDavUrl();

    public String getLabkeyUrl();

    public InputStream getVisitMapFile() throws IOException;

    public void putVisitMapFile(byte[] fileContent) throws IOException;

    public InputStream getDatasetsMetadataFile() throws IOException;

    public void putDatasetsMetadataFile(byte[] fileContent) throws IOException;

    public InputStream getDatasetsManifestFile() throws IOException;

    public void putDatasetsManifestFile(byte[] fileContent) throws IOException;

    public void uploadDataSet(String datasetId, byte[] fileContent) throws IOException;

    public void createStudyLoadFile() throws IOException;
}
