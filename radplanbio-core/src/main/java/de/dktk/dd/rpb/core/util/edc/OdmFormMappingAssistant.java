package de.dktk.dd.rpb.core.util.edc;

import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupDefinition;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappingRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OdmFormMappingAssistant {

    private EventDefinition sourceEvent;
    private EventDefinition targetEvent;
    private List<MappingRecord> mappingRecordList;

    private List<FormDefinition> sourceFormDefinitions = new ArrayList<>();
    private List<FormDefinition> mappingTargetCandidates = new ArrayList<>();
    private List<FormDefinition> mappedTargets = new ArrayList<>();

    private HashMap<String, FormDefinition> formMappingLookup = new HashMap<>();
    private HashMap<FormDefinition, OdmItemGroupMappingAssistant> itemGroupMappingAssistantHashMap = new HashMap<>();

    public OdmFormMappingAssistant(EventDefinition sourceEvent, EventDefinition targetEvent) {
        this.sourceEvent = sourceEvent;
        this.targetEvent = targetEvent;
        this.mappingRecordList = new ArrayList<>();

        this.initialize();
    }

    public List<FormDefinition> getSourceFormDefinitions() {
        return sourceFormDefinitions;
    }

    public List<FormDefinition> getMappingTargetCandidates() {
        return mappingTargetCandidates;
    }

    public List<FormDefinition> getMappedTargets() {
        return mappedTargets;
    }

    private void initialize() {
        this.sourceFormDefinitions.addAll(sourceEvent.getFormDefs());
        this.mappingTargetCandidates.addAll(targetEvent.getFormDefs());
        this.formMappingLookup.clear();
        this.autoMapItems();
    }

    public FormDefinition getMappedFormDefinition(String sourceOid) {
        return this.formMappingLookup.get(sourceOid);
    }



    public void setMapping(FormDefinition sourceDefinition, FormDefinition targetDefinition) {
        this.formMappingLookup.put(sourceDefinition.getOid(), targetDefinition);
        this.mappedTargets.add(targetDefinition);
        this.mappingTargetCandidates.remove(targetDefinition);
        this.itemGroupMappingAssistantHashMap.put(sourceDefinition, new OdmItemGroupMappingAssistant(sourceDefinition, targetDefinition));
    }

    public void clearMapping(FormDefinition sourceDefinition) {
        this.mappedTargets.remove(formMappingLookup.get(sourceDefinition.getOid()));
        this.mappingTargetCandidates.add(formMappingLookup.get(sourceDefinition.getOid()));
        this.formMappingLookup.remove(sourceDefinition.getOid());
        this.itemGroupMappingAssistantHashMap.remove(sourceDefinition);
        //TODO
    }

    public OdmItemGroupMappingAssistant getItemGroupMappingAssistant(FormDefinition formDefinition) {
        return itemGroupMappingAssistantHashMap.get(formDefinition);
    }

    public void autoMapItems(){
        for(FormDefinition sourceItem: sourceFormDefinitions){
            for(FormDefinition targetCandidate: mappingTargetCandidates){
                if(sourceItem.getName().equalsIgnoreCase(targetCandidate.getName())){
                    setMapping(sourceItem,targetCandidate);
                    break;
                }
            }
        }
    }

    public Integer getMappedItemCount(){
        return this.mappedTargets.size();
    }

    public Integer getUnmappedCandidatesCount(){
        return this.mappingTargetCandidates.size();
    }


}
