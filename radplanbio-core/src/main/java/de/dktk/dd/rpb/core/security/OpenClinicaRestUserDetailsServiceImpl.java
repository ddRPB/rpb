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


package de.dktk.dd.rpb.core.security;

import de.dktk.dd.rpb.core.context.UserWithId;

import org.apache.log4j.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Our OpenClinica REST implementation of Spring Security's UserDetailsService.
 *
 * @author tomas@skripcak.net
 * @since 30 Aug 2016
 */
@Named("openClinicaRestUserDetailsServiceImpl")
@Singleton
public class OpenClinicaRestUserDetailsServiceImpl implements UserDetailsService {

    // region Finals

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(OpenClinicaRestUserDetailsServiceImpl.class);

    //endregion

    //region Methods

    /**
     * Retrieve an account depending on its login this method is not case sensitive.<br>
     * use <code>obtainAccount</code> to match the login to either email, login or whatever is your login logic
     *
     * @param username the user's login
     * @return a Spring Security user details object that matches the login
     * @throws UsernameNotFoundException when the user could not be found
     * @throws DataAccessException when an error occurred while retrieving the account
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

        List<String> roleNames = new ArrayList<>();
        roleNames.add("ROLE_USER");
        roleNames.add("ROLE_EDC_USER");
        roleNames.add("ROLE_EDP_PARTICIPATE");

        // Return the Spring security user details which match with the DB account
        return new UserWithId(
                username,
                "", // password
                "", // clearPassword
                true, // isEnabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                this.toGrantedAuthorities(roleNames),
                "" // ID
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
