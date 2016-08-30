/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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
 * RPB StudyParameter entity (based on CDISC ODM OpenClinica specific StudyParameterListRef)
 *
 * @author tomas@skripcak.net
 * @since 26 Feb 2014
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="StudyParameterListRef", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class StudyParameter implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(StudyParameter.class);

    //endregion

    //region Members

    @XmlAttribute(name="StudyParameterListID")
    private String studyParameterListId;

    @XmlAttribute(name="Value")
    private String value;

    //endregion

    //region Properties

    public String getStudyParameterListId() {
        return this.studyParameterListId;
    }

    public void setStudyParameterListId(String value) {
        this.studyParameterListId = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //endregion

}