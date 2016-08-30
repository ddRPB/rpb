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

package de.dktk.dd.rpb.core.domain.ctms;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.Named;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;

import org.apache.log4j.Logger;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.base.Objects;

import de.dktk.dd.rpb.core.domain.admin.Portal;
import de.dktk.dd.rpb.core.domain.admin.Pid;
import de.dktk.dd.rpb.core.domain.admin.Server;
import de.dktk.dd.rpb.core.domain.admin.Pacs;
import de.dktk.dd.rpb.core.domain.edc.Edc;

/**
 * PartnerSite domain entity
 *
 * This is a partner site representation within RadPlanBio, for each partner data about connection to supporting
 * integrated systems as PACS, EDC, etc... are stored and used later when needed.
 *
 * @author tomas@skripcak.net
 * @since 08 August 2013
 */
@Entity
@Table(name = "PARTNERSITE")
public class PartnerSite implements Identifiable<Integer>, Named, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PartnerSite.class);

    //endregion

    //region Members

    private Integer siteId; // pk, auto-increment, serial
    private String identifier; // not null unique
    private String name; // not null
    private String description;
    private Float latitude;
    private Float longitude;

    private Boolean isEnabled;

    // One to one
    private Pacs pacs; // association to PACS system
    private Edc edc; // association to Electronic Data Capture systen
    private Pid pid; // association to Patient Identity Management sytsem
    private Portal portal; // association to RadPlanBio-Portal
    private Server server; // association to RadPlanBio import export server

    // One to many
    private List<DefaultAccount> defaultAccounts; // Partner site users

    // Many to many
    private List<Study> studies; // RadPlanBio studies where partner site is participating

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public PartnerSite() {
        // NOOP
    }

    public PartnerSite(Integer primaryKey) {
        setId(primaryKey);
    }

    //endregion

    //region Properties

    //region Id

    @Id
    @Column(name = "SITEID", precision = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "partnersite_siteid_seq")
    @SequenceGenerator(name = "partnersite_siteid_seq", sequenceName = "partnersite_siteid_seq")
    public Integer getId() {
        return this.siteId;
    }

    public void setId(Integer value) {
        this.siteId = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.siteId != null;
    }

    //endregion

    //region Identifier

    @Size(max = 10)
    @NotEmpty
    @Column(name = "IDENTIFIER", nullable = false, length = 10)
    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String value) {
        this.identifier = value;
    }

    //endregion

    //region Name

    @Size(max = 255)
    @NotEmpty
    @Column(name = "SITENAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    //endregion

    //region Description

    @Size(max = 255)
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    //endregion

    //region Latitude

    @Column(name = "LATITUDE")
    public Float getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Float value) {
        this.latitude = value;
    }

    //endregion

    //region Longitude

    @Column(name = "LONGITUDE")
    public Float getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Float value) {
        this.longitude = value;
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

    //region Pacs

    @OneToOne(fetch= FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="PACSID")
    public Pacs getPacs() {
        return this.pacs;
    }

    public void setPacs(Pacs pacs) {
        this.pacs = pacs;
    }

    //endregion

    //region Edc

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="EDCID")
    public Edc getEdc() {
        return this.edc;
    }

    public void setEdc(Edc edc) {
        this.edc = edc;
    }

    //endregion

    //region Pid

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="GENERATORID")
    public Pid getPid() {
        return this.pid;
    }

    public void setPid(Pid value) {
        this.pid = value;
    }

    //endregion

    //region Portal

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="PORTALID")
    public Portal getPortal() {
        return this.portal;
    }

    public void setPortal(Portal value) {
        this.portal = value;
    }

    //endregion

    //region Server

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="SERVERID")
    public Server getServer() {
        return this.server;
    }

    public void setServer(Server value) {
        this.server = value;
    }

    //endregion

    //region Users

    @Access(AccessType.PROPERTY)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "partnerSite")
    public List<DefaultAccount> getDefaultAccounts() {
        return this.defaultAccounts;
    }

    public void setDefaultAccounts(List<DefaultAccount> list) {
        this.defaultAccounts = list;
    }

    /**
     * Helper method to add the passed {@link DefaultAccount} to the user list.
     */
    public boolean addDefaultAccount(DefaultAccount account) {
        return this.getDefaultAccounts().add(account);
    }

    /**
     * Helper method to remove the passed {@link DefaultAccount} from the user list.
     */
    public boolean removeDefaultAccount(DefaultAccount account) {
        return this.getDefaultAccounts().remove(account);
    }

    /**
     * Helper method to determine if the passed {@link DefaultAccount} is present in the user list.
     */
    @SuppressWarnings("unused")
    public boolean containsDefaultAccount(DefaultAccount account) {
        return this.getDefaultAccounts() != null && this.getDefaultAccounts().contains(account);
    }

    //endregion

    //region Studies

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(mappedBy = "participatingSites")
    public List<Study> getStudies() {
        return this.studies;
    }

    public void setStudies(List<Study> list) {
        this.studies = list;
    }

    public boolean addStudy(Study study) {
        return study != null && this.studies.add(study);
    }

    public boolean removeStudy(Study study) {
        return this.studies != null && this.studies.remove(study);
    }

    @SuppressWarnings("unused")
    public boolean containsStudy(Study study) {
        return this.studies != null && this.studies.contains(study);
    }

    //endregion

    //endregion

    //region Init

    /**
     * Set the default values.
     */
    public void initDefaultValues() {
        // NOOP
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof PartnerSite && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this entity instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.siteId)
                .add("identifier", this.identifier)
                .add("sitename", this.name)
                .add("description", this.description)
                .add("latitude", this.latitude)
                .add("longitude", this.longitude)
                .add("isenabled", this.isEnabled)
                .toString();
    }

    //endregion

}