//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import org.labkey.data.xml.querycustomview.LocalOrRefFiltersType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * A SQL table or object treated like a table in the underlying relational database.
 *
 * <p>Java-Klasse für TableType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="TableType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all minOccurs="0">
 *         &lt;element name="includeColumnsList" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tableTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tableGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="manageTableAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="dbTableName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="manageTables" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nextStep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pkColumnName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="versionColumnName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gridUrl" type="{http://labkey.org/data/xml}StringExpressionType" minOccurs="0"/>
 *         &lt;element name="importUrl" type="{http://labkey.org/data/xml}StringExpressionType" minOccurs="0"/>
 *         &lt;element name="insertUrl" type="{http://labkey.org/data/xml}StringExpressionType" minOccurs="0"/>
 *         &lt;element name="deleteUrl" type="{http://labkey.org/data/xml}StringExpressionType" minOccurs="0"/>
 *         &lt;element name="updateUrl" type="{http://labkey.org/data/xml}StringExpressionType" minOccurs="0"/>
 *         &lt;element name="tableUrl" type="{http://labkey.org/data/xml}StringExpressionType" minOccurs="0"/>
 *         &lt;element name="titleColumn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cacheSize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="importMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="importTemplates" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="template" type="{http://labkey.org/data/xml}ImportTemplateType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="columns" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="column" type="{http://labkey.org/data/xml}ColumnType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="indices" type="{http://labkey.org/data/xml}IndicesType" minOccurs="0"/>
 *         &lt;element name="viewOptions" type="{http://labkey.org/data/xml}ViewOptions" minOccurs="0"/>
 *         &lt;element name="buttonBarOptions" type="{http://labkey.org/data/xml}ButtonBarOptions" minOccurs="0"/>
 *         &lt;element name="aggregateRowOptions" type="{http://labkey.org/data/xml}AggregateRowOptions" minOccurs="0"/>
 *         &lt;element name="javaCustomizer" type="{http://labkey.org/data/xml}TableCustomizerType" minOccurs="0"/>
 *         &lt;element name="auditLogging" type="{http://labkey.org/data/xml}AuditType" minOccurs="0"/>
 *         &lt;element name="filters" type="{http://labkey.org/data/xml/queryCustomView}localOrRefFiltersType" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="tableName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tableDbType" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="TABLE|VIEW|NOT_IN_DB|UNKNOWN"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="useColumnOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="hidden" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TableType", propOrder = {

})
public class TableType {

    protected String includeColumnsList;
    protected String tableTitle;
    protected String description;
    protected String tableGroup;
    protected Boolean manageTableAllowed;
    protected String dbTableName;
    protected String manageTables;
    protected String nextStep;
    protected String pkColumnName;
    protected String versionColumnName;
    protected StringExpressionType gridUrl;
    protected StringExpressionType importUrl;
    protected StringExpressionType insertUrl;
    protected StringExpressionType deleteUrl;
    protected StringExpressionType updateUrl;
    protected StringExpressionType tableUrl;
    protected String titleColumn;
    protected Integer cacheSize;
    protected String importMessage;
    protected ImportTemplates importTemplates;
    protected Columns columns;
    protected IndicesType indices;
    protected ViewOptions viewOptions;
    protected ButtonBarOptions buttonBarOptions;
    protected AggregateRowOptions aggregateRowOptions;
    protected TableCustomizerType javaCustomizer;
    @XmlSchemaType(name = "string")
    protected AuditType auditLogging;
    protected LocalOrRefFiltersType filters;
    @XmlAttribute(name = "tableName", required = true)
    protected String tableName;
    @XmlAttribute(name = "tableDbType", required = true)
    protected String tableDbType;
    @XmlAttribute(name = "useColumnOrder")
    protected Boolean useColumnOrder;
    @XmlAttribute(name = "hidden")
    protected Boolean hidden;

