/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 Tomas Skripcak
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

import de.dktk.dd.rpb.core.dao.edc.OpenClinicaDataDao;
import de.dktk.dd.rpb.core.domain.edc.*;

import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.openclinica.ws.beans.StudyType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of the {@link IOpenClinicaDataRepository} interface.
 * @see IOpenClinicaDataRepository
 *
 * OCDataRepository
 *
 * @author tomas@skripcak.net
 * @since 14 Jul 2013
 */
@Named("openClinicaDataRepository")
@Singleton
public class OpenClinicaDataRepository implements IOpenClinicaDataRepository {

    //region Logging

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(OpenClinicaDataRepository.class);

    //endregion

    //region Injects

    protected OpenClinicaDataDao dao;

    @Inject
    public void setOpenClinicaDataDao(OpenClinicaDataDao dao) {
        this.dao = dao;
    }

    //endregion

    //region Overrides

    //region UserAccount

    @Transactional
    public String getUserAccountHash(String username) {
        return this.dao.getUserAccountHash(username);
    }

    @Transactional
    public String getUserId(String username) {
        return this.dao.getUserId(username);
    }

    @Transactional
    public String getUserName(int id) {
        return this.dao.getUserName(id);
    }

    //endregion

    //region Study

    @Transactional
    public Study getStudyByIdentifier(String identifier) {
        return this.dao.getStudyByIdentifier(identifier);
    }

    @Transactional
    public Study getUserActiveStudy(String username) {
        return this.dao.getUserActiveStudy(username);
    }

    public boolean changeUserActiveStudy(String username, Study newActiveStudy) {
        return this.dao.changeUserActiveStudy(username, newActiveStudy);
    }

    //endregion

    //region StudySubject

    @Transactional
    public List<StudySubject> findStudySubjectsByPseudonym(String pid, List<StudyType> studyTypeList) {
        return this.dao.findStudySubjectsByPseudonym(pid, studyTypeList);
    }

    //endregion

    //region EventData

    @Transactional
    public boolean swapEventDataOrder(StudySubject studySubject, EventData eventData, int originalEventRepeatKey) {

        boolean result = false;

        // Fetch eventData from DB
        List<EventData> eventDataList = this.dao.getEventData(studySubject, eventData.getEventDefinition());

        // Find eventData that will be swapped from DB (they included primary keys IDs)
        EventData eventData1 = null;
        EventData eventData2 = null;

        // Figure out the latest eventData repeat key
        int max = 0;
        for (EventData ed : eventDataList) {

            // Free slot
            if (ed.getStudyEventRepeatKeyInteger() > max) {
                max = ed.getStudyEventRepeatKeyInteger();
            }
            // Original
            if (ed.getStudyEventRepeatKeyInteger() == originalEventRepeatKey) {
                eventData1 = ed;
            }
            // New
            if (ed.getStudyEventRepeatKey().equals(eventData.getStudyEventRepeatKey())) {
                eventData2 = ed;
            }
        }
        // Use the next unused occurrence
        max++;

        // Swap the order
        if (max > 0 && eventData1 != null && eventData2 != null) {
            result = this.dao.changeStudyEventRepeatKey(eventData1, max);
            result &= this.dao.changeStudyEventRepeatKey(eventData2, eventData1.getStudyEventRepeatKeyInteger());
            result &= this.dao.changeStudyEventRepeatKey(eventData1, eventData2.getStudyEventRepeatKeyInteger());
        }

        return result;
    }

    //endregion

    //region ItemData

    @Override
    @Transactional(readOnly = true)
    public List<ItemDefinition> getData(String uniqueIdentifier) {
        return this.dao.findAllItemDataForStudy(uniqueIdentifier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataQueryResult> getData(String uniqueIdentifier, List<ItemDefinition> query, Boolean decodeItemValues) {
        return this.dao.findItemData(uniqueIdentifier, query, decodeItemValues);
    }

    //endregion

    //endregion

}
