/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.portal.web.mb.admin;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ListItem;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.mapping.*;
import de.dktk.dd.rpb.core.repository.ctms.IStudyRepository;
import de.dktk.dd.rpb.core.repository.edc.IMappingRepository;
import de.dktk.dd.rpb.core.service.DataTransformationService;

import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.primefaces.component.api.UIColumn;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bean for administration of data mappings
 *
 * @author tomas@skripcak.net
 * @since 10 March 2015
 */
@Named("mbDataMapping")
@Scope(value="view")
public class DataMappingBean extends CrudEntityViewModel<Mapping, Integer> {

    //region Injects

    //region Main bean

    @Inject
    private MainBean mainBean;

    /**
     * Set MainBean
     *
     * @param bean MainBean
     */
    @SuppressWarnings("unused")
    public void setMainBean(MainBean bean) {
        this.mainBean = bean;
    }

    //endregion

    //region Study integration facade

    @Inject
    private StudyIntegrationFacade studyIntegrationFacade;

    @SuppressWarnings("unused")
    public void setStudyIntegrationFacade(StudyIntegrationFacade value) {
        this.studyIntegrationFacade = value;
    }

    //endregion

    //region Study repository

    @Inject
    private IStudyRepository studyRepository;

    /**
     * Set StudyRepository
     *
     * @param value - StudyRepository
     */
    @SuppressWarnings("unused")
    public void setStudyRepository(IStudyRepository value) {
        this.studyRepository = value;
    }

    //endregion

    //region Repository

    @Inject
    private IMappingRepository repository;

    public IMappingRepository getRepository() {
        return this.repository;
    }

    /**
     * Set Mapping repository
     * @param repository - MappingRepository
     */
    @SuppressWarnings("unused")
    public void setRepository(IMappingRepository repository) {
        this.repository = repository;
    }

    //endregion

    //region DataTransformation

    @Inject
    protected DataTransformationService svcDataTransformation;

    //endregion

    //endregion

    //region Members

    private Study study;
    private Odm metadataOdm;
    private UploadedFile uploadedFile;


    // region Source Item Definition

    private List<AbstractMappedItem> filteredSourceItemDefs;
    private AbstractMappedItem selectedSourceItemDef;
    private AbstractMappedItem newSourceItemDef;

    //endregion

    //region Target Item Definition

    private List<AbstractMappedItem> filteredTargetItemDefs;
    private AbstractMappedItem selectedTargetItemDef;
    private AbstractMappedItem newTargetItemDef;

    //endregion

    //region Mapping Record

    private List<MappingRecord> filteredRecords;
    private MappingRecord selectedRecord;
    private MappingRecord newRecord;

    //endregion

    //region CodeMap

    private List<Map.Entry<String, String>> filteredCodes;
    private Map.Entry<String, String> selectedCodeMap;

    private String sourceCode;
    private String targetCode;
    private ItemDefinition targetItemDef;

    private ListItem selectedOdmTargetCode;

    //endregion

    //endregion

    //region Properties

    //region Uploaded File

    public UploadedFile getUploadedFile() {
        return this.uploadedFile;
    }

    public void setUploadedFile(UploadedFile file) {
        this.uploadedFile = file;
    }

    //endregion

    //region SourceItemDefinitions DataTable

    //region FilteredSourceItemDefs

    public List<AbstractMappedItem> getFilteredSourceItemDefs() {
        return this.filteredSourceItemDefs;
    }

    public void setFilteredSourceItemDefs(List<AbstractMappedItem> filteredSourceItemDefs) {
        this.filteredSourceItemDefs = filteredSourceItemDefs;
    }

    //endregion

    //region SelectedSourceItemDef

    public AbstractMappedItem getSelectedSourceItemDef() {
        return this.selectedSourceItemDef;
    }

    public void setSelectedSourceItemDef(AbstractMappedItem item) {
        this.selectedSourceItemDef = item;
    }

    //endregion

    //region NewSourceItemDefinition

    public AbstractMappedItem getNewSourceItemDef() {
        return this.newSourceItemDef;
    }

    public void setNewSourceItemDef(AbstractMappedItem itemDef) {
        this.newSourceItemDef = itemDef;
    }

    //endregion

    //endregion

    //region TargetItemDefinitions DataTable

    //region FilteredTargetItemDefs

    public List<AbstractMappedItem> getFilteredTargetItemDefs() {
        return this.filteredTargetItemDefs;
    }

    public void setFilteredTargetItemDefs(List<AbstractMappedItem> filteredTargetItemDefs) {
        this.filteredTargetItemDefs = filteredTargetItemDefs;
    }

