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

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.util.PartialDateWrapper;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Represents an item in the LabKey attribute table
 */
public class FormBasedCrfAttributeTableItem {
    private static final Logger log = LoggerFactory.getLogger(FormBasedCrfAttributeTableItem.class);

    private final String participantId;
    private final String pid;
    private final String eventOid;
    private final Integer eventRepeatKey;
    private final String siteIdentifier;
    private final String formOid;
    private final FormData formData;
    private final Double sequenceNumber;

    private final LabKeyExportConfiguration labKeyExportConfiguration;

    private List<ItemData> orderedItemDataList;

    /***
     * Item in an  form based attribute table for the import in LabKey
     * @param participantId StudySubjectId to track the patient
     * @param eventOid StudyEventOid to track the the event
     * @param eventRepeatKey RepeatKey of the StudyEvent
     * @param formOid Oid of the form (repeating item groups have the repeat key of the item group as a suffix, for instance: dummy_oid_1, dummy_oid_2)
     * @param formData itemGroup and item data
     * @param labKeyExportConfiguration
     */
    public FormBasedCrfAttributeTableItem(
            String participantId,
            String pid,
            String eventOid,
            Double sequenceNumber,
            Integer eventRepeatKey,
            String siteIdentifier,
            String formOid,
            FormData formData,
            LabKeyExportConfiguration labKeyExportConfiguration
    ) {

        this.participantId = participantId;
        this.pid = pid;
        this.eventOid = eventOid;
        this.eventRepeatKey = eventRepeatKey;
        this.siteIdentifier = siteIdentifier;
        this.formOid = formOid;
        this.formData = formData;
        this.sequenceNumber = sequenceNumber;
        this.labKeyExportConfiguration = labKeyExportConfiguration;
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

    public List<ItemData> getOrderedItemDataList() {
        return orderedItemDataList;
    }

    public void setOrderedItemDataList(List<ItemData> orderedItemDataList) {
        this.orderedItemDataList = orderedItemDataList;
    }

    /**
     * Generates a list of ItemData ordered by the references of the orderedItemReferencesList that
     * reflects the order in the table.
     *
     * @param orderedItemReferencesList ordered list of definitions
     */
    public void createOrderedItemDataList(List<ItemDefinition> orderedItemReferencesList) {
        this.orderedItemDataList = new ArrayList<>();
        for (ItemDefinition itemDefinition : orderedItemReferencesList) {
            ItemData item = this.getItem(itemDefinition.getOid());
            // ensure that an itemDefinition is set
            if (item.getItemDefinition() == null) {
                item.setItemDefinition(itemDefinition);
            }
            orderedItemDataList.add(item);
        }
    }

    private ItemData getItem(String oid) {
        for (ItemGroupData itemGroupData : formData.getItemGroupDataList()) {
            String repeatKey = itemGroupData.getItemGroupRepeatKey();

            if (repeatKey == null) {
                // do not add a suffix if the repeat key is null
                repeatKey = "";
            } else {
                repeatKey = "_" + repeatKey;
            }

            for (ItemData item : itemGroupData.getItemDataList()) {
                // oid has suffix if the item group is repeating
                String itemOidWithRepeatKeySuffix = item.getItemOid() + repeatKey;
                String itemOid = item.getItemOid();

                if (itemOidWithRepeatKeySuffix.equalsIgnoreCase(oid)) {
                    ItemData itemClone = (ItemData) SerializationUtils.clone(item);
                    itemClone.setItemOid(itemOidWithRepeatKeySuffix);
                    return itemClone;
                } else if (itemOid.equalsIgnoreCase(oid)) {
                    // Not repeating item groups can have a repeat key - the current logic would try to add a suffix
                    // This quick fix checks for an item without suffix - needs to be refactored later
                    ItemData itemClone = (ItemData) SerializationUtils.clone(item);
                    itemClone.setItemOid(itemOid);
                    return itemClone;
                }
            }
        }
        // there is no corresponding item on the form data - need to add an empty item for the tsv export
        log.debug("There is no corresponding data item for " + this.getContextAsString() +
                "EventOid: \"" + oid + "\". Add empty item."
        );

        ItemData emptyItem = new ItemData();
        emptyItem.setItemOid(oid);
        return emptyItem;
    }

    /**
     * Creates an ordered list of items
     *
     * @return List<Object> table item values as list
     */
    public List<Object> getValues() {
        List<Object> valueList = new ArrayList<>();

        valueList.add(valueOf(this.sequenceNumber));
        valueList.add(this.participantId);
        valueList.add(this.pid);
        valueList.add(this.eventOid);
        valueList.add(this.eventRepeatKey);
        valueList.add(this.siteIdentifier);
        valueList.add(this.formOid);

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
                                log.debug(errorMessage, e);
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

    private String getContextAsString() {
        String context = "ParticipantId: \"" + this.participantId + "\", " +
                "EventOid: \"" + this.eventOid + "\", " +
                "EventRepeatKey: \"" + this.eventRepeatKey + "\", " +
                "StudySiteIdentifier: \"" + this.siteIdentifier + "\", " +
                "FormOid: \"" + this.formOid + "\", ";
        return context;
    }
}
