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

package de.dktk.dd.rpb.core.domain.edc;

import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

/**
 * ODM Study BasicDefinition domain entity
 *
 * @author tomas@skripcak.net
 * @since 15 Aug 2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="BasicDefinitions")
public class BasicDefinitions implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(BasicDefinitions.class);

    //endregion

    //region Members

    @XmlElement(name="MeasurementUnit")
    private List<MeasurementUnit> measurementUnits;

    //endregion

    //region Constructors

    public BasicDefinitions() {
        // NOOP
    }

    //endregion

    //region Properties

    public List<MeasurementUnit> getMeasurementUnits() {
        return this.measurementUnits;
    }

    public void setMeasurementUnits(List<MeasurementUnit> measurementUnits) {
        this.measurementUnits = measurementUnits;
    }

    //endregion

}