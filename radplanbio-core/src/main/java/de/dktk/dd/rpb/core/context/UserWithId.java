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

package de.dktk.dd.rpb.core.context;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Simple User that also keep track of the primary key.
 * The clear password is also saved her (for the sake of automatic post login to OpenClinica)
 * The primary key is used by hibernate filter.
 */
public class UserWithId extends User
{
    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Members

    private Serializable id;
    private String email;
    private String clearPassword;

    //endregion

    //region Constructors

    public UserWithId(String username,
                      String password,
                      String clearPassword,
                      String email,
                      boolean enabled,
                      boolean accountNonExpired,
                      boolean credentialsNonExpired,
                      boolean accountNonLocked,
                      Collection<? extends GrantedAuthority> authorities,
                      Serializable id) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.clearPassword = clearPassword;
        this.email = email;
    }

    //endregion

    //region Properties

    public Serializable getId() {
        return this.id;
    }

    public String getClearPassword() {
        return this.clearPassword;
    }

    public void setClearPassword(String value) {
        this.clearPassword = value;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    //endregion

}