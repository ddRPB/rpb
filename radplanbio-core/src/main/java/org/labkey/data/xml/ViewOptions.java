//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse für ViewOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ViewOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="viewFilterItem" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ViewOptions", propOrder = {
        "viewFilterItem"
})
public class ViewOptions {

    protected List<ViewFilterItem> viewFilterItem;

    /**
     * Gets the value of the viewFilterItem property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the viewFilterItem property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViewFilterItem().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ViewFilterItem }
     */
    public List<ViewFilterItem> getViewFilterItem() {
        if (viewFilterItem == null) {
            viewFilterItem = new ArrayList<ViewFilterItem>();
        }
        return this.viewFilterItem;
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
     *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ViewFilterItem {

        @XmlAttribute(name = "type")
        protected String type;
        @XmlAttribute(name = "enabled")
        protected Boolean enabled;

        /**
         * Ruft den Wert der type-Eigenschaft ab.
         *
         * @return possible object is
         * {@link String }
         */
        public String getType() {
            return type;
        }

        /**
         * Legt den Wert der type-Eigenschaft fest.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * Ruft den Wert der enabled-Eigenschaft ab.
         *
         * @return possible object is
         * {@link Boolean }
         */
        public Boolean isEnabled() {
            return enabled;
        }

        /**
         * Legt den Wert der enabled-Eigenschaft fest.
         *
         * @param value allowed object is
         *              {@link Boolean }
         */
        public void setEnabled(Boolean value) {
            this.enabled = value;
        }

    }

}
