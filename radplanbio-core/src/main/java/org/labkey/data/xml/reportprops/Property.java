//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.27 um 12:07:57 PM CEST 
//


package org.labkey.data.xml.reportprops;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://labkey.org/data/xml/reportProps}propDef"/>
 *         &lt;element ref="{http://labkey.org/data/xml/reportProps}propValue"/>
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
    "propDef",
    "propValue"
})
@XmlRootElement(name = "property")
public class Property {

    @XmlElement(required = true)
    protected PropDef propDef;
    @XmlElement(required = true)
    protected PropValue propValue;

    /**
     * Ruft den Wert der propDef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PropDef }
     *     
     */
    public PropDef getPropDef() {
        return propDef;
    }

    /**
     * Legt den Wert der propDef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PropDef }
     *     
     */
    public void setPropDef(PropDef value) {
        this.propDef = value;
    }

    /**
     * Ruft den Wert der propValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PropValue }
     *     
     */
    public PropValue getPropValue() {
        return propValue;
    }

    /**
     * Legt den Wert der propValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PropValue }
     *     
     */
    public void setPropValue(PropValue value) {
        this.propValue = value;
    }

}
