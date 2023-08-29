package de.dktk.dd.rpb.core.domain.lab;

import de.dktk.dd.rpb.core.util.Constants;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SubjectAttributesTest {

    private final Integer id = 1;
    private final String studySubjectId = "studySubjectId";
    private final String secondaryId = "secondaryId";
    private final String uniqueIdentifier = "uniqueIdentifier";
    private final String gender = "f";
    private final LocalDate dateOfBirth = LocalDate.parse("1900-01-20", DateTimeFormatter.ofPattern(Constants.OC_DATEFORMAT));
    private final int yearOfBirth = 1900;
    private final LocalDate enrollmentDate = LocalDate.parse("2000-12-20", DateTimeFormatter.ofPattern(Constants.OC_DATEFORMAT));
    private final String status = "status";


    public SubjectAttributesTest() throws ParseException { }

    @Test
    public void getCellProcessors_returns_correct_items() {
        SubjectAttributes subject = getDummySubject();
        CellProcessor[] cellProcessors = subject.getCellProcessors();
        assertNotNull(cellProcessors);
        assertEquals(9, cellProcessors.length);
    }

    @Test
    public void getValues_returns_correct_items() {

        SubjectAttributes subject = getDummySubject();
        List<Object> values = subject.getValues();

        assertNotNull(values);
        assertEquals(9, values.size());
        values.get(1).equals(subject.getSequenceNum());
        values.get(0).equals(subject.getStudySubjectId());
        values.get(2).equals(subject.getUniqueIdentifier());
        values.get(3).equals(subject.getSecondaryId());
        values.get(4).equals(subject.getGender());
        values.get(5).equals(subject.getDateOfBirth());
        values.get(6).equals(subject.getYearOfBirth());
        values.get(7).equals(subject.getEnrollmentDate());
        values.get(8).equals(subject.getStatus());

    }

    private SubjectAttributes getDummySubject() {
        SubjectAttributes subjectOne = new SubjectAttributes();
        subjectOne.setStudySubjectId(studySubjectId);
        subjectOne.setSecondaryId(secondaryId);
        subjectOne.setUniqueIdentifier(uniqueIdentifier);
        subjectOne.setGender(gender);
        subjectOne.setDateOfBirth(dateOfBirth);
        subjectOne.setYearOfBirth(yearOfBirth);
        subjectOne.setEnrollmentDate(enrollmentDate);
        subjectOne.setStatus(status);
        return subjectOne;
    }
}