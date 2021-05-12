//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.20 um 04:00:36 PM CEST 
//


package org.labkey.data.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * Supported for SQL metadata
 *
 * <p>Java-Klasse für ButtonBarOptions complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ButtonBarOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="includeScript" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="onRender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="item" type="{http://labkey.org/data/xml}ButtonBarItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="position" type="{http://labkey.org/data/xml}PositionTypeEnum" />
 *       &lt;attribute name="includeStandardButtons" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="alwaysShowRecordSelectors" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ButtonBarOptions", propOrder = {
        "includeScript",
        "onRender",
        "item"
})
public class ButtonBarOptions {

    protected List<String> includeScript;
    protected String onRender;
    protected List<ButtonBarItem> item;
    @XmlAttribute(name = "position")
    protected PositionTypeEnum position;
    @XmlAttribute(name = "includeStandardButtons")
    protected Boolean includeStandardButtons;
    @XmlAttribute(name = "alwaysShowRecordSelectors")
    protected Boolean alwaysShowRecordSelectors;

    /**
     * Gets the value of the includeScript property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includeScript property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludeScript().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getIncludeScript() {
        if (includeScript == null) {
            includeScript = new ArrayList<String>();
        }
        return this.includeScript;
    }

    /**
     * Ruft den Wert der onRender-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOnRender() {
        return onRender;
    }

    /**
     * Legt den Wert der onRender-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOnRender(String value) {
        this.onRender = value;
    }

    /**
     * Gets the value of the item property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the item property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItem().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ButtonBarItem }
     */
    public List<ButtonBarItem> getItem() {
        if (item == null) {
            item = new ArrayList<ButtonBarItem>();
        }
        return this.item;
    }

    /**
     * Ruft den Wert der position-Eigenschaft ab.
     *
     * @return possible object is
     * {@link PositionTypeEnum }
     */
    public PositionTypeEnum getPosition() {
        return position;
    }

    /**
     * Legt den Wert der position-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link PositionTypeEnum }
     */
    public void setPosition(PositionTypeEnum value) {
        this.position = value;
    }

    /**
     * Ruft den Wert der includeStandardButtons-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isIncludeStandardButtons() {
        return includeStandardButtons;
    }

    /**
     * Legt den Wert der includeStandardButtons-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIncludeStandardButtons(Boolean value) {
        this.includeStandardButtons = value;
    }

    /**
     * Ruft den Wert der alwaysShowRecordSelectors-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isAlwaysShowRecordSelectors() {
        return alwaysShowRecordSelectors;
    }

    /**
     * Legt den Wert der alwaysShowRecordSelectors-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setAlwaysShowRecordSelectors(Boolean value) {
        this.alwaysShowRecordSelectors = value;
    }

}
