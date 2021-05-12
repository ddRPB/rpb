//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse für FkDisplayColumnNameType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="FkDisplayColumnNameType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="useRawValue" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FkDisplayColumnNameType", propOrder = {
        "value"
})
public class FkDisplayColumnNameType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "useRawValue")
    protected Boolean useRawValue;

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
     * Ruft den Wert der useRawValue-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public boolean isUseRawValue() {
        if (useRawValue == null) {
            return false;
        } else {
            return useRawValue;
        }
    }

    /**
     * Legt den Wert der useRawValue-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setUseRawValue(Boolean value) {
        this.useRawValue = value;
    }

}
