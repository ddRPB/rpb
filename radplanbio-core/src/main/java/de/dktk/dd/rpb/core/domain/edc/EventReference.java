/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

package de.dktk.dd.rpb.core.domain.edc;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * StudyEventReference transient domain entity (CDISC ODM)
 *
 * @author RPB Team
 * @version 1.0.0
 * @since 13 Jun 2020
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StudyEventRef")
public class EventReference implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(EventReference.class);

    //endregion

    //region Members

    @XmlTransient
    private Integer id; // unused for Transient entity

    @XmlAttribute(name = "StudyEventOID")
    private String oid;

    @XmlTransient
    private Boolean isMandatory = Boolean.TRUE;

    @XmlAttribute(name = "OrderNumber")
    private Integer ordinal;

    @XmlTransient
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public EventReference() {

    }

    //endregion

    //region Properties

    //region Id

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

    //region OID

    public String getOid() {
        return this.oid;
    }

    public void setOid(String value) {
        this.oid = value;
    }

    //endregion

    //region IsMandatory

    public Boolean getIsMandatory() {
        return this.isMandatory;
    }

    public void setIsMandatory(Boolean value) {
        this.isMandatory = value;
    }

    //endregion

    //region Ordinal

    public Integer getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(Integer value) {
        this.ordinal = value;
    }

    //endregion

    //endregion

    //region Methods


    //endregion

    //region Overrides

    /**
     * Compare this entity to other provided entity
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof EventReference && this.hashCode() == other.hashCode());
    }

    /**
     * Generate hash code for this entity
     *
     * @return hash code of the entity
     */
    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this, this.oid);
    }

    /**
     * String representation of entity
     *
     * @return string representation of the entity
     */
    @Override
    public String toString() {
        return this.oid;
    }

    //endregion

}
