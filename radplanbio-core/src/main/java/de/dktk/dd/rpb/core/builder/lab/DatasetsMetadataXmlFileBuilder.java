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

package de.dktk.dd.rpb.core.builder.lab;

import de.dktk.dd.rpb.core.domain.edc.EnumCollectSubjectDob;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.MultiSelectListDefinition;
import de.dktk.dd.rpb.core.domain.edc.MultiSelectListItem;
import de.dktk.dd.rpb.core.domain.edc.StudyParameterConfiguration;
import de.dktk.dd.rpb.core.domain.lab.FormBasedCrfAttributeTable;
import de.dktk.dd.rpb.core.domain.lab.ItemGroupBasedCrfAttributeTable;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import org.labkey.data.xml.ColumnType;
import org.labkey.data.xml.TableType;
import org.labkey.data.xml.TablesType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.*;

/***
 * Provides the content for the /datasets/datasets_metadata.xml file of the LabKey export.
 * Here, we define the name, label, description of the datasets and the columns.
 */
public class DatasetsMetadataXmlFileBuilder {

    private static final Logger log = LoggerFactory.getLogger(DatasetsMetadataXmlFileBuilder.class);

    private final LabKeyExportConfiguration labKeyExportConfiguration;

    private String studySubjectColumnName = LABKEY_PARTICIPANT_ID;

    private HashMap<String, TableType> tables = new HashMap<>();
    private List<FormBasedCrfAttributeTable> formBasedCrfAttributeTableList;
    private List<ItemGroupBasedCrfAttributeTable> itemGroupBasedCrfAttributeTableList;

    public DatasetsMetadataXmlFileBuilder() {
        log.debug("Creating DatasetsMetadataXmlFileBuilder instance without arguments");
        StudyParameterConfiguration parameterConfiguration = new StudyParameterConfiguration();
        parameterConfiguration.setCollectSubjectDob(EnumCollectSubjectDob.YES);
        this.labKeyExportConfiguration = new LabKeyExportConfiguration(parameterConfiguration);
    }

