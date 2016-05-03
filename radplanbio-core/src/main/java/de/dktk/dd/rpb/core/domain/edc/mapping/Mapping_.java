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

package de.dktk.dd.rpb.core.domain.edc.mapping;

import de.dktk.dd.rpb.core.domain.ctms.Study;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *  Mapping entity meta-model which is used for JPA
 *  This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 03 Mar 2015
 */
@SuppressWarnings("unused")
@StaticMetamodel(Mapping.class)
public class Mapping_ {

    // Raw attributes
    public static volatile SingularAttribute<Mapping, Integer> id;
    public static volatile SingularAttribute<Mapping, String> name;
    public static volatile SingularAttribute<Mapping, String> description;
    public static volatile SingularAttribute<Mapping, String> type;
    public static volatile SingularAttribute<Mapping, String> sourceType;
    public static volatile SingularAttribute<Mapping, String> targetType;

    // Many to one
    public static volatile SingularAttribute<Mapping, Study> study;

    // One to many
    public static volatile ListAttribute<Mapping, MappingRecord> mappingRecords;
    public static volatile ListAttribute<Mapping, AbstractMappedItem> sourceItemDefinitions;
    public static volatile ListAttribute<Mapping, AbstractMappedItem> targetItemDefinitions;

}
