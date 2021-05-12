package de.dktk.dd.rpb.core.builder.lab;

import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.FormDetails;
import de.dktk.dd.rpb.core.domain.lab.CrfAttributeTable;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.labkey.data.xml.TableType;
import org.labkey.data.xml.TablesType;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DatasetsMetadataXmlFileBuilder.class, Logger.class, FormDefinition.class})
public class DatasetsMetadataXmlFileBuilderTest {
    @Before
    public void setUp() throws Exception {


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
        new DatasetsMetadataXmlFileBuilder(null);
    }

    @Test
    public void does_not_throw_on_empty_table() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-metadata/has_no_tables.xml");

        DatasetsMetadataXmlFileBuilder facade = new DatasetsMetadataXmlFileBuilder(new FileInputStream(file));
        facade.build();
    }

    @Test
    public void adds_a_participants_table_if_input_does_not_have() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-metadata/has_no_tables.xml");

        DatasetsMetadataXmlFileBuilder facade = new DatasetsMetadataXmlFileBuilder(new FileInputStream(file));
        String content = facade.build();

        TablesType tablesType = JAXBHelper.unmashalString(TablesType.class, content);

        List<TableType> participantsTableList = getListOfMatchingTables(tablesType, "SubjectAttributes");

        assertEquals(1, participantsTableList.size());
    }

    @Test
    public void adds_a_new_item() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-metadata/has_events.xml");

        String dummyFormOid = "DummyFormOid";
        String dummyTableName = "DummyTableName";

        List<CrfAttributeTable> crfAttributeTableList = getCrfAttributeTableListWithDummyTable(dummyFormOid, dummyTableName);

        DatasetsMetadataXmlFileBuilder facade = new DatasetsMetadataXmlFileBuilder(new FileInputStream(file), crfAttributeTableList);
        facade.createCrfAttributeTable(dummyFormOid);
        String content = facade.build();

        TablesType tablesType = JAXBHelper.unmashalString(TablesType.class, content);

        List<TableType> dummyTableList = getListOfMatchingTables(tablesType, dummyTableName);

        assertEquals(1, dummyTableList.size());
    }

    private List<CrfAttributeTable> getCrfAttributeTableListWithDummyTable(String dummyFormOid, String dummyTableName) {
        mock(FormDefinition.class);
        FormDefinition formDefinition = new FormDefinition();
        formDefinition.setFormOid(dummyFormOid);
        formDefinition.setOid(dummyFormOid);
        formDefinition.setId(2);
        formDefinition.setName(dummyTableName);

        FormDetails formDetailsMock = mock(FormDetails.class);
        formDefinition.setFormDetails(formDetailsMock);

        CrfAttributeTable crfAttributeTable = new CrfAttributeTable(formDefinition);
        List<CrfAttributeTable> crfAttributeTableList = new ArrayList<>();
        crfAttributeTableList.add(crfAttributeTable);
        return crfAttributeTableList;
    }

    @Test
    public void keeps_the_existing_items() throws JAXBException, FileNotFoundException {
        File file = new File("src/test/data/labkey/datasets-metadata/has_events.xml");

        String dummyFormOid = "DummyFormOid";
        String dummyTableName = "DummyTableName";

        List<CrfAttributeTable> crfAttributeTableList = getCrfAttributeTableListWithDummyTable(dummyFormOid, dummyTableName);

        DatasetsMetadataXmlFileBuilder facade = new DatasetsMetadataXmlFileBuilder(new FileInputStream(file), crfAttributeTableList);
        facade.createCrfAttributeTable(dummyFormOid);
        String content = facade.build();

        TablesType tablesType = JAXBHelper.unmashalString(TablesType.class, content);

        List<TableType> eventAttributTableList = getListOfMatchingTables(tablesType, "EventAttributes");

        assertEquals(1, eventAttributTableList.size());
    }


    // endregion

}