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
 * ODM AdminData User domain entity
 *
 * @author gruhlmirko@gmx.de
 * @since 14 Aug 2018
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="User")
public class User implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    //unused
    private static final Logger log = Logger.getLogger(User.class);

    //endregion

    //region Members

    @XmlAttribute(name="OID")
    private String oid;

    @XmlElement(name="FullName")
    private String fullname;

    @XmlElement(name="FirstName")
    private String firstName;

    @XmlElement(name="LastName")
    private String lastName;

    @XmlElement(name="Organization")
    private String organization;

    //endregion

    //region Constructors

    public User() {
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

    public String getFullname() {
        return fullname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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
                .add("OID", this.oid)
                .add("FullName", this.fullname)
                .add("FirstName", this.firstName)
                .add("LastName", this.lastName)
                .add("Organization", this.organization)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if ( o == null )
            return false;

        if ( !(o instanceof User))
            return false;

        User test = (User) o;
        
        return this.getOid().equals(test.getOid());
    }

    @Override
    public int hashCode() {
        return oid.hashCode();
    }

    //endregion

}
