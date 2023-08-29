//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.27 um 12:37:14 PM CEST 
//


package org.labkey.study.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;


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
 *         &lt;element name="visits" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="qcStates" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="showPrivateDataByDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="cohorts" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="mode" type="{http://labkey.org/study/xml}cohortMode" />
 *                 &lt;attribute name="type" type="{http://labkey.org/study/xml}cohortType" />
 *                 &lt;attribute name="datasetId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="datasetProperty" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="participantAliasDataset" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="datasetId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="sourceProperty" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="aliasProperty" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="datasets" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="schema" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="labelColumn" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="typeNameColumn" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="typeIdColumn" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="definition">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/all>
 *                 &lt;attribute name="dir" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="specimens" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://labkey.org/study/xml}legacySpecimenSettingsType">
 *                 &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="dir" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="settings" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="studyViews" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="dir" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="settings" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="reports" type="{http://labkey.org/study/xml}exportDirType" minOccurs="0"/>
 *         &lt;element name="queries" type="{http://labkey.org/study/xml}exportDirType" minOccurs="0"/>
 *         &lt;element name="views" type="{http://labkey.org/study/xml}exportDirType" minOccurs="0"/>
 *         &lt;element name="lists" type="{http://labkey.org/study/xml}exportDirType" minOccurs="0"/>
 *         &lt;element name="missingValueIndicators" type="{http://labkey.org/study/xml}missingValueIndicatorsType" minOccurs="0"/>
 *         &lt;element name="comments" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="participantCommentDatasetId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="participantCommentDatasetProperty" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="participantVisitCommentDatasetId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="participantVisitCommentDatasetProperty" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="protocolDocs" type="{http://labkey.org/study/xml}exportDirType" minOccurs="0"/>
 *         &lt;element name="studyDescription" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="rendererType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="assayPlan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="treatmentData" type="{http://labkey.org/study/xml}exportDirType" minOccurs="0"/>
 *         &lt;element name="assaySchedule" type="{http://labkey.org/study/xml}exportDirType" minOccurs="0"/>
 *         &lt;element name="properties" type="{http://labkey.org/study/xml}exportDirType" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="allowDataspace" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="importDelay" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="archiveVersion" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="subjectNounSingular" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="subjectNounPlural" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="subjectColumnName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dateBased" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="timepointType" type="{http://labkey.org/study/xml}timepointType" />
 *       &lt;attribute name="startDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="endDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="defaultTimepointDuration" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="securityType" type="{http://labkey.org/study/xml}securityType" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="descriptionRendererType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="investigator" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="grant" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="species" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="alternateIdPrefix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="alternateIdDigits" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;anyAttribute processContents='lax'/>
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
@XmlRootElement(name = "study", namespace = "http://labkey.org/study/xml")
public class Study {

    protected Visits visits;
    protected QcStates qcStates;
    protected Cohorts cohorts;
    protected ParticipantAliasDataset participantAliasDataset;
    protected Datasets datasets;
    protected Specimens specimens;
    protected StudyViews studyViews;
    protected ExportDirType reports;
    protected ExportDirType queries;
    protected ExportDirType views;
    protected ExportDirType lists;
    protected MissingValueIndicatorsType missingValueIndicators;
    protected Comments comments;
    protected ExportDirType protocolDocs;
    protected StudyDescription studyDescription;
    protected String assayPlan;
    protected ExportDirType treatmentData;
    protected ExportDirType assaySchedule;
    protected ExportDirType properties;
    @XmlAttribute(name = "allowDataspace")
    protected Boolean allowDataspace;
    @XmlAttribute(name = "importDelay")
    protected Integer importDelay;
    @XmlAttribute(name = "archiveVersion")
    protected Double archiveVersion;
    @XmlAttribute(name = "label")
    protected String label;
    @XmlAttribute(name = "subjectNounSingular")
    protected String subjectNounSingular;
    @XmlAttribute(name = "subjectNounPlural")
    protected String subjectNounPlural;
    @XmlAttribute(name = "subjectColumnName")
    protected String subjectColumnName;
    @XmlAttribute(name = "dateBased")
    protected Boolean dateBased;
    @XmlAttribute(name = "timepointType")
    protected TimepointType timepointType;
    @XmlAttribute(name = "startDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar startDate;
    @XmlAttribute(name = "endDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endDate;
    @XmlAttribute(name = "defaultTimepointDuration")
    protected Integer defaultTimepointDuration;
    @XmlAttribute(name = "securityType")
    protected SecurityType securityType;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "descriptionRendererType")
    protected String descriptionRendererType;
    @XmlAttribute(name = "investigator")
    protected String investigator;
    @XmlAttribute(name = "grant")
    protected String grant;
    @XmlAttribute(name = "species")
    protected String species;
    @XmlAttribute(name = "alternateIdPrefix")
    protected String alternateIdPrefix;
    @XmlAttribute(name = "alternateIdDigits")
    protected Integer alternateIdDigits;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Ruft den Wert der visits-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Visits }
     *     
     */
    public Visits getVisits() {
        return visits;
    }

    /**
     * Legt den Wert der visits-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Visits }
     *     
     */
    public void setVisits(Visits value) {
        this.visits = value;
    }

    /**
     * Ruft den Wert der qcStates-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QcStates }
     *     
     */
    public QcStates getQcStates() {
        return qcStates;
    }

    /**
     * Legt den Wert der qcStates-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QcStates }
     *     
     */
    public void setQcStates(QcStates value) {
        this.qcStates = value;
    }

    /**
     * Ruft den Wert der cohorts-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Cohorts }
     *     
     */
    public Cohorts getCohorts() {
        return cohorts;
    }

    /**
     * Legt den Wert der cohorts-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Cohorts }
     *     
     */
    public void setCohorts(Cohorts value) {
        this.cohorts = value;
    }

    /**
     * Ruft den Wert der participantAliasDataset-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantAliasDataset }
     *     
     */
    public ParticipantAliasDataset getParticipantAliasDataset() {
        return participantAliasDataset;
    }

    /**
     * Legt den Wert der participantAliasDataset-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantAliasDataset }
     *     
     */
    public void setParticipantAliasDataset(ParticipantAliasDataset value) {
        this.participantAliasDataset = value;
    }

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
     * Ruft den Wert der specimens-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Specimens }
     *     
     */
    public Specimens getSpecimens() {
        return specimens;
    }

    /**
     * Legt den Wert der specimens-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Specimens }
     *     
     */
    public void setSpecimens(Specimens value) {
        this.specimens = value;
    }

    /**
     * Ruft den Wert der studyViews-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link StudyViews }
     *     
     */
    public StudyViews getStudyViews() {
        return studyViews;
    }

    /**
     * Legt den Wert der studyViews-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link StudyViews }
     *     
     */
    public void setStudyViews(StudyViews value) {
        this.studyViews = value;
    }

    /**
     * Ruft den Wert der reports-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExportDirType }
     *     
     */
    public ExportDirType getReports() {
        return reports;
    }

    /**
     * Legt den Wert der reports-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDirType }
     *     
     */
    public void setReports(ExportDirType value) {
        this.reports = value;
    }

    /**
     * Ruft den Wert der queries-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExportDirType }
     *     
     */
    public ExportDirType getQueries() {
        return queries;
    }

    /**
     * Legt den Wert der queries-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDirType }
     *     
     */
    public void setQueries(ExportDirType value) {
        this.queries = value;
    }

    /**
     * Ruft den Wert der views-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExportDirType }
     *     
     */
    public ExportDirType getViews() {
        return views;
    }

    /**
     * Legt den Wert der views-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDirType }
     *     
     */
    public void setViews(ExportDirType value) {
        this.views = value;
    }

    /**
     * Ruft den Wert der lists-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExportDirType }
     *     
     */
    public ExportDirType getLists() {
        return lists;
    }

    /**
     * Legt den Wert der lists-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDirType }
     *     
     */
    public void setLists(ExportDirType value) {
        this.lists = value;
    }

    /**
     * Ruft den Wert der missingValueIndicators-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MissingValueIndicatorsType }
     *     
     */
    public MissingValueIndicatorsType getMissingValueIndicators() {
        return missingValueIndicators;
    }

    /**
     * Legt den Wert der missingValueIndicators-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MissingValueIndicatorsType }
     *     
     */
    public void setMissingValueIndicators(MissingValueIndicatorsType value) {
        this.missingValueIndicators = value;
    }

    /**
     * Ruft den Wert der comments-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Comments }
     *     
     */
    public Comments getComments() {
        return comments;
    }

    /**
     * Legt den Wert der comments-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Comments }
     *     
     */
    public void setComments(Comments value) {
        this.comments = value;
    }

    /**
     * Ruft den Wert der protocolDocs-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExportDirType }
     *     
     */
    public ExportDirType getProtocolDocs() {
        return protocolDocs;
    }

    /**
     * Legt den Wert der protocolDocs-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDirType }
     *     
     */
    public void setProtocolDocs(ExportDirType value) {
        this.protocolDocs = value;
    }

    /**
     * Ruft den Wert der studyDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link StudyDescription }
     *     
     */
    public StudyDescription getStudyDescription() {
        return studyDescription;
    }

    /**
     * Legt den Wert der studyDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link StudyDescription }
     *     
     */
    public void setStudyDescription(StudyDescription value) {
        this.studyDescription = value;
    }

    /**
     * Ruft den Wert der assayPlan-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssayPlan() {
        return assayPlan;
    }

    /**
     * Legt den Wert der assayPlan-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssayPlan(String value) {
        this.assayPlan = value;
    }

    /**
     * Ruft den Wert der treatmentData-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExportDirType }
     *     
     */
    public ExportDirType getTreatmentData() {
        return treatmentData;
    }

    /**
     * Legt den Wert der treatmentData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDirType }
     *     
     */
    public void setTreatmentData(ExportDirType value) {
        this.treatmentData = value;
    }

    /**
     * Ruft den Wert der assaySchedule-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExportDirType }
     *     
     */
    public ExportDirType getAssaySchedule() {
        return assaySchedule;
    }

    /**
     * Legt den Wert der assaySchedule-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDirType }
     *     
     */
    public void setAssaySchedule(ExportDirType value) {
        this.assaySchedule = value;
    }

    /**
     * Ruft den Wert der properties-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExportDirType }
     *     
     */
    public ExportDirType getProperties() {
        return properties;
    }

    /**
     * Legt den Wert der properties-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDirType }
     *     
     */
    public void setProperties(ExportDirType value) {
        this.properties = value;
    }

    /**
     * Ruft den Wert der allowDataspace-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isAllowDataspace() {
        if (allowDataspace == null) {
            return false;
        } else {
            return allowDataspace;
        }
    }

    /**
     * Legt den Wert der allowDataspace-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowDataspace(Boolean value) {
        this.allowDataspace = value;
    }

    /**
     * Ruft den Wert der importDelay-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getImportDelay() {
        return importDelay;
    }

    /**
     * Legt den Wert der importDelay-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setImportDelay(Integer value) {
        this.importDelay = value;
    }

    /**
     * Ruft den Wert der archiveVersion-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getArchiveVersion() {
        return archiveVersion;
    }

    /**
     * Legt den Wert der archiveVersion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setArchiveVersion(Double value) {
        this.archiveVersion = value;
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
     * Ruft den Wert der subjectNounSingular-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubjectNounSingular() {
        return subjectNounSingular;
    }

    /**
     * Legt den Wert der subjectNounSingular-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubjectNounSingular(String value) {
        this.subjectNounSingular = value;
    }

    /**
     * Ruft den Wert der subjectNounPlural-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubjectNounPlural() {
        return subjectNounPlural;
    }

    /**
     * Legt den Wert der subjectNounPlural-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubjectNounPlural(String value) {
        this.subjectNounPlural = value;
    }

    /**
     * Ruft den Wert der subjectColumnName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubjectColumnName() {
        return subjectColumnName;
    }

    /**
     * Legt den Wert der subjectColumnName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubjectColumnName(String value) {
        this.subjectColumnName = value;
    }

    /**
     * Ruft den Wert der dateBased-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDateBased() {
        return dateBased;
    }

    /**
     * Legt den Wert der dateBased-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDateBased(Boolean value) {
        this.dateBased = value;
    }

    /**
     * Ruft den Wert der timepointType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TimepointType }
     *     
     */
    public TimepointType getTimepointType() {
        return timepointType;
    }

    /**
     * Legt den Wert der timepointType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TimepointType }
     *     
     */
    public void setTimepointType(TimepointType value) {
        this.timepointType = value;
    }

    /**
     * Ruft den Wert der startDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Legt den Wert der startDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Ruft den Wert der endDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Legt den Wert der endDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Ruft den Wert der defaultTimepointDuration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDefaultTimepointDuration() {
        return defaultTimepointDuration;
    }

    /**
     * Legt den Wert der defaultTimepointDuration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDefaultTimepointDuration(Integer value) {
        this.defaultTimepointDuration = value;
    }

    /**
     * Ruft den Wert der securityType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SecurityType }
     *     
     */
    public SecurityType getSecurityType() {
        return securityType;
    }

    /**
     * Legt den Wert der securityType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityType }
     *     
     */
    public void setSecurityType(SecurityType value) {
        this.securityType = value;
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
     * Ruft den Wert der descriptionRendererType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescriptionRendererType() {
        return descriptionRendererType;
    }

    /**
     * Legt den Wert der descriptionRendererType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescriptionRendererType(String value) {
        this.descriptionRendererType = value;
    }

    /**
     * Ruft den Wert der investigator-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvestigator() {
        return investigator;
    }

    /**
     * Legt den Wert der investigator-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvestigator(String value) {
        this.investigator = value;
    }

    /**
     * Ruft den Wert der grant-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrant() {
        return grant;
    }

    /**
     * Legt den Wert der grant-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrant(String value) {
        this.grant = value;
    }

    /**
     * Ruft den Wert der species-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecies() {
        return species;
    }

    /**
     * Legt den Wert der species-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecies(String value) {
        this.species = value;
    }

    /**
     * Ruft den Wert der alternateIdPrefix-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlternateIdPrefix() {
        return alternateIdPrefix;
    }

    /**
     * Legt den Wert der alternateIdPrefix-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlternateIdPrefix(String value) {
        this.alternateIdPrefix = value;
    }

    /**
     * Ruft den Wert der alternateIdDigits-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAlternateIdDigits() {
        return alternateIdDigits;
    }

    /**
     * Legt den Wert der alternateIdDigits-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAlternateIdDigits(Integer value) {
        this.alternateIdDigits = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
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
     *       &lt;attribute name="mode" type="{http://labkey.org/study/xml}cohortMode" />
     *       &lt;attribute name="type" type="{http://labkey.org/study/xml}cohortType" />
     *       &lt;attribute name="datasetId" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="datasetProperty" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Cohorts {

        @XmlAttribute(name = "mode")
        protected CohortMode mode;
        @XmlAttribute(name = "type")
        protected CohortType type;
        @XmlAttribute(name = "datasetId")
        protected Integer datasetId;
        @XmlAttribute(name = "datasetProperty")
        protected String datasetProperty;
        @XmlAttribute(name = "file")
        protected String file;

        /**
         * Ruft den Wert der mode-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link CohortMode }
         *     
         */
        public CohortMode getMode() {
            return mode;
        }

        /**
         * Legt den Wert der mode-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link CohortMode }
         *     
         */
        public void setMode(CohortMode value) {
            this.mode = value;
        }

        /**
         * Ruft den Wert der type-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link CohortType }
         *     
         */
        public CohortType getType() {
            return type;
        }

        /**
         * Legt den Wert der type-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link CohortType }
         *     
         */
        public void setType(CohortType value) {
            this.type = value;
        }

        /**
         * Ruft den Wert der datasetId-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getDatasetId() {
            return datasetId;
        }

        /**
         * Legt den Wert der datasetId-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setDatasetId(Integer value) {
            this.datasetId = value;
        }

        /**
         * Ruft den Wert der datasetProperty-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDatasetProperty() {
            return datasetProperty;
        }

        /**
         * Legt den Wert der datasetProperty-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDatasetProperty(String value) {
            this.datasetProperty = value;
        }

        /**
         * Ruft den Wert der file-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFile() {
            return file;
        }

        /**
         * Legt den Wert der file-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFile(String value) {
            this.file = value;
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
     *       &lt;attribute name="participantCommentDatasetId" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="participantCommentDatasetProperty" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="participantVisitCommentDatasetId" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="participantVisitCommentDatasetProperty" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Comments {

        @XmlAttribute(name = "participantCommentDatasetId")
        protected Integer participantCommentDatasetId;
        @XmlAttribute(name = "participantCommentDatasetProperty")
        protected String participantCommentDatasetProperty;
        @XmlAttribute(name = "participantVisitCommentDatasetId")
        protected Integer participantVisitCommentDatasetId;
        @XmlAttribute(name = "participantVisitCommentDatasetProperty")
        protected String participantVisitCommentDatasetProperty;

        /**
         * Ruft den Wert der participantCommentDatasetId-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getParticipantCommentDatasetId() {
            return participantCommentDatasetId;
        }

        /**
         * Legt den Wert der participantCommentDatasetId-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setParticipantCommentDatasetId(Integer value) {
            this.participantCommentDatasetId = value;
        }

        /**
         * Ruft den Wert der participantCommentDatasetProperty-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getParticipantCommentDatasetProperty() {
            return participantCommentDatasetProperty;
        }

        /**
         * Legt den Wert der participantCommentDatasetProperty-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setParticipantCommentDatasetProperty(String value) {
            this.participantCommentDatasetProperty = value;
        }

        /**
         * Ruft den Wert der participantVisitCommentDatasetId-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getParticipantVisitCommentDatasetId() {
            return participantVisitCommentDatasetId;
        }

        /**
         * Legt den Wert der participantVisitCommentDatasetId-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setParticipantVisitCommentDatasetId(Integer value) {
            this.participantVisitCommentDatasetId = value;
        }

        /**
         * Ruft den Wert der participantVisitCommentDatasetProperty-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getParticipantVisitCommentDatasetProperty() {
            return participantVisitCommentDatasetProperty;
        }

        /**
         * Legt den Wert der participantVisitCommentDatasetProperty-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setParticipantVisitCommentDatasetProperty(String value) {
            this.participantVisitCommentDatasetProperty = value;
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
     *       &lt;all>
     *         &lt;element name="schema" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="labelColumn" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="typeNameColumn" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="typeIdColumn" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="definition">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/all>
     *       &lt;attribute name="dir" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    public static class Datasets {

        protected Schema schema;
        @XmlElement(required = true)
        protected Definition definition;
        @XmlAttribute(name = "dir")
        protected String dir;
        @XmlAttribute(name = "file")
        protected String file;

        /**
         * Ruft den Wert der schema-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Schema }
         *     
         */
        public Schema getSchema() {
            return schema;
        }

        /**
         * Legt den Wert der schema-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Schema }
         *     
         */
        public void setSchema(Schema value) {
            this.schema = value;
        }

        /**
         * Ruft den Wert der definition-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Definition }
         *     
         */
        public Definition getDefinition() {
            return definition;
        }

        /**
         * Legt den Wert der definition-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Definition }
         *     
         */
        public void setDefinition(Definition value) {
            this.definition = value;
        }

        /**
         * Ruft den Wert der dir-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDir() {
            return dir;
        }

        /**
         * Legt den Wert der dir-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDir(String value) {
            this.dir = value;
        }

        /**
         * Ruft den Wert der file-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFile() {
            return file;
        }

        /**
         * Legt den Wert der file-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFile(String value) {
            this.file = value;
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
         *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Definition {

            @XmlAttribute(name = "file")
            protected String file;

            /**
             * Ruft den Wert der file-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFile() {
                return file;
            }

            /**
             * Legt den Wert der file-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFile(String value) {
                this.file = value;
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
         *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="labelColumn" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="typeNameColumn" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="typeIdColumn" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Schema {

            @XmlAttribute(name = "file")
            protected String file;
            @XmlAttribute(name = "labelColumn")
            protected String labelColumn;
            @XmlAttribute(name = "typeNameColumn")
            protected String typeNameColumn;
            @XmlAttribute(name = "typeIdColumn")
            protected String typeIdColumn;

            /**
             * Ruft den Wert der file-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFile() {
                return file;
            }

            /**
             * Legt den Wert der file-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFile(String value) {
                this.file = value;
            }

            /**
             * Ruft den Wert der labelColumn-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getLabelColumn() {
                return labelColumn;
            }

            /**
             * Legt den Wert der labelColumn-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setLabelColumn(String value) {
                this.labelColumn = value;
            }

            /**
             * Ruft den Wert der typeNameColumn-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTypeNameColumn() {
                return typeNameColumn;
            }

            /**
             * Legt den Wert der typeNameColumn-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTypeNameColumn(String value) {
                this.typeNameColumn = value;
            }

            /**
             * Ruft den Wert der typeIdColumn-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTypeIdColumn() {
                return typeIdColumn;
            }

            /**
             * Legt den Wert der typeIdColumn-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTypeIdColumn(String value) {
                this.typeIdColumn = value;
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
     *       &lt;attribute name="datasetId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="sourceProperty" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="aliasProperty" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ParticipantAliasDataset {

        @XmlAttribute(name = "datasetId", required = true)
        protected int datasetId;
        @XmlAttribute(name = "sourceProperty", required = true)
        protected String sourceProperty;
        @XmlAttribute(name = "aliasProperty", required = true)
        protected String aliasProperty;

        /**
         * Ruft den Wert der datasetId-Eigenschaft ab.
         * 
         */
        public int getDatasetId() {
            return datasetId;
        }

        /**
         * Legt den Wert der datasetId-Eigenschaft fest.
         * 
         */
        public void setDatasetId(int value) {
            this.datasetId = value;
        }

        /**
         * Ruft den Wert der sourceProperty-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSourceProperty() {
            return sourceProperty;
        }

        /**
         * Legt den Wert der sourceProperty-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSourceProperty(String value) {
            this.sourceProperty = value;
        }

        /**
         * Ruft den Wert der aliasProperty-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAliasProperty() {
            return aliasProperty;
        }

        /**
         * Legt den Wert der aliasProperty-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAliasProperty(String value) {
            this.aliasProperty = value;
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
     *       &lt;attribute name="showPrivateDataByDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class QcStates {

        @XmlAttribute(name = "showPrivateDataByDefault")
        protected Boolean showPrivateDataByDefault;
        @XmlAttribute(name = "file")
        protected String file;

        /**
         * Ruft den Wert der showPrivateDataByDefault-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isShowPrivateDataByDefault() {
            return showPrivateDataByDefault;
        }

        /**
         * Legt den Wert der showPrivateDataByDefault-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setShowPrivateDataByDefault(Boolean value) {
            this.showPrivateDataByDefault = value;
        }

        /**
         * Ruft den Wert der file-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFile() {
            return file;
        }

        /**
         * Legt den Wert der file-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFile(String value) {
            this.file = value;
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
     *     &lt;extension base="{http://labkey.org/study/xml}legacySpecimenSettingsType">
     *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="dir" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="settings" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Specimens
        extends LegacySpecimenSettingsType
    {

        @XmlAttribute(name = "file")
        protected String file;
        @XmlAttribute(name = "dir")
        protected String dir;
        @XmlAttribute(name = "settings")
        protected String settings;

        /**
         * Ruft den Wert der file-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFile() {
            return file;
        }

        /**
         * Legt den Wert der file-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFile(String value) {
            this.file = value;
        }

        /**
         * Ruft den Wert der dir-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDir() {
            return dir;
        }

        /**
         * Legt den Wert der dir-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDir(String value) {
            this.dir = value;
        }

        /**
         * Ruft den Wert der settings-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSettings() {
            return settings;
        }

        /**
         * Legt den Wert der settings-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSettings(String value) {
            this.settings = value;
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
     *       &lt;sequence>
     *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="rendererType" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "description"
    })
    public static class StudyDescription {

        @XmlElementRef(name = "description", namespace = "http://labkey.org/study/xml", type = JAXBElement.class, required = false)
        protected JAXBElement<String> description;
        @XmlAttribute(name = "rendererType")
        protected String rendererType;

        /**
         * Ruft den Wert der description-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public JAXBElement<String> getDescription() {
            return description;
        }

        /**
         * Legt den Wert der description-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link String }{@code >}
         *     
         */
        public void setDescription(JAXBElement<String> value) {
            this.description = value;
        }

        /**
         * Ruft den Wert der rendererType-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRendererType() {
            return rendererType;
        }

        /**
         * Legt den Wert der rendererType-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRendererType(String value) {
            this.rendererType = value;
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
     *       &lt;attribute name="dir" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="settings" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class StudyViews {

        @XmlAttribute(name = "dir")
        protected String dir;
        @XmlAttribute(name = "settings")
        protected String settings;

        /**
         * Ruft den Wert der dir-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDir() {
            return dir;
        }

        /**
         * Legt den Wert der dir-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDir(String value) {
            this.dir = value;
        }

        /**
         * Ruft den Wert der settings-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSettings() {
            return settings;
        }

        /**
         * Legt den Wert der settings-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSettings(String value) {
            this.settings = value;
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
     *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Visits {

        @XmlAttribute(name = "file")
        protected String file;

        /**
         * Ruft den Wert der file-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFile() {
            return file;
        }

        /**
         * Legt den Wert der file-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFile(String value) {
            this.file = value;
        }

    }

}
