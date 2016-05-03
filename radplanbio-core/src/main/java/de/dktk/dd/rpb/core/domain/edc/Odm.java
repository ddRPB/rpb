/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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

import org.apache.log4j.Logger;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
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

    private Integer id;

    // CDISC attributes
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

    // Object hash
    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    /**
     * Default constructor
     */
    @SuppressWarnings("unused")
    public Odm() {
        this.studies = new ArrayList<Study>();
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
                this.clinicalDataList = new ArrayList<ClinicalData>();
                this.clinicalDataList.add(new ClinicalData(studyOid, metaDataVersionOid));
            }
        }
        // 2. when no metadata present initialized according to clinical data
        else if (otherOdm.getClinicalDataList() != null && otherOdm.getClinicalDataList().size() > 0) {
            // The first one is the parent study
            String studyOid = otherOdm.getClinicalDataList().get(0).getStudyOid();
            String metadataVersionOid = otherOdm.getClinicalDataList().get(0).getMetaDataVersionOid();

            this.clinicalDataList = new ArrayList<ClinicalData>();
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

    //region CDISC

    //region FileOID

    @SuppressWarnings("unused")
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

    //endregion

    //region OpenClinica

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
                                    igd.getItemDefs().add(id);
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
     * Get one concrete study from metadata (important for multi-centre study)
     * @param oid of study entity to be returned
     * @return Study entity
     */
    public Study getStudyByOid(String oid) {
        Study result = null;

        for (Study s : this.studies) {
            if (s.getOid() != null && s.getOid().equals(oid)) {
                result = s;
                break;
            }
        }

        return result;
    }

    /**
     * Get CDISC ODM item definition entity according to target (StudyEventOID, FormOID, ItemGroupOID, ItemOID)
     * @param target (StudyEventOID, FormOID, ItemGroupOID, ItemOID)
     * @return ItemDefinition entity
     */
    public ItemDefinition getItemDefinition(MappedOdmItem target) {
        ItemDefinition result = null;

        if (this.getStudies() != null) {
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
                                                    result = id;
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

        return result;
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
        List<Odm> result = new ArrayList<Odm>();

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
            List<String> subjectList = new ArrayList<String>();
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

            // Continue with the resto of enabled subjects
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
                            List<String> idList = new ArrayList<String>();
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

}
