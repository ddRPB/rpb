/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

package de.dktk.dd.rpb.core.domain.edc;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * CrfFieldAnnotation meta model which is used for JPA
 * This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 12 Sep 2013
 */
@SuppressWarnings("unused")
@StaticMetamodel(CrfFieldAnnotation.class)
public class CrfFieldAnnotation_ {

    // Raw attributes
    public static volatile SingularAttribute<CrfFieldAnnotation, Integer> id;
    public static volatile SingularAttribute<CrfFieldAnnotation, String> eventDefinitionOid;
    public static volatile SingularAttribute<CrfFieldAnnotation, String> formOid;
    public static volatile SingularAttribute<CrfFieldAnnotation, String> groupOid;
    public static volatile SingularAttribute<CrfFieldAnnotation, String> crfItemOid;

    // Many to one
    public static volatile SingularAttribute<CrfFieldAnnotation, AnnotationType> annotationType;
    public static volatile SingularAttribute<CrfFieldAnnotation, Study> study;

}