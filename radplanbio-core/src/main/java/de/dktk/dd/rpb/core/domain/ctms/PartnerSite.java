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

package de.dktk.dd.rpb.core.domain.ctms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.Named;
import de.dktk.dd.rpb.core.domain.admin.*;

import de.dktk.dd.rpb.core.domain.bio.Bio;
import de.dktk.dd.rpb.core.domain.edc.FormEngine;
import de.dktk.dd.rpb.core.domain.lab.Lab;
import de.dktk.dd.rpb.core.domain.pacs.Pacs;
import de.dktk.dd.rpb.core.domain.pid.Pid;
import de.dktk.dd.rpb.core.domain.rpb.AbstractComponent;
import de.dktk.dd.rpb.core.domain.rpb.PacsComponent;
import de.dktk.dd.rpb.core.domain.rpb.Portal;
import de.dktk.dd.rpb.core.domain.rpb.Server;
import org.apache.log4j.Logger;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.base.Objects;

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
@XmlRootElement
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
    private String ipRange;
    private Boolean isEnabled;

    // One-to-One
    private Portal portal; // association to RPB portal
    private Pid pid; // association to Patient Identity Management system (Mainzelliste)
    private Edc edc; // association to RPB Electronic Data Capture system (OpenClinica)
    private FormEngine formEngine; // TODO: association to RPB Form Engine (Enketo) move to components
    private Pacs pacs; // association to PACS system (Conquest)
    private Bio bio; // TODO: association to BIO bank system (Centraxx) move to components
    private Lab lab; // TODO: association to LAB system (LabKey) move to components
    private Server server; // association to RPB import export server (will be deprecated in the future)

    // One-to-Many
    private List<DefaultAccount> defaultAccounts; // Partner site users

    // Many-to-Many
    private List<Study> studies; // RPB studies where partner site is participating
    private List<AbstractComponent> components; // RPB components configured for partner site

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

    //region ID

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

    //region IpRange

    @Size(max = 4000)
    @Column(name = "IPRANGE", length = 4000)
    public String getIpRange() {
        return ipRange;
    }

    public void setIpRange(String ipRange) {
        this.ipRange = ipRange;
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

    //region One-to-One

    //region RPB portal

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="PORTALID")
    @XmlTransient
    public Portal getPortal() {
        return this.portal;
    }

    public void setPortal(Portal value) {
        this.portal = value;
    }

    //endregion

    //region PID

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="GENERATORID")
    @XmlTransient
    public Pid getPid() {
        return this.pid;
    }

    public void setPid(Pid value) {
        this.pid = value;
    }

    //endregion

    //region EDC

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="EDCID")
    @XmlTransient
    public Edc getEdc() {
        return this.edc;
    }

    public void setEdc(Edc edc) {
        this.edc = edc;
    }

    //endregion

    //region FormEngine

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="FORMENGINEID")
    @XmlTransient
    public FormEngine getFormEngine() {
        return this.formEngine;
    }

    public void setFormEngine(FormEngine formEngine) {
        this.formEngine = formEngine;
    }

    //endregion

    //region PACS

    @OneToOne(fetch= FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="PACSID")
    @XmlTransient
    public Pacs getPacs() {
        return this.pacs;
    }

    public void setPacs(Pacs pacs) {
        this.pacs = pacs;
    }

    //endregion

    //region BIO

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="BIOID")
    @XmlTransient
    public Bio getBio() {
        return this.bio;
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }

    //endregion

    //region LAB

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="LABID")
    @XmlTransient
    public Lab getLab() {
        return this.lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    //endregion

    //region RPB-server

    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="SERVERID")
    @XmlTransient
    public Server getServer() {
        return this.server;
    }

    public void setServer(Server value) {
        this.server = value;
    }

    //endregion

    //endregion

    //region One-to-Many

    //region Users

    @Access(AccessType.PROPERTY)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "partnerSite")
    @XmlTransient
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
    public boolean containsDefaultAccount(DefaultAccount account) {
        return this.getDefaultAccounts() != null && this.getDefaultAccounts().contains(account);
    }

    //endregion

    //endregion

    //region Many-to-Many

    //region Studies

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(mappedBy = "participatingSites")
    @XmlTransient
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

    //region Components

    //TODO: modify DB model
    @Transient
//    @LazyCollection(LazyCollectionOption.TRUE)
//    @JoinTable(name="PARTNERSITE_COMPONENT", joinColumns= { @JoinColumn(name="SITEID") }, inverseJoinColumns={ @JoinColumn(name="COMPONENTID") })
//    @ManyToMany(fetch= FetchType.LAZY, cascade = { PERSIST, MERGE })
    public List<AbstractComponent> getComponents() {
        return this.components;
    }

    public void setComponents(List<AbstractComponent> list) {
        this.components = list;
    }
    
    //endregion

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

    //region Methods

    /**
     * Determine whether EDC component is enabled for this partner site
     * @return true when EDC is enabled for this partner site
     */
    public boolean hasEnabledEdc() {
        return this.getEdc() != null && this.getEdc().getIsEnabled();
    }

    /**
     * Determine whether FormEngine component is enabled for this partner site
     * @return true when FormEngine is enabled for this partner site
     */
    public boolean hasEnabledFormEngine() {
        return  this.getFormEngine() != null && this.getFormEngine().getIsEnabled();
    }

    /**
     * Determine whether PACS component is enabled for this partner site
     * @return true when PACS is enabled for this partner site
     */
    public boolean hasEnabledPacs() {
        return this.getPacs() != null && this.getPacs().getIsEnabled();
    }

    /**
     * Determine whether BIO bank component is enabled for this partner site
     * @return true when BIO bank is enabled for this partner site
     */
    public boolean hasEnabledBio() {
        return this.getBio() != null && this.getBio().getIsEnabled();
    }

    /**
     * Determine whether LAB component is enabled for this partner site
     * @return true when LAB is enabled for this partner site
     */
    public boolean hasEnabledLab() {
        return this.getLab() != null && this.getLab().getIsEnabled();
    }

    /**
     * Determine whether PID generator component is enabled for this partner site
     * @return true when PID generator is enabled for this partner site
     */
    public boolean hasEnabledPid() {
        return this.getPid() != null && this.getPid().getIsEnabled();
    }

    public List<PacsComponent> findPacsComponents() {
        List<PacsComponent> result = new ArrayList<>();

        if (this.components != null && this.components.size() > 0) {

            for (int i = 0; i < this.components.size(); i++) {
                if (this.components.get(i) instanceof PacsComponent) {
                    result.add((PacsComponent) this.components.get(i));
                }
            }
        }

        return result;
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
                .add("siteName", this.name)
                .add("description", this.description)
                .add("latitude", this.latitude)
                .add("longitude", this.longitude)
                .add("ipRange", this.ipRange)
                .add("isEnabled", this.isEnabled)
                .toString();
    }

    //endregion

}