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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import de.dktk.dd.rpb.core.context.UserWithId;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.admin.DefaultAccountRepository;

/**
 * Our OpenClinica implementation of Spring Security's UserDetailsService.
 *
 * @author tomas@skripcak.net
 * @since 27 January 2014
 */
@Named("openClinicaUserDetailsServiceImpl")
@Singleton
public class OpenClinicaUserDetailsServiceImpl implements UserDetailsService {

    // region Finals

    private static final Logger log = Logger.getLogger(OpenClinicaUserDetailsServiceImpl.class);

    //endregion

    //region Members

    private DefaultAccountRepository defaultAccountRepository;

    //endregion

    //region Constructors

    @Inject
    public OpenClinicaUserDetailsServiceImpl(DefaultAccountRepository defaultAccountRepository) {
        this.defaultAccountRepository = defaultAccountRepository;
    }

    //endregion

    //region Methods

    /**
     * Retrieve an account depending on its login this method is not case sensitive.<br>
     * use <code>obtainAccount</code> to match the login to either email, login or whatever is your login logic
     *
     * @param ocUserName the user's login
     * @return a Spring Security user details object that matches the login
     * @throws UsernameNotFoundException when the user could not be found
     * @throws DataAccessException when an error occurred while retrieving the account
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String ocUserName) throws UsernameNotFoundException, DataAccessException {

        // Check the user name validity
        if (ocUserName == null || ocUserName.trim().isEmpty()) {
            throw new UsernameNotFoundException("Empty username.");
        }

        if (log.isDebugEnabled()) {
            log.debug("Security verification for EDC user " + ocUserName + ".");
        }

        // Get the default account according to OC user name from local DB
        DefaultAccount defaultAccount = defaultAccountRepository.getByOcUsername(ocUserName);

        // The account could not be located
        if (defaultAccount == null) {
            if (log.isInfoEnabled()) {
                log.info("User " + ocUserName + " could not be found.");
            }

            throw new UsernameNotFoundException("OC user " + ocUserName + " could not be found.");
        }

        // Return the Spring security user details which match with the DB account
        return new UserWithId (
                    ocUserName,
                    "", // password
                    "", // clearPassword
                    defaultAccount.getIsEnabled(),
                    true, // accountNonExpired
                    true, // credentialsNonExpired
                    true, // accountNonLocked
                    this.toGrantedAuthorities(defaultAccount.getRoleNames()),
                    defaultAccount.getId()
                );
    }

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