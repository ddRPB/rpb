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

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.apache.log4j.Logger;

import javax.persistence.Transient;
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
public class ClinicalData implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ClinicalData.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id;

    @XmlAttribute(name="StudyOID")
    private String studyOid;

    @XmlAttribute(name="MetaDataVersionOID")
    private String metaDataVersionOid;

    @XmlElement(name="SubjectData")
    private List<StudySubject> studySubjects;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

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

    //region Id

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region StudyOID

    public String getStudyOid() {
        return this.studyOid;
    }

    public void setStudyOid(String value) {
        this.studyOid = value;
    }

    //endregion

    //region MetaDataVersionOID

    public String getMetaDataVersionOid() {
        return this.metaDataVersionOid;
    }

    public void setMetaDataVersionOid(String value) {
        this.metaDataVersionOid = value;
    }

    //endregion

    //region StudySubjects

    public List<StudySubject> getStudySubjects() {
        return this.studySubjects;
    }

    public void setStudySubjects(List<StudySubject> list) {
        this.studySubjects = list;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ClinicalData && hashCode() == other.hashCode());
    }

    /**
     * Generate entity hash code
     * @return hash
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.studyOid);
    }

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("studyOid", this.studyOid)
                .add("metaDataVersionOid", this.metaDataVersionOid)
                .toString();
    }

    //endregion

}