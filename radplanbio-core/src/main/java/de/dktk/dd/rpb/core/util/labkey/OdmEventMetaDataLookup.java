package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.EventDefinitionDetails;
import de.dktk.dd.rpb.core.domain.edc.EventReference;
import de.dktk.dd.rpb.core.domain.edc.MetaDataVersion;

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

}
