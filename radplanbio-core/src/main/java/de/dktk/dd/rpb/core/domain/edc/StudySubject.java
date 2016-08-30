/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappedOdmItem;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappingRecord;
import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;

import de.dktk.dd.rpb.core.util.Constants;
import org.apache.log4j.Logger;
import org.openclinica.ws.beans.StudySubjectWithEventsType;
import org.openclinica.ws.beans.StudyType;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * StudySubject domain entity
 *
 * StudySubject in RPB is transient entity based on CDISC ODM SubjectData with OpenClinica extensions
 *
 * @author tomas@skripcak.net
 * @since 09 Dec 2014
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="SubjectData")
public class StudySubject implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(StudySubject.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name="StudySubjectID", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String studySubjectId;

    @XmlAttribute(name="SecondaryID", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String secondaryId; // Other ID, e.g. from HIS, biobank...

    @XmlAttribute(name="UniqueIdentifier", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String pid; // PersonID or pseudonym

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

    @XmlTransient
    private Person person; // Patient identity for PID Generator (Mainzelliste)

    @XmlTransient
    private Study study; // EDC study

    @XmlTransient
    private Boolean isEnabled;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    //endregion

    //region Constructors

    public StudySubject() {
        this.person = new Person();
        this.isEnabled = Boolean.TRUE;
    }

    public StudySubject(StudySubjectWithEventsType sswet) {
        this(sswet, null);
    }

    public StudySubject(StudySubjectWithEventsType sswet, StudyType st) {
        this.studySubjectId = sswet.getLabel();
        this.secondaryId = sswet.getSecondaryLabel();
        this.enrollmentDate =  sswet.getEnrollmentDate().toString();

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
            this.sex = sswet.getSubject().getGender().value();
        }

        // Associate with study
        if (st != null) {
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

    //endregion

    //region Properties

    //region Id

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

    //region StudySubjectId

    public String getStudySubjectId() {
        return this.studySubjectId;
    }

    public void setStudySubjectId(String value) {
        this.studySubjectId = value;
    }

    //endregion

    //region ProtocolStudySubjectId

    public String getProtocolStudySubjectId() {
        String result = "";

        if (this.study != null && this.studySubjectId != null) {
            result = this.study.getOid() + "-" + this.studySubjectId;
        }

        return result;
    }

    //endregion

    //region SecondaryId

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

    @SuppressWarnings("unused")
    public Integer getYearOfBirth() {
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

    //endregion

    //region EnrollmentDate

    public String getEnrollmentDate() { return this.enrollmentDate; }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Date getDateEnrollment() {
        if (this.enrollmentDate != null && !this.enrollmentDate.equals("")) {
            DateFormat format = new SimpleDateFormat(Constants.OC_DATEFORMAT);
            try {
                return format.parse(this.enrollmentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String getDateEnrollmentString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getDateEnrollment();
        return date != null ? format.format(date) : null;
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

    //region StudyEventData List

    public List<EventData> getStudyEventDataList() {
        return this.studyEventDataList;
    }

    @SuppressWarnings("unused")
    public void setStudyEventDataList(List<EventData> list) {
        this.studyEventDataList = list;
    }

    //endregion

    //region Person

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public boolean addDicomStudyForSubject(DicomStudy ds) {
        return this.person != null && this.person.addDicomStudy(ds);
    }

    public boolean removeDicomStudyFromSubject(DicomStudy ds) {
        return this.person != null && this.person.removeDicomStudy(ds);
    }

    //endregion

    //region Study

    public Study getStudy() { return this.study; }

    public void setStudy(Study study) {
        this.study = study;
    }

    //endregion

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
        return identifiableHashBuilder.hash(log, this, this.subjectKey);
    }

    /**
     * Construct a readable string representation for this RtStructType instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("studySubjectId", this.studySubjectId)
                .add("subjectKey", this.subjectKey)
                .toString();
    }

    //endregion

    //region Methods

    //region Mapping

    public boolean populateDataField(MappingRecord mappingRecord, String value) {
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

    //region DICOM

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

    //region EDC data

    //region Event data

    public int getEventOccurrencesCountForEventDef(EventDefinition eventDef) {
        List<EventData> results = new ArrayList<>();

        if (this.studyEventDataList != null) {
            for (EventData ed : this.studyEventDataList) {
                if (ed.getStudyEventOid().equals(eventDef.getOid())) {
                    results.add(ed);
                }
            }
        }

        return results.size();
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

        if (eventDefOid != null && this.studyEventDataList != null) {
            for (EventData ed : this.studyEventDataList) {
                if (ed.getStudyEventOid().equals(eventDefOid)) {
                    results.add(ed);
                }
            }
        }

        return results;
    }

    public List<EventData> getEventOccurrencesForEventDef(EventDefinition eventDef) {
        return this.getEventOccurrencesForEventDef(eventDef.getOid());
    }

    public List<EventData> getEventOccurrencesForEvenDefs(List<EventDefinition> eventDefs) {
        List<EventData> results = new ArrayList<>();

        if (this.studyEventDataList != null) {
            for (EventData ed : this.studyEventDataList) {
                for (EventDefinition eventDef : eventDefs) {
                    if (ed.getStudyEventOid().equals(eventDef.getOid())) {
                        results.add(ed);
                    }
                }
            }
        }

        return results;

    }

    public EventData getEventOccurrenceForEventDef(EventDefinition eventDef, int repeatKey) {
        if (this.studyEventDataList != null) {
            for (EventData ed : this.studyEventDataList) {
                if (ed.getStudyEventOid().equals(eventDef.getOid()) && ed.getStudyEventRepeatKey().equals(String.valueOf(repeatKey))) {
                    return ed;
                }
            }
        }

        return null;
    }

    //endregion

    //region Item data

    public ItemData getItemDataForItemDef(EventData eventData, ItemDefinition dicomItemDef) {
        if (dicomItemDef != null) {
            for (EventData ed : this.getStudyEventDataList()) {
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

    //endregion

}