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

import com.google.common.base.Objects;

import org.apache.log4j.Logger;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * RPB item data question (based on CDISC ODM)
 *
 * @author tomas@skripcak.net
 * @since 30 Dec 2014
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Question")
public class Question implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Question.class);

    //endregion

    //region Members

    @XmlElement(name="TranslatedText")
    private String translatedText;

    //endregion

    //region Constructors

    public Question() {
        // NOOP
    }

    //endregion

    //region Properties

    public String getTranslatedText() {
        return this.translatedText;
    }

    public void setTranslatedText(String value) {
        this.translatedText = value;
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Question && hashCode() == other.hashCode());
    }

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("translatedText", this.translatedText)
                .toString();
    }

    //endregion

    //region Methods

    //endregion
}