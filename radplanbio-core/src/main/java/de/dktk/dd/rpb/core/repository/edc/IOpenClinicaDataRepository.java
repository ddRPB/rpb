/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

package de.dktk.dd.rpb.core.repository.edc;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.exception.DataBaseItemNotFoundException;
import org.openclinica.ws.beans.StudyType;

import java.util.List;

/**
 * OpenClinica Database Repository - operation layer abstraction
 *
 * @author tomas@skripcak.net
 * @since 05 Jun 2013
 */
public interface IOpenClinicaDataRepository {

    //region Methods

    //region UserAccount

    String getUserAccountHash(String username);

    //endregion

    //region Study

    Study getStudyById(String id);

    Study getStudyByIdentifier(String identifier);

    Study getUserActiveStudy(String username);

    boolean changeUserActiveStudy(String username, Study newActiveStudy);

    //endregion

    //region StudySubject

    StudySubject getStudySubjectByStudySubjectId(String studyIdentifier, String studySubjectId);

    StudySubject getStudySubjectWithEvents(String studyIdentifier, String studySubjectId);

    StudySubject getStudySubjectWithEventsWithForms(String studyIdentifier, String studySubjectId);

    List<StudySubject> findStudySubjectsByPseudonym(String pid, List<StudyType> studyTypeList);

    List<StudySubject> findStudySubjectsByStudy(String studyIdentifier);

    List<StudySubject> findStudySubjectsWithEvents(String studyIdentifier);

    List<StudySubject> findStudySubjectsOfChildrenStudiesWithEvents(String studyIdentifier);

    List<StudySubject> findStudySubjectsWithEventsAndTreatmentGroups(String studyIdentifier);

    List<StudySubject> findStudySubjectsOfChildrenStudiesWithEventsAndTreatmentGroups(String studyIdentifier);

    public int setPidOnExistingStudySubject(StudySubject studySubject, String ocUserName) throws DataBaseItemNotFoundException;

    int setSecondaryIdOnExistingStudySubject(StudySubject studySubject, String ocUserName) throws DataBaseItemNotFoundException;

    //endregion

    //region EventData

    boolean swapEventDataOrder(StudySubject studySubject, EventData eventData1, int originalEventRepeatKey);

    //endregion

    //region ItemData

    ItemData findItemData(String studyOid, String subjectPid, String studyEventOid, String studyEventRepeatKey, String formOid, String itemOid);

    List<ItemDefinition> getData(String uniqueIdentifier);

    List<DataQueryResult> getData(String uniqueIdentifier, List<ItemDefinition> query, Boolean decodeItemValues);

    //endregion

    //endregion
}
