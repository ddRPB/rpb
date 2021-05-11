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

package de.dktk.dd.rpb.core.domain.rpb;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Software meta model which is used for JPA
 * This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 26 Nov 2014
 */
@SuppressWarnings("unused")
@StaticMetamodel(Software.class)
public abstract class Software_ {

    // Raw attributes
    public static volatile SingularAttribute<Software, Integer> id;
    public static volatile SingularAttribute<Software, String> name;
    public static volatile SingularAttribute<Software, String> description;
    public static volatile SingularAttribute<Software, String> version;
    public static volatile SingularAttribute<Software, String> filename;
    public static volatile SingularAttribute<Software, String> platform;
    public static volatile SingularAttribute<Software, Boolean> latest;

    // Many-to-One
    public static volatile SingularAttribute<Software, Portal> portal;

}
