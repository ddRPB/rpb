package org.labkey.data.xml;

import de.dktk.dd.rpb.core.util.JAXBHelper;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TablesTypeTest {

    private final String filePath = "src/test/data/labkey/datasets_metadata.xml";

    @Test
    public void read_datasets_metadata_xml() throws JAXBException {
        File file = new File(filePath);

        TablesType tablesType = JAXBHelper.unmashalFile(TablesType.class, file);
        List tableList = tablesType.getSharedConfigOrDescriptionOrSchemaCustomizer();

        assertNotNull(tablesType);
        assertEquals(1, tableList.size());
    }

    @Test
    public void write_datasets_metadata_xml() throws JAXBException {
        TablesType tablesType = new TablesType();
        List<Object> tablesTypeList = new ArrayList<>();

        TableType tableTypeOne = new TableType();
        tableTypeOne.setTableTitle("Participants");
        tableTypeOne.setDescription("Contains up to one row of Participants data for each Participant/Visit combination.");
        tableTypeOne.setTableName("Participants");
        tableTypeOne.setTableDbType("TABLE");
        tablesTypeList.add(tableTypeOne);

        ColumnType columnTypeOne = getParticipantIdColumnType();
        ColumnType columnTypeTwo = getSequenceNumColumnType();
        ColumnType columnTypeThree = getDateColumnType();
        ColumnType columnTypeFour = getIdColumn();
        ColumnType columnTypeSix = getSecondaryIdColumn();
        ColumnType columnTypeSeven = getPatientIdColumn();
        ColumnType columnTypeEight = getGenderColumn();

        List<ColumnType> columnTypeList = new ArrayList<>();
        columnTypeList.add(columnTypeOne);
        columnTypeList.add(columnTypeTwo);
        columnTypeList.add(columnTypeThree);
        columnTypeList.add(columnTypeFour);
        columnTypeList.add(columnTypeSix);
        columnTypeList.add(columnTypeSeven);
        columnTypeList.add(columnTypeEight);

        TableType.Columns columns = new TableType.Columns();
        columns.setColumn(columnTypeList);

        tableTypeOne.setColumns(columns);

        tablesType.setSharedConfigOrDescriptionOrSchemaCustomizer(tablesTypeList);

        String obj = jaxbObjectToXML(TablesType.class, tablesType);
        System.out.println(obj);

    }

    private ColumnType getGenderColumn() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("Gender");
        columnType.setDatatype("varchar");
        columnType.setColumnTitle("Gender");
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#string");
        columnType.setScale(4000);
        return columnType;
    }

    private ColumnType getPatientIdColumn() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("PatientId");
        columnType.setDatatype("varchar");
        columnType.setColumnTitle("Patient Id");
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#string");
        columnType.setScale(4000);
        return columnType;
    }

    private ColumnType getSecondaryIdColumn() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("SecondaryId");
        columnType.setDatatype("varchar");
        columnType.setColumnTitle("SecondaryId");
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#string");
        columnType.setScale(4000);
        return columnType;
    }

    private ColumnType getIdColumn() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("Id");
        columnType.setDatatype("varchar");
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#string");
        columnType.setScale(4000);
        columnType.setColumnTitle("Id");
        return columnType;
    }

    private ColumnType getDateColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("date");
        columnType.setDatatype("timestamp");
        columnType.setPropertyURI("http://cpas.labkey.com/Study#VisitDate");
        columnType.setFormatString("date");
        return columnType;

    }

    private ColumnType getSequenceNumColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("SequenceNum");
        columnType.setDatatype("double");
        columnType.setColumnTitle("Sequence Num");
        columnType.setPropertyURI("http://cpas.labkey.com/Study#SequenceNum");
        columnType.setNullable(false);

        ColumnType.ImportAliases importAliases = new ColumnType.ImportAliases();
        List<String> importAliasesList = new ArrayList<>();
        importAliasesList.add("visit");
        importAliasesList.add("visitid");
        importAliases.setImportAlias(importAliasesList);
        columnType.setImportAliases(importAliases);

        return columnType;
    }

    private ColumnType getParticipantIdColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("ParticipantId");
        columnType.setDatatype("varchar");
        columnType.setNullable(false);
        columnType.setColumnTitle("Participant ID");
        columnType.setScale(32);
        columnType.setDescription("Subject Identifier");
        columnType.setPropertyURI("http://cpas.labkey.com/Study#ParticipantId");
        ColumnType.ImportAliases importAliases = new ColumnType.ImportAliases();
        List<String> importAliasesList = new ArrayList<>();
        importAliasesList.add("ptid");
        importAliases.setImportAlias(importAliasesList);
        columnType.setImportAliases(importAliases);

        ColumnType.Fk fk = new ColumnType.Fk();
        fk.setFkDbSchema("study");
        fk.setFkTable("Participant");
        fk.setFkColumnName("ParticipantId");

        columnType.setFk(fk);
        return columnType;
    }

    private <T> String jaxbObjectToXML(Class<T> type, T obj) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(type);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(obj, sw);
        String xmlContent = sw.toString();

        return xmlContent;
    }
}