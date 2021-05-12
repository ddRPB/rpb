//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.28 um 06:08:49 PM CEST 
//


package org.labkey.study.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "visit",
    "importAliases",
    "visitTag"
})
@XmlRootElement(name = "visitMap")
public class VisitMap {

    protected List<Visit> visit;
    protected ImportAliases importAliases;
    protected List<VisitTag> visitTag;

    public List<Visit> getVisit() {
        if (visit == null) {
            visit = new ArrayList<Visit>();
        }
        return this.visit;
    }

    public void setVisit(List<Visit> visitList) {
        this.visit = visitList;
    }

    public ImportAliases getImportAliases() {
        return importAliases;
    }

    /**
     * Legt den Wert der importAliases-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link ImportAliases }
     *     
     */
    public void setImportAliases(ImportAliases value) {
        this.importAliases = value;
    }


    public List<VisitTag> getVisitTag() {
        if (visitTag == null) {
            visitTag = new ArrayList<VisitTag>();
        }
        return this.visitTag;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "alias"
    })
    public static class ImportAliases {

        @XmlElement(required = true)
        protected List<Alias> alias;

        /**
         * Gets the value of the alias property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the alias property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAlias().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Alias }
         * 
         * 
         */
        public List<Alias> getAlias() {
            if (alias == null) {
                alias = new ArrayList<Alias>();
            }
            return this.alias;
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
         *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="sequenceNum" type="{http://www.w3.org/2001/XMLSchema}double" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Alias {

            @XmlAttribute(name = "name")
            protected String name;
            @XmlAttribute(name = "sequenceNum")
            protected Double sequenceNum;

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
             * Ruft den Wert der sequenceNum-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link Double }
             *     
             */
            public Double getSequenceNum() {
                return sequenceNum;
            }

            /**
             * Legt den Wert der sequenceNum-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link Double }
             *     
             */
            public void setSequenceNum(Double value) {
                this.sequenceNum = value;
            }

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
     *       &lt;all minOccurs="0">
     *         &lt;element name="datasets" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *                   &lt;element name="dataset">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                           &lt;attribute name="type" type="{http://labkey.org/study/xml}datasetType" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="visitTags" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
     *                   &lt;element name="visitTag">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="cohort" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/all>
     *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="sequenceNum" type="{http://www.w3.org/2001/XMLSchema}double" />
     *       &lt;attribute name="maxSequenceNum" type="{http://www.w3.org/2001/XMLSchema}double" />
     *       &lt;attribute name="protocolDay" type="{http://www.w3.org/2001/XMLSchema}double" />
     *       &lt;attribute name="displayOrder" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="chronologicalOrder" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="cohort" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="typeCode" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="showByDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
     *       &lt;attribute name="visitDateDatasetId" type="{http://www.w3.org/2001/XMLSchema}int" default="-1" />
     *       &lt;attribute name="sequenceNumHandling" type="{http://www.w3.org/2001/XMLSchema}string" default="normal" />
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
    public static class Visit {

        protected Datasets datasets;
        protected VisitTags visitTags;
        protected String description;
        @XmlAttribute(name = "label")
        protected String label;
        @XmlAttribute(name = "sequenceNum")
        protected Double sequenceNum;
        @XmlAttribute(name = "maxSequenceNum")
        protected Double maxSequenceNum;
        @XmlAttribute(name = "protocolDay")
        protected Double protocolDay;
        @XmlAttribute(name = "displayOrder")
        protected Integer displayOrder;
        @XmlAttribute(name = "chronologicalOrder")
        protected Integer chronologicalOrder;
        @XmlAttribute(name = "cohort")
        protected String cohort;
        @XmlAttribute(name = "typeCode")
        protected String typeCode;
        @XmlAttribute(name = "showByDefault")
        protected Boolean showByDefault;
        @XmlAttribute(name = "visitDateDatasetId")
        protected Integer visitDateDatasetId;
        @XmlAttribute(name = "sequenceNumHandling")
        protected String sequenceNumHandling;

        /**
         * Ruft den Wert der datasets-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Datasets }
         *     
         */
        public Datasets getDatasets() {
            return datasets;
        }

        /**
         * Legt den Wert der datasets-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Datasets }
         *     
         */
        public void setDatasets(Datasets value) {
            this.datasets = value;
        }

        /**
         * Ruft den Wert der visitTags-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link VisitTags }
         *     
         */
        public VisitTags getVisitTags() {
            return visitTags;
        }

        /**
         * Legt den Wert der visitTags-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link VisitTags }
         *     
         */
        public void setVisitTags(VisitTags value) {
            this.visitTags = value;
        }

        /**
         * Ruft den Wert der description-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Legt den Wert der description-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

        /**
         * Ruft den Wert der label-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLabel() {
            return label;
        }

        /**
         * Legt den Wert der label-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLabel(String value) {
            this.label = value;
        }

        /**
         * Ruft den Wert der sequenceNum-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Double }
         *     
         */
        public Double getSequenceNum() {
            return sequenceNum;
        }

        /**
         * Legt den Wert der sequenceNum-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Double }
         *     
         */
        public void setSequenceNum(Double value) {
            this.sequenceNum = value;
        }

        /**
         * Ruft den Wert der maxSequenceNum-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Double }
         *     
         */
        public Double getMaxSequenceNum() {
            return maxSequenceNum;
        }

        /**
         * Legt den Wert der maxSequenceNum-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Double }
         *     
         */
        public void setMaxSequenceNum(Double value) {
            this.maxSequenceNum = value;
        }

        /**
         * Ruft den Wert der protocolDay-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Double }
         *     
         */
        public Double getProtocolDay() {
            return protocolDay;
        }

        /**
         * Legt den Wert der protocolDay-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Double }
         *     
         */
        public void setProtocolDay(Double value) {
            this.protocolDay = value;
        }

        /**
         * Ruft den Wert der displayOrder-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getDisplayOrder() {
            return displayOrder;
        }

        /**
         * Legt den Wert der displayOrder-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setDisplayOrder(Integer value) {
            this.displayOrder = value;
        }

        /**
         * Ruft den Wert der chronologicalOrder-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getChronologicalOrder() {
            return chronologicalOrder;
        }

        /**
         * Legt den Wert der chronologicalOrder-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setChronologicalOrder(Integer value) {
            this.chronologicalOrder = value;
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
         * Ruft den Wert der typeCode-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTypeCode() {
            return typeCode;
        }

        /**
         * Legt den Wert der typeCode-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTypeCode(String value) {
            this.typeCode = value;
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
         * Ruft den Wert der visitDateDatasetId-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public int getVisitDateDatasetId() {
            if (visitDateDatasetId == null) {
                return -1;
            } else {
                return visitDateDatasetId;
            }
        }

        /**
         * Legt den Wert der visitDateDatasetId-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setVisitDateDatasetId(Integer value) {
            this.visitDateDatasetId = value;
        }

        /**
         * Ruft den Wert der sequenceNumHandling-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSequenceNumHandling() {
            if (sequenceNumHandling == null) {
                return "normal";
            } else {
                return sequenceNumHandling;
            }
        }

        /**
         * Legt den Wert der sequenceNumHandling-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSequenceNumHandling(String value) {
            this.sequenceNumHandling = value;
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
         *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                 &lt;attribute name="type" type="{http://labkey.org/study/xml}datasetType" />
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
        public static class Datasets {

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
             * {@link Dataset }
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
             *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
             *       &lt;attribute name="type" type="{http://labkey.org/study/xml}datasetType" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Dataset {

                @XmlAttribute(name = "id")
                protected Integer id;
                @XmlAttribute(name = "type")
                protected DatasetType type;

                /**
                 * Ruft den Wert der id-Eigenschaft ab.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Integer }
                 *     
                 */
                public Integer getId() {
                    return id;
                }

                /**
                 * Legt den Wert der id-Eigenschaft fest.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Integer }
                 *     
                 */
                public void setId(Integer value) {
                    this.id = value;
                }

                /**
                 * Ruft den Wert der type-Eigenschaft ab.
                 * 
                 * @return
                 *     possible object is
                 *     {@link DatasetType }
                 *     
                 */
                public DatasetType getType() {
                    return type;
                }

                /**
                 * Legt den Wert der type-Eigenschaft fest.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link DatasetType }
                 *     
                 */
                public void setType(DatasetType value) {
                    this.type = value;
                }

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
         *         &lt;element name="visitTag">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="cohort" type="{http://www.w3.org/2001/XMLSchema}string" />
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
            "visitTag"
        })
        public static class VisitTags {

            protected List<VisitTag> visitTag;

            /**
             * Gets the value of the visitTag property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the visitTag property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getVisitTag().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link VisitTag }
             * 
             * 
             */
            public List<VisitTag> getVisitTag() {
                if (visitTag == null) {
                    visitTag = new ArrayList<VisitTag>();
                }
                return this.visitTag;
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
             *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="cohort" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class VisitTag {

                @XmlAttribute(name = "name")
                protected String name;
                @XmlAttribute(name = "cohort")
                protected String cohort;

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

            }

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
     *       &lt;all minOccurs="0">
     *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/all>
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="caption" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="singleUse" type="{http://www.w3.org/2001/XMLSchema}boolean" />
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
    public static class VisitTag {

        protected String description;
        @XmlAttribute(name = "name")
        protected String name;
        @XmlAttribute(name = "caption")
        protected String caption;
        @XmlAttribute(name = "singleUse")
        protected Boolean singleUse;

        /**
         * Ruft den Wert der description-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Legt den Wert der description-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
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
         * Ruft den Wert der caption-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCaption() {
            return caption;
        }

        /**
         * Legt den Wert der caption-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCaption(String value) {
            this.caption = value;
        }

        /**
         * Ruft den Wert der singleUse-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSingleUse() {
            return singleUse;
        }

        /**
         * Legt den Wert der singleUse-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSingleUse(Boolean value) {
            this.singleUse = value;
        }

    }

}
