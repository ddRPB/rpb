//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml.querycustomview;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse für localOrRefFiltersType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="localOrRefFiltersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://labkey.org/data/xml/queryCustomView}filtersGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "localOrRefFiltersType", propOrder = {
        "filtersGroup"
})
public class LocalOrRefFiltersType {

    @XmlElements({
            @XmlElement(name = "filter", type = FilterType.class),
            @XmlElement(name = "where", type = String.class)
    })
    protected List<Object> filtersGroup;
    @XmlAttribute(name = "ref")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object ref;

    /**
     * Gets the value of the filtersGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the filtersGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFiltersGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FilterType }
     * {@link String }
     */
    public List<Object> getFiltersGroup() {
        if (filtersGroup == null) {
            filtersGroup = new ArrayList<Object>();
        }
        return this.filtersGroup;
    }

    /**
     * Ruft den Wert der ref-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getRef() {
        return ref;
    }

    /**
     * Legt den Wert der ref-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setRef(Object value) {
        this.ref = value;
    }

}
