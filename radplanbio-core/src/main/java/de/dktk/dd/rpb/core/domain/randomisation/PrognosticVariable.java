/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain.randomisation;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.criteria.AbstractCriterion;
import de.dktk.dd.rpb.core.domain.criteria.constraints.AbstractConstraint;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Target;

import javax.persistence.*;
import java.io.*;

/**
 * Prognostic variable of trial subject is used to construct strata for stratified randomisation
 * Every randomised clinical study in RadPlanBio defines properties of trial subjects (subjectCriteria) which are important
 * for the process of randomisation. When new trial subject is created each for each of these criteria the prognostic
 * variable is initialised.
 *
 * @author tomas@skripcak.net
 * @since 16 Sep 2013
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
//TODO: need to find a way to persist value that contains serialised assigned stratification criterion value
@Entity
@Table(name = "PROGNOSTICVAR")
public class PrognosticVariable<T extends Serializable> implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PrognosticVariable.class);

    //endregion

    //region Members

    private Integer id; // pk, auto-increment, serial

    //TODO: annotation on member variable does not seem to trigger JPA persistence
    @Target(value=Serializable.class)
    @Lob
    @Column(name = "VALUE")
    private T value; // any kind of value can be the prognostic variable (keep it generic)

    //TODO: this was just a try to force JPA to persist serialized data, does not seem to work
    //private byte[] binValue;

    // Many to One
    private TrialSubject subject;
    private AbstractCriterion<T,? extends AbstractConstraint<T>> criterion;

    //endregion

    //region Constructors

    @SuppressWarnings("unused")
    public PrognosticVariable() {
        // NOOP
    }

    /**
     * Instantiates a new prognostic variable for given criterion
     *
     * @param criterion the criterion
     */
    public PrognosticVariable(AbstractCriterion<T,? extends AbstractConstraint<T>> criterion) {
        this.criterion = criterion;
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prognosticvar_id_seq")
    @SequenceGenerator(name = "prognosticvar_id_seq", sequenceName = "prognosticvar_id_seq")
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

    //region Value

    // T needs to implement Serializable if JPA should persist it
    //TODO: direct JPA persistence annotations does not seem to help
    //@Target(value=Serializable.class)
    //@Lob
    //@Column(name = "VALUE")
    @Transient
    public T getValue() {
        return this.value;
    }

    public void setValue(T value) throws Exception {
        if (this.criterion != null) {
            this.criterion.isValueCorrect(value);
        }
        this.value = value;
    }


    //TODO: this was just a try to force JPA to persist serialized data, does not seem to work
//    @Transient
//    public Object getValue() {
//        try {
//            if (this.value != null) {
//                return this.value;
//            }
//            else {
//                if (this.binValue != null) {
//                    this.value = convertInstanceOfObject(new ObjectInputStream(new ByteArrayInputStream(this.binValue)).readObject());
//                }
//                return this.value;
//            }
//        }
//        catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setValue(Object o) throws Exception  {
//        if (this.criterion != null) {
//            this.criterion.isValueCorrect(value);
//        }
//
//        this.value = convertInstanceOfObject(o);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try {
//            final ObjectOutputStream oos = new ObjectOutputStream(baos);
//            oos.writeObject(o);
//            oos.close();
//            this.binValue = baos.toByteArray();
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Lob
//    @Column(name = "VALUE", columnDefinition = "BLOB")
//    public byte[] getBinValue() {
//        return this.binValue;
//    }
//
//    public void setBinValue(byte[] binValue) {
//        this.binValue = binValue;
//    }
    
    //endregion

    //region Subject

    @ManyToOne(targetEntity = TrialSubject.class)
    @JoinColumn(name="SUBJECTID")
    public TrialSubject getSubject() {
        return this.subject;
    }

    public void setSubject(TrialSubject subject) {
        this.subject = subject;
    }

    //endregion

    //region Criterion

    @ManyToOne(targetEntity = AbstractCriterion.class)
    @JoinColumn(name="CRITID")
    public AbstractCriterion<T, ? extends AbstractConstraint<T>> getCriterion() {
        return this.criterion;
    }

    /**
     * Set criterion
     * @param criterion abstract criterion for comparison
     */
    protected void setCriterion(AbstractCriterion<T, ? extends AbstractConstraint<T>> criterion) {
        this.criterion = criterion;
    }

    //endregion

    //region Stratum

    /**
     * Gets the stratum.
     *
     * @return the stratum
     */
    @Transient
    public long getStratum() throws Exception {
        AbstractConstraint<?> constraint = criterion.stratify(value);

        if (constraint == null) {
            return -1;
        }
        else {
            return constraint.getId();
        }
    }

    //endregion

    //endregion

    //region Static

    //TODO: just testing this approach for generic persistence
//    public static <T> T convertInstanceOfObject(Object o) {
//        try {
//            return (T) o;
//        } catch (ClassCastException e) {
//            return null;
//        }
//    }

    //endregion

}