package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.lab.ItemGroupBasedCrfAttributeTable;
import de.dktk.dd.rpb.core.domain.lab.ItemGroupBasedCrfAttributeTableItem;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.dktk.dd.rpb.core.util.Constants.LABKEY_DEFAULT_REPEAT_KEY;

public class OdmFormDataToItemGroupBasedCrfAttributeTablesConverter {

    private final List<ItemGroupDefinition> itemGroupDefinitionList;
    private final OdmEventMetaDataLookup odmEventMetaDataLookup;
    private final OdmStudyMetaDataLookup odmStudyMetaDataLookup;
    private final Map<String, Boolean> itemGroupHasData;
    private Map<String, Boolean> itemGroupIsRepeating;
    private final LabKeyExportConfiguration labKeyExportConfiguration;

    private final List<ItemGroupBasedCrfAttributeTable> itemGroupBasedCrfAttributeTableList = new ArrayList<>();

    public OdmFormDataToItemGroupBasedCrfAttributeTablesConverter(
            Odm odm,
            String subjectColumnName,
            OdmEventMetaDataLookup odmEventMetaDataLookup,
            OdmStudyMetaDataLookup odmStudyMetaDataLookup,
            LabKeyExportConfiguration labKeyExportConfiguration
    ) throws MissingPropertyException {
        this.labKeyExportConfiguration = labKeyExportConfiguration;

        MetaDataVersion metaDataVersion = odm.getParentOrSiteStudyByConvention().getMetaDataVersion();
        this.itemGroupDefinitionList = metaDataVersion.getItemGroupDefinitions();

        this.odmEventMetaDataLookup = odmEventMetaDataLookup;
        this.odmStudyMetaDataLookup = odmStudyMetaDataLookup;

        List<ClinicalData> clinicalDataList = odm.getClinicalDataList();
        itemGroupHasData = checkIfItemGroupsHaveData(clinicalDataList);
        checkIfItemGroupsAreRepeating();

        createCrfAttributeTablesFromItemGroupDefinitions(subjectColumnName);
        addClinicalDataToCrfAttributeTable(clinicalDataList);
    }

