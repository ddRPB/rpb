package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.SubjectAttributes;
import de.dktk.dd.rpb.core.util.Constants;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class SubjectConverterTest {

    // region StudySubject
    @Test
    public void coverts_a_plain_object() {
        StudySubject studySubject = new StudySubject();
        SubjectAttributes labkeySubject = SubjectConverter.convertToLabkey(studySubject);

        assertNotNull(labkeySubject);
    }

    @Test
    public void converts_an_object_with_all_properties() throws ParseException {
        DateFormat format = new SimpleDateFormat(Constants.OC_DATEFORMAT);

        String pid = "dummy_pid";
        int id = 123;
        String studySubjectId = "dummy_study_subject";
        String secondaryId = "dummy_secondary_id";
        String sex = "d";

        String birthDayString = "1900-12-31";
        Date birthday = format.parse(birthDayString);

        Integer birthYear = 1900;

        String enrollmentDateString = "2020-11-25";
        Date enrollmentDate = format.parse(enrollmentDateString);

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

        SubjectAttributes labkeySubject = SubjectConverter.convertToLabkey(subject);

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