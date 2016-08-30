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

package de.dktk.dd.rpb.core.domain.criteria.constraints;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.criteria.AbstractCriterion;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Abstract comparison constraint for inclusion criterion checking
 *
 * @author tomas@skripcak.net
 * @since 27 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE")
@Table(name = "CRITCONSTRAINT")
public abstract class AbstractConstraint<T> implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AbstractConstraint.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment, serial

    // Many to One
    AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> criterion;

    //endregion

    //region Constructors

    public AbstractConstraint() {
        // NOOP
    }

    public AbstractConstraint(List<T> args) throws Exception {
        this.configure(args);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "critconstraint_id_seq")
    @SequenceGenerator(name = "critconstraint_id_seq", sequenceName = "critconstraint_id_seq")
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

    //region Criterion

    @ManyToOne
    @JoinColumn(name="CRITID")
    public AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> getCriterion() {
        return this.criterion;
    }

    public void setCriterion(AbstractCriterion<? extends Serializable, ? extends AbstractConstraint<? extends Serializable>> criterion) {
        this.criterion = criterion;
    }

    //endregion

    //endregion

    //region Methods

    public boolean checkValue(T value) {
        try {
            this.isValueCorrect(value);
            return true;
        }
        catch (Exception err) {
            log.error("Error during the check of the criterion value.", err);
            return false;
        }
    }

    //endregion

    //region Abstract methods

    protected abstract void configure(List<T> args) throws Exception;

    public abstract void isValueCorrect(T value) throws Exception;

    public abstract int hashCode();

    public abstract boolean equals(Object o);

    //endregion

}