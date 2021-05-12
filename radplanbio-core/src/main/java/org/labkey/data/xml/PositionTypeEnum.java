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
 * <p>Java-Klasse für PositionTypeEnum.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="PositionTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="top"/>
 *     &lt;enumeration value="bottom"/>
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="both"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "PositionTypeEnum")
@XmlEnum
public enum PositionTypeEnum {


    /**
     * Render only at the top of the grid.
     */
    @XmlEnumValue("top")
    TOP("top"),

    /**
     * Render only at the bottom of the grid.
     */
    @XmlEnumValue("bottom")
    BOTTOM("bottom"),

    /**
     * Do not render the button bar at all.
     */
    @XmlEnumValue("none")
    NONE("none"),

    /**
     * Render on both the top and bottom of the grid.
     */
    @XmlEnumValue("both")
    BOTH("both");
    private final String value;

    PositionTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PositionTypeEnum fromValue(String v) {
        for (PositionTypeEnum c : PositionTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
