package de.dktk.dd.rpb.core.util.labkey;

import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import de.dktk.dd.rpb.core.converter.SubjectConverter;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.SubjectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubjectTsvWriter {

    public static ByteArrayOutputStream getByteArrayOutputStream(List<StudySubject> studySubjects) {
        TsvWriterSettings tsvWriterSettings = new TsvWriterSettings();
        tsvWriterSettings.setRowWriterProcessor(new BeanWriterProcessor<SubjectAttributes>(SubjectAttributes.class));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Writer outputWriter = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8);
        TsvWriter writer = new TsvWriter(outputWriter, tsvWriterSettings);

        writer.writeHeaders();

        for (de.dktk.dd.rpb.core.domain.edc.StudySubject subject : studySubjects) {
            SubjectAttributes labkeySubject = SubjectConverter.convertToLabkey(subject);
            if (labkeySubject.getUniqueIdentifier() != null && !labkeySubject.getUniqueIdentifier().isEmpty()) {
                writer.processRecord(labkeySubject);
            }
        }
        writer.close();
        return byteArrayOutputStream;
    }
}
