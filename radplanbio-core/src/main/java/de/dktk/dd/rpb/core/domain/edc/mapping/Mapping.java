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

package de.dktk.dd.rpb.core.domain.edc.mapping;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.ctms.Study;

import org.apache.log4j.Logger;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Mapping domain entity
 * RPB study data mapping entity for import or export of data in various formats
 *
 * @author tomas@skripcak.net
 * @since 03 Mar 2015
 */
@Entity
@Table(name = "DATAMAPPING")
public class Mapping implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Mapping.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment
    private String name;
    private String description;
    private MappingTypeEnum type; // import or export
    private MappingItemTypeEnum sourceType; // csv or odm
    private MappingItemTypeEnum targetType; // csv or odm

    private Study study; // many to one
    private List<MappingRecord> mappingRecords; // one to many
    private List<AbstractMappedItem> sourceItemDefinitions; // one to many
    private List<AbstractMappedItem> targetItemDefinitions; // one to many

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public Mapping() {
        // NOOP
    }

    public Mapping(Integer primaryKey) {
        setId(primaryKey);
    }

    /**
     * Create a transient clone of mapping enity based on another mapping entity
     * @param anotherMapping mapping entity that should be cloned
     */
    public Mapping(Mapping anotherMapping) {
        this.name = anotherMapping.getName() + "-clone";
        this.description = anotherMapping.getDescription();
        this.type = anotherMapping.getType();
        this.sourceType = anotherMapping.getSourceType();
        this.targetType = anotherMapping.getTargetType();
        this.study = anotherMapping.getStudy();

        for (AbstractMappedItem ami : anotherMapping.getSourceItemDefinitions()) {
            // TODO: need to implement
        }

        for (AbstractMappedItem ami : anotherMapping.getTargetItemDefinitions()) {
            // TODO: need to implement
        }

        for (MappingRecord mr : anotherMapping.getMappingRecords()) {
            MappingRecord newMappingRecord = new MappingRecord(mr);
            this.mappingRecords.add(newMappingRecord);
        }
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "datamapping_id_seq")
    @SequenceGenerator(name = "datamapping_id_seq", sequenceName = "datamapping_id_seq")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return id != null;
    }

    //endregion

    //region Name

    @Column(name = "NAME", length = 255)
    @Size(max = 255)
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region Description

    @Column(name = "DESCRIPTION", length = 512)
    @Size(max = 512)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    //endregion

    //region Type

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 255)
    public MappingTypeEnum getType() {
        return this.type;
    }

    public void setType(MappingTypeEnum enumeration) {
        this.type = enumeration;
    }

    //endregion

    //region SourceType

    @Enumerated(EnumType.STRING)
    @Column(name = "SOURCETYPE", length = 255)
    public MappingItemTypeEnum getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(MappingItemTypeEnum enumeration) {
        this.sourceType = enumeration;
    }

    //endregion

    //region TargetType

    @Enumerated(EnumType.STRING)
    @Column(name = "TARGETTYPE", length = 255)
    public MappingItemTypeEnum getTargetType() {
        return this.targetType;
    }

    public void setTargetType(MappingItemTypeEnum enumeration) {
        this.targetType = enumeration;
    }

    //endregion

    //region Study - many Mappings are defined for one Study

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="STUDYID", nullable=false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    //endregion

    //region MappingRecords - One Mapping consists of many MappingRecords

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="MAPPINGID", referencedColumnName="ID")
    public List<MappingRecord> getMappingRecords() {
        return this.mappingRecords;
    }

    public void setMappingRecords(List<MappingRecord> list) {
        this.mappingRecords = list;
    }

    public Boolean addMappingRecord(MappingRecord mr) {
        return this.mappingRecords.add(mr);
    }

    public Boolean removeMappingRecord(MappingRecord mr) {
        return this.mappingRecords.contains(mr) && this.mappingRecords.remove(mr);
    }

    //endregion

    //region SourceItemDefinitions

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="MAPPINGID", referencedColumnName="ID")
    public List<AbstractMappedItem> getSourceItemDefinitions() {
        return this.sourceItemDefinitions;
    }

    public void setSourceItemDefinitions(List<AbstractMappedItem> list) {
        this.sourceItemDefinitions = list;
    }

    //endregion

    //region TargetItemDefinitions

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="TARGETMAPPINGID", referencedColumnName="ID")
    public List<AbstractMappedItem> getTargetItemDefinitions() {
        return this.targetItemDefinitions;
    }

    public void setTargetItemDefinitions(List<AbstractMappedItem> list) {
        this.targetItemDefinitions = list;
    }

    //endregion

    //endregion

    //region Methods

    public Boolean recordForTargetExists(AbstractMappedItem target) {
        Boolean result = false;

        for (MappingRecord mr : this.mappingRecords) {
            if (mr.getTarget().equals(target)) {
                result = true;
                break;
            }
        }

        return result;
    }

    public Boolean recordForSourceExists(AbstractMappedItem source) {
        Boolean result = false;

        for (MappingRecord mr : this.mappingRecords) {
            if (mr.getSource().equals(source)) {
                result = true;
                break;
            }
        }

        return result;
    }

    public Integer targetCount() {
        int result = 0;

        if (this.targetItemDefinitions != null) {
            result = this.targetItemDefinitions.size();
        }

        return result;
    }

    public Integer targetCountCoveredWithMapping() {
        int result = 0;

        for (AbstractMappedItem target : this.targetItemDefinitions) {
            if (this.recordForTargetExists(target)) {
                result += 1;
            }
        }

        return result;
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Mapping && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("name", this.name)
                .add("description", this.description)
                .add("type", this.type.toString())
                .add("sourceType", this.sourceType.toString())
                .add("targetType", this.targetType.toString())
                .toString();
    }

    //endregion

}
