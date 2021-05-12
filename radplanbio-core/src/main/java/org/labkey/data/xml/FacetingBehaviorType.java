//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für FacetingBehaviorType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="FacetingBehaviorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AUTOMATIC"/>
 *     &lt;enumeration value="ALWAYS_ON"/>
 *     &lt;enumeration value="ALWAYS_OFF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "FacetingBehaviorType")
@XmlEnum
public enum FacetingBehaviorType {


    /**
     * If the column is a lookup, dimension or of type: (boolean, int, date, or text)
     * then it will be considered for faceted filtering.
     */
    AUTOMATIC,

    /**
     * The column is always a candidate for faceted filtering.
     */
    ALWAYS_ON,

    /**
     * The column is never a candidate for faceted filtering.
     */
    ALWAYS_OFF;

    public String value() {
        return name();
    }

    public static FacetingBehaviorType fromValue(String v) {
        return valueOf(v);
    }

}
