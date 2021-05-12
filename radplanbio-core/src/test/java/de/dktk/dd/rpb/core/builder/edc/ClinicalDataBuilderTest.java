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

package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClinicalDataBuilder.class, Logger.class})
public class ClinicalDataBuilderTest {
    private ClinicalDataBuilder clinicalDataBuilder;
    private String dummyStudyOid = "DummyStudyOid";

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        Logger logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);
        this.clinicalDataBuilder = ClinicalDataBuilder.getInstance();
        this.clinicalDataBuilder.setStudyOid(dummyStudyOid);
    }

    @Test
    public void get_instance() {
        assertNotNull(this.clinicalDataBuilder);
    }

    @Test
    public void build_returns_a_valid_clinicalData_instance() throws MissingPropertyException {
        ClinicalData clinicalData = this.clinicalDataBuilder.build();
        assertTrue(clinicalData != null);
    }

    @Test(expected = MissingPropertyException.class)
    public void throws_if_StudyOid_is_missing() throws MissingPropertyException {
        this.clinicalDataBuilder.setStudyOid(null);
        this.clinicalDataBuilder.build();
    }

    @Test
    public void addSubjectData_adds_a_study_subject_to_clinical_data() throws MissingPropertyException {
        StudySubject studySubject = new StudySubject();
        studySubject.setPid("dummyPid");
        ClinicalData clinicalData = this.clinicalDataBuilder.
                addSubjectData(studySubject).
                build();
        assertEquals(studySubject, clinicalData.getStudySubjects().get(0));
    }

    @Test
    public void addSubjectData_adds_a_study_subject_once_only() throws MissingPropertyException {
        StudySubject studySubject = new StudySubject();
        studySubject.setPid("dummyPid");
        this.clinicalDataBuilder.addSubjectData(studySubject);
        ClinicalData clinicalData = this.clinicalDataBuilder.
                addSubjectData(studySubject).
                build();
        assertEquals(studySubject, clinicalData.getStudySubjects().get(0));
    }
}