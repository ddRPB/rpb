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
 * ODM MetaDataVersion domain entity
 *
 * @author tomas@skripcak.net
 * @since 15 Dec 2014
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="MetaDataVersion")
public class MetaDataVersion implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MetaDataVersion.class);

    //endregion

    //region Members

    //region CDISC ODM

    @XmlAttribute(name="OID")
    private String oid;

    @XmlAttribute(name="Name")
    private String name;

    @XmlElement(name="StudyEventDef")
    private List<EventDefinition> studyEventDefinitions;

    @XmlElement(name="FormDef")
    private List<FormDefinition> formDefinitions;

    @XmlElement(name="ItemGroupDef")
    private List<ItemGroupDefinition> itemGroupDefinitions;

    @XmlElement(name="ItemDef")
    private List<ItemDefinition> itemDefinitions;

    @XmlElement(name="CodeList")
    private List<CodeListDefinition> codeListDefinitions;

    //endregion

    //region OpenClinica

    @XmlElement(name="MultiSelectList", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private List<MultiSelectListDefinition> multiSelectListDefinitions;

    @XmlElement(name="StudyDetails", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private StudyDetails studyDetails;

    //endregion

    //endregion

    //region Constructors

    public MetaDataVersion() {
        this.studyEventDefinitions = new ArrayList<EventDefinition>();
        this.formDefinitions = new ArrayList<FormDefinition>();
        this.itemGroupDefinitions = new ArrayList<ItemGroupDefinition>();
        this.itemDefinitions = new ArrayList<ItemDefinition>();
        this.codeListDefinitions = new ArrayList<CodeListDefinition>();
        this.multiSelectListDefinitions = new ArrayList<MultiSelectListDefinition>();
    }

    //endregion

    //region Properties

    //region CDISC ODM

    //region OID

    public String getOid() {
        return this.oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    //endregion

    //region Name

    public String getName() {
        return  this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region StudyEventDefinitions

    public List<EventDefinition> getStudyEventDefinitions() {
        return this.studyEventDefinitions;
    }

    public void setStudyEventDefinitions(List<EventDefinition> list) {
        this.studyEventDefinitions = list;
    }

    //endregion

    //region FormDefinitions

    public List<FormDefinition> getFormDefinitions() {
        return this.formDefinitions;
    }

    public void setFormDefinitions(List<FormDefinition> list) {
        this.formDefinitions = list;
    }

    //endregion

    //region ItemGroupDefinitions

    public List<ItemGroupDefinition> getItemGroupDefinitions() {
        return this.itemGroupDefinitions;
    }

    public void setItemGroupDefinitions(List<ItemGroupDefinition> list) {
        this.itemGroupDefinitions = list;
    }

    //endregion

    //region ItemDefinitions

    public List<ItemDefinition> getItemDefinitions() {
        return this.itemDefinitions;
    }

    public void setItemDefinitions(List<ItemDefinition> list) {
        this.itemDefinitions = list;
    }

    //endregion

    //region CodeListDefinitions

    public List<CodeListDefinition> getCodeListDefinitions() {
        return this.codeListDefinitions;
    }

    public void setCodeListDefinitions(List<CodeListDefinition> list) {
        this.codeListDefinitions = list;
    }

    //endregion

    //endregion

    //region OpenClinica

    //region MultiSelectListDefinitions

    public List<MultiSelectListDefinition> getMultiSelectListDefinitions() {
        return this.multiSelectListDefinitions;
    }

    public void setMultiSelectListDefinitions(List<MultiSelectListDefinition> list) {
        this.multiSelectListDefinitions = list;
    }

    //endregion

    //region StudyDetails

    public StudyDetails getStudyDetails() {
        return this.studyDetails;
    }

    public void setStudyDetails(StudyDetails details) {
        this.studyDetails = details;
    }

    //endregion

    //endregion

    //endregion

}
