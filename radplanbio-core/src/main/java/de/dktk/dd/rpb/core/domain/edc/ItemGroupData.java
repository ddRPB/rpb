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
import java.util.ArrayList;
import java.util.List;

/**
 * ItemGroupData domain entity (based on CDISC ODM ItemGroupData)
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ItemGroupData")
public class ItemGroupData implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ItemGroupData.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name="ItemGroupOID")
    private String itemGroupOid;

    @XmlAttribute(name="ItemGroupRepeatKey")
    private String itemGroupRepeatKey;

    @XmlAttribute(name="TransactionType")
    private String transactionType;

    @XmlElement(name="ItemData")
    private List<ItemData> itemDataList;

    @XmlTransient
    private ItemGroupDefinition itemGroupDefinition;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    //endregion

    //region Constructors

    public ItemGroupData() {
        this.transactionType = "Insert";
    }

    public ItemGroupData(String itemGroupOid) {
        this();

        this.itemGroupOid = itemGroupOid;
    }

    public ItemGroupData(String itemGroupOid, String itemGroupRepeatKey) {
        this(itemGroupOid);

        this.itemGroupRepeatKey = itemGroupRepeatKey;
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

    //region ItemGroupOID

    public String getItemGroupOid() {
        return this.itemGroupOid;
    }

    public void setItemGroupOid(String value) {
        this.itemGroupOid = value;
    }

    //endregion

    //region ItemGroupRepeatKey

    public String getItemGroupRepeatKey() {
        return this.itemGroupRepeatKey;
    }

    public void setItemGroupRepeatKey(String value) {
        this.itemGroupRepeatKey = value;
    }

    //endregion

    //region TransactionType

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String value) {
        this.transactionType = value;
    }

    //endregion

    //region ItemDataList

    public List<ItemData> getItemDataList() {
        return this.itemDataList;
    }

    public void setItemDataList(List<ItemData> list) {
        this.itemDataList = list;
    }

    /**
     * Helper method to add the passed {@link ItemData} to the itemDataList list
     */
    public boolean addItemData(ItemData itemData) {
        if (!this.containsItemData(itemData)) {
            if (this.itemDataList == null) {
                this.itemDataList = new ArrayList<>();
            }
            return this.itemDataList.add(itemData);
        }

        return false;
    }

    /**
     * Helper method to remove the passed {@link ItemData} from the itemDataList list
     */
    public boolean removeItemData(ItemData itemData) {
        return this.containsItemData(itemData) && this.itemDataList.remove(itemData);
    }

    /**
     * Helper method to determine if the passed {@link ItemData} is present in the itemDataList list
     */
    public boolean containsItemData(ItemData itemData) {
        return this.itemDataList != null && this.itemDataList.contains(itemData);
    }

    /**
     * Helper method to determine whether passed item OID is present in the itemDataList list
     * @param itemDataOid itemOid to lookup for
     * @return true if the ItemData with specified form OID exists within group
     */
    public boolean containsItemData(String itemDataOid) {
        if (this.itemDataList != null) {
            for (ItemData itemData : this.itemDataList) {
                if (itemData.getItemOid().equals(itemDataOid)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Helper method to get itemData according to item OID
     * @param itemDataOid formOid to lookup for
     * @return itemData if exists
     */
    public ItemData findItemGroupData(String itemDataOid) {
        if (this.itemDataList != null) {
            for (ItemData itemData : this.itemDataList) {
                if (itemData.getItemOid().equals(itemDataOid)) {
                    return itemData;
                }
            }
        }

        return null;
    }

    //endregion

    //region ItemGroupDefinition

    public ItemGroupDefinition getItemGroupDefinition() {
        return this.itemGroupDefinition;
    }

    public void setItemGroupDefinition(ItemGroupDefinition itemGroupDefinition) {
        this.itemGroupDefinition = itemGroupDefinition;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ItemGroupData && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     * @return hash
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.itemGroupOid);
    }

    /**
     * Construct a readable string representation for this RtStructType instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("itemGroupOid", this.itemGroupOid)
                .add("itemGroupRepeatKey", this.itemGroupRepeatKey)
                .toString();
    }

    //endregion

    //region Methods

    //region Metadata

    public void linkOdmDefinitions(Odm odm) {
        if (odm != null) {

            // ItemGroupDefinition linking
            ItemGroupDefinition itemGroupDefinition = odm.findUniqueItemGroupDefinitionOrNone(this.itemGroupOid);
            if (itemGroupDefinition != null) {
                this.setItemGroupDefinition(itemGroupDefinition);
            }

            // Link next level in hierarchy (Item)
            if (this.itemDataList != null) {
                for (ItemData itemData : this.itemDataList) {
                    itemData.linkOdmDefinitions(odm);
                }
            }
        }
    }

    //endregion

    //endregion

}