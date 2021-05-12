/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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
 * FormDetails transient domain entity (CDISC ODM OC extension)
 *
 * @author tomas@skripcak.net
 * @since 04 Jul 2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="FormDetails", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class FormDetails implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(FormDetails.class);

    //endregion

    //region Members

    @XmlAttribute(name = "FormOID")
    private String formOid;

    @XmlAttribute(name = "ParentFormOID")
    private String parentFormOid;

    @XmlElement(name = "VersionDescription")
    private String versionDescription;

    @XmlElement(name = "RevisionNotes")
    private String revisionNotes;

    // TODO: this should be changed to list because CRF can be used in multiple events with different settings
    @XmlElement(name = "PresentInEventDefinition", namespace = "http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private PresentInEventDefinition presentInEventDefinition;

    //endregion

    //region Constructors

    public FormDetails() {
        // NOOP
    }

    //endregion

    //region Properties

    public String getFormOid() {
        return formOid;
    }

    public void setFormOid(String formOid) {
        this.formOid = formOid;
    }

    public String getParentFormOid() {
        return parentFormOid;
    }

    public void setParentFormOid(String parentFormOid) {
        this.parentFormOid = parentFormOid;
    }

    public PresentInEventDefinition getPresentInEventDefinition() {
        return presentInEventDefinition;
    }

    public void setPresentInEventDefinition(PresentInEventDefinition presentInEventDefinition) {
        this.presentInEventDefinition = presentInEventDefinition;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public String getRevisionNotes() {
        return revisionNotes;
    }


    //endregion

}