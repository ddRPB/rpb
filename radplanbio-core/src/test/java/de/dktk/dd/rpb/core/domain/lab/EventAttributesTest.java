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

public class EventAttributesTest {

    private final Double sequenceNum = new Double(1);
    private final String studySubjectId = "studySubjectId";
    private final Integer id = 11;
    private final String studyEventOid = "Study Event Oid";
    private final String eventName = "Event Name";
    private final Date startDate = new SimpleDateFormat("YYYY-MM-dd").parse("2020-01-19");
    private final Date endDate = new SimpleDateFormat("YYYY-MM-dd").parse("2020-01-25");
    private final String status = "status";
    private final String studyEventRepeatKey = "Study Event Repeat Key";

    private final String filePath = "src/test/data/labkey/labkey_event_data.tsv";

    public EventAttributesTest() throws ParseException {
    }

    @Test
    public void create_read_tsv_file() throws FileNotFoundException {
        createTsvFile();

        List<EventAttributes> beans = getEventAttributesFromFile();
        EventAttributes eventData = beans.get(0);

        assertThat(eventData, samePropertyValuesAs(this.getLabkeyDummyEvent()));
    }

    private void createTsvFile() throws FileNotFoundException {
        TsvWriterSettings tsvWriterSettings = new TsvWriterSettings();
        tsvWriterSettings.setRowWriterProcessor(new BeanWriterProcessor<EventAttributes>(EventAttributes.class));

        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        Writer outputWriter = new OutputStreamWriter(fileOutputStream);
        TsvWriter writer = new TsvWriter(outputWriter, tsvWriterSettings);

        writer.writeHeaders();

        EventAttributes event = getLabkeyDummyEvent();
        writer.processRecord(event);

        writer.close();
    }

    private List<EventAttributes> getEventAttributesFromFile() {
        BeanListProcessor<EventAttributes> rowProcessor = new BeanListProcessor<EventAttributes>(EventAttributes.class);

        TsvParserSettings parserSettings = new TsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        TsvParser parser = new TsvParser(parserSettings);
        parser.parse(new File(filePath));

        return rowProcessor.getBeans();
    }

    private EventAttributes getLabkeyDummyEvent() {
        EventAttributes event = new EventAttributes();
        event.setSequenceNum(sequenceNum);
        event.setStudySubjectId(studySubjectId);
        event.setStudyEventOid(studyEventOid);
        event.setEventName(eventName);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setStatus(status);
        event.setStudyEventRepeatKey(studyEventRepeatKey);
        return event;
    }
}