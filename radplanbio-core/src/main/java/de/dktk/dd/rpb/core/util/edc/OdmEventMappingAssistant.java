package de.dktk.dd.rpb.core.util.edc;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappingRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OdmEventMappingAssistant {
    private final Odm sourceOdm;
    private final Odm targetOdm;
    private final List<MappingRecord> existingMappingRecordsList;

    private List<EventDefinition> sourceEventDefinitionList;
    private List<EventDefinition> targetEventDefinitionList;

    private List<EventDefinition> mappingTargetCandidates = new ArrayList<>();
    private final List<EventDefinition> mappedTargets = new ArrayList<>();

    private final HashMap<String, EventDefinition> eventMappingLookup = new HashMap<>();
    private final HashMap<EventDefinition, OdmFormMappingAssistant> formMappingAssistantHashMap = new HashMap<>();

    public OdmEventMappingAssistant(Odm sourceOdm, Odm targetOdm) {
        this.sourceOdm = sourceOdm;
        this.targetOdm = targetOdm;
        this.existingMappingRecordsList = new ArrayList<>();

        this.initialize();
    }

    public OdmEventMappingAssistant(Odm sourceOdm, Odm targetOdm, List<MappingRecord> existingMappingRecordsList) {
        this.sourceOdm = sourceOdm;
        this.targetOdm = targetOdm;
        this.existingMappingRecordsList = existingMappingRecordsList;

        this.initialize();
    }

    private void initialize() {
        this.sourceEventDefinitionList = this.sourceOdm.getStudies().get(0).getMetaDataVersion().getStudyEventDefinitions();
        this.targetEventDefinitionList = this.targetOdm.getStudies().get(0).getMetaDataVersion().getStudyEventDefinitions();
        this.eventMappingLookup.clear();
        this.formMappingAssistantHashMap.clear();
        this.mappingTargetCandidates.addAll(targetEventDefinitionList);
        this.autoMapItems();
    }

    public EventDefinition getMappedEvent(String sourceOid) {
        if (this.eventMappingLookup.containsKey(sourceOid)) {
            return this.eventMappingLookup.get(sourceOid);
        }
        return null;
    }

    public FormDefinition getMappedForm(EventDefinition eventDefinition, String formOid) {
        if (formMappingAssistantHashMap.get(eventDefinition) != null) {
            OdmFormMappingAssistant formMappingAssistant = formMappingAssistantHashMap.get(eventDefinition);
            return formMappingAssistant.getMappedFormDefinition(formOid);
        }
        return null;
    }

    public void setMapping(EventDefinition sourceEvent, EventDefinition targetEvent) {
        if (eventMappingLookup.containsKey(sourceEvent.getOid())) {
            clearMapping(sourceEvent.getOid());
        }
        this.eventMappingLookup.put(sourceEvent.getOid(), targetEvent);
        this.mappedTargets.add(targetEvent);
        this.mappingTargetCandidates.remove(targetEvent);
        this.formMappingAssistantHashMap.put(sourceEvent, new OdmFormMappingAssistant(sourceEvent, targetEvent));
    }

    public void clearMapping(String sourceOid) {
        this.mappedTargets.remove(eventMappingLookup.get(sourceOid));
        this.mappingTargetCandidates.add(eventMappingLookup.get(sourceOid));
        this.eventMappingLookup.remove(sourceOid);
        this.formMappingAssistantHashMap.remove(sourceOid);
    }

    public List<EventDefinition> getSourceEventDefinitionList() {
        return sourceEventDefinitionList;
    }

    public List<EventDefinition> getMappingTargetCandidates() {
        return mappingTargetCandidates;
    }

    public void setMappingTargetCandidates(List<EventDefinition> mappingTargetCandidates) {
        this.mappingTargetCandidates = mappingTargetCandidates;
    }

    public void removeSelectedEvents(List<EventDefinition> eventDefinitionList) {
        //TODO
        for (EventDefinition eventDefinition : eventDefinitionList) {
            this.sourceEventDefinitionList.remove(eventDefinition);
            if (this.eventMappingLookup.containsKey(eventDefinition.getOid())) {
                this.eventMappingLookup.remove(eventDefinition.getOid());
            }
        }
    }

    public OdmFormMappingAssistant getFormMappingAssistant(EventDefinition eventDefinition) {
        return this.formMappingAssistantHashMap.get(eventDefinition);
    }

    public void setFormMapping(EventDefinition eventDefinition, FormDefinition sourceForm, FormDefinition targetForm) {
        if (formMappingAssistantHashMap.containsKey(eventDefinition)) {
            OdmFormMappingAssistant formMappingAssistant = formMappingAssistantHashMap.get(eventDefinition);
            formMappingAssistant.setMapping(sourceForm, targetForm);
        }
    }

    public void clearFormMapping(EventDefinition eventDefinition, FormDefinition sourceForm) {
        if (formMappingAssistantHashMap.containsKey(eventDefinition)) {
            OdmFormMappingAssistant formMappingAssistant = formMappingAssistantHashMap.get(eventDefinition);
            formMappingAssistant.clearMapping(sourceForm);
        }
    }

    public OdmItemGroupMappingAssistant getItemGroupMappingAssistant(EventDefinition eventDefinition, FormDefinition formDefinition) {
        OdmFormMappingAssistant formMappingAssistant = this.getFormMappingAssistant(eventDefinition);
        return formMappingAssistant.getItemGroupMappingAssistant(formDefinition);
    }

    public void setItemGroupMapping(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition sourceItemGroupDefinition, ItemGroupDefinition targetItemGroupDefinition) {
        OdmItemGroupMappingAssistant itemGroupMappingAssistant = this.getItemGroupMappingAssistant(eventDefinition, formDefinition);
        itemGroupMappingAssistant.setMapping(sourceItemGroupDefinition, targetItemGroupDefinition);
    }

    public void clearItemGroupMapping(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition sourceItemGroupDefinition) {
        OdmItemGroupMappingAssistant itemGroupMappingAssistant = this.getItemGroupMappingAssistant(eventDefinition, formDefinition);
        itemGroupMappingAssistant.clearMapping(sourceItemGroupDefinition);
    }

    public ItemGroupDefinition getMappedItemGroupDefinition(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition sourceItemGroupDefinition) {
        OdmItemGroupMappingAssistant itemGroupMappingAssistant = this.getItemGroupMappingAssistant(eventDefinition, formDefinition);
        if (itemGroupMappingAssistant != null) {
            return itemGroupMappingAssistant.getMappedItemGroupDefinition(sourceItemGroupDefinition.getOid());
        } else {
            return null;
        }
    }

    public OdmItemMappingAssistant getItemMappingAssistant(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition sourceItemGroupDefinition) {
        OdmItemGroupMappingAssistant itemGroupMappingAssistant = getItemGroupMappingAssistant(eventDefinition, formDefinition);
        if (itemGroupMappingAssistant != null) {
            return itemGroupMappingAssistant.getItemMappingAssistant(sourceItemGroupDefinition);
        }
        return null;
    }

    public ItemDefinition getMappedItemDefinition(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition itemGroupDefinition, ItemDefinition itemDefinition) {
        OdmItemMappingAssistant itemMappingAssistant = this.getItemMappingAssistant(eventDefinition, formDefinition, itemGroupDefinition);
        if (itemMappingAssistant != null) {
            return itemMappingAssistant.getMappedItemDefinition(itemDefinition.getOid());

        } else {
            return null;
        }
    }

    public void setItemMapping(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition itemGroupDefinition, ItemDefinition sourceItemDefinition, ItemDefinition targetItemDefinition) {
        OdmItemMappingAssistant itemMappingAssistant = this.getItemMappingAssistant(eventDefinition, formDefinition, itemGroupDefinition);
        itemMappingAssistant.setMapping(sourceItemDefinition, targetItemDefinition);
    }

    public void clearItemMapping(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition itemGroupDefinition, ItemDefinition sourceItemDefinition) {
        OdmItemMappingAssistant itemMappingAssistant = this.getItemMappingAssistant(eventDefinition, formDefinition, itemGroupDefinition);
        itemMappingAssistant.clearMapping(sourceItemDefinition);
    }

    public void autoMapItems() {
        for (EventDefinition sourceItem : sourceEventDefinitionList) {
            for (EventDefinition targetCandidate : mappingTargetCandidates) {
                if (sourceItem.getName().equalsIgnoreCase(targetCandidate.getName())) {
                    setMapping(sourceItem, targetCandidate);
                    break;
                }
            }
        }
    }

    public Integer getMappedItemCount() {
        return this.mappedTargets.size();
    }

    public Integer getUnmappedCandidatesCount() {
        return this.mappingTargetCandidates.size();
    }


}
