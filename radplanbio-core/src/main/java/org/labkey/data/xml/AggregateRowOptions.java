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


/**
 * Supported for SQL metadata
 *
 * <p>Java-Klasse für AggregateRowOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="AggregateRowOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="position" type="{http://labkey.org/data/xml}PositionTypeEnum" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AggregateRowOptions")
public class AggregateRowOptions {

    @XmlAttribute(name = "position")
    protected PositionTypeEnum position;

    /**
     * Ruft den Wert der position-Eigenschaft ab.
     *
     * @return possible object is
     * {@link PositionTypeEnum }
     */
    public PositionTypeEnum getPosition() {
        return position;
    }

    /**
     * Legt den Wert der position-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link PositionTypeEnum }
     */
    public void setPosition(PositionTypeEnum value) {
        this.position = value;
    }

}
