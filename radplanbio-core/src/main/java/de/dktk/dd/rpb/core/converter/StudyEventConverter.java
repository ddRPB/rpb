package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.lab.EventAttributes;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.labkey.SequenceNumberCalculator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StudyEventConverter {

    public static EventAttributes convertToLabkey(EventData eventData, String studySubjectId, Double seq) {
        EventAttributes eventAttributes = new EventAttributes();

        eventAttributes.setSequenceNum(seq);
        eventAttributes.setStudySubjectId(studySubjectId);
        eventAttributes.setStudyEventOid(eventData.getStudyEventOid());
        eventAttributes.setEventName(eventData.getEventName());
        eventAttributes.setStartDate(
                eventData.getStartDate() != null ? StudyEventConverter.parseOcTimeStringToDateString(eventData.getStartDate()) : null
        );
        eventAttributes.setEndDate(
                eventData.getEndDate() != null ? StudyEventConverter.parseOcTimeStringToDateString(eventData.getEndDate()) : null
        );
        eventAttributes.setStatus(eventData.getStatus());
        eventAttributes.setSystemStatus(eventData.getSystemStatus());
        eventAttributes.setStudyEventRepeatKey(eventData.getStudyEventRepeatKey());
        eventAttributes.setType(eventData.getEventDefinition().getType());

        return eventAttributes;
    }

    public static EventAttributes convertToLabkey(EventData eventData, String studySubjectId) throws MissingPropertyException {
        String repeatKey = eventData.getStudyEventRepeatKey();

        Integer ordinal = eventData.getEventDefinition().getOrdinal();
        boolean isRepeating = eventData.getEventDefinition().getIsRepeating();

        Double sequenceNumber = SequenceNumberCalculator.calculateLabKeySequenceNumber(isRepeating, repeatKey, ordinal);

        return StudyEventConverter.convertToLabkey(eventData, studySubjectId, sequenceNumber);
    }

    public static LocalDate parseOcTimeStringToDateString(String ocTimeStampString) {

        if (ocTimeStampString.isEmpty()) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.OC_TIMESTAMPFORMAT);

        return LocalDate.parse(ocTimeStampString, formatter);
    }

}
