/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Teamk
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
 * MultiSelectListDefinition CDISC ODM OpenClinica extension domain entity
 *
 * @author tomas@skripcak.net
 * @since 03 Mar 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="MultiSelectList", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class MultiSelectListDefinition implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MultiSelectListDefinition.class);

    //endregion

    //region Members

    @XmlAttribute(name="MultiSelectListID")
    private String multiSelectListId;

    @XmlAttribute(name="ID")
    private String id;

    @XmlAttribute(name="Name")
    private String name;

    @XmlAttribute(name="DataType")
    private String dataType;

    @XmlAttribute(name="ActualDataType")
    private String actualDataType;

    @XmlElement(name="MultiSelectListItem", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private List<MultiSelectListItem> multiSelectListItems;

    //endregion

    //region Properties

    public String getMultiSelectListId() {
        return this.multiSelectListId;
    }

    public void setMultiSelectListId(String id) {
        this.multiSelectListId = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getActualDataType() {
        return this.actualDataType;
    }

    public void setActualDataType(String value) {
        this.actualDataType = value;
    }

    public List<MultiSelectListItem> getMultiSelectListItems() {
        return this.multiSelectListItems;
    }

    public void setMultiSelectListItems(List<MultiSelectListItem> list) {
        this.multiSelectListItems = list;
    }

    //endregion

}