    /**
     * Ruft den Wert der includeColumnsList-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getIncludeColumnsList() {
        return includeColumnsList;
    }

    /**
     * Legt den Wert der includeColumnsList-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIncludeColumnsList(String value) {
        this.includeColumnsList = value;
    }

    /**
     * Ruft den Wert der tableTitle-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTableTitle() {
        return tableTitle;
    }

    /**
     * Legt den Wert der tableTitle-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTableTitle(String value) {
        this.tableTitle = value;
    }

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Ruft den Wert der tableGroup-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTableGroup() {
        return tableGroup;
    }

    /**
     * Legt den Wert der tableGroup-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTableGroup(String value) {
        this.tableGroup = value;
    }

    /**
     * Ruft den Wert der manageTableAllowed-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isManageTableAllowed() {
        return manageTableAllowed;
    }

    /**
     * Legt den Wert der manageTableAllowed-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setManageTableAllowed(Boolean value) {
        this.manageTableAllowed = value;
    }

    /**
     * Ruft den Wert der dbTableName-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDbTableName() {
        return dbTableName;
    }

    /**
     * Legt den Wert der dbTableName-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDbTableName(String value) {
        this.dbTableName = value;
    }

    /**
     * Ruft den Wert der manageTables-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getManageTables() {
        return manageTables;
    }

    /**
     * Legt den Wert der manageTables-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setManageTables(String value) {
        this.manageTables = value;
    }

    /**
     * Ruft den Wert der nextStep-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNextStep() {
        return nextStep;
    }

    /**
     * Legt den Wert der nextStep-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNextStep(String value) {
        this.nextStep = value;
    }

    /**
     * Ruft den Wert der pkColumnName-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPkColumnName() {
        return pkColumnName;
    }

    /**
     * Legt den Wert der pkColumnName-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPkColumnName(String value) {
        this.pkColumnName = value;
    }

    /**
     * Ruft den Wert der versionColumnName-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersionColumnName() {
        return versionColumnName;
    }

    /**
     * Legt den Wert der versionColumnName-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersionColumnName(String value) {
        this.versionColumnName = value;
    }

    /**
     * Ruft den Wert der gridUrl-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link StringExpressionType }
     *
     */
    public StringExpressionType getGridUrl() {
        return gridUrl;
    }

    /**
     * Legt den Wert der gridUrl-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link StringExpressionType }
     *
     */
    public void setGridUrl(StringExpressionType value) {
        this.gridUrl = value;
    }

    /**
     * Ruft den Wert der importUrl-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link StringExpressionType }
     *
     */
    public StringExpressionType getImportUrl() {
        return importUrl;
    }

    /**
     * Legt den Wert der importUrl-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link StringExpressionType }
     *
     */
    public void setImportUrl(StringExpressionType value) {
        this.importUrl = value;
    }

    /**
     * Ruft den Wert der insertUrl-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link StringExpressionType }
     *
     */
    public StringExpressionType getInsertUrl() {
        return insertUrl;
    }

    /**
     * Legt den Wert der insertUrl-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link StringExpressionType }
     *
     */
    public void setInsertUrl(StringExpressionType value) {
        this.insertUrl = value;
    }

    /**
     * Ruft den Wert der deleteUrl-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link StringExpressionType }
     *
     */
    public StringExpressionType getDeleteUrl() {
        return deleteUrl;
    }

    /**
     * Legt den Wert der deleteUrl-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link StringExpressionType }
     *
     */
    public void setDeleteUrl(StringExpressionType value) {
        this.deleteUrl = value;
    }

    /**
     * Ruft den Wert der updateUrl-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link StringExpressionType }
     *
     */
    public StringExpressionType getUpdateUrl() {
        return updateUrl;
    }

    /**
     * Legt den Wert der updateUrl-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link StringExpressionType }
     *
     */
    public void setUpdateUrl(StringExpressionType value) {
        this.updateUrl = value;
    }

    /**
     * Ruft den Wert der tableUrl-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link StringExpressionType }
     *
     */
    public StringExpressionType getTableUrl() {
        return tableUrl;
    }

    /**
     * Legt den Wert der tableUrl-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link StringExpressionType }
     *
     */
    public void setTableUrl(StringExpressionType value) {
        this.tableUrl = value;
    }

    /**
     * Ruft den Wert der titleColumn-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitleColumn() {
        return titleColumn;
    }

    /**
     * Legt den Wert der titleColumn-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitleColumn(String value) {
        this.titleColumn = value;
    }

    /**
     * Ruft den Wert der cacheSize-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getCacheSize() {
        return cacheSize;
    }

    /**
     * Legt den Wert der cacheSize-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setCacheSize(Integer value) {
        this.cacheSize = value;
    }

    /**
     * Ruft den Wert der importMessage-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getImportMessage() {
        return importMessage;
    }

    /**
     * Legt den Wert der importMessage-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setImportMessage(String value) {
        this.importMessage = value;
    }

    /**
     * Ruft den Wert der importTemplates-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link ImportTemplates }
     *
     */
    public ImportTemplates getImportTemplates() {
        return importTemplates;
    }

    /**
     * Legt den Wert der importTemplates-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link ImportTemplates }
     *
     */
    public void setImportTemplates(ImportTemplates value) {
        this.importTemplates = value;
    }

    /**
     * Ruft den Wert der columns-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Columns }
     *
     */
    public Columns getColumns() {
        return columns;
    }

