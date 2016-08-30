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

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * ODM ItemDefinition RangeCheck domain entity
 *
 * @author tomas@skripcak.net
 * @since 15 Aug 2016
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="RangeCheck")
public class RangeCheck implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RangeCheck.class);

    //endregion

    //region Members

    @XmlAttribute(name="Comparator")
    private String comparator;

    @XmlAttribute(name="SoftHard")
    private String softHard;

    @XmlElement(name="CheckValue")
    private String checkValue;

    @XmlElement(name="ErrorMessage")
    private ErrorMessage errorMessage;

    //endregion

    //region Constructors

    public RangeCheck() {
        // NOOP
    }

    //endregion

    //region Properties

    public String getComparator() {
        return this.comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String getSoftHard() {
        return this.softHard;
    }

    public void setSoftHard(String softHard) {
        this.softHard = softHard;
    }

    public String getCheckValue() {
        return this.checkValue;
    }

    public void setCheckValue(String checkValue) {
        this.checkValue = checkValue;
    }

    public ErrorMessage getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    //endregion

}