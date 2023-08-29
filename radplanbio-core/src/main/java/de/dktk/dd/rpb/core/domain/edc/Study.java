/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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
import de.dktk.dd.rpb.core.util.Constants;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudyType;
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
 * CDISC ODM EDC Study transient domain entity
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Study")
public class Study implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(Study.class);

    //endregion

    //region Members

    // In this case Id is not transient, because we use Id (OC study primary key) when changing the active study
    private Integer id;

    @XmlAttribute(name = "OID")
    private String oid;

    @XmlElement(name = "MetaDataVersion")
    private MetaDataVersion metaDataVersion;

    @XmlElement(name = "GlobalVariables")
    private GlobalVariables globalVariables;

    @XmlElement(name = "BasicDefinitions")
    private BasicDefinitions basicDefinitions;

    private String uniqueIdentifier;
    private String secondaryIdentifier;
    private String name;
    private String ocoid;

    private Status status;

    private Study parentStudy;
    private List<Study> studySites;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    //endregion

    //region Constructors

    public Study() {
        // NOOP
    }

    public Study(StudyType studyType) {
        this();

        this.oid = studyType.getOid();
        this.name = studyType.getName();
        this.uniqueIdentifier = studyType.getIdentifier();
    }

    public Study(StudyType parentStudyType, SiteType siteType) {
        this();

        this.parentStudy = new Study(parentStudyType);

        this.oid = siteType.getOid();
        this.name = siteType.getName();
        this.uniqueIdentifier = siteType.getIdentifier();
    }

    public Study(Study parentStudy, SiteType siteType) {
        this();

        this.parentStudy = parentStudy;

        this.oid = siteType.getOid();
        this.name = siteType.getName();
        this.uniqueIdentifier = siteType.getIdentifier();
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

    //region OID

    public String getOid() {
        return this.oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    //endregion

    //region MetaDataVersion

    public MetaDataVersion getMetaDataVersion() {
        return this.metaDataVersion;
    }

    public void setMetaDataVersion(MetaDataVersion entity) {
        this.metaDataVersion = entity;
    }

    //endregion

    //region GlobalVariables

    public GlobalVariables getGlobalVariables() {
        return this.globalVariables;
    }

    public void setGlobalVariables(GlobalVariables globalVariables) {
        this.globalVariables = globalVariables;
    }

    //endregion

    //region BasicDefinitions

    public BasicDefinitions getBasicDefinitions() {
        return basicDefinitions;
    }

    public void setBasicDefinitions(BasicDefinitions basicDefinitions) {
        this.basicDefinitions = basicDefinitions;
    }

    //endregion

    //region UniqueIdentifier

    public String getUniqueIdentifier() {
        return this.uniqueIdentifier;
    }

    public void setUniqueIdentifier(String value) {
        this.uniqueIdentifier = value;
    }

    //endregion

    //region Secondary Identifier

    public String getSecondaryIdentifier() {
        return this.secondaryIdentifier;
    }

    public void setSecondaryIdentifier(String value) {
        this.secondaryIdentifier = value;
    }

    //endregion

    //region Name

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region OC OID

    public String getOcoid() {
        return this.ocoid;
    }

    public void setOcoid(String value) {
        this.ocoid = value;
    }

    //endregion

    //region Status

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status object) {
        this.status = object;
    }

    //endregion

    //region ParentStudy

    public Study getParentStudy() {
        return this.parentStudy;
    }

    public void setParentStudy(Study study) {
        this.parentStudy = study;
    }

    //endregion

    //region StudySites

    public List<Study> getStudySites() {
        return this.studySites;
    }

    public void setStudySites(List<Study> studySites) {
        this.studySites = studySites;
    }

    //endregion

    //region OC Study UniqueIdentifier

    /**
     * Get unique identifier for mono-centre study or parent study unique identifier for multi-centre study
     *
     * @return OC study unique identifier
     */
    public String getOcStudyUniqueIdentifier() {
        return this.parentStudy != null ? this.parentStudy.getUniqueIdentifier() : this.uniqueIdentifier;
    }

    //endregion

    //region OC Study Name

    /**
     * Get name for mono-centre study or parent study name for multi-centre study
     *
     * @return OC study name
     */
    public String getOcStudyName() {
        return this.parentStudy != null ? this.parentStudy.getName() : this.name;
    }

    //endregion

    //region OC Site UniqueIdentifier

    /**
     * Get site identifier for multi-centre study only
     *
     * @return OC site identifier
     */
    public String getOcSiteIdentifier() {
        return this.parentStudy != null ? this.uniqueIdentifier : null;
    }

    //endregion

    //region OC Site Name

    /**
     * Get site name for multi-centre study only
     *
     * @return OC site name
     */
    public String getOcSiteName() {
        return this.parentStudy != null ? this.name : null;
    }

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

    /**
     * Generate entity hash code
     *
     * @return hash
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.oid);
    }

    /**
     * Construct a readable string representation for this RtStructType instance.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("oid", this.oid)
                .toString();
    }

    //endregion

    //region Methods

    /**
     * Determine whether study metadata indicates multi-centre parent study
     * Study sites have to be populated for this to work
     *
     * @return True if study metadata defines multiple sites
     */
    public boolean isMultiCentre() {
        return (this.studySites != null && this.studySites.size() > 1);
    }

    /**
     * Extract PartnerSite identifier from study metadata protocol id (RPB partner site identifier)
     * e.g. DD-STR-HNPraedBIO-2013 -> DD
     *
     * @return PartnerSite identifier that correspond to StudySite metadata in multi-centre setup or empty string for mono-centre study
     */
    public String extractPartnerSiteIdentifier() {
        // Default empty string indicates mono-centre study
        String siteIdentifier = "";

        if (this.globalVariables != null &&
                this.globalVariables.getProtocolName() != null &&
                !this.globalVariables.getProtocolName().isEmpty()) {

            String protocolName = this.globalVariables.getProtocolName();
            // OC specific
            String ocSiteSeparator = " " + Constants.RPB_IDENTIFIERSEP + " ";

            // Multi-centric studies protocol names can be split by ocSiteSeparator
            if (protocolName.contains(ocSiteSeparator)) {

                String[] splitIdentifiers = protocolName.split(ocSiteSeparator);

                String siteProtocolId = splitIdentifiers[1];
                String[] siteProtocolIdParts = siteProtocolId.split(Constants.RPB_IDENTIFIERSEP);

                // If site protocol id starts with two codes separated by RPB separator
                if (siteProtocolIdParts.length > 1) {
                    // first position is the PartnerSiteIdentifier
                    siteIdentifier = siteProtocolIdParts[0];
                }
            }
        }

        return siteIdentifier;
    }

    /**
     * Extracts the StudySite modifier from study metadata protocol id (second position from OC site protocol id)
     * e.g. DD-PRO-STR-AOK-2017 -> PRO
     *
     * @return String StudySite modifier or empty string  if there is no modifier used (mono or standard multi-centre study)
     */
    public String extractStudySiteModifier() {
        // Default empty string
        String siteModifier = "";

        if (this.globalVariables != null &&
                this.globalVariables.getProtocolName() != null &&
                !this.globalVariables.getProtocolName().isEmpty()) {

            String protocolName = this.globalVariables.getProtocolName();
            // OC specific
            String ocSiteSeparator = " " + Constants.RPB_IDENTIFIERSEP + " ";

            // Multi-centric studies protocol names can be split by ocSiteSeparator
            if (protocolName.contains(ocSiteSeparator)) {

                String[] splitIdentifiers = protocolName.split(ocSiteSeparator);

                String siteProtocolId = splitIdentifiers[1];
                String[] siteProtocolIdParts = siteProtocolId.split(Constants.RPB_IDENTIFIERSEP);

                // If site protocol id starts with two codes separated by RPB separator
                if (siteProtocolIdParts.length > 2) {
                    // second position in siteProtocolId (first position should be PartnerSiteIdentifier)
                    siteModifier = siteProtocolIdParts[1];
                }
            }
        }

        return siteModifier;
    }

    /**
     * Extract StudySite identifier from study metadata protocol id (OC site protocol id)
     * e.g. DD-STR-PETra-2013
     *
     * @return StudySite identifier that correspond to StudySite metadata in multi-centre setup or empty string for mono-centre study
     */
    public String extractStudySiteIdentifier() {
        // Default empty string indicates mono-centre study
        String siteIdentifier = "";

        // Extract only when parent study exists
        if (this.globalVariables != null &&
                this.globalVariables.getProtocolName() != null &&
                !this.globalVariables.getProtocolName().isEmpty()) {

            String protocolName = this.globalVariables.getProtocolName();
            // OC specific
            String ocSiteSeparator = " " + Constants.RPB_IDENTIFIERSEP + " ";

            // Multi-centric studies protocol names can be split by ocSiteSeparator
            if (protocolName.contains(ocSiteSeparator)) {

                String[] splitIdentifiers = protocolName.split(ocSiteSeparator);
                siteIdentifier = splitIdentifiers[1];

            }
        }

        return siteIdentifier;
    }

    /**
     * Extract Study identifier from study metadata protocol id (OC site protocol id).
     * <p>
     * e.g. STR-PETra-2013
     *
     * @return Study identifier
     */
    public String extractStudyIdentifier() {
        String studyIdentifier = "";

        // Extract only when parent study exists
        if (this.globalVariables != null &&
                this.globalVariables.getProtocolName() != null &&
                !this.globalVariables.getProtocolName().isEmpty()) {

            String protocolName = this.globalVariables.getProtocolName();
            // OC specific
            String ocSiteSeparator = " " + Constants.RPB_IDENTIFIERSEP + " ";

            // Multi-centric studies protocol names can be split by ocSiteSeparator
            if (protocolName.contains(ocSiteSeparator)) {

                String[] splitIdentifiers = protocolName.split(ocSiteSeparator);
                studyIdentifier = splitIdentifiers[0];

            } else {
                // Mono-centric study
                studyIdentifier = protocolName;
            }
        }

        return studyIdentifier;
    }

    /**
     * Get all ItemDefinitions withing in a Study (metadata for all items)
     *
     * @return List of all ItemDefinition entities in the study
     */
    public List<ItemDefinition> findItemDefinitions() {
        List<ItemDefinition> items = new ArrayList<>();

        // Collect items from all events and forms
        if (this.metaDataVersion != null && this.metaDataVersion.getStudyEventDefinitions() != null) {
            for (EventDefinition eventDefinition : this.metaDataVersion.getStudyEventDefinitions()) {
                if (eventDefinition.getFormDefs() != null) {
                    for (FormDefinition formDefinition : eventDefinition.getFormDefs()) {
                        if (formDefinition.getEventDefinition() != eventDefinition) {
                            formDefinition.setEventDefinition(eventDefinition);
                        }
                        if (formDefinition.getItemGroupDefs() != null) {
                            for (ItemGroupDefinition itemGroupDefinition : formDefinition.getItemGroupDefs()) {
                                if (itemGroupDefinition.getFormDefinition() != formDefinition) {
                                    itemGroupDefinition.setFormDefinition(formDefinition);
                                }
                                if (itemGroupDefinition.getItemDefs() != null) {
                                    for (ItemDefinition itemDefinition : itemGroupDefinition.getItemDefs()) {
                                        if (itemDefinition.getItemGroupDefinition() != itemGroupDefinition) {
                                            itemDefinition.setItemGroupDefinition(itemGroupDefinition);
                                        }
                                        if (itemDefinition.isPresentInForm(formDefinition.getOid())) {
                                            items.add(itemDefinition);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return items;
    }

    //endregion

}
