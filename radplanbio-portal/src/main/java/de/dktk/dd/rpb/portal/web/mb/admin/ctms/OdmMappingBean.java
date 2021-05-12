package de.dktk.dd.rpb.portal.web.mb.admin.ctms;


import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.edc.mapping.*;
import de.dktk.dd.rpb.core.ocsoap.odm.MetadataODM;
import de.dktk.dd.rpb.core.repository.edc.IMappingRepository;
import de.dktk.dd.rpb.core.repository.support.Repository;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import de.dktk.dd.rpb.core.util.edc.OdmEventMappingAssistant;
import de.dktk.dd.rpb.core.util.edc.OdmFormMappingAssistant;
import de.dktk.dd.rpb.core.util.edc.OdmItemGroupMappingAssistant;
import de.dktk.dd.rpb.core.util.edc.OdmItemMappingAssistant;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("odmMapping")
@Scope("view")
public class OdmMappingBean extends CrudEntityViewModel<Mapping, Integer> {
    //region Injects

    private final MainBean mainBean;
    private final AuditLogService auditLogService;
    private final StudyIntegrationFacade studyIntegrationFacade;

    private IMappingRepository repository;

    //endregion

    //region members

    private int activeIndex = 0;

    private UploadedFile uploadedFile;
    private Odm sourceOdm;
    private Odm targetOdm;

    private OdmEventMappingAssistant mappingAssistant;

    private EventDefinition selectedEventDefinition;
    private FormDefinition selectedFormDefinition;
    private ItemGroupDefinition selectedItemGroupDefinition;
    private ItemDefinition selectedItemDefinition;

    private List<EventDefinition> selectedEventDefinitionList = new ArrayList<>();
    private List<FormDefinition> selectedFormDefinitionList = new ArrayList<>();

    public OdmMappingBean(MainBean mainBean, AuditLogService auditLogService, StudyIntegrationFacade studyIntegrationFacade, IMappingRepository repository) {
        this.mainBean = mainBean;
        this.auditLogService = auditLogService;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.repository = repository;
        init();
    }

    public void init(){
        this.studyIntegrationFacade.init(mainBean);
    }

