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
 * <p>Java-Klasse für securityType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="securityType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BASIC_READ"/>
 *     &lt;enumeration value="BASIC_WRITE"/>
 *     &lt;enumeration value="ADVANCED_READ"/>
 *     &lt;enumeration value="ADVANCED_WRITE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "securityType")
@XmlEnum
public enum SecurityType {

    BASIC_READ,
    BASIC_WRITE,
    ADVANCED_READ,
    ADVANCED_WRITE;

    public String value() {
        return name();
    }

    public static SecurityType fromValue(String v) {
        return valueOf(v);
    }

}
