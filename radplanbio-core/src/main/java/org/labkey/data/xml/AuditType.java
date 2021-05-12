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
 * <p>Java-Klasse für AuditType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="AuditType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="DETAILED"/>
 *     &lt;enumeration value="SUMMARY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "AuditType")
@XmlEnum
public enum AuditType {


    /**
     * No audit logging is performed.
     */
    NONE,

    /**
     * Differences between the new and old row data is logged.
     */
    DETAILED,

    /**
     * Minimal logging is performed.
     */
    SUMMARY;

    public String value() {
        return name();
    }

    public static AuditType fromValue(String v) {
        return valueOf(v);
    }

}