    private Map<String, Boolean> checkIfItemGroupsHaveData(List<ClinicalData> clinicalDataList) {
        final Map<String, Boolean> itemGroupHasData;
        itemGroupHasData = new HashMap<>();
        for (ClinicalData clinicalData : clinicalDataList) {
            List<StudySubject> studySubjectList = clinicalData.getStudySubjects();

            if (studySubjectList == null) {
                return itemGroupHasData;
            }

            for (StudySubject subject : clinicalData.getStudySubjects()) {
                for (EventData event : subject.getEventOccurrences()) {
                    for (FormData formData : event.getFormDataList()) {

                        if (formData.getItemGroupDataList() != null) {
                            if (formData.getItemGroupDataList().size() > 0) {
                                for (ItemGroupData itemGroupData : formData.getItemGroupDataList()) {
                                    if (itemGroupData.getItemDataList().size() > 0) {
                                        itemGroupHasData.put(itemGroupData.getItemGroupOid(), true);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        return itemGroupHasData;
    }

    private void checkIfItemGroupsAreRepeating() {
        this.itemGroupIsRepeating = new HashMap<>();
        for (ItemGroupDefinition definition : this.itemGroupDefinitionList) {
            boolean isRepeating = definition.getIsRepeating();
            String oid = definition.getOid();
            this.itemGroupIsRepeating.put(oid, isRepeating);
        }
    }

    public List<ItemGroupBasedCrfAttributeTable> getItemGroupBasedCrfAttributeTableList() {
        return itemGroupBasedCrfAttributeTableList;
    }

    private void createCrfAttributeTablesFromItemGroupDefinitions(String subjectColumnName) {

        for (ItemGroupDefinition itemGroupDefinition : itemGroupDefinitionList) {
            String itemGroupOid = itemGroupDefinition.getOid();

            boolean itemGroupHasData = this.itemGroupHasData.containsKey(itemGroupOid) || !this.labKeyExportConfiguration.isExportOnlyItemGroupBasedCrfAttributesWithData();
            boolean itemGroupIsRepeating = this.itemGroupIsRepeating.get(itemGroupOid) || !this.labKeyExportConfiguration.isExportOnlyRepeatingItemGroupBasedCrfAttributes();

            if (itemGroupHasData && itemGroupIsRepeating) {
                ItemGroupBasedCrfAttributeTable table = new ItemGroupBasedCrfAttributeTable(itemGroupDefinition, subjectColumnName, labKeyExportConfiguration);
                this.itemGroupBasedCrfAttributeTableList.add(table);
                table.createOrderedItemsLists();
            }

        }

    }

    private void addClinicalDataToCrfAttributeTable(List<ClinicalData> clinicalDataList) throws MissingPropertyException {
        for (ClinicalData clinicalData : clinicalDataList) {
            String studyOid = clinicalData.getStudyOid();
            String siteIdentifier = this.odmStudyMetaDataLookup.getStudySiteIdentifierByOid(studyOid);

            List<StudySubject> subjectList = clinicalData.getStudySubjects();

            if (subjectList == null) {
                return;
            }

            for (StudySubject subject : clinicalData.getStudySubjects()) {
                String participantId = subject.getStudySubjectId();
                String pid = subject.getPid();
                for (EventData event : subject.getEventOccurrences()) {
                    String eventOid = event.getStudyEventOid();
                    Integer eventRepeatKey = event.getStudyEventRepeatKeyInteger();
                    if (eventRepeatKey == null) {
                        eventRepeatKey = LABKEY_DEFAULT_REPEAT_KEY;
                    }
                    Integer ordinal = odmEventMetaDataLookup.getStudyEventOrdinal(eventOid);
                    Boolean isRepeating = odmEventMetaDataLookup.getStudyEventIsRepeating(eventOid);
                    Double sequenceNumber = SequenceNumberCalculator.calculateLabKeySequenceNumber(isRepeating, String.valueOf(eventRepeatKey), ordinal);


                    for (FormData formData : event.getFormDataList()) {
                        String formOid = formData.getFormOid();
                        Integer formRepeatKey = LABKEY_DEFAULT_REPEAT_KEY;

                        List<ItemGroupData> itemGroupDataList = formData.getItemGroupDataList();

                        if (itemGroupDataList != null) {
                            for (ItemGroupData itemGroupData : itemGroupDataList) {
                                String itemGroupOid = itemGroupData.getItemGroupOid();
                                String itemGroupRepeatKeyString = itemGroupData.getItemGroupRepeatKey();
                                Integer itemGroupRepeatKey;

                                if (itemGroupRepeatKeyString == null) {
                                    itemGroupRepeatKey = LABKEY_DEFAULT_REPEAT_KEY;
                                } else {
                                    itemGroupRepeatKey = Integer.parseInt(itemGroupRepeatKeyString);
                                }

                                List<ItemData> itemDataList = itemGroupData.getItemDataList();
                                ItemGroupBasedCrfAttributeTable attributeTable = this.getCrfTable(itemGroupOid);

                                if (attributeTable != null) {
                                    ItemGroupBasedCrfAttributeTableItem attributeTableItem = new ItemGroupBasedCrfAttributeTableItem(
                                            participantId,
                                            pid,
                                            sequenceNumber,
                                            eventOid,
                                            eventRepeatKey,
                                            siteIdentifier,
                                            formOid,
                                            formRepeatKey,
                                            itemGroupOid,
                                            labKeyExportConfiguration, itemGroupRepeatKey,
                                            itemDataList
                                    );
                                    attributeTable.addCrfAttributeTableItem(attributeTableItem);
                                }
                            }
                        }


                    }
                }
            }
        }
    }

    private ItemGroupBasedCrfAttributeTable getCrfTable(String itemGroupOid) {
        for (ItemGroupBasedCrfAttributeTable table : this.itemGroupBasedCrfAttributeTableList) {
            if (table.getItemGroupDefinition().getOid().equalsIgnoreCase(itemGroupOid)) {
                return table;
            }
        }
        return null;
    }

}