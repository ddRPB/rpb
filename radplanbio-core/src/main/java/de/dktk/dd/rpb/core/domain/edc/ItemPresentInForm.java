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

import org.apache.log4j.Logger;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * ItemPresentInfFrom CDISC ODM OpenClinica extension domain entity
 *
 * @author tomas@skripcak.net
 * @since 03 Mar 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ItemPresentInForm", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class ItemPresentInForm implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ItemPresentInForm.class);

    //endregion

    //region Members

    @XmlAttribute(name="FormOID")
    private String formOid;

    @XmlAttribute(name="ColumnNumber")
    private Integer columnNumber;

    @XmlAttribute(name="DefaultValue")
    private String defaultValue;

    @XmlAttribute(name="PHI")
    private String phi;

    @XmlAttribute(name="ShowItem")
    private String showItem;

    @XmlAttribute(name="OrderInForm")
    private Integer orderInForm;

    @XmlElement(name="LeftItemText", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String leftItemText;

    @XmlElement(name="ItemHeader", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String itemHeader;

    @XmlElement(name="SectionLabel", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private String sectionLabel;

    @XmlElement(name="ItemResponse", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private ItemResponse itemResponse;

    //endregion

    //region Properties

    public String getFormOid() {
        return this.formOid;
    }

    public void setFormOid(String oid) {
        this.formOid = oid;
    }

    public Integer getColumnNumber() {
        return this.columnNumber;
    }

    public void setColumnNumber(Integer number) {
        this.columnNumber = number;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    public String getPhi() {
        return this.phi;
    }

    public void setPhi(String value) {
        this.phi = value;
    }

    public String getShowItem() {
        return this.showItem;
    }

    public void setShowItem(String value) {
        this.showItem = value;
    }

    public Integer getOrderInForm() {
        return this.orderInForm;
    }

    public void setOrderInForm(Integer number) {
        this.orderInForm = number;
    }

    public String getLeftItemText() {
        return this.leftItemText;
    }

    public void setLeftItemText(String value) {
        this.leftItemText = value;
    }

    public String getItemHeader() {
        return this.itemHeader;
    }

    public void setItemHeader(String value) {
        this.itemHeader = value;
    }

    public String getSectionLabel() {
        return this.sectionLabel;
    }

    public void setSectionLabel(String value) {
        this.sectionLabel = value;
    }

    public ItemResponse getItemResponse() {
        return this.itemResponse;
    }

    public void setItemResponse(ItemResponse response) {
        this.itemResponse = response;
    }

    //endregion

}