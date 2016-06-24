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

package de.dktk.dd.rpb.portal.web.mb.admin;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.inject.Inject;
import javax.inject.Named;

import javax.annotation.PostConstruct;

import javax.faces.context.FacesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.admin.Role;
import de.dktk.dd.rpb.core.repository.admin.DefaultAccountRepository;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;

import org.primefaces.component.api.UIColumn;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;

/**
 * Bean for administration of the RPB DefaultAccount entities and assigning user roles
 *
 * @author tomas@skripcak.net
 * @since 21 August 2013
 */
@Named("mbUserAccount")
@Scope("view")
public class UserAccountBean extends CrudEntityViewModel<DefaultAccount, Integer> {

    //region Injects

    //region Repository

    @Inject
    DefaultAccountRepository repository;

    /**
     * Get DefaultAccountRepository
     * @return DefaultAccountRepository
     */
    @SuppressWarnings("unused")
    @Override
    public DefaultAccountRepository getRepository() {
        return this.repository;
    }

    /**
     * Set UserAccountRepository
     *
     * @param value - UserAccountRepository
     */
    @SuppressWarnings("unused")
    public void setUserAccountRepository(DefaultAccountRepository value) {
        this.repository = value;
    }

    //endregion

    //endregion

    //region Members

    private Boolean isLdapUserSource;
    private List<Role> assignedRoles;

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

    //region Init

    @PostConstruct
    public void init() {
        this.isLdapUserSource = Boolean.FALSE;

        this.setColumnVisibilityList(
                this.buildColumnVisibilityList()
        );
        this.setPreSortOrder(
                this.buildSortOrder()
        );
        this.load();
    }

    //endregion

    //region Commands

    //region DefaultAccount

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

    public void doGenerateApiKey(DefaultAccount account) {
        account.setApiKey(
                this.getRandom32ChApiKey()
        );
    }

    //endregion

    //region DefaultAccount Roles

    public void prepareNewRoles() {
        this.assignedRoles = new ArrayList<Role>();
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

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        this.newEntity = this.repository.getNew();
        this.newEntity.setApiKey(
                this.getRandom32ChApiKey()
        );
    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = new ArrayList<SortMeta>();

        UIViewRoot viewRoot =  FacesContext.getCurrentInstance().getViewRoot();
        UIComponent column = viewRoot.findComponent(":form:tabView:dtEntities:colUserName");

        SortMeta sm1 = new SortMeta();
        sm1.setSortBy((UIColumn) column);
        sm1.setSortField("colUserName");
        sm1.setSortOrder(SortOrder.ASCENDING);

        results.add(sm1);

        return results;
    }

    /**
     * Create column visibility list
     * @return List of Boolean values determining column visibility
     */
    protected List<Boolean> buildColumnVisibilityList() {
        List<Boolean> result = new ArrayList<Boolean>();
        result.add(Boolean.TRUE); // Username
        result.add(Boolean.TRUE); // Email
        result.add(Boolean.TRUE); // PartnerSite
        result.add(Boolean.TRUE); // IsEnabled
        result.add(Boolean.FALSE); // LDAP
        result.add(Boolean.FALSE); // API-key enabled
        result.add(Boolean.TRUE); // PrivilegesCount

        return result;
    }

    //endregion

    //region Private Methods

    private String getRandom32ChApiKey() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    //endregion

}