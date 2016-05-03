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

package de.dktk.dd.rpb.core.domain.randomisation;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * TrialSubject meta model which is used for JPA
 * This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 27 Jan 2014
 */
@SuppressWarnings("unused")
@StaticMetamodel(TrialSubject.class)
public class TrialSubject_ {

    // Raw attributes
    public static volatile SingularAttribute<TrialSubject, Integer> id;
    public static volatile SingularAttribute<TrialSubject, String> pid;
    public static volatile SingularAttribute<TrialSubject, String> ocStudySubjectId;

    // Many One
    public static volatile SingularAttribute<TrialSubject, PartnerSite> trialSite;
    public static volatile SingularAttribute<TrialSubject, TreatmentArm> treatmentArm;

    // One to Many
    public static volatile ListAttribute<TrialSubject, PrognosticVariable> prognosticVariables;

}
