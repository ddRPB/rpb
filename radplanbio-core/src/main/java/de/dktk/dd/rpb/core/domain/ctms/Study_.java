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

import de.dktk.dd.rpb.core.domain.criteria.AbstractCriterion;
import de.dktk.dd.rpb.core.domain.edc.CrfFieldAnnotation;
import de.dktk.dd.rpb.core.domain.edc.mapping.Mapping;
import de.dktk.dd.rpb.core.domain.randomisation.AbstractRandomisationConfiguration;
import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Study meta model which is used for JPA
 * This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 16 Sep 2013
 */
@SuppressWarnings("unused")
@StaticMetamodel(Study.class)
public class Study_ {

    // Raw attributes
    public static volatile SingularAttribute<Study, Integer> id;
    public static volatile SingularAttribute<Study, String> name;
    public static volatile SingularAttribute<Study, String> title;
    public static volatile SingularAttribute<Study, String> description;
    public static volatile SingularAttribute<Study, Integer> expectedTotalEnrollment;
    public static volatile SingularAttribute<Study, String> comment;

    //TODO: should be moved to tags later
    public static volatile SingularAttribute<Study, String> ocStudyIdentifier;
    public static volatile SingularAttribute<Study, Boolean> isStratifyTrialSite;

    // One to one
    public static volatile SingularAttribute<Study, AbstractRandomisationConfiguration> randomisationConfiguration;
    public static volatile SingularAttribute<Study, AbstractProtocolType> protocolType;

    // Many to one
    public static volatile SingularAttribute<Study, PartnerSite> partnerSite;
    public static volatile SingularAttribute<Study, StudyPhase> phase;
    public static volatile SingularAttribute<Study, StudyStatus> status;
    public static volatile SingularAttribute<Study, SponsoringType> sponsoringType;

    // One to many
    public static volatile ListAttribute<Study, StudyDoc> studyDocs;
    public static volatile ListAttribute<Study, CrfFieldAnnotation> crfFieldAnnotations;
    public static volatile ListAttribute<Study, TreatmentArm> treatmentArms;
    public static volatile ListAttribute<Study, AbstractCriterion> subjectCriteria;
    public static volatile ListAttribute<Study, Mapping> dataMappings;
    public static volatile ListAttribute<Study, StudyPerson> studyPersonnel;
    public static volatile ListAttribute<Study, StudyOrganisation> studyOrganisations;
    public static volatile ListAttribute<Study, StudyTag> tags;
    public static volatile ListAttribute<Study, TimeLineEvent> timeLineEvents;

    // Many to many
    public static volatile ListAttribute<Study, PartnerSite> participatingSites;
    public static volatile ListAttribute<Study, TumourEntity> tumourEntities;

}