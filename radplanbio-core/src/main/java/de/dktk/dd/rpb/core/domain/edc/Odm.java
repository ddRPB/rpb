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
import de.dktk.dd.rpb.core.domain.edc.mapping.MappedOdmItem;

import de.dktk.dd.rpb.core.util.Constants;
import org.apache.log4j.Logger;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * CDISC ODM XML entity root
 * supporting OpenClinica Extensions
 *
 * @author tomas@skripcak.net
 * @since 15 Dec 2014
 * @version 1.0.0
 */
@XmlRootElement(name="ODM")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace="http://www.cdisc.org/ns/odm/v1.3")
public class Odm implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Odm.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name="FileOID")
    private String fileOid;

    @XmlAttribute(name="Description")
    private String description;

    @XmlAttribute(name="CreationDateTime")
    private String creationDateTime;

    @XmlAttribute(name="FileType")
    private String fileType;

    @XmlAttribute(name="ODMVersion")
    private String odmVersion;

    @XmlElement(name="Study")
    private List<Study> studies;

    @XmlElement(name="ClinicalData")
    private List<ClinicalData> clinicalDataList;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

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
        StudyDetails result = null;

        if (this.studies != null && this.studies.size() == 1) {
            if (this.studies.get(0).getMetaDataVersion().getStudyDetails() != null) {
                result = this.studies.get(0).getMetaDataVersion().getStudyDetails();
            }
        }

        return result;
    }

    //endregion

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
            if (metadata != null && metadata.getStudyEventDefinitions() != null) {
                for (EventDefinition eventDefinition : metadata.getStudyEventDefinitions()) {
                    if (eventDefinitionOid.equals(eventDefinition.getOid())) {
                        return eventDefinition;
                    }
                }
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

    public StudySubject findUniqueStudySubjectOrNone(String studySubjectIdentifier) {
        if  (studySubjectIdentifier != null) {
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

    //endregion

    //region EventData

    public List<EventData> findEventData(String eventDefinitionOid) {
        List<EventData> result = new ArrayList<>();

        ClinicalData data = this.findUniqueClinicalDataOrNone();
        if (data != null && data.getStudySubjects() != null) {
            if (data.getStudySubjects().size() == 1) {
                StudySubject studySubject = data.getStudySubjects().get(0);
                for (EventData eventData : studySubject.getEventOccurrences()) {
                    if (eventData.getStudyEventOid().equals((eventDefinitionOid))) {
                        result.add(eventData);
                    }
                }
            }
        }

        return result;
    }

    public EventData findEventData(EventData eventData) {
        // Check whether we are looking for identifiable eventData (according EventDefinition OID)
        if (eventData != null && eventData.getStudyEventOid() != null && !eventData.getStudyEventOid().equals("")) {

            // Lookup for existing EventData
            List<EventData> existingEventDataList = this.findEventData(eventData.getStudyEventOid());
            if (existingEventDataList != null && existingEventDataList.size() > 0) {

                // Iterate over and look if there is event with the same start datetime
                for (EventData existingEventData : existingEventDataList) {
                    if (existingEventData.getStartDate() != null) {
                        if (existingEventData.getStartDate().equals(eventData.getStartDate())) {
                            return existingEventData;
                        }
                    }
                }
            }
            else {
                return null;
            }
        }

        return null;
    }

    public boolean containsEventData(EventData eventData) {
        // Check whether we are looking for identifiable eventData (according EventDefinition OID)
        if (eventData != null && eventData.getStudyEventOid() != null && !eventData.getStudyEventOid().equals("")) {

            // Lookup for existing EventData
            List<EventData> existingEventDataList = this.findEventData(eventData.getStudyEventOid());
            if (existingEventDataList != null && existingEventDataList.size() > 0) {

                // Iterate over and look if there is event with the same start datetime
                for (EventData existingEventData : existingEventDataList) {
                    if (existingEventData.getStartDate() != null) {
                        if (existingEventData.getStartDate().equals(eventData.getStartDate())) {
                            return true;
                        }
                    }
                }
            }
            else {
                return false;
            }
        }

        return false;
    }

    public int calculateNextEventDataRepeatKey(EventData eventData) {
        // Check whether we are looking for identifiable eventData (according EventDefinition OID)
        if (eventData != null && eventData.getStudyEventOid() != null && !eventData.getStudyEventOid().equals("")) {

            // Lookup for existing EventData
            List<EventData> existingEventDataList = this.findEventData(eventData.getStudyEventOid());
            if (existingEventDataList != null) {
                return existingEventDataList.size() + 1;
            }
            else {
                return 1;
            }
        }

        return 1;
    }

    //endregion

    //endregion

    /**
     * Update the entities hierarchy CDISC element definitions according to the entities references from metadata
     */
    public void updateHierarchy() {
        for (Study s : this.studies) {
            // Study metadata
            MetaDataVersion mdv = s.getMetaDataVersion();

            // Update form definitions according to form references
            for (EventDefinition ed : mdv.getStudyEventDefinitions()) {
                for (FormDefinition fr : ed.getFormRefs()) {
                    for (FormDefinition fd: mdv.getFormDefinitions()) {
                        if (fr.getFormOid() != null && fr.getFormOid().equals(fd.getOid())) {
                            ed.getFormDefs().add(fd);
                            break;
                        }
                    }
                }

                // Update item group definitions according to item group references
                for (FormDefinition fd : ed.getFormDefs()) {
                    for (ItemGroupDefinition igd : mdv.getItemGroupDefinitions()) {
                        for (ItemGroupDefinition igr : fd.getItemGroupRefs()) {
                            if (igr.getItemGroupOid() != null && igr.getItemGroupOid().equals((igd.getOid()))) {
                                fd.getItemGroupDefs().add(igd);
                                break;
                            }
                        }
                    }

                    // Update item definitions according to item references
                    for (ItemGroupDefinition igd: fd.getItemGroupDefs()) {
                        for (ItemDefinition id: mdv.getItemDefinitions()) {
                            for (ItemDefinition ir : igd.getItemRefs()) {
                                if (ir.getItemOid() != null && ir.getItemOid().equals((id.getOid()))) {
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
                            }
                            else if (id.getMultiSelectListRef() != null) {
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
     * Get CDISC ODM item definition entity according to target (StudyEventOID, FormOID, ItemGroupOID, ItemOID)
     * @param target (StudyEventOID, FormOID, ItemGroupOID, ItemOID)
     * @return ItemDefinition entity
     */
    public ItemDefinition getItemDefinition(MappedOdmItem target) {
        if (this.getStudies() != null && target != null) {
            for (Study s : this.getStudies()) {
                if (s.getMetaDataVersion() != null) {
                    for (EventDefinition ed : s.getMetaDataVersion().getStudyEventDefinitions()) {
                        if (ed.getOid().equals(target.getStudyEventOid()))  {
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
     * @param freshSubject newly loaded subject with correct subject keys
     */
    public Boolean updateSubjectKeys(List<StudySubject> freshSubject) {
        Boolean result = false;

        if (this.clinicalDataList.size() > 0) {
            for (StudySubject ss : this.clinicalDataList.get(0).getStudySubjects()) {
                for (StudySubject fss : freshSubject) {
                    if (fss.getStudySubjectId().equals(ss.getStudySubjectId())) {
                        ss.setSubjectKey(fss.getSubjectKey());
                        ss.setPid(fss.getPid());
                        break;
                    }
                }
            }

            result = true;
        }

        return result;
    }

    /**
     * Set the study subject list in ODM clinical data collection
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
            }
            else {
                newOdm= new Odm(this);
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
    public void sortItemGroupDataRepeatingLast() {
        if (this.clinicalDataList != null) {
            for (ClinicalData cd : this.clinicalDataList) {
                if (cd.getStudySubjects() != null) {
                    for (StudySubject ss : cd.getStudySubjects()) {
                        if (ss.getStudyEventDataList() != null) {
                            for (EventData ed : ss.getStudyEventDataList()) {
                                if (ed.getFormDataList() != null) {
                                    for (FormData fd : ed.getFormDataList()) {
                                        if (fd.getItemGroupDataList() != null && fd.getItemGroupDataList().size() > 0) {
                                            Collections.sort(fd.getItemGroupDataList(), new Comparator<ItemGroupData>() {
                                                public int compare(ItemGroupData groupData1, ItemGroupData groupData2) {

                                                    int result = 0;

                                                    // Ungrouped always first
                                                    if (groupData1.getItemGroupOid() != null && groupData1.getItemGroupOid().contains(Constants.OC_IG_UNGROUPED)) {
                                                        result = -1;
                                                    }
                                                    else if (groupData2.getItemGroupOid() != null && groupData2.getItemGroupOid().contains(Constants.OC_IG_UNGROUPED)) {
                                                        result = 1;
                                                    }
                                                    // Both are the same groups
                                                    else if (groupData1.getItemGroupOid() != null && groupData1.getItemGroupOid().equals(groupData2.getItemGroupOid())) {

                                                        // Both are repeating
                                                        if (groupData1.getItemGroupRepeatKey() != null && !groupData1.getItemGroupRepeatKey().equals("") &&
                                                                groupData2.getItemGroupRepeatKey() != null && !groupData2.getItemGroupRepeatKey().equals("")) {

                                                            if (Integer.parseInt(groupData1.getItemGroupRepeatKey()) < Integer.parseInt(groupData2.getItemGroupRepeatKey())) {
                                                                result = -1;
                                                            }
                                                            else if (Integer.parseInt(groupData1.getItemGroupRepeatKey()) > Integer.parseInt(groupData2.getItemGroupRepeatKey())) {
                                                                result = 1;
                                                            }
                                                            else {
                                                                result = 0;
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        // First is repeating group
                                                        if (groupData1.getItemGroupRepeatKey() != null && !groupData1.getItemGroupRepeatKey().equals("")) {
                                                            result = 1;
                                                        }
                                                        // Second is repeating group
                                                        else if (groupData2.getItemGroupRepeatKey() != null && !groupData2.getItemGroupRepeatKey().equals("")) {
                                                            result = -1;
                                                        }
                                                    }

                                                    return result;
                                                }
                                            });
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

    /**
     * Remove unnecessary attributes that do not need to be serialised to XML
     */
    public void cleanAttributesUnnecessaryForImport() {

        // In general these properties are not important for import (clear them)
        this.setOdmVersion(null);
        this.setFileType(null);
        this.setDescription(null);
        this.setCreationDateTime(null);
        this.setFileOid(null);
        this.setStudies(null);

        // Also remove unnecessary attributes from StudySubject clinical data (these are OC extensions)
        for (ClinicalData cd : this.getClinicalDataList()) {

            // Collect keys of disabled subjects (we do not want to import them)
            List<String> subjectList = new ArrayList<>();
            for (StudySubject ss: cd.getStudySubjects()) {
                if (ss.getIsEnabled() == Boolean.FALSE) {
                    subjectList.add(ss.getSubjectKey());
                }
            }

            // Remove disabled subjects (we do not want to import them)
            for (String oid : subjectList) {
                for (StudySubject ss: cd.getStudySubjects()) {
                    if (ss.getSubjectKey().equals(oid)) {
                        cd.getStudySubjects().remove(ss);
                        break;
                    }
                }
            }

            // Continue with the rest of enabled subjects
            for (StudySubject ss: cd.getStudySubjects()) {
                ss.setPid(null);
                ss.setYearOfBirth(null);
                ss.setDateOfBirth(null);
                ss.setSex(null);
                ss.setSecondaryId(null);
                ss.setStudySubjectId(null);

                // Remove empty items from import
                for (EventData ed : ss.getStudyEventDataList()) {
                    for (FormData fd : ed.getFormDataList()) {
                        for(ItemGroupData igd : fd.getItemGroupDataList()) {

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
     * @return hash
     */
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

}