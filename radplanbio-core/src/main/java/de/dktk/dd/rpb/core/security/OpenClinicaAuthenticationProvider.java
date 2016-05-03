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

import de.dktk.dd.rpb.core.context.UserWithId;
import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.admin.DefaultAccountRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import de.dktk.dd.rpb.core.service.OpenClinicaService;

import de.dktk.dd.rpb.core.service.AuditLogService;

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
 * OpenClinicaAuthenticationProvider
 *
 * @author tomas@skripcak.net
 * @since 27 January 2014
 */
public class OpenClinicaAuthenticationProvider implements AuthenticationProvider {

    //region Finals

    private static final Logger log = Logger.getLogger(OpenClinicaUserDetailsServiceImpl.class);

    //endregion

    //region Members

    private UserDetailsService userDetailsService;
    private DefaultAccountRepository defaultAccountRepository;
    private AuditLogService auditLogService;

    //endregion

    //region Constructors

    @Inject
    public OpenClinicaAuthenticationProvider(DefaultAccountRepository defaultAccountRepository, AuditLogService auditLogService) {
        this.defaultAccountRepository = defaultAccountRepository;
        this.auditLogService = auditLogService;
    }

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

    //region Overrides

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        String username = String.valueOf(auth.getPrincipal());
        String password = String.valueOf(auth.getCredentials());

        String passwordHash = DigestUtils.shaHex(password);

        // Log the default authentication request
        log.info("OC username: " + username);

        boolean ocSoapAuthenticationSucesfull = false;

        // 1. Use the username to load the data for the user, including authorities and password.
        UserDetails user = userDetailsService.loadUserByUsername(username);
        DefaultAccount defaultAccount = defaultAccountRepository.getByOcUsername(username);
        user =  new UserWithId (
                username,
                passwordHash,
                password,
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                user.getAuthorities(),
                defaultAccount.getId()
        );

        IOpenClinicaService svcOpenClinica = new OpenClinicaService();
        if (defaultAccount.hasOpenClinicaAccount() && defaultAccount.getPartnerSite().getEdc().getIsEnabled()) {
            svcOpenClinica.connectWithHash(
                    user.getUsername(),
                    passwordHash,
                    defaultAccount.getPartnerSite().getEdc().getSoapBaseUrl(),
                    defaultAccount.getPartnerSite().getEdc().getEdcBaseUrl()
            );

            if (svcOpenClinica.listAllStudies() != null) {
                ocSoapAuthenticationSucesfull = true;
            }
            else {
                if (log.isInfoEnabled()) {
                    log.info("EDC user " + username + " SOAP authentication failed.");
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

        // 2. Check whether user is enabled (it is enabled when the authentication via SOAP was sucessfull)
        if (user.isEnabled() && ocSoapAuthenticationSucesfull) {
            this.auditLogService.event(AuditEvent.LoginSuccessful, username);
        }
        else {
            this.auditLogService.event(AuditEvent.LoginFailed, username);
            throw new BadCredentialsException("Bad Credentials");
        }

        return new OpenClinicaUsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OpenClinicaUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    //endregion

}
