//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für MultiTablesType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="MultiTablesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="tables" type="{http://labkey.org/data/xml}TablesType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiTablesType", propOrder = {
        "tables"
})
public class MultiTablesType {

    protected TablesType tables;

    /**
     * Ruft den Wert der tables-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TablesType }
     */
    public TablesType getTables() {
        return tables;
    }

    /**
     * Legt den Wert der tables-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TablesType }
     */
    public void setTables(TablesType value) {
        this.tables = value;
    }

}
