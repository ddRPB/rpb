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

package de.dktk.dd.rpb.core.domain.admin;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import org.joda.time.Instant;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * DefaultAccount meta model which is used for JPA
 * This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 10 Apr 2013
 */
@SuppressWarnings("unused")
@StaticMetamodel(DefaultAccount.class)
public abstract class DefaultAccount_
{
    // Raw attributes
    public static volatile SingularAttribute<DefaultAccount, Integer> id;
    public static volatile SingularAttribute<DefaultAccount, String> username;
    public static volatile SingularAttribute<DefaultAccount, String> password;
    public static volatile SingularAttribute<DefaultAccount, String> email;
    public static volatile SingularAttribute<DefaultAccount, Boolean> isEnabled;
    public static volatile SingularAttribute<DefaultAccount, String> ocUsername;
    public static volatile SingularAttribute<DefaultAccount, Instant> lastVisit;
    public static volatile SingularAttribute<DefaultAccount, Boolean> nonLocked;
    public static volatile SingularAttribute<DefaultAccount, Integer> lockCounter;
    public static volatile SingularAttribute<DefaultAccount, String> apiKey;
    public static volatile SingularAttribute<DefaultAccount, Boolean> apiKeyEnabled;

    // Many to one
    public static volatile SingularAttribute<DefaultAccount, PartnerSite> partnerSite;

    // Many to many
    public static volatile ListAttribute<DefaultAccount, Role> roles;

}