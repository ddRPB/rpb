//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse für TargetType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="TargetType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="method" type="{http://labkey.org/data/xml}MethodType" default="LINK" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetType", propOrder = {
        "value"
})
public class TargetType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "method")
    protected MethodType method;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Ruft den Wert der method-Eigenschaft ab.
     *
     * @return possible object is
     * {@link MethodType }
     */
    public MethodType getMethod() {
        if (method == null) {
            return MethodType.LINK;
        } else {
            return method;
        }
    }

    /**
     * Legt den Wert der method-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link MethodType }
     */
    public void setMethod(MethodType value) {
        this.method = value;
    }

}
