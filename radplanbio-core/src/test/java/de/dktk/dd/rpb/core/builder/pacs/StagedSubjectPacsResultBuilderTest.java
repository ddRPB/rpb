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
package de.dktk.dd.rpb.core.builder.pacs;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.domain.pacs.StagedSubject;
import de.dktk.dd.rpb.core.util.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StagedSubjectPacsResultBuilder.class, Logger.class, LoggerFactory.class})
public class StagedSubjectPacsResultBuilderTest {
    private StagedSubjectPacsResultBuilder stagedSubjectPacsResultBuilder;
    private List<StudySubject> queriedStudySubjects;
    private List<Subject> resultSubjects;
    private Subject subject;

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        queriedStudySubjects = new ArrayList<>();
        resultSubjects = new ArrayList<>();
        subject = new Subject();

    }


    @Test
    public void get_instance() {
        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        assertNotNull(stagedSubjectPacsResultBuilder);
    }

    // region filterResultsByStudySubjectList

    @Test
    public void filter_on_empty_lists_does_not_throw() {
        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        stagedSubjectPacsResultBuilder = stagedSubjectPacsResultBuilder.filterResultsByStudySubjectList();
        assertNotNull(stagedSubjectPacsResultBuilder);
    }

    @Test
    public void filter_of_elements_without_pid_or_uniqueIdentifier_does_not_throw() {
        StudySubject studySubjectOne = new StudySubject();
        queriedStudySubjects.add(studySubjectOne);

        Subject subjectOne = new Subject();
        resultSubjects.add(subjectOne);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        stagedSubjectPacsResultBuilder = stagedSubjectPacsResultBuilder.filterResultsByStudySubjectList();
        assertNotNull(stagedSubjectPacsResultBuilder);
    }

    @Test
    public void filter_subject_that_was_not_part_of_the_query() {
        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("2");
        queriedStudySubjects.add(studySubjectOne);

        Subject subjectOne = new Subject();
        subjectOne.setUniqueIdentifier("1");
        resultSubjects.add(subjectOne);
        Subject subjectTwo = new Subject();
        subjectTwo.setUniqueIdentifier("2");
        resultSubjects.add(subjectTwo);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        stagedSubjectPacsResultBuilder.filterResultsByStudySubjectList();
        List<StagedSubject> filteredSubjects = stagedSubjectPacsResultBuilder.getStagedSubjects();

        assertEquals(1, filteredSubjects.size());
        assertEquals("2", filteredSubjects.get(0).getUniqueIdentifier());
    }

    //endregion

    // region addSubjectsWithoutResultsFromStudySubjectList

    @Test
    public void addSubjects_on_empty_lists_does_not_throw() {
        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        stagedSubjectPacsResultBuilder = stagedSubjectPacsResultBuilder.addSubjectsWithoutResultsFromStudySubjectList();
        assertNotNull(stagedSubjectPacsResultBuilder);
    }

    @Test
    public void addSubjects_of_elements_without_pid_or_uniqueIdentifier_does_not_throw() {
        StudySubject studySubjectOne = new StudySubject();
        queriedStudySubjects.add(studySubjectOne);

        Subject subjectOne = new Subject();
        resultSubjects.add(subjectOne);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        stagedSubjectPacsResultBuilder = stagedSubjectPacsResultBuilder.addSubjectsWithoutResultsFromStudySubjectList();
        assertNotNull(stagedSubjectPacsResultBuilder);
    }

    @Test
    public void add_subject_that_was_part_of_the_query_but_not_part_of_the_results() {
        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        queriedStudySubjects.add(studySubjectOne);

        StudySubject studySubjectTwo = new StudySubject();
        studySubjectTwo.setPid("2");
        queriedStudySubjects.add(studySubjectTwo);

        Subject subjectTwo = new Subject();
        subjectTwo.setUniqueIdentifier("1");
        resultSubjects.add(subjectTwo);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        stagedSubjectPacsResultBuilder.addSubjectsWithoutResultsFromStudySubjectList();
        List<StagedSubject> filteredSubjects = stagedSubjectPacsResultBuilder.getStagedSubjects();

        assertEquals(2, filteredSubjects.size());
        assertEquals("2", filteredSubjects.get(1).getUniqueIdentifier());
    }

    // endregion

    //region getStagedSubjects()

    @Test
    public void migrateInformationFromStudySubjectsToSubject() {
        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        StudySubject studySubjectTwo = new StudySubject();
        studySubjectTwo.setPid("2");
        studySubjectTwo.setStudySubjectId("StudySubjectId2");
        queriedStudySubjects.add(studySubjectOne);
        queriedStudySubjects.add(studySubjectTwo);

        Subject subjectTwo = new Subject();
        subjectTwo.setUniqueIdentifier("2");
        resultSubjects.add(subjectTwo);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        List<StagedSubject> results = stagedSubjectPacsResultBuilder.getStagedSubjects();
        assertEquals("2", results.get(0).getUniqueIdentifier());
        assertEquals("StudySubjectId2", results.get(0).getStudySubjectId());

    }

    @Test
    public void does_not_throw_if_subject_property_uniqueIdentifier_is_null() {
        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        StudySubject studySubjectTwo = new StudySubject();
        studySubjectTwo.setPid("2");
        queriedStudySubjects.add(studySubjectOne);
        queriedStudySubjects.add(studySubjectTwo);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        stagedSubjectPacsResultBuilder.getStagedSubjects();
    }

    @Test
    public void keeps_the_subject_if_no_matching_study_subject_object_exists() {
        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        StudySubject studySubjectTwo = new StudySubject();
        studySubjectTwo.setPid("2");
        studySubjectTwo.setStudySubjectId("StudySubjectId2");
        queriedStudySubjects.add(studySubjectOne);
        queriedStudySubjects.add(studySubjectTwo);
        subject.setUniqueIdentifier("3");
        resultSubjects.add(subject);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        stagedSubjectPacsResultBuilder.getStagedSubjects();

        Subject resultSubject = stagedSubjectPacsResultBuilder.getStagedSubjects().get(0);
        assertEquals("3", resultSubject.getUniqueIdentifier());
    }

    @Test
    public void migrates_study_subject_id() {
        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        studySubjectOne.setStudySubjectId("StudySubjectId1");
        queriedStudySubjects.add(studySubjectOne);

        StudySubject studySubjectTwo = new StudySubject();
        studySubjectTwo.setPid("2");
        studySubjectTwo.setStudySubjectId("StudySubjectId2");
        queriedStudySubjects.add(studySubjectTwo);

        subject.setUniqueIdentifier("2");
        resultSubjects.add(subject);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);
        stagedSubjectPacsResultBuilder.getStagedSubjects();

        Subject resultSubject = stagedSubjectPacsResultBuilder.getStagedSubjects().get(0);
        assertEquals("StudySubjectId2", resultSubject.getStudySubjectId());
    }

    @Test
    public void migrates_secondary_id() {
        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        studySubjectOne.setSecondaryId("SecondaryId1");
        queriedStudySubjects.add(studySubjectOne);

        subject.setUniqueIdentifier("1");
        resultSubjects.add(subject);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);

        Subject resultSubject = stagedSubjectPacsResultBuilder.getStagedSubjects().get(0);
        assertEquals("SecondaryId1", resultSubject.getSecondaryId());
    }

    @Test
    public void migrates_gender() {
        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        studySubjectOne.setSex("Sex1");
        queriedStudySubjects.add(studySubjectOne);

        subject.setUniqueIdentifier("1");
        resultSubjects.add(subject);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);

        Subject resultSubject = stagedSubjectPacsResultBuilder.getStagedSubjects().get(0);
        assertEquals("Sex1", resultSubject.getGender());
    }

    @Test
    public void migrates_birthday() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.OC_DATEFORMAT);

        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        studySubjectOne.setDateOfBirth("1999-02-01");
        queriedStudySubjects.add(studySubjectOne);

        subject.setUniqueIdentifier("1");
        resultSubjects.add(subject);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);

        Subject resultSubject = stagedSubjectPacsResultBuilder.getStagedSubjects().get(0);
        assertEquals(sdf.parse("1999-02-01"), resultSubject.getDateOfBirth());
    }

    @Test
    public void migrates_year_of_birth() {
        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        studySubjectOne.setYearOfBirth(1999);
        queriedStudySubjects.add(studySubjectOne);

        subject.setUniqueIdentifier("1");
        resultSubjects.add(subject);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);

        Subject resultSubject = stagedSubjectPacsResultBuilder.getStagedSubjects().get(0);
        assertEquals(1999, resultSubject.getYearOfBirth());
    }

    @Test
    public void migrates_enrollment() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.OC_DATEFORMAT);

        StudySubject studySubjectOne = new StudySubject();
        studySubjectOne.setPid("1");
        studySubjectOne.setEnrollmentDate(sdf.parse("1964-02-05"));
        queriedStudySubjects.add(studySubjectOne);

        subject.setUniqueIdentifier("1");
        resultSubjects.add(subject);

        stagedSubjectPacsResultBuilder = StagedSubjectPacsResultBuilder.getInstance(queriedStudySubjects, resultSubjects);

        Subject resultSubject = stagedSubjectPacsResultBuilder.getStagedSubjects().get(0);
        assertEquals("1964-02-05", resultSubject.getEnrollmentDate().toString());
    }
    // endregion
}
