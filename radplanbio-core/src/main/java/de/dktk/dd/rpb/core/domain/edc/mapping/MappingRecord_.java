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

package de.dktk.dd.rpb.core.domain.edc.mapping;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *  MappingRecord entity meta model which is used for JPA
 *  This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 13 Mar 2015
 */
@SuppressWarnings("unused")
@StaticMetamodel(MappingRecord.class)
public class MappingRecord_ {

    // Raw attributes
    public static volatile SingularAttribute<MappingRecord, Integer> id;
    public static volatile SingularAttribute<MappingRecord, String> defaultValue;
    public static volatile SingularAttribute<MappingRecord, String> multiValueSeparator;
    public static volatile SingularAttribute<MappingRecord, String> dateFormatString;
    public static volatile SingularAttribute<MappingRecord, String> calculationString;
    public static volatile SingularAttribute<MappingRecord, Integer> priority;
    public static volatile SingularAttribute<MappingRecord, String> studyEventRepeatKey;
    public static volatile SingularAttribute<MappingRecord, String> itemGroupRepeatKey;

    // One to one
    public static volatile SingularAttribute<MappingRecord, AbstractMappedItem> source;
    public static volatile SingularAttribute<MappingRecord, AbstractMappedItem> target;

    // Collection
    public static volatile MapAttribute<MappingRecord, String, String> mapping;

}
