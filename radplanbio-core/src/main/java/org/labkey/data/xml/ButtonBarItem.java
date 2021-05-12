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
 * <p>Java-Klasse für ButtonBarItem complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ButtonBarItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="target" type="{http://labkey.org/data/xml}TargetType"/>
 *         &lt;element name="onClick" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="item" type="{http://labkey.org/data/xml}ButtonMenuItem" maxOccurs="unbounded"/>
 *         &lt;element name="originalText" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/choice>
 *       &lt;attribute name="permission" type="{http://labkey.org/data/xml}PermissionType" />
 *       &lt;attribute name="permissionClass" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="iconCls" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="hidden" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="requiresSelection" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="requiresSelectionMinCount" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="requiresSelectionMaxCount" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="insertPosition">
 *         &lt;simpleType>
 *           &lt;union memberTypes=" {http://www.w3.org/2001/XMLSchema}int">
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *                 &lt;enumeration value="beginning"/>
 *                 &lt;enumeration value="end"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/union>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="insertBefore" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="insertAfter" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="suppressWarning" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ButtonBarItem", propOrder = {
        "target",
        "onClick",
        "item",
        "originalText"
})
public class ButtonBarItem {

    protected TargetType target;
    protected String onClick;
    protected List<ButtonMenuItem> item;
    protected String originalText;
    @XmlAttribute(name = "permission")
    protected PermissionType permission;
    @XmlAttribute(name = "permissionClass")
    protected String permissionClass;
    @XmlAttribute(name = "text")
    protected String text;
    @XmlAttribute(name = "iconCls")
    protected String iconCls;
    @XmlAttribute(name = "hidden")
    protected Boolean hidden;
    @XmlAttribute(name = "requiresSelection")
    protected Boolean requiresSelection;
    @XmlAttribute(name = "requiresSelectionMinCount")
    protected Integer requiresSelectionMinCount;
    @XmlAttribute(name = "requiresSelectionMaxCount")
    protected Integer requiresSelectionMaxCount;
    @XmlAttribute(name = "insertPosition")
    protected String insertPosition;
    @XmlAttribute(name = "insertBefore")
    protected String insertBefore;
    @XmlAttribute(name = "insertAfter")
    protected String insertAfter;
    @XmlAttribute(name = "suppressWarning")
    protected Boolean suppressWarning;

    /**
     * Ruft den Wert der target-Eigenschaft ab.
     *
     * @return possible object is
     * {@link TargetType }
     */
    public TargetType getTarget() {
        return target;
    }

    /**
     * Legt den Wert der target-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link TargetType }
     */
    public void setTarget(TargetType value) {
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
     * Ruft den Wert der originalText-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * Legt den Wert der originalText-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOriginalText(String value) {
        this.originalText = value;
    }

    /**
     * Ruft den Wert der permission-Eigenschaft ab.
     *
     * @return possible object is
     * {@link PermissionType }
     */
    public PermissionType getPermission() {
        return permission;
    }

    /**
     * Legt den Wert der permission-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link PermissionType }
     */
    public void setPermission(PermissionType value) {
        this.permission = value;
    }

    /**
     * Ruft den Wert der permissionClass-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPermissionClass() {
        return permissionClass;
    }

    /**
     * Legt den Wert der permissionClass-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPermissionClass(String value) {
        this.permissionClass = value;
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
     * Ruft den Wert der iconCls-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getIconCls() {
        return iconCls;
    }

    /**
     * Legt den Wert der iconCls-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setIconCls(String value) {
        this.iconCls = value;
    }

    /**
     * Ruft den Wert der hidden-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isHidden() {
        return hidden;
    }

    /**
     * Legt den Wert der hidden-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setHidden(Boolean value) {
        this.hidden = value;
    }

    /**
     * Ruft den Wert der requiresSelection-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isRequiresSelection() {
        return requiresSelection;
    }

    /**
     * Legt den Wert der requiresSelection-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setRequiresSelection(Boolean value) {
        this.requiresSelection = value;
    }

    /**
     * Ruft den Wert der requiresSelectionMinCount-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getRequiresSelectionMinCount() {
        return requiresSelectionMinCount;
    }

    /**
     * Legt den Wert der requiresSelectionMinCount-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setRequiresSelectionMinCount(Integer value) {
        this.requiresSelectionMinCount = value;
    }

    /**
     * Ruft den Wert der requiresSelectionMaxCount-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getRequiresSelectionMaxCount() {
        return requiresSelectionMaxCount;
    }

    /**
     * Legt den Wert der requiresSelectionMaxCount-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setRequiresSelectionMaxCount(Integer value) {
        this.requiresSelectionMaxCount = value;
    }

    /**
     * Ruft den Wert der insertPosition-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsertPosition() {
        return insertPosition;
    }

    /**
     * Legt den Wert der insertPosition-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsertPosition(String value) {
        this.insertPosition = value;
    }

    /**
     * Ruft den Wert der insertBefore-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsertBefore() {
        return insertBefore;
    }

    /**
     * Legt den Wert der insertBefore-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsertBefore(String value) {
        this.insertBefore = value;
    }

    /**
     * Ruft den Wert der insertAfter-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getInsertAfter() {
        return insertAfter;
    }

    /**
     * Legt den Wert der insertAfter-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInsertAfter(String value) {
        this.insertAfter = value;
    }

    /**
     * Ruft den Wert der suppressWarning-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isSuppressWarning() {
        return suppressWarning;
    }

    /**
     * Legt den Wert der suppressWarning-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setSuppressWarning(Boolean value) {
        this.suppressWarning = value;
    }

}
