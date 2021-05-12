//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PHIType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="PHIType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NotPHI"/>
 *     &lt;enumeration value="Limited"/>
 *     &lt;enumeration value="PHI"/>
 *     &lt;enumeration value="Restricted"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "PHIType")
@XmlEnum
public enum PHIType {


    /**
     * Column does not contain protected health information
     */
    @XmlEnumValue("NotPHI")
    NOT_PHI("NotPHI"),

    /**
     * Column may include protected health information such as dates and zip codes
     */
    @XmlEnumValue("Limited")
    LIMITED("Limited"),

    /**
     * Column may include other protected health information
     */
    PHI("PHI"),

    /**
     * Column may include personal health information subject to additional privacy laws, such as drivers license or social security numbers
     */
    @XmlEnumValue("Restricted")
    RESTRICTED("Restricted");
    private final String value;

    PHIType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PHIType fromValue(String v) {
        for (PHIType c : PHIType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
