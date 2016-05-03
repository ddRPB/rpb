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

package de.dktk.dd.rpb.core.domain.admin;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.apache.log4j.Logger;

import com.google.common.base.Objects;

/**
 * AuditLog domain entity
 *
 * @author tomas@skripcak.net
 * @since 27 January 2015
 */
@Entity
@Table(name = "AUDITLOG")
public class AuditLog implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(AuditLog.class);

    //endregion

    //region Members

    // Raw attributes
    private Integer id;
    private String username;
    private String event;
    private Date eventDate;
    private String stringAttribute1;
    private String stringAttribute2;
    private String stringAttribute3;

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public AuditLog() {
        //NOOP
    }

    @SuppressWarnings("unused")
    public AuditLog(Integer primaryKey) {
        this.setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "ID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auditlog_id_seq")
    @SequenceGenerator(name = "auditlog_id_seq", sequenceName = "auditlog_id_seq")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    @XmlTransient
    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region User

    @Size(max = 255)
    @Column(name = "USERNAME", length = 255)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //endregion

    //region Event

    @Size(max = 255)
    @Column(name = "EVENT", length = 255)
    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    //endregion

    //region EventDate

    @Column(name = "EVENTDATE")
    @Temporal(TIMESTAMP)
    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    //endregion

    //region StringAttribute1

    @Size(max = 255)
    @Column(name = "STRINGATTRIBUTE1", length = 255)
    public String getStringAttribute1() {
        return this.stringAttribute1;
    }

    public void setStringAttribute1(String stringAttribute1) {
        this.stringAttribute1 = stringAttribute1;
    }

    //endregion

    //region StringAttribute2

    @Size(max = 255)
    @Column(name = "STRINGATTRIBUTE2", length = 255)
    public String getStringAttribute2() {
        return this.stringAttribute2;
    }

    public void setStringAttribute2(String stringAttribute2) {
        this.stringAttribute2 = stringAttribute2;
    }

    //endregion

    //region StringAttribute3

    @Size(max = 256)
    @Column(name = "STRINGATTRIBUTE3", length = 256)
    public String getStringAttribute3() {
        return this.stringAttribute3;
    }

    public void setStringAttribute3(String stringAttribute3) {
        this.stringAttribute3 = stringAttribute3;
    }


    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof AuditLog && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this AuditLog instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("user", this.username)
                .add("event", this.event)
                .add("eventDate", this.eventDate)
                .add("stringAttribute1", this.stringAttribute1)
                .add("stringAttribute2", this.stringAttribute2)
                .add("stringAttribute3", this.stringAttribute3)
                .toString();
    }

    //endregion

}