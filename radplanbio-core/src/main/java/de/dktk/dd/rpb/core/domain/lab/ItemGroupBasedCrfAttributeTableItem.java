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

import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.MultiSelectListDefinition;
import de.dktk.dd.rpb.core.domain.edc.MultiSelectListItem;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.util.PartialDateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.LABKEY_IDENTIFIERSEP;
import static java.lang.String.valueOf;

public class ItemGroupBasedCrfAttributeTableItem {
    private static final Logger log = LoggerFactory.getLogger(ItemGroupBasedCrfAttributeTableItem.class);

    private final String participantId;
    private final String pid;
    private final double sequenceNumber;
    private final String eventOid;
    private final Integer eventRepeatKey;
    private final String formOid;
    private final String studySiteIdentifier;
    private final Integer formRepeatKey;
    private final String itemGroupOid;

    private final LabKeyExportConfiguration labKeyExportConfiguration;

    private Integer itemGroupRepeatKey;
    private String formOidFormRepeatKeyItemGroupOidItemGroupOidCombined;

    private final List<ItemData> itemDataList;
    private List<ItemData> orderedItemDataList;
    private List<ItemDefinition> itemDefinitionList;

    public ItemGroupBasedCrfAttributeTableItem(
            String participantId,
            String pid,
            double sequenceNumber,
            String eventOid,
            Integer eventRepeatKey,
            String studySiteIdentifier,
            String formOid,
            Integer formRepeatKey,
            String itemGroupOid,
            LabKeyExportConfiguration labKeyExportConfiguration, Integer itemGroupRepeatKey,
            List<ItemData> itemDataList) {
        this.participantId = participantId;
        this.pid = pid;
        this.sequenceNumber = sequenceNumber;
        this.eventOid = eventOid;
        this.eventRepeatKey = eventRepeatKey;
        this.formOid = formOid;
        this.studySiteIdentifier = studySiteIdentifier;
        this.formRepeatKey = formRepeatKey;
        this.labKeyExportConfiguration = labKeyExportConfiguration;
        this.itemDataList = itemDataList;
        this.itemGroupOid = itemGroupOid;
        this.itemGroupRepeatKey = itemGroupRepeatKey;

        formOidFormRepeatKeyItemGroupOidItemGroupOidCombined = ItemGroupBasedCrfAttributeTableItem.calculateCombinedId(studySiteIdentifier, formOid, formRepeatKey, itemGroupOid, itemGroupRepeatKey);
    }

