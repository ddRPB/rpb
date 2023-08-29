package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupData;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupDefinition;
import de.dktk.dd.rpb.core.domain.edc.MetaDataVersion;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.FormBasedCrfAttributeTable;
import de.dktk.dd.rpb.core.domain.lab.FormBasedCrfAttributeTableItem;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.dktk.dd.rpb.core.util.Constants.LABKEY_DEFAULT_REPEAT_KEY;

public class OdmFormDataToFormBasedCrfAttributeTablesConverter {
    private static final Logger log = LoggerFactory.getLogger(OdmFormDataToFormBasedCrfAttributeTablesConverter.class);

    private final List<FormDefinition> formDefinitionList;
    private final List<ItemGroupDefinition> itemGroupDefinitionList;

    private final List<ItemDefinition> itemDefinitionList;
    private final OdmEventMetaDataLookup odmEventMetaDataLookup;
    private final OdmStudyMetaDataLookup odmStudyMetaDataLookup;
    private final Map<String, Integer> maxRepeatsPerItemGroup;
    private final Map<String, Boolean> formHasData;
    private final LabKeyExportConfiguration labKeyExportConfiguration;

    private final List<FormBasedCrfAttributeTable> formBasedCrfAttributeTableList = new ArrayList<>();

    /**
     * Converts the data from the Odm export to FormBasedCrfAttributesTables
     *
     * @param odm                       Odm export
     * @param subjectColumnName         String name of the subject column, based on the LabKey configuration read from the export
     * @param odmEventMetaDataLookup    OdmEventMetaDataLookup lookups that help to add event related information
     * @param odmStudyMetaDataLookup    OdmStudyMetaDataLookup lookups that help to add study related information
     * @param labKeyExportConfiguration LabKeyExportConfiguration configuration object for the LabKey export
     * @throws MissingPropertyException
     */
    public OdmFormDataToFormBasedCrfAttributeTablesConverter(
            Odm odm,
            String subjectColumnName,
            OdmEventMetaDataLookup odmEventMetaDataLookup,
            OdmStudyMetaDataLookup odmStudyMetaDataLookup,
            LabKeyExportConfiguration labKeyExportConfiguration
    ) throws MissingPropertyException {
        this.labKeyExportConfiguration = labKeyExportConfiguration;

        MetaDataVersion metaDataVersion = odm.getParentOrSiteStudyByConvention().getMetaDataVersion();
        this.formDefinitionList = metaDataVersion.getFormDefinitions();
        this.itemGroupDefinitionList = metaDataVersion.getItemGroupDefinitions();
        this.itemDefinitionList = metaDataVersion.getItemDefinitions();

        this.odmEventMetaDataLookup = odmEventMetaDataLookup;
        this.odmStudyMetaDataLookup = odmStudyMetaDataLookup;

        List<ClinicalData> clinicalDataList = odm.getClinicalDataList();
        this.maxRepeatsPerItemGroup = getMaxRepeatsPerItemGroup(clinicalDataList);
        this.formHasData = checkIfFormHasData(clinicalDataList);

        createFormBasedCrfAttributeTablesFromFormDefinitions(subjectColumnName);

        addClinicalDataToCrfAttributeTable(clinicalDataList);
    }

    public List<FormBasedCrfAttributeTable> getFormBasedCrfAttributeTableList() {
        return formBasedCrfAttributeTableList;
    }

    /**
     * Creates a FormBasedCrfAttributesTable per Form, based on the FormDefinition
     *
     * @param subjectColumnName String alternative column name, based on LabKey configuration
     */
    private void createFormBasedCrfAttributeTablesFromFormDefinitions(String subjectColumnName) {
        for (FormDefinition formDefinition : this.formDefinitionList) {
            String formOid = formDefinition.getOid();

            if (formContainsData(formOid) || exportAllFormsIsEnabled()) {

                FormBasedCrfAttributeTable table = new FormBasedCrfAttributeTable(formDefinition, subjectColumnName, this.labKeyExportConfiguration);
                this.formBasedCrfAttributeTableList.add(table);

                List<ItemGroupDefinition> tableItemGroupDefinitionList = new ArrayList<>();
                table.setItemGroupDefinitionList(tableItemGroupDefinitionList);

                Map<String, Integer> maxRepeatsPerItemGroupPerTable = new HashMap<>();
                table.setMaxRepeatsPerItemGroup(maxRepeatsPerItemGroupPerTable);

                // maxRepeatsPerItemGroupPerTable helps create table columns for repeating item groups in oid_{RepeatKey} format
                for (ItemGroupDefinition itemGroupDefinition : formDefinition.getItemGroupDefs()) {
                    tableItemGroupDefinitionList.add((ItemGroupDefinition) SerializationUtils.clone(itemGroupDefinition));

                    if (this.maxRepeatsPerItemGroup.containsKey(itemGroupDefinition.getOid())) {
                        maxRepeatsPerItemGroupPerTable.put(itemGroupDefinition.getOid(), this.maxRepeatsPerItemGroup.get(itemGroupDefinition.getOid()));
                    } else {
                        maxRepeatsPerItemGroupPerTable.put(itemGroupDefinition.getOid(), 1);
                    }

                }

                List<ItemDefinition> tableItemDefinitions = new ArrayList<>();
                table.setItemDefinitionList(tableItemDefinitions);

                for (ItemGroupDefinition itemGroupDefinition : tableItemGroupDefinitionList) {
                    List<ItemDefinition> itemDefinitionList = itemGroupDefinition.getItemDefs();
                    // Filter items that do not belong to form
                    List<ItemDefinition> filteredItemDefinitionList = new ArrayList<>();
                    itemGroupDefinition.setItemDefs(filteredItemDefinitionList);

                    for (ItemDefinition itemDefinition : itemDefinitionList) {
                        // The FormOid property lists all forms where the item belongs to
                        List<String> formOidList = new ArrayList<>();
                        if (itemDefinition.getFormOids() != null) {
                            formOidList.addAll(Arrays.asList(itemDefinition.getFormOids().split(",")));
                        } else {
                            // item belongs to the if form if FormOid property is null as default
                            formOidList.add(formOid);
                        }

                        ItemDefinition fullDefinition = null;
                        if (formOidList.contains(formOid)) {
                            fullDefinition = this.getItemDefinition(itemDefinition.getItemOid());
                        }

                        if (fullDefinition != null) {
                            tableItemDefinitions.add(fullDefinition);
                            filteredItemDefinitionList.add(itemDefinition);
                        }
                    }
                }

                // A list of ItemReferences is used as reference for the order of the columns in a table
                table.createOrderedItemReferencesLists();
            }
        }
    }

