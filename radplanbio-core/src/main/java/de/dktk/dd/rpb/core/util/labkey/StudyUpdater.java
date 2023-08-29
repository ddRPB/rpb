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

package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.builder.lab.DatasetsManifestXmlFileBuilder;
import de.dktk.dd.rpb.core.builder.lab.DatasetsMetadataXmlFileBuilder;
import de.dktk.dd.rpb.core.builder.lab.VisitMapXmlFileBuilder;
import de.dktk.dd.rpb.core.converter.FormDataConverter;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.*;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.handler.lab.ILabKeyWebdavHandler;
import org.labkey.study.xml.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.*;

/**
 * Functionality to update data on the LabKey import folder.
 */
public class StudyUpdater {
    private static final Logger log = LoggerFactory.getLogger(StudyUpdater.class);

    private final DatasetsMetadataXmlFileBuilder datasetsMetadataBuilder;
    private final DatasetsManifestXmlFileBuilder datasetsManifestBuilder;
    private final VisitMapXmlFileBuilder visitMapXmlFileBuilder;

    private final ILabKeyWebdavHandler webdavHandler;
    private final List<StudySubject> studySubjectList;
    private final Odm odmFileContent;
    private final OdmEventMetaDataLookup odmEventMetaDataLookup;
    private final LabKeyExportConfiguration labKeyExportConfiguration;

    private List<FormBasedCrfAttributeTable> formBasedCrfAttributeTableList;
    private List<ItemGroupBasedCrfAttributeTable> itemGroupBasedCrfAttributeTableList;

