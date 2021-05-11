/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain.rpb;

import com.google.common.base.Objects;
import org.apache.log4j.Logger;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * MobileComponent domain object entity implementing AbstractComponent
 *
 * @author tomas@skripcak.net
 * @since 15 Sep 2017
 */
//@Entity
//@DiscriminatorValue("MOBILE")
public class MobileComponent extends AbstractComponent {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(MobileComponent.class);

    //endregion

    //region Constructors

    public MobileComponent() {
        // NOOP
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof MobileComponent && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return this.identifiableHashBuilder.hash(log, this);
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
                .toString();
    }

    //endregion

}