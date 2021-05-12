//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Supported for SQL metadata, assays import/export, dataset import/export and list import/export.
 *
 * <p>Java-Klasse für ConditionalFormatType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ConditionalFormatType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filters" type="{http://labkey.org/data/xml}ConditionalFormatFiltersType"/>
 *         &lt;element name="bold" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="italics" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="strikethrough" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="textColor" type="{http://labkey.org/data/xml}rbgColor" minOccurs="0"/>
 *         &lt;element name="backgroundColor" type="{http://labkey.org/data/xml}rbgColor" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConditionalFormatType", propOrder = {
        "filters",
        "bold",
        "italics",
        "strikethrough",
        "textColor",
        "backgroundColor"
})
public class ConditionalFormatType {

    @XmlElement(required = true)
    protected ConditionalFormatFiltersType filters;
    protected Boolean bold;
    protected Boolean italics;
    protected Boolean strikethrough;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String textColor;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String backgroundColor;

    /**
     * Ruft den Wert der filters-Eigenschaft ab.
     *
     * @return possible object is
     * {@link ConditionalFormatFiltersType }
     */
    public ConditionalFormatFiltersType getFilters() {
        return filters;
    }

    /**
     * Legt den Wert der filters-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link ConditionalFormatFiltersType }
     */
    public void setFilters(ConditionalFormatFiltersType value) {
        this.filters = value;
    }

    /**
     * Ruft den Wert der bold-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isBold() {
        return bold;
    }

    /**
     * Legt den Wert der bold-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setBold(Boolean value) {
        this.bold = value;
    }

    /**
     * Ruft den Wert der italics-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isItalics() {
        return italics;
    }

    /**
     * Legt den Wert der italics-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setItalics(Boolean value) {
        this.italics = value;
    }

    /**
     * Ruft den Wert der strikethrough-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isStrikethrough() {
        return strikethrough;
    }

    /**
     * Legt den Wert der strikethrough-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setStrikethrough(Boolean value) {
        this.strikethrough = value;
    }

    /**
     * Ruft den Wert der textColor-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTextColor() {
        return textColor;
    }

    /**
     * Legt den Wert der textColor-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTextColor(String value) {
        this.textColor = value;
    }

    /**
     * Ruft den Wert der backgroundColor-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Legt den Wert der backgroundColor-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBackgroundColor(String value) {
        this.backgroundColor = value;
    }

}
