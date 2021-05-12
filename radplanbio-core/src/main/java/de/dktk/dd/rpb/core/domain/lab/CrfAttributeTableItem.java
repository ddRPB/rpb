package de.dktk.dd.rpb.core.domain.lab;

import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupData;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item in the Labkey attribute table
 */
public class CrfAttributeTableItem {

    private final String participantId;
    private final String eventOid;
    private final Integer eventRepeatKey;
    private final String formOid;
    private final FormData formData;

    /***
     * Item in an atttribute table for the import in Labkey
     * @param participantId StudySubjectId to track the patient
     * @param eventOid StudyEventOid to track the the event
     * @param eventRepeatKey RepeatKey of the StudyEvent
     * @param formOid Oid of the form (repeating item groups have the repeat key of the item group as a suffix, for instance: dummy_oid_1, dummy_oid_2)
     * @param formData itemGroup and item data
     */
    public CrfAttributeTableItem(String participantId, String eventOid, Integer eventRepeatKey, String formOid, FormData formData) {
        this.participantId = participantId;
        this.eventOid = eventOid;
        this.eventRepeatKey = eventRepeatKey;
        this.formOid = formOid;
        this.formData = formData;
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

    /**
     * Generates a list of ItemData ordered by the references of the orderedItemReferencesList
     *
     * @param orderedItemReferencesList ordered list of definitions
     * @return ordered list of items
     */
    public List<ItemData> getItemDataList(List<ItemDefinition> orderedItemReferencesList) {
        List<ItemData> itemDataList = new ArrayList<>();
        for (ItemDefinition itemDefinition : orderedItemReferencesList) {
            itemDataList.add(this.getItem(itemDefinition.getOid()));
        }

        return itemDataList;
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
                String itemOid = item.getItemOid() + repeatKey;
                if (itemOid.equalsIgnoreCase(oid)) {
                    return item;
                }
            }
        }
        // there is no corresponding item on the form data - need to add an empty item for the tsv export
        return new ItemData();
    }
}
