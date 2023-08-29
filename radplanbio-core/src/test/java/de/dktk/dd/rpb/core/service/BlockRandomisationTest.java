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

package de.dktk.dd.rpb.core.service;

import de.dktk.dd.rpb.core.domain.criteria.DichotomousCriterion;
import de.dktk.dd.rpb.core.domain.criteria.constraints.DichotomousConstraint;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.randomisation.BlockRandomisationConfiguration;
import de.dktk.dd.rpb.core.domain.randomisation.PrognosticVariable;
import de.dktk.dd.rpb.core.domain.randomisation.TrialSubject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

//TODO: uncomment what needed for simulation execution
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({Logger.class})
public class BlockRandomisationTest {

    private int id = 0;
    private Study study;
    private TrialSubject subject;
    private BlockRandomisationConfiguration conf;

//    @Before
//    public void setUp() {
//        mockStatic(Logger.class);
//        Logger logger = mock(Logger.class);
//        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
//        study = new Study();
//        conf = new BlockRandomisationConfiguration();
//        study.setRandomisationConfiguration(conf);
//    }

//    @Test
//    public void simulateCR() {
//
//        // set null for ABSOLUTE (not variable block size, when min and max are the same)
//        conf.setMinimumBlockSize(3);
//        conf.setMaximumBlockSize(6);
//
//        conf.setType(BlockRandomisationConfiguration.TYPE.MULTIPLY);
//
//        Random rndb = new Random();
//
//        // Two arms
//        int numberOfTreatmentArms = 2;
//        // Planned subjects per arm (two arms)
//        int patientsPerArm1 = 44;
//        int patientsPerArm2 = 22;
//        int patientsTotal = patientsPerArm1 + patientsPerArm2;
//
//        List<TreatmentArm> arms = new ArrayList<>();
//        TreatmentArm arm1 = new TreatmentArm();
//        arm1.setName("Intervention");
//        arm1.setPlannedSubjectsCount(patientsPerArm1);
//        arms.add(arm1);
//
//        TreatmentArm arm2 = new TreatmentArm();
//        arm2.setName("Observation");
//        arm2.setPlannedSubjectsCount(patientsPerArm2);
//        arms.add(arm2);
//
//        study.setIsStratifyTrialSite(true);
//        study.setTreatmentArms(arms);
//
//        PartnerSite site1 = new PartnerSite();
//        site1.setIdentifier("DD");
//
//        List<Study> studies = new ArrayList<>();
//        studies.add(study);
//        site1.setStudies(studies);
//
//        boolean isYes = rndb.nextBoolean();
//
//        for (int i = 0; i < patientsTotal; i++) {
//            subject = new TrialSubject();
//            subject.setTrialSite(site1);
//
//            List<PrognosticVariable<?>> variables = new ArrayList<>();
//
//            // First prognostic variable definition is exclusive yes/no property
//            PrognosticVariable<String> v1 = getEmptyDichotomProperty1();
//            variables.add(v1);
//            PrognosticVariable<String> v2 = getEmptyDichotomProperty1();
//            variables.add(v2);
//            PrognosticVariable<String> v3 = getEmptyDichotomProperty1();
//            variables.add(v3);
//
//            try {
//                v1.setValue(isYes ? "yes" : "no");
//                isYes = rndb.nextBoolean();
//                v2.setValue(isYes ? "yes" : "no");
//                isYes = rndb.nextBoolean();
//                v3.setValue(isYes ? "yes" : "no");
//
//            } catch (Exception err) {
//                fail(err.getMessage());
//            }
//
//            subject.setPrognosticVariables(variables);
//
//            TreatmentArm assignedArm = study.getRandomisationConfiguration().getScheme().randomise(subject);
//
//            subject.setTreatmentArm(assignedArm);
//            assignedArm.addSubject(subject);
//
//            isYes = rndb.nextBoolean();
//        }
//
//        int v1yesv2yesv3yesIntervention = 0;
//        int v1yesv2yesv3noIntervention = 0;
//        int v1yesv2nov3yesIntervention = 0;
//        int v1yesv2nov3noIntervention = 0;
//        int v1nov2yesv3yesIntervention = 0;
//        int v1nov2yesv3noIntervention = 0;
//        int v1nov2nov3yesIntervention = 0;
//        int v1no2vnov3noIntervention = 0;
//
//        int v1yesv2yesv3yesObservation = 0;
//        int v1yesv2yesv3noObservation = 0;
//        int v1yesv2nov3yesObservation = 0;
//        int v1yesv2nov3noObservation = 0;
//        int v1nov2yesv3yesObservation = 0;
//        int v1nov2yesv3noObservation = 0;
//        int v1nov2nov3yesObservation = 0;
//        int v1no2vnov3noObservation = 0;
//
//        TreatmentArm resultArmIntervention = study.getTreatmentArms().get(0);
//        for (TrialSubject sub: resultArmIntervention.getSubjects()) {
//            if (sub.getTrialSite().getIdentifier().equals("DD")) {
//                if (sub.getTreatmentArm().getName().equals("Intervention")) {
//
//                    String v1 = (String) sub.getPrognosticVariables().get(0).getValue();
//                    String v2 = (String) sub.getPrognosticVariables().get(1).getValue();
//                    String v3 = (String) sub.getPrognosticVariables().get(2).getValue();
//
//                    // v1yesv2yesv3yesIntervention
//                    if ("yes".equals(v1) && "yes".equals(v2) && "yes".equals(v3)) {
//                        v1yesv2yesv3yesIntervention++;
//                    }
//                    // v1yesv2yesv3noIntervention
//                    else if ("yes".equals(v1) && "yes".equals(v2) && "no".equals(v3)) {
//                        v1yesv2yesv3noIntervention++;
//                    }
//                    // v1yesv2nov3yesIntervention
//                    else if ("yes".equals(v1) && "no".equals(v2) && "yes".equals(v3)) {
//                        v1yesv2nov3yesIntervention++;
//                    }
//                    // v1yesv2nov3noIntervention
//                    else if ("yes".equals(v1) && "no".equals(v2) && "no".equals(v3)) {
//                        v1yesv2nov3noIntervention++;
//                    }
//                    // v1nov2yesv3yesIntervention
//                    else if ("no".equals(v1) && "yes".equals(v2) && "yes".equals(v3)) {
//                        v1nov2yesv3yesIntervention++;
//                    }
//                    // v1nov2yesv3noIntervention
//                    else if ("no".equals(v1) && "yes".equals(v2) && "no".equals(v3)) {
//                        v1nov2yesv3noIntervention++;
//                    }
//                    // v1nov2nov3yesIntervention
//                    else if ("no".equals(v1) && "no".equals(v2) && "yes".equals(v3)) {
//                        v1nov2nov3yesIntervention++;
//                    }
//                    // v1no2vnov3noIntervention
//                    else if ("no".equals(v1) && "no".equals(v2) && "no".equals(v3)) {
//                        v1no2vnov3noIntervention++;
//                    }
//
//                }
//            }
//        }
//
//        // At least
//        assertTrue("Should be at least <" + (patientsPerArm1 - 1) + "> but was <" + resultArmIntervention.getSubjects().size() +">", resultArmIntervention.getSubjects().size() >= patientsPerArm1 - 1);
//
//        // At most
//        assertTrue("Should be at most <" + (patientsPerArm1 + 1) + "> but was <" + resultArmIntervention.getSubjects().size() + ">", resultArmIntervention.getSubjects().size() <= patientsPerArm1 + 1);
//
//        TreatmentArm resultArmObservation = study.getTreatmentArms().get(1);
//        for (TrialSubject sub: resultArmObservation.getSubjects()) {
//            if (sub.getTrialSite().getIdentifier().equals("DD")) {
//                if (sub.getTreatmentArm().getName().equals("Observation")) {
//
//                    String v1 = (String) sub.getPrognosticVariables().get(0).getValue();
//                    String v2 = (String) sub.getPrognosticVariables().get(1).getValue();
//                    String v3 = (String) sub.getPrognosticVariables().get(2).getValue();
//
//                    // v1yesv2yesv3yesObservation
//                    if ("yes".equals(v1) && "yes".equals(v2) && "yes".equals(v3)) {
//                        v1yesv2yesv3yesObservation++;
//                    }
//                    // v1yesv2yesv3noObservation
//                    else if ("yes".equals(v1) && "yes".equals(v2) && "no".equals(v3)) {
//                        v1yesv2yesv3noObservation++;
//                    }
//                    // v1yesv2nov3yesObservation
//                    else if ("yes".equals(v1) && "no".equals(v2) && "yes".equals(v3)) {
//                        v1yesv2nov3yesObservation++;
//                    }
//                    // v1yesv2nov3noObservation
//                    else if ("yes".equals(v1) && "no".equals(v2) && "no".equals(v3)) {
//                        v1yesv2nov3noObservation++;
//                    }
//                    // v1nov2yesv3yesObservation
//                    else if ("no".equals(v1) && "yes".equals(v2) && "yes".equals(v3)) {
//                        v1nov2yesv3yesObservation++;
//                    }
//                    // v1nov2yesv3noObservation
//                    else if ("no".equals(v1) && "yes".equals(v2) && "no".equals(v3)) {
//                        v1nov2yesv3noObservation++;
//                    }
//                    // v1nov2nov3yesObservation
//                    else if ("no".equals(v1) && "no".equals(v2) && "yes".equals(v3)) {
//                        v1nov2nov3yesObservation++;
//                    }
//                    // v1no2vnov3noObservation
//                    else if ("no".equals(v1) && "no".equals(v2) && "no".equals(v3)) {
//                        v1no2vnov3noObservation++;
//                    }
//                }
//            }
//        }
//
//        // At least
//        assertTrue("Should be at least <" + (patientsPerArm2 - 1) + "> but was <" + resultArmObservation.getSubjects().size() +">", resultArmObservation.getSubjects().size() >= patientsPerArm2 - 1);
//
//        // At most
//        assertTrue("Should be at most <" + (patientsPerArm2 + 1) + "> but was <" + resultArmObservation.getSubjects().size() + ">", resultArmObservation.getSubjects().size() <= patientsPerArm2 + 1);
//
//        int totalInterventionStrataGroups =  v1yesv2yesv3yesIntervention + v1yesv2yesv3noIntervention +
//                v1yesv2nov3yesIntervention + v1yesv2nov3noIntervention +
//                v1nov2yesv3yesIntervention + v1nov2yesv3noIntervention +
//                v1nov2nov3yesIntervention + v1no2vnov3noIntervention;
//
//        int totalObservationStrataGroups =  v1yesv2yesv3yesObservation + v1yesv2yesv3noObservation +
//                v1yesv2nov3yesObservation + v1yesv2nov3noObservation +
//                v1nov2yesv3yesObservation + v1nov2yesv3noObservation +
//                v1nov2nov3yesObservation +  v1no2vnov3noObservation;
//
//        assertEquals(patientsPerArm1 + patientsPerArm2, totalInterventionStrataGroups + totalObservationStrataGroups);
//    }

//    @Test
//    public void testOneSiteTwoGroupsNoStrata() {
//        // Configuration
//        conf.setMinimumBlockSize(3);
//        conf.setMaximumBlockSize(6);
//        conf.setType(BlockRandomisationConfiguration.TYPE.MULTIPLY);
//
//        study.setIsStratifyTrialSite(false);
//        PartnerSite site1 = new PartnerSite();
//        site1.setIdentifier("SITE1");
//        study.setIsStratifyTrialSite(true);
//
//        List<Study> studies = new ArrayList<>();
//        studies.add(study);
//        site1.setStudies(studies);
//
//        // Two arms
//        int numberOfTreatmentArms = 2;
//        // Planned subjects per arm (two arms)
//        int patientsPerArm = 49;
//
//        List<TreatmentArm> arms = new ArrayList<>();
//        for (int i = 0; i < numberOfTreatmentArms; i++) {
//            TreatmentArm arm = new TreatmentArm();
//            arm.setName("Dummy treatment arm: " + (i + 1));
//            arm.setPlannedSubjectsCount(patientsPerArm);
//            arms.add(arm);
//        }
//
//        study.setTreatmentArms(arms);
//
//        for (int i = 0; i < numberOfTreatmentArms * patientsPerArm; i++) {
//            subject = new TrialSubject();
//            subject.setTrialSite(site1);
//
//            TreatmentArm assignedArm = study.getRandomisationConfiguration().getScheme().randomise(subject);
//
//            subject.setTreatmentArm(assignedArm);
//            assignedArm.addSubject(subject);
//        }
//
//        for(TreatmentArm arm : study.getTreatmentArms()){
//
//            // At least
//            assertTrue("Should be at least <" + (patientsPerArm - 1) + "> but was <" + arm.getSubjects().size() +">", arm.getSubjects().size() >= patientsPerArm - 1);
//
//            // At most
//            assertTrue("Should be at most <" + (patientsPerArm + 1) + "> but was <" + arm.getSubjects().size() + ">", arm.getSubjects().size() <= patientsPerArm + 1);
//
//            // Does not necessary need to be as planned
//            //assertEquals(patientsPerArm - 1, arm.getSubjects().size());
//        }
//    }

//    @Test
//    public void testOneSiteTwoGroupsNoStrataUnequalAllocations() {
//        // Configuration
//        conf.setMinimumBlockSize(3);
//        conf.setMaximumBlockSize(6);
//        conf.setType(BlockRandomisationConfiguration.TYPE.MULTIPLY);
//
//        study.setIsStratifyTrialSite(false);
//        PartnerSite site1 = new PartnerSite();
//        site1.setIdentifier("SITE1");
//        study.setIsStratifyTrialSite(true);
//
//        List<Study> studies = new ArrayList<>();
//        studies.add(study);
//        site1.setStudies(studies);
//
//        // Two arms
//        int numberOfTreatmentArms = 2;
//        // Planned subjects per arm (two arms)
//        int patientsPerArm1 = 76;
//        int patientsPerArm2 = 38;
//
//        int patientsTotal = patientsPerArm1 + patientsPerArm2;
//
//        List<TreatmentArm> arms = new ArrayList<>();
//        TreatmentArm arm1 = new TreatmentArm();
//        arm1.setName("Dummy treatment arm: 1");
//        arm1.setPlannedSubjectsCount(patientsPerArm1);
//        arms.add(arm1);
//
//        TreatmentArm arm2 = new TreatmentArm();
//        arm2.setName("Dummy treatment arm: 2");
//        arm2.setPlannedSubjectsCount(patientsPerArm2);
//        arms.add(arm2);
//
//        study.setTreatmentArms(arms);
//
//        for (int i = 0; i < patientsTotal; i++) {
//            subject = new TrialSubject();
//            subject.setTrialSite(site1);
//
//            TreatmentArm assignedArm = study.getRandomisationConfiguration().getScheme().randomise(subject);
//
//            subject.setTreatmentArm(assignedArm);
//            assignedArm.addSubject(subject);
//        }
//
//
//        int skuska1treatment1 = 0;
//        int skuska1treatment2 = 0;
//
//        TreatmentArm resultArm1 = study.getTreatmentArms().get(0);
//        for (TrialSubject sub: resultArm1.getSubjects()) {
//            if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 1")) {
//                skuska1treatment1++;
//            }
//        }
//
//        // At least
//        assertTrue("Should be at least <" + (patientsPerArm1 - 1) + "> but was <" + resultArm1.getSubjects().size() +">", resultArm1.getSubjects().size() >= patientsPerArm1 - 1);
//
//        // At most
//        assertTrue("Should be at most <" + (patientsPerArm1 + 1) + "> but was <" + resultArm1.getSubjects().size() + ">", resultArm1.getSubjects().size() <= patientsPerArm1 + 1);
//
//        TreatmentArm resultArm2 = study.getTreatmentArms().get(1);
//        for (TrialSubject sub: resultArm2.getSubjects()) {
//            if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 2")) {
//                skuska1treatment2++;
//            }
//        }
//
//        // At least
//        assertTrue("Should be at least <" + (patientsPerArm2 - 1) + "> but was <" + resultArm2.getSubjects().size() +">", resultArm2.getSubjects().size() >= patientsPerArm2 - 1);
//
//        // At most
//        assertTrue("Should be at most <" + (patientsPerArm2 + 1) + "> but was <" + resultArm2.getSubjects().size() + ">", resultArm2.getSubjects().size() <= patientsPerArm2 + 1);
//
//
//        assertEquals(patientsPerArm1 + patientsPerArm2, skuska1treatment1 + skuska1treatment2);
//    }

//    @Test
//    public void testOneSiteTwoGroupsStrataUnequalAllocations() {
//        conf.setMinimumBlockSize(3);
//        conf.setMaximumBlockSize(6);
//        // set null for ABSOLUTE (not variable block size, when min and max are the same)
//        conf.setType(BlockRandomisationConfiguration.TYPE.MULTIPLY);
//
//        Random rndb = new Random();
//
//        // Two arms
//        int numberOfTreatmentArms = 2;
//        // Planned subjects per arm (two arms)
//        int patientsPerArm1 = 76;
//        int patientsPerArm2 = 38;
//        int patientsTotal = patientsPerArm1 + patientsPerArm2;
//
//        List<TreatmentArm> arms = new ArrayList<>();
//        TreatmentArm arm1 = new TreatmentArm();
//        arm1.setName("Dummy treatment arm: 1");
//        arm1.setPlannedSubjectsCount(patientsPerArm1);
//        arms.add(arm1);
//
//        TreatmentArm arm2 = new TreatmentArm();
//        arm2.setName("Dummy treatment arm: 2");
//        arm2.setPlannedSubjectsCount(patientsPerArm2);
//        arms.add(arm2);
//
//        study.setIsStratifyTrialSite(true);
//        study.setTreatmentArms(arms);
//
//        PartnerSite site1 = new PartnerSite();
//        site1.setIdentifier("SITE1");
//
//        List<Study> studies = new ArrayList<>();
//        studies.add(study);
//        site1.setStudies(studies);
//
//        boolean isYes = rndb.nextBoolean();
//
//        for (int i = 0; i < patientsTotal; i++) {
//            subject = new TrialSubject();
//            subject.setTrialSite(site1);
//
//            List<PrognosticVariable<?>> variables = new ArrayList<>();
//
//            // First prognostic variable definition is exclusive yes/no property
//            PrognosticVariable<String> v1 = getEmptyDichotomProperty1();
//            variables.add(v1);
//
//            try {
//                v1.setValue(isYes ? "yes" : "no");
//            } catch (Exception err) {
//                fail(err.getMessage());
//            }
//            subject.setPrognosticVariables(variables);
//
//            TreatmentArm assignedArm = study.getRandomisationConfiguration().getScheme().randomise(subject);
//
//            subject.setTreatmentArm(assignedArm);
//            assignedArm.addSubject(subject);
//
//            isYes = rndb.nextBoolean();
//        }
//
//        int skuska1yestreatment1 = 0;
//        int skuska1yestreatment2 = 0;
//        int skuska1notreatment1 = 0;
//        int skuska1notreatment2 = 0;
//
//        TreatmentArm resultArm1 = study.getTreatmentArms().get(0);
//        for (TrialSubject sub: resultArm1.getSubjects()) {
//            if (sub.getTrialSite().getIdentifier().equals("SITE1")) {
//                if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 1")) {
//                    for (PrognosticVariable pv : sub.getPrognosticVariables()) {
//                        String value = (String) pv.getValue();
//                        if ("yes".equals(value)) {
//                            skuska1yestreatment1++;
//                        } else if ("no".equals(value)) {
//                            skuska1notreatment1++;
//                        }
//                    }
//                }
//            }
//        }
//
//        // At least
//        assertTrue("Should be at least <" + (patientsPerArm1 - 1) + "> but was <" + resultArm1.getSubjects().size() +">", resultArm1.getSubjects().size() >= patientsPerArm1 - 1);
//
//        // At most
//        assertTrue("Should be at most <" + (patientsPerArm1 + 1) + "> but was <" + resultArm1.getSubjects().size() + ">", resultArm1.getSubjects().size() <= patientsPerArm1 + 1);
//
//        TreatmentArm resultArm2 = study.getTreatmentArms().get(1);
//        for (TrialSubject sub: resultArm2.getSubjects()) {
//            if (sub.getTrialSite().getIdentifier().equals("SITE1")) {
//                if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 2")) {
//                    for (PrognosticVariable pv : sub.getPrognosticVariables()) {
//                        String value = (String) pv.getValue();
//                        if ("yes".equals(value)) {
//                            skuska1yestreatment2++;
//                        } else if ("no".equals(value)) {
//                            skuska1notreatment2++;
//                        }
//                    }
//                }
//            }
//        }
//
//        // At least
//        assertTrue("Should be at least <" + (patientsPerArm2 - 1) + "> but was <" + resultArm2.getSubjects().size() +">", resultArm2.getSubjects().size() >= patientsPerArm2 - 1);
//
//        // At most
//        assertTrue("Should be at most <" + (patientsPerArm2 + 1) + "> but was <" + resultArm2.getSubjects().size() + ">", resultArm2.getSubjects().size() <= patientsPerArm2 + 1);
//
//
//        assertEquals(patientsPerArm1 + patientsPerArm2, skuska1yestreatment1 + skuska1yestreatment2 + skuska1notreatment1 + skuska1notreatment2);
//    }

//    @Test
//    public void testTwoSitesTwoGroupsWithPartnerSitesStrata() {
//        conf.setMinimumBlockSize(3);
//        conf.setMaximumBlockSize(6);
//        // set null for ABSOLUTE (not variable block size, when min and max are the same)
//        conf.setType(BlockRandomisationConfiguration.TYPE.MULTIPLY);
//
//        Random rndb = new Random();
//
//        // Two arms
//        int numberOfTreatmentArms = 2;
//        // Planned subjects per arm (two arms)
//        int patientsPerArm = 154;
//
//        List<TreatmentArm> arms = new ArrayList<>();
//
//        for (int i = 0; i < numberOfTreatmentArms; i++) {
//            TreatmentArm arm = new TreatmentArm();
//            arm.setName("Dummy treatment arm: " + (i + 1));
//            arm.setPlannedSubjectsCount(patientsPerArm);
//            arms.add(arm);
//        }
//
//        study.setIsStratifyTrialSite(true);
//        study.setTreatmentArms(arms);
//
//        PartnerSite site1 = new PartnerSite();
//        site1.setIdentifier("SITE1");
//
//        PartnerSite site2 = new PartnerSite();
//        site2.setIdentifier("SITE2");
//
//        List<Study> studies = new ArrayList<>();
//        studies.add(study);
//        site1.setStudies(studies);
//
//        boolean isYes = rndb.nextBoolean();
//        boolean onetwo = rndb.nextBoolean();
//
//        for (int i = 0; i < patientsPerArm * numberOfTreatmentArms; i++) {
//            subject = new TrialSubject();
//            if (onetwo) {
//                subject.setTrialSite(site1);
//            } else {
//                subject.setTrialSite(site2);
//            }
//
//            List<PrognosticVariable<?>> variables = new ArrayList<>();
//
//            // First prognostic variable definition is exclusive yes/no property
//            PrognosticVariable<String> v1 = getEmptyDichotomProperty1();
//            variables.add(v1);
////                        PrognosticVariable<String> v2 = getEmptyDichotomProperty2();
////                        variables.add(v2);
////                        PrognosticVariable<String> v3 = getEmptyDichotomProperty3();
////                        variables.add(v3);
//            try {
//                //
//                v1.setValue(isYes ? "yes" : "no");
//                //                            v2.setValue("option" + j);
////                            v3.setValue("option" + k);
//            } catch (Exception err) {
//                fail(err.getMessage());
//            }
//            subject.setPrognosticVariables(variables);
//
//            TreatmentArm assignedArm = study.getRandomisationConfiguration().getScheme().randomise(subject);
//
//            subject.setTreatmentArm(assignedArm);
//            assignedArm.addSubject(subject);
//
//            onetwo = rndb.nextBoolean();
//            isYes = rndb.nextBoolean();
//        }
//
//        int skuska1yestreatment1 = 0;
//        int skuska1yestreatment2 = 0;
//        int skuska1notreatment1 = 0;
//        int skuska1notreatment2 = 0;
//        int skuska2yestreatment1 = 0;
//        int skuska2yestreatment2 = 0;
//        int skuska2notreatment1 = 0;
//        int skuska2notreatment2 = 0;
//        for(TreatmentArm arm : study.getTreatmentArms()){
//
//            for (TrialSubject sub: arm.getSubjects()) {
//                if (sub.getTrialSite().getIdentifier().equals("SITE1")) {
//                    if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 1")) {
//                        for (PrognosticVariable pv : sub.getPrognosticVariables()) {
//                            String value = (String)pv.getValue();
//                            if ("yes".equals(value)) {
//                                skuska1yestreatment1++;
//                            } else if ("no".equals(value)) {
//                                skuska1notreatment1++;
//                            }
//                        }
//                    } else if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 2")) {
//                        for (PrognosticVariable pv : sub.getPrognosticVariables()) {
//                            String value = (String)pv.getValue();
//                            if ("yes".equals(value)) {
//                                skuska1yestreatment2++;
//                            } else if ("no".equals(value)) {
//                                skuska1notreatment2++;
//                            }
//                        }
//                    }
//                } else if (sub.getTrialSite().getIdentifier().equals("SITE2")) {
//                    if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 1")) {
//                        for (PrognosticVariable pv : sub.getPrognosticVariables()) {
//                            String value = (String)pv.getValue();
//                            if ("yes".equals(value)) {
//                                skuska2yestreatment1++;
//                            } else if ("no".equals(value)) {
//                                skuska2notreatment1++;
//                            }
//                        }
//                    } else if (sub.getTreatmentArm().getName().equals("Dummy treatment arm: 2")) {
//                        for (PrognosticVariable pv : sub.getPrognosticVariables()) {
//                            String value = (String)pv.getValue();
//                            if ("yes".equals(value)) {
//                                skuska2yestreatment2++;
//                            } else if ("no".equals(value)) {
//                                skuska2notreatment2++;
//                            }
//                        }
//                    }
//                }
//            }
//
//
//            // At least
//            assertTrue("Should be at least <" + (patientsPerArm - 1) + "> but was <" + arm.getSubjects().size() +">", arm.getSubjects().size() >= patientsPerArm - 1);
//
//            // At most
//            assertTrue("Should be at most <" + (patientsPerArm + 1) + "> but was <" + arm.getSubjects().size() + ">", arm.getSubjects().size() <= patientsPerArm + 1);
//
//            // Does not necessary need to be as planned
//            //assertEquals(patientsPerArm, arm.getSubjects().size());
//        }
//
//        assertEquals(patientsPerArm * numberOfTreatmentArms, skuska1yestreatment1 + skuska1yestreatment2 + skuska1notreatment1 + skuska1notreatment2 + skuska2yestreatment1 + skuska2yestreatment2 + skuska2notreatment1 + skuska2notreatment2);
//    }

//    @Test
//    public void testFourHundredAllocations2(){
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

                List<String> value = new ArrayList<>();
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
            } catch (Exception err) {
                fail(err.getMessage());
            }
        }

        return new PrognosticVariable<>(dCriterion1);

    }

    private int getNextId() {
        return id++;
    }

}
