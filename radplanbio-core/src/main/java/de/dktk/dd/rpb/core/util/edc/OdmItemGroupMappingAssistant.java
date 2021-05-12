package de.dktk.dd.rpb.core.util.edc;

import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OdmItemGroupMappingAssistant {

    private FormDefinition sourceForm;
    private FormDefinition destinationForm;

    private List<ItemGroupDefinition> sourceItemGroupList = new ArrayList<>();

    private List<ItemGroupDefinition> mappingTargetCandidates = new ArrayList<>();
    private List<ItemGroupDefinition> mappedTargets = new ArrayList<>();

    private HashMap<String, ItemGroupDefinition> itemGroupMapping = new HashMap<>();
    private HashMap<ItemGroupDefinition, OdmItemMappingAssistant> itemMappingAssistantHashMap = new HashMap<>();

    public OdmItemGroupMappingAssistant(FormDefinition sourceForm, FormDefinition destinationForm) {
        this.sourceForm = sourceForm;
        this.destinationForm = destinationForm;

        this.initialize();
    }

    public List<ItemGroupDefinition> getSourceItemGroupList() {
        return sourceItemGroupList;
    }

    private void initialize(){
        this.sourceItemGroupList.addAll(sourceForm.getItemGroupDefs());
        this.mappingTargetCandidates.addAll(destinationForm.getItemGroupDefs());
        itemGroupMapping.clear();
        this.autoMapItems();
    }

    public List<ItemGroupDefinition> getMappingTargetCandidates() {
        return mappingTargetCandidates;
    }

    public void setMapping(ItemGroupDefinition sourceItemGroupDefinition, ItemGroupDefinition targetItemGroupDefinition){
        if(itemGroupMapping.containsKey(sourceItemGroupDefinition.getOid())){
            clearMapping(sourceItemGroupDefinition);
        }
        itemGroupMapping.put(sourceItemGroupDefinition.getOid(), targetItemGroupDefinition);
        mappedTargets.add(targetItemGroupDefinition);
        mappingTargetCandidates.remove(targetItemGroupDefinition);
        itemMappingAssistantHashMap.put(sourceItemGroupDefinition, new OdmItemMappingAssistant(sourceItemGroupDefinition,targetItemGroupDefinition));
    }

    public void clearMapping(ItemGroupDefinition sourceItemGroupDefinition){
        mappedTargets.remove(itemGroupMapping.get(sourceItemGroupDefinition.getOid()));
        mappingTargetCandidates.add(itemGroupMapping.get(sourceItemGroupDefinition.getOid()));
        itemGroupMapping.remove(sourceItemGroupDefinition.getOid());
        itemMappingAssistantHashMap.remove(sourceItemGroupDefinition);
    }

    public ItemGroupDefinition getMappedItemGroupDefinition(String sourceOid){
        return itemGroupMapping.get(sourceOid);
    }
    public OdmItemMappingAssistant getItemMappingAssistant(ItemGroupDefinition itemGroupDefinition) {
        return itemMappingAssistantHashMap.get(itemGroupDefinition);
    }

    public void autoMapItems(){
        for(ItemGroupDefinition sourceItem: sourceItemGroupList){
            for(ItemGroupDefinition targetCandidate: mappingTargetCandidates){
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
