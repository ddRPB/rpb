/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PacsComponent (Conquest) domain object entity implementing AbstractComponent
 *
 * @author tomas@skripcak.net
 * @since 21 Nov 2017
 */
//@Entity
//@DiscriminatorValue("PACS")
public class PacsComponent extends AbstractComponent {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(PacsComponent.class);

    //endregion

    //region Members

    private String aet;
    private Boolean isClinical;

    //endregion

    //region Constructors

    public PacsComponent() {
        this.isClinical = false;
    }

    public PacsComponent(String aet, String host, int port) {
        this();
        this.aet = aet;
        this.host = host;
        this.port = port;
    }

    //endregion

    //region Properties

    //@Size(max = 255)
    //@Column(name = "AET", nullable = false)
    public String getAet() {
        return this.aet;
    }

    public void setAet(String aet) {
        this.aet = aet;
    }

    //@Column(name = "ISCLINICAL")
    public Boolean getIsClinical() {
        return this.isClinical;
    }

    public void setIsClinical(Boolean isClinical) {
        this.isClinical = isClinical;
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof PacsComponent && hashCode() == other.hashCode());
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
                .add("aet", this.aet)
                .add("host", this.host)
                .add("port", this.port.toString())
                .toString();
    }

    //endregion

}
