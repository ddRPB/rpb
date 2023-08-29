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

package de.dktk.dd.rpb.core.domain.admin;

import com.google.common.base.Objects;
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.edc.Study;
import de.dktk.dd.rpb.core.util.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

/**
 * DefaultAccount is primary user account entity in RPB, every user have to have at least one default account
 * each account is normally also connected with e.g. secondary account in EDC system (OpenClinica)
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@Entity
@Table(name = "DEFAULTACCOUNT")
@XmlRootElement
public class DefaultAccount implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DefaultAccount.class);

    //endregion

    //region Members

    // Raw attributes
    private Integer id; // pk
    private String username; // unique (not null)
    private String password; // not null
    private String email; // default null
    private Boolean isEnabled;

    private String ocUsername; // default null
    private String ocPasswordHash; // transient

    private Instant lastVisit;
    private Boolean nonLocked;
    private Integer lockCounter;

    private String apiKey; // unique
    private Boolean apiKeyEnabled;

    // Many to many
    private List<Role> roles = new ArrayList<Role>();

    // Many to one
    private PartnerSite partnerSite;

    // Transient
    private Study activeStudy;
    private String passwordCopy;
    private String oldPassword;

    // Object hash
    private IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder();

    //endregion

    //region Constructors

    public DefaultAccount() {
        // NOOP
    }

    public DefaultAccount(Integer primaryKey) {
        setId(primaryKey);
    }

    //endregion

    //region Persistent Properties

    //region Id

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "defaultaccount_id_seq")
    @SequenceGenerator(name = "defaultaccount_id_seq", sequenceName = "defaultaccount_id_seq")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region Username

    @Size(min = 4, max = 255)
    @NotEmpty
    @Column(name = "USERNAME", nullable = false, unique = true, length = 255)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //endregion

    //region Password

    @Size(max = 255)
    @NotEmpty
    @Column(name = "PASSWORD", nullable = false, length = 255)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //endregion

    //region Email

    @Size(max = 255)
    @Column(name = "EMAIL", length = 255)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    //endregion

    //region IsEnabled

    @Column(name = "ISENABLED", length = 1)
    public Boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    //endregion

    //region OcUsername

    @Size(max = 255)
    @Column(name = "OCUSERNAME", length = 255)
    public String getOcUsername() {
        return this.ocUsername;
    }

    public void setOcUsername(String value) {
        this.ocUsername = value;
    }

    //endregion

    //region LastVisit

    @Column(name = "LASTVISIT")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentInstantAsTimestamp")
    @SuppressWarnings("unused")
    public Instant getLastVisit() {
        return this.lastVisit;
    }

    @SuppressWarnings("unused")
    public void setLastVisit(Instant lastVisit) {
        this.lastVisit = lastVisit;
    }

    //endregion

    //region NonLocked

    @Column(name = "NONLOCKED")
    @SuppressWarnings("unused")
    public Boolean getNonLocked() {
        return this.nonLocked;
    }

    @SuppressWarnings("unused")
    public void setNonLocked(Boolean nonLocked) {
        this.nonLocked = nonLocked;
    }

    //endregion

    //region LockCounter

    @Column(name = "LOCKCOUNTER")
    @SuppressWarnings("unused")
    public Integer getLockCounter() {
        return this.lockCounter;
    }

    @SuppressWarnings("unused")
    public void setLockCounter(Integer lockCounter) {
        this.lockCounter = lockCounter;

    }

    //endregion

    //region ApiKey

    @Size(max = 255)
    @Column(name = "APIKEY", unique = true, length = 255)
    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    //endregion

    //region ApiKeyEnabled

    @Column(name = "APIKEYENABLED")
    public Boolean getApiKeyEnabled() {
        return this.apiKeyEnabled;
    }

    public void setApiKeyEnabled(Boolean apiKeyEnabled) {
        this.apiKeyEnabled = apiKeyEnabled;
    }

    //endregion

    //endregion

    //region Transient Properties

    //region PasswordCopy

    @Transient
    @XmlTransient
    public String getPasswordCopy() {
        return passwordCopy;
    }

    public void setPasswordCopy(String value) {
        this.passwordCopy = value;
    }

    //endregion

    //region OldPassword

    @Transient
    @XmlTransient
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String value) {
        this.oldPassword = value;
    }

    //endregion

    //region IsLdapUser

    /**
     * Determine whether the user is LDAP user
     * @return true for LDAP user
     */
    @Transient
    public boolean isLdapUser() {
        return Constants.RPB_LDAPPASSHASH.equals(this.password);
    }

    //endregion

    //region OCPasswordHash

    @Transient
    public String getOcPasswordHash() {
        return this.ocPasswordHash;
    }

    public void setOcPasswordHash(String value) {
        this.ocPasswordHash = value;
    }

    //endregion

    //region ActiveStudy EDC

    @Transient
    public Study getActiveStudy() {
        return this.activeStudy;
    }

    public void setActiveStudy(Study study) {
        this.activeStudy= study;
    }

    //endregion

    //endregion

    //region Many to One

    //region PartnerSite

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="PARTNERSITEID")
    @XmlTransient
    public PartnerSite getPartnerSite() {
        return this.partnerSite;
    }

    public void setPartnerSite(PartnerSite value) {
        this.partnerSite = value;
    }

    //endregion

    //endregion

    //region Many to Many

    /**
     * Get list of granted authorities {@link Role} for this user
     */
    @JoinTable(name = "ACCOUNT_ROLE", joinColumns = @JoinColumn(name = "ACCOUNTID"), inverseJoinColumns = @JoinColumn(name = "ROLEID"))
    @ManyToMany(fetch= FetchType.EAGER, cascade = { PERSIST, MERGE })
    @XmlTransient
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Set the list of granted authorities.
     *
     * It is recommended to use the helper method {@link #addRole(Role)} / {@link #removeRole(Role)}
     * if you want to preserve referential integrity at the object level.
     *
     * @param roles the list of Role
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Helper method to add the passed {@link Role} to the roles list.
     */
    public Boolean addRole(Role role) {
        if (!this.containsRole(role)) {
            if (this.roles == null) {
                this.roles = new ArrayList<Role>();
            }
            return this.roles.add(role);
        }

        return Boolean.FALSE;
    }

    /**
     * Helper method to add passed {@link Role} entity list to assigned roles
     * @param roles list of roles to assign
     * @return true if successful
     */
    public Boolean addRoles(List<Role> roles) {
        for (Role r : roles) {
            if (!this.addRole(r)) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    /**
     * Helper method to remove the passed {@link Role} from the roles list.
     */
    public boolean removeRole(Role role) {
        return this.containsRole(role) && this.roles.remove(role);
    }

    /**
     * Helper method to determine if the passed {@link Role} is present in the roles list.
     */
    public boolean containsRole(Role role) {
        return this.roles != null && this.roles.contains(role);
    }

    /**
     * Determine whether user have assigned authority with given role
     * @param role role to compare
     * @return true when user have assigned authority with given role
     */
    public Boolean hasRole(Role role) {
        if (role != null) {
            return this.hasRoleName(role.getName());
        }

        return Boolean.FALSE;
    }

    /**
     * Determine whether user have assigned authority with given roleName
     * @param roleName roleName to compare
     * @return true when user have assigned authority with given roleName
     */
    public Boolean hasRoleName(String roleName) {
        if (!"".equals(roleName) && this.roles != null) {
            for (Role r : this .roles) {
                if (r.getName().equals(roleName)) {
                    return Boolean.TRUE;
                }
            }
        }

        return Boolean.FALSE;
    }

    /**
     * Returns the granted authorities name list for this user
     * @return list of role names
     */
    @Transient
    @XmlTransient
    public List<String> getRoleNames() {
        List<String> roleNames = new ArrayList<>();

        if (this.roles != null) {
            for (Role role : this.roles) {
                roleNames.add(role.getName());
            }
        }

        return roleNames;
    }

    /**
     * Returns the number of granted authorities for this user
     * @return number of assigned roles
     */
    @Transient
    @XmlTransient
    public Integer getPrivilegesCount() {
        return this.roles != null ? this.roles.size() : 0;
    }

    // endregion

    //region Init

    /**
     * Set the default values.
     */
    public void initDefaultValues() {
        this.nonLocked = Boolean.TRUE;
        this.lockCounter = 0;
    }

    //endregion

    //region Methods

    public void createLdapPassword() {
        this.password = Constants.RPB_LDAPPASSHASH;
    }

    public void createSecurePasswordHash() {
        this.password = DigestUtils.shaHex(password);
    }

    public Boolean oldPasswordMatch(String dbPassHash) {
        return this.oldPassword != null && DigestUtils.shaHex(this.oldPassword).equals(dbPassHash);
    }

    public Boolean passwordMatch() {
        return this.password != null && this.password.equals(this.passwordCopy);
    }

    public Boolean hasOpenClinicaAccount() {
        return this.ocUsername != null && !this.ocUsername.equals("");
    }

    //endregion

    //region Overrides

    /**
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof DefaultAccount && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Account instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("username", this.username)
                .add("password", this.password)
                .add("isEnabled", this.isEnabled)
                .add("apiKey", this.apiKey)
                .toString();
    }

    //endregion

}
