/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static de.dktk.dd.rpb.core.util.Constants.RPB_PASSWDPARAM;
import static de.dktk.dd.rpb.core.util.Constants.RPB_UNAMEPARAM;

/**
 * Authentication processing filter for RadPlanBio
 * <p>
 * This is the place where it is decided which authentication token will be used depending on the authentication method
 * selected by user via web GUI. It is similar to the UsernamePasswordAuthenticationFilter class from Spring,
 * but this class points hard coded to /login - but we need /login.faces instead. That's why this class extends the
 * AbstractAuthenticationProcessingFilter directly. Because of that, the filter needs to be placed before the
 * "BASIC_AUTH_Filter" instead of having the position of such a filter.
 *
 * @author tomas@skripcak.net
 * @since 05 Jun 2013
 */
public class MyAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    //region Injects

    private IDefaultAccountRepository defaultAccountRepository;

    @Inject
    public void setIDefaultAccountRepository(IDefaultAccountRepository IDefaultAccountRepository) {
        this.defaultAccountRepository = IDefaultAccountRepository;
    }

    //endregion

    //region Members

    private String usernameParameter = RPB_UNAMEPARAM;
    private String passwordParameter = RPB_PASSWDPARAM;

    //endregion

    //region Constructors

    public MyAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    protected MyAuthenticationFilter(String urlPattern) {
        super(new AntPathRequestMatcher(urlPattern, "POST"));
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

        username = replaceNullWithEmptyString(username);
        password = replaceNullWithEmptyString(password);
        username = username.trim();

        // Create UsernamePasswordAuthenticationToken according to user selection
        UsernamePasswordAuthenticationToken authRequest;

        // Load RPB account first
        DefaultAccount account;
        if (!username.isEmpty()) {
            account = this.defaultAccountRepository.getByUsername(username);
        } else {
            account = null;
        }

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

    private String replaceNullWithEmptyString(String value) {
        if (value == null) {
            value = "";
        }
        return value;
    }

    //endregion

}