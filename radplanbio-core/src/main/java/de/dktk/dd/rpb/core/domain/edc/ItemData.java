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
import org.apache.log4j.Logger;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * ItemData domain entity (based on CDISC ODM ItemData)
 *
 * ItemData refers to its metadata via ItemDefinition (transient) property
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ItemData")
public class ItemData implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ItemData.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name="ItemOID")
    private String itemOid;

    @XmlAttribute(name="Value")
    private String value;

    @XmlTransient
    private ItemDefinition itemDefinition;

    @XmlTransient
    private String status;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    //endregion

    //region Constructors

    public ItemData() {
        // NOOP
    }

    public ItemData(String itemOid) {
        this();

        this.itemOid = itemOid;
    }

    public ItemData(String itemOid, String value) {
        this(itemOid);

        this.value = value;
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

    //region ItemOID

    public String getItemOid() {
        return this.itemOid;
    }

    public void setItemOid(String value) {
        this.itemOid = value;
    }

    //endregion

    //region Value

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //endregion

    //region ItemDefinition

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
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

    //region OrderInForm

    public Integer getOrderInForm() {
        if (this.itemDefinition != null) {
            if (this.itemDefinition.getItemDetails() != null) {
                if (this.itemDefinition.getItemDetails().getItemPresentInForm() != null) {
                    if (this.itemDefinition.getItemDetails().getItemPresentInForm().getOrderInForm() != null) {
                        return this.itemDefinition.getItemDetails().getItemPresentInForm().getOrderInForm();
                    }
                }
            }
        }

        return null;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ItemData && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     * @return hash
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.itemOid);
    }

    /**
     * Construct a readable string representation for this RtStructType instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("itemOid", this.itemOid)
                .add("value", this.value)
                .toString();
    }

    //endregion

    //region Methods

    //region Metadata

    public void linkOdmDefinitions(Odm odm) {
        if (odm != null) {

            // ItemDefinition linking
            ItemDefinition itemDefinition = odm.findUniqueItemDefinitionOrNone(this.itemOid);
            if (itemDefinition != null) {
                this.setItemDefinition(itemDefinition);
            }
            // ItemDefinition does not exists in metadata - item removed
            else {
                this.setStatus(EnumItemDataStatus.INVALID.toString());
            }
        }
    }

    //endregion

    //region Data

    /**
     * Determine whether item data has assigned not empty value
     * @return true if ItemData has not empty value
     */
    public boolean hasValue() {
        return this.value != null && !this.value.equals("");
    }

    //endregion

    //endregion

}