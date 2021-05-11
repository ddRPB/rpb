/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.edc.mapping.*;
import de.dktk.dd.rpb.core.repository.edc.IMappingRepository;
import de.dktk.dd.rpb.core.service.DataTransformationService;

import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

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

    //region Repository

    @Inject
    private IMappingRepository repository;

    public IMappingRepository getRepository() {
        return this.repository;
    }

    //endregion

    //region DataTransformation

    @Inject
    protected DataTransformationService svcDataTransformation;

    //endregion

    //endregion

    //region Members

    //region File

    private UploadedFile uploadedFile;

    //endregion

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
        return this.newTargetItemDef;
    }

    public void setNewTargetItemDef(AbstractMappedItem itemDef) {
        this.newTargetItemDef = itemDef;
    }

    //endregion

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        this.load();
    }

    //endregion

    //region Commands

    //region Mapping

    /**
     * Load source item definitions for new mapping entity
     */
    public void loadSourceMappedItems() {
        try {
            // Extract source item definitions from provided file in case of IMPORT mapping
            if (this.newEntity.getType().equals(MappingTypeEnum.IMPORT)) {

                this.newEntity.setSourceItemDefinitions(
                        this.svcDataTransformation.extractMappedDataItemDefinitions(
                                this.uploadedFile.getInputstream(),
                                this.uploadedFile.getFileName()
                        )
                );
            }
            else if (this.newEntity.getType().equals(MappingTypeEnum.EXPORT)) {
                throw new Exception("Not implemented.");
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Load target item definitions for new mapping entity
     */
    public void loadTargetMappedItems(EventDefinition event, FormDefinition crf) {
        try {
            // Extract target item definitions from active study in case of IMPORT mapping
            if (this.newEntity.getType().equals(MappingTypeEnum.IMPORT)) {

                if (crf != null) {

                    // Collect item definitions from CRF
                    List<AbstractMappedItem> itemList = new ArrayList<>();
                    for (ItemGroupDefinition igd : crf.getItemGroupDefs()) {
                        for (ItemDefinition id : igd.getItemDefs()) {
                            MappedOdmItem moi = new MappedOdmItem(event, crf, igd, id);
                            itemList.add(moi);
                        }
                    }

                    // Initiate the list
                    if (this.newEntity.getTargetItemDefinitions() == null) {
                        this.newEntity.setTargetItemDefinitions(
                                new ArrayList<AbstractMappedItem>()
                        );
                    }

                    // Additionally to items defined in study
                    if (this.newEntity.getTargetItemDefinitions().size() == 0) {
                        // Add Subject related elements necessary for subject creation
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_PERSONID));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_STUDYSUBJECTID));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_SECONDARYID));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_GENDER));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_DATEOFBIRTH));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_YEAROFBIRTH));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_FIRSTNAME));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_LASTNAME));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_BIRTHNAME));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_CITY));
                        this.newEntity.getTargetItemDefinitions().add(new MappedOdmItem(Constants.SS_ZIP));

                        // Add Event related elements necessary for even scheduling
                        this.newEntity.addTargetEventOccurrence(event);
                    }

                    // Add items from crf
                    for (AbstractMappedItem mi : itemList) {
                        this.newEntity.getTargetItemDefinitions().add(mi);
                    }
                }
                else if (this.newEntity.getType().equals(MappingTypeEnum.EXPORT)) {
                    throw new Exception("Not implemented.");
                }
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

            // Persist the cloned new transient entity
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

    //endregion

    //region SourceItemDefinition

    public void prepareNewSourceItemDefinition(Mapping mapping) {
        // Reset selected so that the toolbar commands are updated properly
        this.selectedSourceItemDef = null;

        if (mapping.getType() == MappingTypeEnum.IMPORT) {
            if (mapping.getSourceType() == MappingItemTypeEnum.CSV) {
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
                this.selectedSourceItemDef = null;
            }

            this.messageUtil.infoText("Source item definition changes not saved.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void saveSourceItemDefinition(Mapping mapping) {
        try {
            // Saving new source item definition
            if (this.newSourceItemDef != null) {
                AbstractMappedItem nai = new MappedCsvItem(this.newSourceItemDef.getLabel());
                mapping.getSourceItemDefinitions().add(nai);

                this.messageUtil.infoText("New source item definition saved.");

                this.newSourceItemDef = null;
            }
            // Edition the existing one
            else if (this.selectedSourceItemDef != null) {
                for (AbstractMappedItem ami : mapping.getSourceItemDefinitions()) {
                    if (ami.getLabel().equals(this.selectedSourceItemDef.getLabel())) {
                        ((MappedCsvItem)ami).setHeader(this.selectedSourceItemDef.getLabel());
                    }
                }

                this.messageUtil.infoText("Existing source item definition saved.");

                this.selectedSourceItemDef = null;
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void removeSourceItemDefinition(Mapping mapping) {
        try {
            mapping.getSourceItemDefinitions().remove(
                    this.selectedSourceItemDef
            );

            this.messageUtil.infoText("Selected source item definition removed.");

            this.selectedSourceItemDef = null;
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region TargetItemDefinitions

    //endregion

    //region MappingRecord

    /**
     * Insert a new entity record into repository
     */
    public void doCreateRecordEntity(MappingRecord newRecord) {
        this.selectedEntity.addMappingRecord(newRecord);
        this.doUpdateEntity();
    }

    /**
     * Delete selected entity record from repository
     */
    public void doDeleteRecordEntity(MappingRecord selectedRecord) {
        this.selectedEntity.removeMappingRecord(selectedRecord);
        this.doUpdateEntity();
    }

    //endregion

    //endregion

    //region Event Handlers

    /**
     * Upload input data file for import
     * @param event upload event
     */
    public void handleUpload(FileUploadEvent event) {
        try {
            this.uploadedFile = event.getFile();
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

        this.uploadedFile = null;
        this.selectedSourceItemDef = null;
        this.newSourceItemDef = null;
        this.selectedTargetItemDef = null;
        this.newTargetItemDef = null;
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return List of SortMeta objects
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtEntities:colMappingDefName", "colMappingDefName", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<>();

        result.add(Boolean.TRUE); // Name
        result.add(Boolean.TRUE); // Type
        result.add(Boolean.TRUE); // From
        result.add(Boolean.TRUE); // To

        return result;
    }

    //endregion

}
