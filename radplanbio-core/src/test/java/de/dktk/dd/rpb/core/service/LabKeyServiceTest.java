package de.dktk.dd.rpb.core.service;

import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.MetaDataVersion;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.handler.lab.ILabKeyWebdavHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LabKeyService.class, Logger.class, LoggerFactory.class})

public class LabKeyServiceTest {
    private Logger logger;
    private LabKeyService labkeyService;
    private ILabKeyWebdavHandler handlerMock;

    private final Map<String, String> oidNameLookup = new HashMap<>();
    private final Map<String, Integer> ordinalLookup = new HashMap<>();

    private Odm odm;

    private LabKeyExportConfiguration labKeyExportConfiguration;

    @Before
    public void executedBeforeEach() throws IOException {
        this.labkeyService = new LabKeyService();
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        odm = new Odm();

        this.handlerMock = mock(ILabKeyWebdavHandler.class);

        File datasetsManifestFile = new File("src/test/data/labkey/datasets-manifest/is_empty.xml");
        InputStream datasetsManifestStream = new FileInputStream(datasetsManifestFile);

        when(this.handlerMock.getDatasetsManifestFile()).thenReturn(datasetsManifestStream);

        File datasetsMetaDataFile = new File("src/test/data/labkey/datasets-metadata/has_no_tables.xml");
        InputStream datasetsMetaDataStream = new FileInputStream(datasetsMetaDataFile);
        when(this.handlerMock.getDatasetsMetadataFile()).thenReturn(datasetsMetaDataStream);

        File studyFile = new File("src/test/data/labkey/study.xml");
        InputStream studyFileInputStream = new FileInputStream(studyFile);
        when(this.handlerMock.getStudyFile()).thenReturn(studyFileInputStream);

        MetaDataVersion metaDataVersion = new MetaDataVersion();
        Study study = new Study();
        study.setMetaDataVersion(metaDataVersion);
        List<Study> studyList = new ArrayList<>();
        studyList.add(study);
        odm.setStudies(studyList);

        odm.setParentStudy(study);

        ClinicalData clinicalData = new ClinicalData();
        List<ClinicalData> clinicalDataList = new ArrayList<>();
        clinicalDataList.add(clinicalData);
        odm.setClinicalDataList(clinicalDataList);

        clinicalData.setStudySubjects(new ArrayList<>());

        this.labKeyExportConfiguration = mock(LabKeyExportConfiguration.class);

    }

    // region static methods

    @Test
    public void removeExportFolderFromPath() {
        String pathWithExportFolder = "/org/labkey/_webdav/STR-HNPraedBIO-2013/DD-STR-HNPraedBIO-2013/@files/export/study/assaySchedule/assayspecimen.tsv";
        String pathWithoutExportFolder = "/org/labkey/_webdav/STR-HNPraedBIO-2013/DD-STR-HNPraedBIO-2013/@files/study/assaySchedule/assayspecimen.tsv";

        assertEquals(pathWithoutExportFolder, LabKeyService.removeExportFromPath(pathWithExportFolder));
    }

    @Test
    public void removeLabkeyFromPath() {
        String pathWithLabkeyFolder = "/org/labkey/_webdav/STR-HNPraedBIO-2013/DD-STR-HNPraedBIO-2013/@files/export/study/assaySchedule/assayspecimen.tsv";
        String pathWithoutLabkeyFolder = "/org/_webdav/STR-HNPraedBIO-2013/DD-STR-HNPraedBIO-2013/@files/export/study/assaySchedule/assayspecimen.tsv";

        assertEquals(pathWithoutLabkeyFolder, LabKeyService.removeLabKeyFromPath(pathWithLabkeyFolder));
    }

    // endregion

    //region object methods

    // region checkExportPathExists()

    @Test
    public void checkExportPathExists() throws IOException {
        this.labkeyService.checkExportPathExists(handlerMock);

        verify(this.handlerMock, times(1)).hasExportPath();
    }

    // endregion

    // region checkStudyFilePathExists(ILabKeyWebdavHandler labkeyWebdavHandler)

    @Test
    public void checkStudyFilePathExists() throws IOException {
        this.labkeyService.checkStudyFilePathExists(handlerMock);

        verify(this.handlerMock, times(1)).exists("");
    }

    // endregion


    // region getExportPathModificationDate(ILabKeyWebdavHandler webdavHandler)

    @Test
    public void getExportPathModificationDate_calls_method_on_handler() throws IOException {
        this.labkeyService.getExportPathModificationDate(handlerMock);

        verify(this.handlerMock, times(1)).getExportPathModificationDate();
    }

    // endregion

    // region handleUpdate

    @Test
    public void handleUpdate_triggers_the_export_on_the_handler() throws Exception {
        List<StudySubject> studySubjectList = new ArrayList<>();

        this.labkeyService.handleUpdate(this.handlerMock, studySubjectList, odm, this.labKeyExportConfiguration);

        verify(this.handlerMock, times(1)).copyExportPathToPipelineRoot();
    }

    @Test
    public void handleUpdate_uploads_subject_and_event_attributes() throws Exception {
        List<StudySubject> studySubjectList = new ArrayList<>();
        List<EventDefinition> eventDefinitionList = new ArrayList<>();

        this.labkeyService.handleUpdate(this.handlerMock, studySubjectList, odm, this.labKeyExportConfiguration);

        verify(this.handlerMock, times(1)).uploadDataSet(eq("5001"), any(byte[].class));
        verify(this.handlerMock, times(1)).uploadDataSet(eq("5002"), any(byte[].class));
    }

    @Test
    public void handleUpdate_uploads_dataset_metadata_file() throws Exception {
        List<StudySubject> studySubjectList = new ArrayList<>();
        List<EventDefinition> eventDefinitionList = new ArrayList<>();

        this.labkeyService.handleUpdate(this.handlerMock, studySubjectList, odm, this.labKeyExportConfiguration);

        verify(this.handlerMock, times(1)).putDatasetsMetadataFile(any(byte[].class));
    }

    @Test
    public void handleUpdate_uploads_dataset_manifest_file() throws Exception {
        List<StudySubject> studySubjectList = new ArrayList<>();
        List<EventDefinition> eventDefinitionList = new ArrayList<>();

        this.labkeyService.handleUpdate(this.handlerMock, studySubjectList, odm, this.labKeyExportConfiguration);

        verify(this.handlerMock, times(1)).putDatasetsManifestFile(any(byte[].class));
    }

    @Test
    public void handleUpdate_uploads_visit_map_file() throws Exception {
        List<StudySubject> studySubjectList = new ArrayList<>();
        List<EventDefinition> eventDefinitionList = new ArrayList<>();

        this.labkeyService.handleUpdate(this.handlerMock, studySubjectList, odm, this.labKeyExportConfiguration);

        verify(this.handlerMock, times(1)).putVisitMapFile(any(byte[].class));
    }

    @Test
    public void handleUpdate_creates_study_load_file() throws Exception {
        List<StudySubject> studySubjectList = new ArrayList<>();
        List<EventDefinition> eventDefinitionList = new ArrayList<>();

        this.labkeyService.handleUpdate(this.handlerMock, studySubjectList, odm, this.labKeyExportConfiguration);

        verify(this.handlerMock, times(1)).createStudyLoadFile();
    }

    // endregion

    // endregion

}
