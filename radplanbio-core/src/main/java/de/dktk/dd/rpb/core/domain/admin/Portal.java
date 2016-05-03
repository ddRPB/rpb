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

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.NotEmpty;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;

/**
 * RPB Portal domain entity - Portal Server of partner site
 * Basically it hold the communication configuration to PartnerSite RadPlanBio Portal
 *
 * @author tomas@skripcak.net
 * @since 14 August 2013
 */
@Entity
@Table(name = "PORTAL")
public class Portal implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(Portal.class);

    //endregion

    //region Members

    private Integer portalId; // pk
    private String portalBaseUrl; // not null
    private String publicUrl;

    private Boolean isEnabled; // default true
    private String version;

    // One to many
    private List<Software> software;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public Portal() {
        // NOOP
    }

    public Portal(Integer primaryKey) {
        this.setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "PORTALID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "portal_portalid_seq")
    @SequenceGenerator(name = "portal_portalid_seq", sequenceName = "portal_portalid_seq")
    public Integer getId() {
        return this.portalId;
    }

    public void setId(Integer value) {
        this.portalId = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.portalId != null;
    }

    //endregion

    //region PortalBaseUrl

    @Size(max = 255)
    @NotEmpty
    @Column(name = "PORTALBASEURL", nullable = false, length = 255)
    public String getPortalBaseUrl()  {
        return this.portalBaseUrl;
    }

    public void setPortalBaseUrl(String value) {
        this.portalBaseUrl = value;
    }

    //endregion

    //region PublicUrl

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

    //region Software

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "portal", orphanRemoval = true)
    public List<Software> getSoftware() {
        return this.software;
    }

    public void setSoftware(List<Software> list) {
        this.software = list;
    }

    /**
     * Helper method to add the passed {@link Software} to the software list.
     */
    @SuppressWarnings("unused")
    public boolean addSoftware(Software software) {
        // Bidirectional
        software.setPortal(this);

        // Only add when does not exists
        return !this.containsSoftware(software) && this.software.add(software);
    }

    /**
     * Helper method to remove the passed {@link Software} from the software list.
     */
    @SuppressWarnings("unused")
    public boolean removeSoftware(Software software) {
        // Bidirectional
        software.setPortal(null);

        // Only remove when exists
        return this.containsSoftware(software) && this.software.remove(software);
    }

    /**
     * Helper method to determine if the passed {@link Software} is present in the software list.
     */
    @SuppressWarnings("unused")
    public boolean containsSoftware(Software software) {
        return this.software != null && this.software.contains(software);
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
    public boolean equals(Object other) {
        return this == other || (other instanceof Portal && hashCode() == other.hashCode());
    }


    @Override
    public int hashCode()
    {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Portal instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.portalId)
                .add("portalbaseurl", this.portalBaseUrl)
                .add("publicUrl", this.publicUrl)
                .add("isenabled", this.isEnabled)
                .add("version", this.version)
                .toString();
    }

    //endregion

}
