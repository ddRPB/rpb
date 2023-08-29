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

import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupDefinition;
import de.dktk.dd.rpb.core.domain.edc.MultiSelectListDefinition;
import de.dktk.dd.rpb.core.domain.edc.MultiSelectListItem;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.util.labkey.ItemDefinitionDefaultComparator;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.*;

/**
 * Helps to create item group based CrfAttributeTables for the LabKey export.
 */
public class ItemGroupBasedCrfAttributeTable {
    private final String itemGroupOid;
    private final String subjectColumnName;
    private final String name;
    private final String comment;

    private final ItemGroupDefinition itemGroupDefinition;

    private final LabKeyExportConfiguration labKeyExportConfiguration;

    private List<ItemDefinition> orderedItemReferencesList;
    private List<ItemGroupBasedCrfAttributeTableItem> itemGroupBasedCrfAttributeTableItemList = new ArrayList<>();

    public ItemGroupBasedCrfAttributeTable(
            ItemGroupDefinition itemGroupDefinition,
            String subjectColumnName,
            LabKeyExportConfiguration labKeyExportConfiguration) {
        this.itemGroupDefinition = itemGroupDefinition;
        this.itemGroupOid = itemGroupDefinition.getOid();
        this.name = itemGroupDefinition.getName();
        this.comment = itemGroupDefinition.getComment();
        this.subjectColumnName = subjectColumnName;
        this.labKeyExportConfiguration = labKeyExportConfiguration;
    }

    public ItemGroupDefinition getItemGroupDefinition() {
        return itemGroupDefinition;
    }

    public String getItemGroupOid() {
        return itemGroupOid;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public List<ItemDefinition> getOrderedItemReferencesList() {
        return orderedItemReferencesList;
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
        headerList.add(LABKEY_FORM_REPEAT_KEY);
        headerList.add(LABKEY_ITEM_GROUP_OID);
        headerList.add(LABKEY_ITEM_GROUP_REPEAT_KEY);
        headerList.add(LABKEY_FORM_ITEM_GROUP_COMBINED_ID);

        for (ItemDefinition itemDefinition : orderedItemReferencesList) {
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
     * Order of items needs to be defined in order that table definitions in datasets_metadata.xml and tsv files
     * have items on the same position.
     *
     * @return List<ItemDefinition> ordered List of ItemDefinitions.
     */
    public List<ItemDefinition> createOrderedItemsLists() {
        this.orderedItemReferencesList = new ArrayList<>();

        // copy the list, because we want to order it and the original list could be referenced from somewhere
        this.orderedItemReferencesList.addAll(itemGroupDefinition.getItemDefs());
        // order by order number with the default comparator
        this.orderedItemReferencesList.sort(new ItemDefinitionDefaultComparator());

        return orderedItemReferencesList;
    }

    /**
     * Helper to create tsv file items for LabKey export
     *
     * @return List<ItemGroupBasedCrfAttributeTableItem> in predefined order
     */
    public List<ItemGroupBasedCrfAttributeTableItem> getOrderedAttributeList() throws MissingPropertyException {
        List<ItemGroupBasedCrfAttributeTableItem> attributesList = new ArrayList<>();
        for (ItemGroupBasedCrfAttributeTableItem item : this.itemGroupBasedCrfAttributeTableItemList) {
            item.createOrderedItemDataList(this.orderedItemReferencesList);
            attributesList.add(item);
        }

        return attributesList;
    }

    public void addCrfAttributeTableItem(ItemGroupBasedCrfAttributeTableItem itemGroupBasedCrfAttributeTableItem) {
        this.itemGroupBasedCrfAttributeTableItemList.add(itemGroupBasedCrfAttributeTableItem);
    }
}
