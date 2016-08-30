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

package de.dktk.dd.rpb.core.domain.criteria;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.criteria.constraints.AbstractConstraint;

import org.apache.log4j.Logger;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract criterion for comparison. Criterion value type in this case generic. Criterion represents some sort of value
 * checking. E.g. we can define criterion for choosing between gender of the subject, or for answering simple yes/no
 * questions (e.g. patient has resection performed?).
 *
 * Each criterion belong to the study. Study is domain root (aggregate object). Study criteria are than used for randomisation
 *
 * Criterion contains from list of constraints (where the constraint is the same type as criterion) which defines strata for randomisation
 *
 * @author tomas@skripcak.net
 * @since 27 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE")
@Table(name = "CRIT")
public abstract  class AbstractCriterion<T extends Serializable, C extends AbstractConstraint<T>> implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AbstractCriterion.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment, serial
    private String name;
    private String description;

    // Many to One
    private Study study;

    // One to Many
    protected List<C> strata;

    @Transient
    @SuppressWarnings("unused")
    protected List<T> configuredValues; // List of values configured which is reported by concrete class inherited from abstract criterion

    //endregion

    //region Constructors

    public AbstractCriterion() {
        // NOOP
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crit_id_seq")
    @SequenceGenerator(name = "crit_id_seq", sequenceName = "crit_id_seq")
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

    //region Name

    @Size(max = 255)
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region Description

    @Size(max = 255)
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    //endregion

    //region Study

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="STUDYID")
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    //endregion

    //region Strata

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity=AbstractConstraint.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "criterion", orphanRemoval = true)
    public List<C> getStrata() {
        return this.strata;
    }

    public void setStrata(List<C> strata){
        this.strata = strata;
    }

    @SuppressWarnings("unchecked, unused")
    public void addStrata(AbstractConstraint<?> abstractConstraint){
        if (this.strata == null){
            this.strata = new ArrayList<C>();
        }

        // Bidirectional
        if (abstractConstraint != null) {
            abstractConstraint.setCriterion(this);
        }

        this.strata.add((C) abstractConstraint);
    }

    //endregion

    //region ConfiguredValues

    @Transient
    @SuppressWarnings("unused")
    public abstract List<T> getConfiguredValues();

    //endregion

    //endregion

    //region Methods

    @Transient
    @SuppressWarnings("unused")
    public abstract Class<C> getContstraintType();

    @SuppressWarnings("unused")
    public C stratify(T value) throws Exception {
        this.isValueCorrect(value);

        if (strata == null || strata.isEmpty()) {
            return null;
        }

        for (C stratum : strata) {
            if (stratum.checkValue(value)) {
                return stratum;
            }
        }

        return null;
    }

    public abstract void isValueCorrect(T value) throws Exception;

    @SuppressWarnings("unused")
    public boolean checkValue(T value) {
        try {
            isValueCorrect(value);
            return true;
        }
        catch (Exception err) {
            return false;
        }
    }

    //endregion

}