package de.dktk.dd.rpb.core.domain.lab;

import de.dktk.dd.rpb.core.converter.StudyEventConverter;
import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupDefinition;
import de.dktk.dd.rpb.core.util.labkey.OdmEventMetaDataLookup;
import org.apache.commons.lang.SerializationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Helps to create CrfAttributeTables for the LabKey export.
 */
public class CrfAttributeTable {
    private String oid;
    private String name;
    private String parentFormOid;
    private String versionDescription;
    private String revisionNotes;

    private List<ItemGroupDefinition> itemGroupDefinitionList;
    private List<ItemDefinition> itemDefinitionList;
    private List<String> itemGroupOrderList;
    private List<ItemDefinition> orderedItemReferencesList;
    private List<CrfAttributeTableItem> crfAttributeTableItemList;

    private Map<String, Integer> maxRepeatsPerItemGroup;

    /**
     * Helper to create the different files to export a CrfAttributeTable to LabKey
     *
     * @param formDefinition FormDefinition from the Odm export
     */
    public CrfAttributeTable(FormDefinition formDefinition) {
        this.oid = formDefinition.getOid();
        this.name = formDefinition.getName();
        this.parentFormOid = formDefinition.getFormDetails().getParentFormOid();
        this.versionDescription = formDefinition.getFormDetails().getVersionDescription();
        this.revisionNotes = formDefinition.getFormDetails().getRevisionNotes();
        this.itemGroupOrderList = new ArrayList<>();
        for (ItemGroupDefinition itemGroupDefinition : formDefinition.getItemGroupRefs()) {
            itemGroupOrderList.add(itemGroupDefinition.getItemGroupOid());
        }
        this.crfAttributeTableItemList = new ArrayList<>();


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

    public List<ItemDefinition> getItemDefinitionList() {
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

    public List<CrfAttributeTableItem> getCrfAttributeTableItemList() {
        return crfAttributeTableItemList;
    }

    public void addCrfAttributeTableItem(CrfAttributeTableItem crfAttributeTableItem) {
        this.crfAttributeTableItemList.add(crfAttributeTableItem);
    }

    /**
     * Helper to create tsv file header for LabKey export
     *
     * @return array of header names for creating tsv files in the predefined order
     */
    public String[] getHeaderNames() {
        List<String> headerList = new ArrayList<>();

        headerList.add("SequenceNum");
        headerList.add("ParticipantId");
        headerList.add("StudyEventOID");
        headerList.add("StudyEventRepeatKey");
        headerList.add("FormOID");

        for (ItemDefinition itemDefinition : orderedItemReferencesList) {
            headerList.add(itemDefinition.getName());
        }

        String[] array = new String[headerList.size()];

        return headerList.toArray(array);
    }

    /**
     * Helper to create tsv file items for LabKey export
     *
     * @param odmEventMetaDataLookup OdmEventMetaDataLookup with meta data information about the event
     * @return List<CrfAttributes> in predefined order
     */
    public List<CrfAttributes> getOrderedAttributeList(OdmEventMetaDataLookup odmEventMetaDataLookup) {
        List<CrfAttributes> attributesList = new ArrayList<>();
        for (CrfAttributeTableItem item : this.crfAttributeTableItemList) {
            String studySubjectId = item.getParticipantId();
            String studyEventOid = item.getEventOid();
            Integer studyEventRepeatKey = item.getEventRepeatKey();
            Integer ordinal = odmEventMetaDataLookup.getStudyEventOrdinal(studyEventOid);
            if (item.getEventRepeatKey() == null) {
                studyEventRepeatKey = 0;
            }
            Double sequenceNumber = StudyEventConverter.calculateLabkeySequenceNumber(String.valueOf(studyEventRepeatKey), ordinal);

            String formOid = item.getFormOid();
            List<ItemData> itemDataList = item.getItemDataList(this.orderedItemReferencesList);

            CrfAttributes attributes = new CrfAttributes(
                    studySubjectId,
                    studyEventOid,
                    studyEventRepeatKey,
                    sequenceNumber,
                    formOid,
                    itemDataList);

            attributesList.add(attributes);
        }


        return attributesList;
    }

    public List<ItemDefinition> getOrderedItemReferencesList() {
        this.createOrderedItemsLists();
        return orderedItemReferencesList;
    }

    /**
     * Order of items needs to be defined in order that table definitions in datasets_metadata.xml and tsv files
     * have items on the same position. Additionally, repeating item groups will be represented as {oid}_{repeat_key}
     * columns.
     *
     * @return List<ItemDefinition> ordered List where repeating item groups have an {oid}_{repeatingKey} columns representation.
     */
    private List<ItemDefinition> createOrderedItemsLists() {
        this.orderedItemReferencesList = new ArrayList<>();
        List<String> allTableItemsOidList = new ArrayList<>();
        for (String itemGroupOid : this.itemGroupOrderList) {
            ItemGroupDefinition itemGroupDefinition = getItemGroupDefinition(itemGroupOid);
            List<ItemDefinition> itemRefList = itemGroupDefinition.getItemRefs();
            Collections.sort(itemRefList);
            Integer repeatKey = maxRepeatsPerItemGroup.get(itemGroupOid);
            for (Integer i = 1; i <= repeatKey; i++) {
                String repeatKeyPart = "";
                if (repeatKey > 1) {
                    repeatKeyPart = "_" + i.toString();
                }

                for (ItemDefinition itemReference : itemRefList) {
                    String oid = itemReference.getItemOid();
                    allTableItemsOidList.add(oid + repeatKeyPart);
                    ItemDefinition itemDefinition = this.getItemDefinition(oid);
                    if (repeatKey > 1) {
                        ItemDefinition itemClone = (ItemDefinition) SerializationUtils.clone(itemDefinition);
                        itemClone.setName(itemClone.getName() + repeatKeyPart);
                        itemClone.setOid(itemClone.getOid() + repeatKeyPart);
                        orderedItemReferencesList.add(itemClone);
                    } else {
                        orderedItemReferencesList.add(itemDefinition);
                    }
                }
            }

        }

        return orderedItemReferencesList;
    }

    private ItemGroupDefinition getItemGroupDefinition(String oid) {
        for (ItemGroupDefinition itemGroupDefinition : this.itemGroupDefinitionList) {
            if (itemGroupDefinition.getOid().equalsIgnoreCase(oid)) {
                return itemGroupDefinition;
            }
        }
        return null;
    }

    private ItemDefinition getItemDefinition(String oid) {
        for (ItemDefinition itemDefinition : this.itemDefinitionList) {
            if (itemDefinition.getOid().equalsIgnoreCase(oid)) {
                return itemDefinition;
            }
        }
        return null;
    }
}