    private String subjectColumnName;

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
                        Odm odmFileContent,
                        LabKeyExportConfiguration labKeyExportConfiguration
    ) throws IOException, JAXBException, MissingPropertyException {
        this.webdavHandler = webdavHandler;
        this.studySubjectList = studySubjectList;
        this.odmFileContent = odmFileContent;
        this.labKeyExportConfiguration = labKeyExportConfiguration;

        this.webdavHandler.copyExportPathToPipelineRoot();

        Study studyConfiguration = getStudyConfiguration(webdavHandler);
        this.subjectColumnName = studyConfiguration.getSubjectColumnName();
        odmEventMetaDataLookup = getOdmEventMetaDataLookup(odmFileContent);

        OdmStudyMetaDataLookup odmStudyMetaDataLookup = new OdmStudyMetaDataLookup(odmFileContent);


        OdmFormDataToFormBasedCrfAttributeTablesConverter odmFormDataToFormBasedCrfAttributeTablesConverter = new OdmFormDataToFormBasedCrfAttributeTablesConverter(
                odmFileContent, this.subjectColumnName, odmEventMetaDataLookup, odmStudyMetaDataLookup, labKeyExportConfiguration
        );
        this.formBasedCrfAttributeTableList = odmFormDataToFormBasedCrfAttributeTablesConverter.getFormBasedCrfAttributeTableList();


        OdmFormDataToItemGroupBasedCrfAttributeTablesConverter odmFormDataToItemGroupBasedCrfAttributeTablesConverter = new OdmFormDataToItemGroupBasedCrfAttributeTablesConverter(
                odmFileContent, this.subjectColumnName, odmEventMetaDataLookup, odmStudyMetaDataLookup, labKeyExportConfiguration
        );
        this.itemGroupBasedCrfAttributeTableList = odmFormDataToItemGroupBasedCrfAttributeTablesConverter.getItemGroupBasedCrfAttributeTableList();

        this.datasetsMetadataBuilder = new DatasetsMetadataXmlFileBuilder(
                webdavHandler.getDatasetsMetadataFile(),
                labKeyExportConfiguration,
                formBasedCrfAttributeTableList,
                itemGroupBasedCrfAttributeTableList,
                subjectColumnName
        );

        this.datasetsManifestBuilder = new DatasetsManifestXmlFileBuilder(
                webdavHandler.getDatasetsManifestFile());

        this.visitMapXmlFileBuilder = new VisitMapXmlFileBuilder(odmEventMetaDataLookup);
    }

    private Study getStudyConfiguration(ILabKeyWebdavHandler webdavHandler) throws IOException, JAXBException {
        InputStream studyStream = webdavHandler.getStudyFile();
        JAXBContext studyContext = JAXBContext.newInstance(Study.class);
        Unmarshaller studyUnmarshaller = studyContext.createUnmarshaller();
        Study studyConfiguration = (Study) studyUnmarshaller.unmarshal(studyStream);
        return studyConfiguration;
    }

    /**
     * Triggers the update flow.
     *
     * @throws IOException   Exceptions that will be propagated to upper layers.
     * @throws JAXBException Exceptions that will be propagated to upper layers.
     */
    public void runUpdate() throws IOException, JAXBException, MissingPropertyException, ParseException {
        this.uploadSubjectAttributeTable();
        this.uploadSubjectGroupAttributeTable();
        this.uploadEventAttributesTable();
        this.uploadFormAttributesTable();

        this.uploadCrfAttributesPerFormTables();
        this.uploadCrfAttributesPerItemGroupTables();

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
        ByteArrayOutputStream labkeySubjectTsvFileContent = SubjectTsvWriter.getByteArrayOutputStream(this.studySubjectList, this.subjectColumnName, this.labKeyExportConfiguration);
        byte[] fileContent = labkeySubjectTsvFileContent.toByteArray();
        Integer subjectAttributesId = this.datasetsManifestBuilder.findOrCreateDataset("SubjectAttributes", LABKEY_EDC_ATTRIBUTES);
        this.webdavHandler.uploadDataSet(subjectAttributesId.toString(), fileContent);
    }

    private void uploadSubjectGroupAttributeTable() throws IOException {
        ByteArrayOutputStream labkeySubjectTsvFileContent = SubjectGroupTsvWriter.getByteArrayOutputStream(this.studySubjectList, this.subjectColumnName);
        byte[] fileContent = labkeySubjectTsvFileContent.toByteArray();
        Integer subjectAttributesId = this.datasetsManifestBuilder.findOrCreateDataset(LABKEY_SUBJECT_GROUP_ATTRIBUTES, LABKEY_EDC_ATTRIBUTES);
        this.webdavHandler.uploadDataSet(subjectAttributesId.toString(), fileContent);
    }



    private void uploadEventAttributesTable() throws IOException {
        ByteArrayOutputStream labkeyEventTsvFileContent = StudyEventTsvWriter.getByteArrayOutputstream(this.studySubjectList, this.subjectColumnName);
        byte[] eventFileContent = labkeyEventTsvFileContent.toByteArray();
        Integer eventAttributesId = this.datasetsManifestBuilder.findOrCreateDataset("EventAttributes", LABKEY_EDC_ATTRIBUTES);
        this.webdavHandler.uploadDataSet(eventAttributesId.toString(), eventFileContent);
    }

    private void uploadFormAttributesTable() throws IOException, MissingPropertyException, ParseException {
        List<FormAttributes> formAttributesList = getFormAttributesList();
        byte[] formAttributesFileContent = writeFormAttributesFileContent(formAttributesList);
        Integer formAttributesId = this.datasetsManifestBuilder.findOrCreateDataset("FormAttributes", LABKEY_EDC_ATTRIBUTES);
        this.webdavHandler.uploadDataSet(formAttributesId.toString(), formAttributesFileContent);

    }

    private byte[] writeFormAttributesFileContent(List<FormAttributes> formAttributesList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ICsvListWriter listWriter = new CsvListWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8),
                CsvPreference.TAB_PREFERENCE);

        String[] headerArray = FormAttributes.getHeaders(subjectColumnName);

        try {
            listWriter.writeHeader(headerArray);

            for (FormAttributes formAttributes : formAttributesList) {
                listWriter.write(formAttributes.getValues(), formAttributes.getCellProcessors());
            }

        } catch (IOException e) {
            String errorDescription = "There was a problem during writing form attributes table";
            this.log.error(errorDescription, e);
            throw new IOException(errorDescription, e);
        } finally {
            if (listWriter != null) {
                listWriter.close();
            }
        }

        byte[] formAttributesFileContent = byteArrayOutputStream.toByteArray();
        return formAttributesFileContent;
    }

    private List<FormAttributes> getFormAttributesList() throws MissingPropertyException, ParseException {
        List<StudySubject> studySubjectList = odmFileContent.getStudySubjects();
        List<FormAttributes> formAttributesList = new ArrayList<>();

        if (studySubjectList != null) {
            for (StudySubject studySubject : studySubjectList) {
                String studySubjectId = studySubject.getStudySubjectId();
                List<EventData> eventDataList = studySubject.getStudyEventDataList();
                if (eventDataList != null) {

                    for (EventData eventData : eventDataList) {
                        FormDataConverter formDataConverter = new FormDataConverter(studySubjectId, eventData, this.odmEventMetaDataLookup);
                        List<FormData> formDataList = eventData.getFormDataList();

                        if (formDataList != null) {
                            for (FormData formData : formDataList) {

                                if (formData.getItemGroupDataList() != null) {
                                    formAttributesList.add(formDataConverter.convertToFormAttributes(formData));
                                }
                            }
                        } else {
                            log.debug("FormData list is null on object: " + eventData.toString());
                        }
                    }

                } else {
                    log.debug("EventData list is null on object: " + studySubject.toString());
                }
            }
        }
        return formAttributesList;
    }

    private void uploadCrfAttributesPerFormTables() throws IOException, MissingPropertyException {
        for (FormBasedCrfAttributeTable attributeTable : this.formBasedCrfAttributeTableList) {

            String name = attributeTable.getName();
            String oid = attributeTable.getOid();


            Integer eventAttributesFileId = this.datasetsManifestBuilder.findOrCreateDataset(oid, LABKEY_EDC_FORM_VERSIONS);
            this.datasetsMetadataBuilder.createFormBasedCrfAttributeTableItems(oid);

            OdmEventMetaDataLookup odmEventMetaDataLookup = getOdmEventMetaDataLookup(odmFileContent);

            ICsvListWriter listWriter = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                listWriter = new CsvListWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8),
                        CsvPreference.TAB_PREFERENCE);

                listWriter.writeHeader(attributeTable.getHeaderNames());
                for (FormBasedCrfAttributeTableItem item : attributeTable.getOrderedAttributeList()) {
                    listWriter.write(item.getValues(), attributeTable.getCellProcessors());
                }

            } catch (IOException e) {
                String errorDescription = "There was a problem during writing attribute table \" " + name + "\".";
                this.log.error(errorDescription, e);
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

    private void uploadCrfAttributesPerItemGroupTables() throws IOException {
        for (ItemGroupBasedCrfAttributeTable attributeTable : this.itemGroupBasedCrfAttributeTableList
        ) {
            String name = attributeTable.getItemGroupOid();
            String oid = attributeTable.getItemGroupOid();

            Integer eventAttributesFileId = this.datasetsManifestBuilder.findOrCreateDataset(oid, LABKEY_EDC_ITEM_GROUPS);

            this.datasetsMetadataBuilder.createItemGroupBasedCrfTableItems(oid);

            ICsvListWriter listWriter = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                listWriter = new CsvListWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8),
                        CsvPreference.TAB_PREFERENCE);

                listWriter.writeHeader(attributeTable.getHeaderNames());
                for (ItemGroupBasedCrfAttributeTableItem item : attributeTable.getOrderedAttributeList()) {
                    listWriter.write(item.getValues(), attributeTable.getCellProcessors());
                }

            } catch (IOException | MissingPropertyException e) {
                String errorDescription = "There was a problem during writing attribute table \" " + name + "\".";
                this.log.error(errorDescription, e);
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

    private OdmEventMetaDataLookup getOdmEventMetaDataLookup(Odm odmFileContent) throws MissingPropertyException {
        if (odmFileContent.getParentOrSiteStudyByConvention() == null)
            throw new MissingPropertyException("There is a problem getting the parent or site study from the ODM.");
        if (odmFileContent.getParentOrSiteStudyByConvention().getMetaDataVersion() == null) throw new MissingPropertyException(
                "The study " + odmFileContent.getParentOrSiteStudyByConvention().getOid() + " does not have a MetaData node");

        return new OdmEventMetaDataLookup(odmFileContent.getParentOrSiteStudyByConvention().getMetaDataVersion());
    }
}