    //endregion

    //region SelectedTargetItemDef

    public AbstractMappedItem getSelectedTargetItemDef() {
        return this.selectedTargetItemDef;
    }

    public void setSelectedTargetItemDef(AbstractMappedItem item) {
        this.selectedTargetItemDef = item;
    }

    //endregion

    //region NewTargetItemDefinition

    public AbstractMappedItem getNewTargetItemDef() {
        return this.newSourceItemDef;
    }

    public void setNewTargetItemDef(AbstractMappedItem itemDef) {
        this.newTargetItemDef = itemDef;
    }

    //endregion

    //endregion

    //region Record DataTable

    //region FilteredRecords Property

    /**
     * Get Filtered Entities List
     * @return List - Entities List
     */
    @SuppressWarnings("unused")
    public List<MappingRecord> getFilteredRecords() {
        return this.filteredRecords;
    }

    /**
     * Set Filtered Entities List
     * @param list - Entities List
     */
    @SuppressWarnings("unused")
    public void setFilteredRecords(List<MappingRecord> list) {
        this.filteredRecords = list;
    }

    //endregion

    //region SelectedRecord

    public MappingRecord getSelectedRecord() {
        return this.selectedRecord;
    }

    public void setSelectedRecord(MappingRecord rec) {
        this.selectedRecord = rec;
        this.selectedTargetChanged();
    }

    public String getSelectedRecordTargetSeRepeatKey() {
        if (this.selectedRecord.getTarget() != null) {
            return ((MappedOdmItem) this.selectedRecord.getTarget()).getStudyEventRepeatKey();
        }

        return null;
    }

    public void setSelectedRecordTargetSeRepeatKey(String key) {
        if (this.selectedRecord.getTarget() != null) {
            ((MappedOdmItem) this.selectedRecord.getTarget()).setStudyEventRepeatKey(key);
        }
    }

    public String getSelectedRecordTargetIgRepeatKey() {
        if (this.selectedRecord.getTarget() != null) {
            return ((MappedOdmItem) this.selectedRecord.getTarget()).getItemGroupRepeatKey();
        }

        return null;
    }

    public void setSelectedRecordTargetIgRepeatKey(String key) {
        if (this.selectedRecord.getTarget() != null) {
            ((MappedOdmItem) this.selectedRecord.getTarget()).setItemGroupRepeatKey(key);
        }
    }

    //endregion

    //region NewRecord

    public MappingRecord getNewRecord() {
        return this.newRecord;
    }

    public void setNewRecord(MappingRecord rec) {
        this.newRecord = rec;
    }

    public String getNewRecordTargetSeRepeatKey() {
        if (this.newRecord.getTarget() != null) {
            return ((MappedOdmItem) this.newRecord.getTarget()).getStudyEventRepeatKey();
        }

        return null;
    }

    public void setNewRecordTargetSeRepeatKey(String key) {
        if (this.newRecord.getTarget() != null) {
            ((MappedOdmItem) this.newRecord.getTarget()).setStudyEventRepeatKey(key);
        }
    }

    public String getNewRecordTargetIgRepeatKey() {
        if (this.newRecord.getTarget() != null) {
            return ((MappedOdmItem) this.newRecord.getTarget()).getItemGroupRepeatKey();
        }

        return null;
    }

    public void setNewRecordTargetIgRepeatKey(String key) {
        if (this.newRecord.getTarget() != null) {
            ((MappedOdmItem) this.newRecord.getTarget()).setItemGroupRepeatKey(key);
        }
    }

    //endregion

    //endregion

    //region CodeMap DataTable

    //region FilteredCodes Property

    public List<Map.Entry<String, String>> getFilteredCodes() {
        return this.filteredCodes;
    }

    public void setFilteredCodes(List<Map.Entry<String, String>> codeMapList) {
        this.filteredCodes = codeMapList;
    }

    //endregion

    //region SelectedCodeMap

    public Map.Entry<String, String> getSelectedCodeMap() {
        return this.selectedCodeMap;
    }

    public void setSelectedCodeMap(Map.Entry<String, String> codeMap) {
        this.selectedCodeMap = codeMap;
    }

    //endregion

    //endregion

    //region Enums

    public MappingTypeEnum[] getMappingTypeValues() {
        return MappingTypeEnum.values();
    }

    public MappingItemTypeEnum[] getMappingItemTypeValues() {
        return MappingItemTypeEnum.values();
    }

    //endregion

    //region SourceCode

    public String getSourceCode() {
        return this.sourceCode;
    }