    public static String calculateCombinedId(String studySiteIdentifier, String formOid, Integer formRepeatKey, String itemGroupOid, Integer itemGroupRepeatKey) {
        return studySiteIdentifier +
                LABKEY_IDENTIFIERSEP +
                formOid +
                LABKEY_IDENTIFIERSEP +
                formRepeatKey +
                LABKEY_IDENTIFIERSEP +
                itemGroupOid +
                LABKEY_IDENTIFIERSEP +
                itemGroupRepeatKey;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getEventOid() {
        return eventOid;
    }

    public Integer getEventRepeatKey() {
        return eventRepeatKey;
    }

    public String getFormOid() {
        return formOid;
    }

    public Integer getFormRepeatKey() {
        return formRepeatKey;
    }

    public String getItemGroupOid() {
        return itemGroupOid;
    }

    public Integer getItemGroupRepeatKey() {
        return itemGroupRepeatKey;
    }

    public void setItemGroupRepeatKey(Integer itemGroupRepeatKey) {
        this.itemGroupRepeatKey = itemGroupRepeatKey;
    }

    public String getFormOidFormRepeatKeyItemGroupOidItemGroupOidCombined() {
        return formOidFormRepeatKeyItemGroupOidItemGroupOidCombined;
    }

    public List<ItemDefinition> getItemDefinitionList() {
        return itemDefinitionList;
    }

    public void setItemDefinitionList(List<ItemDefinition> itemDefinitionList) {
        this.itemDefinitionList = itemDefinitionList;
    }

    public List<ItemData> getOrderedItemDataList() {
        return orderedItemDataList;
    }

    public void setOrderedItemDataList(List<ItemData> orderedItemDataList) {
        this.orderedItemDataList = orderedItemDataList;
    }

    /**
     * Generates a list of ItemData ordered by the references of the orderedItemReferencesList
     *
     * @param orderedItemReferencesList ordered list of definitions
     */
    public void createOrderedItemDataList(List<ItemDefinition> orderedItemReferencesList) {
        this.orderedItemDataList = new ArrayList<>();

        for (ItemDefinition itemDefinition : orderedItemReferencesList) {
            ItemData itemData = this.getItem(itemDefinition.getOid());
            // ensure that an itemDefinition is set
            if (itemData.getItemDefinition() == null) {
                itemData.setItemDefinition(itemDefinition);
            }
            orderedItemDataList.add(itemData);
        }

    }

    private ItemData getItem(String oid) {

        for (ItemData item : itemDataList) {
            String itemOid = item.getItemOid();
            if (itemOid.equalsIgnoreCase(oid)) {
                item.setItemOid(itemOid);
                return item;
            }
        }

        // there is no corresponding item on the form data - need to add an empty item for the tsv export
        ItemData emptyItem = new ItemData();
        emptyItem.setItemOid(oid);
        return emptyItem;
    }

    public List<Object> getValues() {
        List<Object> valueList = new ArrayList<>();

        valueList.add(valueOf(this.sequenceNumber));
        valueList.add(this.participantId);
        valueList.add(this.pid);
        valueList.add(this.eventOid);
        valueList.add(this.eventRepeatKey);
        valueList.add(this.studySiteIdentifier);
        valueList.add(this.formOid);
        valueList.add(this.formRepeatKey);
        valueList.add(this.itemGroupOid);
        valueList.add(this.itemGroupRepeatKey);
        valueList.add(this.formOidFormRepeatKeyItemGroupOidItemGroupOidCombined);

        for (ItemData item : orderedItemDataList) {
            valueList.add(item.getValue());

            // decoded value
            if (this.labKeyExportConfiguration.isAddDecodedValueColumns()) {
                if (item.getItemDefinition() != null) {
                    String itemValue = item.getValue();
                    ItemDefinition itemDefinition = item.getItemDefinition();

                    if (itemDefinition.getCodeListDef() != null) {
                        if (itemValue == null) {
                            valueList.add(null);
                        } else {
                            valueList.add(itemDefinition.getCodeListDef().getDecodedText(item.getValue()));

                        }
                    }
                }
            }

            // multiselect value
            if (this.labKeyExportConfiguration.isAddMultiSelectValueColumns()) {
                if (item.getItemDefinition() != null) {
                    String itemValue = item.getValue();
                    if (itemValue == null) {
                        itemValue = "";
                    }

                    ItemDefinition itemDefinition = item.getItemDefinition();
                    if (itemDefinition.getMultiSelectListDef() != null) {
                        String[] splittedValues = itemValue.split(",");

                        MultiSelectListDefinition definition = itemDefinition.getMultiSelectListDef();
                        for (MultiSelectListItem multiSelectListItem : definition.getMultiSelectListItems()) {
                            if (Arrays.asList(splittedValues).contains(multiSelectListItem.getCodedOptionValue())) {
                                valueList.add(true);
                            } else {
                                valueList.add(false);
                            }

                        }

                    }
                }
            }

            if (this.labKeyExportConfiguration.isAddPartialDateColumns()) {
                if (item.getItemDefinition() != null) {
                    ItemDefinition definition = item.getItemDefinition();
                    if (definition.getDataType().equals("partialDate")) {
                        String itemValue = item.getValue();
                        if (itemValue == null) {
                            valueList.add(null);
                            valueList.add(null);
                        } else {
                            PartialDateWrapper partialDateWrapper = null;
                            try {
                                partialDateWrapper = new PartialDateWrapper(itemValue);
                                valueList.add(partialDateWrapper.getMinDate());
                                valueList.add(partialDateWrapper.getMaxDate());
                            } catch (MissingPropertyException e) {
                                String errorMessage = "There was an issue handling the partial date. Adding null values";
                                log.debug(errorMessage,e);
                                valueList.add(null);
                                valueList.add(null);
                            }


                        }

                    }
                }
            }

        }

        return valueList;
    }
}
