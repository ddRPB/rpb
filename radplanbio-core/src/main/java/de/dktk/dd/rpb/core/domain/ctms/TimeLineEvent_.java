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

package de.dktk.dd.rpb.core.domain.ctms;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

/**
 * TimeLineEvent meta model which is used for JPA
 * This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 16 Sep 2013
 */
@SuppressWarnings("unused")
@StaticMetamodel(TimeLineEvent.class)
public class TimeLineEvent_ {

    // Raw attributes
    public static volatile SingularAttribute<TimeLineEvent, Integer> id;
    public static volatile SingularAttribute<TimeLineEvent, String> name;
    public static volatile SingularAttribute<TimeLineEvent, String> description;
    public static volatile SingularAttribute<TimeLineEvent, Date> startDate;
    public static volatile SingularAttribute<TimeLineEvent, Date> endDate;

    // Many to one
    public static volatile SingularAttribute<TimeLineEvent, EventType> type;
    public static volatile SingularAttribute<TimeLineEvent, Study> study;
    public static volatile SingularAttribute<TimeLineEvent, Organisation> organisation;

}