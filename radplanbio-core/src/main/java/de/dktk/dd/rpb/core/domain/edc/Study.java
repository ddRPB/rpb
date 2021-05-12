/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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
import org.apache.log4j.Logger;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudyType;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * EDC Study transient domain entity
 *
 * @author tomas@skripcak.net
 * @since 18 Oct 2013
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Study")
public class Study implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Study.class);

    //endregion

    //region Members

    // In this case Id is not transient, because we use Id (OC study primary key) when changing the active study
    private Integer id;

    @XmlAttribute(name="OID")
    private String oid;

    @XmlElement(name="MetaDataVersion")
    private MetaDataVersion metaDataVersion;

    @XmlElement(name="GlobalVariables")
    private GlobalVariables globalVariables;

    @XmlElement(name="BasicDefinitions")
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
     * @return OC study unique identifier
     */
    public String getOcStudyUniqueIdentifier() {
        return this.parentStudy != null ? this.parentStudy.getUniqueIdentifier() : this.uniqueIdentifier;
    }

    //endregion

    //region OC Study Name

    /**
     * Get name for mono-centre study or parent study name for multi-centre study
     * @return OC study name
     */
    public String getOcStudyName() {
        return this.parentStudy != null ? this.parentStudy.getName() : this.name;
    }

    //endregion

    //region OC Site UniqueIdentifier

    /**
     * Get site identifier for multi-centre study only
     * @return OC site identifier
     */
    public String getOcSiteIdentifier() {
        return this.parentStudy != null ? this.uniqueIdentifier : null;
    }

    //endregion

    //region OC Site Name

    /**
     * Get site name for multi-centre study only
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
     * @return hash
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.oid);
    }

    /**
     * Construct a readable string representation for this RtStructType instance.
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
     *
     * @param parentStudyProtocolId ParentStudy protocol id
     * @return PartnerSite identifier that correspond to StudySite metadata in multi-centre setup or empty string for mono-centre study
     */
    public String extractPartnerSiteIdentifier(String parentStudyProtocolId) {
        // Default empty string indicates mono-centre study
        String siteIdentifier = "";

        // Extract only when parent study exists
        if (parentStudyProtocolId != null && !parentStudyProtocolId.isEmpty()) {
            if (this.globalVariables != null &&
                this.globalVariables.getProtocolName() != null &&
                !this.globalVariables.getProtocolName().isEmpty()) {

                // OC specific
                String ocSiteSeparator = " " + Constants.RPB_IDENTIFIERSEP + " ";

                // Multi-centre
                if (this.globalVariables.getProtocolName().contains(ocSiteSeparator)) {

                    //TODO: use substring -> it is faster then replace
                    // Extract site protocol id
                    String siteProtocolId = this.globalVariables.getProtocolName().replace(
                        parentStudyProtocolId + ocSiteSeparator, ""
                    );

                    // Extract partner site identifier
                    if (!siteProtocolId.isEmpty()) {
                        int index = siteProtocolId.indexOf(Constants.RPB_IDENTIFIERSEP);
                        siteIdentifier = siteProtocolId.substring(0, index);
                    }
                }
            }
        }

        return siteIdentifier;
    }

    /**
     * Extract StudySite identifier from study metadata protocol id (OC site protocol id)
     *
     * @param parentStudyProtocolId ParentStudy protocol id
     * @return StudySite identifier that correspond to StudySite metadata in multi-centre setup or empty string for mono-centre study
     */
    public String extractStudySiteIdentifier(String parentStudyProtocolId) {
        // Default empty string indicates mono-centre study
        String siteIdentifier = "";

        // Extract only when parent study exists
        if (parentStudyProtocolId != null && !parentStudyProtocolId.isEmpty()) {
            if (this.globalVariables != null &&
                this.globalVariables.getProtocolName() != null &&
                !this.globalVariables.getProtocolName().isEmpty()) {

                // OC specific
                String ocSiteSeparator = " " + Constants.RPB_IDENTIFIERSEP + " ";

                // Multi-centre
                if (this.globalVariables.getProtocolName().contains(ocSiteSeparator)) {
                    String controlString = parentStudyProtocolId + ocSiteSeparator;
                    int index = this.globalVariables.getProtocolName().indexOf(controlString);
                    siteIdentifier = this.globalVariables.getProtocolName().substring(index + controlString.length());
                }
            }
        }

        return siteIdentifier;
    }

    /**
     * Get all ItemDefinitions withing in a Study (metadata for all items)
     *
     * @return List of all ItemDefinition entities
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