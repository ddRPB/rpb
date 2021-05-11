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

package de.dktk.dd.rpb.core.security;

import de.dktk.dd.rpb.core.context.UserWithId;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.edc.UserAccount;
import de.dktk.dd.rpb.core.repository.admin.IDefaultAccountRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import de.dktk.dd.rpb.core.service.OpenClinicaService;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.inject.Inject;

/**
 * OpenClinicaRestApiAuthenticationProvider
 *
 * Pure authentication against OpenClinica using new REST API
 *
 * @author tomas@skripcak.net
 * @since 30 Aug 2016
 */
public class OpenClinicaRestApiAuthenticationProvider implements AuthenticationProvider {

    //region Finals

    private static final Logger log = Logger.getLogger(OpenClinicaRestApiAuthenticationProvider.class);

    //endregion

    //region Members

    private UserDetailsService userDetailsService;
    private IDefaultAccountRepository IDefaultAccountRepository;
    private AuditLogService auditLogService;

    //endregion

    //region Constructors

    @Inject
    public OpenClinicaRestApiAuthenticationProvider(IDefaultAccountRepository IDefaultAccountRepository, AuditLogService auditLogService) {
        this.IDefaultAccountRepository = IDefaultAccountRepository;
        this.auditLogService = auditLogService;
    }

    //endregion

    //region Properties

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    //endregion

    //region Overrides

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        String username = String.valueOf(auth.getPrincipal());
        String password = String.valueOf(auth.getCredentials());

        String passwordHash = DigestUtils.shaHex(password);

        // Log the default authentication request
        log.info("OC username: " + username);

        // 1. Use the username to load the data for the user, including authorities and password.
        UserDetails user = userDetailsService.loadUserByUsername(username);
        user =  new UserWithId(
                username,
                passwordHash,
                password,
                "",
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                user.getAuthorities(),
                "" // id
        );

        // OC authentication part
        boolean ocAuthenticationSuccessful = false;

        DefaultAccount defaultAccount = IDefaultAccountRepository.getByOcUsername(username);
        if (defaultAccount.hasOpenClinicaAccount() && defaultAccount.getPartnerSite().getEdc().getIsEnabled()) {

            // Authenticate over REST web services
            UserAccount account;
            IOpenClinicaService svcOpenClinica = new OpenClinicaService();

            // Init
            svcOpenClinica.connect(
                    username,
                    password,
                    defaultAccount.getPartnerSite().getEdc().getSoapBaseUrl(),
                    defaultAccount.getPartnerSite().getEdc().getEdcBaseUrl()
            );

            // Load
            account = svcOpenClinica.loginUserAccount(username, password);

            if (account != null) {
                ocAuthenticationSuccessful = true;
            }
            else {
                if (log.isInfoEnabled()) {
                    log.info("EDC user " + username + " REST authentication failed.");
                }
            }
        }
        else {
            if (log.isInfoEnabled()) {
                log.info("User " + username + " could not be found.");
            }

            throw new UsernameNotFoundException("User " + username + " could not be found.");
        }

        // Save clear password so I can use it to communicate with OpenClinica via REST service
        ((UserWithId)user).setClearPassword(password);
        ((UserWithId)user).setEmail(defaultAccount.getEmail());

        // 2. Check whether user is enabled (it is enabled when the authentication via REST was successful)
        if (user.isEnabled() && ocAuthenticationSuccessful) {
            this.auditLogService.event(AuditEvent.LoginSuccessful, username);
        }
        else {
            this.auditLogService.event(AuditEvent.LoginFailed, username);
            throw new BadCredentialsException("Bad Credentials");
        }

        return new OpenClinicaUsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OpenClinicaUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    //endregion

}