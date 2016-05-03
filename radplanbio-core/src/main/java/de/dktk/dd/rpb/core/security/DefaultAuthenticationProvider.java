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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 *
 * Default authentication provider is using the RadPlanBio database
 *
 * @author tomas@skripcak.net
 * @since 07 May 2013
 */
public class DefaultAuthenticationProvider implements AuthenticationProvider {

    //region Finals

    private static final Logger log = Logger.getLogger(DefaultUserDetailsServiceImpl.class);

    //endregion

    //region Members

    private UserDetailsService userDetailsService;

    //endregion

    //region Properties

    @SuppressWarnings("unused")
    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    //endregion

    //region Methods

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        String username = String.valueOf(auth.getPrincipal());
        String password = String.valueOf(auth.getCredentials());
        String passwordHash = DigestUtils.shaHex(password);

        // Log the default authentication request
        log.info("username:" + username);

        // Use the username to load the data for the user, including authorities and password.
        UserDetails user = userDetailsService.loadUserByUsername(username);


        // Check if account is enabled
        if (!user.isEnabled()) {
            throw new DisabledException("This user account is disabled");
        }
        // Check the passwords match.
        if (!user.getPassword().equals(passwordHash)) {
            throw new BadCredentialsException("Bad Credentials");
        }

        // Return an authenticated token, containing user data and authorities
        return new DefaultUsernamePasswordAuthenticationToken(
                user, // principal
                null, // credentials
                user.getAuthorities() // authorities
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (DefaultUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    //endregion

}