    /***
     * Builder of the content for the /datasets/datasets_metadata.xml file of the LabKey export.
     * Here, we define the name, label, description of the datasets and the columns.
     * @param inputStream InputStream of an existing datasets_metadata.xml file
     * @param labKeyExportConfiguration Configuration object where UI user defined the parameter of the export
     * @throws JAXBException exceptions related to the unmarshalling with JAXB
     */
    public DatasetsMetadataXmlFileBuilder(InputStream inputStream, LabKeyExportConfiguration labKeyExportConfiguration) throws JAXBException {
        this.labKeyExportConfiguration = labKeyExportConfiguration;
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
     * Builder of the content for the /datasets/datasets_metadata.xml file of the LabKey export.
     * Here, we define the name, label, description of the datasets and the columns.
     *
     * @param inputStream InputStream of an existing datasets_metadata.xml file
     * @param labKeyExportConfiguration Configuration object where UI user defined the parameter of the export
     * @param formBasedCrfAttributeTableList ordered list of FormBasedCrfAttributeTables to create meta data for FormAttribute datasets
     * @param itemGroupBasedCrfAttributeTableList ordered list of ItemGroupBasedCrfAttributeTables to create meta data for ItemGroup datasets
     * @param subjectColumnName alternative name for the subject column read from the LabKey configuration
     * @throws JAXBException exceptions related to the unmarshalling with JAXB
     */
    public DatasetsMetadataXmlFileBuilder(
            InputStream inputStream,
            LabKeyExportConfiguration labKeyExportConfiguration,
            List<FormBasedCrfAttributeTable> formBasedCrfAttributeTableList,
            List<ItemGroupBasedCrfAttributeTable> itemGroupBasedCrfAttributeTableList,
            String subjectColumnName
    ) throws JAXBException {
        log.debug("Creating DatasetsMetadataXmlFileBuilder instance with InputStream, FormBasedCrfAttributeTable List, " +
                "ItemGroupBasedCrfAttributeTable List, and subjectColumnName arguments");

        this.labKeyExportConfiguration = labKeyExportConfiguration;

        if (formBasedCrfAttributeTableList == null) {
            log.debug("formBasedCrfAttributeTableList is null");
        }

        if (itemGroupBasedCrfAttributeTableList == null) {
            log.debug("itemGroupBasedCrfAttributeTableList is null");
        }

        if (!subjectColumnName.isEmpty()) {
            this.studySubjectColumnName = subjectColumnName;
        }

        this.formBasedCrfAttributeTableList = formBasedCrfAttributeTableList;
        this.itemGroupBasedCrfAttributeTableList = itemGroupBasedCrfAttributeTableList;
        new DatasetsMetadataXmlFileBuilder(inputStream, this.labKeyExportConfiguration);
    }

    /**
     * Build step that returns the content prepared by the builder.
     *
     * @return /datasets/datasets_metadata.xml file content as a String
     * @throws JAXBException exception related to the marshalling step
     */
    public String build() throws JAXBException {

        List<Object> tablesTypeList = new ArrayList<>();

        this.createSubjectAttributesTableItems();
        this.createSubjectGroupsTableItems();
        this.createEventAttributesTableItems();
        this.createFormAttributesTableItems();

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
    private DatasetsMetadataXmlFileBuilder createSubjectAttributesTableItems() {
        log.debug("Adding SubjectAttributes table meta data.");
        TableType subjectAttributesTable = new TableType();
        this.tables.put(LABKEY_SUBJECT_ATTRIBUTES, subjectAttributesTable);
        subjectAttributesTable.setTableTitle(LABKEY_SUBJECT_ATTRIBUTES);
        subjectAttributesTable.setDescription("Contains up to one row of Participants data for each Participant/Visit combination.");
        subjectAttributesTable.setTableName(LABKEY_SUBJECT_ATTRIBUTES);
        subjectAttributesTable.setTableDbType("TABLE");

        List<ColumnType> columnTypeList = new ArrayList<>();
        columnTypeList.add(getSequenceNumColumnType());
        columnTypeList.add(getStudySubjectIdColumnType());
        columnTypeList.add(getPatientIdColumn());
        columnTypeList.add(getSecondaryIdColumn());

        if (this.labKeyExportConfiguration.isSexRequired()) {
            columnTypeList.add(getGenderColumn());
        }
        if (this.labKeyExportConfiguration.isFullDateOfBirthRequired()) {
            columnTypeList.add(getDateTimeColumn("BirthDate", "BirthDate", "Date of Birth"));
        }
        if (this.labKeyExportConfiguration.isYearOfBirthRequired()) {
            columnTypeList.add(getBirthYearColumnType());
        }
        columnTypeList.add(getDateTimeColumn("Enrollment", "Enrollment", "Enrollment date"));
        columnTypeList.add(getStatusColumn());

        TableType.Columns columns = new TableType.Columns();
        columns.setColumn(columnTypeList);

        subjectAttributesTable.setColumns(columns);
        return this;
    }

    /**
     * Creates a table that reflects the assignment of the study subjects to subject groups
     *
     * @return DatasetsMetadataXmlFileBuilder
     */
    private DatasetsMetadataXmlFileBuilder createSubjectGroupsTableItems() {
        log.debug("Adding " + LABKEY_SUBJECT_GROUP_ATTRIBUTES + " table meta data.");
        TableType subjectGroupAttributesTable = new TableType();
        this.tables.put(LABKEY_SUBJECT_GROUP_ATTRIBUTES, subjectGroupAttributesTable);
        subjectGroupAttributesTable.setTableTitle(LABKEY_SUBJECT_GROUP_ATTRIBUTES);
        subjectGroupAttributesTable.setDescription("Contains subject to subject group mapping.");
        subjectGroupAttributesTable.setTableName(LABKEY_SUBJECT_GROUP_ATTRIBUTES);
        subjectGroupAttributesTable.setTableDbType("TABLE");

        List<ColumnType> columnTypeList = new ArrayList<>();
        columnTypeList.add(getSequenceNumColumnType());
        columnTypeList.add(getStudySubjectIdColumnType());
        columnTypeList.add(getPatientIdColumn());
        columnTypeList.add(getKeyStringColumn(LABKEY_STUDY_GROUP_CLASS_ID, LABKEY_STUDY_GROUP_CLASS_ID, LABKEY_STUDY_GROUP_CLASS_ID));
        columnTypeList.add(getStringColumn(LABKEY_STUDY_GROUP_CLASS_NAME, LABKEY_STUDY_GROUP_CLASS_NAME, LABKEY_STUDY_GROUP_CLASS_NAME));
        columnTypeList.add(getStringColumn(LABKEY_STUDY_GROUP_NAME, LABKEY_STUDY_GROUP_NAME, LABKEY_STUDY_GROUP_NAME));

        TableType.Columns columns = new TableType.Columns();
        columns.setColumn(columnTypeList);

        subjectGroupAttributesTable.setColumns(columns);
        return this;
    }

    /**
     * Creates a table that includes general information about the study events
     *
     * @return DatasetsMetadataXmlFileBuilder
     */
    private DatasetsMetadataXmlFileBuilder createEventAttributesTableItems() {
        log.debug("Adding EventAttributes table meta data.");
        TableType eventAttributesTable = new TableType();
        this.tables.put(LABKEY_EVENT_ATTRIBUTES, eventAttributesTable);
        eventAttributesTable.setTableTitle(LABKEY_EVENT_ATTRIBUTES);
        eventAttributesTable.setDescription("Contains up to one row of Participants data for each Participant/Visit combination.");
        eventAttributesTable.setTableName(LABKEY_EVENT_ATTRIBUTES);
        eventAttributesTable.setTableDbType("TABLE");

        List<ColumnType> columnTypeList = new ArrayList<>();
        columnTypeList.add(getSequenceNumColumnType());
        columnTypeList.add(getStudySubjectIdColumnType());
        // Can be removed if feature is tested and this optional property is not needed
        // columnTypeList.add(getDateColumnType());
        columnTypeList.add(getStudyEventOIDColumn());
        columnTypeList.add(getEventNameColumn());
        columnTypeList.add(getStartDateColumn());
        columnTypeList.add(getEnd_DateColumn());
        columnTypeList.add(getStatusColumn());
        columnTypeList.add(getStringColumn(LABKEY_SYSTEM_STATUS, LABKEY_SYSTEM_STATUS, LABKEY_SYSTEM_STATUS));
        columnTypeList.add(getStudyEventRepeatKeyColumn());
        columnTypeList.add(getStringColumn(LABKEY_TYPE, LABKEY_TYPE, LABKEY_TYPE));

        TableType.Columns columns = new TableType.Columns();
        columns.setColumn(columnTypeList);

        eventAttributesTable.setColumns(columns);
        return this;
    }

    /**
     * Creates a table that has information about the status of the forms per patient and event
     * @return DatasetsMetadataXmlFileBuilder
     */
    private DatasetsMetadataXmlFileBuilder createFormAttributesTableItems() {
        log.debug("Adding FormAttributes table meta data.");
        TableType formAttributesTable = new TableType();
        this.tables.put(LABKEY_FORM_ATTRIBUTES, formAttributesTable);
        formAttributesTable.setTableTitle(LABKEY_FORM_ATTRIBUTES);
        formAttributesTable.setDescription("Contains up to one row of form meta data for each Participant/Visit/Form combination.");
        formAttributesTable.setTableName(LABKEY_FORM_ATTRIBUTES);
        formAttributesTable.setTableDbType("TABLE");

        List<ColumnType> columnTypeList = new ArrayList<>();
        columnTypeList.add(getStudySubjectIdColumnType());
        columnTypeList.add(getStudyEventRepeatKeyColumn());
        columnTypeList.add(getKeyStringColumn(LABKEY_FORM_OID, LABKEY_FORM_OID, LABKEY_FORM_OID));
        columnTypeList.add(getStringColumn(LABKEY_FORM_VERSION, LABKEY_FORM_VERSION, LABKEY_FORM_VERSION));
        columnTypeList.add(getStringColumn(LABKEY_INTERVIEWER_NAME, LABKEY_INTERVIEWER_NAME, LABKEY_INTERVIEWER_NAME));
        columnTypeList.add(getDateTimeColumn(LABKEY_INTERVIEW_DATE, LABKEY_INTERVIEW_DATE, LABKEY_INTERVIEW_DATE));
        columnTypeList.add(getStringColumn(LABKEY_STATUS, LABKEY_STATUS, LABKEY_STATUS));

        TableType.Columns columns = new TableType.Columns();
        columns.setColumn(columnTypeList);

        formAttributesTable.setColumns(columns);
        return this;
    }

    /**
     * Creates a FormAttributesTable that reflects the content of a form
     * @param formOid OID of the form
     * @return @return DatasetsMetadataXmlFileBuilder
     */
    public DatasetsMetadataXmlFileBuilder createFormBasedCrfAttributeTableItems(String formOid) {
        log.debug("Adding FormBasedCrfAttributes forms table meta data for form: " + formOid);
        FormBasedCrfAttributeTable attributeTable = getFormBasedCrfAttributeTable(formOid);
        if (attributeTable != null) {
            TableType labkeyCrfAttributesTable = new TableType();
            this.tables.put(attributeTable.getName(), labkeyCrfAttributesTable);

            labkeyCrfAttributesTable.setTableTitle(attributeTable.getName());
            labkeyCrfAttributesTable.setDescription("Version Description: " + attributeTable.getVersionDescription() +
                    " Revision Notes: " + attributeTable.getRevisionNotes());
            labkeyCrfAttributesTable.setTableName(attributeTable.getOid());
            labkeyCrfAttributesTable.setTableDbType("TABLE");

            List<ItemDefinition> itemDefinitionList = attributeTable.getOrderedItemDefinitionsListWithRepeatKeyClones();
            List<ColumnType> columnTypeList = new ArrayList<>();

            columnTypeList.add(getSequenceNumColumnType());
            columnTypeList.add(getStudySubjectIdColumnType());
            columnTypeList.add(getPatientIdColumn());
            columnTypeList.add(getStudyEventOIDColumn());
            columnTypeList.add(getStudyEventRepeatKeyColumn());
            columnTypeList.add(getStringColumn(LABKEY_STUDY_SITE_IDENTIFIER, LABKEY_STUDY_SITE_IDENTIFIER, "Identifier of the study site"));
            columnTypeList.add(getStringColumn(LABKEY_FORM_OID, LABKEY_FORM_OID, "Identifier of the form"));

            addColumnsTypeToListBasedOnItemDefinitionList(itemDefinitionList, columnTypeList);

            TableType.Columns columns = new TableType.Columns();
            columns.setColumn(columnTypeList);
            labkeyCrfAttributesTable.setColumns(columns);
        } else {
            log.warn("attributeTable is null");
        }
        return this;
    }

    private FormBasedCrfAttributeTable getFormBasedCrfAttributeTable(String formOid) {
        for (FormBasedCrfAttributeTable table : this.formBasedCrfAttributeTableList) {
            if (table.getOid().equalsIgnoreCase(formOid)) {
                return table;
            }
        }
        log.warn("Could not find FormBasedCrfAttributeTable with oid: " + formOid);
        return null;
    }

    private ColumnType getSequenceNumColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName(LABKEY_SEQUENCE_NUMBER);
        columnType.setDatatype("double");
        columnType.setColumnTitle(LABKEY_SEQUENCE_NUMBER);
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

    private ColumnType getStudySubjectIdColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName(this.studySubjectColumnName);
        columnType.setDatatype("varchar");
        columnType.setNullable(false);
        columnType.setColumnTitle(this.studySubjectColumnName);
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
        fk.setFkColumnName(this.studySubjectColumnName);

        columnType.setFk(fk);
        return columnType;
    }

    private ColumnType getStudyEventOIDColumn() {
        return getStringColumn(LABKEY_STUDY_EVENT_OID, LABKEY_STUDY_EVENT_OID, "Identifier of the study event");
    }

    private ColumnType getStudyEventRepeatKeyColumn() {
        return getIntegerColumn(LABKEY_STUDY_EVENT_REPEAT_KEY, LABKEY_STUDY_EVENT_REPEAT_KEY, "RepeatKey of the study event");
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

    private void addColumnsTypeToListBasedOnItemDefinitionList(List<ItemDefinition> itemDefinitionList, List<ColumnType> columnTypeList) {
        for (ItemDefinition itemDefinition : itemDefinitionList) {
            String columnType = itemDefinition.getDataType();
            // values and partial dates
            addColumnTypeFromDefinition(columnTypeList, itemDefinition, columnType);
            // decoded Values
            addDecodedValueColumn(columnTypeList, itemDefinition);
            // multi selects
            addMultiSelectColumns(columnTypeList, itemDefinition);

        }
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

    private void addColumnTypeFromDefinition(List<ColumnType> columnTypeList, ItemDefinition itemDefinition, String columnType) {
        switch (columnType) {
            case "integer":
                columnTypeList.add(getIntegerColumn(itemDefinition.getOid(), itemDefinition.getName(), itemDefinition.getDescription()));
                break;
            case "date":
                columnTypeList.add(getDateTimeColumn(itemDefinition.getOid(), itemDefinition.getName(), itemDefinition.getDescription()));
                break;
            case "partialDate":
                columnTypeList.add(getStringColumn(itemDefinition.getOid(), itemDefinition.getName(), itemDefinition.getDescription()));

                addPartialDateColumn(columnTypeList, itemDefinition);

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

    private void addDecodedValueColumn(List<ColumnType> columnTypeList, ItemDefinition itemDefinition) {
        // decoded value
        if (this.labKeyExportConfiguration.isAddDecodedValueColumns()) {
            if (itemDefinition.getCodeListDef() != null) {
                columnTypeList.add(getStringColumn(
                        itemDefinition.getOid() + LABKEY_DECODED_VALUE_SUFFIX,
                        itemDefinition.getName() + LABKEY_DECODED_VALUE_SUFFIX,
                        itemDefinition.getDescription() + LABKEY_DECODED_VALUE_SUFFIX)
                );
            }
        }
    }

    private void addMultiSelectColumns(List<ColumnType> columnTypeList, ItemDefinition itemDefinition) {
        if (this.labKeyExportConfiguration.isAddMultiSelectValueColumns()) {
            if (itemDefinition.getMultiSelectListDef() != null) {
                MultiSelectListDefinition definition = itemDefinition.getMultiSelectListDef();

                for (MultiSelectListItem multiSelectListItem : definition.getMultiSelectListItems()) {
                    String codedValue = multiSelectListItem.getCodedOptionValue();
                    String translatedText = multiSelectListItem.getDecodedText();

                    columnTypeList.add(getBooleanColumn(
                            itemDefinition.getOid() + LABKEY_MULTISELECT_VALUE_SUFFIX + codedValue,
                            itemDefinition.getName() + LABKEY_MULTISELECT_VALUE_SUFFIX + codedValue,
                            itemDefinition.getDescription() + " " + translatedText)
                    );
                }
            }
        }
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

    private void addPartialDateColumn(List<ColumnType> columnTypeList, ItemDefinition itemDefinition) {
        if (this.labKeyExportConfiguration.isAddPartialDateColumns()) {

            columnTypeList.add(getDateTimeColumn(
                    itemDefinition.getOid() + LABKEY_PARTIAL_DATE_MIN_SUFFIX,
                    itemDefinition.getName() + LABKEY_PARTIAL_DATE_MIN_SUFFIX,
                    itemDefinition.getDescription() + LABKEY_PARTIAL_DATE_MIN_SUFFIX)
            );

            columnTypeList.add(getDateTimeColumn(
                    itemDefinition.getOid() + LABKEY_PARTIAL_DATE_MAX_SUFFIX,
                    itemDefinition.getName() + LABKEY_PARTIAL_DATE_MAX_SUFFIX,
                    itemDefinition.getDescription() + LABKEY_PARTIAL_DATE_MAX_SUFFIX)
            );
        }
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

    /**
     * Creates an ItemGroupAttributesTable that reflects the content of a form
     * @param oid ItemGroup OID
     * @return DatasetsMetadataXmlFileBuilder
     */
    public DatasetsMetadataXmlFileBuilder createItemGroupBasedCrfTableItems(String oid) {
        log.debug("Adding ItemGroupBasedCrfAttributes table meta data for item group: " + oid);
        ItemGroupBasedCrfAttributeTable attributeTable = getItemGroupCrfAttributeTable(oid);
        if (attributeTable != null) {
            TableType labKeyCrfAttributesTable = new TableType();
            this.tables.put(attributeTable.getItemGroupOid(), labKeyCrfAttributesTable);
            String name = attributeTable.getName();
            String comment = attributeTable.getComment();
            String itemGroupOid = attributeTable.getItemGroupOid();

            String tableTitle = name;
            String tableDescription = "Name: " + name + " OID: " + itemGroupOid;

            if (comment != null) {
                tableTitle = tableTitle + " " + LABKEY_IDENTIFIERSEP + " " + comment;
                tableDescription = tableDescription + " Comment: " + comment;
            }

            tableTitle = tableTitle + " " + LABKEY_IDENTIFIERSEP + " " + " [" + itemGroupOid + "]";

            labKeyCrfAttributesTable.setTableTitle(tableTitle);
            labKeyCrfAttributesTable.setDescription(tableDescription);
            labKeyCrfAttributesTable.setTableName(itemGroupOid);
            labKeyCrfAttributesTable.setTableDbType("TABLE");

            List<ItemDefinition> itemDefinitionList = attributeTable.getOrderedItemReferencesList();
            List<ColumnType> columnTypeList = new ArrayList<>();

            columnTypeList.add(getSequenceNumColumnType());
            columnTypeList.add(getStudySubjectIdColumnType());
            columnTypeList.add(getPatientIdColumn());
            columnTypeList.add(getStudyEventOIDColumn());
            columnTypeList.add(getStudyEventRepeatKeyColumn());
            columnTypeList.add(getStringColumn(LABKEY_STUDY_SITE_IDENTIFIER, LABKEY_STUDY_SITE_IDENTIFIER, "Identifier of the study site"));
            columnTypeList.add(getStringColumn(LABKEY_FORM_OID, LABKEY_FORM_OID, "Identifier of the form"));
            columnTypeList.add(getIntegerColumn(LABKEY_FORM_REPEAT_KEY, LABKEY_FORM_REPEAT_KEY, "Repeat key of the form"));
            columnTypeList.add(getStringColumn(LABKEY_ITEM_GROUP_OID, LABKEY_ITEM_GROUP_OID, "Identifier of the item group"));
            columnTypeList.add(getIntegerColumn(LABKEY_ITEM_GROUP_REPEAT_KEY, LABKEY_ITEM_GROUP_REPEAT_KEY, "Identifier of the item group"));
            columnTypeList.add(getKeyStringColumn(LABKEY_FORM_ITEM_GROUP_COMBINED_ID, LABKEY_FORM_ITEM_GROUP_COMBINED_ID, "Combined identifier (Form and ItemGroup OIDs/RepeatKeys)."));

            addColumnsTypeToListBasedOnItemDefinitionList(itemDefinitionList, columnTypeList);

            TableType.Columns columns = new TableType.Columns();
            columns.setColumn(columnTypeList);
            labKeyCrfAttributesTable.setColumns(columns);
        } else {
            log.debug("attributeTable is null");
        }
        return this;
    }

    private ItemGroupBasedCrfAttributeTable getItemGroupCrfAttributeTable(String oid) {
        for (ItemGroupBasedCrfAttributeTable table : this.itemGroupBasedCrfAttributeTableList) {
            if (table.getItemGroupOid().equalsIgnoreCase(oid)) {
                return table;
            }
        }
        log.debug("Could not find ItemGroupBasedCrfAttributeTable with oid: " + oid);
        return null;
    }

    private ColumnType getKeyStringColumn(String name, String title, String description) {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName(name);
        columnType.setDescription(description);
        columnType.setDatatype("varchar");
        columnType.setColumnTitle(title);
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#string");
        columnType.setScale(4000);
        columnType.setIsKeyField(true);
        return columnType;
    }

    private ColumnType getStatusColumn() {
        return getStringColumn(LABKEY_STATUS, LABKEY_STATUS, LABKEY_STATUS);
    }

    private ColumnType getEnd_DateColumn() {
        return getDateTimeColumn(LABKEY_END_DATE, LABKEY_END_DATE, LABKEY_END_DATE);
    }

    private ColumnType getStartDateColumn() {
        return getDateTimeColumn(LABKEY_START_DATE, LABKEY_START_DATE, LABKEY_START_DATE);
    }

    private ColumnType getEventNameColumn() {
        return getStringColumn(LABKEY_EVENT_NAME, LABKEY_EVENT_NAME, "Name of the study event");
    }

    private ColumnType getBirthYearColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName(LABKEY_BIRTHYEAR);
        columnType.setDescription(LABKEY_BIRTHYEAR);
        columnType.setDatatype("int");
        columnType.setColumnTitle(LABKEY_BIRTHYEAR);
        columnType.setRangeURI("http://www.w3.org/2001/XMLSchema#int");

        return columnType;
    }

    // TODO: remove if it is not needed anymore - was part of EventAttributes Table
    private ColumnType getDateColumnType() {
        ColumnType columnType = new ColumnType();
        columnType.setColumnName(LABKEY_DATE);
        columnType.setDescription(LABKEY_DATE);
        columnType.setDatatype("timestamp");
        columnType.setPropertyURI("http://cpas.labkey.com/Study#VisitDate");
        columnType.setFormatString(LABKEY_DATE);
        return columnType;

    }

    private ColumnType getSecondaryIdColumn() {
        return getStringColumn(LABKEY_SECONDARY_ID, LABKEY_SECONDARY_ID, LABKEY_SECONDARY_ID);
    }

    private ColumnType getPatientIdColumn() {
        return getStringColumn(LABKEY_PATIENT_ID, LABKEY_PATIENT_ID, LABKEY_PATIENT_ID);
    }

    private ColumnType getGenderColumn() {
        return getStringColumn(LABKEY_GENDER, LABKEY_GENDER, LABKEY_GENDER);
    }
}
