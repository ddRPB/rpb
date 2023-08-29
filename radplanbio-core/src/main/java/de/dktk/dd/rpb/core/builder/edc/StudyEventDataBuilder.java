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

package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for the StudyEventData object,
 * which can be used to create the StudyEventData part of an ODM-XML object.
 */
public class StudyEventDataBuilder {
    //region Finals

    private static final Logger log = LoggerFactory.getLogger(SubjectDataBuilder.class);
    private final EventData eventData = new EventData();

    // endregion

    // region constructor
    private StudyEventDataBuilder() {
    }

    /**
     * Returning a new StudyEventDataBuilder instance.
     *
     * @return StudyEventDataBuilder instance
     */
    public static StudyEventDataBuilder getInstance() {
        return new StudyEventDataBuilder();
    }

    //endregion

    /**
     * Builds the EventData instance out of the current configuration properties.
     *
     * @return EventData instance - similar to StudyEventData object in ODM-XML
     */
    public EventData build() throws MissingPropertyException {
        if (this.eventData.getStudyEventOid() == null || this.eventData.getStudyEventOid().isEmpty()) {
            throw new MissingPropertyException("StudyEventOid is missing.");
        }
        return this.eventData;
    }

    /**
     * Setting the StudyEventOid property for the EventData object.
     *
     * @param studyEventOid identifier for the EventData object
     * @return StudyEventDataBuilder
     */
    public StudyEventDataBuilder setStudyEventOid(String studyEventOid) {
        this.eventData.setStudyEventOid(studyEventOid);
        return this;
    }

    /**
     * Setting the StudyEventRepeatKey property for the EventData object.
     *
     * @param studyEventRepeatKey String identifier for repeating events
     *                            (in context with the StudyEventOid)
     * @return StudyEventDataBuilder
     */
    public StudyEventDataBuilder setStudyEventRepeatKey(String studyEventRepeatKey) {
        this.eventData.setStudyEventRepeatKey(studyEventRepeatKey);
        return this;
    }

    /**
     * Adding FormData to the EventData object
     *
     * @param formData FormData instance
     * @return StudyEventDataBuilder
     */
    public StudyEventDataBuilder addFormData(FormData formData) {
        boolean success = this.eventData.addFormData(formData);
        if (!success) {
            log.warn("There was a problem adding the FormData object to " +
                    "the EventData. FormData object: " + formData.toString());
        }
        return this;
    }
}
