//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml.querycustomview;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für customViewType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="customViewType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="columns" type="{http://labkey.org/data/xml/queryCustomView}columnsType" minOccurs="0"/>
 *         &lt;element name="filters" type="{http://labkey.org/data/xml/queryCustomView}filtersType" minOccurs="0"/>
 *         &lt;element name="sorts" type="{http://labkey.org/data/xml/queryCustomView}sortsType" minOccurs="0"/>
 *         &lt;element name="aggregates" type="{http://labkey.org/data/xml/queryCustomView}aggregatesType" minOccurs="0"/>
 *         &lt;element name="analyticsProviders" type="{http://labkey.org/data/xml/queryCustomView}analyticsProvidersType" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="schema" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="query" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="hidden" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="canInherit" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="canOverride" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="customIconUrl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="containerFilter" type="{http://labkey.org/data/xml/queryCustomView}containerFilterType" />
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="showInDataViews" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customViewType", propOrder = {

})
public class CustomViewType {

    protected ColumnsType columns;
    protected FiltersType filters;
    protected SortsType sorts;
    protected AggregatesType aggregates;
    protected AnalyticsProvidersType analyticsProviders;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "schema")
    protected String schema;
    @XmlAttribute(name = "query")
    protected String query;
    @XmlAttribute(name = "hidden")
    protected Boolean hidden;
    @XmlAttribute(name = "canInherit")
    protected Boolean canInherit;
    @XmlAttribute(name = "canOverride")
    protected Boolean canOverride;
    @XmlAttribute(name = "customIconUrl")
    protected String customIconUrl;
    @XmlAttribute(name = "containerFilter")
    protected ContainerFilterType containerFilter;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "showInDataViews")
    protected Boolean showInDataViews;
    @XmlAttribute(name = "category")
    protected String category;

    /**
     * Ruft den Wert der columns-Eigenschaft ab.
     *
     * @return possible object is
     * {@link ColumnsType }
     */
    public ColumnsType getColumns() {
        return columns;
    }

    /**
     * Legt den Wert der columns-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link ColumnsType }
     */
    public void setColumns(ColumnsType value) {
        this.columns = value;
    }

    /**
     * Ruft den Wert der filters-Eigenschaft ab.
     *
     * @return possible object is
     * {@link FiltersType }
     */
    public FiltersType getFilters() {
        return filters;
    }

    /**
     * Legt den Wert der filters-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link FiltersType }
     */
    public void setFilters(FiltersType value) {
        this.filters = value;
    }

    /**
     * Ruft den Wert der sorts-Eigenschaft ab.
     *
     * @return possible object is
     * {@link SortsType }
     */
    public SortsType getSorts() {
        return sorts;
    }

    /**
     * Legt den Wert der sorts-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link SortsType }
     */
    public void setSorts(SortsType value) {
        this.sorts = value;
    }

    /**
     * Ruft den Wert der aggregates-Eigenschaft ab.
     *
     * @return possible object is
     * {@link AggregatesType }
     */
    public AggregatesType getAggregates() {
        return aggregates;
    }

    /**
     * Legt den Wert der aggregates-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link AggregatesType }
     */
    public void setAggregates(AggregatesType value) {
        this.aggregates = value;
    }

    /**
     * Ruft den Wert der analyticsProviders-Eigenschaft ab.
     *
     * @return possible object is
     * {@link AnalyticsProvidersType }
     */
    public AnalyticsProvidersType getAnalyticsProviders() {
        return analyticsProviders;
    }

    /**
     * Legt den Wert der analyticsProviders-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link AnalyticsProvidersType }
     */
    public void setAnalyticsProviders(AnalyticsProvidersType value) {
        this.analyticsProviders = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der schema-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Legt den Wert der schema-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSchema(String value) {
        this.schema = value;
    }

    /**
     * Ruft den Wert der query-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getQuery() {
        return query;
    }

    /**
     * Legt den Wert der query-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setQuery(String value) {
        this.query = value;
    }

    /**
     * Ruft den Wert der hidden-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
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
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setHidden(Boolean value) {
        this.hidden = value;
    }

    /**
     * Ruft den Wert der canInherit-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public boolean isCanInherit() {
        if (canInherit == null) {
            return false;
        } else {
            return canInherit;
        }
    }

    /**
     * Legt den Wert der canInherit-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setCanInherit(Boolean value) {
        this.canInherit = value;
    }

    /**
     * Ruft den Wert der canOverride-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public boolean isCanOverride() {
        if (canOverride == null) {
            return false;
        } else {
            return canOverride;
        }
    }

    /**
     * Legt den Wert der canOverride-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setCanOverride(Boolean value) {
        this.canOverride = value;
    }

    /**
     * Ruft den Wert der customIconUrl-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCustomIconUrl() {
        return customIconUrl;
    }

    /**
     * Legt den Wert der customIconUrl-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCustomIconUrl(String value) {
        this.customIconUrl = value;
    }

    /**
     * Ruft den Wert der containerFilter-Eigenschaft ab.
     *
     * @return possible object is
     * {@link ContainerFilterType }
     */
    public ContainerFilterType getContainerFilter() {
        return containerFilter;
    }

    /**
     * Legt den Wert der containerFilter-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link ContainerFilterType }
     */
    public void setContainerFilter(ContainerFilterType value) {
        this.containerFilter = value;
    }

    /**
     * Ruft den Wert der label-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLabel() {
        return label;
    }

    /**
     * Legt den Wert der label-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Ruft den Wert der showInDataViews-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public boolean isShowInDataViews() {
        if (showInDataViews == null) {
            return false;
        } else {
            return showInDataViews;
        }
    }

    /**
     * Legt den Wert der showInDataViews-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setShowInDataViews(Boolean value) {
        this.showInDataViews = value;
    }

    /**
     * Ruft den Wert der category-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCategory() {
        return category;
    }

    /**
     * Legt den Wert der category-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCategory(String value) {
        this.category = value;
    }

}
