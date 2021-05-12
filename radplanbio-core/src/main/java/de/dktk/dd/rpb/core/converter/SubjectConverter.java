package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.SubjectAttributes;

/**
 * Converts objects that represents a subject into different representations.
 */
public class SubjectConverter {

    /**
     * Converts a StudySubject into a LabkeySubject
     *
     * @param subject StudySubject
     * @return LabkeySubject
     */
    public static SubjectAttributes convertToLabkey(StudySubject subject) {
        SubjectAttributes labkeySubject = new SubjectAttributes();

        labkeySubject.setUniqueIdentifier(subject.getPid());
        labkeySubject.setStudySubjectId(subject.getStudySubjectId());
        labkeySubject.setSecondaryId(subject.getSecondaryId());
        labkeySubject.setGender(subject.getSex());
        labkeySubject.setDateOfBirth(subject.getComparableDateOfBirth());

        if (subject.getYearOfBirth() != null) {
            labkeySubject.setYearOfBirth(subject.getYearOfBirth());
        }

        labkeySubject.setEnrollmentDate(subject.getDateEnrollment());
        labkeySubject.setStatus(subject.getStatus());
        labkeySubject.setEnabled(subject.getIsEnabled());


        return labkeySubject;
    }
}
