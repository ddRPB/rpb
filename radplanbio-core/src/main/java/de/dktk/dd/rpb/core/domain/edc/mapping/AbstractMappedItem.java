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

package de.dktk.dd.rpb.core.domain.edc.mapping;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

import org.apache.log4j.Logger;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Abstract MappedItem domain entity
 * Mapped item, no matter which type... csv, odm
 *
 * @author tomas@skripcak.net
 * @since 03 Mar 2015
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE")
@Table(name = "MAPPEDITEM")
public abstract class AbstractMappedItem implements Identifiable<Integer>, Serializable {

    //region Finals

    protected static final long serialVersionUID = 1L;
    protected static final Logger log = Logger.getLogger(AbstractMappedItem.class);

    //endregion

    //region Members

    protected Integer id; // pk, auto increment
    protected String label; // transient (for UI data binding)

    // Object hash
    protected IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mappeditem_id_seq")
    @SequenceGenerator(name = "mappeditem_id_seq", sequenceName = "mappeditem_id_seq")
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

    //region Label

    @Transient
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String value) {
        this.label = value;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof AbstractMappedItem && hashCode() == other.hashCode());
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
                .add("label", this.label)
                .toString();
    }

    //endregion
}