    // region getter and setter

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }


    public List<EventDefinition> getSelectedEventDefinitionList() {
        return selectedEventDefinitionList;
    }

    public void setSelectedEventDefinitionList(List<EventDefinition> selectedEventDefinitionList) {
        this.selectedEventDefinitionList = selectedEventDefinitionList;
    }

    public FormDefinition getSelectedFormDefinition() {
        return selectedFormDefinition;
    }

    public List<FormDefinition> getSelectedFormDefinitionAsList() {
        List<FormDefinition> formDefinitionList = new ArrayList<>();
        formDefinitionList.add(selectedFormDefinition);
        return formDefinitionList;
    }

    public void setSelectedFormDefinition(FormDefinition selectedFormDefinition) {
        this.selectedFormDefinition = selectedFormDefinition;
    }

    public List<FormDefinition> getSelectedFormDefinitionList() {
        return selectedFormDefinitionList;
    }

    public void setSelectedFormDefinitionList(List<FormDefinition> selectedFormDefinitionList) {
        this.selectedFormDefinitionList = selectedFormDefinitionList;
    }

    public EventDefinition getSelectedEventDefinition() {
        return selectedEventDefinition;
    }

    public List<EventDefinition> getSelectedEventDefinitionAsList() {
        List<EventDefinition> list = new ArrayList<>();
        list.add(selectedEventDefinition);
        return list;
    }

    public void setSelectedEventDefinition(EventDefinition selectedEventDefinition) {
        this.selectedEventDefinition = selectedEventDefinition;
    }

    public ItemGroupDefinition getSelectedItemGroupDefinition() {
        return selectedItemGroupDefinition;
    }

    public void setSelectedItemGroupDefinition(ItemGroupDefinition selectedItemGroupDefinition) {
        this.selectedItemGroupDefinition = selectedItemGroupDefinition;
    }

    public List<ItemGroupDefinition> getSelectedItemGroupDefinitionAsList() {
        List<ItemGroupDefinition> list = new ArrayList<>();
        list.add(selectedItemGroupDefinition);
        return list;
    }

    public List<ItemDefinition> getSelectedItemDefinitionAsList() {
        List<ItemDefinition> list = new ArrayList<>();
        list.add(this.selectedItemDefinition);
        return list;
    }


    public ItemDefinition getSelectedItemDefinition() {
        return selectedItemDefinition;
    }

    public void setSelectedItemDefinition(ItemDefinition selectedItemDefinition) {
        this.selectedItemDefinition = selectedItemDefinition;
    }

    // endregion

    public void prepareNewOdmUpload() {
        this.uploadedFile = null;
        // TODO: reset parameters

    }

    /**
     * Upload input data file for import
     *
     * @param event upload event
     */
    public void handleUpload(FileUploadEvent event) {
        try {
            this.uploadedFile = event.getFile();
            this.loadFileContent();
            loadTargetMetaData();
            loadAssistant();
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void loadAssistant() {
        this.mappingAssistant = new OdmEventMappingAssistant(this.sourceOdm, this.targetOdm);
    }

    public List<EventDefinition> getSourceEventList() {
        if (this.mappingAssistant != null) {
            return this.mappingAssistant.getSourceEventDefinitionList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<EventDefinition> getTargetEventList() {
        if (this.mappingAssistant != null) {
            return this.mappingAssistant.getMappingTargetCandidates();
        } else {
            return new ArrayList<>();
        }
    }

    public List<FormDefinition> getTargetFormList() {
        if (this.mappingAssistant != null) {
            OdmFormMappingAssistant formMappingAssistant = this.mappingAssistant.getFormMappingAssistant(this.selectedEventDefinition);
            return formMappingAssistant.getMappingTargetCandidates();
        } else {
            return new ArrayList<>();
        }

    }

    public List<ItemGroupDefinition> getTargetItemGroupList() {
        if (this.mappingAssistant != null && this.selectedFormDefinition != null) {
            OdmItemGroupMappingAssistant itemGroupMappingAssistant = this.mappingAssistant.getItemGroupMappingAssistant(this.selectedEventDefinition, this.selectedFormDefinition);
            List<ItemGroupDefinition> targetList = new ArrayList<>();
            if (itemGroupMappingAssistant != null) {
                targetList.addAll(itemGroupMappingAssistant.getMappingTargetCandidates());
            }
            return targetList;
        } else {
            return new ArrayList<>();
        }

    }

    public List<ItemDefinition> getTargetItemList() {
        if (this.mappingAssistant != null && this.selectedFormDefinition != null && this.selectedItemGroupDefinition != null) {
            OdmItemMappingAssistant itemMappingAssistant = this.mappingAssistant.getItemMappingAssistant(this.selectedEventDefinition, this.selectedFormDefinition, this.selectedItemGroupDefinition);
            List<ItemDefinition> targetList = new ArrayList<>();
            if (itemMappingAssistant != null) {
                targetList.addAll(itemMappingAssistant.getMappingTargetCandidates());
            }
            return targetList;
        } else {
            return new ArrayList<>();
        }

    }

    public void loadFileContent() {

        try {
            this.sourceOdm = JAXBHelper.unmarshalInputstream2(Odm.class, this.uploadedFile.getInputstream());
            this.sourceOdm.updateHierarchy();
//            this.eventDefinitionList = createMappedEventDefinitionList(this.sourceOdm);
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
        // TODO
    }

    public void loadTargetMetaData() {
        String uniqueIdentifier = this.mainBean.getActiveStudy().getUniqueIdentifier();
        MetadataODM metadata = this.mainBean
                .getOpenClinicaService()
                .getStudyMetadata(uniqueIdentifier);
        this.targetOdm = metadata.unmarshallOdm();
        this.targetOdm.updateHierarchy();
    }

    public void assignMapping(EventDefinition eventDefinition) {
        this.mappingAssistant.setMapping(this.selectedEventDefinition, eventDefinition);
    }

    public void assignFormMapping(FormDefinition targetDefinition) {
        this.mappingAssistant.setFormMapping(this.selectedEventDefinition, this.selectedFormDefinition, targetDefinition);
    }

    public void assignItemGroupMapping(ItemGroupDefinition targetDefinition) {
        this.mappingAssistant.setItemGroupMapping(this.selectedEventDefinition, this.selectedFormDefinition, this.selectedItemGroupDefinition, targetDefinition);
    }

    public void assignItemMapping(ItemDefinition targetDefinition) {
        this.mappingAssistant.setItemMapping(this.selectedEventDefinition, this.selectedFormDefinition, this.selectedItemGroupDefinition, this.selectedItemDefinition, targetDefinition);
    }

    public void clearFormMapping() {
        this.mappingAssistant.clearFormMapping(this.selectedEventDefinition, this.selectedFormDefinition);
    }

    public void clearItemGroupMapping() {
        this.mappingAssistant.clearItemGroupMapping(this.selectedEventDefinition, this.selectedFormDefinition, this.selectedItemGroupDefinition);
    }

    public void clearItemMapping() {
        this.mappingAssistant.clearItemMapping(this.selectedEventDefinition, this.selectedFormDefinition, this.selectedItemGroupDefinition, this.selectedItemDefinition);
    }

    public void clearMapping(String sourceOid) {
        this.mappingAssistant.clearMapping(sourceOid);
    }


    public void removeSelectedEventsFromMapping() {
        if (this.selectedEventDefinitionList.size() > 0 && this.mappingAssistant != null) {
            this.mappingAssistant.removeSelectedEvents(this.selectedEventDefinitionList);
        }
    }

    public String getMappedItemOid(String oid) {
        if (this.mappingAssistant != null && this.mappingAssistant.getMappedEvent(oid) != null) {
            return this.mappingAssistant.getMappedEvent(oid).getOid();
        }
        return "not mapped";
    }

    public String getMappedItemName(String oid) {
        if (this.mappingAssistant != null && this.mappingAssistant.getMappedEvent(oid) != null) {
            return this.mappingAssistant.getMappedEvent(oid).getName();
        }
        return "not mapped";
    }

    public String getMappedFormItemOid(EventDefinition eventDefinition, String formOid) {
        if (this.mappingAssistant != null && this.mappingAssistant.getMappedForm(eventDefinition, formOid) != null) {
            return this.mappingAssistant.getMappedForm(eventDefinition, formOid).getOid();
        }
        return "not mapped";
    }

    public String getMappedFormItemName(EventDefinition eventDefinition, String formOid) {
        if (this.mappingAssistant != null && this.mappingAssistant.getMappedForm(eventDefinition, formOid) != null) {
            return this.mappingAssistant.getMappedForm(eventDefinition, formOid).getName();
        }
        return "not mapped";
    }

    public String getMappedItemGroupDefOid(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition itemGroupDefinition) {
        if (this.mappingAssistant != null && this.mappingAssistant.getMappedItemGroupDefinition(eventDefinition, formDefinition, itemGroupDefinition) != null) {
            return this.mappingAssistant.getMappedItemGroupDefinition(eventDefinition, formDefinition, itemGroupDefinition).getOid();
        }
        return "not mapped";
    }

    public ItemDefinition getMappedItemDefOid(EventDefinition eventDefinition, FormDefinition formDefinition, ItemGroupDefinition itemGroupDefinition, ItemDefinition itemDefinition) {
        if (this.mappingAssistant != null && this.mappingAssistant.getMappedItemDefinition(eventDefinition, formDefinition, itemGroupDefinition, itemDefinition) != null) {
            return this.mappingAssistant.getMappedItemDefinition(eventDefinition, formDefinition, itemGroupDefinition, itemDefinition);
        } else {
            ItemDefinition dummyItem = new ItemDefinition();
            dummyItem.setOid("not mapped");
            dummyItem.setName("not mapped");
            return dummyItem;
        }
    }

    public void createOdmMapping() {
        List<MappingRecord> mappingRecordList = new ArrayList<>();
            List<Mapping> allMappings = new ArrayList<>();

        if (this.selectedEventDefinitionList.size() > 0 && this.mappingAssistant != null) {

            for (EventDefinition sourceEvent : this.selectedEventDefinitionList) {
                if (mappingAssistant.getMappedEvent(sourceEvent.getOid()) != null) {
                    EventDefinition targetEvent = mappingAssistant.getMappedEvent(sourceEvent.getOid());

                    OdmFormMappingAssistant formMappingAssistant = this.mappingAssistant.getFormMappingAssistant(sourceEvent);
                    if (formMappingAssistant != null) {
                        for (FormDefinition sourceFormDefinition : formMappingAssistant.getSourceFormDefinitions()) {
                            if (formMappingAssistant.getMappedFormDefinition(sourceFormDefinition.getOid()) != null) {
                                FormDefinition targetFormDefinition = formMappingAssistant.getMappedFormDefinition(sourceFormDefinition.getOid());

                                OdmItemGroupMappingAssistant itemGroupMappingAssistant = this.mappingAssistant.getItemGroupMappingAssistant(sourceEvent, sourceFormDefinition);
                                if (itemGroupMappingAssistant != null) {
                                    for (ItemGroupDefinition sourceItemGroupDefinition : itemGroupMappingAssistant.getSourceItemGroupList()) {
                                        Mapping mapping = this.repository.getNew();
                                        mapping.setName(sourceEvent.getOid() + "/" + sourceFormDefinition.getOid() + "/" + sourceItemGroupDefinition.getOid());
                                        mapping.setSourceType(MappingItemTypeEnum.ODM);
                                        mapping.setTargetType(MappingItemTypeEnum.ODM);
                                        mapping.setType(MappingTypeEnum.IMPORT);
                                        String uniqueIdentifier = this.mainBean.getActiveStudy().getUniqueIdentifier();
                                        Study study = this.studyIntegrationFacade.loadStudy();
                                        mapping.setStudy(study);

                                        mapping.setMappingRecords(new ArrayList<>());
                                        mapping.setSourceItemDefinitions( new ArrayList<>());
                                        mapping.setTargetItemDefinitions(new ArrayList<>());

                                        allMappings.add(mapping);

                                        if (itemGroupMappingAssistant.getMappedItemGroupDefinition(sourceItemGroupDefinition.getOid()) != null) {
                                            ItemGroupDefinition targetItemGroupDefinition = itemGroupMappingAssistant.getMappedItemGroupDefinition(sourceItemGroupDefinition.getOid());

                                            OdmItemMappingAssistant itemMappingAssistant = this.mappingAssistant.getItemMappingAssistant(sourceEvent, sourceFormDefinition, sourceItemGroupDefinition);
                                            if (itemMappingAssistant != null) {
                                                for (ItemDefinition sourceItemDefinition : itemMappingAssistant.getSourceItemList()) {
                                                    if (itemMappingAssistant.getMappedItemDefinition(sourceItemDefinition.getOid()) != null) {
                                                        ItemDefinition targetItemDefinition = itemMappingAssistant.getMappedItemDefinition(sourceItemDefinition.getOid());

                                                        MappedOdmItem mappedSourceOdmItem = new MappedOdmItem();
                                                        mappedSourceOdmItem.setStudyEventOid(sourceEvent.getOid());
                                                        mappedSourceOdmItem.setFormOid(sourceFormDefinition.getOid());
                                                        mappedSourceOdmItem.setItemGroupOid(sourceItemGroupDefinition.getOid());
                                                        mappedSourceOdmItem.setItemOid(sourceItemDefinition.getOid());

                                                        MappedOdmItem mappedTargetOdmItem = new MappedOdmItem();
                                                        mappedTargetOdmItem.setStudyEventOid(targetEvent.getOid());
                                                        mappedTargetOdmItem.setFormOid(targetFormDefinition.getOid());
                                                        mappedTargetOdmItem.setItemGroupOid(targetItemGroupDefinition.getOid());
                                                        mappedTargetOdmItem.setItemOid(targetItemDefinition.getOid());

                                                        MappingRecord mappingRecord = new MappingRecord();
                                                        mappingRecord.setSource(mappedSourceOdmItem);
                                                        mappingRecord.setTarget(mappedTargetOdmItem);
                                                        Map<String,String> mappingMap = new HashMap<>();
                                                        mappingMap.put(sourceEvent.getOid(), targetEvent.getOid());
                                                        mappingMap.put(sourceFormDefinition.getOid(), targetEvent.getOid());
                                                        mappingMap.put(sourceItemGroupDefinition.getOid(), targetItemGroupDefinition.getOid());
                                                        mappingMap.put(sourceItemDefinition.getOid(), targetItemDefinition.getOid());
                                                        mappingRecord.setMapping(mappingMap);

                                                        mappingRecordList.add(mappingRecord);
                                                        mapping.addMappingRecord(mappingRecord);
                                                        mapping.getSourceItemDefinitions().add(mappedSourceOdmItem);
                                                        mapping.getTargetItemDefinitions().add(mappedTargetOdmItem);
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }

            System.out.println(mappingRecordList);

        }
        for(Mapping mapping: allMappings){
            this.repository.merge(mapping);
        }

        this.mappingAssistant.getMappedItemCount();
        //TODO
    }



/*
    public void mapItems() {
        OdmMappingAssistantFirstVersion assistant = new OdmMappingAssistantFirstVersion(this.sourceItemList, this.targetItemList);
        HashMap<String, String> mappingMap = new HashMap<>();
        mappingMap.put("SE_HNPLABORATORY", "SE_HNP2BLUTABNAHMEANDLAB");
        mappingMap.put("F_HNPRB_ADJUVANTV1_4631", "F_HNPRB_ADJUVANTV1_1613");
        mappingMap.put("I_HNPRB_HEINGANGSNR_4778", "I_HNPRB_HEINGANGSNR");
        mappingMap.put("F_EOS_DEV10", "F_ENDOFSTUDY_DEV11");
        mappingMap.put("I_HNPRD_PRETRCTDCM", "I_HNPRD_STUID1DCM");

        assistant.mapItems(mappingMap);

        this.perfectMatchingRecordList = assistant.getPerfectMatches();
        this.manualMappingRecordList = assistant.getMatchingRecords();
        this.notMatchingSourceRecordList = assistant.getNotMatchingRecordList();
    }
*/


    private List<MappedOdmItem> extractMappedDataItemDefinitionsFromOdm(Odm metadata) {
        List<MappedOdmItem> result = new ArrayList<>();

        for (EventDefinition ed : metadata.getStudies().get(0).getMetaDataVersion().getStudyEventDefinitions()) {

            if (ed.getFormDefs() != null) {
                for (FormDefinition fd : ed.getFormDefs()) {

                    if (fd.getItemGroupDefs() != null) {
                        for (ItemGroupDefinition igd : fd.getItemGroupDefs()) {

                            if (igd.getItemDefs() != null) {
                                for (ItemDefinition id : igd.getItemDefs()) {
                                    MappedOdmItem moi = new MappedOdmItem(ed, fd, igd, id);
                                    result.add(moi);
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private List<EventDefinition> createMappedEventDefinitionList(Odm metadata) {
        List<EventDefinition> result = new ArrayList<>();

        for (EventDefinition ed : metadata.getStudies().get(0).getMetaDataVersion().getStudyEventDefinitions()) {
            result.add(ed);
        }

        return result;
    }

    private void createTree(Odm metadata, TreeNode rootNode) {

        for (EventDefinition ed : metadata.getStudies().get(0).getMetaDataVersion().getStudyEventDefinitions()) {
            TreeNode eventTreeNode = new DefaultTreeNode(ed.getOid() + " - " + ed.getName() + " - " + ed.getDescription(), rootNode);
//            TreeNode eventTreeNode = new DefaultTreeNode(ed.getOid(), null);

            if (ed.getFormDefs() != null) {
                for (FormDefinition fd : ed.getFormDefs()) {
                    TreeNode formDefinitionTreeNode = new DefaultTreeNode(fd.getOid() + " - " + fd.getName(), eventTreeNode);

                    if (fd.getItemGroupDefs() != null) {
                        for (ItemGroupDefinition igd : fd.getItemGroupDefs()) {
                            TreeNode itemGroupTreeNode = new DefaultTreeNode(igd.getOid() + " - " + igd.getName() + " - " + igd.getComment(), formDefinitionTreeNode);

                            if (igd.getItemDefs() != null) {
                                for (ItemDefinition id : igd.getItemDefs()) {
                                    TreeNode itemTreeNode = new DefaultTreeNode(id.getOid() + " - " + id.getName() + " - " + igd.getComment(), itemGroupTreeNode);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String onFlowProcess(FlowEvent event) {
        return event.getNewStep();

    }

    public String getStepIndex() {
        if (sourceOdm != null) {
            return "selectImportsTab";
        }
        return "fileUploadTab";
    }

    /**
     * Get Repository
     *
     * @return repository
     */
    @Override
    protected IMappingRepository getRepository() {
            return this.repository;
    }

    /**
     * Prepare new entity
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        return null;
    }
}
