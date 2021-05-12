//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.27 um 12:07:57 PM CEST 
//


package org.labkey.study.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.labkey.data.xml.reportprops.PropertyList;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="categories" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="datasets1" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="dataset">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;all>
 *                             &lt;element name="tags" type="{http://labkey.org/data/xml/reportProps}propertyList" minOccurs="0"/>
 *                           &lt;/all>
 *                           &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="cohort" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="showByDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                           &lt;attribute name="demographicData" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="tag" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="useTimeKeyField" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *       &lt;attribute name="defaultDateFormat" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defaultNumberFormat" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="metaDataFile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "datasets")
public class Datasets {

    protected Categories categories;
    protected Datasets1 datasets1;
    @XmlAttribute(name = "defaultDateFormat")
    protected String defaultDateFormat;
    @XmlAttribute(name = "defaultNumberFormat")
    protected String defaultNumberFormat;
    @XmlAttribute(name = "metaDataFile")
    protected String metaDataFile;

    /**
     * Ruft den Wert der categories-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Categories }
     *     
     */
    public Categories getCategories() {
        return categories;
    }

    /**
     * Legt den Wert der categories-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Categories }
     *     
     */
    public void setCategories(Categories value) {
        this.categories = value;
    }

    /**
     * Ruft den Wert der datasets1-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Datasets1 }
     *     
     */
    public Datasets1 getDatasets1() {
        return datasets1;
    }

    /**
     * Legt den Wert der datasets1-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Datasets1 }
     *     
     */
    public void setDatasets1(Datasets1 value) {
        this.datasets1 = value;
    }

    /**
     * Ruft den Wert der defaultDateFormat-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    /**
     * Legt den Wert der defaultDateFormat-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultDateFormat(String value) {
        this.defaultDateFormat = value;
    }

    /**
     * Ruft den Wert der defaultNumberFormat-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultNumberFormat() {
        return defaultNumberFormat;
    }

    /**
     * Legt den Wert der defaultNumberFormat-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultNumberFormat(String value) {
        this.defaultNumberFormat = value;
    }

    /**
     * Ruft den Wert der metaDataFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaDataFile() {
        return metaDataFile;
    }

    /**
     * Legt den Wert der metaDataFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaDataFile(String value) {
        this.metaDataFile = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "category"
    })
    public static class Categories {

        protected List<String> category;

        /**
         * Gets the value of the category property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the category property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCategory().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getCategory() {
            if (category == null) {
                category = new ArrayList<String>();
            }
            return this.category;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *         &lt;element name="dataset">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;all>
     *                   &lt;element name="tags" type="{http://labkey.org/data/xml/reportProps}propertyList" minOccurs="0"/>
     *                 &lt;/all>
     *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                 &lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="cohort" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="showByDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *                 &lt;attribute name="demographicData" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="tag" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="useTimeKeyField" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "dataset"
    })
    public static class Datasets1 {

        protected List<Dataset> dataset;

        /**
         * Gets the value of the dataset property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the dataset property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDataset().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Datasets1 .Dataset }
         * 
         * 
         */
        public List<Dataset> getDataset() {
            if (dataset == null) {
                dataset = new ArrayList<Dataset>();
            }
            return this.dataset;
        }


        /**
         * <p>Java-Klasse für anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;all>
         *         &lt;element name="tags" type="{http://labkey.org/data/xml/reportProps}propertyList" minOccurs="0"/>
         *       &lt;/all>
         *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
         *       &lt;attribute name="category" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="cohort" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="showByDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
         *       &lt;attribute name="demographicData" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
         *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="tag" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="useTimeKeyField" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
        public static class Dataset {

            protected PropertyList tags;
            @XmlAttribute(name = "name", required = true)
            protected String name;
            @XmlAttribute(name = "id", required = true)
            protected int id;
            @XmlAttribute(name = "category")
            protected String category;
            @XmlAttribute(name = "cohort")
            protected String cohort;
            @XmlAttribute(name = "showByDefault")
            protected Boolean showByDefault;
            @XmlAttribute(name = "demographicData")
            protected Boolean demographicData;
            @XmlAttribute(name = "type")
            protected String type;
            @XmlAttribute(name = "tag")
            protected String tag;
            @XmlAttribute(name = "useTimeKeyField")
            protected Boolean useTimeKeyField;

            /**
             * Ruft den Wert der tags-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link PropertyList }
             *     
             */
            public PropertyList getTags() {
                return tags;
            }

            /**
             * Legt den Wert der tags-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link PropertyList }
             *     
             */
            public void setTags(PropertyList value) {
                this.tags = value;
            }

            /**
             * Ruft den Wert der name-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Legt den Wert der name-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Ruft den Wert der id-Eigenschaft ab.
             * 
             */
            public int getId() {
                return id;
            }

            /**
             * Legt den Wert der id-Eigenschaft fest.
             * 
             */
            public void setId(int value) {
                this.id = value;
            }

            /**
             * Ruft den Wert der category-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCategory() {
                return category;
            }

            /**
             * Legt den Wert der category-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCategory(String value) {
                this.category = value;
            }

            /**
             * Ruft den Wert der cohort-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCohort() {
                return cohort;
            }

            /**
             * Legt den Wert der cohort-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCohort(String value) {
                this.cohort = value;
            }

            /**
             * Ruft den Wert der showByDefault-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public boolean isShowByDefault() {
                if (showByDefault == null) {
                    return true;
                } else {
                    return showByDefault;
                }
            }

            /**
             * Legt den Wert der showByDefault-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setShowByDefault(Boolean value) {
                this.showByDefault = value;
            }

            /**
             * Ruft den Wert der demographicData-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public boolean isDemographicData() {
                if (demographicData == null) {
                    return false;
                } else {
                    return demographicData;
                }
            }

            /**
             * Legt den Wert der demographicData-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setDemographicData(Boolean value) {
                this.demographicData = value;
            }

            /**
             * Ruft den Wert der type-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getType() {
                return type;
            }

            /**
             * Legt den Wert der type-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setType(String value) {
                this.type = value;
            }

            /**
             * Ruft den Wert der tag-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTag() {
                return tag;
            }

            /**
             * Legt den Wert der tag-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTag(String value) {
                this.tag = value;
            }

            /**
             * Ruft den Wert der useTimeKeyField-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isUseTimeKeyField() {
                return useTimeKeyField;
            }

            /**
             * Legt den Wert der useTimeKeyField-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setUseTimeKeyField(Boolean value) {
                this.useTimeKeyField = value;
            }

        }

    }

}
