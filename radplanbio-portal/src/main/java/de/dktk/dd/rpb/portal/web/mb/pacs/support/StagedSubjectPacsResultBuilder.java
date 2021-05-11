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

package de.dktk.dd.rpb.portal.web.mb.pacs.support;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.util.Constants;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Helps to organize the post processing for PACS query results
 */
public class StagedSubjectPacsResultBuilder {

    private static final Logger log = Logger.getLogger(StagedSubjectPacsResultBuilder.class);
    private List<StudySubject> queriedStudySubjects;
    private List<Subject> resultSubjects;
    private List<StagedSubject> stagedSubjectsList;

    private StagedSubjectPacsResultBuilder(List<StudySubject> queriedStudySubjects, List<Subject> resultSubjects) {
        this.queriedStudySubjects = queriedStudySubjects;
        this.resultSubjects = resultSubjects;
        this.stagedSubjectsList = new ArrayList<>();
    }

    /**
     * Creates an instance of a SubjectPacsResultBuilder
     *
     * @param queriedStudySubjects List<StudySubject> List of queried StudySubjects
     * @param resultSubjects       List<Subject> results from the PACS query - could include data from patients that are
     *                             actually not within the study - includes only subject where studies are staged in PACS
     * @return PacsResultBuilder
     */
    public static StagedSubjectPacsResultBuilder getInstance(List<StudySubject> queriedStudySubjects, List<Subject> resultSubjects) {
        return new StagedSubjectPacsResultBuilder(queriedStudySubjects, resultSubjects);
    }

    /**
     * Getter for the list of results
     *
     * @return List<Subject>
     */
    public List<Subject> getResultSubjects() {
        return resultSubjects;
    }

    /**
     * Getter for the list of results
     *
     * @return List<StagedSubject> List of StagedSubject that is build out of the arguments provided within the constructor.
     */
    public List<StagedSubject> getStagedSubjects() {
        this.migrateInformationFromStudySubjectsToSubjects();
        return this.stagedSubjectsList;
    }

    private void migrateInformationFromStudySubjectsToSubjects() {
        for (Subject subject : resultSubjects) {
            try {
                StagedSubject stagedSubject = new StagedSubject();
                migrateInformationFromSubject(subject, stagedSubject);
                migrateInformationFromStudySubjects(stagedSubject);
                this.stagedSubjectsList.add(stagedSubject);
            } catch (NullPointerException e) {
                String warnMessage = "There was a problem migrating information for these subject " + subject.toString() +
                        ". It is likely that the uniqueIdentifier is missing or the queriedStudySubjects consist an" +
                        " object where the pid is null";
                log.warn(warnMessage);
                log.debug(ExceptionUtils.getFullStackTrace(e));
            }
        }
    }

    private void migrateInformationFromSubject(Subject subject, StagedSubject stagedSubject) throws NullPointerException {
        stagedSubject.setUniqueIdentifier(subject.getUniqueIdentifier());
        stagedSubject.setDicomStudyList(subject.getDicomStudyList());
    }

