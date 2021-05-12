package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.lab.EventAttributes;

public class StudyEventConverter {

    public static EventAttributes convertToLabkey(EventData eventData, String studySubjectId, Double seq) {
        EventAttributes eventAttributes = new EventAttributes();

        eventAttributes.setSequenceNum(seq);
        eventAttributes.setStudySubjectId(studySubjectId);
        eventAttributes.setStudyEventOid(eventData.getStudyEventOid());
        eventAttributes.setEventName(eventData.getEventName());
        eventAttributes.setStartDate(eventData.getComparableStartDate());
        eventAttributes.setEndDate(eventData.getComparableEndDate());
        eventAttributes.setStatus(eventData.getStatus());
        eventAttributes.setSystemStatus(eventData.getSystemStatus());
        eventAttributes.setStudyEventRepeatKey(eventData.getStudyEventRepeatKey());
        eventAttributes.setType(eventData.getEventDefinition().getType());

        return eventAttributes;
    }

    public static EventAttributes convertToLabkey(EventData eventData, String studySubjectId) {
        String repeatKey = eventData.getStudyEventRepeatKey();

        Integer ordinal = eventData.getEventDefinition().getOrdinal();
        boolean isRepeating = eventData.getEventDefinition().getIsRepeating();

        Double sequenceNumber;
        if (isRepeating) {
            sequenceNumber = calculateLabkeySequenceNumber(repeatKey, ordinal);

        } else {
            sequenceNumber = new Double(ordinal);
        }

        return StudyEventConverter.convertToLabkey(eventData, studySubjectId, sequenceNumber);
    }

    public static Double calculateLabkeySequenceNumber(String repeatKey, Integer ordinal) {
        Double sequenceNumber;
        if (repeatKey != null) {
            String formattedRepeatKey = String.format("%04d", Integer.parseInt(repeatKey));
            sequenceNumber = new Double(Integer.toString(ordinal) + "." + formattedRepeatKey);
        } else {
            sequenceNumber = new Double(ordinal);
        }
        return sequenceNumber;
    }
}
