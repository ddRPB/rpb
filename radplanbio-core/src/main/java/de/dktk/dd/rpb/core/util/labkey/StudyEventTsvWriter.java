package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.converter.StudyEventConverter;
import de.dktk.dd.rpb.core.domain.edc.EventData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.EventAttributes;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StudyEventTsvWriter {

    public static ByteArrayOutputStream getByteArrayOutputstream(List<StudySubject> studySubjectList, String subjectColumnName) throws IOException {

        String[] headerArray = EventAttributes.getHeaders(subjectColumnName);
        CellProcessor[] cellProcessors = EventAttributes.getCellProcessors();

        ICsvListWriter listWriter = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        listWriter = new CsvListWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8),
                CsvPreference.TAB_PREFERENCE);

        try {
            listWriter.writeHeader(headerArray);

            for (StudySubject subject : studySubjectList) {

                String studySubjectId = subject.getStudySubjectId();
                List<EventData> eventDataList = subject.getStudyEventDataList();
                if (eventDataList != null && eventDataList.size() > 0) {
                    for (int i = 0; i < eventDataList.size(); i++) {
                        EventAttributes event = StudyEventConverter.convertToLabkey(eventDataList.get(i), studySubjectId);
                        listWriter.write(event.getValues(), cellProcessors);
                    }
                }

            }

        } catch (IOException | MissingPropertyException e) {
            String errorDescription = "There was a problem during writing Subject Attribute table.";
//            this.log.debug(errorDescription, e);
            throw new IOException(errorDescription, e);
        } finally {
            if (listWriter != null) {
                listWriter.close();
            }
        }

        return byteArrayOutputStream;
    }

}
