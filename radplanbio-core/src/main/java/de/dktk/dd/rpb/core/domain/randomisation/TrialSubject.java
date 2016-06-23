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

package de.dktk.dd.rpb.core.domain.randomisation;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;

import org.apache.log4j.Logger;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.*;

/**
 * Randomised trial subject domain entity
 *
 * @author tomas@skripcak.net
 * @since 24 Jan 2014
 *
 * Inspired by RANDI2 http://dschrimpf.github.io/randi3/
 */
@Entity
@Table(name = "TRIALSUBJECT")
public class TrialSubject implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(TrialSubject.class);

    //endregion

    //region Members

    private Integer id; // pk
    private String pid; // RadPlanBio patient pseudonym
    private String ocStudySubjectId; // OpenClinica study subject id

    // Many To One
    private PartnerSite trialSite; // Trial subject belong to partner site
    private TreatmentArm treatmentArm; // Trial subject is assigned to treatment arm

    // One To Many
    private List<PrognosticVariable<?>> prognosticVariables; // Prognostic variables for stratified randomisation

    //endregion

    //region Constructors

    public TrialSubject() {
        // NOOP
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trialsubject_id_seq")
    @SequenceGenerator(name = "trialsubject_id_seq", sequenceName = "trialsubject_id_seq")
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

    //region Pid

    @Column(name="PID")
    public String getPid() {
        return this.pid;
    }

    public void setPid(String value) {
        this.pid = value;
    }

    //endregion

    //region OC study subject id

    @Column(name="OCSTUDYSUBJECTID")
    public String getOcStudySubjectId() {
        return ocStudySubjectId;
    }

    public void setOcStudySubjectId(String value) {
        this.ocStudySubjectId = value;
    }

    //endregion

    //region Trial site

    @ManyToOne
    @JoinColumn(name="SITEID")
    public PartnerSite getTrialSite() {
        return this.trialSite;
    }

    public void setTrialSite(PartnerSite site) {
        this.trialSite = site;
    }

    //endregion

    //region Treatment arm

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="ARMID")
    public TreatmentArm getTreatmentArm() {
        return this.treatmentArm;
    }

    public void setTreatmentArm(TreatmentArm arm) {
        this.treatmentArm = arm;
    }

    //endregion

    //region Prognostic variables

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY, mappedBy = "subject")
    public List<PrognosticVariable<?>> getPrognosticVariables() {
        return this.prognosticVariables;
    }

    public void setPrognosticVariables(List<PrognosticVariable<?>> variables) {
        this.prognosticVariables = variables;
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

    /**
     * Gets the strata for this trial subject
     *
     * Generate the stratum identification string for the actual trial subject.
     * [criterion_id]_[constraint_id];[criterion_id]_[constraint_id];...
     *
     * @return the strata string
     */
    @Transient
    public String getStrata() {
        List<String> stratum = new ArrayList<String>();

        if (this.prognosticVariables != null) {
            for (PrognosticVariable pv : this.prognosticVariables) {
                try {
                    stratum.add(pv.getCriterion().getId() + "_" + pv.getStratum());
                } catch (Exception err) {
                    log.error("Criterion violation", err);
                }
            }
        }

        Collections.sort(stratum);

        // Build a strata combined string as a unique representation of strata class
        StringBuilder result = new StringBuilder();
        for (String l : stratum) {
            result.append(l).append(";");
        }

        return result.toString();
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using an object hash code
     *
     * @param other object to compare with
     * @return true if objects are the same
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof TrialSubject && this.hashCode() == other.hashCode());
    }

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this object instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("pid", this.pid)
                .add("ocStudySubjectId", this.ocStudySubjectId)
                .toString();
    }

    //endregion

}