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
 * EventDataMatch
 *
 * @author tomas@skripcak.net
 * @since 07 Jun 2016
 */
public class EventDataMatch implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(EventDataMatch.class);

    //endregion

    //region Members

    private String studyEventOid;
    private String studyEventRepeatKey;
    private Boolean match;
    private List<FormDataMatch> formDataMatchList;

    //endregion

    //region Constructors

    public EventDataMatch() {
        // NOOP
    }

    public EventDataMatch(EventData sourceEventData, EventData targetEventData) {
        this();

        if (sourceEventData.getStudyEventOid().equals(targetEventData.getStudyEventOid())) {

            this.studyEventOid = sourceEventData.getStudyEventOid();
            if (sourceEventData.getStudyEventRepeatKey() != null && targetEventData.getStudyEventRepeatKey() != null) {
                if (sourceEventData.getStudyEventRepeatKey().equals(targetEventData.getStudyEventRepeatKey())) {
                    this.studyEventRepeatKey = sourceEventData.getStudyEventRepeatKey();
                }
            }

            // Initial matching status is true e.g. if none of them have form data
            this.match = Boolean.TRUE;
            this.formDataMatchList = new ArrayList<>();

            // When both have forms, dive in to match from data
            if (sourceEventData.getFormDataList() != null && targetEventData.getFormDataList() != null) {

                for (FormData sfd : sourceEventData.getFormDataList()) {
                    for (FormData tfd : targetEventData.getFormDataList()) {
                        if (sfd.getFormOid().equals(tfd.getFormOid())) {

                            // Create a match object for form data - propagate the creation of rest down
                            this.getFormDataMatchList().add(
                                new FormDataMatch(sfd, tfd)
                            );

                            // Check if the last added form data matches
                            this.match = this.match && this.getFormDataMatchList().get(this.getFormDataMatchList().size() - 1).getMatch();

                            break;
                        }
                    }
                }
            }
            // They do not match if one has forms and the other one does not
            else if (sourceEventData.getFormDataList() == null && targetEventData.getFormDataList() != null) {
                this.match = Boolean.FALSE;
            }
            else if (sourceEventData.getFormDataList() != null && targetEventData.getFormDataList() == null) {
                this.match = Boolean.FALSE;
            }
        }
    }

    //endregion

    //region Properties

    //region StudyEventOID

    public String getStudyEventOid() {
        return this.studyEventOid;
    }

    public void setStudyEventOid(String value) {
        this.studyEventOid = value;
    }

    //endregion

    //region StudyEventRepeatKey

    public String getStudyEventRepeatKey() {
        return this.studyEventRepeatKey;
    }

    public void setStudyEventRepeatKey(String value) {
        this.studyEventRepeatKey = value;
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

    //region FormDataMatchList

    public List<FormDataMatch> getFormDataMatchList() {
        return formDataMatchList;
    }

    public void setFormDataMatchList(List<FormDataMatch> formDataMatchList) {
        this.formDataMatchList = formDataMatchList;
    }

    //endregion

    //endregion

}
