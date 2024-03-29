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

package de.dktk.dd.rpb.core.domain.ctms;

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.util.Constants;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Study TimeLineEvent domain entity
 *
 * @author tomas@skripcak.net
 * @since 30 Jun 2015
 */
@Entity
@Table(name = "TIMELINEEVENT")
public class TimeLineEvent implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(TimeLineEvent.class);

    //endregion

    //region Members

    private Integer id; // pk, auto increment
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;

    // Many-to-One
    //TODO: private EventImportance importance;
    private EventType type;
    private Study study;
    private Organisation organisation;

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    //endregion

    //region Constructors

    public TimeLineEvent() {
        // NOOP
    }

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "timelineevent_id_seq")
    @SequenceGenerator(name = "timelineevent_id_seq", sequenceName = "timelineevent_id_seq")
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
    @NotEmpty
    @Column(name = "NAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region Description

    @Size(max = 4000)
    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    //endregion

    //region StartDate

    @Column(name = "STARTDATE")
    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date date) {
        this.startDate = date;
    }

    @Transient
    public String getStartDateString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getStartDate();
        return date != null ? format.format(date) : null;
    }

    //endregion

    //region EndDate

    @Column(name = "ENDDATE")
    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date date) {
        this.endDate = date;
    }

    @Transient
    public String getEndDateString() {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        Date date = this.getEndDate();
        return date != null ? format.format(date) : null;
    }

    //endregion

    //region ManyToOne

    //region EventType

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TYPEID", nullable = false)
    public EventType getType() {
        return this.type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    //endregion

    //region Study

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STUDYID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    //endregion

    //region Organisation

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ORGANISATIONID")
    public Organisation getOrganisation() {
        return this.organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    //endregion

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
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof TimeLineEvent && hashCode() == other.hashCode());
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
                .toString();
    }

    //endregion

}
