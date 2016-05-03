/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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

import org.apache.log4j.Logger;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * EventData domain entity (based on CDISC ODM StudyEventData)
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="StudyEventData")
public class EventData implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(EventData.class);

    //endregion

    //region Members

    @XmlAttribute(name="StudyEventOID")
    private String studyEventOid;

    @XmlAttribute(name="StudyEventRepeatKey")
    private String studyEventRepeatKey;

    @XmlElement(name="FormData")
    private List<FormData> formDataList;

    @XmlAttribute(name="StartDate", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1-api")
    private String startDate;

    @XmlAttribute(name="EventName", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1-api")
    private String eventName;

    //endregion

    //region Constructors

    public EventData() {
        // NOOP
    }

    public EventData(String studyEventOid) {
        this.studyEventOid = studyEventOid;
    }

    public EventData(String studyEventOid, String studyEventRepeatKey) {
        this.studyEventOid = studyEventOid;
        this.studyEventRepeatKey = studyEventRepeatKey;
    }

    //endregion

    //region Properties

    public String getStudyEventOid() {
        return this.studyEventOid;
    }

    public void setStudyEventOid(String value) {
        this.studyEventOid = value;
    }

    public String getStudyEventRepeatKey() {
        return this.studyEventRepeatKey;
    }

    public void setStudyEventRepeatKey(String value) {
        this.studyEventRepeatKey = value;
    }

    public List<FormData> getFormDataList() {
        return this.formDataList;
    }

    public void setFormDataList(List<FormData> list) {
        this.formDataList = list;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String value) {
        this.startDate = value;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventName(String value) {
        this.eventName = value;
    }

    //endregion

    //region Methods


    //endregion

}
