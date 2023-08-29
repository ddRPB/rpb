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
import de.dktk.dd.rpb.core.domain.Identifiable;
import de.dktk.dd.rpb.core.domain.IdentifiableHashBuilder;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * AbstractComponent domain entity
 * It is used as a ancestor for specific RPB linked web components (third party systems)
 *
 * @author tomas@skripcak.net
 * @since 15 Sep 2017
 */
//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name="DATASCOPE")
//@Table(name = "COMPONENT")
public abstract class AbstractComponent  implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(AbstractComponent.class);

    //endregion

    //region Members

    protected Integer id; // pk, auto increment

    protected String host; // IP address
    protected Integer port; // port

    protected String baseUrl; // not null
    protected String publicUrl;
    protected String dataPath;

    protected Boolean serviceAccountEnabled;
    protected String username;
    protected String password;

    protected Boolean apiKeyEnabled;
    protected String apiKey;

    protected Boolean isEnabled; // default true
    protected Boolean isValidated; // default false

    protected String version;
    protected String apiVersion;

    // Many to many
    protected List<PartnerSite> partnerSites;

    protected IdentifiableHashBuilder identifiableHashBuilder = new IdentifiableHashBuilder(); // Object hash

    //endregion

    //region Properties

    //region ID

    //@Id
    //@Column(name = "ID")
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "component_id_seq")
    //@SequenceGenerator(name = "component_id_seq", sequenceName = "component_id_seq")
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return id != null;
    }

    //endregion

    //region Host

    //@Size(max = 255)
    //@Column(name = "HOST")
    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    //endregion

    //region Port

    //@Column(name = "PORT")
    public Integer getPort() {
          return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    //endregion

    //region BaseUrl

    //@Size(max = 255)
    //@NotEmpty
    //@Column(name = "BASEURL", nullable = false)
    public String getBaseUrl()  {
        return this.baseUrl;
    }

    public void setBaseUrl(String value) {
        this.baseUrl = value;
    }

    //endregion

    //region PublicUrl

    //@Size(max = 255)
    //@Column(name = "PUBLICURL")
    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    //endregion

    //region DataPath

    //@Size(max = 255)
    //@Column(name = "DATAPATH")
    public String getDataPath() {
        return this.dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    //endregion

    //region ServiceAccountEnabled

    //@Column(name = "SERVICEACCOUNTENABLED")
    public Boolean getServiceAccountEnabled() {
        return this.serviceAccountEnabled;
    }

    public void setServiceAccountEnabled(Boolean hasServiceAccount) {
        this.serviceAccountEnabled = hasServiceAccount;
    }

    //endregion

    //region Username

    //@Size(max = 255)
    //@Column(name = "USERNAME")
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    //endregion

    //region Password

    //@Size(max = 255)
    //@Column(name = "PASSWORD")
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //endregion

    //region ApiKeyEnabled

    //@Column(name = "APIKEYENABLED")
    public Boolean getApiKeyEnabled() {
        return this.apiKeyEnabled;
    }

    public void setApiKeyEnabled(Boolean apiKeyEnabled) {
        this.apiKeyEnabled = apiKeyEnabled;
    }

    //endregion

    //region ApiKey

    //@Size(max = 255)
    //@Column(name = "APIKEY")
    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    //endregion

    //region IsEnabled

    //@Column(name = "ISENABLED")
    public Boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(Boolean value) {
        this.isEnabled = value;
    }

    //endregion

    //region IsValidated

    //@Column(name = "ISVALIDATED")
    public Boolean getIsValidated() {
        return this.isValidated;
    }

    public void setIsValidated(Boolean value) {
        this.isValidated = value;
    }

    //endregion

    //region Version

    //@Size(max = 15)
    //@Column(name = "VERSION", length = 15)
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String value) {
        this.version = value;
    }

    //endregion

    //region ApiVersion

    //@Size(max = 15)
    //@Column(name = "APIVERSION", length = 15)
    public String getApiVersion() {
        return this.apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    //endregion

    //region Many-to-Many

    //region PartnerSites

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany(mappedBy = "components")
    public List<PartnerSite> getPartnerSites() {
        return this.partnerSites;
    }

    public void setPartnerSites(List<PartnerSite> list) {
        this.partnerSites = list;
    }

    //endregion

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
     * Equals implementation using a business key.
     */
    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof AbstractComponent && hashCode() == other.hashCode());
    }

    @Override
    public int hashCode() {
        return identifiableHashBuilder.hash(log, this);
    }

    /**
     * Construct a readable string representation for this Study instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("baseUrl", this.baseUrl)
                .add("isEnabled", this.isEnabled)
                .add("isValidated", this.isValidated)
                .add("version", this.version)
                .toString();
    }

    //endregion

}
