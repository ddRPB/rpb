/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2021 RPB Team
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

package de.dktk.dd.rpb.core.domain.lab;

import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupDefinition;
import de.dktk.dd.rpb.core.domain.edc.MultiSelectListDefinition;
import de.dktk.dd.rpb.core.domain.edc.MultiSelectListItem;
import de.dktk.dd.rpb.core.util.labkey.ItemDefinitionClinicaComparator;
import de.dktk.dd.rpb.core.util.labkey.ItemDefinitionDefaultComparator;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.dktk.dd.rpb.core.util.Constants.LABKEY_DECODED_VALUE_SUFFIX;
import static de.dktk.dd.rpb.core.util.Constants.LABKEY_FORM_OID;
import static de.dktk.dd.rpb.core.util.Constants.LABKEY_MULTISELECT_VALUE_SUFFIX;
import static de.dktk.dd.rpb.core.util.Constants.LABKEY_PARTIAL_DATE_MAX_SUFFIX;
import static de.dktk.dd.rpb.core.util.Constants.LABKEY_PARTIAL_DATE_MIN_SUFFIX;
import static de.dktk.dd.rpb.core.util.Constants.LABKEY_PATIENT_ID;
import static de.dktk.dd.rpb.core.util.Constants.LABKEY_SEQUENCE_NUMBER;
import static de.dktk.dd.rpb.core.util.Constants.LABKEY_STUDY_EVENT_OID;
import static de.dktk.dd.rpb.core.util.Constants.LABKEY_STUDY_EVENT_REPEAT_KEY;
import static de.dktk.dd.rpb.core.util.Constants.LABKEY_STUDY_SITE_IDENTIFIER;
import static de.dktk.dd.rpb.core.util.Constants.ODM_PARTIAL_DATE;

/**
 * Helper to create the different files to export a FormBasedCrfAttributeTable to LabKey
 */
public class FormBasedCrfAttributeTable {
    private static final Logger log = LoggerFactory.getLogger(FormBasedCrfAttributeTable.class);

    private final String oid;
    private final String name;
    private final String parentFormOid;
    private final String versionDescription;
    private final String revisionNotes;
    private final String subjectColumnName;

    private final LabKeyExportConfiguration labKeyExportConfiguration;
    private final ItemDefinitionClinicaComparator formComparator;

    private List<ItemGroupDefinition> itemGroupDefinitionList;
    private List<ItemDefinition> itemDefinitionList;
    private List<ItemDefinition> orderedItemDefinitionsListWithRepeatKeyClones;
    private final List<FormBasedCrfAttributeTableItem> formBasedCrfAttributeTableItemList;

    private Map<String, Integer> maxRepeatsPerItemGroup;

    /**
     * Helper to create the different files to export a FormBasedCrfAttributeTable to LabKey
     *
     * @param formDefinition    FormDefinition from the Odm export
     * @param subjectColumnName Name of the study subject column in the LabKey table
     */
    public FormBasedCrfAttributeTable(
            FormDefinition formDefinition,
            String subjectColumnName,
            LabKeyExportConfiguration labKeyExportConfiguration
    ) {

        this.oid = formDefinition.getOid();
        this.name = formDefinition.getName();
        this.parentFormOid = formDefinition.getFormDetails().getParentFormOid();
        this.versionDescription = formDefinition.getFormDetails().getVersionDescription();
        this.revisionNotes = formDefinition.getFormDetails().getRevisionNotes();

        this.formBasedCrfAttributeTableItemList = new ArrayList<>();
        this.subjectColumnName = subjectColumnName;

        this.labKeyExportConfiguration = labKeyExportConfiguration;
        this.formComparator = labKeyExportConfiguration.getFormItemComparator(this.oid);
    }

    public String getOid() {
        return oid;
    }

    public String getName() {
        return name;
    }