    private boolean exportAllFormsIsEnabled() {
        return !this.labKeyExportConfiguration.isExportOnlyFormFormBasedCrfAttributesWithData();
    }

    private boolean formContainsData(String formOid) {
        return this.formHasData.containsKey(formOid);
    }

    /**
     * Creates table items (FormBasedCrfAttributeTableItem) based on the ClinicalData from the ODM.
     *
     * @param clinicalDataList ClinicalData list parsed from the ODM export
     * @throws MissingPropertyException
     */
    private void addClinicalDataToCrfAttributeTable(List<ClinicalData> clinicalDataList) throws MissingPropertyException {
        for (ClinicalData clinicalData : clinicalDataList) {

            String studyOid = clinicalData.getStudyOid();
            String siteIdentifier = this.odmStudyMetaDataLookup.getStudySiteIdentifierByOid(studyOid);
            List<StudySubject> subjectList = clinicalData.getStudySubjects();

            if (subjectList == null) {
                log.debug("Subject list of study \"" + clinicalData.getStudyOid() + "\" is empty");
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

                        // Creating an item only if the form contains ItemGroups
                        if (formData.getItemGroupDataList() != null) {
                            FormBasedCrfAttributeTable attributeTable = this.getCrfTable(formOid);
                            FormBasedCrfAttributeTableItem attributeTableItem = new FormBasedCrfAttributeTableItem(
                                    participantId,
                                    pid,
                                    eventOid,
                                    sequenceNumber,
                                    eventRepeatKey,
                                    siteIdentifier,
                                    formOid,
                                    formData,
                                    labKeyExportConfiguration);
                            attributeTable.addCrfAttributeTableItem(attributeTableItem);
                        }

                    }
                }
            }
        }
    }

    /**
     * Uses the ClinicalData from the ODM to create a lookup with the maximum of repeats per ItemGroup. The lookup is
     * used to create the extra columns oid_{RepeatKey}.
     *
     * @param clinicalDataList ClinicalData list parsed from the ODM export
     * @return Map<String, Boolean> ItemGroupOid -> maximum of repeats
     */
    private Map<String, Integer> getMaxRepeatsPerItemGroup(List<ClinicalData> clinicalDataList) {
        Map<String, Integer> maxRepeatsPerItemGroup = new HashMap<>();
        for (ClinicalData clinicalData : clinicalDataList) {

            List<StudySubject> subjectList = clinicalData.getStudySubjects();

            if (subjectList == null) {
                return maxRepeatsPerItemGroup;
            }

            for (StudySubject subject : subjectList) {
                for (EventData event : subject.getEventOccurrences()) {
                    for (FormData formData : event.getFormDataList()) {

                        if (formData.getItemGroupDataList() != null) {
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
        }

        return maxRepeatsPerItemGroup;
    }

    /**
     * Creates a lookup that reflects if a form defined in the ODM export file is empty or contains real data
     *
     * @param clinicalDataList ClinicalData list parsed from the ODM export
     * @return Map<String, Boolean> FormOid -> hasData
     */
    private Map<String, Boolean> checkIfFormHasData(List<ClinicalData> clinicalDataList) {
        Map<String, Boolean> formHasData = new HashMap<>();
        for (ClinicalData clinicalData : clinicalDataList) {
            List<StudySubject> studySubjectList = clinicalData.getStudySubjects();

            if (studySubjectList == null) {
                return formHasData;
            }

            for (StudySubject subject : clinicalData.getStudySubjects()) {
                for (EventData event : subject.getEventOccurrences()) {
                    for (FormData formData : event.getFormDataList()) {

                        if (formData.getItemGroupDataList() != null) {
                            if (formData.getItemGroupDataList().size() > 0) {
                                formHasData.put(formData.getFormOid(), true);
                            }
                        }

                    }
                }
            }
        }
            return formHasData;
        }

        private ItemDefinition getItemDefinition (String itemOid){

            for (ItemDefinition itemDefinition : this.itemDefinitionList) {
                if (itemDefinition.getOid().equalsIgnoreCase(itemOid)) {
                    return itemDefinition;
                }
            }
            log.debug("Could not find ItemDefinition with oid: \"" + itemOid + "\" in this.itemDefinitionList.");
            return null;
        }

        private FormBasedCrfAttributeTable getCrfTable (String formOid){
            for (FormBasedCrfAttributeTable table : this.formBasedCrfAttributeTableList) {
                if (table.getOid().equalsIgnoreCase(formOid)) {
                    return table;
                }
            }
            log.debug("Could not find FormBasedCrfAttributeTable with oid: \"" + formOid + "\" in this.formBasedCrfAttributeTableList.");
            return null;
        }

    }
