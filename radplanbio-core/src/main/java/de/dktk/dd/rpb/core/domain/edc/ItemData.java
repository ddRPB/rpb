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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * ItemData domain entity (based on CDISC ODM ItemData)
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ItemData")
public class ItemData implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ItemData.class);

    //endregion

    //region Members

    @XmlAttribute(name="ItemOID")
    private String itemOid;

    @XmlAttribute(name="Value")
    private String value;

    //endregion

    //region Constructors

    public ItemData() {
        // NOOP
    }

    public ItemData(String itemOid) {
        this.itemOid = itemOid;
    }

    public ItemData(String itemOid, String value) {
        this.itemOid = itemOid;
        this.value = value;
    }

    //endregion

    //region Properties

    public String getItemOid() {
        return this.itemOid;
    }

    public void setItemOid(String value) {
        this.itemOid = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //endregion

}