    public String getParentFormOid() {
        return parentFormOid;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public String getRevisionNotes() {
        return revisionNotes;
    }

    public List<ItemGroupDefinition> getItemGroupDefinitionList() {
        return itemGroupDefinitionList;
    }

    public void setItemGroupDefinitionList(List<ItemGroupDefinition> itemGroupDefinitionList) {
        this.itemGroupDefinitionList = itemGroupDefinitionList;
    }

    public List<ItemDefinition> getOrderedItemDefinitionList() {
        return itemDefinitionList;
    }

    public void setItemDefinitionList(List<ItemDefinition> itemDefinitionList) {
        this.itemDefinitionList = itemDefinitionList;
    }

    public Map<String, Integer> getMaxRepeatsPerItemGroup() {
        return maxRepeatsPerItemGroup;
    }

    public void setMaxRepeatsPerItemGroup(Map<String, Integer> maxRepeatsPerItemGroup) {
        this.maxRepeatsPerItemGroup = maxRepeatsPerItemGroup;
    }

    public List<FormBasedCrfAttributeTableItem> getCrfAttributeTableItemList() {
        return formBasedCrfAttributeTableItemList;
    }

    public void addCrfAttributeTableItem(FormBasedCrfAttributeTableItem formBasedCrfAttributeTableItem) {
        this.formBasedCrfAttributeTableItemList.add(formBasedCrfAttributeTableItem);
    }

    /**
     * Helper to create tsv file header for LabKey export
     *
     * @return array of header names for creating tsv files in the predefined order
     */
    public String[] getHeaderNames() {
        List<String> headerList = new ArrayList<>();

        headerList.add(LABKEY_SEQUENCE_NUMBER);
        headerList.add(this.subjectColumnName);
        headerList.add(LABKEY_PATIENT_ID);
        headerList.add(LABKEY_STUDY_EVENT_OID);
        headerList.add(LABKEY_STUDY_EVENT_REPEAT_KEY);
        headerList.add(LABKEY_STUDY_SITE_IDENTIFIER);
        headerList.add(LABKEY_FORM_OID);

        for (ItemDefinition itemDefinition : orderedItemDefinitionsListWithRepeatKeyClones) {
            headerList.add(itemDefinition.getOid());

            // decoded value
            if (this.labKeyExportConfiguration.isAddDecodedValueColumns() && itemDefinition.getCodeListDef() != null) {
                headerList.add(itemDefinition.getOid() + LABKEY_DECODED_VALUE_SUFFIX);
            }

            // multiselect value
            if (this.labKeyExportConfiguration.isAddMultiSelectValueColumns() && itemDefinition.getMultiSelectListDef() != null) {
                MultiSelectListDefinition definition = itemDefinition.getMultiSelectListDef();

                for (MultiSelectListItem multiSelectListItem : definition.getMultiSelectListItems()) {
                    String codedValue = multiSelectListItem.getCodedOptionValue();
                    headerList.add(itemDefinition.getOid() + LABKEY_MULTISELECT_VALUE_SUFFIX + codedValue);
                }
            }

            // partial date
            if (this.labKeyExportConfiguration.isAddPartialDateColumns() && itemDefinition.getDataType().equals(ODM_PARTIAL_DATE)) {
                headerList.add(itemDefinition.getOid() + LABKEY_PARTIAL_DATE_MIN_SUFFIX);
                headerList.add(itemDefinition.getOid() + LABKEY_PARTIAL_DATE_MAX_SUFFIX);
            }

        }

        String[] array = new String[headerList.size()];

        return headerList.toArray(array);
    }

    /**
     * The ICsvListWriter needs an array of CellProcessors to write the correct tsv file per table.
     *
     * @return CellProcessor[] Array of CellProcessors
     */
    public CellProcessor[] getCellProcessors() {
        List<CellProcessor> cellProcessorList = new ArrayList<>();

        for (int i = 0; i < this.getHeaderNames().length; i++) {
            cellProcessorList.add(new Optional());
        }

        CellProcessor[] array = new CellProcessor[cellProcessorList.size()];

        return cellProcessorList.toArray(array);
    }

    /**
     * Helper to create tsv file items for LabKey export
     *
     * @return List<FormBasedCrfAttributes> in predefined order
     */
    public List<FormBasedCrfAttributeTableItem> getOrderedAttributeList() {
        List<FormBasedCrfAttributeTableItem> attributesList = new ArrayList<>();
        for (FormBasedCrfAttributeTableItem item : this.formBasedCrfAttributeTableItemList) {
            item.createOrderedItemDataList(this.orderedItemDefinitionsListWithRepeatKeyClones);
            attributesList.add(item);
        }

        return attributesList;
    }

    public List<ItemDefinition> getOrderedItemDefinitionsListWithRepeatKeyClones() {
        return orderedItemDefinitionsListWithRepeatKeyClones;
    }

    /**
     * Order of items needs to be defined in order that table definitions in datasets_metadata.xml and tsv files
     * have items on the same position. Additionally, repeating item groups will be represented as {oid}_{repeat_key}
     * columns.
     *
     * @return List<ItemDefinition> ordered List where repeating item groups have an {oid}_{repeatingKey} columns representation.
     */
    public List<ItemDefinition> createOrderedItemReferencesLists() {
        this.orderedItemDefinitionsListWithRepeatKeyClones = new ArrayList<>();
        for (ItemGroupDefinition itemGroupDefinition : this.itemGroupDefinitionList) {
            boolean isRepeating = itemGroupDefinition.getIsRepeating();
            Integer repeatKey = maxRepeatsPerItemGroup.get(itemGroupDefinition.getOid());

            List<ItemDefinition> orderedItemDefList = getOrderedItemDefinitionList(itemGroupDefinition);


            for (int i = 1; i <= repeatKey; i++) {
                String repeatKeyPart = "";

                if (isRepeating) {
                    repeatKeyPart = "_" + i;
                }

                for (ItemDefinition itemReference : orderedItemDefList) {
                    // Cloning is necessary to avoid side effects, because the function will change some values of the
                    // original definitions to create {oid}_{repeatingKey} columns.
                    ItemDefinition itemClone = (ItemDefinition) SerializationUtils.clone(itemReference);

                    if (isRepeating) {
                        // Repeats are reflected by adding the RepeatKey to the name and oid.
                        // Example: oid = "dummyOID", repeat= 2 ->  "dummyOID_2"
                        itemClone.setName(itemClone.getName() + repeatKeyPart);
                        itemClone.setOid(itemClone.getOid() + repeatKeyPart);
                    }

                    orderedItemDefinitionsListWithRepeatKeyClones.add(itemClone);
                }

            }
        }


        return orderedItemDefinitionsListWithRepeatKeyClones;
    }

    private List<ItemDefinition> getOrderedItemDefinitionList(ItemGroupDefinition itemGroupDefinition) {
        List<ItemDefinition> itemRefList = new ArrayList<>(itemGroupDefinition.getItemDefs());

        if (this.formComparator != null) {
            try {
                // sort list to adapt column order in table
                itemRefList.sort(this.formComparator);
            } catch (Exception e) {
                log.warn("Sorting ItemReferences with comparator: \"" + this.formComparator + "\" failed.", e);
            }
        } else {
            itemRefList.sort(new ItemDefinitionDefaultComparator());
        }
        return itemRefList;
    }
}
