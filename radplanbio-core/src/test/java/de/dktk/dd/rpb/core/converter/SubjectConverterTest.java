/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.domain.lab.SubjectAttributes;
import de.dktk.dd.rpb.core.util.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class, LoggerFactory.class})
public class SubjectConverterTest {
    LabKeyExportConfiguration labKeyExportConfiguration;

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        labKeyExportConfiguration = mock(LabKeyExportConfiguration.class);
        when(labKeyExportConfiguration.isSexRequired()).thenReturn(true);
        when(labKeyExportConfiguration.isFullDateOfBirthRequired()).thenReturn(true);
        when(labKeyExportConfiguration.isYearOfBirthRequired()).thenReturn(true);
    }

    // region StudySubject
    @Test
    public void coverts_a_plain_object() {
        StudySubject studySubject = new StudySubject();
        SubjectAttributes labkeySubject = SubjectConverter.convertToLabKey(studySubject, labKeyExportConfiguration);

        assertNotNull(labkeySubject);
    }

    @Test
    public void converts_an_object_with_all_properties() throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.OC_DATEFORMAT);

        String pid = "dummy_pid";
        int id = 123;
        String studySubjectId = "dummy_study_subject";
        String secondaryId = "dummy_secondary_id";
        String sex = "d";

        String birthDayString = "1900-12-31";
        LocalDate birthday = LocalDate.parse(birthDayString, formatter);

        Integer birthYear = 1900;

        String enrollmentDateString = "2020-11-25";
        LocalDate enrollmentDate = LocalDate.parse(enrollmentDateString, formatter);

        String status = "dummy_status";
        Boolean enabled = true;

        StudySubject subject = new StudySubject();
        subject.setPid(pid);
        subject.setId(id);
        subject.setStudySubjectId(studySubjectId);
        subject.setSecondaryId(secondaryId);
        subject.setSex(sex);
        subject.setDateOfBirth(birthDayString);
        subject.setYearOfBirth(birthYear);
        subject.setEnrollmentDate(enrollmentDateString);
        subject.setStatus(status);
        subject.setIsEnabled(enabled);

        SubjectAttributes labkeySubject = SubjectConverter.convertToLabKey(subject, labKeyExportConfiguration);

        assertEquals(pid, labkeySubject.getUniqueIdentifier());
        assertEquals(studySubjectId, labkeySubject.getStudySubjectId());
        assertEquals(secondaryId, labkeySubject.getSecondaryId());
        assertEquals(sex, labkeySubject.getGender());
        assertEquals(birthday, labkeySubject.getDateOfBirth());
        assertTrue(birthYear == labkeySubject.getYearOfBirth());
        assertEquals(enrollmentDate, labkeySubject.getEnrollmentDate());
        assertEquals(status, labkeySubject.getStatus());
        assertEquals(enabled, labkeySubject.getEnabled());
    }

    // endregion
}
