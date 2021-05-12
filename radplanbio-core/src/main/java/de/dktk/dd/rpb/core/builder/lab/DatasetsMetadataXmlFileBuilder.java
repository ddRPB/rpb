package de.dktk.dd.rpb.core.builder.lab;

import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.lab.CrfAttributeTable;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import org.apache.log4j.Logger;
import org.labkey.data.xml.ColumnType;
import org.labkey.data.xml.TableType;
import org.labkey.data.xml.TablesType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * Provides the content for the /datasets/datasets_metadata.xml file of the LabKey export
 */
public class DatasetsMetadataXmlFileBuilder {

    private static final Logger log = Logger.getLogger(DatasetsMetadataXmlFileBuilder.class);

    private HashMap<String, TableType> tables = new HashMap<>();
    private List<CrfAttributeTable> crfAttributeTableList;

    public DatasetsMetadataXmlFileBuilder() {
        log.debug("Creating DatasetsMetadataXmlFileBuilder instance without arguments");
    }

    /***
     * Builder of the content for the /datasets/datasets_metadata.xml file of the LabKey export
     * @param inputStream InputStream of an existing datasets_metadata.xml file
     * @throws JAXBException exceptions related to the unmarshalling with JAXB
     */
    public DatasetsMetadataXmlFileBuilder(InputStream inputStream) throws JAXBException {
        log.debug("Creating DatasetsMetadataXmlFileBuilder instance with InputStream argument");
        if (inputStream != null) {
            JAXBElement element = JAXBHelper.unmarshalInputstream(TablesType.class, inputStream);
            List<Object> tableList = ((TablesType) element.getValue()).getSharedConfigOrDescriptionOrSchemaCustomizer();
            for (Object table : tableList) {
                if (table.getClass().getName().equals("org.labkey.data.xml.TableType")) {
                    String title = ((TableType) table).getTableTitle();
                    tables.put(title, (TableType) table);
                }
            }
        } else {
            log.warn("InputStream is null");
        }
    }

    /**
     * Builder of the content for the /datasets/datasets_metadata.xml file of the LabKey export
     *
     * @param inputStream           InputStream of an existing datasets_metadata.xml file
     * @param crfAttributeTableList Information from the MetaDataVersion part of the ODM export
     * @throws JAXBException exceptions related to the unmarshalling with JAXB
     */
    public DatasetsMetadataXmlFileBuilder(InputStream inputStream, List<CrfAttributeTable> crfAttributeTableList) throws JAXBException {
        log.debug("Creating DatasetsMetadataXmlFileBuilder instance with InputStream and CrfAttributeTable list arguments");
        if (crfAttributeTableList == null) {
            log.warn("crfAttributeTableList is null");
        }
        this.crfAttributeTableList = crfAttributeTableList;
        new DatasetsMetadataXmlFileBuilder(inputStream);
    }

    /**
     * Build step that returns the content prepared by the builder
     *
     * @return /datasets/datasets_metadata.xml file content as a String
     * @throws JAXBException exception related to the marshalling step
     */
    public String build() throws JAXBException {

        List<Object> tablesTypeList = new ArrayList<>();

        this.createNewParticipantsTable();
        this.createNewEventDataTable();

        tablesTypeList.addAll(this.tables.values());

        TablesType tablesType = new TablesType();
        tablesType.setSharedConfigOrDescriptionOrSchemaCustomizer(tablesTypeList);

        return JAXBHelper.jaxbObjectToXML(TablesType.class, tablesType);
    }

    /**
     * Creates a table that includes demographic information about the study subjects
     *
     * @return DatasetsMetadataXmlFileBuilder
     */
    private DatasetsMetadataXmlFileBuilder createNewParticipantsTable() {
        log.debug("Adding participants table.");
        TableType particpantsTable = new TableType();
        this.tables.put("SubjectAttributes", particpantsTable);
        particpantsTable.setTableTitle("SubjectAttributes");
        particpantsTable.setDescription("Contains up to one row of Participants data for each Participant/Visit combination.");
        particpantsTable.setTableName("SubjectAttributes");
        particpantsTable.setTableDbType("TABLE");

        List<ColumnType> columnTypeList = new ArrayList<>();
        columnTypeList.add(getSequenceNumColumnType());
        columnTypeList.add(getParticipantIdColumnType());
        columnTypeList.add(getPatientIdColumn());
        columnTypeList.add(getSecondaryIdColumn());
        columnTypeList.add(getGenderColumn());
        columnTypeList.add(getDateTimeColumn("BirthDate", "BirthDate", "Date of Birth"));
        columnTypeList.add(getBirthYearColumnType());
        columnTypeList.add(getDateTimeColumn("Enrollment", "Enrollment", "Enrollment date"));
        columnTypeList.add(getStatusColumn());

        TableType.Columns columns = new TableType.Columns();
        columns.setColumn(columnTypeList);

        particpantsTable.setColumns(columns);
        return this;
    }

