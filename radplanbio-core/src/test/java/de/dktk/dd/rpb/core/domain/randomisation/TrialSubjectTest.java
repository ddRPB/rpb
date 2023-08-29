/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2018 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.criteria.DichotomousCriterion;
import de.dktk.dd.rpb.core.domain.criteria.constraints.DichotomousConstraint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Trial subject
 *
 * @author tomas@skripcak.net
 * @since 24 Jan 2014
 *
 * Inspired by RANDI2 <http://www.gnu.org/licenses/>
 * http://dschrimpf.github.io/randi3/
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TrialSubjectTest.class, Logger.class, LoggerFactory.class})
public class TrialSubjectTest {

    private int id = 0;

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
    }

    @Test
    public void testGetStratum1() {
        // every entry grouped the members of one group
        List<List<TrialSubject>> subjects = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 2; j++) {
                for (int k = 1; k <= 2; k++) {

                    List<TrialSubject> subjectsGroup = new ArrayList<>();

                    // generate objects with same group
                    for (int l = 0; l < 3; l++) {
                        TrialSubject subject = new TrialSubject();
                        List<PrognosticVariable<?>> variables = new ArrayList<>();
                        PrognosticVariable<String> v1 = getEmptyDichotomProperty1();
                        variables.add(v1);
//                        PrognosticVariable<String> v2 = getEmptyDichotomProperty2();
//                        variables.add(v2);
//                        PrognosticVariable<String> v3 = getEmptyDichotomProperty3();
//                        variables.add(v3);
                        try {
                            v1.setValue("option" + i);
//                            v2.setValue("option" + j);
//                            v3.setValue("option" + k);
                        }
                        catch (Exception err) {
                            fail(err.getMessage());
                        }
                        subject.setPrognosticVariables(variables);
                        subjectsGroup.add(subject);
                    }
                    subjects.add(subjectsGroup);
                }

            }
        }

        assertEquals(8, subjects.size());
        testStratification(subjects);
    }

    private void testStratification(List<List<TrialSubject>> subjects) {
        for (List<TrialSubject> subs : subjects) {
            // Test getStratum in one group
            for (int k = 0; k < subs.size() - 1; k++) {
                for (int l = k + 1; l < subs.size(); l++) {
                    assertEquals(subs.get(k).getStrata(), subs.get(l).getStrata());
                }
            }

//            for (int j = i + 1; j < subs.size(); j++) {
//                assertFalse(subjects.get(i).get(0).getStrata().equals(subjects.get(j).get(0).getStrata()));
//            }

        }
    }

    private DichotomousCriterion dCriterion1 = null;

    private PrognosticVariable<String> getEmptyDichotomProperty1() {
        if (dCriterion1 == null) {

            // Setup criterion with two options
            dCriterion1 = new DichotomousCriterion();

            dCriterion1.setId(getNextId());
            dCriterion1.setOption1("option1");
            dCriterion1.setOption2("option2");

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
                co = new DichotomousConstraint(value);
                co.setId(this.getNextId());

                // Add second constraint to criterion
                dCriterion1.addStrata(co);
            }
            catch (Exception err) {
                fail(err.getMessage());
            }
        }

        return new PrognosticVariable<>(dCriterion1);

    }

    private int getNextId() {
        return id++;
    }

}
