/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

package de.dktk.dd.rpb.portal.web.mb.admin;

import de.dktk.dd.rpb.core.dao.support.OrderByDirection;
import de.dktk.dd.rpb.core.dao.support.SearchParameters;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount_;
import de.dktk.dd.rpb.core.domain.admin.Role;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.repository.admin.IDefaultAccountRepository;
import de.dktk.dd.rpb.core.repository.admin.IPartnerSiteRepository;
import de.dktk.dd.rpb.core.util.UuidUtil;
import de.dktk.dd.rpb.portal.web.mb.support.GenericLazyDataModel;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bean for administration of RPB DefaultAccounts
 *
 * @author tomas@skripcak.net
 * @since 01 Apr 2016
 */
@Named("mbUserAccountLazy")
@Scope(value="view")
public class UserAccountLazyBean extends GenericLazyDataModel<DefaultAccount, Integer> {

    //region Members

    private Boolean isLdapUserSource;
    private List<Role> assignedRoles;

    private IPartnerSiteRepository IPartnerSiteRepository;

    //endregion

    //region Properties

    //region Is LDAP UserSource

    public Boolean getIsLdapUserSource() {
        return this.isLdapUserSource;
    }

    public void setIsLdapUserSource(Boolean value) {
        this.isLdapUserSource = value;
    }

    //endregion

    //region AssignedRoles

    public List<Role> getAssignedRoles() {
        return this.assignedRoles;
    }

    public void setAssignedRoles(List<Role> roles) {
        this.assignedRoles = roles;
    }

    //endregion

    //endregion

    //region Constructors

    @Inject
    public UserAccountLazyBean(IDefaultAccountRepository repository, IPartnerSiteRepository IPartnerSiteRepository) {
        super(repository);
        this.IPartnerSiteRepository = IPartnerSiteRepository;
    }

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.columnVisibilityList = this.buildColumnVisibilityList();
        this.isLdapUserSource = Boolean.FALSE;
    }

    //endregion

    //region Commands

    public void doGenerateApiKey(DefaultAccount account) {
        account.setApiKey(
                UuidUtil.getRandom32ChApiKey()
        );
    }

    //region DefaultAccount Roles

    public void prepareNewRoles() {
        this.assignedRoles = new ArrayList<>();
    }

    public void doAddRoles(List<Role> roles) {
        this.selectedEntity.addRoles(roles);
        this.doUpdateEntity();
    }

    public void doRemoveRole(Role selectedAccountRole) {
        this.selectedEntity.removeRole(selectedAccountRole);
        this.doUpdateEntity();
    }

    //endregion

    //endregion

    //region Overrides

    @Override
    public void doCreateEntity() {
        Boolean isValid = Boolean.TRUE;

        if (!this.isLdapUserSource) {
            try {
                if (!this.newEntity.passwordMatch()) {
                    isValid = Boolean.FALSE;
                    throw new Exception("Passwords do not match!");
                }
            } catch (Exception err) {
                FacesContext.getCurrentInstance().validationFailed();
                this.messageUtil.errorText(err.getMessage());
            }
        }

        if (isValid) {
            if (!this.isLdapUserSource) {
                this.newEntity.createSecurePasswordHash();
            }
            else {
                this.newEntity.createLdapPassword();
            }
            super.doCreateEntity();
        }
    }

    public void doUpdatePassword(DefaultAccount account) {
        this.selectedEntity = account;

        Boolean isValid = Boolean.TRUE;

        try {
            if (!this.selectedEntity.oldPasswordMatch(this.repository.get(account).getPassword())) {
                isValid = Boolean.FALSE;
                throw new Exception ("Old password does not match!");
            }
            if (!this.selectedEntity.passwordMatch()) {
                isValid = Boolean.FALSE;
                throw new Exception("Passwords do not match!");
            }
        }
        catch (Exception err) {
            FacesContext.getCurrentInstance().validationFailed();
            this.messageUtil.errorText(err.getMessage());
        }

        if (isValid) {
            this.selectedEntity.createSecurePasswordHash();
            super.doUpdateEntity();
        }
    }

    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        // Initial visibility of columns
        results.add(Boolean.TRUE); // Username
        results.add(Boolean.FALSE); // Email
        results.add(Boolean.TRUE); // PartnerSite
        results.add(Boolean.TRUE); // IsEnabled
        results.add(Boolean.FALSE); // NonLocked
        results.add(Boolean.FALSE); // IsLDAP
        results.add(Boolean.TRUE); // ApiKeyEnabled
        results.add(Boolean.TRUE); // PrivilegesCount

        return results;
    }

    @Override
    protected DefaultAccount getEntity(Map<String, Object> filters) {
        DefaultAccount example = this.repository.getNew();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            switch (entry.getKey()) {
                case "username":
                    example.setUsername(entry.getValue().toString());
                    break;
                case "email":
                    example.setEmail(entry.getValue().toString());
                    break;
                case "partnerSite":
                    example.setPartnerSite(this.IPartnerSiteRepository.getNew());
                    example.getPartnerSite().setName(((PartnerSite) entry.getValue()).getName());
                    break;
                case "isEnabled":
                    example.setIsEnabled((Boolean) entry.getValue());
                    break;
                case "nonLocked":
                    example.setNonLocked((Boolean) entry.getValue());
                    break;
                case "isLdapUser()":
                    if ((Boolean) entry.getValue()) {
                        example.setPassword("LDAP");
                    }
                    break;
                case "apiKeyEnabled":
                    example.setApiKeyEnabled((Boolean) entry.getValue());
                    break;
            }
        }

        return example;
    }

    @Override
    protected SearchParameters searchParameters() {
        return new SearchParameters() //
                .anywhere() //
                .caseInsensitive();
    }

    @Override
    protected SearchParameters defaultOrder(SearchParameters searchParameters) {
        return searchParameters.orderBy(DefaultAccount_.username, OrderByDirection.ASC);
    }

    //endregion

}
