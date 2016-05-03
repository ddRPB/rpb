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

package de.dktk.dd.rpb.core.domain.admin;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

/**
 * RadPlanBio Server domain object - Import/Export Server of partner site
 * Basically it hold the communication configuration to PartnerSite Import/Export server
 *
 * @author tomas@skripcak.net
 * @since 14 August 2013
 */
@Entity
@Table(name = "SERVERIE")
public class Server implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Server.class);

    //endregion

    //region Members

    private Integer serverId; // pk
    private String ipAddress; // not null
    private Integer port; // not null
    private String publicUrl;

    private Boolean isEnabled; // default true
    private String version;

    //endregion

    //region Constructors

    public Server() {
        // NOOP
    }

    public Server(Integer primaryKey) {
        this.setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "SERVERID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serverie_serverid_seq")
    @SequenceGenerator(name = "serverie_serverid_seq", sequenceName = "serverie_serverid_seq")
    public Integer getId() {
        return this.serverId;
    }

    public void setId(Integer value) {
        this.serverId = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.serverId != null;
    }

    //endregion

    //region IpAddress

    @Size(max = 255)
    @NotEmpty
    @Column(name = "IPADDRESS", nullable = false, length = 255)
    public String getIpAddress()  {
        return this.ipAddress;
    }

    public void setIpAddress(String value) {
        this.ipAddress = value;
    }

    //endregion

    //region Port

    @Column(name = "PORT", nullable = false)
    public Integer getPort()  {
        return this.port;
    }

    public void setPort(Integer value) {
        this.port = value;
    }

    //endregion

    //region PublicURL

    @Size(max = 255)
    @Column(name = "PUBLICURL", length = 255)
    public String getPublicUrl()  {
        return this.publicUrl;
    }

    public void setPublicUrl(String value) {
        this.publicUrl = value;
    }

    //endregion

    //region IsEnabled

    @Column(name = "ISENABLED")
    public Boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(Boolean value) {
        this.isEnabled = value;
    }

    //endregion

    //region Version

    @Size(max = 15)
    @Column(name = "VERSION", length = 15)
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String value) {
        this.version = value;
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
     * equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other)
    {
        return this == other || (other instanceof Server && hashCode() == other.hashCode());
    }

    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    @Override
    public int hashCode()
    {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Server instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.getId())
                .add("ipaddress", this.getIpAddress())
                .add("port", this.getPort())
                .add("isenabled", this.getIsEnabled())
                .toString();
    }

    //endregion

}
