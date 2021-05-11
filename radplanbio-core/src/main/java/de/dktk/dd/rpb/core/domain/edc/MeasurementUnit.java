/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

import com.google.common.base.Objects;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * ODM Study BasicDefinition MeasurementUnit domain entity
 *
 * @author tomas@skripcak.net
 * @since 15 Aug 2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="MeasurementUnit")
public class MeasurementUnit implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MeasurementUnit.class);

    //endregion

    //region Members

    @XmlAttribute(name="OID")
    private String oid;

    @XmlAttribute(name="Name")
    private String name;

    @XmlElement(name="Symbol")
    private Symbol symbol;

    //endregion

    //region Constructors

    public MeasurementUnit() {
        // NOOP
    }

    //endregion

    //region Properties

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    //endregion

    //region Overrides

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("oid", this.oid)
                .add("name", this.name)
                .toString();
    }

    //endregion

}