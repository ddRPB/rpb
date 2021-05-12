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
 * <p>Java-Klasse für DefaultValueEnumType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="DefaultValueEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FIXED_EDITABLE"/>
 *     &lt;enumeration value="FIXED_NON_EDITABLE"/>
 *     &lt;enumeration value="LAST_ENTERED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "DefaultValueEnumType")
@XmlEnum
public enum DefaultValueEnumType {

    FIXED_EDITABLE,
    FIXED_NON_EDITABLE,
    LAST_ENTERED;

    public String value() {
        return name();
    }

    public static DefaultValueEnumType fromValue(String v) {
        return valueOf(v);
    }

}
