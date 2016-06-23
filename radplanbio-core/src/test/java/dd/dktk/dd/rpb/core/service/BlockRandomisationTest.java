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

package de.dktk.dd.rpb.core.service;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.criteria.DichotomousCriterion;
import de.dktk.dd.rpb.core.domain.criteria.constraints.DichotomousConstraint;
import de.dktk.dd.rpb.core.domain.randomisation.BlockRandomisationConfiguration;
import de.dktk.dd.rpb.core.domain.randomisation.PrognosticVariable;
import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;
import de.dktk.dd.rpb.core.domain.randomisation.TrialSubject;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRandomisationTest {

    private int id = 0;
    private Study study;
    private TrialSubject subject;
    private BlockRandomisationConfiguration conf;

    @Before
    public void setUp() {
        study = new Study();
        conf = new BlockRandomisationConfiguration();
        study.setRandomisationConfiguration(conf);
    }

    @Test
    public void testOneSiteTwoGroupsNoStrata() {
        // Configuration
        conf.setMinimumBlockSize(3);
        conf.setMaximumBlockSize(6);
        conf.setType(BlockRandomisationConfiguration.TYPE.MULTIPLY);

        study.setIsStratifyTrialSite(false);
        PartnerSite site1 = new PartnerSite();
        site1.setIdentifier("SITE1");
        study.setIsStratifyTrialSite(true);

        List<Study> studies = new ArrayList<Study>();
        studies.add(study);
        site1.setStudies(studies);

        // Two arms
        int numberOfTreatmentArms = 2;
        // Planned subjects per arm (two arms)
        int patientsPerArm = 49;

        List<TreatmentArm> arms = new ArrayList<TreatmentArm>();
        for (int i = 0; i < numberOfTreatmentArms; i++) {
            TreatmentArm arm = new TreatmentArm();
            arm.setName("Dummy treatment arm: " + (i + 1));
            arm.setPlannedSubjectsCount(patientsPerArm);
            arms.add(arm);
        }

        study.setTreatmentArms(arms);

        for (int i = 0; i < numberOfTreatmentArms * patientsPerArm; i++) {
            subject = new TrialSubject();
            subject.setTrialSite(site1);

            TreatmentArm assignedArm = study.getRandomisationConfiguration().getScheme().randomise(subject);

            subject.setTreatmentArm(assignedArm);
            assignedArm.addSubject(subject);
        }

        for(TreatmentArm arm : study.getTreatmentArms()){

            // At least
            assertTrue("Should be at least <" + (patientsPerArm - 1) + "> but was <" + arm.getSubjects().size() +">", arm.getSubjects().size() >= patientsPerArm - 1);

            // At most
            assertTrue("Should be at most <" + (patientsPerArm + 1) + "> but was <" + arm.getSubjects().size() + ">", arm.getSubjects().size() <= patientsPerArm + 1);

            // Does not necessary need to be as planned
            //assertEquals(patientsPerArm - 1, arm.getSubjects().size());
        }
    }

    @Test
    public void testTwoSitesTwoGroupsWithPartnerSitesStrata() {
        conf.setMinimumBlockSize(3);
        conf.setMaximumBlockSize(6);
        // set null for ABSOLUTE (not variable block size, when min and max are the same)
        conf.setType(BlockRandomisationConfiguration.TYPE.MULTIPLY);

        Random rndb = new Random();

        // Two arms
        int numberOfTreatmentArms = 2;
        // Planned subjects per arm (two arms)
        int patientsPerArm = 154;

        List<TreatmentArm> arms = new ArrayList<TreatmentArm>();

        for (int i = 0; i < numberOfTreatmentArms; i++) {
            TreatmentArm arm = new TreatmentArm();
            arm.setName("Dummy treatment arm: " + (i + 1));
            arm.setPlannedSubjectsCount(patientsPerArm);
            arms.add(arm);
        }

        study.setIsStratifyTrialSite(true);
        study.setTreatmentArms(arms);

        PartnerSite site1 = new PartnerSite();
        site1.setIdentifier("SITE1");

        PartnerSite site2 = new PartnerSite();
        site2.setIdentifier("SITE2");

        List<Study> studies = new ArrayList<Study>();
        studies.add(study);
        site1.setStudies(studies);

        boolean isYes = rndb.nextBoolean();
        boolean onetwo = rndb.nextBoolean();

        for (int i = 0; i < patientsPerArm * numberOfTreatmentArms; i++) {
            subject = new TrialSubject();
            if (onetwo) {
                subject.setTrialSite(site1);
            }
            else {
                subject.setTrialSite(site2);
            }

            List<PrognosticVariable<?>> variables = new ArrayList<PrognosticVariable<?>>();

            // First prognostic variable definition is exclusive yes/no property
            PrognosticVariable<String> v1 = getEmptyDichotomProperty1();
            variables.add(v1);
//                        PrognosticVariable<String> v2 = getEmptyDichotomProperty2();
//                        variables.add(v2);
//                        PrognosticVariable<String> v3 = getEmptyDichotomProperty3();
//                        variables.add(v3);
            try {
                //
                v1.setValue(isYes ? "yes" : "no");
                //                            v2.setValue("option" + j);
//                            v3.setValue("option" + k);
            }
            catch (Exception err) {
                fail(err.getMessage());
            }
            subject.setPrognosticVariables(variables);

            TreatmentArm assignedArm = study.getRandomisationConfiguration().getScheme().randomise(subject);

            subject.setTreatmentArm(assignedArm);
            assignedArm.addSubject(subject);

            onetwo = rndb.nextBoolean();
            isYes = rndb.nextBoolean();
        }

        int skuska1yestreatment1 = 0;
        int skuska1yestreatment2 = 0;
        int skuska1notreatment1 = 0;
        int skuska1notreatment2 = 0;
        int skuska2yestreatment1 = 0;
        int skuska2yestreatment2 = 0;
        int skuska2notreatment1 = 0;
        int skuska2notreatment2 = 0;
        for(TreatmentArm arm : study.getTreatmentArms()){

            for (TrialSubject sub: arm.getSubjects()) {
                if (sub.getTrialSite().getIdentifier().equals("SITE1")) {
                    if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 1")) {
                        for (PrognosticVariable pv : sub.getPrognosticVariables()) {
                            String value = (String)pv.getValue();
                            if (value.equals("yes")) {
                                skuska1yestreatment1++;
                            }
                            else if (value.equals("no"))
                            {
                                skuska1notreatment1++;
                            }
                        }
                    }
                    else if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 2"))
                    {
                        for (PrognosticVariable pv : sub.getPrognosticVariables()) {
                            String value = (String)pv.getValue();
                            if (value.equals("yes")) {
                                skuska1yestreatment2++;
                            }
                            else if (value.equals("no"))
                            {
                                skuska1notreatment2++;
                            }
                        }
                    }
                }
                else if (sub.getTrialSite().getIdentifier().equals("SITE2")) {
                    if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 1")) {
                        for (PrognosticVariable pv : sub.getPrognosticVariables()) {
                            String value = (String)pv.getValue();
                            if (value.equals("yes")) {
                                skuska2yestreatment1++;
                            }
                            else if (value.equals("no"))
                            {
                                skuska2notreatment1++;
                            }
                        }
                    }
                    else if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 2"))
                    {
                        for (PrognosticVariable pv : sub.getPrognosticVariables()) {
                            String value = (String)pv.getValue();
                            if (value.equals("yes")) {
                                skuska2yestreatment2++;
                            }
                            else if (value.equals("no"))
                            {
                                skuska2notreatment2++;
                            }
                        }
                    }
                }
            }


            // At least
            assertTrue("Should be at least <" + (patientsPerArm - 1) + "> but was <" + arm.getSubjects().size() +">", arm.getSubjects().size() >= patientsPerArm - 1);

            // At most
            assertTrue("Should be at most <" + (patientsPerArm + 1) + "> but was <" + arm.getSubjects().size() + ">", arm.getSubjects().size() <= patientsPerArm + 1);

            // Does not necessary need to be as planned
            //assertEquals(patientsPerArm, arm.getSubjects().size());
        }

        assertEquals(patientsPerArm * numberOfTreatmentArms, skuska1yestreatment1 + skuska1yestreatment2 + skuska1notreatment1 + skuska1notreatment2 + skuska2yestreatment1 + skuska2yestreatment2 + skuska2notreatment1 + skuska2notreatment2);
    }