    /**
     * Creates a table that includes general information about the study events
     *
     * @return DatasetsMetadataXmlFileBuilder
     */
    private DatasetsMetadataXmlFileBuilder createNewEventDataTable() {
        log.debug("Adding events table.");
        TableType eventTable = new TableType();
        this.tables.put("EventAttributes", eventTable);
        eventTable.setTableTitle("EventAttributes");
        eventTable.setDescription("Contains up to one row of Participants data for each Participant/Visit combination.");
        eventTable.setTableName("EventAttributes");
        eventTable.setTableDbType("TABLE");

        List<ColumnType> columnTypeList = new ArrayList<>();
        columnTypeList.add(getParticipantIdColumnType());
        columnTypeList.add(getSequenceNumColumnType());
        columnTypeList.add(getDateColumnType());
        columnTypeList.add(getStudyEventOIDColumn());
        columnTypeList.add(getEventNameColumn());
        columnTypeList.add(getStartDateColumn());
        columnTypeList.add(getEnd_DateColumn());
        columnTypeList.add(getStatusColumn());
        columnTypeList.add(getStringColumn("System Status", "System Status", "System Status"));
        columnTypeList.add(getStudyEventRepeatKeyColumn());
        columnTypeList.add(getStringColumn("Type", "Type", "Type"));

        TableType.Columns columns = new TableType.Columns();
        columns.setColumn(columnTypeList);

        eventTable.setColumns(columns);
        return this;
    }

    public DatasetsMetadataXmlFileBuilder createCrfAttributeTable(String formOid) {
        log.debug("Adding CrfAttribute table for: " + formOid);
        CrfAttributeTable attributeTable = getCrfAttributeTable(formOid);
        if (attributeTable != null) {
            TableType labkeyCrfAttributesTable = new TableType();
            this.tables.put(attributeTable.getName(), labkeyCrfAttributesTable);

            labkeyCrfAttributesTable.setTableTitle(attributeTable.getName());
            labkeyCrfAttributesTable.setDescription("Version Description: " + attributeTable.getVersionDescription() + " Revision Notes: " + attributeTable.getRevisionNotes());
            labkeyCrfAttributesTable.setTableName(attributeTable.getName());
            labkeyCrfAttributesTable.setTableDbType("TABLE");

            List<ItemDefinition> itemDefinitionList = attributeTable.getOrderedItemReferencesList();
            List<ColumnType> columnTypeList = new ArrayList<>();

            columnTypeList.add(getSequenceNumColumnType());
            columnTypeList.add(getParticipantIdColumnType());
            columnTypeList.add(getStudyEventOIDColumn());
            columnTypeList.add(getStudyEventRepeatKeyColumn());
            columnTypeList.add(getStringColumn("FormOid", "FormOid", "Identifier of the form"));

            for (ItemDefinition itemDefinition : itemDefinitionList) {
                String columnType = itemDefinition.getDataType();
                switch (columnType) {
                    case "integer":
                        columnTypeList.add(getIntegerColumn(itemDefinition.getOid(), itemDefinition.getName(), itemDefinition.getDescription()));
                        break;
                    case "date":
                        columnTypeList.add(getDateTimeColumn(itemDefinition.getOid(), itemDefinition.getName(), itemDefinition.getDescription()));
                        break;
                    case "text":
                        columnTypeList.add(getStringColumn(itemDefinition.getOid(), itemDefinition.getName(), itemDefinition.getDescription()));
                        break;
                    case "float":
                        columnTypeList.add(getDoubleColumn(itemDefinition.getOid(), itemDefinition.getName(), itemDefinition.getDescription()));
                        break;
                    case "double":
                        columnTypeList.add(getDoubleColumn(itemDefinition.getOid(), itemDefinition.getName(), itemDefinition.getDescription()));
                        break;
                    case "boolean":
                        columnTypeList.add(getBooleanColumn(itemDefinition.getOid(), itemDefinition.getName(), itemDefinition.getDescription()));
                        break;
                    default:
                        log.error(columnType + " does not match known types.");
                }
            }

            TableType.Columns columns = new TableType.Columns();
            columns.setColumn(columnTypeList);
            labkeyCrfAttributesTable.setColumns(columns);
        } else {
            log.warn("attributeTable is null");
        }
        return this;
    }

