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

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.edc.DataQueryResult;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;

import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.ColumnModel;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.DualListModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean for building and execution of custom queries within RadPlanBio
 *
 * @author tomas@skripcak.net
 * @since 04 Jun 2013
 */
@Named("mbCustomQuery")
@Scope("view")
public class OCDataBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

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

    //region Messages

    @Inject
    @SuppressWarnings("unused")
    protected MessageUtil messageUtil;

    //endregion

    //endregion

    //region  Members

    private Boolean decodeItemValues;
    private Integer activeTabIndex;

    private DualListModel<ItemDefinition> queryDefinitions;
    private List<ItemDefinition> dataItemDefinitions;

    private List<ColumnModel> columns;
    private List<DataQueryResult> dataItems;
    private List<DataQueryResult> filteredDataItems;

    private List<SortMeta> subjectsPreSortOrder;

    //endregion

    //region Properties

    //region DecodeItemValues

    public Boolean getDecodeItemValues() {
        return this.decodeItemValues;
    }

    public void setDecodeItemValues(Boolean value) {
        this.decodeItemValues = value;
    }

    //endregion

    //region ActiveTabIndex

    public Integer getActiveTabIndex() {
        return this.activeTabIndex;
    }

    public void setActiveTabIndex(Integer value) {
        this.activeTabIndex = value;
    }

    //endregion

    //region PickList

    public DualListModel<ItemDefinition> getQueryDefinitions() {
        return  this.queryDefinitions;
    }

    public void setQueryDefinitions(DualListModel<ItemDefinition> model) {
        this.queryDefinitions = model;
    }

    //endregion

    //region DataItem Table

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public List<DataQueryResult> getDataItems() {
        return  this.dataItems;
    }

    public void setDataItems(List<DataQueryResult> list) {
        this.dataItems = list;
    }

    public List<DataQueryResult> getFilteredDataItems() {
        return this.filteredDataItems;
    }

    public void setFilteredDataItems(List<DataQueryResult> list) {
        this.filteredDataItems = list;
    }

    //endregion

    //region Sorting

    public List<SortMeta> getSubjectsPreSortOrder() {
        return this.subjectsPreSortOrder;
    }

    public void setSubjectsPreSortOrder(List<SortMeta> sortList) {
        this.subjectsPreSortOrder = sortList;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {

        // Init
        this.decodeItemValues = false;

        this.setSubjectsPreSortOrder(
                this.buildSubjectsSortOrder()
        );

        // Load initial data
        this.load();
    }

    //endregion

    //region Commands

    /**
     * Reload metadata
     */
    public void load() {
        try {
            // Reload the definitions
            this.studyIntegrationFacade.init(this.mainBean);
            this.dataItemDefinitions = this.studyIntegrationFacade.getAllFields();

            // Prepare model for PickList
            this.queryDefinitions = new DualListModel<ItemDefinition>(this.dataItemDefinitions, new ArrayList<ItemDefinition>());
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Load data according to query
     */
    public void executeQuery() {

        try {
            // Generate columns according the query
            this.createDynamicColumns();

            //TODO: query directly OC DB right now but later collect all data via REST
            this.dataItems = this.mainBean.getOpenClinicaDataRepository().getData(
                    this.mainBean.getActiveStudy().getUniqueIdentifier(),
                    this.queryDefinitions.getTarget(),
                    this.decodeItemValues
            );

            // Go to query results tab
            this.activeTabIndex = 1;
            this.messageUtil.infoText(this.dataItems.size() + " query results returned.");
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }

        // Later on...
        // possibility to rename extracted fields
        // possibility to run central as well as de-central query
        // possibility to include data elements from other subsystem into a query
    }

    /**
     * Reset query definition
     */
    public void resetQuery() {
        this.queryDefinitions = new DualListModel<ItemDefinition>(this.dataItemDefinitions, new ArrayList<ItemDefinition>());
    }

    //endregion

    //region Private methods

    /**
     * Prepare dynamically the column names
     */
    private void createDynamicColumns() {
        this.columns = new ArrayList<ColumnModel>();

        for(ItemDefinition item : this.queryDefinitions.getTarget()) {
            this.columns.add(new ColumnModel(item.getLabel(), item.getOid(), item.getName()));
        }
    }

    private List<SortMeta> buildSubjectsSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtDataItems:colStudySubjectId");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colStudySubjectId");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }


//    public void postProcessXLS(Object document) {
//        HSSFWorkbook wb = (HSSFWorkbook) document;
//        HSSFSheet sheet = wb.getSheetAt(0);
//        HSSFRow header = sheet.getRow(0);
//
//        HSSFCellStyle cellStyle = wb.createCellStyle();
//        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
//        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//
//        for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
//            HSSFCell cell = header.getCell(i);
//
//            cell.setCellStyle(cellStyle);
//        }
//    }

//    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
//        Document pdf = (Document) document;
//        pdf.open();
//        pdf.setPageSize(PageSize.A4);
//
//        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//        String logo = servletContext.getRealPath("") + File.separator + "resources" + File.separator + "demo" + File.separator + "images" + File.separator + "prime_logo.png";
//
//        pdf.add(Image.getInstance(logo));
//    }

    //endregion

}