    public void setSourceCode(String value) {
        this.sourceCode = value;
    }

    //endregion

    //region TargetCode

    public ListItem getSelectedOdmTargetCode() {
        return this.selectedOdmTargetCode;
    }

    public void setSelectedOdmTargetCode(ListItem li) {
        this.selectedOdmTargetCode = li;
    }

    public String getTargetCode() {
        return this.targetCode;
    }

    public void setTargetCode(String value) {
        this.targetCode = value;
    }

    //endregion

    //region TargetItemDefinition

    public ItemDefinition getTargetItemDef() {
        return this.targetItemDef;
    }

    //endregion

    //region TargetIsCoded

    /**
     * Target item is coded also when it is SS_GENDER (that is not the item of eCRF but property of study subject)
     * @return True if item is codified
     */
    public Boolean getTargetIsCoded() {

        MappingRecord mr = null;

        if (this.newRecord != null) {
            mr = this.newRecord;
        }
        else if (this.selectedRecord != null) {
            mr = this.selectedRecord;
        }

        if (mr != null && this.targetItemDef != null) {
            return this.targetItemDef.isCoded() || mr.getTarget().getLabel().equals("SS_GENDER");
        }
        else if (mr != null) {
            if (mr.getTarget() != null) {
                return mr.getTarget().getLabel().equals("SS_GENDER");
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    //endregion

    // region TargetIsDate

    /**
     * Target is date also when it is SS_DATEOFBIRTH (that is not the item of eCRF but property of study subject)
     * @return True if item is date
     */
    public Boolean getTargetIsDate() {

        MappingRecord mr = null;

        if (this.newRecord != null) {
            mr = this.newRecord;
        }
        else if (this.selectedRecord != null) {
            mr = this.selectedRecord;
        }

        if (mr != null && this.targetItemDef != null) {
            return this.targetItemDef.isDate() || mr.getTarget().getLabel().equals("SS_DATEOFBIRTH");
        }
        else if (mr != null) {
            if (mr.getTarget() != null) {
                return mr.getTarget().getLabel().equals("SS_DATEOFBIRTH");
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        this.load();
    }

    //endregion

    //region Commands

    public void selectedTargetChanged() {
        try {
            if (this.newRecord != null) {
                this.targetItemDef = this.metadataOdm.getItemDefinition(
                        (MappedOdmItem) this.newRecord.getTarget()
                );
            }
            else if (this.selectedRecord != null) {
                this.targetItemDef = this.metadataOdm.getItemDefinition(
                        (MappedOdmItem) this.selectedRecord.getTarget()
                );
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //region Mapping entity

    /**
     * Load entity list and store into ViewModel for binding
     */
    public void load() {
        try {
            // Reload study metadata
            this.studyIntegrationFacade.init(this.mainBean);
            this.study = this.studyIntegrationFacade.loadStudy();
            this.metadataOdm = this.studyIntegrationFacade.getMetadataOdm();

            // Load fresh list of entities
            this.entityList = this.studyIntegrationFacade.loadStudy().getDataMappings();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Load source and target item definitions for new mapping entity
     */
    public void loadMappedItems() {
        try {
            // Extract source item definitions from provided file in case of IMPORT mapping
            // target is CDISC ODM of active study
            if (this.newEntity.getType().equals(MappingTypeEnum.IMPORT)) {

                this.newEntity.setSourceItemDefinitions(
                        this.svcDataTransformation.extractMappedDataItemDefinitions(
                                this.uploadedFile.getInputstream(),
                                this.uploadedFile.getFileName()
                        )
                );

                // Load target item definitions from active study
                this.newEntity.setTargetItemDefinitions(
                        this.svcDataTransformation.extractMappedDataItemDefinitions(
                                this.metadataOdm
                        )
                );

                // Additionally to items defined in study add Subject related elements necessary for subject creation
                MappedOdmItem personId = new MappedOdmItem();
                personId.setItemOid("SS_PERSONID");
                MappedOdmItem studySubjectId = new MappedOdmItem();
                studySubjectId.setItemOid("SS_STUDYSUBJECTID");
                MappedOdmItem secondaryId = new MappedOdmItem();
                secondaryId.setItemOid("SS_SECONDARYID");
                MappedOdmItem gender = new MappedOdmItem();
                gender.setItemOid("SS_GENDER");
                MappedOdmItem dateOfBirth = new MappedOdmItem();
                dateOfBirth.setItemOid("SS_DATEOFBIRTH");
                MappedOdmItem yearOfBirth = new MappedOdmItem();
                yearOfBirth.setItemOid("SS_YEAROFBIRTH");

                MappedOdmItem firstName = new MappedOdmItem();
                firstName.setItemOid("SS_FIRSTNAME");
                MappedOdmItem lastName = new MappedOdmItem();
                lastName.setItemOid("SS_LASTNAME");
                MappedOdmItem birthName = new MappedOdmItem();
                birthName.setItemOid("SS_BIRTHNAME");
                MappedOdmItem city = new MappedOdmItem();
                city.setItemOid("SS_CITY");
                MappedOdmItem zip = new MappedOdmItem();
                zip.setItemOid("SS_ZIP");

                this.newEntity.getTargetItemDefinitions().add(personId);
                this.newEntity.getTargetItemDefinitions().add(studySubjectId);
                this.newEntity.getTargetItemDefinitions().add(secondaryId);
                this.newEntity.getTargetItemDefinitions().add(gender);
                this.newEntity.getTargetItemDefinitions().add(dateOfBirth);
                this.newEntity.getTargetItemDefinitions().add(yearOfBirth);

                this.newEntity.getTargetItemDefinitions().add(firstName);
                this.newEntity.getTargetItemDefinitions().add(lastName);
                this.newEntity.getTargetItemDefinitions().add(birthName);
                this.newEntity.getTargetItemDefinitions().add(city);
                this.newEntity.getTargetItemDefinitions().add(zip);
            }
            // Extract target item definition from provided file in case of EXPORT mapping
            // source is CDISC ODM of active study
            else if (this.newEntity.getType().equals(MappingTypeEnum.EXPORT)) {
                throw new Exception("Not implemented.");
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Type of mapping (IMPORT, EXPORT) changed, setup automaticaly the source and target types
     */
    public void selectedMappingTypeChanged() {
        try {
            if (this.newEntity.getType() == MappingTypeEnum.IMPORT) {
                this.newEntity.setTargetType(MappingItemTypeEnum.ODM);
            }
            else if (this.newEntity.getType() == MappingTypeEnum.EXPORT) {
                this.newEntity.setSourceType(MappingItemTypeEnum.ODM);
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Insert a new entity into repository
     */
    public void doCreateEntity() {
        try {
            // Persist
            this.study.addDataMapping(this.newEntity);
            this.studyRepository.merge(this.study);

            this.messageUtil.infoEntity("status_saved_ok", this.newEntity);

            this.newEntity = null;

            // Reload
            this.load();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Clone the selected entity and insert the clone into repository TODO: not finished yet
     */
    public void doCloneEntity() {
        try {
            // Get persistent copy
            this.selectedEntity = this.repository.merge(
                    this.selectedEntity
            );

            // Clone mapping entity
            this.newEntity = new Mapping(this.selectedEntity);

            // Persiste the cloned new transient entity
            this.repository.save(this.newEntity);

            this.messageUtil.info("status_saved_ok", this.newEntity);

            this.newEntity = null;

            // Reload
            this.load();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Delete selected entity from repository
     */
    public void doDeleteEntity() {
        try {
            // Delete
            this.study.removeDataMapping(this.selectedEntity);
            this.repository.delete(this.selectedEntity);

            this.messageUtil.infoEntity("status_deleted_ok", this.selectedEntity);
            this.selectedEntity = null;

            // Reload
            this.load();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Source Item Definition entity

    public void prepareNewSourceItemDefition() {
        this.selectedSourceItemDef = null;

        if (this.selectedEntity.getType() == MappingTypeEnum.IMPORT) {
            if (this.selectedEntity.getSourceType() == MappingItemTypeEnum.CSV) {
                this.newSourceItemDef = new MappedCsvItem();
            }
        }
    }

    public void cancelSourceItemDefinition() {
        try {
            // Saving new source item definition
            if (this.newSourceItemDef != null) {
                this.newSourceItemDef = null;
            }
            // Edition the existing one
            else if (this.selectedSourceItemDef != null) {

            }

            this.messageUtil.infoText("Source item definition changes not saved.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void saveSourceItemDefinition() {
        try {
            // Saving new source item definition
            if (this.newSourceItemDef != null) {
                AbstractMappedItem nai = new MappedCsvItem(this.newSourceItemDef.getLabel());
                this.selectedEntity.getSourceItemDefinitions().add(
                        nai
                );

                this.newSourceItemDef = null;
            }
            // Edition the existing one
            else if (this.selectedSourceItemDef != null) {
                for (AbstractMappedItem ami : this.selectedEntity.getSourceItemDefinitions()) {
                    if (ami.getLabel().equals(this.selectedSourceItemDef.getLabel())) {
                        ((MappedCsvItem)ami).setHeader(this.selectedSourceItemDef.getLabel());
                    }
                }
            }

            this.messageUtil.infoText("Source item definition saved.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void editSourceItemDefinition() {
        try {

        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void removeSourceItemDefinition() {
        try {
            this.selectedEntity.getSourceItemDefinitions().remove(
                    this.selectedSourceItemDef
            );
            this.selectedSourceItemDef = null;
            this.messageUtil.infoText("Selected source item definition removed.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Mapping Record

    public void prepareNewRecord() {
        this.newRecord = new MappingRecord();
    }

    /**
     * Insert a new entity record into repository
     */
    public void doCreateRecordEntity() {
        try {
            // Get persistent entity
            this.selectedEntity = this.repository.getById(
                    this.selectedEntity.getId()
            );

            this.selectedEntity.addMappingRecord(
                    this.newRecord
            );

            this.selectedEntity = this.repository.merge(
                    this.selectedEntity
            );

            this.messageUtil.infoEntity("status_saved_ok", this.newRecord);

            // Clean up
            this.newRecord = null;
            this.selectedRecord = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Update selected entity record in repository
     */
    public void doUpdateRecordEntity() {
        try {
            // Reattach detached selected entity and persist changes
            this.selectedEntity = this.repository.merge(
                    this.selectedEntity
            );
            this.messageUtil.infoEntity("status_saved_ok", this.selectedRecord);

            // Clean up
            this.selectedRecord = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Delete selected entity record from repository
     */
    public void doDeleteRecordEntity() {
        try {
            // Remove mapping record from mapping
            this.selectedEntity.removeMappingRecord(
                    this.selectedRecord
            );

            // Reattach detached selected entity and persist changes
            this.selectedEntity = this.repository.merge(
                    this.selectedEntity
            );
            this.messageUtil.infoEntity("status_deleted_ok", this.selectedRecord);

            // Clean up
            this.selectedRecord = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region CodeMap

    /**
     * Use all target codes as source codes for mapping (they are the same no recoding necessary)
     */
    public void addAllCodes() {
        try {
            for (ListItem li : this.targetItemDef.getListItems()) {

                // Handles new and existing records and add the only missing coding
                if (this.newRecord != null) {
                    if (!this.newRecord.recodeForTargetExists(li.getCodedValue())) {

                        this.newRecord.addCodeMapping(
                                li.getCodedValue(),
                                li.getCodedValue()
                        );
                    }
                }
                else if (this.selectedRecord != null) {
                    if (!this.selectedRecord.recodeForTargetExists(li.getCodedValue())) {
                        this.selectedRecord.addCodeMapping(
                                li.getCodedValue(),
                                li.getCodedValue()
                        );
                    }
                }
            }

            // Clean up
            this.sourceCode = null;
            this.targetCode = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Add new code mapping to selected detached mapping record entity
     */
    public void addCodeMapping() {
        try {
            // Handles new and existing record, do not verify the existence of target code (multiple source codes can refer to one target)
            if (this.newRecord != null) {
                this.newRecord.addCodeMapping(
                        this.sourceCode,
                        this.targetCode
                );
            }
            else if (this.selectedRecord != null) {
                this.selectedRecord.addCodeMapping(
                        this.sourceCode,
                        this.selectedOdmTargetCode.getCodedValue()
                );
            }

            // Clean up
            this.sourceCode = null;
            this.targetCode = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Remove selected code mapping from selected detached mapping record entity
     */
    public void removeCodeMapping() {
        try {
            // Remove
            if (this.newRecord != null) {
                this.newRecord.removeCodeMapping(
                        this.selectedCodeMap.getKey()
                );
            }
            else if (this.selectedRecord != null) {
                this.selectedRecord.removeCodeMapping(
                        this.selectedCodeMap.getKey()
                );
            }

            // Message
            this.messageUtil.info("status_removed_ok", this.selectedCodeMap);

            // Clean up
            this.selectedCodeMap = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //endregion

    //region Event Handlers

    /**
     * Upload input data for import
     * @param event upload event
     */
    public void handleUpload(FileUploadEvent event) {
        try {
            this.uploadedFile = event.getFile();

            this.messageUtil.infoText("File name: " +
                    event.getFile().getFileName() + " file size: " +
                    event.getFile().getSize() / 1024 + " Kb content type: " +
                    event.getFile().getContentType() + " The document file was uploaded.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
    }

    /*
    * Need to build an initial sort order for data table multi sort
    */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtEntities:colMappingDefName");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colMappingDefName");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    //endregion

}
