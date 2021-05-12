//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The definition of column within the table, view or result set.
 *
 * <p>Java-Klasse für ColumnType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ColumnType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="columnIndex" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="datatype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nullable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="columnTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scale" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="precision" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="defaultValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="defaultValueType" type="{http://labkey.org/data/xml}DefaultValueEnumType" minOccurs="0"/>
 *         &lt;element name="autoFillValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isAutoInc" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isDisplayColumn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isReadOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isUserEditable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isHidden" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="shownInInsertView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="shownInUpdateView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="shownInDetailsView" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="measure" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="dimension" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="keyVariable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="recommendedVariable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="defaultScale" type="{http://labkey.org/data/xml}DefaultScaleType" minOccurs="0"/>
 *         &lt;element name="facetingBehavior" type="{http://labkey.org/data/xml}FacetingBehaviorType" minOccurs="0"/>
 *         &lt;element name="protected" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="excludeFromShifting" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="phi" type="{http://labkey.org/data/xml}PHIType" minOccurs="0"/>
 *         &lt;element name="shouldLog" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isUnselectable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isMvEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="sortDescending" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="inputType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inputLength" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="inputRows" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="onChange" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isKeyField" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="optionlistQuery" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url" type="{http://labkey.org/data/xml}StringExpressionType" minOccurs="0"/>
 *         &lt;element name="urlTarget" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="displayWidth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="formatString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="conditionalFormats" type="{http://labkey.org/data/xml}ConditionalFormatsType" minOccurs="0"/>
 *         &lt;element name="validators" type="{http://labkey.org/data/xml}ValidatorsType" minOccurs="0"/>
 *         &lt;element name="excelFormatString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tsvFormatString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="textExpression" type="{http://labkey.org/data/xml}StringExpressionType" minOccurs="0"/>
 *         &lt;element name="textAlign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="propertyURI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="conceptURI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="redactedText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rangeURI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sortColumn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fk" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="fkFolderPath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="fkTable" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="fkColumnName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="fkDbSchema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="fkMultiValued" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="fkJunctionLookup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="fkDisplayColumnName" type="{http://labkey.org/data/xml}FkDisplayColumnNameType" minOccurs="0"/>
 *                   &lt;element name="filters" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="filterGroup" type="{http://labkey.org/data/xml}FilterGroupType" maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="importAliases" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="importAlias" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Ontology" type="{http://labkey.org/data/xml}OntologyType" minOccurs="0"/>
 *         &lt;element name="displayColumnFactory" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="className" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="properties" type="{http://labkey.org/data/xml}PropertiesType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *       &lt;attribute name="columnName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mvColumnName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="wrappedColumnName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColumnType", propOrder = {

})
public class ColumnType {

    protected Integer columnIndex;
    protected String datatype;
    protected Boolean nullable;
    protected Boolean required;
    protected String columnTitle;
    protected Integer scale;
    protected Integer precision;
    protected String defaultValue;
    @XmlSchemaType(name = "string")
    protected DefaultValueEnumType defaultValueType;
    protected String autoFillValue;
    protected Boolean isAutoInc;
    protected Boolean isDisplayColumn;
    protected Boolean isReadOnly;
    protected Boolean isUserEditable;
    protected Boolean isHidden;
    protected Boolean shownInInsertView;
    protected Boolean shownInUpdateView;
    protected Boolean shownInDetailsView;
    protected Boolean measure;
    protected Boolean dimension;
    protected Boolean keyVariable;
    protected Boolean recommendedVariable;
    @XmlSchemaType(name = "string")
    protected DefaultScaleType defaultScale;
    @XmlSchemaType(name = "string")
    protected FacetingBehaviorType facetingBehavior;
    @XmlElement(name = "protected")
    protected Boolean _protected;
    protected Boolean excludeFromShifting;
    @XmlSchemaType(name = "string")
    protected PHIType phi;
    protected Boolean shouldLog;
    protected Boolean isUnselectable;
    protected Boolean isMvEnabled;
    protected Boolean sortDescending;
    protected String inputType;
    protected Integer inputLength;
    protected Integer inputRows;
    protected String onChange;
    protected Boolean isKeyField;
    protected String description;
    protected String optionlistQuery;
    protected StringExpressionType url;
    protected String urlTarget;
    protected String displayWidth;
    protected String formatString;
    protected ConditionalFormatsType conditionalFormats;
    protected ValidatorsType validators;
    protected String excelFormatString;
    protected String tsvFormatString;
    protected StringExpressionType textExpression;
    protected String textAlign;
    protected String propertyURI;
    protected String conceptURI;
    protected String redactedText;
    protected String rangeURI;
    protected String sortColumn;
    protected Fk fk;
    protected ImportAliases importAliases;
    @XmlElement(name = "Ontology")
    protected OntologyType ontology;
    protected DisplayColumnFactory displayColumnFactory;
    @XmlAttribute(name = "columnName", required = true)
    protected String columnName;
    @XmlAttribute(name = "mvColumnName")
    protected String mvColumnName;
    @XmlAttribute(name = "wrappedColumnName")
    protected String wrappedColumnName;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der columnIndex-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getColumnIndex() {
        return columnIndex;
    }

    /**
     * Legt den Wert der columnIndex-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setColumnIndex(Integer value) {
        this.columnIndex = value;
    }

    /**
     * Ruft den Wert der datatype-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     * Legt den Wert der datatype-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatatype(String value) {
        this.datatype = value;
    }

    /**
     * Ruft den Wert der nullable-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isNullable() {
        return nullable;
    }

    /**
     * Legt den Wert der nullable-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setNullable(Boolean value) {
        this.nullable = value;
    }

    /**
     * Ruft den Wert der required-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isRequired() {
        return required;
    }

    /**
     * Legt den Wert der required-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setRequired(Boolean value) {
        this.required = value;
    }

    /**
     * Ruft den Wert der columnTitle-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColumnTitle() {
        return columnTitle;
    }

    /**
     * Legt den Wert der columnTitle-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColumnTitle(String value) {
        this.columnTitle = value;
    }

    /**
     * Ruft den Wert der scale-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getScale() {
        return scale;
    }

    /**
     * Legt den Wert der scale-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setScale(Integer value) {
        this.scale = value;
    }

    /**
     * Ruft den Wert der precision-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getPrecision() {
        return precision;
    }

    /**
     * Legt den Wert der precision-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setPrecision(Integer value) {
        this.precision = value;
    }

    /**
     * Ruft den Wert der defaultValue-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Legt den Wert der defaultValue-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    /**
     * Ruft den Wert der defaultValueType-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link DefaultValueEnumType }
     *
     */
    public DefaultValueEnumType getDefaultValueType() {
        return defaultValueType;
    }

    /**
     * Legt den Wert der defaultValueType-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link DefaultValueEnumType }
     *
     */
    public void setDefaultValueType(DefaultValueEnumType value) {
        this.defaultValueType = value;
    }

    /**
     * Ruft den Wert der autoFillValue-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAutoFillValue() {
        return autoFillValue;
    }

    /**
     * Legt den Wert der autoFillValue-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAutoFillValue(String value) {
        this.autoFillValue = value;
    }

    /**
     * Ruft den Wert der isAutoInc-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsAutoInc() {
        return isAutoInc;
    }

    /**
     * Legt den Wert der isAutoInc-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsAutoInc(Boolean value) {
        this.isAutoInc = value;
    }

    /**
     * Ruft den Wert der isDisplayColumn-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsDisplayColumn() {
        return isDisplayColumn;
    }

    /**
     * Legt den Wert der isDisplayColumn-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsDisplayColumn(Boolean value) {
        this.isDisplayColumn = value;
    }

    /**
     * Ruft den Wert der isReadOnly-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsReadOnly() {
        return isReadOnly;
    }

    /**
     * Legt den Wert der isReadOnly-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsReadOnly(Boolean value) {
        this.isReadOnly = value;
    }

    /**
     * Ruft den Wert der isUserEditable-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsUserEditable() {
        return isUserEditable;
    }

    /**
     * Legt den Wert der isUserEditable-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsUserEditable(Boolean value) {
        this.isUserEditable = value;
    }

    /**
     * Ruft den Wert der isHidden-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsHidden() {
        return isHidden;
    }

    /**
     * Legt den Wert der isHidden-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsHidden(Boolean value) {
        this.isHidden = value;
    }

    /**
     * Ruft den Wert der shownInInsertView-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isShownInInsertView() {
        return shownInInsertView;
    }

    /**
     * Legt den Wert der shownInInsertView-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setShownInInsertView(Boolean value) {
        this.shownInInsertView = value;
    }

    /**
     * Ruft den Wert der shownInUpdateView-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isShownInUpdateView() {
        return shownInUpdateView;
    }

    /**
     * Legt den Wert der shownInUpdateView-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setShownInUpdateView(Boolean value) {
        this.shownInUpdateView = value;
    }

    /**
     * Ruft den Wert der shownInDetailsView-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isShownInDetailsView() {
        return shownInDetailsView;
    }

    /**
     * Legt den Wert der shownInDetailsView-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setShownInDetailsView(Boolean value) {
        this.shownInDetailsView = value;
    }

    /**
     * Ruft den Wert der measure-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isMeasure() {
        return measure;
    }

    /**
     * Legt den Wert der measure-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setMeasure(Boolean value) {
        this.measure = value;
    }

    /**
     * Ruft den Wert der dimension-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isDimension() {
        return dimension;
    }

    /**
     * Legt den Wert der dimension-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setDimension(Boolean value) {
        this.dimension = value;
    }

    /**
     * Ruft den Wert der keyVariable-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isKeyVariable() {
        return keyVariable;
    }

    /**
     * Legt den Wert der keyVariable-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setKeyVariable(Boolean value) {
        this.keyVariable = value;
    }

    /**
     * Ruft den Wert der recommendedVariable-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isRecommendedVariable() {
        return recommendedVariable;
    }

    /**
     * Legt den Wert der recommendedVariable-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setRecommendedVariable(Boolean value) {
        this.recommendedVariable = value;
    }

    /**
     * Ruft den Wert der defaultScale-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link DefaultScaleType }
     *
     */
    public DefaultScaleType getDefaultScale() {
        return defaultScale;
    }

    /**
     * Legt den Wert der defaultScale-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link DefaultScaleType }
     *
     */
    public void setDefaultScale(DefaultScaleType value) {
        this.defaultScale = value;
    }

    /**
     * Ruft den Wert der facetingBehavior-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link FacetingBehaviorType }
     *
     */
    public FacetingBehaviorType getFacetingBehavior() {
        return facetingBehavior;
    }

    /**
     * Legt den Wert der facetingBehavior-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link FacetingBehaviorType }
     *
     */
    public void setFacetingBehavior(FacetingBehaviorType value) {
        this.facetingBehavior = value;
    }

    /**
     * Ruft den Wert der protected-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isProtected() {
        return _protected;
    }

    /**
     * Legt den Wert der protected-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setProtected(Boolean value) {
        this._protected = value;
    }

    /**
     * Ruft den Wert der excludeFromShifting-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isExcludeFromShifting() {
        return excludeFromShifting;
    }

    /**
     * Legt den Wert der excludeFromShifting-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setExcludeFromShifting(Boolean value) {
        this.excludeFromShifting = value;
    }

    /**
     * Ruft den Wert der phi-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link PHIType }
     *
     */
    public PHIType getPhi() {
        return phi;
    }

    /**
     * Legt den Wert der phi-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link PHIType }
     *
     */
    public void setPhi(PHIType value) {
        this.phi = value;
    }

    /**
     * Ruft den Wert der shouldLog-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isShouldLog() {
        return shouldLog;
    }

    /**
     * Legt den Wert der shouldLog-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setShouldLog(Boolean value) {
        this.shouldLog = value;
    }

    /**
     * Ruft den Wert der isUnselectable-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsUnselectable() {
        return isUnselectable;
    }

    /**
     * Legt den Wert der isUnselectable-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsUnselectable(Boolean value) {
        this.isUnselectable = value;
    }

    /**
     * Ruft den Wert der isMvEnabled-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsMvEnabled() {
        return isMvEnabled;
    }

    /**
     * Legt den Wert der isMvEnabled-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsMvEnabled(Boolean value) {
        this.isMvEnabled = value;
    }

    /**
     * Ruft den Wert der sortDescending-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isSortDescending() {
        return sortDescending;
    }

    /**
     * Legt den Wert der sortDescending-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setSortDescending(Boolean value) {
        this.sortDescending = value;
    }

    /**
     * Ruft den Wert der inputType-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInputType() {
        return inputType;
    }

    /**
     * Legt den Wert der inputType-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInputType(String value) {
        this.inputType = value;
    }

    /**
     * Ruft den Wert der inputLength-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getInputLength() {
        return inputLength;
    }

    /**
     * Legt den Wert der inputLength-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setInputLength(Integer value) {
        this.inputLength = value;
    }

    /**
     * Ruft den Wert der inputRows-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getInputRows() {
        return inputRows;
    }

    /**
     * Legt den Wert der inputRows-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setInputRows(Integer value) {
        this.inputRows = value;
    }

    /**
     * Ruft den Wert der onChange-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnChange() {
        return onChange;
    }

    /**
     * Legt den Wert der onChange-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnChange(String value) {
        this.onChange = value;
    }

    /**
     * Ruft den Wert der isKeyField-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsKeyField() {
        return isKeyField;
    }

    /**
     * Legt den Wert der isKeyField-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsKeyField(Boolean value) {
        this.isKeyField = value;
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
     * Ruft den Wert der optionlistQuery-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOptionlistQuery() {
        return optionlistQuery;
    }

    /**
     * Legt den Wert der optionlistQuery-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOptionlistQuery(String value) {
        this.optionlistQuery = value;
    }

    /**
     * Ruft den Wert der url-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link StringExpressionType }
     *
     */
    public StringExpressionType getUrl() {
        return url;
    }

    /**
     * Legt den Wert der url-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link StringExpressionType }
     *
     */
    public void setUrl(StringExpressionType value) {
        this.url = value;
    }

    /**
     * Ruft den Wert der urlTarget-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUrlTarget() {
        return urlTarget;
    }

    /**
     * Legt den Wert der urlTarget-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUrlTarget(String value) {
        this.urlTarget = value;
    }

    /**
     * Ruft den Wert der displayWidth-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDisplayWidth() {
        return displayWidth;
    }

    /**
     * Legt den Wert der displayWidth-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDisplayWidth(String value) {
        this.displayWidth = value;
    }

    /**
     * Ruft den Wert der formatString-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFormatString() {
        return formatString;
    }

    /**
     * Legt den Wert der formatString-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFormatString(String value) {
        this.formatString = value;
    }

    /**
     * Ruft den Wert der conditionalFormats-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link ConditionalFormatsType }
     *
     */
    public ConditionalFormatsType getConditionalFormats() {
        return conditionalFormats;
    }

    /**
     * Legt den Wert der conditionalFormats-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link ConditionalFormatsType }
     *
     */
    public void setConditionalFormats(ConditionalFormatsType value) {
        this.conditionalFormats = value;
    }

    /**
     * Ruft den Wert der validators-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link ValidatorsType }
     *
     */
    public ValidatorsType getValidators() {
        return validators;
    }

    /**
     * Legt den Wert der validators-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link ValidatorsType }
     *
     */
    public void setValidators(ValidatorsType value) {
        this.validators = value;
    }

    /**
     * Ruft den Wert der excelFormatString-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExcelFormatString() {
        return excelFormatString;
    }

    /**
     * Legt den Wert der excelFormatString-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExcelFormatString(String value) {
        this.excelFormatString = value;
    }

    /**
     * Ruft den Wert der tsvFormatString-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTsvFormatString() {
        return tsvFormatString;
    }

    /**
     * Legt den Wert der tsvFormatString-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTsvFormatString(String value) {
        this.tsvFormatString = value;
    }

    /**
     * Ruft den Wert der textExpression-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link StringExpressionType }
     *
     */
    public StringExpressionType getTextExpression() {
        return textExpression;
    }

    /**
     * Legt den Wert der textExpression-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link StringExpressionType }
     *
     */
    public void setTextExpression(StringExpressionType value) {
        this.textExpression = value;
    }

    /**
     * Ruft den Wert der textAlign-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTextAlign() {
        return textAlign;
    }

    /**
     * Legt den Wert der textAlign-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTextAlign(String value) {
        this.textAlign = value;
    }

    /**
     * Ruft den Wert der propertyURI-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPropertyURI() {
        return propertyURI;
    }

    /**
     * Legt den Wert der propertyURI-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPropertyURI(String value) {
        this.propertyURI = value;
    }

    /**
     * Ruft den Wert der conceptURI-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getConceptURI() {
        return conceptURI;
    }

    /**
     * Legt den Wert der conceptURI-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setConceptURI(String value) {
        this.conceptURI = value;
    }

    /**
     * Ruft den Wert der redactedText-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRedactedText() {
        return redactedText;
    }

    /**
     * Legt den Wert der redactedText-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRedactedText(String value) {
        this.redactedText = value;
    }

    /**
     * Ruft den Wert der rangeURI-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRangeURI() {
        return rangeURI;
    }

    /**
     * Legt den Wert der rangeURI-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRangeURI(String value) {
        this.rangeURI = value;
    }

    /**
     * Ruft den Wert der sortColumn-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSortColumn() {
        return sortColumn;
    }

    /**
     * Legt den Wert der sortColumn-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSortColumn(String value) {
        this.sortColumn = value;
    }

    /**
     * Ruft den Wert der fk-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Fk }
     *
     */
    public Fk getFk() {
        return fk;
    }

    /**
     * Legt den Wert der fk-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Fk }
     *
     */
    public void setFk(Fk value) {
        this.fk = value;
    }

    /**
     * Ruft den Wert der importAliases-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link ImportAliases }
     *
     */
    public ImportAliases getImportAliases() {
        return importAliases;
    }

    /**
     * Legt den Wert der importAliases-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link ImportAliases }
     *
     */
    public void setImportAliases(ImportAliases value) {
        this.importAliases = value;
    }

    /**
     * Ruft den Wert der ontology-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link OntologyType }
     *
     */
    public OntologyType getOntology() {
        return ontology;
    }

    /**
     * Legt den Wert der ontology-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link OntologyType }
     *
     */
    public void setOntology(OntologyType value) {
        this.ontology = value;
    }

    /**
     * Ruft den Wert der displayColumnFactory-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link DisplayColumnFactory }
     *
     */
    public DisplayColumnFactory getDisplayColumnFactory() {
        return displayColumnFactory;
    }

    /**
     * Legt den Wert der displayColumnFactory-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link DisplayColumnFactory }
     *
     */
    public void setDisplayColumnFactory(DisplayColumnFactory value) {
        this.displayColumnFactory = value;
    }

    /**
     * Ruft den Wert der columnName-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Legt den Wert der columnName-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColumnName(String value) {
        this.columnName = value;
    }

    /**
     * Ruft den Wert der mvColumnName-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMvColumnName() {
        return mvColumnName;
    }

    /**
     * Legt den Wert der mvColumnName-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMvColumnName(String value) {
        this.mvColumnName = value;
    }

    /**
     * Ruft den Wert der wrappedColumnName-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWrappedColumnName() {
        return wrappedColumnName;
    }

    /**
     * Legt den Wert der wrappedColumnName-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWrappedColumnName(String value) {
        this.wrappedColumnName = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
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
     *       &lt;sequence>
     *         &lt;element name="className" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="properties" type="{http://labkey.org/data/xml}PropertiesType" minOccurs="0"/>
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
            "className",
            "properties"
    })
    public static class DisplayColumnFactory {

        @XmlElement(required = true)
        protected String className;
        protected PropertiesType properties;

        /**
         * Ruft den Wert der className-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getClassName() {
            return className;
        }

        /**
         * Legt den Wert der className-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setClassName(String value) {
            this.className = value;
        }

        /**
         * Ruft den Wert der properties-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link PropertiesType }
         *
         */
        public PropertiesType getProperties() {
            return properties;
        }

        /**
         * Legt den Wert der properties-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link PropertiesType }
         *
         */
        public void setProperties(PropertiesType value) {
            this.properties = value;
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
     *       &lt;all>
     *         &lt;element name="fkFolderPath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="fkTable" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="fkColumnName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="fkDbSchema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="fkMultiValued" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="fkJunctionLookup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="fkDisplayColumnName" type="{http://labkey.org/data/xml}FkDisplayColumnNameType" minOccurs="0"/>
     *         &lt;element name="filters" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="filterGroup" type="{http://labkey.org/data/xml}FilterGroupType" maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Fk {

        protected String fkFolderPath;
        @XmlElement(required = true)
        protected String fkTable;
        protected String fkColumnName;
        protected String fkDbSchema;
        protected String fkMultiValued;
        protected String fkJunctionLookup;
        protected FkDisplayColumnNameType fkDisplayColumnName;
        protected Filters filters;

        /**
         * Ruft den Wert der fkFolderPath-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getFkFolderPath() {
            return fkFolderPath;
        }

        /**
         * Legt den Wert der fkFolderPath-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setFkFolderPath(String value) {
            this.fkFolderPath = value;
        }

        /**
         * Ruft den Wert der fkTable-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getFkTable() {
            return fkTable;
        }

        /**
         * Legt den Wert der fkTable-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setFkTable(String value) {
            this.fkTable = value;
        }

        /**
         * Ruft den Wert der fkColumnName-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getFkColumnName() {
            return fkColumnName;
        }

        /**
         * Legt den Wert der fkColumnName-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setFkColumnName(String value) {
            this.fkColumnName = value;
        }

        /**
         * Ruft den Wert der fkDbSchema-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getFkDbSchema() {
            return fkDbSchema;
        }

        /**
         * Legt den Wert der fkDbSchema-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setFkDbSchema(String value) {
            this.fkDbSchema = value;
        }

        /**
         * Ruft den Wert der fkMultiValued-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getFkMultiValued() {
            return fkMultiValued;
        }

        /**
         * Legt den Wert der fkMultiValued-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setFkMultiValued(String value) {
            this.fkMultiValued = value;
        }

        /**
         * Ruft den Wert der fkJunctionLookup-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getFkJunctionLookup() {
            return fkJunctionLookup;
        }

        /**
         * Legt den Wert der fkJunctionLookup-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setFkJunctionLookup(String value) {
            this.fkJunctionLookup = value;
        }

        /**
         * Ruft den Wert der fkDisplayColumnName-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link FkDisplayColumnNameType }
         *
         */
        public FkDisplayColumnNameType getFkDisplayColumnName() {
            return fkDisplayColumnName;
        }

        /**
         * Legt den Wert der fkDisplayColumnName-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link FkDisplayColumnNameType }
         *
         */
        public void setFkDisplayColumnName(FkDisplayColumnNameType value) {
            this.fkDisplayColumnName = value;
        }

        /**
         * Ruft den Wert der filters-Eigenschaft ab.
         *
         * @return
         *     possible object is
         *     {@link Filters }
         *
         */
        public Filters getFilters() {
            return filters;
        }

        /**
         * Legt den Wert der filters-Eigenschaft fest.
         *
         * @param value
         *     allowed object is
         *     {@link Filters }
         *
         */
        public void setFilters(Filters value) {
            this.filters = value;
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
         *       &lt;sequence>
         *         &lt;element name="filterGroup" type="{http://labkey.org/data/xml}FilterGroupType" maxOccurs="unbounded" minOccurs="0"/>
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
                "filterGroup"
        })
        public static class Filters {

            protected List<FilterGroupType> filterGroup;

            /**
             * Gets the value of the filterGroup property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the filterGroup property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getFilterGroup().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link FilterGroupType }
             *
             *
             */
            public List<FilterGroupType> getFilterGroup() {
                if (filterGroup == null) {
                    filterGroup = new ArrayList<FilterGroupType>();
                }
                return this.filterGroup;
            }

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
     *         &lt;element name="importAlias" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "importAlias"
    })
    public static class ImportAliases {

        protected List<String> importAlias;

        /**
         * Gets the value of the importAlias property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the importAlias property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getImportAlias().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         *
         *
         */
        public List<String> getImportAlias() {
            if (importAlias == null) {
                importAlias = new ArrayList<String>();
            }
            return this.importAlias;
        }

        public void setImportAlias(List<String> importAliases) {
            this.importAlias = importAliases;
        }

    }

}