    private CrfAttributeTable getCrfAttributeTable(String formOid) {
        for (CrfAttributeTable table : this.crfAttributeTableList) {
            if (table.getOid().equalsIgnoreCase(formOid)) {
                return table;
            }
        }
        log.warn("Could not find CrfAttributeTable with oid: " + formOid);
        return null;
    }

    private ColumnType getStudyEventRepeatKeyColumn() {
        return getIntegerColumn("StudyEventRepeatKey", "StudyEventRepeatKey", "RepeatKey of the study event");
    }

    private ColumnType getStatusColumn() {
        return getStringColumn("Status", "Status", "Status");
    }

    private ColumnType getEnd_DateColumn() {
        return getDateTimeColumn("EndDate", "EndDate", "EndDate");
    }

    private ColumnType getStartDateColumn() {
        return getDateTimeColumn("StartDate", "StartDate", "StartDate");
    }

    private ColumnType getEventNameColumn() {
        return getStringColumn("EventName", "Event Name", "Name of the study event");
    }

    private ColumnType getStudyEventOIDColumn() {
        return getStringColumn("StudyEventOID", "Study Event OID", "Identifier of the study event");
    }

    private ColumnType getDateTimeColumn(String name, String title, String description) {
        ColumnType columnType = new ColumnType();
        columnType.setDatatype("timestamp");
        columnType.setColumnTitle(title);
        columnType.setColumnName(name);
        columnType.setDescription(description);
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#dateTime");
        return columnType;
    }

    private ColumnType getParticipantIdColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("ParticipantId");
        columnType.setDatatype("varchar");
        columnType.setNullable(false);
        columnType.setColumnTitle("ParticipantId");
        columnType.setDescription("Holds the unique identifier for the study subject.");
        columnType.setScale(32);
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

    private ColumnType getSequenceNumColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("SequenceNum");
        columnType.setDatatype("double");
        columnType.setColumnTitle("Sequence Num");
        columnType.setDescription("Ordinal.RepeatKey");
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

    private ColumnType getBirthYearColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("BirthYear");
        columnType.setDescription("Year of birth");
        columnType.setDatatype("int");
        columnType.setColumnTitle("BirthYear");
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#int");

        return columnType;
    }

    private ColumnType getDateColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName("date");
        columnType.setDescription("Date");
        columnType.setDatatype("timestamp");
        columnType.setPropertyURI("http://cpas.labkey.com/Study#VisitDate");
        columnType.setFormatString("date");
        return columnType;

    }

    private ColumnType getSecondaryIdColumn() {
        return getStringColumn("SecondaryId", "Secondary Id", "Secondary Id");
    }

    private ColumnType getPatientIdColumn() {
        return getStringColumn("Pid", "Pid", "Pid");
    }

    private ColumnType getGenderColumn() {
        return getStringColumn("Gender", "Gender", "Gender");
    }

    private ColumnType getStringColumn(String name, String title, String description) {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName(name);
        columnType.setDescription(description);
        columnType.setDatatype("varchar");
        columnType.setColumnTitle(title);
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#string");
        columnType.setScale(4000);
        return columnType;
    }

    private ColumnType getIntegerColumn(String name, String title, String description) {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName(name);
        columnType.setDescription(description);
        columnType.setDatatype("integer");
        columnType.setColumnTitle(title);
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#int");
        return columnType;
    }

    private ColumnType getDoubleColumn(String name, String title, String description) {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName(name);
        columnType.setDescription(description);
        columnType.setDatatype("double");
        columnType.setColumnTitle(title);
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#double");
        return columnType;
    }

    private ColumnType getBooleanColumn(String name, String title, String description) {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName(name);
        columnType.setDescription(description);
        columnType.setDatatype("boolean");
        columnType.setColumnTitle(title);
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#boolean");
        return columnType;
    }
}
