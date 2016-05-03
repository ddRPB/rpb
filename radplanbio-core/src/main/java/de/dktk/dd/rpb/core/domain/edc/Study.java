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
import org.openclinica.ws.beans.StudyType;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * OpenClinica Study transient domain entity
 *
 * @author tomas@skripcak.net
 * @since 18 Oct 2013
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Study")
public class Study implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Study.class);

    //endregion

    //region Members

    @XmlAttribute(name="OID")
    private String oid;

    @XmlElement(name="MetaDataVersion")
    private MetaDataVersion metaDataVersion;

    private Integer id;
    private String uniqueIdentifier;
    private String secondaryIdentifier;
    private String name;
    private String ocoid;

    private Status status;

    private Study parentStudy;

    //endregion

    //region Constructors

    public Study() {
        // NOOP
    }

    public Study(StudyType st) {
        this.oid = st.getOid();
        this.name = st.getName();
        this.uniqueIdentifier = st.getIdentifier();
    }

    //endregion

    //region Properties

    //region OID

    public String getOid() {
        return this.oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    //endregion

    //region MetaDataVersion

    public MetaDataVersion getMetaDataVersion() {
        return this.metaDataVersion;
    }

    public void setMetaDataVersion(MetaDataVersion entity) {
        this.metaDataVersion = entity;
    }

    //endregion

    //region Id

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    //endregion

    //region UniqueIdentifier

    public String getUniqueIdentifier() {
        return this.uniqueIdentifier;
    }

    public void setUniqueIdentifier(String value) {
        this.uniqueIdentifier = value;
    }

    //endregion

    //region Secondary Identifier

    public String getSecondaryIdentifier() {
        return this.secondaryIdentifier;
    }

    public void setSecondaryIdentifier(String value) {
        this.secondaryIdentifier = value;
    }

    //endregion

    //region Name

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region OC OID

    public String getOcoid() {
        return this.ocoid;
    }

    public void setOcoid(String value) {
        this.ocoid = value;
    }

    //endregion

    //region Status

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status object) {
        this.status = object;
    }

    //endregion

    //region ParentStudy

    public Study getParentStudy() {
        return this.parentStudy;
    }

    public void setParentStudy(Study study) {
        this.parentStudy = study;
    }

    //endregion

    //endregion

    //region Methods

    //endregion

}
