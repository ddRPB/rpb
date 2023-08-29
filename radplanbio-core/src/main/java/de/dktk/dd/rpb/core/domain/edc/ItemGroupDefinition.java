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
 * FormItemGroupDefinition transient domain entity (CDISC ODM)
 *
 * @author tomas@skripcak.net
 * @since 21 Oct 2013
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ItemGroupDef")
public class ItemGroupDefinition implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ItemGroupDefinition.class);

    //endregion

    //region Members

    private Integer id; // unused for Transient entity

    @XmlAttribute(name="OID")
    private String oid;

    @XmlAttribute(name="ItemGroupOID")
    private String itemGroupOid;

    @XmlAttribute(name="Name")
    private String name;

    @XmlAttribute(name="SASDatasetName")
    private String sasDatasetName;

    @XmlAttribute(name="Comment")
    private String comment;

    @XmlAttribute(name="Repeating")
    @XmlJavaTypeAdapter(NoYesBooleanAdapter.class)
    private Boolean isRepeating;

    @XmlElement(name="ItemRef")
    private List<ItemDefinition> itemRefs;

    private List<ItemDefinition> itemDefs;

    //region RadPlanBio

    @XmlTransient
    private FormDefinition formDefinition;

    //endregion

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public ItemGroupDefinition() {
        this.itemDefs = new ArrayList<ItemDefinition>();
        this.itemRefs = new ArrayList<ItemDefinition>();
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

    //region ItemGroupOID

    public String getItemGroupOid() {
        return this.itemGroupOid;
    }

    public void setItemGroupOid(String value) {
        this.itemGroupOid = value;
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

    //region SASDatasetName

    public String getSasDatasetName() {
        return this.sasDatasetName;
    }

    public void setSasDatasetName(String value) {
        this.sasDatasetName = value;
    }

    //endregion

    //region Comment

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    //endregion

    //region IsRepeating

    public boolean getIsRepeating() {
        return this.isRepeating;
    }

    public void setIsRepeating(boolean value) {
        this.isRepeating = value;
    }

    //endregion

    //region ItemRefs

    public List<ItemDefinition> getItemRefs() {
        return this.itemRefs;
    }

    public void setItemRefs(List<ItemDefinition> list) {
        this.itemRefs = list;
    }

    //endregion

    //region ItemDefs

    public List<ItemDefinition> getItemDefs() {
        return this.itemDefs;
    }

    public void setItemDefs(List<ItemDefinition> list) {
        this.itemDefs = list;
    }

    public boolean addItemDef(ItemDefinition itemDefinition) {
        if (!this.itemDefs.contains(itemDefinition)) {
            return this.itemDefs.add(itemDefinition);
        }

        return false;
    }

    public boolean removeItemDef(ItemDefinition itemDefinition) {
        return this.itemDefs.contains(itemDefinition) && this.itemDefs.remove(itemDefinition);
    }

    //endregion

    //region RadPlanBio

    public FormDefinition getFormDefinition() {
        return this.formDefinition;
    }

    public void setFormDefinition(FormDefinition formDefinition) {
        this.formDefinition = formDefinition;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ItemGroupDefinition && hashCode() == other.hashCode());
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
        else if (this.itemGroupOid != null && !this.itemGroupOid.equals("")) {
            alternateIdentifier = this.itemGroupOid;
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
                .add("id", this.id)
                .add("oid", this.oid)
                .add("name", this.name)
                .toString();
    }

    //endregion

}