    private void migrateInformationFromStudySubjects(Subject subject) throws NullPointerException {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.OC_DATEFORMAT);
        StudySubject studysubject = getStudySubjectBySubjectUniqueIdentifier(subject);
        if (studysubject != null) {
            migrateStudySubjectIdFromStudySubjectToSubject(subject, studysubject);
            migrateSecondaryIdFromStudySubjectToSubject(subject, studysubject);
            migrateSexFromStudySubjectToSubject(subject, studysubject);
            migrateBirthDateFromStudySubjectToSubject(subject, studysubject, sdf);
            migrateYearOfBirthFromStudySubjectToSubject(subject, studysubject);
            migrateEnrollmentDateFromStudySubjectToSubject(subject, studysubject, sdf);
        }
    }

    private StudySubject getStudySubjectBySubjectUniqueIdentifier(Subject subject) {
        StudySubject studysubject;
        if (subject.getUniqueIdentifier() != null) {
            studysubject = getFirstMatchingStudySubjectByUniqueIdentifier(subject.getUniqueIdentifier());
        } else {
            throw new NullPointerException("Subject object needs to have an UniqueIdentifier");
        }
        return studysubject;
    }

    private void migrateStudySubjectIdFromStudySubjectToSubject(Subject subject, StudySubject studysubject) {
        if (studysubject.getStudySubjectId() != null) {
            subject.setStudySubjectId(studysubject.getStudySubjectId());
        }
    }

    private void migrateSecondaryIdFromStudySubjectToSubject(Subject subject, StudySubject studysubject) {
        if (studysubject.getSecondaryId() != null) {
            subject.setSecondaryId(studysubject.getSecondaryId());
        }
    }

    private void migrateSexFromStudySubjectToSubject(Subject subject, StudySubject studysubject) {
        if (studysubject.getSex() != null) {
            subject.setGender(studysubject.getSex());
        }
    }

    private void migrateBirthDateFromStudySubjectToSubject(Subject subject, StudySubject studysubject, SimpleDateFormat sdf) {
        if (studysubject.getDateOfBirth() != null) {
            try {
                subject.setDateOfBirth(sdf.parse(studysubject.getDateOfBirth()));
            } catch (ParseException e) {
                log.warn("There was a problem parsing the birth date " + studysubject.getDateOfBirth());
                log.debug(ExceptionUtils.getFullStackTrace(e));
            }
        }
    }

    private void migrateYearOfBirthFromStudySubjectToSubject(Subject subject, StudySubject studysubject) {
        if (studysubject.getYearOfBirth() != null) {
            subject.setYearOfBirth(studysubject.getYearOfBirth());
        }
    }

    private void migrateEnrollmentDateFromStudySubjectToSubject(Subject subject, StudySubject studysubject, SimpleDateFormat sdf) {
        if (studysubject.getEnrollmentDate() != null) {
            try {
                subject.setEnrollmentDate(dateToXMLGregorianCalendar(sdf.parse(studysubject.getEnrollmentDate())));
            } catch (ParseException | DatatypeConfigurationException e) {
                log.warn("There was a problem parsing or converting the enrollment date " + studysubject.getEnrollmentDate())
                ;
                log.debug(ExceptionUtils.getFullStackTrace(e));
            }
        }
    }

    private StudySubject getFirstMatchingStudySubjectByUniqueIdentifier(String uniqueIdentifier) {
        for (StudySubject studySubject : queriedStudySubjects) {
            if (uniqueIdentifier.equals(studySubject.getPid())) {
                return studySubject;
            }
        }
        return null;
    }

    private static XMLGregorianCalendar dateToXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
        GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
        gc.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    }

    /**
     * Filters subjects that are actually not part of the query
     */
    public StagedSubjectPacsResultBuilder filterResultsByStudySubjectList() {
        if (queriedStudySubjects.size() > 0 && resultSubjects.size() > 0) {
            List<Subject> filteredSubjects = new ArrayList<>();
            for (Subject subject : this.resultSubjects) {
                try {
                    String pid = subject.getUniqueIdentifier();
                    if (getFirstMatchingStudySubjectByUniqueIdentifier(pid) != null) {
                        filteredSubjects.add(subject);
                    }
                } catch (NullPointerException e) {
                    log.warn("There was a problem filtering the subject " + subject.toString() +
                            ". It is likely that the uniqueIdentifier is missing or the queriedStudySubjects consists" +
                            " an object where the pid is null");
                    log.debug(ExceptionUtils.getFullStackTrace(e));
                }
            }
            this.resultSubjects = filteredSubjects;
        }
        return this;
    }

    /**
     * Adds Subject objects with appropriate UniqueIdentifier if the subject was part of the query
     * (queriedStudySubjects), but had not results in the query.
     */
    public StagedSubjectPacsResultBuilder addSubjectsWithoutResultsFromStudySubjectList() {
        if (queriedStudySubjects.size() > 0) {
            for (StudySubject studySubject : queriedStudySubjects) {
                try {
                    String uniqueIdentifier = studySubject.getPid();
                    if (getFirstMatchingSubjectByPid(uniqueIdentifier) == null) {
                        addSubjectWithUniqueIdentifierToResultList(uniqueIdentifier);
                    }
                } catch (NullPointerException e) {
                    log.warn("There was a problem to synchronize " + studySubject.toString() +
                            ". It is likely that the uniqueIdentifier is missing or the queriedStudySubjects consist an object where the pid is null");
                    log.debug(ExceptionUtils.getFullStackTrace(e));
                }
            }
        }
        return this;
    }

    private Subject getFirstMatchingSubjectByPid(String pid) {
        for (Subject subject : resultSubjects) {
            if (pid.equals(subject.getUniqueIdentifier())) {
                return subject;
            }
        }
        return null;
    }

    private void addSubjectWithUniqueIdentifierToResultList(String uniqueIdentifier) {
        Subject newSubject = new Subject();
        newSubject.setUniqueIdentifier(uniqueIdentifier);
        this.resultSubjects.add(newSubject);
    }
}