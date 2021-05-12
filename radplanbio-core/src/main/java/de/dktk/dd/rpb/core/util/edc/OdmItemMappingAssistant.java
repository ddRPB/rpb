package de.dktk.dd.rpb.core.util.edc;

import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OdmItemMappingAssistant {

    private ItemGroupDefinition sourceItemGroupDefinition;
    private ItemGroupDefinition targetItemGroupDefinition;

    private List<ItemDefinition> sourceItemList = new ArrayList<>();

    private List<ItemDefinition> mappingTargetCandidates = new ArrayList<>();
    private List<ItemDefinition> mappedTargets = new ArrayList<>();

    private HashMap<String, ItemDefinition> itemMapping = new HashMap<>();

    public OdmItemMappingAssistant(ItemGroupDefinition sourceItemGroupDefinition, ItemGroupDefinition targetItemGroupDefinition) {
        this.sourceItemGroupDefinition = sourceItemGroupDefinition;
        this.targetItemGroupDefinition = targetItemGroupDefinition;
        initialize();
    }

    public List<ItemDefinition> getMappingTargetCandidates() {
        return mappingTargetCandidates;
    }

    public List<ItemDefinition> getSourceItemList() {
        return sourceItemList;
    }

    private void initialize(){
        this.sourceItemList.addAll(sourceItemGroupDefinition.getItemDefs());
        this.mappingTargetCandidates.addAll(targetItemGroupDefinition.getItemDefs());
        this.mappedTargets.clear();
        this.autoMapItems();
    }

    public ItemDefinition getMappedItemDefinition(String oid){
        return itemMapping.get(oid);
    }

    public void setMapping(ItemDefinition sourceItemDefinition, ItemDefinition targetItemDefinition){
        if(itemMapping.containsKey(sourceItemDefinition.getOid())){
            clearMapping(sourceItemDefinition);
        }
        itemMapping.put(sourceItemDefinition.getOid(), targetItemDefinition);
        mappedTargets.add(targetItemDefinition);
        mappingTargetCandidates.remove(targetItemDefinition);
    }

    public void clearMapping(ItemDefinition sourceItemDefinition){
        mappedTargets.remove(itemMapping.get(sourceItemDefinition.getOid()));
        mappingTargetCandidates.add(itemMapping.get(sourceItemDefinition.getOid()));
        itemMapping.remove(sourceItemDefinition.getOid());
    }

    public void autoMapItems(){
        for(ItemDefinition sourceItem: sourceItemList){
            for(ItemDefinition targetCandidate: mappingTargetCandidates){
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