    /**
     * Legt den Wert der columns-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Columns }
     *
     */
    public void setColumns(Columns value) {
        this.columns = value;
    }

    /**
     * Ruft den Wert der indices-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link IndicesType }
     *
     */
    public IndicesType getIndices() {
        return indices;
    }

    /**
     * Legt den Wert der indices-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link IndicesType }
     *
     */
    public void setIndices(IndicesType value) {
        this.indices = value;
    }

    /**
     * Ruft den Wert der viewOptions-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link ViewOptions }
     *
     */
    public ViewOptions getViewOptions() {
        return viewOptions;
    }

    /**
     * Legt den Wert der viewOptions-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link ViewOptions }
     *
     */
    public void setViewOptions(ViewOptions value) {
        this.viewOptions = value;
    }

    /**
     * Ruft den Wert der buttonBarOptions-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link ButtonBarOptions }
     *
     */
    public ButtonBarOptions getButtonBarOptions() {
        return buttonBarOptions;
    }

    /**
     * Legt den Wert der buttonBarOptions-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link ButtonBarOptions }
     *
     */
    public void setButtonBarOptions(ButtonBarOptions value) {
        this.buttonBarOptions = value;
    }

    /**
     * Ruft den Wert der aggregateRowOptions-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link AggregateRowOptions }
     *
     */
    public AggregateRowOptions getAggregateRowOptions() {
        return aggregateRowOptions;
    }

    /**
     * Legt den Wert der aggregateRowOptions-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link AggregateRowOptions }
     *
     */
    public void setAggregateRowOptions(AggregateRowOptions value) {
        this.aggregateRowOptions = value;
    }

    /**
     * Ruft den Wert der javaCustomizer-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link TableCustomizerType }
     *
     */
    public TableCustomizerType getJavaCustomizer() {
        return javaCustomizer;
    }

    /**
     * Legt den Wert der javaCustomizer-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link TableCustomizerType }
     *
     */
    public void setJavaCustomizer(TableCustomizerType value) {
        this.javaCustomizer = value;
    }

    /**
     * Ruft den Wert der auditLogging-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link AuditType }
     *
     */
    public AuditType getAuditLogging() {
        return auditLogging;
    }

    /**
     * Legt den Wert der auditLogging-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link AuditType }
     *
     */
    public void setAuditLogging(AuditType value) {
        this.auditLogging = value;
    }

    /**
     * Ruft den Wert der filters-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link LocalOrRefFiltersType }
     *
     */
    public LocalOrRefFiltersType getFilters() {
        return filters;
    }

    /**
     * Legt den Wert der filters-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link LocalOrRefFiltersType }
     *
     */
    public void setFilters(LocalOrRefFiltersType value) {
        this.filters = value;
    }

    /**
     * Ruft den Wert der tableName-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Legt den Wert der tableName-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTableName(String value) {
        this.tableName = value;
    }

    /**
     * Ruft den Wert der tableDbType-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTableDbType() {
        return tableDbType;
    }

    /**
     * Legt den Wert der tableDbType-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTableDbType(String value) {
        this.tableDbType = value;
    }

    /**
     * Ruft den Wert der useColumnOrder-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isUseColumnOrder() {
        if (useColumnOrder == null) {
            return false;
        } else {
            return useColumnOrder;
        }
    }

    /**
     * Legt den Wert der useColumnOrder-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setUseColumnOrder(Boolean value) {
        this.useColumnOrder = value;
    }

    /**
     * Ruft den Wert der hidden-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isHidden() {
        if (hidden == null) {
            return false;
        } else {
            return hidden;
        }
    }

    /**
     * Legt den Wert der hidden-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setHidden(Boolean value) {
        this.hidden = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     *
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="column" type="{http://labkey.org/data/xml}ColumnType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "column"
    })
    public static class Columns {

        protected List<ColumnType> column;

        /**
         * Gets the value of the column property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the column property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getColumn().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ColumnType }
         *
         *
         */
        public List<ColumnType> getColumn() {
            if (column == null) {
                column = new ArrayList<ColumnType>();
            }
            return this.column;
        }

        public void setColumn(List<ColumnType> columnList) {
            this.column = columnList;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     *
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="template" type="{http://labkey.org/data/xml}ImportTemplateType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "template"
    })
    public static class ImportTemplates {

        protected List<ImportTemplateType> template;

        /**
         * Gets the value of the template property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the template property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTemplate().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ImportTemplateType }
         *
         *
         */
        public List<ImportTemplateType> getTemplate() {
            if (template == null) {
                template = new ArrayList<ImportTemplateType>();
            }
            return this.template;
        }

    }

}
