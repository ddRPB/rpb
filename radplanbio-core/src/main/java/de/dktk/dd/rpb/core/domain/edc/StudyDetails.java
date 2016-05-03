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
 * StudyDetails entity (based on CDISC ODM OpenClinica specific)
 *
 * @author tomas@skripcak.net
 * @since 26 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="StudyDetails", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class StudyDetails implements Serializable{

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(StudyDetails.class);

    //endregion

    //region Members

    @XmlAttribute(name="StudyOID")
    private String studyOid;

    @XmlElement(name="StudyParameterConfiguration", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private StudyParameterConfiguration studyParameterConfiguration;

    //endregion

    //region Properties

    public String getStudyOid() {
        return this.studyOid;
    }

    public void setStudyOid(String value) {
        this.studyOid = value;
    }

    public StudyParameterConfiguration getStudyParameterConfiguration() {
        return this.studyParameterConfiguration;
    }

    public void setStudyParameterConfiguration(StudyParameterConfiguration conf) {
        this.studyParameterConfiguration = conf;
    }

    //endregion

    //region Methods

    public void reloadParameters() {
        this.studyParameterConfiguration.reloadParameterFromList();
    }

    //endregion

}
