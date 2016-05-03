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
import org.apache.log4j.Logger;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * ObservationalProtocolType domain object entity implementing AbstractProtocolType
 *
 * @author tomas@skripcak.net
 * @since 17 Sep 2013
 */
@Entity
@DiscriminatorValue("OBSERVATIONAL")
public class ObservationalProtocolType extends AbstractProtocolType {

    // Purpose: Natural history, Screening, Psychosocial
    // Duration: Longitudinal, Cross-sectional
    // Selection: Convenience Sample, Defined Population, Random Sample, Case Control
    // Timing: Retrospective, Prospective

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ObservationalProtocolType.class);

    //endregion

    //region Constructors

    public ObservationalProtocolType() {
        // NOOP
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ObservationalProtocolType && hashCode() == other.hashCode());
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
