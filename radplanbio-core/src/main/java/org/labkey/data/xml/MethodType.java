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
 * <p>Java-Klasse für MethodType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="MethodType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LINK"/>
 *     &lt;enumeration value="GET"/>
 *     &lt;enumeration value="POST"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "MethodType")
@XmlEnum
public enum MethodType {


    /**
     * Rendered as a standard HTML link, with an <a> tag.
     */
    LINK,

    /**
     * Rendered to perform an HTTP GET form submission
     */
    GET,

    /**
     * Rendered to perform an HTTP POST form submission
     */
    POST;

    public String value() {
        return name();
    }

    public static MethodType fromValue(String v) {
        return valueOf(v);
    }

}
