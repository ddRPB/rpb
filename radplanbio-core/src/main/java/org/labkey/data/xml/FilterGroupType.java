//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import org.labkey.data.xml.querycustomview.FilterType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * A filter to be applied to target table results, which populate the selection list for this column,
 * the operation attribute determines when to apply the filters for the lookup. Valid values are 'insert' or
 * 'update' and multiple comma delimited values can be specified. If the attribute is omitted then it is the
 * equivalent of : 'insert,update'. Multiple filter elements can be specified and if so will be combined when
 * the table is queried.
 *
 *
 * <p>Java-Klasse für FilterGroupType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="FilterGroupType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="filter" type="{http://labkey.org/data/xml/queryCustomView}filterType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="operation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterGroupType", propOrder = {
        "filter"
})
public class FilterGroupType {

    protected List<FilterType> filter;
    @XmlAttribute(name = "operation")
    protected String operation;

    /**
     * Gets the value of the filter property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the filter property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFilter().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FilterType }
     */
    public List<FilterType> getFilter() {
        if (filter == null) {
            filter = new ArrayList<FilterType>();
        }
        return this.filter;
    }

    /**
     * Ruft den Wert der operation-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Legt den Wert der operation-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOperation(String value) {
        this.operation = value;
    }

}
