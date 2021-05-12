//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.27 um 12:37:14 PM CEST 
//


package org.labkey.study.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für timepointType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="timepointType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VISIT"/>
 *     &lt;enumeration value="DATE"/>
 *     &lt;enumeration value="CONTINUOUS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "timepointType")
@XmlEnum
public enum TimepointType {

    VISIT,
    DATE,
    CONTINUOUS;

    public String value() {
        return name();
    }

    public static TimepointType fromValue(String v) {
        return valueOf(v);
    }

}
