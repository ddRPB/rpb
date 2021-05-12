//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import org.labkey.data.xml.querycustomview.NamedFiltersType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse für TablesType complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="TablesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="sharedConfig" type="{http://labkey.org/data/xml}SharedConfigType" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="schemaCustomizer" type="{http://labkey.org/data/xml}SchemaCustomizerType"/>
 *         &lt;element name="table" type="{http://labkey.org/data/xml}TableType"/>
 *         &lt;element name="filters" type="{http://labkey.org/data/xml/queryCustomView}namedFiltersType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TablesType", propOrder = {
        "sharedConfigOrDescriptionOrSchemaCustomizer"
})
@XmlRootElement(name = "tables", namespace = "http://labkey.org/data/xml")
public class TablesType {

    @XmlElements({
            @XmlElement(name = "sharedConfig", type = SharedConfigType.class),
            @XmlElement(name = "description", type = String.class),
            @XmlElement(name = "schemaCustomizer", type = SchemaCustomizerType.class),
            @XmlElement(name = "table", type = TableType.class),
            @XmlElement(name = "filters", type = NamedFiltersType.class)
    })
    protected List<Object> sharedConfigOrDescriptionOrSchemaCustomizer;

    /**
     * Gets the value of the sharedConfigOrDescriptionOrSchemaCustomizer property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sharedConfigOrDescriptionOrSchemaCustomizer property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSharedConfigOrDescriptionOrSchemaCustomizer().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SharedConfigType }
     * {@link String }
     * {@link SchemaCustomizerType }
     * {@link TableType }
     * {@link NamedFiltersType }
     */
    public List<Object> getSharedConfigOrDescriptionOrSchemaCustomizer() {
        if (sharedConfigOrDescriptionOrSchemaCustomizer == null) {
            sharedConfigOrDescriptionOrSchemaCustomizer = new ArrayList<Object>();
        }
        return this.sharedConfigOrDescriptionOrSchemaCustomizer;
    }

    public void setSharedConfigOrDescriptionOrSchemaCustomizer(List<Object> objectList) {
        this.sharedConfigOrDescriptionOrSchemaCustomizer = (List<Object>) objectList;
    }
}
