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
 * <p>Java-Klasse für sortType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="sortType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="column" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="descending" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sortType")
public class SortType {

    @XmlAttribute(name = "column")
    protected String column;
    @XmlAttribute(name = "descending")
    protected Boolean descending;

    /**
     * Ruft den Wert der column-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getColumn() {
        return column;
    }

    /**
     * Legt den Wert der column-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setColumn(String value) {
        this.column = value;
    }

    /**
     * Ruft den Wert der descending-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public boolean isDescending() {
        if (descending == null) {
            return false;
        } else {
            return descending;
        }
    }

    /**
     * Legt den Wert der descending-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setDescending(Boolean value) {
        this.descending = value;
    }

}
