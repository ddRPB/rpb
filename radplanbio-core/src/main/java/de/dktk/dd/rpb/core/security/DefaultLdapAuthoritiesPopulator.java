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

package de.dktk.dd.rpb.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.admin.DefaultAccountRepository;
import org.apache.log4j.Logger;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import javax.inject.Inject;

import static com.google.common.collect.Lists.newArrayList;

/**
 * DefaultLdapAuthoritiesPopulator
 * Populate LDAP user authorities with assigned RPB user authorities (we don't use LDAP for authorisation)
 *
 * @author tomas@skripcak.net
 * @since 16 Nov 2015
 */
public class DefaultLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    //region Finals

    private static final Logger log = Logger.getLogger(DefaultLdapAuthoritiesPopulator.class);

    //endregion

    //region Injects

    @Inject
    private DefaultAccountRepository defaultAccountRepository;

    @SuppressWarnings("unused")
    public void setDefaultAccountRepository(DefaultAccountRepository repository) {
        this.defaultAccountRepository = repository;
    }

    //endregion

    public Collection<GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        DefaultAccount account = defaultAccountRepository.getByUsername(username);

        // The account could not be located
        if (account != null) {
            // From account roles get granted authorities collection
            grantedAuthorities = toGrantedAuthorities(account.getRoleNames());
        }
        else {
            log.info("LDAP user " + username + " could not be found in RPB database, no authorities loaded.");
        }

        return grantedAuthorities;
    }

    //region Private

    /**
     * Create collection of GrantedAuthorities from list of string roles
     * @param roles List of string roles
     * @return collection of GrantedAuthorities
     */
    private Collection<GrantedAuthority> toGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> result = newArrayList();

        for (String role : roles) {
            result.add(new SimpleGrantedAuthority(role));
        }

        return result;
    }

    //endregion

}