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
 * <p>Java-Klasse für PermissionType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="PermissionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="READ"/>
 *     &lt;enumeration value="INSERT"/>
 *     &lt;enumeration value="UPDATE"/>
 *     &lt;enumeration value="DELETE"/>
 *     &lt;enumeration value="ADMIN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "PermissionType")
@XmlEnum
public enum PermissionType {


    /**
     * User must have read permission in the current container
     */
    READ,

    /**
     * User must have insert permission in the current container
     */
    INSERT,

    /**
     * User must have update permission in the current container
     */
    UPDATE,

    /**
     * User must have delete permission in the current container
     */
    DELETE,

    /**
     * User must have admin permission in the current container
     */
    ADMIN;

    public String value() {
        return name();
    }

    public static PermissionType fromValue(String v) {
        return valueOf(v);
    }

}
