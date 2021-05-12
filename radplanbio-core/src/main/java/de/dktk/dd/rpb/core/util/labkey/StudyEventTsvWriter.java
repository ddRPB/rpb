package de.dktk.dd.rpb.core.util.labkey;

import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import de.dktk.dd.rpb.core.converter.StudyEventConverter;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.EventAttributes;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StudyEventTsvWriter {

    public static ByteArrayOutputStream getByteArrayOutputstream(List<StudySubject> studySubjectList) {
        TsvWriterSettings tsvWriterSettings = new TsvWriterSettings();
        tsvWriterSettings.setRowWriterProcessor(new BeanWriterProcessor<EventAttributes>(EventAttributes.class));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Writer outputWriter = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8);
        TsvWriter writer = new TsvWriter(outputWriter, tsvWriterSettings);

        writer.writeHeaders();

        for (de.dktk.dd.rpb.core.domain.edc.StudySubject subject : studySubjectList) {

            String studySubjectId = subject.getStudySubjectId();
            List<EventData> eventDataList = subject.getStudyEventDataList();
            if (eventDataList != null && eventDataList.size() > 0) {
                for (int i = 0; i < eventDataList.size(); i++) {
                    EventAttributes event = StudyEventConverter.convertToLabkey(eventDataList.get(i), studySubjectId);
                    writer.processRecord(event);
                }
            }

        }

        writer.close();
        return byteArrayOutputStream;
    }
}
