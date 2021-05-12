//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.04.27 um 12:37:14 PM CEST 
//


package org.labkey.study.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.labkey.study.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _StudyStudyDescriptionDescription_QNAME = new QName("http://labkey.org/study/xml", "description");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.labkey.study.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Study }
     * 
     */
    public Study createStudy() {
        return new Study();
    }

    /**
     * Create an instance of {@link LegacySpecimenSettingsType }
     * 
     */
    public LegacySpecimenSettingsType createLegacySpecimenSettingsType() {
        return new LegacySpecimenSettingsType();
    }

    /**
     * Create an instance of {@link LegacySpecimenSettingsType.SpecimenWebPartGroupings }
     * 
     */
    public LegacySpecimenSettingsType.SpecimenWebPartGroupings createLegacySpecimenSettingsTypeSpecimenWebPartGroupings() {
        return new LegacySpecimenSettingsType.SpecimenWebPartGroupings();
    }

    /**
     * Create an instance of {@link Study.Datasets }
     * 
     */
    public Study.Datasets createStudyDatasets() {
        return new Study.Datasets();
    }

    /**
     * Create an instance of {@link MissingValueIndicatorsType }
     * 
     */
    public MissingValueIndicatorsType createMissingValueIndicatorsType() {
        return new MissingValueIndicatorsType();
    }

    /**
     * Create an instance of {@link Study.Visits }
     * 
     */
    public Study.Visits createStudyVisits() {
        return new Study.Visits();
    }

    /**
     * Create an instance of {@link Study.QcStates }
     * 
     */
    public Study.QcStates createStudyQcStates() {
        return new Study.QcStates();
    }

    /**
     * Create an instance of {@link Study.Cohorts }
     * 
     */
    public Study.Cohorts createStudyCohorts() {
        return new Study.Cohorts();
    }

    /**
     * Create an instance of {@link Study.ParticipantAliasDataset }
     * 
     */
    public Study.ParticipantAliasDataset createStudyParticipantAliasDataset() {
        return new Study.ParticipantAliasDataset();
    }

    /**
     * Create an instance of {@link Study.Specimens }
     * 
     */
    public Study.Specimens createStudySpecimens() {
        return new Study.Specimens();
    }

    /**
     * Create an instance of {@link Study.StudyViews }
     * 
     */
    public Study.StudyViews createStudyStudyViews() {
        return new Study.StudyViews();
    }

    /**
     * Create an instance of {@link ExportDirType }
     * 
     */
    public ExportDirType createExportDirType() {
        return new ExportDirType();
    }

    /**
     * Create an instance of {@link Study.Comments }
     * 
     */
    public Study.Comments createStudyComments() {
        return new Study.Comments();
    }

    /**
     * Create an instance of {@link Study.StudyDescription }
     * 
     */
    public Study.StudyDescription createStudyStudyDescription() {
        return new Study.StudyDescription();
    }

    /**
     * Create an instance of {@link LegacySpecimenSettingsType.SpecimenWebPartGroupings.Grouping }
     * 
     */
    public LegacySpecimenSettingsType.SpecimenWebPartGroupings.Grouping createLegacySpecimenSettingsTypeSpecimenWebPartGroupingsGrouping() {
        return new LegacySpecimenSettingsType.SpecimenWebPartGroupings.Grouping();
    }

    /**
     * Create an instance of {@link Study.Datasets.Schema }
     * 
     */
    public Study.Datasets.Schema createStudyDatasetsSchema() {
        return new Study.Datasets.Schema();
    }

    /**
     * Create an instance of {@link Study.Datasets.Definition }
     * 
     */
    public Study.Datasets.Definition createStudyDatasetsDefinition() {
        return new Study.Datasets.Definition();
    }

    /**
     * Create an instance of {@link MissingValueIndicatorsType.MissingValueIndicator }
     * 
     */
    public MissingValueIndicatorsType.MissingValueIndicator createMissingValueIndicatorsTypeMissingValueIndicator() {
        return new MissingValueIndicatorsType.MissingValueIndicator();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://labkey.org/study/xml", name = "description", scope = Study.StudyDescription.class)
    public JAXBElement<String> createStudyStudyDescriptionDescription(String value) {
        return new JAXBElement<String>(_StudyStudyDescriptionDescription_QNAME, String.class, Study.StudyDescription.class, value);
    }

}
