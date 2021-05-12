package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.builder.lab.DatasetsManifestXmlFileBuilder;
import de.dktk.dd.rpb.core.builder.lab.DatasetsMetadataXmlFileBuilder;
import de.dktk.dd.rpb.core.builder.lab.VisitMapXmlFileBuilder;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.CrfAttributeTable;
import de.dktk.dd.rpb.core.domain.lab.CrfAttributes;
import de.dktk.dd.rpb.core.handler.lab.ILabKeyWebdavHandler;
import org.apache.log4j.Logger;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Functionality to update data on the LabKey import folder.
 */
public class StudyUpdater {
    private static final Logger log = Logger.getLogger(StudyUpdater.class);

    private final DatasetsMetadataXmlFileBuilder datasetsMetadataBuilder;
    private final DatasetsManifestXmlFileBuilder datasetsManifestBuilder;
    private final VisitMapXmlFileBuilder visitMapXmlFileBuilder;

    private final ILabKeyWebdavHandler webdavHandler;
    private final List<StudySubject> studySubjectList;
    private final Odm odmFileContent;

    private List<CrfAttributeTable> crfAttributeTableList;

    /**
     * Upload files to the Labkey server in order to reload the study with the updated data.
     *
     * @param webdavHandler    Handler for the communication with Labkey
     * @param studySubjectList List of StudySubjects from the EDC system. Includes all participants.
     * @param odmFileContent   ODM object that includes MetaDataVersion and ClinicalData. Includes only participants with event data.
     * @throws IOException   Exceptions that will be propagated to upper layers.
     * @throws JAXBException Exceptions that will be propagated to upper layers.
     */
    public StudyUpdater(ILabKeyWebdavHandler webdavHandler,
                        List<StudySubject> studySubjectList,
                        Odm odmFileContent
    ) throws IOException, JAXBException {
        this.webdavHandler = webdavHandler;
        this.studySubjectList = studySubjectList;
        this.odmFileContent = odmFileContent;

        this.webdavHandler.copyExportPathToPipelineRoot();

        OdmFormDataToAttributeTablesConverter odmFormDataToAttributeTablesConverter = new OdmFormDataToAttributeTablesConverter(odmFileContent);
        this.crfAttributeTableList = odmFormDataToAttributeTablesConverter.getCrfAttributeTableList();

        this.datasetsMetadataBuilder = new DatasetsMetadataXmlFileBuilder(
                webdavHandler.getDatasetsMetadataFile(), crfAttributeTableList);

        this.datasetsManifestBuilder = new DatasetsManifestXmlFileBuilder(
                webdavHandler.getDatasetsManifestFile());

        OdmEventMetaDataLookup odmEventMetaDataLookup = new OdmEventMetaDataLookup(odmFileContent.findFirstStudyOrNone().getMetaDataVersion());
        this.visitMapXmlFileBuilder = new VisitMapXmlFileBuilder(odmEventMetaDataLookup);
    }

    /**
     * Triggers the update flow.
     *
     * @throws IOException   Exceptions that will be propagated to upper layers.
     * @throws JAXBException Exceptions that will be propagated to upper layers.
     */
    public void runUpdate() throws IOException, JAXBException {
        this.uploadSubjectAttributeTable();
        this.uploadEventAttributesTable();
        this.uploadFormAttributeTables();

        buildUploadDatasetsMetadataFile();
        buildUploadDatasetsManifestFile();
        buildUploadVisitMapFile();

        webdavHandler.createStudyLoadFile();
    }

    private void buildUploadVisitMapFile() throws JAXBException, IOException {
        byte[] visitMapContent = visitMapXmlFileBuilder.build().getBytes(StandardCharsets.UTF_8);
        webdavHandler.putVisitMapFile(visitMapContent);
    }

    private void buildUploadDatasetsManifestFile() throws JAXBException, IOException {
        byte[] manifestContent = datasetsManifestBuilder.build().getBytes(StandardCharsets.UTF_8);
        webdavHandler.putDatasetsManifestFile(manifestContent);
    }

    private void buildUploadDatasetsMetadataFile() throws JAXBException, IOException {
        byte[] metadataContent = datasetsMetadataBuilder.build().getBytes(StandardCharsets.UTF_8);
        webdavHandler.putDatasetsMetadataFile(metadataContent);
    }

    private void uploadSubjectAttributeTable() throws IOException {
        ByteArrayOutputStream labkeySubjectTsvFileContent = SubjectTsvWriter.getByteArrayOutputStream(this.studySubjectList);
        byte[] fileContent = labkeySubjectTsvFileContent.toByteArray();
        Integer subjectAttributesId = this.datasetsManifestBuilder.findOrCreateDataset("SubjectAttributes");
        this.webdavHandler.uploadDataSet(subjectAttributesId.toString(), fileContent);
    }

    private void uploadEventAttributesTable() throws IOException {
        ByteArrayOutputStream labkeyEventTsvFileContent = StudyEventTsvWriter.getByteArrayOutputstream(this.studySubjectList);
        byte[] eventFileContent = labkeyEventTsvFileContent.toByteArray();
        Integer eventAttributesId = this.datasetsManifestBuilder.findOrCreateDataset("EventAttributes");
        this.webdavHandler.uploadDataSet(eventAttributesId.toString(), eventFileContent);
    }

    private void uploadFormAttributeTables() throws IOException {
        for (CrfAttributeTable attributeTable : this.crfAttributeTableList) {

            String name = attributeTable.getName();
            String oid = attributeTable.getOid();


            Integer eventAttributesFileId = this.datasetsManifestBuilder.findOrCreateDataset(name.trim());
            this.datasetsMetadataBuilder.createCrfAttributeTable(oid);

            OdmEventMetaDataLookup odmEventMetaDataLookup = new OdmEventMetaDataLookup(odmFileContent.findFirstStudyOrNone().getMetaDataVersion());

            ICsvListWriter listWriter = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                listWriter = new CsvListWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8),
                        CsvPreference.TAB_PREFERENCE);

                listWriter.writeHeader(attributeTable.getHeaderNames());
                for (CrfAttributes item : attributeTable.getOrderedAttributeList(odmEventMetaDataLookup)) {
                    listWriter.write(item.getValues(), item.getCellProcessors());
                }

            } catch (IOException e) {
                String errorDescription = "There was a problem during writing attribute table \" " + name + "\".";
                this.log.debug(errorDescription, e);
                throw new IOException(errorDescription, e);
            } finally {
                if (listWriter != null) {
                    listWriter.close();
                }
            }

            byte[] eventFileContent = byteArrayOutputStream.toByteArray();
            this.webdavHandler.uploadDataSet(eventAttributesFileId.toString(), eventFileContent);
        }
    }
}
