//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.27 um 12:37:14 PM CEST 
//


package org.labkey.study.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Legacy specimen settings in study.xml are superseded by specimen_settings.xml (>= version 13.2).
 *             
 * 
 * <p>Java-Klasse für legacySpecimenSettingsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="legacySpecimenSettingsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="specimenWebPartGroupings" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="grouping">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                             &lt;element name="groupBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="repositoryType" type="{http://labkey.org/study/xml}specimenRepositoryType" />
 *       &lt;attribute name="allowReqLocRepository" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="allowReqLocClinic" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="allowReqLocSal" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="allowReqLocEndpoint" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "legacySpecimenSettingsType", propOrder = {
    "specimenWebPartGroupings"
})
@XmlSeeAlso({
    Study.Specimens.class
})
public class LegacySpecimenSettingsType {

    protected SpecimenWebPartGroupings specimenWebPartGroupings;
    @XmlAttribute(name = "repositoryType")
    protected SpecimenRepositoryType repositoryType;
    @XmlAttribute(name = "allowReqLocRepository")
    protected Boolean allowReqLocRepository;
    @XmlAttribute(name = "allowReqLocClinic")
    protected Boolean allowReqLocClinic;
    @XmlAttribute(name = "allowReqLocSal")
    protected Boolean allowReqLocSal;
    @XmlAttribute(name = "allowReqLocEndpoint")
    protected Boolean allowReqLocEndpoint;

    /**
     * Ruft den Wert der specimenWebPartGroupings-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SpecimenWebPartGroupings }
     *     
     */
    public SpecimenWebPartGroupings getSpecimenWebPartGroupings() {
        return specimenWebPartGroupings;
    }

    /**
     * Legt den Wert der specimenWebPartGroupings-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SpecimenWebPartGroupings }
     *     
     */
    public void setSpecimenWebPartGroupings(SpecimenWebPartGroupings value) {
        this.specimenWebPartGroupings = value;
    }

    /**
     * Ruft den Wert der repositoryType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SpecimenRepositoryType }
     *     
     */
    public SpecimenRepositoryType getRepositoryType() {
        return repositoryType;
    }

    /**
     * Legt den Wert der repositoryType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SpecimenRepositoryType }
     *     
     */
    public void setRepositoryType(SpecimenRepositoryType value) {
        this.repositoryType = value;
    }

    /**
     * Ruft den Wert der allowReqLocRepository-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowReqLocRepository() {
        return allowReqLocRepository;
    }

    /**
     * Legt den Wert der allowReqLocRepository-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowReqLocRepository(Boolean value) {
        this.allowReqLocRepository = value;
    }

    /**
     * Ruft den Wert der allowReqLocClinic-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowReqLocClinic() {
        return allowReqLocClinic;
    }

    /**
     * Legt den Wert der allowReqLocClinic-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowReqLocClinic(Boolean value) {
        this.allowReqLocClinic = value;
    }

    /**
     * Ruft den Wert der allowReqLocSal-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowReqLocSal() {
        return allowReqLocSal;
    }

    /**
     * Legt den Wert der allowReqLocSal-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowReqLocSal(Boolean value) {
        this.allowReqLocSal = value;
    }

    /**
     * Ruft den Wert der allowReqLocEndpoint-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowReqLocEndpoint() {
        return allowReqLocEndpoint;
    }

    /**
     * Legt den Wert der allowReqLocEndpoint-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowReqLocEndpoint(Boolean value) {
        this.allowReqLocEndpoint = value;
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
     *         &lt;element name="grouping">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *                   &lt;element name="groupBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
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
        "grouping"
    })
    public static class SpecimenWebPartGroupings {

        protected List<Grouping> grouping;

        /**
         * Gets the value of the grouping property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the grouping property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGrouping().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Grouping }
         * 
         * 
         */
        public List<Grouping> getGrouping() {
            if (grouping == null) {
                grouping = new ArrayList<Grouping>();
            }
            return this.grouping;
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
         *         &lt;element name="groupBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "groupBy"
        })
        public static class Grouping {

            protected List<String> groupBy;

            /**
             * Gets the value of the groupBy property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the groupBy property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getGroupBy().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getGroupBy() {
                if (groupBy == null) {
                    groupBy = new ArrayList<String>();
                }
                return this.groupBy;
            }

        }

    }

}
