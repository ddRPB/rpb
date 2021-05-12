package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.lab.CrfAttributeTable;
import de.dktk.dd.rpb.core.domain.lab.CrfAttributeTableItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OdmFormDataToAttributeTablesConverter {

    private final List<FormDefinition> formDefinitionList;
    private final List<ItemGroupDefinition> itemGroupDefinitionList;
    private final List<ItemDefinition> itemDefinitionList;
    private final Map<String, Integer> maxRepeatsPerItemGroup;
    private final Map<String, Boolean> formHasData;

    private final List<CrfAttributeTable> crfAttributeTableList = new ArrayList<>();

    public OdmFormDataToAttributeTablesConverter(Odm odm) {
        MetaDataVersion metaDataVersion = odm.findFirstStudyOrNone().getMetaDataVersion();
        this.formDefinitionList = metaDataVersion.getFormDefinitions();
        this.itemGroupDefinitionList = metaDataVersion.getItemGroupDefinitions();
        this.itemDefinitionList = metaDataVersion.getItemDefinitions();

        List<ClinicalData> clinicalDataList = odm.getClinicalDataList();
        Map<String, Integer> maxRepeatsPerItemGroup = getMaxRepeatsPerItemGroup(clinicalDataList);
        this.maxRepeatsPerItemGroup = maxRepeatsPerItemGroup;
        this.formHasData = checkIfFormHasData(clinicalDataList);
        createCrfAttributeTablesFromFormDefinitions();
        addClinicalDataToCrfAttributeTable(clinicalDataList);
    }


    public List<CrfAttributeTable> getCrfAttributeTableList() {
        return this.crfAttributeTableList;
    }

    private void createCrfAttributeTablesFromFormDefinitions() {
        for (FormDefinition formDefinition : this.formDefinitionList) {
            String formOid = formDefinition.getOid();

            if (this.formHasData.containsKey(formOid)) {
                CrfAttributeTable table = new CrfAttributeTable(formDefinition);
                this.crfAttributeTableList.add(table);

                List<ItemGroupDefinition> tableItemGroupDefinitionList = new ArrayList<>();
                table.setItemGroupDefinitionList(tableItemGroupDefinitionList);

                Map<String, Integer> maxRepeatsPerItemGroup = new HashMap<>();
                table.setMaxRepeatsPerItemGroup(maxRepeatsPerItemGroup);

                for (ItemGroupDefinition itemGroupDefinition : formDefinition.getItemGroupRefs()) {
                    String oid = itemGroupDefinition.getItemGroupOid();
                    ItemGroupDefinition realItemGroupDefinition = this.getItemGroupDefinition(oid);
                    tableItemGroupDefinitionList.add(realItemGroupDefinition);

                    if (this.maxRepeatsPerItemGroup.containsKey(itemGroupDefinition.getItemGroupOid())) {
                        maxRepeatsPerItemGroup.put(itemGroupDefinition.getItemGroupOid(), this.maxRepeatsPerItemGroup.get(itemGroupDefinition.getItemGroupOid()));
                    } else {
                        maxRepeatsPerItemGroup.put(itemGroupDefinition.getItemGroupOid(), 1);
                    }

                }

                List<ItemDefinition> tableItemDefinitions = new ArrayList<>();
                table.setItemDefinitionList(tableItemDefinitions);

                for (ItemGroupDefinition itemGroupDefinition : tableItemGroupDefinitionList) {
                    List<ItemDefinition> itemDefinitionList = itemGroupDefinition.getItemRefs();
                    for (ItemDefinition itemDefinition : itemDefinitionList) {
                        tableItemDefinitions.add(this.getItemDefinition(itemDefinition.getItemOid()));
                    }
                }
            }
        }
    }

    private void addClinicalDataToCrfAttributeTable(List<ClinicalData> clinicalDataList) {
        for (ClinicalData clinicalData : clinicalDataList) {

            List<StudySubject> subjectList = clinicalData.getStudySubjects();

            if(subjectList == null){
                return;
            }

            for (StudySubject subject : clinicalData.getStudySubjects()) {
                String participantId = subject.getStudySubjectId();
                for (EventData event : subject.getEventOccurrences()) {
                    String eventOid = event.getStudyEventOid();
                    Integer eventRepeatKey = event.getStudyEventRepeatKeyInteger();
                    for (FormData formData : event.getFormDataList()) {
                        String formOid = formData.getFormOid();

                        CrfAttributeTable attributeTable = this.getCrfTable(formOid);
                        CrfAttributeTableItem attributeTableItem = new CrfAttributeTableItem(participantId, eventOid, eventRepeatKey, formOid, formData);
                        attributeTable.addCrfAttributeTableItem(attributeTableItem);

                    }
                }
            }
        }
    }

    private Map<String, Integer> getMaxRepeatsPerItemGroup(List<ClinicalData> clinicalDataList) {
        Map<String, Integer> maxRepeatsPerItemGroup = new HashMap<>();
        for (ClinicalData clinicalData : clinicalDataList) {

            List<StudySubject> subjectList = clinicalData.getStudySubjects();

            if(subjectList == null){
                return maxRepeatsPerItemGroup;
            }

            for (StudySubject subject : subjectList) {
                for (EventData event : subject.getEventOccurrences()) {
                    for (FormData formData : event.getFormDataList()) {
                        for (ItemGroupData itemGroupDefinition : formData.getItemGroupDataList()) {
                            String repeatKey = itemGroupDefinition.getItemGroupRepeatKey();
                            if (repeatKey != null) {
                                Integer currentMaximum = 0;
                                if (maxRepeatsPerItemGroup.containsKey(itemGroupDefinition.getItemGroupOid())) {
                                    currentMaximum = maxRepeatsPerItemGroup.get(itemGroupDefinition.getItemGroupOid());
                                }
                                maxRepeatsPerItemGroup.put(itemGroupDefinition.getItemGroupOid(), Integer.max(currentMaximum, Integer.parseInt(repeatKey)));

                            }
                        }
                    }
                }
            }
        }

        return maxRepeatsPerItemGroup;
    }

    private Map<String, Boolean> checkIfFormHasData(List<ClinicalData> clinicalDataList) {
        Map<String, Boolean> formHasData = new HashMap<>();
        for (ClinicalData clinicalData : clinicalDataList) {
            List<StudySubject> studySubjectList = clinicalData.getStudySubjects();

            if(studySubjectList == null){
                return formHasData;
            }

            for (StudySubject subject : clinicalData.getStudySubjects()) {
                for (EventData event : subject.getEventOccurrences()) {
                    for (FormData formData : event.getFormDataList()) {
                        if (formData.getItemGroupDataList().size() > 0) {
                            formHasData.put(formData.getFormOid(), true);
                        }
                    }
                }
            }
        }
        return formHasData;
    }


    private ItemGroupDefinition getItemGroupDefinition(String itemGroupOid) {

        for (ItemGroupDefinition itemGroupDefinition : this.itemGroupDefinitionList) {
            if (itemGroupDefinition.getOid().equalsIgnoreCase(itemGroupOid)) {
                return itemGroupDefinition;
            }
        }
        return null;
    }

    private ItemDefinition getItemDefinition(String itemOid) {

        for (ItemDefinition itemDefinition : this.itemDefinitionList) {
            if (itemDefinition.getOid().equalsIgnoreCase(itemOid)) {
                return itemDefinition;
            }
        }
        return null;
    }

    private CrfAttributeTable getCrfTable(String formoid) {
        for (CrfAttributeTable table : this.crfAttributeTableList) {
            if (table.getOid().equalsIgnoreCase(formoid)) {
                return table;
            }
        }
        return null;
    }
}
