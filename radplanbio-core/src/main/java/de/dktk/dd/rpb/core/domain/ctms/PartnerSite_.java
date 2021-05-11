/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

package de.dktk.dd.rpb.core.domain.ctms;

import de.dktk.dd.rpb.core.domain.admin.*;
import de.dktk.dd.rpb.core.domain.bio.Bio;
import de.dktk.dd.rpb.core.domain.edc.Edc;
import de.dktk.dd.rpb.core.domain.edc.FormEngine;
import de.dktk.dd.rpb.core.domain.lab.Lab;
import de.dktk.dd.rpb.core.domain.pacs.Pacs;
import de.dktk.dd.rpb.core.domain.pid.Pid;
import de.dktk.dd.rpb.core.domain.rpb.Portal;
import de.dktk.dd.rpb.core.domain.rpb.Server;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * RadPlanBio Partner Site meta model which is used for JPA
 * This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 08 August 2013
 */
@SuppressWarnings("unused")
@StaticMetamodel(PartnerSite.class)
public abstract class PartnerSite_ {

    // Raw attributes
    public static volatile SingularAttribute<PartnerSite, Integer> id;
    public static volatile SingularAttribute<PartnerSite, String> identifier;
    public static volatile SingularAttribute<PartnerSite, String> name;
    public static volatile SingularAttribute<PartnerSite, String> description;
    public static volatile SingularAttribute<PartnerSite, Float> latitude;
    public static volatile SingularAttribute<PartnerSite, Float> longitude;
    public static volatile SingularAttribute<PartnerSite, String> ipRange;
    public static volatile SingularAttribute<PartnerSite, Boolean> isEnabled;

    // Technical attribute for query
    public static volatile SingularAttribute<PartnerSite, Integer> pacsId;
    public static volatile SingularAttribute<PartnerSite, Integer> edcId;
    public static volatile SingularAttribute<PartnerSite, Integer> portalId;
    public static volatile SingularAttribute<PartnerSite, Integer> pidId;
    public static volatile SingularAttribute<PartnerSite, Integer> serverId;
    public static volatile SingularAttribute<PartnerSite, Integer> bioId;

    // One to one
    public static volatile SingularAttribute<PartnerSite, Pacs> pacs;
    public static volatile SingularAttribute<PartnerSite, Edc> edc;
    public static volatile SingularAttribute<PartnerSite, FormEngine> formEngine;
    public static volatile SingularAttribute<PartnerSite, Portal> portal;
    public static volatile SingularAttribute<PartnerSite, Pid> pid;
    public static volatile SingularAttribute<PartnerSite, Server> server;
    public static volatile SingularAttribute<PartnerSite, Bio> bio;
    public static volatile SingularAttribute<PartnerSite, Lab> lab;

    // One to many
    public static volatile ListAttribute<PartnerSite, DefaultAccount> defaultAccounts;

    // Many to many
    public static volatile ListAttribute<PartnerSite, Study> studies;

}