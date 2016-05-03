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
 * CDISC ODM clinical data domain entity
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ClinicalData")
public class ClinicalData implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ClinicalData.class);

    //endregion

    //region Members

    @XmlAttribute(name="StudyOID")
    String studyOid;

    @XmlAttribute(name="MetaDataVersionOID")
    String metaDataVersionOid;

    @XmlElement(name="SubjectData")
    private List<StudySubject> studySubjects;

    //endregion

    //region Constructors

    public ClinicalData() {
        // NOOP
    }

    public ClinicalData(String studyOid) {
        this.studyOid = studyOid;
    }

    public ClinicalData(String studyOid, String metaDataVersionOid) {
        this.studyOid = studyOid;
        this.metaDataVersionOid = metaDataVersionOid;
        this.studySubjects = new ArrayList<StudySubject>();
    }

    //endregion

    //region Properties

    public String getStudyOid() {
        return this.studyOid;
    }

    public void setStudyOid(String value) {
        this.studyOid = value;
    }

    public String getMetaDataVersionOid() {
        return this.metaDataVersionOid;
    }

    public void setMetaDataVersionOid(String value) {
        this.metaDataVersionOid = value;
    }

    public List<StudySubject> getStudySubjects() {
        return this.studySubjects;
    }

    public void setStudySubjects(List<StudySubject> list) {
        this.studySubjects = list;
    }

    //endregion

}
