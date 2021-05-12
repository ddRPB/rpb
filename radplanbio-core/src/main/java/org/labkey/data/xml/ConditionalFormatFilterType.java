//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import org.labkey.data.xml.querycustomview.OperatorType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ConditionalFormatFilterType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ConditionalFormatFilterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="operator" type="{http://labkey.org/data/xml/queryCustomView}operatorType" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConditionalFormatFilterType")
public class ConditionalFormatFilterType {

    @XmlAttribute(name = "operator")
    protected OperatorType operator;
    @XmlAttribute(name = "value")
    protected String value;

    /**
     * Ruft den Wert der operator-Eigenschaft ab.
     *
     * @return possible object is
     * {@link OperatorType }
     */
    public OperatorType getOperator() {
        return operator;
    }

    /**
     * Legt den Wert der operator-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link OperatorType }
     */
    public void setOperator(OperatorType value) {
        this.operator = value;
    }

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

}
