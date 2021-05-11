/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ListItem;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappedOdmItem;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappingRecord;
import de.dktk.dd.rpb.core.repository.edc.IMappingRecordRepository;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bean for administration of MappingRecords
 *
 * @author tomas@skripcak.net
 * @since 01 July 2015
 */
@Named("mbMappingRecord")
@Scope(value="view")
public class MappingRecordBean extends CrudEntityViewModel<MappingRecord, Integer> {

    //region Injects

    //region Repository

    @Inject
    private IMappingRecordRepository repository;

    /**
     * Get StructTypeRepository
     * @return StructTypeRepository
     */
    @Override
    public IMappingRecordRepository getRepository() {
        return this.repository;
    }

    //endregion

    //endregion

    //region Members

    private List<Map.Entry<String, String>> filteredCodes;
    private Map.Entry<String, String> selectedCodeMap;

    private String sourceCode;
    private String targetCode;

    private ListItem selectedOdmTargetCode;

    //endregion

    //region Properties

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

    public ItemDefinition getTargetItemDef(Odm metadataOdm, MappingRecord mappingRecord) {
        if (metadataOdm != null && mappingRecord != null) {
            return metadataOdm.getItemDefinition(
                    (MappedOdmItem) mappingRecord.getTarget()
            );
        }
        else {
            return null;
        }
    }

    //endregion

    //region TargetIsCoded

    /**
     * Target item is coded also when it is SS_GENDER (that is not the item of eCRF but property of study subject)
     * @return True if item is codified
     */
    public Boolean getTargetIsCoded(Odm metadataOdm, MappingRecord mappingRecord) {

        ItemDefinition id = this.getTargetItemDef(metadataOdm, mappingRecord);
        if (mappingRecord != null && id != null) {
            return id.isCoded() || mappingRecord.getTarget().getLabel().equals(Constants.SS_GENDER);
        }
        else if (mappingRecord != null) {
            if (mappingRecord.getTarget() != null) {
                return mappingRecord.getTarget().getLabel().equals(Constants.SS_GENDER);
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

    //region TargetIsDate

    /**
     * Target is date also when it is SS_DATEOFBIRTH (that is not the item of eCRF but property of study subject)
     * or STARTDATE of event occurrence
     * @return True if item is date
     */
    public Boolean getTargetIsDate(Odm metadataOdm, MappingRecord mappingRecord) {

        ItemDefinition id = this.getTargetItemDef(metadataOdm, mappingRecord);
        if (mappingRecord != null && id != null) {
            return (id.isDate() ||
                    mappingRecord.getTarget().getLabel().equals(Constants.SS_DATEOFBIRTH) ||
                    mappingRecord.getTarget().getLabel().startsWith(Constants.SE_STARTDATE + "_SE"));
        }
        else if (mappingRecord != null) {
            if (mappingRecord.getTarget() != null) {
                return (mappingRecord.getTarget().getLabel().equals(Constants.SS_DATEOFBIRTH) ||
                        mappingRecord.getTarget().getLabel().startsWith(Constants.SE_STARTDATE + "_SE"));
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

    //region TargetIsMultiSelect

    /**
     * Target is multi select
     * @return True if item is multi select
     */
    public Boolean getTargetIsMultiSelect(Odm metadataOdm, MappingRecord mappingRecord) {
        ItemDefinition id = this.getTargetItemDef(metadataOdm, mappingRecord);
        if (id != null) {
            return id.isMultiCoded();
        }
        else {
            return Boolean.FALSE;
        }
    }

    //endregion

    //region TargetIsNumber

    public Boolean getTargetIsNumber(Odm metadataOdm, MappingRecord mappingRecord) {
        ItemDefinition id = this.getTargetItemDef(metadataOdm, mappingRecord);
        if (id != null) {
            return id.isNumber();
        }
        else {
            return Boolean.FALSE;
        }
    }

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

    /**
     * Use all target codes as source codes for mapping (they are the same no recoding necessary)
     */
    public void addAllCodes(Odm metadataOdm, MappingRecord mappingRecord) {
        try {
            ItemDefinition id = this.getTargetItemDef(metadataOdm, mappingRecord);
            if (id != null) {
                for (ListItem li : id.getListItems()) {

                    // Handles new and existing records and add the only missing coding
                    if (this.newEntity != null) {
                        if (!this.newEntity.recodeForTargetExists(li.getCodedValue())) {

                            this.newEntity.addCodeMapping(
                                    li.getCodedValue(),
                                    li.getCodedValue()
                            );
                        }
                    } else if (this.selectedEntity != null) {
                        if (!this.selectedEntity.recodeForTargetExists(li.getCodedValue())) {
                            this.selectedEntity.addCodeMapping(
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
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Add new code mapping to selected detached mapping record entity
     */
    public void addCodeMapping(MappingRecord mappingRecord) {
        try {
            // Add
            mappingRecord.addCodeMapping(this.sourceCode, this.targetCode);

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
    public void removeCodeMapping(MappingRecord mappingRecord) {
        try {
            // Remove
            mappingRecord.removeCodeMapping(this.selectedCodeMap.getKey());

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

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();

        // Reset the coding part for fresh UI dialog
        if (this.filteredCodes != null) {
            this.filteredCodes.clear();
        }
        this.selectedCodeMap = null;
        this.selectedOdmTargetCode = null;
    }

    /**
     * Need to build an initial sort order for data table multi sort
     * @return List of SortMeta objects
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtRecords:colSourceLabel", "colSourceLabel", SortOrder.ASCENDING);
        if (results != null) {

            List<SortMeta> nextResults = DataTableUtil.buildSortOrder(":form:tabView:dtRecords:colTargetLabel", "colTargetLabel", SortOrder.ASCENDING);
            if (nextResults != null) {

                results.addAll(nextResults);

                List<SortMeta> nextNextResults = DataTableUtil.buildSortOrder(":form:tabView:dtRecords:colRecordPriority", "colRecordPriority", SortOrder.ASCENDING);
                if (nextNextResults != null) {

                    results.addAll(nextNextResults);
                }
            }
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

        result.add(Boolean.TRUE); // Source
        result.add(Boolean.TRUE); // Target
        result.add(Boolean.TRUE); // Priority
        result.add(Boolean.FALSE); // DefaultValue
        result.add(Boolean.FALSE); // MultiValueSeparator
        result.add(Boolean.FALSE); // DateFormatString
        result.add(Boolean.FALSE); // StudyEventRepeatKey
        result.add(Boolean.FALSE); // ItemGroupRepeatKey

        return result;
    }

    //endregion

}
