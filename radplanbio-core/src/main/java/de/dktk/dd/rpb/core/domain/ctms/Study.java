/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.core.domain.ctms;

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.Named;
import de.dktk.dd.rpb.core.domain.criteria.AbstractCriterion;
import de.dktk.dd.rpb.core.domain.criteria.constraints.AbstractConstraint;
import de.dktk.dd.rpb.core.domain.edc.AnnotationType;
import de.dktk.dd.rpb.core.domain.edc.CrfFieldAnnotation;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.FormDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupDefinition;
import de.dktk.dd.rpb.core.domain.edc.mapping.Mapping;
import de.dktk.dd.rpb.core.domain.randomisation.AbstractRandomisationConfiguration;
import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

/**
 * Study domain entity
 *
 * Persistent RPB Study entity hold the reference to transient EDC Study
 * This reference is persisted in order to enable advanced study management features in RPB which goes behind the scope
 * the EDC system e.g.: CTMS,randomisation, CRF annotations, data mappings (import/export) etc.
 *
 * @author tomas@skripcak.net
 * @since 17 Sep 2013
 */
@Entity
@Table(name = "STUDY")
public class Study implements Identifiable<Integer>, Named, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(Study.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment
    private String protocolId; // sponsor unique protocol id 255
    private String name; // short study name (possible abbreviation) 255
    private String title; // long (full) study title 4000
    private String description; // optional description 4000
    private Integer expectedTotalEnrollment; // number of expected study subjects
    private String comment;
    private String ocStudyIdentifier; // EDC study reference not null

    private Boolean isStratifyTrialSite; // should stratification during randomisation include trial site property
    private Boolean isEnrollmentArmAssignmentRequired; // should treatment arm be specified during enrolment process

    // One to one
    private AbstractProtocolType protocolType; // study design (interventional, observational, registry)
    private AbstractRandomisationConfiguration randomisationConfiguration; // configuration for randomisation

    @Transient
    private de.dktk.dd.rpb.core.domain.edc.Study edcStudy; // OC CDISC ODM study metadata definition

    // Many to one
    private PartnerSite partnerSite; // not null, owner of the study (data is located on owner systems), randomisation is performed here (principal site)
    private StudyPhase phase;
    private StudyStatus status;
    private SponsoringType sponsoringType;
    private TimePerspective timing;

    // One to many
    private List<StudyDoc> studyDocs; // attached study documents
    private List<Mapping> dataMappings; // mappings used in study data transformations
    private List<TreatmentArm> treatmentArms; // for randomisation the possible treatment arms
    private List<AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>>> subjectCriteria; // randomisation criteria
    private List<StudyTag> tags; // study tags with e.g. different secondary IDs
    private List<StudyPerson> studyPersonnel; // personnel involved in study with specific role
    private List<StudyOrganisation> studyOrganisations; // organisation involved in study with specific role
    private List<TimeLineEvent> timeLineEvents; // events (milestones) in study
    private List<CrfFieldAnnotation> crfFieldAnnotations; // annotation of study CRF item fields

    // Many to many
    private List<PartnerSite> participatingSites; // RPB sites participating on study (important for randomisation)
    private List<TumourEntity> tumourEntities;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public Study() {
        this.tags = new ArrayList<>();
    }

    public Study(Integer primaryKey) {
        this.setId(primaryKey);
        this.tags = new ArrayList<>();
        this.tumourEntities = new ArrayList<>();
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "study_id_seq")
    @SequenceGenerator(name = "study_id_seq", sequenceName = "study_id_seq")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return id != null;
    }

    //endregion

    //region ProtocolId

    @Size(max = 255)
    @Column(name = "PROTOCOLID")
    public String getProtocolId() {
        return this.protocolId;
    }

    public void setProtocolId(String value) {
        this.protocolId = value;
    }

    //endregion

    //region Name

    @Size(max = 255)
    @Column(name = "Name")
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region Title

    @Size(max = 4000)
    @Column(name = "TITLE", length = 4000)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    //endregion

    //region Description

    @Size(max = 4000)
    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    //endregion

    //region ExpectedTotalEnrollment

    @Column(name = "EXPECTEDTOTALENROLLMENT")
    public Integer getExpectedTotalEnrollment() {
        return this.expectedTotalEnrollment;
    }

    public void setExpectedTotalEnrollment(Integer value) {
        this.expectedTotalEnrollment = value;
    }

    //endregion

    //region Comment

    @Column(name = "COMMENT")
    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    //endregion

    // region EDC StudyIdentifier

    @Size(max = 255)
    @Column(name = "OCSTUDYIDENTIFIER")
    public String getOcStudyIdentifier() {
        return this.ocStudyIdentifier;
    }

    public void setOcStudyIdentifier(String value) {
        this.ocStudyIdentifier = value;
    }

    //endregion

    //region IsRandomisedClinicalTrial

    @Transient
    public boolean getIsRandomisedClinicalTrial() {
        return this.randomisationConfiguration != null;
    }

    //endregion

    //region IsStratifyTrialSite

    @Column(name = "STRATIFYSITE")
    public Boolean getIsStratifyTrialSite() {
        return this.isStratifyTrialSite;
    }

    public void setIsStratifyTrialSite(Boolean value) {
        this.isStratifyTrialSite = value;
    }

    //endregion

    //region IsEnrollmentArmAssignmentRequired

    @Column(name = "ENROLLMENTARMREQUIRED")
    public Boolean getIsEnrollmentArmAssignmentRequired() {
        return this.isEnrollmentArmAssignmentRequired;
    }

    public void setIsEnrollmentArmAssignmentRequired(Boolean value) {
        isEnrollmentArmAssignmentRequired = value;
    }

    //endregion

    //region IsInSilico

    @Transient
    public Boolean getIsInSilico() {
        return this.protocolType != null && this.protocolType instanceof InSilicoProtocolType;
    }

    //endregion

    //region IsInterventional

    @Transient
    public Boolean getIsInterventional() {
        return this.protocolType != null && this.protocolType instanceof InterventionalProtocolType;
    }

    //endregion

    //region IsMetaAnalysis

    @Transient
    public Boolean getIsMetaAnalysis() {
        return this.protocolType != null && this.protocolType instanceof MetaAnalysisProtocolType;
    }

    //endregion

    //region IsObservational

    @Transient
    public Boolean getIsObservational() {
        return this.protocolType != null && this.protocolType instanceof ObservationalProtocolType;
    }

    //endregion

    //region IsPilot

    @Transient
    public Boolean getIsPilot() {
        return this.protocolType != null && this.protocolType instanceof PilotProtocolType;
    }

    //endregion

    //region IsRegistry

    @Transient
    public Boolean getIsRegistry() {
        return this.protocolType != null && this.protocolType instanceof RegistryProtocolType;
    }

    //endregion

    //endregion

    //region One-to-One

    //region RandomisationConfiguration

    @OneToOne(mappedBy = "study", cascade = CascadeType.ALL)
    public AbstractRandomisationConfiguration getRandomisationConfiguration() {
        return this.randomisationConfiguration;
    }

    public void setRandomisationConfiguration(AbstractRandomisationConfiguration randomisationConfiguration) {
        this.randomisationConfiguration = randomisationConfiguration;

        if (this.randomisationConfiguration != null && this.randomisationConfiguration.getStudy() == null) {
            this.randomisationConfiguration.setStudy(this);
        }
    }

    //endregion

    //region ProtocolType

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="PROTOCOLTYPEID")
    public AbstractProtocolType getProtocolType() {
        return this.protocolType;
    }

    public void setProtocolType(AbstractProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    @Transient
    public EnumProtocolType getEnumProtocolType() {
        EnumProtocolType result = null;

        if (this.protocolType != null) {
            if (this.protocolType instanceof ObservationalProtocolType) {
                result = EnumProtocolType.OBSERVATIONAL;
            }
            else if (this.protocolType instanceof InterventionalProtocolType) {
                result = EnumProtocolType.INTERVENTIONAL;
            }
            else if (this.protocolType instanceof RegistryProtocolType) {
                result = EnumProtocolType.REGISTRY;
            }
            else if (this.protocolType instanceof PilotProtocolType) {
                result = EnumProtocolType.PILOT;
            }
            else if (this.protocolType instanceof MetaAnalysisProtocolType) {
                result = EnumProtocolType.METAANALYSIS;
            }
            else if (this.protocolType instanceof InSilicoProtocolType) {
                result = EnumProtocolType.INSILICO;
            }
        }

        return result;
    }

    public void setEnumProtocolType(EnumProtocolType enumProtocolType) {
        // Create value object according selected enum
        if (enumProtocolType != null) {
            if (enumProtocolType == EnumProtocolType.OBSERVATIONAL) {
                // There is a change of protocol type
                if (this.protocolType == null || !(this.protocolType instanceof ObservationalProtocolType)) {
                    this.protocolType = new ObservationalProtocolType();
                }
            }
            else if (enumProtocolType == EnumProtocolType.INTERVENTIONAL) {
                // There is a change of protocol type
                if (this.protocolType == null || !(this.protocolType instanceof InterventionalProtocolType)) {
                    this.protocolType = new InterventionalProtocolType();
                }
            }
            else if (enumProtocolType == EnumProtocolType.REGISTRY) {
                // There is a change of protocol type
                if (this.protocolType == null || !(this.protocolType instanceof RegistryProtocolType)) {
                    this.protocolType = new RegistryProtocolType();
                }
            }
            else if (enumProtocolType == EnumProtocolType.PILOT) {
                // There is a change of protocol type
                if (this.protocolType == null || !(this.protocolType instanceof PilotProtocolType)) {
                    this.protocolType = new PilotProtocolType();
                }
            }
            else if (enumProtocolType == EnumProtocolType.METAANALYSIS) {
                // There is a change of protocol type
                if (this.protocolType == null || !(this.protocolType instanceof MetaAnalysisProtocolType)) {
                    this.protocolType = new MetaAnalysisProtocolType();
                }
            }
            else if (enumProtocolType == EnumProtocolType.INSILICO) {
                // There is a change of protocol type
                if (this.protocolType == null || !(this.protocolType instanceof InSilicoProtocolType)) {
                    this.protocolType = new InSilicoProtocolType();
                }
            }
        }
        else {
            this.setProtocolType(null);
        }
    }

    //endregion

    //region EDC Study

    @Transient
    public de.dktk.dd.rpb.core.domain.edc.Study getEdcStudy() {
        return this.edcStudy;
    }

    public void setEdcStudy(de.dktk.dd.rpb.core.domain.edc.Study edcStudy) {
        this.edcStudy = edcStudy;
    }

    /**
     * Determine whether study has assigned EDC study metadata
     *
     * @return True when EDC study metadata (CDISC ODM) are present
     */
    @Transient
    public Boolean getHasEdcStudy() {
        return this.edcStudy != null;
    }

    /**
     * Determine whether study is defined as multi-centre according to EDC study metadata
     *
     * @return True for multi-centre study, False if EDC study metadata does not exists or if mono-centre
     */
    @Transient
    public Boolean getIsEdcStudyMultiCentre() {
        return (this.edcStudy != null && this.edcStudy.isMultiCentre());
    }

    //endregion

    //endregion

    //region One-to-Many

    //region StudyDocuments

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study", orphanRemoval = true)
    public List<StudyDoc> getStudyDocs() {
        return this.studyDocs;
    }

    public void setStudyDocs(List<StudyDoc> list) {
        this.studyDocs = list;
    }

    /**
     * Helper method to add the passed {@link StudyDoc} to the studyDocs list.
     */
    public boolean addDoc(StudyDoc doc) {
        if (!this.containsDoc(doc)) {
            doc.setStudy(this);
            return this.studyDocs.add(doc);
        }

        return false;
    }

    /**
     * Helper method to remove the passed {@link StudyDoc} from the studyDocs list.
     */
    public boolean removeDoc(StudyDoc doc) {
        return this.containsDoc(doc) && this.studyDocs.remove(doc);
    }

    /**
     * Helper method to determine if the passed {@link StudyDoc} is present in the studyDocs list.
     */
    public boolean containsDoc(StudyDoc doc) {
        return this.studyDocs != null && this.studyDocs.contains(doc);
    }

    //endregion

    //region DataMappings

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study", orphanRemoval = true)
    public List<Mapping> getDataMappings() {
        return this.dataMappings;
    }

    public void setDataMappings(List<Mapping> list) {
        this.dataMappings = list;
    }

    /**
     * Helper method to add the passed {@link Mapping} to the dataMapping list
     * @param mapping {@link Mapping} that should be added
     * @return True if addition was successful
     */
    public boolean addDataMapping(Mapping mapping) {
        if (!this.containsDataMapping(mapping)) {
            mapping.setStudy(this);
            return this.dataMappings.add(mapping);
        }

        return false;
    }

    /**
     * Helper method to remove the passed {@link Mapping} from the dataMapping list
     * @param mapping {@link Mapping} that should be removed
     * @return True if removal was successful
     */
    public boolean removeDataMapping(Mapping mapping) {
        return this.containsDataMapping(mapping) && this.dataMappings.remove(mapping);
    }

    /**
     * Helper method to determine whether provided {@link Mapping} exists within this study
     * @param mapping {@link Mapping} that should be checked for existence
     * @return True if provided {@link Mapping}  definition already exists
     */
    public boolean containsDataMapping(Mapping mapping) {
        return this.dataMappings != null && this.dataMappings.contains(mapping);
    }

    //endregion

    //region CrfFieldAnnotations

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study", orphanRemoval = true)
    public List<CrfFieldAnnotation> getCrfFieldAnnotations() {
        return this.crfFieldAnnotations;
    }

    public void setCrfFieldAnnotations(List<CrfFieldAnnotation> list) {
        this.crfFieldAnnotations = list;
    }

    /**
     * Helper method to add the passed {@link CrfFieldAnnotation} to the annotation list.
     */
    public boolean addCrfFieldAnnotation(CrfFieldAnnotation annotation) {
        if (!this.containsCrfFieldAnnotation(annotation)) {
            annotation.setStudy(this);
            return this.crfFieldAnnotations.add(annotation);
        }

        return false;
    }

    /**
     * Helper method to remove the passed {@link CrfFieldAnnotation} from the annotation list.
     */
    public boolean removeCrfFieldAnnotation(CrfFieldAnnotation annotation) {
        return this.containsCrfFieldAnnotation(annotation) && this.crfFieldAnnotations.remove(annotation);
    }

    /**
     * Helper method to determine if the passed {@link CrfFieldAnnotation} is present in the annotation list.
     */
    public boolean containsCrfFieldAnnotation(CrfFieldAnnotation annotation) {
        return this.crfFieldAnnotations != null && this.crfFieldAnnotations.contains(annotation);
    }

    //endregion

    //region TreatmentArms

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study", orphanRemoval = true)
    public List<TreatmentArm> getTreatmentArms() {
        return this.treatmentArms;
    }

    public void setTreatmentArms(List<TreatmentArm> list) {
        this.treatmentArms = list;
    }

    public boolean addTreatmentArm(TreatmentArm treatmentArm) {
        if (treatmentArm != null) {
            treatmentArm.setStudy(this);
        }

        return this.treatmentArms.add(treatmentArm);
    }

    public boolean removeTreatmentArm(TreatmentArm treatmentArm) {
        if (treatmentArm != null) {
            treatmentArm.setStudy(null);
        }

        return this.containsTreatmentArm(treatmentArm) && this.treatmentArms.remove(treatmentArm);
    }

    public boolean containsTreatmentArm(TreatmentArm treatmentArm) {
        return this.treatmentArms != null && this.treatmentArms.contains(treatmentArm);
    }

    //endregion

    //region SubjectStratificationCriteria

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study", orphanRemoval = true)
    public List<AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>>> getSubjectCriteria() {
        return this.subjectCriteria;
    }

    public void setSubjectCriteria(List<AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>>> criteria) {
        this.subjectCriteria = criteria;
    }

    public Boolean addSubjectCriterion(AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> criterion) {
        if (criterion != null) {
            criterion.setStudy(this);
        }

        if (!this.containsSubjectCriterion(criterion)) {
            criterion.setStudy(this);
            return this.subjectCriteria.add(criterion);
        }

        return false;
    }

    public boolean removeSubjectCriterion(AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> criterion) {
        if (this.containsSubjectCriterion(criterion)) {
            criterion.setStudy(null);
            return this.subjectCriteria.remove(criterion);
        }

        return false;
    }

    public Boolean containsSubjectCriterion(AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> criterion) {
        return this.subjectCriteria != null && this.subjectCriteria.contains(criterion);
    }

    //endregion

    //region Tags

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study", orphanRemoval = true)
    public List<StudyTag> getTags() {
        return this.tags;
    }

    public void setTags(List<StudyTag> list) {
        this.tags = list;
    }

    /**
     * Helper method to add the passed {@link StudyTag} to the tag list
     * @param tag {@link StudyTag} that should be added
     * @return True if addition was successful
     */
    public boolean addTag(StudyTag tag) {
        // TODO: business logic verify whether tag max occurrence is not broken

        tag.setStudy(this);
        return this.tags.add(tag);
    }

    /**
     * Helper method to remove the passed {@link StudyTag} from the tag list
     * @param tag {@link StudyTag} that should be removed
     * @return True if removal was successful
     */
    public boolean removeTag(StudyTag tag) {
        return this.containsTag(tag) && this.tags.remove(tag);
    }

    /**
     * Helper method to determine whether provided {@link StudyTag} exists within this study
     * @param tag {@link StudyTag} that should be checked for existence
     * @return True if provided {@link StudyTag} definition already exists
     */
    public boolean containsTag(StudyTag tag) {
        return this.tags != null && this.tags.contains(tag);
    }

    //endregion

    //region StudyPersonnel

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study", orphanRemoval = true)
    public List<StudyPerson> getStudyPersonnel() {
        return this.studyPersonnel;
    }

    public void setStudyPersonnel(List<StudyPerson> studyPersonnel) {
        this.studyPersonnel = studyPersonnel;
    }

    /**
     * Helper method to add the passed {@link StudyPerson} to the studyPersonnel list
     * @param studyPerson {@link StudyPerson} that should be added
     * @return True if addition was successful
     */
    public boolean addStudyPersonnel(StudyPerson studyPerson) {
        if (!this.studyPersonnel.contains(studyPerson)) {
            studyPerson.setStudy(this);
            return this.studyPersonnel.add(studyPerson);
        }

        return false;
    }

    /**
     * Helper method to remove the passed {@link StudyPerson} from the studyPersonnel list
     * @param studyPerson {@link StudyPerson} that should be removed
     * @return True if removal was successful
     */
    public boolean removeStudyPersonnel(StudyPerson studyPerson)  {
        return this.containsStudyPersonnel(studyPerson) && this.studyPersonnel.remove(studyPerson);
    }

    /**
     * Helper method to determine whether provided {@link StudyPerson} exists within this study
     * @param studyPerson {@link StudyPerson} that should be checked for existence
     * @return True if provided {@link StudyPerson} definition already exists
     */
    public boolean containsStudyPersonnel(StudyPerson studyPerson) {
        return this.studyPersonnel != null && this.studyPersonnel.contains(studyPerson);
    }

    public Boolean studyPersonExists(Person person) {
        if (person != null) {
            for (StudyPerson assignedStudyPerson : this.studyPersonnel) {
                if (assignedStudyPerson.getPerson().getId().equals(person.getId())) {
                    return true;
                }
            }

            return false;
        }
        else {
            return null;
        }
    }

    //endregion

    //region StudyOrganisations

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study", orphanRemoval = true)
    public List<StudyOrganisation> getStudyOrganisations() {
        return this.studyOrganisations;
    }

    public void setStudyOrganisations(List<StudyOrganisation> studyOrganisations) {
        this.studyOrganisations = studyOrganisations;
    }

    /**
     * Helper method to add the passed {@link StudyOrganisation} to the studyOrganisations list
     * @param studyOrganisation {@link StudyOrganisation} that should be added
     * @return True if addition was successful
     */
    public boolean addStudyOrganisation(StudyOrganisation studyOrganisation) {
        if (!this.studyOrganisations.contains(studyOrganisation)) {
            studyOrganisation.setStudy(this);
            return this.studyOrganisations.add(studyOrganisation);
        }

        return false;
    }

    /**
     * Helper method to remove the passed {@link StudyOrganisation} from the studyOrganisations list
     * @param studyOrganisation {@link StudyOrganisation} that should be removed
     * @return True if removal was successful
     */
    public boolean removeStudyOrganisation(StudyOrganisation studyOrganisation) {
        return this.containsStudyOrganisation(studyOrganisation) && this.studyOrganisations.remove(studyOrganisation);
    }

    /**
     * Helper method to determine whether provided {@link StudyOrganisation} exists within this study
     * @param studyOrganisation {@link StudyOrganisation} that should be checked for existence
     * @return True if provided {@link StudyOrganisation} definition already exists
     */
    public boolean containsStudyOrganisation(StudyOrganisation studyOrganisation) {
        return this.studyOrganisations != null && this.studyOrganisations.contains(studyOrganisation);
    }

    public Boolean studyOrganisationExists(Organisation organistaion) {
        if (organistaion != null) {
            for (StudyOrganisation assignedOrg : this.studyOrganisations) {
                if (assignedOrg.getOrganisation().getId().equals(organistaion.getId())) {
                    return true;
                }
            }

            return false;
        }
        else {
            return null;
        }
    }

    //endregion

    //region TimeLineEvents

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study", orphanRemoval = true)
    public List<TimeLineEvent> getTimeLineEvents() {
        return this.timeLineEvents;
    }

    public void setTimeLineEvents(List<TimeLineEvent> timeLineEvents) {
        this.timeLineEvents = timeLineEvents;
    }

    /**
     * Helper method to add the passed {@link TimeLineEvent} to the timeLineEvents list
     * @param event {@link TimeLineEvent} that should be added
     * @return True if addition was successful
     */
    public boolean addTimeLineEvent(TimeLineEvent event) {
        if (!this.timeLineEvents.contains(event)) {
            event.setStudy(this);
            return this.timeLineEvents.add(event);
        }

        return false;
    }

    /**
     * Helper method to remove the passed {@link TimeLineEvent} from the timeLineEvents list
     * @param event {@link TimeLineEvent} that should be removed
     * @return True if removal was successful
     */
    public boolean removeTimeLineEvent(TimeLineEvent event) {
        return this.containsTimeLineEvent(event) && this.timeLineEvents.remove(event);
    }

    /**
     * Helper method to determine whether provided {@link TimeLineEvent} exists within this study
     * @param event {@link TimeLineEvent} that should be checked for existence
     * @return True if provided {@link TimeLineEvent} definition already exists
     */
    public boolean containsTimeLineEvent(TimeLineEvent event) {
        return this.timeLineEvents != null && this.timeLineEvents.contains(event);
    }

    //endregion

    //endregion

    //region Many-to-One

    //region PrincipalPartnerSite

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="SITEID")
    public PartnerSite getPartnerSite() {
        return this.partnerSite;
    }

    public void setPartnerSite(PartnerSite value) {
        this.partnerSite = value;
    }

    //endregion

    //region StudyPhase

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="PHASEID")
    public StudyPhase getPhase() {
        return this.phase;
    }

    public void setPhase(StudyPhase phase) {
        this.phase = phase;
    }

    //endregion

    //region StudyStatus

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="STATUSID")
    public StudyStatus getStatus() {
        return this.status;
    }

    public void setStatus(StudyStatus status) {
        this.status = status;
    }

    //endregion

    //region SponsoringType

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="SPONSORINGTYPEID")
    public SponsoringType getSponsoringType() {
        return this.sponsoringType;
    }

    public void setSponsoringType(SponsoringType sponsoringType) {
        this.sponsoringType = sponsoringType;
    }

    //endregion

    //region TimePerspective

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TIMEPERSPECTIVEID")
    public TimePerspective getTiming() {
        return this.timing;
    }

    public void setTiming(TimePerspective timing) {
        this.timing = timing;
    }

    //endregion

    //endregion

    //region Many-to-Many

    //region ParticipatingSites

    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name="STUDYPARTNER", joinColumns= { @JoinColumn(name="STUDYID") }, inverseJoinColumns={ @JoinColumn(name="SITEID") })
    @ManyToMany(fetch= FetchType.EAGER, cascade = { PERSIST, MERGE })
    public List<PartnerSite> getParticipatingSites() {
        return this.participatingSites;
    }

    public void setParticipatingSites(List<PartnerSite> set) {
        this.participatingSites = set;
    }

    /**
     * Adds the participating site.
     *
     * @param participatingSite the participating partner site
     */
    public boolean addParticipatingSite(PartnerSite participatingSite) {
        if (participatingSite != null) {
            this.participatingSites.add(participatingSite);
            return participatingSite.addStudy(this);
        }

        return Boolean.FALSE;
    }

    public boolean removeParticipatingSite(PartnerSite site) {
        if (site != null) {
            site.removeStudy(this);
        }

        return this.participatingSites != null && this.participatingSites.remove(site);
    }

    //endregion

    //region TumourEntities

    //TODO: Lazy load TRUE
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "STUDY_TUMOURENTITY", joinColumns = @JoinColumn(name = "STUDYID"), inverseJoinColumns = @JoinColumn(name = "TUMOURENTITYID"))
    @ManyToMany(fetch= FetchType.EAGER, cascade = { PERSIST, MERGE })
    public List<TumourEntity> getTumourEntities() {
        return this.tumourEntities;
    }

    public void setTumourEntities(List<TumourEntity> tumourEntities) {
        this.tumourEntities = tumourEntities;
    }

    /**
     * Helper method to add the passed {@link TumourEntity} to the tumourEntities list.
     */
    public boolean addTumourEntity(TumourEntity te) {
        if (!this.containsTumourEntity(te)) {
            if (this.tumourEntities == null) {
                this.tumourEntities = new ArrayList<>();
            }
            return this.tumourEntities.add(te);
        }

        return Boolean.FALSE;
    }

    public boolean addTumourEntities(List<TumourEntity> tumourEntities) {
        for (TumourEntity te : tumourEntities) {
            if (!this.addTumourEntity(te)) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    /**
     * Helper method to remove the passed {@link TumourEntity} from the tumourEntities list.
     */
    public boolean removeTumourEntity(TumourEntity te) {
        return this.containsTumourEntity(te) && this.tumourEntities.remove(te);
    }

    /**
     * Helper method to determine if the passed {@link TumourEntity} is present in the tumourEntities list.
     */
    public boolean containsTumourEntity(TumourEntity te) {
        return this.tumourEntities != null && this.tumourEntities.contains(te);
    }

    public String tumourEntitiesString() {
        List<String> tumourEntityNames = new ArrayList<>();

        if (this.tumourEntities != null) {
            for (TumourEntity tumourEntity : this.tumourEntities) {
                tumourEntityNames.add(tumourEntity.getName());
            }
        }

        return StringUtils.join(tumourEntityNames, ",");
    }

    //endregion

    //endregion

    //endregion

    //region Methods

    //region Init

    /**
     * Set the default values.
     */
    public void initDefaultValues() {
        // NOOP
    }

    //endregion
    
    //region StudyTags Methods

    public void initialiseAllRequiredTags(List<StudyTagType> requiredTagTypes) {

        for (StudyTagType stt : requiredTagTypes) {

            boolean requiredExists = false;
            for (StudyTag st : this.getTags()) {
                if (stt.getId().equals(st.getType().getId())) {
                    requiredExists = true;
                    break;
                }
            }

            if (!requiredExists) {
                this.addTag(
                        new StudyTag(stt)
                );
            }
        }
    }

    /**
     * All study tags associated with study has to have a valid value
     *
     * @return validity of study tags
     */
    public Boolean areTagsValid() {
        Boolean result = Boolean.TRUE;

        for (StudyTag st : this.tags) {
            if (st.getValue() == null || st.getValue().equals("")) {
                result = Boolean.FALSE;
                break;
            }
        }

        return result;
    }

    public String getTagValue(String tagTypeName) {
        String result = null;

        if (tagTypeName != null) {
            for (StudyTag tag : this.tags) {
                if (tag.getType().getName().equals(tagTypeName)) {
                    result = tag.getValue();
                    break;
                }
            }
        }

        return result;
    }

    public Boolean getBooleanTagValue(String tagTypeName) {
        Boolean result = null;

        if (tagTypeName != null) {
            for (StudyTag tag : this.tags) {
                if (tag.getType().getName().equals(tagTypeName) && tag.getType().getIsBoolean()) {
                    result = Boolean.valueOf(tag.getValue());
                    break;
                }
            }
        }

        return result;
    }

    public Boolean isTagBoolean(String tagTypeName) {
        Boolean result = Boolean.FALSE;

        if (tagTypeName != null) {
            for (StudyTag tag : this.tags) {
                if (tag.getType().getName().equals((tagTypeName))) {
                    result = tag.getType().isBoolean;
                    break;
                }
            }
        }

        return result;
    }

    //endregion

    //region CrfFieldAnnotation Methods

    public CrfFieldAnnotation findAnnotation(String annotationTypeName, CrfFieldAnnotation example) {
        List<CrfFieldAnnotation> result = this.findMatchingAnnotations(annotationTypeName, example);
        return result.size() > 0 ? result.get(0) : null;
    }

    public List<CrfFieldAnnotation> findAnnotations(String annotationTypeName) {
        List<CrfFieldAnnotation> result = new ArrayList<>();

        if (this.crfFieldAnnotations != null) {
            for (CrfFieldAnnotation cfa : this.crfFieldAnnotations) {
                if (cfa.getAnnotationType().getName().equals(annotationTypeName)) {
                    result.add(cfa);
                }
            }
        }

        return result;
    }

    public List<CrfFieldAnnotation> findAnnotations(CrfFieldAnnotation example) {
        return this.findMatchingAnnotations(example);
    }

    public List<CrfFieldAnnotation> findAnnotations(String annotationTypeName, CrfFieldAnnotation example) {
        return this.findMatchingAnnotations(annotationTypeName, example);
    }

    public List<CrfFieldAnnotation> findAnnotations(List<String> annotationTypeNames, CrfFieldAnnotation example) {
        List<CrfFieldAnnotation> results = new ArrayList<>();

        for (String annotationTypeName : annotationTypeNames) {
            results.addAll(this.findMatchingAnnotations(annotationTypeName, example));
        }

        return results;
    }

    //endregion

    //region AnnotationTypes

    public List<AnnotationType> findAnnotationsTypes(CrfFieldAnnotation example) {
        List<AnnotationType> results = new ArrayList<>();

        for (CrfFieldAnnotation specimenAnnotation : this.findAnnotations(example)) {
            results.add(specimenAnnotation.getAnnotationType());
        }

        return results;
    }

    //endregion

    //region Study Metadata Methods

    //region EventDefinition

    /**
     * Find list of EventDefinition entities from this study EDC metadata
     *
     * @return List of EventDefinition entities
     */
    public List<EventDefinition> findEventDefinitions() {
        List<EventDefinition> results = new ArrayList<>();

        if (this.edcStudy != null) {
            if (this.edcStudy.getMetaDataVersion() != null) {
                if (this.edcStudy.getMetaDataVersion().getStudyEventDefinitions() != null) {
                    results = this.edcStudy.getMetaDataVersion().getStudyEventDefinitions();
                }
            }
        }

        return results;
    }

    /**
     * Find list of EventDefinition entities that hold items annotated with specified AnnotationType name
     *
     * @param annotationTypeName unique name that identifies RPB AnnotationType (e.g. DICOM_STUDY_INSTANCE_UID)
     * @return List of EventDefinition entities
     */
    public List<EventDefinition> findAnnotatedEventDefinitions(String annotationTypeName) {
        List<EventDefinition> results = new ArrayList<>();
        List<CrfFieldAnnotation> annotations = this.findAnnotations(annotationTypeName);

        if (this.edcStudy != null) {
            if (this.edcStudy.getMetaDataVersion() != null) {
                if (this.edcStudy.getMetaDataVersion().getStudyEventDefinitions() != null) {
                    for (EventDefinition ed : this.edcStudy.getMetaDataVersion().getStudyEventDefinitions()) {
                        for (CrfFieldAnnotation cfa : annotations) {
                            if (ed.getOid() != null && ed.getOid().equals(cfa.getEventDefinitionOid())) {
                                results.add(ed);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return results;
    }

    //endregion

    //region ItemDefinition

    /**
     * Find list of ItemDefinition entities that are annotated with specified AnnotationType name and belong to specified EventDefinition
     *
     * @param annotationTypeName unique name that identifies RPB AnnotationType (e.g. DICOM_STUDY_INSTANCE_UID)
     * @param eventDef EventDefinition where item should belong
     * @return List of ItemDefinition entities
     */
    public List<ItemDefinition> findAnnotatedItemDefinitionsForEventDef(String annotationTypeName, EventDefinition eventDef) {
        List<ItemDefinition> results = new ArrayList<>();

        List<CrfFieldAnnotation> annotations = this.findAnnotations(annotationTypeName);

        if (eventDef != null) {
            for (CrfFieldAnnotation cfa : annotations) {
                if (cfa.getEventDefinitionOid().equals(eventDef.getOid())) {
                    for (FormDefinition fd : eventDef.getFormDefs()) {
                        if (cfa.getFormOid().equals(fd.getOid())) {

                            // Annotations normally applies for default versions (the other versions are translations of forms to other languages)
                            if (fd.getFormDetails() != null && fd.getFormDetails().getPresentInEventDefinition().getIsDefaultVersion()) {

                                for (ItemGroupDefinition igd : fd.getItemGroupDefs()) {
                                    if (cfa.getGroupOid().equals(igd.getOid())) {
                                        for (ItemDefinition id : igd.getItemDefs()) {
                                            if (cfa.getCrfItemOid().equals(id.getOid())) {
                                                igd.setFormDefinition(fd);
                                                id.setItemGroupDefinition(igd);
                                                results.add(id);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return results;
    }

    //endregion

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Study && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .toString();
    }

    //endregion

    //region Private Methods

    //region CrfFieldAnnotation

    private List<CrfFieldAnnotation> findMatchingAnnotations(String annotationTypeName, CrfFieldAnnotation example) {
        List<CrfFieldAnnotation> result = new ArrayList<>();

        // Filter is provided
        if (example != null) {

            // Annotation with any event Oid is a match when Oid is not provided
            boolean eventOidMatch = StringUtils.isEmpty(example.getEventDefinitionOid());
            // Annotation with any form Oid is a match when Oid is not provided
            boolean formOidMatch = StringUtils.isEmpty(example.getFormOid());
            // Annotation with any item group Oid is a match when Oid is not provided
            boolean itemGroupOidMatch = StringUtils.isEmpty(example.getGroupOid());
            // Annotation with any item Oid is a match when Oid is not provided
            boolean itemOidMatch = StringUtils.isEmpty(example.getCrfItemOid());

            for (CrfFieldAnnotation annotation : this.crfFieldAnnotations) {

                // Check if the type of annotation is matching
                if (annotationTypeName.equals(annotation.getAnnotationType().getName())) {

                    boolean exampleMatch = this.annotationExampleMatch(
                            example,
                            annotation,
                            eventOidMatch,
                            formOidMatch,
                            itemGroupOidMatch,
                            itemOidMatch
                    );

                    // Check the if example match
                    if (exampleMatch) {
                        result.add(annotation);
                    }
                }
            }
        }

        return result;
    }

    private List<CrfFieldAnnotation> findMatchingAnnotations(CrfFieldAnnotation example) {
        List<CrfFieldAnnotation> result = new ArrayList<>();

        // Filter is provided
        if (example != null) {

            // Annotation with any event Oid is a match when Oid is not provided
            boolean eventOidMatch = StringUtils.isEmpty(example.getEventDefinitionOid());
            // Annotation with any form Oid is a match when Oid is not provided
            boolean formOidMatch = StringUtils.isEmpty(example.getFormOid());
            // Annotation with any item group Oid is a match when Oid is not provided
            boolean itemGroupOidMatch = StringUtils.isEmpty(example.getGroupOid());
            // Annotation with any item Oid is a match when Oid is not provided
            boolean itemOidMatch = StringUtils.isEmpty(example.getCrfItemOid());

            for (CrfFieldAnnotation annotation : this.crfFieldAnnotations) {

                boolean exampleMatch = this.annotationExampleMatch(
                        example,
                        annotation,
                        eventOidMatch,
                        formOidMatch,
                        itemGroupOidMatch,
                        itemOidMatch
                );

                // Check the if example match
                if (exampleMatch) {
                    result.add(annotation);
                }
            }

        }

        return result;
    }

    private boolean annotationExampleMatch(CrfFieldAnnotation example, CrfFieldAnnotation annotation, boolean eventOidMatch, boolean formOidMatch, boolean itemGroupOidMatch, boolean itemOidMatch) {

        // Check matching when OIDs are provided
        if (!eventOidMatch) {
            eventOidMatch = annotation.getEventDefinitionOid().equals(example.getEventDefinitionOid());
        }
        if (!formOidMatch) {
            formOidMatch = annotation.getFormOid().equals(example.getFormOid());
        }
        if (!itemGroupOidMatch) {
            itemGroupOidMatch = annotation.getGroupOid().equals(example.getGroupOid());
        }
        if (!itemOidMatch) {
            itemOidMatch = annotation.getCrfItemOid().equals(example.getCrfItemOid());
        }

        // Check the if annotation example match the provided annotation
        return  eventOidMatch && formOidMatch && itemGroupOidMatch && itemOidMatch;
    }

    //endregion

    //endregion

}
