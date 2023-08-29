/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2021 RPB Team
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

package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.lab.FormAttributes;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.labkey.OdmEventMetaDataLookup;
import de.dktk.dd.rpb.core.util.labkey.SequenceNumberCalculator;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static de.dktk.dd.rpb.core.util.Constants.LABKEY_DEFAULT_REPEAT_KEY;

/**
 * Convertor for transforming EDC ODM FormData into LAB FormAttributes
 */
public class FormDataConverter {

    //region Members

    private String studySubjectId;
    private boolean isRepeating;
    private String studyEventOid;
    private String studyEventRepeatKey;
    private Integer studyEventOrdinal;
    private Double labKeySequenceNumber;

    //endregion

    //region Constructors

    /**
     * Helper class to convert FormData that are created from an ODM export.
     *
     * @param studySubjectId String StudySubjectId
     * @param eventData EventData from the ODM export
     * @param odmEventMetaDataLookup OdmEventMetaDataLookup that is created out of the ODM metadata
     * @throws MissingPropertyException
     */
    public FormDataConverter(String studySubjectId,
                             EventData eventData,
                             OdmEventMetaDataLookup odmEventMetaDataLookup) throws MissingPropertyException {
        
        this.studySubjectId = studySubjectId;
        this.verifyStudySubjectId();

        if (eventData == null)  {
            throw new MissingPropertyException("eventData argument is null");
        }

        this.studyEventOid = eventData.getStudyEventOid();
        this.studyEventRepeatKey = eventData.getStudyEventRepeatKey();

        if(this.studyEventRepeatKey == null){
            // Set default value
            this.studyEventRepeatKey = String.valueOf(LABKEY_DEFAULT_REPEAT_KEY);
        }

        if (odmEventMetaDataLookup == null) {
            throw new MissingPropertyException("odmEventMetaDataLookup is null");
        }

        this.isRepeating = odmEventMetaDataLookup.getStudyEventIsRepeating(studyEventOid);
        this.studyEventOrdinal = odmEventMetaDataLookup.getStudyEventOrdinal(studyEventOid);

        this.labKeySequenceNumber = SequenceNumberCalculator
                .calculateLabKeySequenceNumber(isRepeating, studyEventRepeatKey, studyEventOrdinal);
    }

    //endregion

    //region Methods

    /**
     * Converts an EDC FormData object (from the ODM export) to an LAB FormAttributes object (for the LabKey export)
     * 
     * @param formData FormData from the ODM export
     * @return LAB FormAttributes
     * @throws ParseException
     */
    public FormAttributes convertToFormAttributes(FormData formData) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.OC_DATEFORMAT);

        FormAttributes formAttributes = new FormAttributes();

        formAttributes.setSequenceNum(this.labKeySequenceNumber);
        formAttributes.setStudySubjectId(this.studySubjectId);
        formAttributes.setStudyEventOid(this.studyEventOid);
        formAttributes.setStudyEventRepeatKey(this.studyEventRepeatKey);

        formAttributes.setFormOid(formData.getFormOid());
        formAttributes.setFormVersion(formData.getVersion());
        formAttributes.setInterviewerName(formData.getInterviewerName());
        formAttributes.setInterviewDate(formData.getInterviewDate() != null ? LocalDate.parse(formData.getInterviewDate(), formatter) : null);

        formAttributes.setStatus(formData.getStatus());

        return formAttributes;
    }

    //endregion

    //region Private

    private void verifyStudySubjectId() throws MissingPropertyException {

        if (this.studySubjectId == null) {
            throw new MissingPropertyException("StudySubjectId is missing");
        }

        if (this.studySubjectId.isEmpty()) {
            throw new MissingPropertyException("StudySubjectId is empty");
        }
    }

    //endregion
    
}