//    @Ignore
//    public void testFourHundretAllocations2(){
//        RandomizationHelper.addArms(trial, 400, 400);
//        conf.setMinimum(1);
//        conf.setMaximum(2);
//        conf.setType(BlockRandomizationConfig.TYPE.MULTIPLY);
//        for (int i : upto(400)) {
//            s = new TrialSubject();
//            randomize(trial, s);
//        }
//        for(TreatmentArm arm : trial.getTreatmentArms()){
//            assertAtLeast(199, arm.getSubjects().size());
//            assertAtMost(201, arm.getSubjects().size());
//        }
//    }

//    @Test
//    public void testVaryingAllocation(){
//        RandomizationHelper.addArms(trial, 20, 20);
//        conf.setMinimum(15);
//        conf.setMaximum(20);
//        conf.setType(BlockRandomizationConfig.TYPE.ABSOLUTE);
//        for (int i : upto(20)) {
//            s = new TrialSubject();
//            randomize(trial, s);
//        }
//        for(TreatmentArm arm : trial.getTreatmentArms()){
//            assertAtLeast(7, arm.getSubjects().size());
//            assertAtMost(13, arm.getSubjects().size());
//        }
//    }

    private DichotomousCriterion dCriterion1 = null;
    private PrognosticVariable<String> getEmptyDichotomProperty1() {
        if (dCriterion1 == null) {

            // Setup criterion with two exclusive options
            dCriterion1 = new DichotomousCriterion();

            dCriterion1.setId(getNextId());
            dCriterion1.setOption1("yes");
            dCriterion1.setOption2("no");

            try {

                List<String> value = new ArrayList<String>();
                value.add(dCriterion1.getOption1());

                // Setup constraints for criterion
                DichotomousConstraint co = new DichotomousConstraint(value);
                co.setId(this.getNextId());

                // Add first constraint to criterion
                dCriterion1.addStrata(co);

                value.clear();
                value.add(dCriterion1.getOption2());

                // Setup second constraint for criterion
                co = new DichotomousConstraint(value);
                co.setId(this.getNextId());

                // Add second constraint to criterion
                dCriterion1.addStrata(co);
            }
            catch (Exception err) {
                fail(err.getMessage());
            }
        }

        return new PrognosticVariable<String>(dCriterion1);

    }

    private int getNextId() {
        return id++;
    }

}