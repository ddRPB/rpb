//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse für OntologyType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="OntologyType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="refId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OntologyType", propOrder = {
        "value"
})
public class OntologyType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "refId")
    protected String refId;
    @XmlAttribute(name = "source")
    protected String source;

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
     * Ruft den Wert der refId-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRefId() {
        return refId;
    }

    /**
     * Legt den Wert der refId-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRefId(String value) {
        this.refId = value;
    }

    /**
     * Ruft den Wert der source-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSource() {
        return source;
    }

    /**
     * Legt den Wert der source-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSource(String value) {
        this.source = value;
    }

}
