//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml.querycustomview;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für operatorType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="operatorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="eq"/>
 *     &lt;enumeration value="dateeq"/>
 *     &lt;enumeration value="dateneq"/>
 *     &lt;enumeration value="datelt"/>
 *     &lt;enumeration value="datelte"/>
 *     &lt;enumeration value="dategt"/>
 *     &lt;enumeration value="dategte"/>
 *     &lt;enumeration value="neqornull"/>
 *     &lt;enumeration value="neq"/>
 *     &lt;enumeration value="isblank"/>
 *     &lt;enumeration value="isnonblank"/>
 *     &lt;enumeration value="gt"/>
 *     &lt;enumeration value="lt"/>
 *     &lt;enumeration value="gte"/>
 *     &lt;enumeration value="lte"/>
 *     &lt;enumeration value="between"/>
 *     &lt;enumeration value="notbetween"/>
 *     &lt;enumeration value="contains"/>
 *     &lt;enumeration value="doesnotcontain"/>
 *     &lt;enumeration value="doesnotstartwith"/>
 *     &lt;enumeration value="startswith"/>
 *     &lt;enumeration value="in"/>
 *     &lt;enumeration value="hasmvvalue"/>
 *     &lt;enumeration value="nomvvalue"/>
 *     &lt;enumeration value="inornull"/>
 *     &lt;enumeration value="notin"/>
 *     &lt;enumeration value="notinornull"/>
 *     &lt;enumeration value="containsoneof"/>
 *     &lt;enumeration value="containsnoneof"/>
 *     &lt;enumeration value="memberof"/>
 *     &lt;enumeration value="exp:childof"/>
 *     &lt;enumeration value="exp:parentof"/>
 *     &lt;enumeration value="q"/>
 *     &lt;enumeration value="where"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "operatorType")
@XmlEnum
public enum OperatorType {

    @XmlEnumValue("eq")
    EQ("eq"),
    @XmlEnumValue("dateeq")
    DATEEQ("dateeq"),
    @XmlEnumValue("dateneq")
    DATENEQ("dateneq"),
    @XmlEnumValue("datelt")
    DATELT("datelt"),
    @XmlEnumValue("datelte")
    DATELTE("datelte"),
    @XmlEnumValue("dategt")
    DATEGT("dategt"),
    @XmlEnumValue("dategte")
    DATEGTE("dategte"),
    @XmlEnumValue("neqornull")
    NEQORNULL("neqornull"),
    @XmlEnumValue("neq")
    NEQ("neq"),
    @XmlEnumValue("isblank")
    ISBLANK("isblank"),
    @XmlEnumValue("isnonblank")
    ISNONBLANK("isnonblank"),
    @XmlEnumValue("gt")
    GT("gt"),
    @XmlEnumValue("lt")
    LT("lt"),
    @XmlEnumValue("gte")
    GTE("gte"),
    @XmlEnumValue("lte")
    LTE("lte"),
    @XmlEnumValue("between")
    BETWEEN("between"),
    @XmlEnumValue("notbetween")
    NOTBETWEEN("notbetween"),
    @XmlEnumValue("contains")
    CONTAINS("contains"),
    @XmlEnumValue("doesnotcontain")
    DOESNOTCONTAIN("doesnotcontain"),
    @XmlEnumValue("doesnotstartwith")
    DOESNOTSTARTWITH("doesnotstartwith"),
    @XmlEnumValue("startswith")
    STARTSWITH("startswith"),
    @XmlEnumValue("in")
    IN("in"),
    @XmlEnumValue("hasmvvalue")
    HASMVVALUE("hasmvvalue"),
    @XmlEnumValue("nomvvalue")
    NOMVVALUE("nomvvalue"),
    @XmlEnumValue("inornull")
    INORNULL("inornull"),
    @XmlEnumValue("notin")
    NOTIN("notin"),
    @XmlEnumValue("notinornull")
    NOTINORNULL("notinornull"),
    @XmlEnumValue("containsoneof")
    CONTAINSONEOF("containsoneof"),
    @XmlEnumValue("containsnoneof")
    CONTAINSNONEOF("containsnoneof"),
    @XmlEnumValue("memberof")
    MEMBEROF("memberof"),
    @XmlEnumValue("exp:childof")
    EXP_CHILDOF("exp:childof"),
    @XmlEnumValue("exp:parentof")
    EXP_PARENTOF("exp:parentof"),
    @XmlEnumValue("q")
    Q("q"),
    @XmlEnumValue("where")
    WHERE("where");
    private final String value;

    OperatorType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OperatorType fromValue(String v) {
        for (OperatorType c : OperatorType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
