package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.domain.edc.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This object holds the meta data information of the study events
 */
public class OdmEventMetaDataLookup {

    private Map<String, Integer> studyEventOrderNumberMap = new HashMap<>();
    private Map<String, Boolean> studyEventIsRepeatingMap = new HashMap<>();
    private Map<String, String> studyEventNameMap = new HashMap<>();
    private Map<String, EventDefinitionDetails> studyEventDetailsMap = new HashMap<>();
    private List<EventDefinition> eventDefinitionList;

    private Map<String, FormDetails> formDetailsMap = new HashMap<>();
    private Map<String, String> formNameMap = new HashMap<>();
    private List<FormDefinition> formDefinitionList;

    private Map<String, Boolean> itemGroupRepeatingLookup = new HashMap<>();
    private Map<String, Map<String, Integer>> itemGroupPositionLookup = new HashMap<>();

    private Map<String, Map<String, Integer>> itemOrderNumberMap = new HashMap<>();

    /**
     * Lookups that provide meta data information from the ODM
     *
     * @param metaDataVersion MetaDataVersion part of the ODM export
     */
    public OdmEventMetaDataLookup(MetaDataVersion metaDataVersion) {
        if (metaDataVersion.getProtocolDefinitions() != null) {
            this.createProtocolLookups(metaDataVersion.getProtocolDefinitions().getEventReferences());
        }
        this.createEventDefinitionLookups(metaDataVersion.getStudyEventDefinitions());
        this.createFormDefinitionLookups(metaDataVersion.getFormDefinitions());
        this.createItemGroupLookups(metaDataVersion.getItemGroupDefinitions());
    }

    private void createProtocolLookups(List<EventReference> eventReferenceList) {
        for (EventReference eventReference : eventReferenceList) {
            studyEventOrderNumberMap.put(eventReference.getOid(), eventReference.getOrdinal());
        }
    }

    private void createEventDefinitionLookups(List<EventDefinition> eventDefinitionList) {
        this.eventDefinitionList = eventDefinitionList;

        for (EventDefinition eventDefinition : eventDefinitionList) {
            studyEventIsRepeatingMap.put(eventDefinition.getOid(), eventDefinition.getIsRepeating());
            studyEventNameMap.put(eventDefinition.getOid(), eventDefinition.getName());
            studyEventDetailsMap.put(eventDefinition.getOid(), eventDefinition.getEventDefinitionDetails());
        }
    }

    private void createFormDefinitionLookups(List<FormDefinition> formDefinitionList) {
        this.formDefinitionList = formDefinitionList;

        for (FormDefinition formDefinition : formDefinitionList) {
            formDetailsMap.put(formDefinition.getOid(), formDefinition.getFormDetails());
            formNameMap.put(formDefinition.getOid(), formDefinition.getName());

            List<ItemGroupDefinition> itemGroupDefinitionList = formDefinition.getItemGroupRefs();

            Map<String, Integer> itemGroupPosMap = new HashMap<>();
            this.itemGroupPositionLookup.put(formDefinition.getOid(), itemGroupPosMap);

            for (int i = 0; i < itemGroupDefinitionList.size(); i++) {
                itemGroupPosMap.put(itemGroupDefinitionList.get(i).getItemGroupOid(), i);
            }
        }
    }

    private void createItemGroupLookups(List<ItemGroupDefinition> itemGroupDefinitionList) {
        for (ItemGroupDefinition itemGroupDefinition : itemGroupDefinitionList) {
            this.itemGroupRepeatingLookup.put(itemGroupDefinition.getOid(), itemGroupDefinition.getIsRepeating());

            Map<String, Integer> itemOrderMap = new HashMap<>();
            itemOrderNumberMap.put(itemGroupDefinition.getOid(), itemOrderMap);
            for (ItemDefinition itemDefinition : itemGroupDefinition.getItemRefs()) {
                itemOrderMap.put(itemDefinition.getItemOid(), itemDefinition.getOrderNumber());
            }
        }
    }

    public List<EventDefinition> getEventDefinitionList() {
        return eventDefinitionList;
    }

    public Integer getStudyEventOrdinal(String eventOid) {
        return this.studyEventOrderNumberMap.get(eventOid);
    }

    public Boolean getStudyEventIsRepeating(String eventOid) {
        return this.studyEventIsRepeatingMap.get(eventOid);
    }

    public String getStudyEventName(String eventOid) {
        return this.studyEventNameMap.get(eventOid);
    }

    public EventDefinitionDetails getEventDefinitionDetails(String eventOid) {
        return this.studyEventDetailsMap.get(eventOid);
    }

    public List<FormDefinition> getFormDefinitionList() {
        return formDefinitionList;
    }

    public Integer getItemGroupPosition(String formOid, String itemGroupOid) {
        return itemGroupPositionLookup.get(formOid).get(itemGroupOid);
    }

    public Integer getItemOrderNumber(String itemGroupOid, String itemOid) {
        return this.itemOrderNumberMap.get(itemGroupOid).get(itemOid);
    }

    public Boolean itemGroupIsRepeating(String itemGroupOid) {
        return itemGroupRepeatingLookup.get(itemGroupOid);
    }

    public FormDetails getFormDetails(String formOid) {
        return formDetailsMap.get(formOid);
    }

    public String getFormName(String formOid) {
        return formNameMap.get(formOid);
    }
}
