package de.dktk.dd.rpb.core.domain.lab;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import org.junit.Test;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class SubjectAttributesTest {

    private final Integer id = 1;
    private final String studySubjectId = "studySubjectId";
    private final String secondaryId = "secondaryId";
    private final String uniqueIdentifier = "uniqueIdentifier";
    private final String gender = "f";
    private final Date dateOfBirth = new SimpleDateFormat("YYYY-MM-dd").parse("1900-01-20");
    private final int yearOfBirth = 1900;
    private final Date enrollmentDate = new SimpleDateFormat("YYYY-MM-dd").parse("2000-12-20");
    private final String status = "status";
    private final Boolean isEnabled = true;

    private final String filePath = "./src/test/data/labkey/labkey_subject_attributes.tsv";


    public SubjectAttributesTest() throws ParseException {
    }

    @Test
    public void create_and_read_Tsv_file() throws FileNotFoundException {

        createTsvFile();
        List<SubjectAttributes> beans = getSubjectAttributesFromFile();
        SubjectAttributes subject = beans.get(0);

        assertThat(subject, samePropertyValuesAs(this.getDummySubject()));

    }

    private List<SubjectAttributes> getSubjectAttributesFromFile() {
        BeanListProcessor<SubjectAttributes> rowProcessor = new BeanListProcessor<SubjectAttributes>(SubjectAttributes.class);

        TsvParserSettings parserSettings = new TsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        TsvParser parser = new TsvParser(parserSettings);
        parser.parse(new File(filePath));

        return rowProcessor.getBeans();
    }

    private void createTsvFile() throws FileNotFoundException {
        TsvWriterSettings tsvWriterSettings = new TsvWriterSettings();
        tsvWriterSettings.setRowWriterProcessor(new BeanWriterProcessor<SubjectAttributes>(SubjectAttributes.class));

        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        Writer outputWriter = new OutputStreamWriter(fileOutputStream);
        TsvWriter writer = new TsvWriter(outputWriter, tsvWriterSettings);

        writer.writeHeaders();

        SubjectAttributes subjectOne = getDummySubject();
        writer.processRecord(subjectOne);

        writer.close();
    }

    private SubjectAttributes getDummySubject() {
        SubjectAttributes subjectOne = new SubjectAttributes();
        subjectOne.setStudySubjectId(studySubjectId);
        subjectOne.setSecondaryId(secondaryId);
        subjectOne.setUniqueIdentifier(uniqueIdentifier);
        subjectOne.setGender(gender);
        subjectOne.setDateOfBirth(dateOfBirth);
        subjectOne.setEnrollmentDate(enrollmentDate);
        subjectOne.setStatus(status);
        return subjectOne;
    }
}