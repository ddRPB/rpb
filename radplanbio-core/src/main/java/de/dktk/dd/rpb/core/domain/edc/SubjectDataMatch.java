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

package de.dktk.dd.rpb.core.domain.edc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SubjectDataMatch
 *
 * @author tomas@skripcak.net
 * @since 07 Jun 2016
 */
public class SubjectDataMatch implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SubjectDataMatch.class);

    //endregion

    //region Members

    private String subjectKey;
    private Boolean match;
    private List<EventDataMatch> eventDataMatchList;

    //endregion

    //region Constructors

    public SubjectDataMatch() {
        // NOOP
    }

    public SubjectDataMatch(StudySubject sourceStudySubject, StudySubject targetStudySubject) {
        this();

        if (sourceStudySubject.getSubjectKey().equals(targetStudySubject.getSubjectKey())) {
            this.subjectKey = sourceStudySubject.getSubjectKey();
            this.eventDataMatchList = new ArrayList<>();
            this.match = Boolean.TRUE;

            if (sourceStudySubject.getStudyEventDataList() != null && targetStudySubject.getStudyEventDataList() != null) {
                for (EventData sed : sourceStudySubject.getEventOccurrences()) {
                    for (EventData ted : targetStudySubject.getEventOccurrences()) {
                        if (sed.getStudyEventOid().equals(ted.getStudyEventOid())) {
                            if (sed.getStudyEventRepeatKey() != null && ted.getStudyEventRepeatKey() != null) {
                                if (sed.getStudyEventRepeatKey().equals(ted.getStudyEventRepeatKey())) {
                                    // Create a match object for event data - propagate the creation of rest down
                                    this.getEventDataMatchList().add(
                                            new EventDataMatch(sed, ted)
                                    );

                                    // Check if the last added event data matches
                                    this.match = this.match && this.getEventDataMatchList().get(this.getEventDataMatchList().size() - 1).getMatch();

                                    break;
                                }
                            }
                            else {

                                // Create a match object for event data - propagate the creation of rest down
                                this.getEventDataMatchList().add(
                                        new EventDataMatch(sed, ted)
                                );

                                // Check if the last added event data matches
                                this.match = this.match && this.getEventDataMatchList().get(this.getEventDataMatchList().size() - 1).getMatch();

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    //endregion

    //region Properties

    //region SubjectKey

    public String getSubjectKey() {
        return this.subjectKey;
    }

    public void setSubjectKey(String value) {
        this.subjectKey = value;
    }

    //endregion

    //region Match

    public Boolean getMatch() {
        return match;
    }

    public void setMatch(Boolean value) {
        this.match = value;
    }

    //endregion

    //region EventDataMatchList

    public List<EventDataMatch> getEventDataMatchList() {
        return eventDataMatchList;
    }

    public void setEventDataMatchList(List<EventDataMatch> eventDataMatchList) {
        this.eventDataMatchList = eventDataMatchList;
    }

    //endregion

    //endregion

}
