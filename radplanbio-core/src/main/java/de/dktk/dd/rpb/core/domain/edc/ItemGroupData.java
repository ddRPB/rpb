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

import org.apache.log4j.Logger;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * ItemGroupData domain entity (based on CDISC ODM ItemGroupData)
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ItemGroupData")
public class ItemGroupData implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ItemGroupData.class);

    //endregion

    //region Members

    @XmlAttribute(name="ItemGroupOID")
    private String itemGroupOid;

    @XmlAttribute(name="ItemGropupRepeatKey")
    private String itemGroupRepeatKey;

    @XmlAttribute(name="TransactionType")
    private String transactionType;

    @XmlElement(name="ItemData")
    private List<ItemData> itemDataList;

    //endregion

    //region Constructors

    public ItemGroupData() {
        this.transactionType = "Insert";
    }

    public ItemGroupData(String itemGroupOid) {
        this.itemGroupOid = itemGroupOid;
        this.transactionType = "Insert";
    }

    public ItemGroupData(String itemGroupOid, String itemGroupRepeatKey) {
        this.itemGroupOid = itemGroupOid;
        this.itemGroupRepeatKey = itemGroupRepeatKey;
        this.transactionType = "Insert";
    }

    //endregion

    //region Properties

    public String getItemGroupOid() {
        return this.itemGroupOid;
    }

    public void setItemGroupOid(String value) {
        this.itemGroupOid = value;
    }

    public String getItemGroupRepeatKey() {
        return this.itemGroupRepeatKey;
    }

    public void setItemGroupRepeatKey(String value) {
        this.itemGroupRepeatKey = value;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String value) {
        this.transactionType = value;
    }

    //region ItemDataList

    public List<ItemData> getItemDataList() {
        return this.itemDataList;
    }

    public void setItemDataList(List<ItemData> list) {
        this.itemDataList = list;
    }

    public List<ItemData> addItemData(ItemData itemData) {
        if (!this.itemDataList.contains(itemData)) {
            this.itemDataList.add(itemData);
        }

        return this.itemDataList;
    }

    //endregion

    //endregion

}
