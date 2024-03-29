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
import de.dktk.dd.rpb.core.domain.edc.mapping.MappedOdmItem;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CDISC ODM XML entity root
 * supporting OpenClinica Extensions
 *
 * @author tomas@skripcak.net
 * @since 15 Dec 2014
 */
@XmlRootElement(name = "ODM")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.cdisc.org/ns/odm/v1.3")
public class Odm implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(Odm.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name = "FileOID")
    private String fileOid;

    @XmlAttribute(name = "Description")
    private String description;

    @XmlAttribute(name = "CreationDateTime")
    private String creationDateTime;

    @XmlAttribute(name = "FileType")
    private String fileType;

    @XmlAttribute(name = "ODMVersion")
    private String odmVersion;

    @XmlElement(name = "Study")
    private List<Study> studies;

    private Study parentStudy;

    @XmlElement(name = "ClinicalData")
    private List<ClinicalData> clinicalDataList;

    @XmlElement(name = "AdminData")
    private List<AdminData> adminDataList;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    private StudyDetails studyDetails;

    //endregion

    //region Constructors

    /**
     * Default constructor
     */
    public Odm() {
        this.studies = new ArrayList<>();
    }

    /**
     * Initialise ODM based on another ODM object graph
     *
     * @param otherOdm object graph
     */
    public Odm(Odm otherOdm) {
        this.description = "New ODM model.";

        // 1. try to initialize according to ODM metadata if present
        if (otherOdm.getStudies() != null && otherOdm.getStudies().size() > 0) {
            // The fist one is the parent study
            String studyOid = otherOdm.getStudies().get(0).getOid();
            if (otherOdm.getStudies().get(0).getMetaDataVersion() != null) {
                String metaDataVersionOid = otherOdm.getStudies().get(0).getMetaDataVersion().getOid();
                this.clinicalDataList = new ArrayList<>();
                this.clinicalDataList.add(new ClinicalData(studyOid, metaDataVersionOid));
            }
        }
        // 2. when no metadata present initialized according to clinical data
        else if (otherOdm.getClinicalDataList() != null && otherOdm.getClinicalDataList().size() > 0) {
            // The first one is the parent study
            String studyOid = otherOdm.getClinicalDataList().get(0).getStudyOid();
            String metadataVersionOid = otherOdm.getClinicalDataList().get(0).getMetaDataVersionOid();

            this.clinicalDataList = new ArrayList<>();
            this.clinicalDataList.add(new ClinicalData(studyOid, metadataVersionOid));
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

    //region FileOID

    public String getFileOid() {
        return this.fileOid;
    }

    public void setFileOid(String value) {
        this.fileOid = value;
    }

    //endregion

    //region Description

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    //endregion

    //region CreationDateTime

    @SuppressWarnings("unused")
    public String getCreationDateTime() {
        return this.creationDateTime;
    }

    public void setCreationDateTime(String value) {
        this.creationDateTime = value;
    }

    //endregion

    //region FileType

    @SuppressWarnings("unused")
    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String value) {
        this.fileType = value;
    }

    //endregion

    //region ODMVersion

    @SuppressWarnings("unused")
    public String getOdmVersion() {
        return this.odmVersion;
    }

    public void setOdmVersion(String value) {
        this.odmVersion = value;
    }

    //endregion

    //region Studies

    public List<Study> getStudies() {
        return this.studies;
    }

    public void setStudies(List<Study> list) {
        this.studies = list;
    }

    //endregion

    /**
     * The parent study property is set to align the site studies to the parent.
     *
     * @return EDC Study representation of the parent study
     */
    public Study getParentStudy() {
        return parentStudy;
    }

    /**
     * The parent study property is set to align the site studies to the parent.
     *
     * @param parentStudy EDC Study representation of the parent study
     */
    public void setParentStudy(Study parentStudy) {
        this.parentStudy = parentStudy;
    }


    //region Clinical Data List

    public List<ClinicalData> getClinicalDataList() {
        return this.clinicalDataList;
    }

    @SuppressWarnings("unused")
    public void setClinicalDataList(List<ClinicalData> list) {
        this.clinicalDataList = list;
    }

    //endregion

    //region StudyDetails

    public StudyDetails getStudyDetails() {
        studyDetails = null;

        if (this.studies != null && this.studies.size() > 0) {
            // the first study is the parents study and has the correct configuration details
            if (this.studies.get(0).getMetaDataVersion().getStudyDetails() != null) {
                studyDetails = this.studies.get(0).getMetaDataVersion().getStudyDetails();
            }
        }

        return studyDetails;
    }

    //endregion

    //region AdminData

    public List<AdminData> getAdminDataList() {
        return adminDataList;
    }

    public void setAdminDataList(List<AdminData> adminDataList) {
        this.adminDataList = adminDataList;
    }


    //endregion

//    public String getExpirationDateTime() {
//        return expirationDateTime;
//    }
//
//    public void setExpirationDateTime(String expirationDateTime) {
//        this.expirationDateTime = expirationDateTime;
//    }

    //endregion

    //region Methods

    //region Metadata

    //region Study

    public Study findFirstStudyOrNone() {
        if (this.studies != null) {
            if (this.studies.size() > 0) {
                return this.studies.get(0);
            }
        }

        return null;
    }

    public Study findUniqueStudyOrNone(String studyOid) {
        if (studyOid != null) {
            if (this.studies != null) {
                for (Study study : this.studies) {
                    if (studyOid.equals(study.getOid())) {
                        return study;
                    }
                }
            }
        }

        return null;
    }

    //endregion

    //region EventDefinition

    public List<EventDefinition> findEventDefinitions() {
        MetaDataVersion metadata = this.findUniqueMetadataOrNone();
        if (metadata != null) {
            return metadata.getStudyEventDefinitions();
        }

        return null;
    }

    public EventDefinition findUniqueEventDefinitionOrNone(String eventDefinitionOid) {
        if (eventDefinitionOid != null) {
            MetaDataVersion metadata = this.findUniqueMetadataOrNone();
            if (metadata != null) {
                return metadata.findUniqueEventDefinition(eventDefinitionOid);
            }
        }

        return null;
    }

    //endregion

    //region FormDefinition

    public FormDefinition findUniqueFormDefinitionOrNone(String formDefinitionOid) {
        if (formDefinitionOid != null) {
            MetaDataVersion metadata = this.findUniqueMetadataOrNone();
            if (metadata != null && metadata.getStudyEventDefinitions() != null) {
                for (EventDefinition eventDefinition : metadata.getStudyEventDefinitions()) {
                    for (FormDefinition formDefinition : eventDefinition.getFormDefs()) {
                        if (formDefinitionOid.equals(formDefinition.getOid())) {
                            return formDefinition;
                        }
                    }
                }
            }
        }

        return null;
    }

    //endregion

    //region ItemGroupDefinition

    public ItemGroupDefinition findUniqueItemGroupDefinitionOrNone(String itemGroupDefinitionOid) {
        if (itemGroupDefinitionOid != null) {
            MetaDataVersion metadata = this.findUniqueMetadataOrNone();
            if (metadata != null && metadata.getStudyEventDefinitions() != null) {
                for (EventDefinition eventDefinition : metadata.getStudyEventDefinitions()) {
                    for (FormDefinition formDefinition : eventDefinition.getFormDefs()) {
                        for (ItemGroupDefinition itemGroupDefinition : formDefinition.getItemGroupDefs()) {
                            if (itemGroupDefinitionOid.equals(itemGroupDefinition.getOid())) {
                                return itemGroupDefinition;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    //endregion

    //region ItemDefinition

    public ItemDefinition findUniqueItemDefinitionOrNone(String itemDefinitionOid) {
        if (itemDefinitionOid != null) {
            MetaDataVersion metadata = this.findUniqueMetadataOrNone();
            if (metadata != null && metadata.getStudyEventDefinitions() != null) {
                for (EventDefinition eventDefinition : metadata.getStudyEventDefinitions()) {
                    for (FormDefinition formDefinition : eventDefinition.getFormDefs()) {
                        for (ItemGroupDefinition itemGroupDefinition : formDefinition.getItemGroupDefs()) {
                            for (ItemDefinition itemDefinition : itemGroupDefinition.getItemDefs()) {
                                if (itemDefinitionOid.equals(itemDefinition.getOid())) {
                                    return itemDefinition;
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

    //region Data

    //region Odm

    public Odm findUniqueOdmOrNoneForSubject(String studySubjectIdentifier) {

        Odm resultOdm = new Odm(this);
        resultOdm.setDescription(null);

        for (StudySubject ss : this.getClinicalDataList().get(0).getStudySubjects()) {

            if (studySubjectIdentifier.equals(ss.getStudySubjectId()) || studySubjectIdentifier.equals(ss.getSubjectKey())) {

                resultOdm.getClinicalDataList().get(0).getStudySubjects().add(ss);
                return resultOdm;
            }
        }

        return null;
    }

    //endregion

    //region ClinicalData

    public ClinicalData findUniqueClinicalDataOrNone() {
        // The first one is the main study clinical data
        if (this.clinicalDataList != null && this.clinicalDataList.size() >= 1) {
            return this.clinicalDataList.get(0);
        }

        return null;
    }

    //endregion

    //region StudySubject

    public StudySubject findFirstStudySubjectOrNone() {
        if (this.clinicalDataList != null) {
            for (ClinicalData clinicaData : this.clinicalDataList) {
                if (clinicaData.getStudySubjects() != null && clinicaData.getStudySubjects().size() == 1) {
                    return clinicaData.getStudySubjects().get(0);
                }
            }
        }

        return null;
    }

    public List<StudySubject> getStudySubjects() {
        List<StudySubject> studySubjectList = new ArrayList<>();
        if (this.clinicalDataList != null) {
            for (ClinicalData clinicaData : this.clinicalDataList) {
                studySubjectList.addAll(clinicaData.getStudySubjects());
            }
        }

        return studySubjectList;
    }

    public StudySubject findUniqueStudySubjectOrNone(String studySubjectIdentifier) {
        if (studySubjectIdentifier != null) {
            if (this.clinicalDataList != null) {
                for (ClinicalData clinicaData : this.clinicalDataList) {
                    if (clinicaData.getStudySubjects() != null && clinicaData.getStudySubjects().size() == 1) {
                        for (StudySubject studySubject : clinicaData.getStudySubjects()) {
                            if (studySubjectIdentifier.equals(studySubject.getStudySubjectId()) || studySubjectIdentifier.equals(studySubject.getSubjectKey())) {
                                return studySubject;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean addStudySubject(StudySubject ss) {
        if (this.clinicalDataList != null) {
            for (ClinicalData clinicaData : this.clinicalDataList) {
                if (clinicaData.getStudySubjects() != null) {
                    return clinicaData.getStudySubjects().add(ss);
                }
            }
        }

        return false;
    }

    //endregion

    //region EventData

    public EventData findEventData(String eventDefinitionOid, int repeatKey) {
        EventData result = null;
        for (EventData ed : this.findEventData(eventDefinitionOid)) {
            if (ed.getStudyEventRepeatKey() != null && !ed.getStudyEventRepeatKey().isEmpty()) {
                if (repeatKey == Integer.parseInt(ed.getStudyEventRepeatKey())) {
                    result = ed;
                    break;
                }
            }
        }

        return result;
    }

    public List<EventData> findEventData(String eventDefinitionOid) {
        List<EventData> result = new ArrayList<>();
        if (eventDefinitionOid != null && !eventDefinitionOid.isEmpty()) {
            ClinicalData data = this.findUniqueClinicalDataOrNone();
            if (data != null && data.getStudySubjects() != null) {
                if (data.getStudySubjects().size() == 1) {
                    StudySubject studySubject = data.getStudySubjects().get(0);
                    for (EventData eventData : studySubject.getEventOccurrencesForEventDef(eventDefinitionOid)) {
                        result.add(eventData);
                    }
                }
            }
        }

        return result;
    }

    public EventData findMatchingEventData(EventData eventData) {
        EventData result = null;

        if (eventData != null) {

            // Lookup for existing event data
            List<EventData> existingEventDataList = this.findEventData(eventData.getStudyEventOid());
            if (existingEventDataList != null) {

                // Iterate over and try to match
                for (EventData sed : existingEventDataList) {
                    EventDataMatch edm = new EventDataMatch(sed, eventData);
                    if (edm.getMatch()) {
                        result = sed;
                        break;
                    }
                }
            }
        }

        return result;
    }

    public int calculateNextEventDataRepeatKey(EventData eventData) {
        int result = 0;

        // Check whether we are looking for identifiable eventData (according EventDefinition OID)
        if (eventData != null && eventData.getStudyEventOid() != null && !eventData.getStudyEventOid().isEmpty()) {

            // Lookup for existing EventData
            List<EventData> existingEventDataList = this.findEventData(eventData.getStudyEventOid());
            if (existingEventDataList != null) {
                for (EventData ed : existingEventDataList) {
                    // Check if there is a scheduled event that could be used and use the lowest one
                    if (ed.getStatus().equals(EnumEventDataStatus.SCHEDULED.toString())) {
                        if (result < Integer.parseInt(ed.getStudyEventRepeatKey())) {
                            result = Integer.parseInt(ed.getStudyEventRepeatKey());
                        }
                    }
                }

                // If no scheduled empty event found generate next repeat key
                if (result < 1) {
                    result = existingEventDataList.size() + 1;
                }
            }
        }

        return result;
    }

    //endregion

    //endregion

    /**
     * Correct messed up ODM where multiple unique OID nodes exists however they have mergeable content
     */
    public void groupElementsByOid() {
        // Study metadata
        if (this.studies != null) {
            for (Study s : this.studies) {
                if (s.getMetaDataVersion() != null) {
                    s.getMetaDataVersion().groupElementsByOid();
                }
            }
        }
        // Clinical data
        if (this.clinicalDataList != null) {
            for (ClinicalData cd : this.clinicalDataList) {
                cd.groupElementsByOid();
            }
        }
    }

    /**
     * Update the entities hierarchy CDISC element definitions according to the entities references from metadata
     */
    public void updateHierarchy() {
        for (Study s : this.studies) {
            // Study metadata
            MetaDataVersion mdv = s.getMetaDataVersion();

            // Update form definitions according to form references
            for (EventDefinition ed : mdv.getStudyEventDefinitions()) {

                setOrdinalFromProtocolDefinition(mdv, ed);

                setDefinitionFromEventDefinitionDetails(ed);


                for (FormDefinition fr : ed.getFormRefs()) {
                    for (FormDefinition fd : mdv.getFormDefinitions()) {
                        if (fr.getFormOid() != null && fr.getFormOid().equals(fd.getOid())) {
                            List<FormDefinition> formDefinitionList = ed.getFormDefs();
                            if (!formDefinitionList.contains(fd)) {
                                formDefinitionList.add(fd);
                            }

                            break;
                        }
                    }
                }

                // Update item group definitions according to item group references
                for (FormDefinition fd : ed.getFormDefs()) {
                    for (ItemGroupDefinition igd : mdv.getItemGroupDefinitions()) {
                        for (ItemGroupDefinition igr : fd.getItemGroupRefs()) {
                            if (igr.getItemGroupOid() != null && igr.getItemGroupOid().equals((igd.getOid()))) {
                                List<ItemGroupDefinition> itemGroupDefinitionList = fd.getItemGroupDefs();
                                if (!itemGroupDefinitionList.contains(igd)) {
                                    itemGroupDefinitionList.add(igd);
                                }
                                break;
                            }
                        }
                    }

                    // Update item definitions according to item references
                    for (ItemGroupDefinition igd : fd.getItemGroupDefs()) {
                        for (ItemDefinition id : mdv.getItemDefinitions()) {
                            for (ItemDefinition ir : igd.getItemRefs()) {

                                if (ir.getItemOid() != null && ir.getItemOid().equals((id.getOid()))) {
                                    // copy orderNumber and itemOid from the reference to the definition
                                    id.setOrderNumber(ir.getOrderNumber());
                                    id.setItemOid(ir.getItemOid());
                                    // copy formOids property to filter items that do not belong to the specific form version
                                    ir.setFormOids(id.getFormOids());

                                    igd.addItemDef(id);
                                    break;
                                }
                            }

                            // Update code list reference for item according to reference, if it is coded item
                            if (id.getCodeListRef() != null) {
                                for (CodeListDefinition cld : mdv.getCodeListDefinitions()) {
                                    if (cld.getOid().equals(id.getCodeListRef().getCodeListOid())) {
                                        id.setCodeListDef(cld);
                                        break;
                                    }
                                }
                            } else if (id.getMultiSelectListRef() != null) {
                                for (MultiSelectListDefinition msld : mdv.getMultiSelectListDefinitions()) {
                                    if (msld.getId().equals(id.getMultiSelectListRef().getMultiSelectListId())) {
                                        id.setMultiSelectListDef(msld);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Setup OpenClinica study parameters
        if (this.getStudyDetails() != null) {
            this.getStudyDetails().reloadParameters();
        }

    }

    /**
     * If it is null, the definition property will be updated with information from the EventDefinitionDetails
     *
     * @param ed EventDefinition
     */
    private void setDefinitionFromEventDefinitionDetails(EventDefinition ed) {
        if (ed.getEventDefinitionDetails() != null && ed.getDescription() == null) {
            if (ed.getEventDefinitionDetails().getDescription() != null) {
                ed.setDescription(ed.getEventDefinitionDetails().getDescription());
            }
        }
    }

    /**
     * In ODM, the ordinal is mapped in the protocol property via StudyEventRef. This function updates the ordinal
     * property of the EventDefinition object, based on that mapping
     *
     * @param mdv MetaDataVersion
     * @param ed  EventDefinition
     */
    private void setOrdinalFromProtocolDefinition(MetaDataVersion mdv, EventDefinition ed) {
        String oid = ed.getOid();
        Protocol protocol = mdv.getProtocolDefinitions();
        EventReference reference = protocol.getEventReferenceByOid(oid);
        ed.setOrdinal(reference.getOrdinal());
    }

    /**
     * Get CDISC ODM item definition entity according to target (StudyEventOID, FormOID, ItemGroupOID, ItemOID)
     *
     * @param target (StudyEventOID, FormOID, ItemGroupOID, ItemOID)
     * @return ItemDefinition entity
     */
    public ItemDefinition getItemDefinition(MappedOdmItem target) {
        if (this.getStudies() != null && target != null) {
            for (Study s : this.getStudies()) {
                if (s.getMetaDataVersion() != null) {
                    for (EventDefinition ed : s.getMetaDataVersion().getStudyEventDefinitions()) {
                        if (ed.getOid().equals(target.getStudyEventOid())) {
                            for (FormDefinition fd : ed.getFormDefs()) {
                                if (fd.getOid().equals(target.getFormOid())) {
                                    for (ItemGroupDefinition igd : fd.getItemGroupDefs()) {
                                        if (igd.getOid().equals(target.getItemGroupOid())) {
                                            for (ItemDefinition id : igd.getItemDefs()) {
                                                if (id.getOid().equals(target.getItemOid())) {
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
        }

        return null;
    }

    /**
     * Change Subject keys of subject in clinical data list with the new ones
     *
     * @param freshSubjects newly loaded subject with correct subject keys
     */
    public Boolean updateSubjectKeys(List<StudySubject> freshSubjects, EnumStudySubjectIdGeneration idStrategy) {
        Boolean result = false;

        if (this.clinicalDataList.size() > 0) {
            for (StudySubject ss : this.clinicalDataList.get(0).getStudySubjects()) {
                for (StudySubject fss : freshSubjects) {

                    // For study with manual SSID generation, use SSID to match patients
                    if (EnumStudySubjectIdGeneration.MANUAL.equals(idStrategy)) {
                        if (fss.getStudySubjectId().equals(ss.getStudySubjectId())) {
                            ss.setSubjectKey(fss.getSubjectKey());
                            ss.setPid(fss.getPid());
                            break;
                        }
                    }
                    // For study with automatic SSID generation, use PID to match patients
                    else if (EnumStudySubjectIdGeneration.AUTO.equals((idStrategy))) {
                        if (fss.getPid().equals(ss.getPid())) {
                            ss.setStudySubjectId(fss.getStudySubjectId());
                            ss.setSubjectKey(fss.getSubjectKey());
                            break;
                        }
                    } else {
                        if (fss.getStudySubjectId().equals(ss.getStudySubjectId())) {
                            ss.setSubjectKey(fss.getSubjectKey());
                            ss.setPid(fss.getPid());
                            break;
                        }
                    }
                }
            }

            result = true;
        }

        return result;
    }

    /**
     * Set the study subject list in ODM clinical data collection
     *
     * @param studySubjects subjects
     * @return true when successful
     */
    public Boolean populateSubjects(List<StudySubject> studySubjects) {
        Boolean result = false;

        if (this.clinicalDataList.size() > 0) {
            this.clinicalDataList.get(0).setStudySubjects(studySubjects);
            result = true;
        }

        return result;
    }

    /**
     * Split this ODM object graph to list of ODM objects with specified limited set of study subjects
     *
     * @param maxSubjectsPerOdm maximum number of subjects per ODM
     * @return list of ODM objects
     */
    public List<Odm> splitToList(Integer maxSubjectsPerOdm) {
        List<Odm> result = new ArrayList<>();

        Odm newOdm = new Odm(this);
        newOdm.setDescription(null);
        result.add(newOdm);

        int j = 0;

        for (StudySubject ss : this.getClinicalDataList().get(0).getStudySubjects()) {
            if (j < maxSubjectsPerOdm) {
                newOdm.getClinicalDataList().get(0).getStudySubjects().add(ss);
                j++;
            } else {
                newOdm = new Odm(this);
                newOdm.setDescription(null);
                result.add(newOdm);

                j = 0;

                newOdm.getClinicalDataList().get(0).getStudySubjects().add(ss);
                j++;
            }
        }

        return result;
    }

    /**
     * Sort ItemGroupData elements so that the repeating groups are the last
     */
    public void sortItemGroupData() {
        if (this.clinicalDataList != null) {
            for (ClinicalData cd : this.clinicalDataList) {
                if (cd.getStudySubjects() != null) {
                    for (StudySubject ss : cd.getStudySubjects()) {
                        if (ss.getStudyEventDataList() != null) {
                            for (EventData ed : ss.getStudyEventDataList()) {
                                if (ed.getFormDataList() != null) {
                                    for (FormData fd : ed.getFormDataList()) {
                                        fd.sortItemGroupData();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove unnecessary attributes that do not need to be serialised to XML
     */
    public void cleanAttributesUnnecessaryForImport() {

        // In general these properties are not important for import (clear them)
        this.setOdmVersion(null);
        //this.setExpirationDateTime(null);
        this.setFileType(null);
        this.setDescription(null);
        this.setCreationDateTime(null);
        this.setFileOid(null);
        this.setStudies(null);
        this.setAdminDataList(null);

        // Also remove unnecessary attributes from StudySubject clinical data (these are OC extensions)
        for (ClinicalData cd : this.getClinicalDataList()) {

            cd.setMetaDataVersionOid(null);

            // Collect keys of disabled subjects (we do not want to import them)
            List<String> subjectList = new ArrayList<>();
            for (StudySubject ss : cd.getStudySubjects()) {
                if (ss.getIsEnabled() == Boolean.FALSE) {
                    subjectList.add(ss.getSubjectKey());
                }
            }

            // Remove disabled subjects (we do not want to import them)
            for (String oid : subjectList) {
                for (StudySubject ss : cd.getStudySubjects()) {
                    if (ss.getSubjectKey().equals(oid)) {
                        cd.getStudySubjects().remove(ss);
                        break;
                    }
                }
            }

            // Continue with the rest of enabled subjects
            for (StudySubject ss : cd.getStudySubjects()) {
                ss.setPid(null);
                ss.setYearOfBirth(null);
                ss.setDateOfBirth(null);
                ss.setSex(null);
                ss.setSecondaryId(null);
                ss.setStudySubjectId(null);

                // Remove empty items from import
                for (EventData ed : ss.getStudyEventDataList()) {

                    ed.setStatus(null);
                    ed.setStartDate((String) null);

                    for (FormData fd : ed.getFormDataList()) {

                        fd.setStatus(null);
                        fd.setInterviewDate(null);
                        fd.setInterviewerName(null);
                        fd.setVersion(null);

                        for (ItemGroupData igd : fd.getItemGroupDataList()) {

                            // Collect oids of empty items (we do not want to import them)
                            List<String> idList = new ArrayList<>();
                            for (ItemData id : igd.getItemDataList()) {
                                if (id.getValue() != null && id.getValue().equals("")) {
                                    idList.add(id.getItemOid());
                                }
                            }

                            // Remove empty items (we do not want to import them)
                            for (String oid : idList) {
                                for (ItemData id : igd.getItemDataList()) {
                                    if (id.getItemOid().equals(oid)) {
                                        igd.getItemDataList().remove(id);
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

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Odm && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     *
     * @return hash
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this entity instance.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("fileOid", this.fileOid)
                .add("fileType", this.fileType)
                .add("odmVersion", this.odmVersion)
                .add("description", this.description)
                .toString();
    }

    //endregion

    //region Private

    //region MetaDataVersion

    /**
     * Return the first ODM MetaDataVersion if present, which should represent the main study metadata element
     *
     * @return First present ODM MetaDataVersion
     */
    private MetaDataVersion findUniqueMetadataOrNone() {

        if (this.studies != null && this.studies.size() >= 1) {
            if (this.studies.get(0).getMetaDataVersion() != null) {
                return this.studies.get(0).getMetaDataVersion();
            }
        }

        return null;
    }

    //endregion

    //endregion

    /**
     * By RPB convention, the ODM can just consist of the site specific study or the parent study with site specific studies.
     * In the second case, the parent study property needs to be set with "setParentStudy", because the information is not
     * part of the original ODM file. The RPB convention is that the overall study configuration is done in the parent study.
     * The site study inherits the configuration from the parent and consists of the clinical data of the site.
     *
     * @return Study parent study in case that there are more than one and the site specific study if there is just one
     * @throws MissingPropertyException
     */
    public Study getParentOrSiteStudyByConvention() throws MissingPropertyException {
        if (this.studies == null) {
            throw new MissingPropertyException("There are no study properties in the ODM: " + this.fileOid + " " + this.description);
        }

        if (this.studies.size() == 1) {
            return this.studies.get(0);
        }

        if (this.parentStudy == null) {
            throw new MissingPropertyException("The parent study is null. There is no clear way to find out the parent study in the ODM: " +
                    this.fileOid + " " + this.description
            );
        }
        ;

        for (Study study : this.getStudies()) {

            if (study.getOid().equals(parentStudy.getOcoid())) {
                return study;
            }

            throw new MissingPropertyException("There is no study with the oid " + parentStudy.getOcoid() + " in the ODM: " +
                    this.fileOid + " " + this.description
            );
        }

        return null;
    }
}
