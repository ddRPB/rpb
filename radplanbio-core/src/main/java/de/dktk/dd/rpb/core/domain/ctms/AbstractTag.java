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
 * AbstractTag domain entity
 * It is used as a ancestor for entity related tag domain objects
 * Simple speaking it allows to collect tags for domain entities
 *
 * @author tomas@skripcak.net
 * @since 25 August 2013
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ENTITY")
@Table(name = "TAG")
public abstract class AbstractTag implements Identifiable<Integer>, Serializable {

    //region Finals

    protected static final long serialVersionUID = 1L;
    protected static final Logger log = Logger.getLogger(AbstractTag.class);

    //endregion

    //region Members

    protected Integer id; // pk, auto increment
    protected String value; // tag value

    // Many to one
    protected AbstractTagType type;

    // Object hash
    protected IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_id_seq")
    @SequenceGenerator(name = "tag_id_seq", sequenceName = "tag_id_seq")
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

    //region Value

    @Size(max = 255)
    @NotEmpty
    @Column(name = "VALUE", nullable = false, length = 255)
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //endregion

    //region BooleanValue

    @Transient
    public Boolean getBooleanValue() {
        Boolean result = null;

        if (this.value != null) {
            result = Boolean.valueOf(this.value);
        }

        return result;
    }

    public void setBooleanValue(Boolean value) {
        this.value = value.toString();
    }

    //endregion

    //region AbstractTagType

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TYPEID", nullable = false)
    public AbstractTagType getType() {
        return this.type;
    }

    public void setType(AbstractTagType value) {
        this.type = value;
    }

    //endregion

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof AbstractTag && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this entity instance.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("value", this.value)
                .toString();
    }

    //endregion

}