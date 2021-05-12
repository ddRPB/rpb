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
import de.dktk.dd.rpb.core.adapter.NoYesBooleanAdapter;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.apache.log4j.Logger;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * FormDefinition transient domain entity (CDISC ODM)
 *
 * @author tomas@skripcak.net
 * @since 21 Oct 2013
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="FormDef")
public class FormDefinition implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(FormDefinition.class);

    //endregion

    //region Members

    private Integer id; // unused for Transient entity

    @XmlAttribute(name="OID")
    private String oid;

    @XmlAttribute(name="FormOID")
    private String formOid;

    @XmlAttribute(name="Name")
    private String name;

    @XmlAttribute(name="Repeating")
    @XmlJavaTypeAdapter(NoYesBooleanAdapter.class)
    private Boolean isRepeating;

    @XmlElement(name="FormDetails", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private FormDetails formDetails;

    @XmlElement(name="ItemGroupRef")
    private List<ItemGroupDefinition> itemGroupRefs;

    private List<ItemGroupDefinition> itemGroupDefs;

    //region RadPlanBio

    @XmlTransient
    private EventDefinition eventDefinition;

    //endregion

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public FormDefinition() {
        this.itemGroupRefs = new ArrayList<>();
        this.itemGroupDefs = new ArrayList<>();
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

    //region FormOID

    public String getFormOid() {
        return this.formOid;
    }

    public void setFormOid(String value) {
        this.formOid = value;
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

    //region IsRepeating

    public Boolean getIsRepeating() {
        return this.getIsRepeating();
    }

    public void setIsRepeating(Boolean value) {
        this.isRepeating = value;
    }

    //endregion

    //region FormDetails

    public FormDetails getFormDetails() {
        return formDetails;
    }

    public void setFormDetails(FormDetails formDetails) {
        this.formDetails = formDetails;
    }

    //endregion

    //region ItemGroupRefs

    public List<ItemGroupDefinition> getItemGroupRefs() {
        return this.itemGroupRefs;
    }

    public void setItemGroupRefs(List<ItemGroupDefinition> list) {
        this.itemGroupRefs = list;
    }

    //endregion

    //region ItemGroupDefs

    public List<ItemGroupDefinition> getItemGroupDefs() {
        return this.itemGroupDefs;
    }

    public void setItemGroupDefs(List<ItemGroupDefinition> list) {
        this.itemGroupDefs = list;
    }

    //endregion

    //region RadPlanBio

    // TODO: this should be changed and deprecated because form can be used in multiple events

    public EventDefinition getEventDefinition() {
        return this.eventDefinition;
    }

    public void setEventDefinition(EventDefinition eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof FormDefinition && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     * @return hash
     */
    @Override
    public int hashCode() {
        String alternateIdentifier = "";
        if (this.oid != null && !this.oid.equals("")) {
            alternateIdentifier = this.oid;
        }
        else if (this.formOid != null && !this.formOid.equals("")) {
            alternateIdentifier = this.formOid;
        }

        return identifiableHashBuilder.hash(log, this, alternateIdentifier);
    }

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("oid", this.oid)
                .add("name", this.name)
                .toString();
    }

    //endregion

}