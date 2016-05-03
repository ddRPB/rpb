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
import java.util.ArrayList;
import java.util.List;

/**
 * FormData domain entity (based on CDISC ODM FormData)
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="FormData")
public class FormData implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(FormData.class);

    //endregion

    //region Members

    @XmlAttribute(name="FormOID")
    private String formOid;

    @XmlElement(name="ItemGroupData")
    private List<ItemGroupData> itemGroupDataList;

    @XmlAttribute(name="Url", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1-api")
    private String url;

    @XmlAttribute(name="VersionDescription", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1-api")
    private String versionDescription;

    @XmlAttribute(name="FormName", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1-api")
    private String formName;

    @XmlAttribute(name="Status", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1-api")
    private String status;

    //endregion

    //region Constructors

    public FormData() {
        // NOOP
    }

    public FormData(String formOid) {
        this.formOid = formOid;
    }

    //endregion

    //region Properties

    public String getFormOid() {
        return this.formOid;
    }

    public void setFormOid(String value) {
        this.formOid = value;
    }

    public List<ItemGroupData> getItemGroupDataList() {
        return this.itemGroupDataList;
    }

    public void setItemGroupDataList(List<ItemGroupData> list) {
        this.itemGroupDataList = list;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    public String getVersionDescription() {
        return this.versionDescription;
    }

    public void setVersionDescription(String value) {
        this.versionDescription = value;
    }

    public String getFormName() {
        return this.formName;
    }

    public void setFormName(String value) {
        this.formName = value;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    //endregion

    //region Methods

    public List<ItemData> getAllItemData() {
        List<ItemData> results = new ArrayList<ItemData>();

        if (this.itemGroupDataList != null) {
            for (ItemGroupData igd : this.itemGroupDataList) {
                if (igd.getItemDataList() != null) {
                    for (ItemData id : igd.getItemDataList()) {
                        results.add(id);
                    }
                }
            }
        }

        return results;
    }

    //endregion

}
