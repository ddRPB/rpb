/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2021 RPB Team
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

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *  RadPlanBio AuditLog meta model which is used for JPA
 *  This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 27 Jan 2015
 */
@SuppressWarnings("unused")
@StaticMetamodel(AuditLog.class)
public class AuditLog_ {

    // Raw attributes
    public static volatile SingularAttribute<AuditLog, Integer> id;
    public static volatile SingularAttribute<AuditLog, String> username;
    public static volatile SingularAttribute<AuditLog, String> event;
    public static volatile SingularAttribute<AuditLog, Date> eventDate;
    public static volatile SingularAttribute<AuditLog, String> stringAttribute1;
    public static volatile SingularAttribute<AuditLog, String> stringAttribute2;
    public static volatile SingularAttribute<AuditLog, String> stringAttribute3;

}