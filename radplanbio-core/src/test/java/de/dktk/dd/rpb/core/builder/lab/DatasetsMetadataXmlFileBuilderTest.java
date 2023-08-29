package de.dktk.dd.rpb.core.builder.lab;

import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.FormDetails;
import de.dktk.dd.rpb.core.domain.lab.FormBasedCrfAttributeTable;
import de.dktk.dd.rpb.core.domain.lab.ItemGroupBasedCrfAttributeTable;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.labkey.data.xml.TableType;
import org.labkey.data.xml.TablesType;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.LABKEY_PARTICIPANT_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DatasetsMetadataXmlFileBuilder.class, Logger.class, LoggerFactory.class, FormDefinition.class})
public class DatasetsMetadataXmlFileBuilderTest {
    LabKeyExportConfiguration labKeyExportConfiguration;
    String subjectColumnName = "dummySubject";

    @Before
    public void setUp() throws Exception {
        labKeyExportConfiguration = mock(LabKeyExportConfiguration.class);
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);


    }

    //region Constructor empty
    @Test
    public void XML_content_has_participants_table() throws JAXBException {
        DatasetsMetadataXmlFileBuilder facade = new DatasetsMetadataXmlFileBuilder();
        String content = facade.build();

        TablesType tablesType = JAXBHelper.unmashalString(TablesType.class, content);

        String participantTableName = "SubjectAttributes";

        List<TableType> participantsTableList = getListOfMatchingTables(tablesType, participantTableName);

        assertEquals(1, participantsTableList.size());
    }

    private List<TableType> getListOfMatchingTables(TablesType tablesType, String participantTableName) {
        List<TableType> participantsTableList = new ArrayList();
        for (Object tableType : tablesType.getSharedConfigOrDescriptionOrSchemaCustomizer()) {
            if (tableType.getClass().getName().equals("org.labkey.data.xml.TableType")) {
                TableType table = (TableType) tableType;
                if (table.getTableTitle().equals(participantTableName)) {
                    participantsTableList.add(table);
                }
            }
        }
        return participantsTableList;
    }

    // endregion

    //region Constructor has input stream

    @Test
    public void does_not_throws_if_inputstream_is_null() throws JAXBException {
        new DatasetsMetadataXmlFileBuilder(null,labKeyExportConfiguration);
    }

    @Test
    public void does_not_throw_on_empty_table() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-metadata/has_no_tables.xml");

        DatasetsMetadataXmlFileBuilder facade = new DatasetsMetadataXmlFileBuilder(new FileInputStream(file), labKeyExportConfiguration);
        facade.build();
    }

    @Test
    public void adds_a_participants_table_if_input_does_not_have() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-metadata/has_no_tables.xml");

        DatasetsMetadataXmlFileBuilder facade = new DatasetsMetadataXmlFileBuilder(new FileInputStream(file), labKeyExportConfiguration);
        String content = facade.build();

        TablesType tablesType = JAXBHelper.unmashalString(TablesType.class, content);

        List<TableType> participantsTableList = getListOfMatchingTables(tablesType, "SubjectAttributes");

        assertEquals(1, participantsTableList.size());
    }

    @Test
    public void adds_a_new_item() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-metadata/has_events.xml");

        String dummyFormOid = "DummyFormOid";
        String dummyTableName = "DummyFormOid";

        List<FormBasedCrfAttributeTable> formBasedCrfAttributeTableList = getCrfAttributeTableListWithDummyTable(dummyFormOid, dummyTableName);
        List<ItemGroupBasedCrfAttributeTable> itemGroupBasedCrfAttributeTableList = new ArrayList<>();

        DatasetsMetadataXmlFileBuilder facade = new DatasetsMetadataXmlFileBuilder(new FileInputStream(file), labKeyExportConfiguration, formBasedCrfAttributeTableList, itemGroupBasedCrfAttributeTableList, subjectColumnName);
        facade.createFormBasedCrfAttributeTableItems(dummyFormOid);
        String content = facade.build();

        TablesType tablesType = JAXBHelper.unmashalString(TablesType.class, content);

        List<TableType> dummyTableList = getListOfMatchingTables(tablesType, dummyTableName);

        assertEquals(1, dummyTableList.size());
    }

    private List<FormBasedCrfAttributeTable> getCrfAttributeTableListWithDummyTable(String dummyFormOid, String dummyTableName) {
        mock(FormDefinition.class);
        FormDefinition formDefinition = new FormDefinition();
        formDefinition.setFormOid(dummyFormOid);
        formDefinition.setOid(dummyFormOid);
        formDefinition.setId(2);
        formDefinition.setName(dummyTableName);
        formDefinition.setItemGroupDefs(new ArrayList<>());

        FormDetails formDetailsMock = mock(FormDetails.class);
        formDefinition.setFormDetails(formDetailsMock);

        LabKeyExportConfiguration labKeyExportConfiguration = mock(LabKeyExportConfiguration.class);

        FormBasedCrfAttributeTable formBasedCrfAttributeTable = new FormBasedCrfAttributeTable(formDefinition, LABKEY_PARTICIPANT_ID, labKeyExportConfiguration);
        formBasedCrfAttributeTable.setItemDefinitionList(new ArrayList<>());
        List<FormBasedCrfAttributeTable> formBasedCrfAttributeTableList = new ArrayList<>();
        formBasedCrfAttributeTableList.add(formBasedCrfAttributeTable);
        formBasedCrfAttributeTable.setItemGroupDefinitionList(new ArrayList<>());
        formBasedCrfAttributeTable.createOrderedItemReferencesLists();
        return formBasedCrfAttributeTableList;
    }

    @Test
    public void keeps_the_existing_items() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-metadata/has_events.xml");

        String dummyFormOid = "DummyFormOid";
        String dummyTableName = "DummyTableName";

        List<FormBasedCrfAttributeTable> formBasedCrfAttributeTableList = getCrfAttributeTableListWithDummyTable(dummyFormOid, dummyTableName);
        List<ItemGroupBasedCrfAttributeTable> itemGroupBasedCrfAttributeTableList = new ArrayList<>();

        DatasetsMetadataXmlFileBuilder facade = new DatasetsMetadataXmlFileBuilder(new FileInputStream(file), labKeyExportConfiguration, formBasedCrfAttributeTableList, itemGroupBasedCrfAttributeTableList, subjectColumnName);
        facade.createFormBasedCrfAttributeTableItems(dummyFormOid);
        String content = facade.build();

        TablesType tablesType = JAXBHelper.unmashalString(TablesType.class, content);

        List<TableType> eventAttributTableList = getListOfMatchingTables(tablesType, "EventAttributes");

        assertEquals(1, eventAttributTableList.size());
    }


    // endregion

}
