package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.domain.lab.SubjectAttributes;

/**
 * Converts objects that represents a subject into different representations.
 */
public class SubjectConverter {

    /**
     * Converts a StudySubject into a LabKeySubject
     *
     * @param subject StudySubject
     * @param exportConfiguration LabKeyExportConfiguration
     * @return LabKeySubject
     */
    public static SubjectAttributes convertToLabKey(StudySubject subject, LabKeyExportConfiguration exportConfiguration) {
        SubjectAttributes labKeySubject = new SubjectAttributes();

        labKeySubject.setUniqueIdentifier(subject.getPid());
        labKeySubject.setStudySubjectId(subject.getStudySubjectId());
        labKeySubject.setSecondaryId(subject.getSecondaryId());

        if (exportConfiguration.isSexRequired()) {
            labKeySubject.setGender(subject.getSex());
        }

        if (exportConfiguration.isFullDateOfBirthRequired()) {
            labKeySubject.setDateOfBirth(subject.getComparableDateOfBirth());
        }

        if (exportConfiguration.isYearOfBirthRequired() && subject.getYearOfBirth() != null) {
            labKeySubject.setYearOfBirth(subject.getYearOfBirth());
        }

        labKeySubject.setEnrollmentDate(subject.getDateEnrollment());
        labKeySubject.setStatus(subject.getStatus());
        labKeySubject.setEnabled(subject.getIsEnabled());


        return labKeySubject;
    }
}
