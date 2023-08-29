/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2021 RPB Team
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
import de.dktk.dd.rpb.core.domain.Named;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * AbstractTagType domain entity
 * It is used as a ancestor for entity related tag type domain objects
 * Simple speaking it allows to collect tags in certain types for domain entities
 *
 * @author tomas@skripcak.net
 * @since 25 August 2013
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ENTITY")
@Table(name = "TAGTYPE")
public abstract class AbstractTagType implements Identifiable<Integer>, Named, Serializable {

    //region Finals

    protected static final long serialVersionUID = 1L;
    protected static final Logger log = LoggerFactory.getLogger(AbstractTagType.class);

    //endregion

    //region Members

    protected Integer id; // pk, auto increment
    protected String name; // can be used for UI as label
    protected String description; // can be used for UI as tooltip

    protected Boolean isBoolean; // tag value is true false representation and it should be converted to boolean in UI
    protected String regex; // regular expression can be used for string tag validation
    protected Integer size; // can be used for string tag size validation

    protected Boolean isRequired; // can be used for tag validation
    protected Integer maxOccurence; // can be used for tag validation and business logic

    // Object hash
    protected IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Properties

    //region ID

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tagtype_id_seq")
    @SequenceGenerator(name = "tagtype_id_seq", sequenceName = "tagtype_id_seq")
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

    //region IsBoolean

    @Column(name = "ISBOOLEAN")
    public Boolean getIsBoolean() {
        return this.isBoolean;
    }

    public void setIsBoolean(Boolean value) {
        this.isBoolean = value;
    }

    //endregion

    //region RegEx

    @Size(max = 255)
    @Column(name = "REGEX")
    public String getRegex() {
        return this.regex;
    }

    public void setRegex(String value) {
        this.regex = value;
    }

    //endregion

    //region Size

    @Column(name = "SIZE")
    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer value) {
        this.size = value;
    }

    //endregion

    //region IsRequired

    @Column(name = "ISREQUIRED")
    public Boolean getIsRequired() {
        return this.isRequired;
    }

    public void setIsRequired(Boolean value) {
        this.isRequired = value;
    }

    //endregion

    //region MaxOccurence

    @Column(name="MAXOCCURENCE")
    public Integer getMaxOccurence() {
        return this.maxOccurence;
    }

    public void setMaxOccurence(Integer value) {
        this.maxOccurence = value;
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

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof AbstractTagType && hashCode() == other.hashCode());
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
                .add("name", this.name)
                .add("description", this.description)
                .toString();
    }

    //endregion

}
