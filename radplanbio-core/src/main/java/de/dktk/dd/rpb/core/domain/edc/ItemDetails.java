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

/**
 * ItemDetails CDISC ODM OpenClinica extension domain entity
 *
 * @author tomas@skripcak.net
 * @since 03 Mar 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ItemDetails", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class ItemDetails implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ItemDetails.class);

    //endregion

    //region Members

    @XmlAttribute(name="ItemOID")
    private String itemOid;

    @XmlElement(name="ItemPresentInForm", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private ItemPresentInForm itemPresentInForm;

    //endregion

    //region Properties

    public String getItemOid() {
        return this.itemOid;
    }

    public void setItemOid(String value) {
        this.itemOid = value;
    }

    public ItemPresentInForm getItemPresentInForm() {
        return this.itemPresentInForm;
    }

    public void setItemPresentInForm(ItemPresentInForm itemInForm) {
        this.itemPresentInForm = itemInForm;
    }

    //endregion

}
