/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain.ctms;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * StudyDoc domain entity
 *
 * @author tomas@skripcak.net
 * @since 12 Sep 2013
 */
@Entity
@Table(name = "STUDYDOC", uniqueConstraints={
    @UniqueConstraint(columnNames = {"STUDYID", "FILENAME"})
})
public class StudyDoc implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(StudyDoc.class);

    //endregion

    //region Members

    // Raw attributes
    private Integer id; // pk
    private String filename; // not null
    private String displayname;
    private String description;

    // Many to one
    private DocType docType;
    private Study study;

    //endregion

    //region Constructors

    public StudyDoc() {
        // NOOP
    }

    public StudyDoc(Integer primaryKey) {
        setId(primaryKey);
    }

    //endregion

    //region Persistent Properties

    //region Id

    @Id
    @Column(name = "ID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studydoc_id_seq")
    @SequenceGenerator(name = "studydoc_id_seq", sequenceName = "studydoc_id_seq")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    //endregion

    //region Filename

    @Size(max = 255)
    @NotEmpty
    @Column(name = "FILENAME", nullable = false)
    public String getFilename()  {
        return this.filename;
    }

    public void setFilename(String value) {
        this.filename = value;
    }

    //endregion

    //region Displayname

    @Size(max = 255)
    @Column(name = "DISPLAYNAME")
    public String getDisplayname()  {
        return this.displayname;
    }

    public void setDisplayname(String value) {
        this.displayname = value;
    }

    //endregion

    //region Description

    @Size(max = 4000)
    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription()  {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    //endregion

    //endregion

    //region Many to One

    //region DocType

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="DOCTYPEID")
    public DocType getDocType() {
        return this.docType;
    }

    public void setDocType(DocType value) {
        this.docType = value;
    }

    //endregion

    //region Study

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="STUDYID", nullable=false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study value) {
        this.study = value;
    }

    //endregion

    //endregion

    //region Transient Properties

    @Transient
    public boolean isIdSet() {
        return this.id != null;
    }

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
        return this == other || (other instanceof StudyDoc && hashCode() == other.hashCode());
    }

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    @Override
    public int hashCode()
    {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this AnnotationType instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.getId())
                .add("name", this.getFilename())
                .add("name", this.getDisplayname())
                .add("description", this.getDescription())
                .toString();
    }

    //endregion

}