/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

package de.dktk.dd.rpb.core.domain.edc;

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.bio.AbstractSpecimen;
import de.dktk.dd.rpb.core.domain.bio.AliquotGroup;
import de.dktk.dd.rpb.core.domain.bio.Specimen;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappedOdmItem;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappingRecord;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;
import de.dktk.dd.rpb.core.util.Constants;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.openclinica.ws.beans.StudyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * StudySubject domain entity
 *
 * StudySubject in RPB is transient entity based on CDISC ODM SubjectData
 * 
 * includes OpenClinica extensions
 * includes RPB extensions
 *
 * @author tomas@skripcak.net
 * @since 09 Dec 2014
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="SubjectData")
public class StudySubject implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(StudySubject.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name="StudySubjectID", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String studySubjectId;

    @XmlAttribute(name="SecondaryID", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String secondaryId;

    @XmlAttribute(name="UniqueIdentifier", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String pid; // PersonID aka pseudonym aka PID

    @XmlAttribute(name="SubjectKey")
    private String subjectKey; // In OC the subject key is the one which starts with SS_

    @XmlAttribute(name="Sex", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String sex;

    @XmlAttribute(name="YearOfBirth", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private Integer yearOfBirth;

    @XmlAttribute(name="DateOfBirth", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String dateOfBirth;

    // @XmlAttribute(name="EnrollmentDate", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    @XmlTransient
    private String enrollmentDate;

    @XmlAttribute(name="Status", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String status;

    @XmlElement(name="StudyEventData")
    private List<EventData> studyEventDataList;

    @XmlElement(name="SubjectGroupData", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private List<SubjectGroupData> subjectGroupDataList;


    @XmlTransient
    private Person person; // Subject as Patient (Person)

    @XmlTransient
    private Study study; // EDC study

    @XmlTransient
    private List<TreatmentArm> treatmentArmList;

    @XmlTransient
    private Boolean isEnabled;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    //endregion

    //region Constructors

    public StudySubject() {
        this.person = new Person();
        this.isEnabled = Boolean.TRUE;

        init();

    }

    private void init() {
        this.studyEventDataList = new ArrayList<>();
        this.subjectGroupDataList = new ArrayList<>();
    }

    public StudySubject(EnumStudySubjectIdGeneration idStrategy) {
        this();

        // If the SSID should be auto-generated by EDC emtpy string needs to be set as SSID
        if (idStrategy.equals(EnumStudySubjectIdGeneration.AUTO)) {
            this.studySubjectId = "";
        }
    }

    public StudySubject(StudySubjectWithEventsType sswet) {
        this(sswet, null);
        init();
    }

    public StudySubject(StudySubjectWithEventsType sswet, StudyType st) {
        init();
        if (sswet != null) {

            // Get details from StudySubjectWithEventsType
            this.studySubjectId = sswet.getLabel();
            this.secondaryId = sswet.getSecondaryLabel();
            this.enrollmentDate = sswet.getEnrollmentDate().toString();

            // Associate with person
            if (sswet.getSubject() != null) {
                if (this.person == null) {
                    this.setPerson(
                            new Person(
                                    sswet.getSubject()
                            )
                    );
                }
                else {
                    this.person.setPid(
                            sswet.getSubject().getUniqueIdentifier()
                    );
                }

                this.pid = sswet.getSubject().getUniqueIdentifier();


                if (!(sswet.getSubject().getGender() == null)) {
                    this.sex = sswet.getSubject().getGender().value();
                }
            }

            // Associate with study
            if (st != null) {

                // Multi centre
                if (st.getSites() != null && st.getSites().getSite() != null && st.getSites().getSite().size() > 0) {

                    // Look for partner site according to the referencing study identifier
                    if (sswet.getStudyRef() != null && sswet.getStudyRef().getIdentifier() != null) {

                        String siteIdentifier = sswet.getStudyRef().getIdentifier();
                        for (SiteType site : st.getSites().getSite()) {
                            if (site.getIdentifier().equals(siteIdentifier)) {
                                if (this.study == null) {
                                    this.setStudy(
                                            new Study(st, site)
                                    );
                                } else {
                                    this.study.setOid(site.getOid());
                                    this.study.setName(site.getName());
                                    this.study.setUniqueIdentifier(site.getIdentifier());

                                    this.study.setParentStudy(new Study(st));
                                }
                            }
                        }
                    }
                    // No study ref - handle like mono centre (study ref is missing for non enhanced OC SOAP)
                    else {
                        if (this.study == null) {
                            this.setStudy(
                                    new Study(st)
                            );
                        }
                        else {
                            this.study.setOid(st.getOid());
                            this.study.setName(st.getName());
                            this.study.setUniqueIdentifier(st.getIdentifier());
                        }
                    }
                }
                // Mono centre
                else {
                    if (this.study == null) {
                        this.setStudy(
                                new Study(st)
                        );
                    }
                    else {
                        this.study.setOid(st.getOid());
                        this.study.setName(st.getName());
                        this.study.setUniqueIdentifier(st.getIdentifier());
                    }
                }
            }
        }
    }

    //endregion

    //region Properties

    //region ID

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region StudySubject ID

    public String getStudySubjectId() {
        return this.studySubjectId;
    }

    public void setStudySubjectId(String value) {
        this.studySubjectId = value;
    }

    //endregion

    //region ProtocolStudySubject ID

    public String getProtocolStudySubjectId() {
        String result = "";

        if (this.study != null && this.studySubjectId != null) {
            result = this.study.getOid() + "-" + this.studySubjectId;
        }

        return result;
    }

    //endregion

    //region Secondary ID

    public String getSecondaryId() {
        return this.secondaryId;
    }

    public void setSecondaryId(String value) {
        this.secondaryId = value;
    }

    //endregion

    //region PID

    public String getPid() {
        return this.pid;
    }

    public void setPid(String value) {
        this.pid = value;
    }

    //endregion

    //region SubjectKey

    public String getSubjectKey() {
        return this.subjectKey;
    }

    public void setSubjectKey(String value) {
        this.subjectKey = value;
    }

    //endregion

    //region Sex

    public String getSex() {
        return this.sex;
    }

    public void setSex(String value) {
        this.sex = value;
    }

    //endregion

    //region YearOfBirth
    
    public Integer getYearOfBirth() {
        // When missing but the full date is provided, set it on first call
        if (this.yearOfBirth == null && this.dateOfBirth != null && !this.dateOfBirth.isEmpty()) {
            this.yearOfBirth = Integer.parseInt(this.dateOfBirth.substring(0, 4));
        }

        return this.yearOfBirth;
    }

    public void setYearOfBirth(Integer value) {
        this.yearOfBirth = value;
    }

    //endregion

    //region DateOfBirth

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String value) {
        this.dateOfBirth = value;
    }

    public LocalDate getComparableDateOfBirth() {
        if (this.dateOfBirth != null && !this.dateOfBirth.equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.OC_DATEFORMAT);
            return LocalDate.parse(this.dateOfBirth, formatter);
        }

        return null;
    }

    public String getComparableDateOfBirthString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.RPB_DATEFORMAT);
        LocalDate date = this.getComparableDateOfBirth();
        return date != null ? formatter.format(date) : null;
    }

    //endregion

    //region EnrollmentDate

    public String getEnrollmentDate() { return this.enrollmentDate; }

    public LocalDate getDateEnrollment() {
        if (this.enrollmentDate != null && !this.enrollmentDate.equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.OC_DATEFORMAT);
            return LocalDate.parse(this.enrollmentDate, formatter);
        }

        return null;
    }

    public String getDateEnrollmentString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.RPB_DATEFORMAT);
        LocalDate date  = this.getDateEnrollment();
        return date != null ? formatter.format(date) : null;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        DateFormat format = new SimpleDateFormat(Constants.OC_DATEFORMAT);
        this.enrollmentDate = format.format(enrollmentDate);

    }

    public void setEnrollmentDate(XMLGregorianCalendar enrollmentDate) {
       this.setEnrollmentDate(enrollmentDate.toGregorianCalendar().getTime());
    }

    //endregion

    //region Status

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    //endregion

    //region StudyEventDataList

    public List<EventData> getStudyEventDataList() {
        return this.studyEventDataList;
    }

    public void setStudyEventDataList(List<EventData> list) {
        this.studyEventDataList = list;
    }

    public boolean addStudyEventData(EventData eventData) {
        return !this.containsStudyEventData(eventData) && this.studyEventDataList.add(eventData);
    }

    public boolean removeStudyEventData(EventData eventData) {
        return this.containsStudyEventData(eventData) && this.studyEventDataList.remove(eventData);
    }
    
    public boolean containsStudyEventData(EventData eventData) {
        return this.studyEventDataList != null && this.studyEventDataList.contains(eventData);
    }

    public void initDefaultStudyEventDataList() {
        if (this.studyEventDataList == null) {
            this.studyEventDataList = new ArrayList<>();
        }
        else {
            this.studyEventDataList.clear();
        }
    }

    //endregion

    // region SubjectGroupDataList

    public List<SubjectGroupData> getSubjectGroupDataList() {
        return subjectGroupDataList;
    }

    public void setSubjectGroupDataList(List<SubjectGroupData> subjectGroupDataList) {
        this.subjectGroupDataList = subjectGroupDataList;
    }

    // endregion

    //region Person

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    //endregion

    //region Study

    public Study getStudy() { return this.study; }

    public void setStudy(Study study) {
        this.study = study;
    }

    //endregion

    // region treatment arm

    public List<TreatmentArm> getTreatmentArmList() {
        return treatmentArmList;
    }

    public void setTreatmentArmList(List<TreatmentArm> treatmentArmList) {
        this.treatmentArmList = treatmentArmList;
    }


    // endregion

    //region IsEnabled

    public Boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(Boolean value) {
        this.isEnabled = value;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof StudySubject && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     * @return hash
     */
    @Override
    public int hashCode() {
        if (this.subjectKey != null) {
            return identifiableHashBuilder.hash(log, this, this.subjectKey);
        }
        else if (this.pid != null) {
            return identifiableHashBuilder.hash(log, this, this.pid);
        }
        else if (this.studySubjectId != null) {
            return identifiableHashBuilder.hash(log, this, this.studySubjectId);
        }
        else if (this.secondaryId != null) {
            return identifiableHashBuilder.hash(log, this, this.secondaryId);
        }
        else {
            return this.identifiableHashBuilder.hash(log, this);
        }
    }

    /**
     * Construct a readable string representation for this RtStructType instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("subjectKey", this.subjectKey)
                .add("pid", this.pid)
                .add("studySubjectId", this.studySubjectId)
                .add("secondaryId", this.secondaryId)
                .toString();
    }

    //endregion

    //region Methods

    //region Mapping

    public boolean populateDataField(MappingRecord mappingRecord, String value, String eventStartDate) {
        Boolean result = false;

        if (mappingRecord.getTarget() instanceof MappedOdmItem) {
            MappedOdmItem odmTarget = (MappedOdmItem) mappingRecord.getTarget();
            EventData targetEvent = null;

            // If subject data was loaded from ODM, this structure will be already initialised
            if (this.studyEventDataList != null) {
                for (EventData ed : this.studyEventDataList) {
                    if (ed.getStudyEventOid().equals(odmTarget.getStudyEventOid())) {
                        if (mappingRecord.getStudyEventRepeatKey() != null && !mappingRecord.getStudyEventRepeatKey().equals("")) {
                            if (ed.getStudyEventRepeatKey() != null && ed.getStudyEventRepeatKey().equals(mappingRecord.getStudyEventRepeatKey())) {
                                targetEvent = ed;
                                break;
                            }
                        }
                        else {
                            targetEvent = ed;
                            break;
                        }
                    }
                }
            }
            else {
                this.studyEventDataList = new ArrayList<>();
            }

            if (targetEvent == null) {
                if (mappingRecord.getStudyEventRepeatKey() != null && !mappingRecord.getStudyEventRepeatKey().equals("")) {
                    targetEvent = new EventData(odmTarget.getStudyEventOid(), mappingRecord.getStudyEventRepeatKey());
                }
                else {
                    targetEvent = new EventData(odmTarget.getStudyEventOid());
                }

                // Setup also event start date if provided for proper scheduling of events
                if (eventStartDate != null) {
                    targetEvent.setStartDate(eventStartDate);
                }

                this.studyEventDataList.add(targetEvent);
            }

            FormData targetForm = null;
            if (targetEvent.getFormDataList() != null) {
                for (FormData fd : targetEvent.getFormDataList()) {
                    if (fd.getFormOid().equals(odmTarget.getFormOid())) {
                        targetForm = fd;
                        break;
                    }
                }
            }
            else {
                targetEvent.setFormDataList(new ArrayList<FormData>());
            }

            if (targetForm == null) {
                targetForm = new FormData(odmTarget.getFormOid());
                targetEvent.getFormDataList().add(targetForm);
            }

            ItemGroupData targetItemGroup = null;
            if (targetForm.getItemGroupDataList() != null) {
                for (ItemGroupData igd : targetForm.getItemGroupDataList()) {
                    if (igd.getItemGroupOid().equals(odmTarget.getItemGroupOid())) {

                        if (mappingRecord.getItemGroupRepeatKey() != null && !mappingRecord.getItemGroupRepeatKey().equals("")) {
                            if (igd.getItemGroupRepeatKey() != null && igd.getItemGroupRepeatKey().equals(mappingRecord.getItemGroupRepeatKey())) {
                                targetItemGroup = igd;
                                break;
                            }
                        }
                        else {
                            targetItemGroup = igd;
                            break;
                        }
                    }
                }
            }
            else {
                targetForm.setItemGroupDataList(new ArrayList<ItemGroupData>());
            }

            if (targetItemGroup == null) {
                if (mappingRecord.getItemGroupRepeatKey() != null && !mappingRecord.getItemGroupRepeatKey().equals("")) {
                    targetItemGroup = new ItemGroupData(odmTarget.getItemGroupOid(), mappingRecord.getItemGroupRepeatKey());
                }
                else {
                    targetItemGroup = new ItemGroupData(odmTarget.getItemGroupOid());
                }
                targetForm.getItemGroupDataList().add(targetItemGroup);
            }

            if (targetItemGroup.getItemDataList() == null) {
                targetItemGroup.setItemDataList(new ArrayList<ItemData>());
            }
            targetItemGroup.addItemData(new ItemData(odmTarget.getItemOid(), value));

            result = true;
        }

        return result;
    }

    //endregion

    //region Patient

    //region DICOM

    public boolean addDicomStudyForSubject(DicomStudy ds) {
        return this.person != null && this.person.addDicomStudy(ds);
    }

    public boolean removeDicomStudyFromSubject(DicomStudy ds) {
        return this.person != null && this.person.removeDicomStudy(ds);
    }

    public boolean hasDicomStudyWithUid(String studyInstanceUid) {
        if (this.person != null) {
            return this.getPerson().hasDicomStudyWithUid(studyInstanceUid);
        }

        return Boolean.FALSE;
    }

    public DicomStudy getDicomStudyWithUid(String studyInstanceUid) {
        if (this.person != null) {
            return this.person.getDicomStudyWithUid(studyInstanceUid);
        }

        return null;
    }
    
    //endregion

    //region Specimen

    public void addSpecimenForSubject(AbstractSpecimen specimen) {

        // TODO: assign specimen to partner site
        // TODO: assign specimen to to study

        if (this.person != null) {

            if (this.person.getBioSpecimens() != null) {
                for (AbstractSpecimen existingSpecimen : this.person.getBioSpecimens()) {
                    // Master Sample
                    if (existingSpecimen instanceof Specimen) {

                        // Master Sample is EDTA blood
                        if (existingSpecimen.getKind().equals("BLD_EDTA")) {

                            // Aliquot Group does not exists
                            if (existingSpecimen.getChildren() == null || existingSpecimen.getChildren().size() == 0) {

                                // Create Aliquot Group of the same kind as provided derived aliquot sample
                                AliquotGroup aliquotGroup = new AliquotGroup();

                                // TODO: inherit data from master sample


                                specimen.addChild(aliquotGroup);
                            }

                            //TODO: get aliquot group of the same type as derived aliquot (it should exist now)
                            //TODO: set additional fields from annotations (centrifugation) from derived sample
                            //TODO: add derived sample to the group
                        }
                        // Master Sample is serum
                        else if (existingSpecimen.getKind().equals("SER")) {

                        }
                    }
                }
            }

        }
    }

    //endregion

    //region Study0

    public boolean isStudy0Subject() {
        if (this.person != null) {
            return this.person.findStudySubjectId(Constants.study0Identifier) != null;
        }

        return false;
    }

    public String getStudy0SSID() {
        if (this.person != null) {
            return this.person.findStudySubjectId(Constants.study0Identifier);
        }

        return null;
    }

    public String extractStudy0PureSSID() {
        if (this.person != null) {
            String ssid = this.person.findStudySubjectId(Constants.study0Identifier);
            if (ssid != null) {
                return ssid.replace(Constants.HISprefix, "");
            }
        }

        return null;
    }

    //endregion

    //endregion

    //region EDC data

    public void groupElementsByOid() {
        if (this.studyEventDataList != null) {

            List<EventData> uniqueEventDataList = new ArrayList<>();

            for (EventData ed : this.studyEventDataList) {
                boolean eventDataExist = false;

                for (EventData newEd : uniqueEventDataList) {
                    if (newEd.equals(ed)) {
                        eventDataExist = true;

                        // Add the referenced forms
                        newEd.getFormDataList().addAll(ed.getFormDataList());
                        break;
                    }
                }

                if (!eventDataExist) {
                    uniqueEventDataList.add(ed);
                }
            }

            this.setStudyEventDataList(uniqueEventDataList);
        }
    }

    //region EventData

    public int getEventOccurrencesCountForEventDef(String eventDefOid) {
        int i = 0;

        if (this.studyEventDataList != null) {
            for (EventData ed : this.studyEventDataList) {
                if (ed.getStudyEventOid().equals(eventDefOid)) {
                    // Ignore invalid events
                    // they have data but have been removed from study (definition is not in metadata anymore)
                    if (!EnumEventDataStatus.INVALID.toString().equals(ed.getStatus())) {
                        i++;
                    }
                }
            }
        }

        return i;
    }

    public int getEventOccurrencesCountForEventDef(EventDefinition eventDef) {
        return this.getEventOccurrencesCountForEventDef(eventDef.getOid());
    }
    
    /**
     * Get only event occurrences that actually contain some CRF data (ignore the scheduled one)
     *
     * @return List of EventData entities
     */
    public List<EventData> getEventOccurrences() {
        List<EventData> results = new ArrayList<>();

        if (this.studyEventDataList != null) {
            for (EventData ed : this.studyEventDataList) {
                // Ignore events that have data but have been removed from study (Definition is not in metadata)
                if (!EnumEventDataStatus.INVALID.toString().equals(ed.getStatus())) {
                    if (ed.getFormDataList() != null) {
                        results.add(ed);
                    }
                }
            }
        }

        return results;
    }

    public List<EventData> getEventOccurrencesForEventDef(String eventDefOid) {
        List<EventData> results = new ArrayList<>();

        if (eventDefOid != null && !eventDefOid.isEmpty()) {
            if (this.studyEventDataList != null) {
                for (EventData ed : this.studyEventDataList) {
                    if (ed.getStudyEventOid().equals(eventDefOid)) {
                        // Ignore invalid events
                        // they have data but have been removed from study (definition is not in metadata anymore)
                        if (!EnumEventDataStatus.INVALID.toString().equals(ed.getStatus())) {
                            results.add(ed);
                        }
                    }
                }
            }
        }

        return results;
    }

    public List<EventData> getEventOccurrencesForEventDef(EventDefinition eventDef) {
        return this.getEventOccurrencesForEventDef(eventDef.getOid());
    }

    public List<EventData> getEventOccurrencesForEventDefs(List<String> eventDefOidList) {
        List<EventData> results = new ArrayList<>();

        if (this.studyEventDataList != null) {
            for (EventData ed : this.studyEventDataList) {
                for (String eventDefOid : eventDefOidList) {
                    if (ed.getStudyEventOid().equals(eventDefOid)) {
                        // Ignore invalid events
                        // they have data but have been removed from study (definition is not in metadata anymore)
                        if (!EnumEventDataStatus.INVALID.toString().equals(ed.getStatus())) {
                            results.add(ed);
                        }
                    }
                }
            }
        }

        return results;
    }

    public List<EventData> getEventOccurrencesForEvenDefs(List<EventDefinition> eventDefs) {
        List<String> eventDefOidList = new ArrayList<>();
        for (EventDefinition eventDef : eventDefs) {
            eventDefOidList.add(eventDef.getOid());
        }

        return this.getEventOccurrencesForEventDefs(eventDefOidList);
    }

    public EventData getEventOccurrenceForEventDef(String eventDefOid, int repeatKey) {
        if (this.studyEventDataList != null) {
            for (EventData ed : this.studyEventDataList) {
                if (ed.getStudyEventOid().equals(eventDefOid) && ed.getStudyEventRepeatKey().equals(String.valueOf(repeatKey))) {
                    return ed;
                }
            }
        }

        return null;
    }

    public EventData getEventOccurrenceForEventDef(EventDefinition eventDef, int repeatKey) {
       return this.getEventOccurrenceForEventDef(eventDef.getOid(), repeatKey);
    }
    
    //endregion

    //region ItemData

    public ItemData getItemDataForItemDef(EventData eventData, ItemDefinition dicomItemDef) {
        if (dicomItemDef != null) {
            for (EventData ed : this.studyEventDataList) {
                if (ed.getStudyEventOid().equals(eventData.getStudyEventOid()) && ed.getStudyEventRepeatKey().equals(eventData.getStudyEventRepeatKey())) {
                    if (ed.getFormDataList() != null) {
                        for (FormData fd : ed.getFormDataList()) {
                            if (fd.getItemGroupDataList() != null) {
                                for (ItemGroupData igd : fd.getItemGroupDataList()) {
                                    if (igd.getItemDataList() != null) {
                                        for (ItemData id : igd.getItemDataList()) {
                                            if (id.getItemOid().equals(dicomItemDef.getOid())) {
                                                return id;
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

        return null;
    }

    public List<ItemData> findAnnotatedItemData(List<CrfFieldAnnotation> annotations) {
        List<ItemData> result = new ArrayList<>();

        if (annotations != null) {
            for (CrfFieldAnnotation annotation : annotations) {
                result.addAll(this.findAnnotatedItemData(annotation));
            }
        }

        return result;
    }

    public List<ItemData> findAnnotatedItemData(CrfFieldAnnotation annotation) {
        List<ItemData> result = new ArrayList<>();

        if (annotation != null) {
            for (EventData eventData : this.studyEventDataList) {
                result.addAll(eventData.findAnnotatedItemData(annotation));
            }
        }

        return result;
    }

    //endregion

    //endregion

    //region Metadata

    public void linkOdmDefinitions(Odm odm) {
        if (odm != null) {
            // Subject does not have metadata, Link next level in hierarchy (StudyEvent)
            if (this.studyEventDataList != null) {

                // EventDefinition linking
                for (EventData eventData : this.studyEventDataList) {
                    eventData.linkOdmDefinitions(odm);
                }
            }
        }
    }

    //endregion

    // region treatment arms



    // endregion

    //endregion


}
