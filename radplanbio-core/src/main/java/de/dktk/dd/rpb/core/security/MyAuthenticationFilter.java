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

import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.repository.admin.IDefaultAccountRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static de.dktk.dd.rpb.core.util.Constants.*;

/**
 * Authentication processing filter for RadPlanBio
 * <p>
 * This is the place where it is decided which authentication token will be used depending on the authentication method
 * selected by user via web GUI
 *
 * @author tomas@skripcak.net
 * @since 05 Jun 2013
 */
public class MyAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    //region Injects

    private IDefaultAccountRepository IDefaultAccountRepository;

    @Inject
    public void setIDefaultAccountRepository(IDefaultAccountRepository IDefaultAccountRepository) {
        this.IDefaultAccountRepository = IDefaultAccountRepository;
    }

    //endregion

    //region Members

    private String usernameParameter = RPB_UNAMEPARAM;
    private String passwordParameter = RPB_PASSWDPARAM;

    //endregion

    //region Constructors

    public MyAuthenticationFilter() {
        super("/j_spring_security_check");
    }

    protected MyAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    //endregion

    //region Properties

    /**
     * Sets the parameter name which will be used to obtain the username from the login request.
     *
     * @param usernameParameter the parameter name. Defaults to "j_username".
     */
    @SuppressWarnings("unused")
    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    /**
     * Sets the parameter name which will be used to obtain the password from the login request..
     *
     * @param passwordParameter the parameter name. Defaults to "j_password".
     */
    @SuppressWarnings("unused")
    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    /**
     * Gets usernameParameter.
     *
     * @return usernameParameter String
     */
    @SuppressWarnings("unused")
    public final String getUsernameParameter() {
        return usernameParameter;
    }

    /**
     * Gets passwordParameter.
     *
     * @return passwordParameter String
     */
    @SuppressWarnings("unused")
    public final String getPasswordParameter() {
        return passwordParameter;
    }

    //endregion

    //region Methods

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws AuthenticationException {

        // Retrieve authentication data from the request
        String username = this.obtainUsername(httpServletRequest).toLowerCase();
        String password = this.obtainPassword(httpServletRequest);

        // Create UsernamePasswordAuthenticationToken according to user selection
        UsernamePasswordAuthenticationToken authRequest;

        // Load RPB account first
        DefaultAccount account = this.IDefaultAccountRepository.getByUsername(username);

        if (account != null) {
            // LDAP account
            if (account.isLdapUser()) {
                authRequest = new LdapAuthenticationToken(username, password);
            }
            // OC account
            else if (account.hasOpenClinicaAccount()) {
                authRequest = new OpenClinicaUsernamePasswordAuthenticationToken(username, password);
            }
            // Default account
            else {
                authRequest = new DefaultUsernamePasswordAuthenticationToken(username, password);
            }
        } else {
            authRequest = new DefaultUsernamePasswordAuthenticationToken(username, password);
        }

        // Allow subclasses to set the "details" property
        setDetails(httpServletRequest, authRequest);

        return super.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Enables subclasses to override the composition of the password, such as by including additional values
     * and a separator.<p>This might be used for example if a postcode/zipcode was required in addition to the
     * password. A delimiter such as a pipe (|) should be used to separate the password and extended value(s). The
     * <code>AuthenticationDao</code> will need to generate the expected password in a corresponding manner.</p>
     *
     * @param request so that request attributes can be retrieved
     * @return the password that will be presented in the <code>Authentication</code> request token to the
     * <code>AuthenticationManager</code>
     */
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(passwordParameter);
    }

    /**
     * Enables subclasses to override the composition of the username, such as by including additional values
     * and a separator.
     *
     * @param request so that request attributes can be retrieved
     * @return the username that will be presented in the <code>Authentication</code> request token to the
     * <code>AuthenticationManager</code>
     */
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(usernameParameter);
    }

    /**
     * Provided so that subclasses may configure what is put into the authentication request's details
     * property.
     *
     * @param request     that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details set
     */
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    //endregion

}