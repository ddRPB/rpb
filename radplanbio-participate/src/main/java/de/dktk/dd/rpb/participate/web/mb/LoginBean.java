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

package de.dktk.dd.rpb.participate.web.mb;

import de.dktk.dd.rpb.core.security.OpenClinicaUsernamePasswordAuthenticationToken;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.*;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Bean for participate login view
 *
 * @author tomas@skripcak.net
 * @since 28 Sept 2015
 */
@Named("mbLogin")
@Scope(value="view")
public class LoginBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Constructors

    @Inject
    public LoginBean(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    //endregion

    //region Members

    private AuthenticationManager authenticationManager;

    private String username;
    private String password;
    private String loginError;

    //endregion

    //region Properties

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public String getLoginError() {
        return this.loginError;
    }

    public void setLoginError(String value) {
        this.loginError = value;
    }

    //endregion

    //region Commands

    public String login() {
        String result = null;

        try {
            Authentication authentication;
            if (this.username != null && !this.username.equals("")) {

                this.username = this.username.toLowerCase();
                authentication = this.authenticationManager
                    .authenticate(
                            new OpenClinicaUsernamePasswordAuthenticationToken(
                                    this.username,
                                    this.password
                            )
                    );
            }
            else {
                throw new Exception("Attempt to login with emtpy user name, please provide your user name when you want to login.");
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            result = "/home.faces?faces-redirect=true";
        }
        catch (Exception err) {
            this.loginError = err.getMessage();
        }

        return result;
    }

    //endregion

}