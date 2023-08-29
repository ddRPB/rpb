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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ItemDetails CDISC ODM OpenClinica extension domain entity
 *
 * @author tomas@skripcak.net
 * @since 03 Mar 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemDetails", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class ItemDetails implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ItemDetails.class);

    //endregion

    //region Members

    @XmlAttribute(name = "ItemOID")
    private String itemOid;

    @XmlElement(name = "ItemPresentInForm", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private List<ItemPresentInForm> itemPresentInForm;

    //endregion

    //region Properties

    public String getItemOid() {
        return this.itemOid;
    }

    public void setItemOid(String value) {
        this.itemOid = value;
    }

    public List<ItemPresentInForm> getItemPresentInForm() {
        return this.itemPresentInForm;
    }

    public void setItemPresentInForm(List<ItemPresentInForm> itemPresentInFormList) {
        this.itemPresentInForm = itemPresentInFormList;
    }

    //endregion

    // region Methods

    public String getLeftItemTextElementByFormOid(String formOid) {
        if (this.itemPresentInForm == null || formOid == null) {
            return "";
        }

        for (ItemPresentInForm item : this.itemPresentInForm
        ) {
            if (item.getFormOid().equals(formOid)) {
                if (item.getLeftItemText() != null) {
                    return item.getLeftItemText();
                }
            }
        }

        return "";
    }

    // endregion

}
