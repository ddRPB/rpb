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
 * <p>Java-Klasse für ButtonMenuItem complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ButtonMenuItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="target" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="onClick" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="item" type="{http://labkey.org/data/xml}ButtonMenuItem" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *       &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ButtonMenuItem", propOrder = {
        "target",
        "onClick",
        "item"
})
public class ButtonMenuItem {

    protected String target;
    protected String onClick;
    protected List<ButtonMenuItem> item;
    @XmlAttribute(name = "text")
    protected String text;
    @XmlAttribute(name = "icon")
    protected String icon;

    /**
     * Ruft den Wert der target-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTarget() {
        return target;
    }

    /**
     * Legt den Wert der target-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Ruft den Wert der onClick-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOnClick() {
        return onClick;
    }

    /**
     * Legt den Wert der onClick-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOnClick(String value) {
        this.onClick = value;
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
     * {@link ButtonMenuItem }
     */
    public List<ButtonMenuItem> getItem() {
        if (item == null) {
            item = new ArrayList<ButtonMenuItem>();
        }
        return this.item;
    }

    /**
     * Ruft den Wert der text-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getText() {
        return text;
    }

    /**
     * Legt den Wert der text-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Ruft den Wert der icon-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Legt den Wert der icon-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setIcon(String value) {
        this.icon = value;
    }

}
