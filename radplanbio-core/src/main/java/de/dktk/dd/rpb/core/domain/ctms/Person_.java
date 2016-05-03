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

package de.dktk.dd.rpb.core.domain.ctms;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Study meta model which is used for JPA
 * This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 24 Jun 2015
 */
@SuppressWarnings("unused")
@StaticMetamodel(Person.class)
public class Person_ {

    // Raw attributes
    public static volatile SingularAttribute<Person, Integer> id;
    public static volatile SingularAttribute<Person, String> titlesBefore;
    public static volatile SingularAttribute<Person, String> firstname;
    public static volatile SingularAttribute<Person, String> surname;
    public static volatile SingularAttribute<Person, String> titlesAfter;
    public static volatile SingularAttribute<Person, String> birthname;
    public static volatile SingularAttribute<Person, String> comment;

    // Many to one
    public static volatile SingularAttribute<Person, PersonStatus> status;

    // One to many
    public static volatile ListAttribute<Person, StudyPerson> studyPersonnel;
    public static volatile ListAttribute<Person, CurriculumVitaeItem> curriculumVitaeItems;

}
