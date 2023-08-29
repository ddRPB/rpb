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

import de.dktk.dd.rpb.core.adapter.NoYesBooleanAdapter;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * StudyEventDefinition transient domain entity (CDISC ODM)
 *
 * @author tomas@skripcak.net
 * @since 21 Oct 2013
 * @version 1.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="StudyEventDef")
public class EventDefinition implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(EventDefinition.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id; // unused for Transient entity

    @XmlAttribute(name="OID")
    private String oid;

    @XmlAttribute(name="Name")
    private String name;

    @XmlTransient
    private String description;

    @XmlAttribute(name="Type")
    private String type;

    @XmlTransient
    private String category;

    @XmlAttribute(name="Repeating")
    @XmlJavaTypeAdapter(NoYesBooleanAdapter.class)
    private Boolean isRepeating;

    @XmlTransient
    private Boolean isMandatory = Boolean.TRUE;

    @XmlTransient
    private Integer ordinal;

    @XmlElement(name = "FormRef")
    private List<FormDefinition> formRefs;

    @XmlElement(name = "EventDefinitionDetails", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private EventDefinitionDetails eventDefinitionDetails;

    @XmlTransient
    private List<FormDefinition> formDefs;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public EventDefinition() {
        this.formDefs = new ArrayList<>();
        this.formRefs = new ArrayList<>();
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

    //region Name

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
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

    //region Type

    public String getType() {
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }

    //endregion

    //region Logger

    public String getCategory() { return this.category; }

    public void setCategory(String value) {
        this.category = value;
    }

    //endregion

    //region IsRepeating

    public Boolean getIsRepeating() {
        return this.isRepeating;
    }

    public void setIsRepeating(Boolean value) {
        this.isRepeating = value;
    }

    //endregion

    //region IsMandatory

    public Boolean getIsMandatory() {
        return this.isMandatory;
    }

    public void setIsMandatory(Boolean value) {
        this.isMandatory = value;
    }

    //endregion

    //region Ordinal

    public Integer getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(Integer value) {
        this.ordinal = value;
    }

    //endregion

    //region FormRefs

    public List<FormDefinition> getFormRefs() {
        return this.formRefs;
    }

    public void setFormRefs(List<FormDefinition> list) {
        this.formRefs = list;
    }

    //endregion

    //region FormDefs

    public List<FormDefinition> getFormDefs() {
        return this.formDefs;
    }

    public void setFormDefs(List<FormDefinition> list) {
        this.formDefs = list;
    }

    //endregion

    // region EventDefinitionDetails

    public EventDefinitionDetails getEventDefinitionDetails() {
        return eventDefinitionDetails;
    }

    public void setEventDefinitionDetails(EventDefinitionDetails eventDefinitionDetails) {
        this.eventDefinitionDetails = eventDefinitionDetails;
    }

    // endregion

    //endregion

    //region Methods

    /**
     * Find list of visible FromDefinitions
     *
     * @return List of visible FormDefinition entities
     */
    public List<FormDefinition> findVisibleFormDefinitions() {
        List<FormDefinition> results = new ArrayList<>();

        if (this.formDefs != null) {
            for (FormDefinition formDefinition : this.formDefs) {

                // For OpenClinica form check the vendor specific extension (visibility)
                if (formDefinition.getFormDetails() != null) {
                    if (formDefinition.getFormDetails().getPresentInEventDefinition() != null) {
                        if (!formDefinition.getFormDetails().getPresentInEventDefinition().getHideCrf()) {
                            results.add(formDefinition);
                        }
                    }
                }
                // Otherwise just add the form the the results
                else {
                    results.add(formDefinition);
                }
            }
        }

        return results;
    }

    /**
     * Find eCRF item definition in Event according to provided OID
     *
     * @param itemDefOid item definition OID
     * @return item definition object instance
     */
    public ItemDefinition findItemDef(String itemDefOid) {
        if (this.formDefs != null) {
            for (FormDefinition formDef : this.formDefs) {
                for (ItemGroupDefinition itemGroupDef : formDef.getItemGroupDefs()) {
                    for (ItemDefinition itemDef : itemGroupDef.getItemDefs()) {
                        if (itemDef.getOid().equals(itemDefOid)) {
                            return itemDef;
                        }
                    }
                }
            }
        }

        return null;
    }

    //endregion

    //region Overrides

    /**
     * Compare this entity to other provided entity
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof EventDefinition && this.hashCode() == other.hashCode());
    }

    /**
     * Generate hash code for this entity
     * @return hash code of the entity
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.oid);
    }

    /**
     * String representation of entity
     * @return string representation of the entity
     */
    @Override
    public String toString() {
        return this.oid;
    }

    //endregion

}
