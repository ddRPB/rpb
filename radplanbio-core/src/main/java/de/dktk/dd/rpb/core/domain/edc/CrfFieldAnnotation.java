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
import de.dktk.dd.rpb.core.domain.ctms.Study;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * CrfFieldAnnotation domain entity
 * Mapping between OpenClinica eCRF field and its RadPlanBio annotation
 *
 * @author tomas@skripcak.net
 * @since 12 Sep 2013
 */
@Entity
@Table(name = "CRFFIELDANNOTATION")
public class CrfFieldAnnotation implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(CrfFieldAnnotation.class);

    //endregion

    //region Members

    // Raw attributes
    private Integer id; // pk
    private String eventDefinitionOid; // not null
    private String formOid; // not null
    private String groupOid; // not null
    private String crfItemOid; // not null

    // Many to one
    private Study study;
    private AnnotationType annotationType;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public CrfFieldAnnotation() {
        // NOOP
    }

    public CrfFieldAnnotation(Integer primaryKey) {
        setId(primaryKey);
    }

    public CrfFieldAnnotation(CrfFieldAnnotation annotation) {
        this.eventDefinitionOid = annotation.getEventDefinitionOid();
        this.formOid = annotation.getFormOid();
        this.groupOid = annotation.getGroupOid();
        this.annotationType = annotation.getAnnotationType();
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crffieldannotation_id_seq")
    @SequenceGenerator(name = "crffieldannotation_id_seq", sequenceName = "crffieldannotation_id_seq")
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

    //region EventDefinitionOid

    @Size(max = 255)
    @NotEmpty
    @Column(name = "EVENTDEFINITIONOID", nullable = false)
    public String getEventDefinitionOid()  {
        return this.eventDefinitionOid;
    }

    public void setEventDefinitionOid(String value) {
        this.eventDefinitionOid = value;
    }

    //endregion

    //region FormOid

    @Size(max = 255)
    @NotEmpty
    @Column(name = "FORMOID", nullable = false)
    public String getFormOid()  {
        return this.formOid;
    }

    public void setFormOid(String value) {
        this.formOid = value;
    }

    //endregion

    //region GroupOid

    @Size(max = 255)
    @NotEmpty
    @Column(name = "GROUPOID", nullable = false)
    public String getGroupOid()  {
        return this.groupOid;
    }

    public void setGroupOid(String value) {
        this.groupOid = value;
    }

    //endregion

    //region CrfItemOid

    @Size(max = 255)
    @NotEmpty
    @Column(name = "CRFITEMOID", nullable = false)
    public String getCrfItemOid()  {
        return this.crfItemOid;
    }

    public void setCrfItemOid(String value) {
        this.crfItemOid = value;
    }

    //endregion

    //endregion

    //region Many to One

    //region AnnotationType

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="TYPEID")
    public AnnotationType getAnnotationType() {
        return this.annotationType;
    }

    public void setAnnotationType(AnnotationType value) {
        this.annotationType = value;
    }

    //endregion

    //region Study

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="STUDYID")
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study value) {
        this.study = value;
    }

    //endregion

    //endregion

    //region Methods

    /**
     * Set the default values.
     */
    public void initDefaultValues() {
        // NOOP
    }

    //endregion

    //region Overrides

    /**
     * equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof CrfFieldAnnotation && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this CrfFieldAnnotation instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("eventDefinitionOid", this.eventDefinitionOid)
                .add("formOid", this.formOid)
                .add("groupOid", this.groupOid)
                .add("crfItemOid", this.crfItemOid)
                .toString();
    }

    //endregion

}