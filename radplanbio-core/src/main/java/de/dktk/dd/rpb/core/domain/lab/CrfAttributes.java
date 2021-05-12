package de.dktk.dd.rpb.core.domain.lab;

import de.dktk.dd.rpb.core.domain.edc.ItemData;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Helper for creating CrfAttributeTables for the LabKey export.
 */
public class CrfAttributes {

    private String studySubjectId;
    private double sequenceNumber;
    private String studyEventOid;
    private Integer eventRepeatKey;
    private String formOid;

    private List<ItemData> items;

    /**
     * Helper for creating CrfAttributeTables for the LabKey export.
     *
     * @param studySubjectId String Id of the study subject
     * @param studyEventOid  String Oid of the event that is represented by the table
     * @param eventRepeatKey Integer RepeatKey of the event
     * @param sequenceNumber Double SequenceNumber {Ordinal}.{RepeatKey}
     * @param formOid        String Oid of the form
     * @param items          List<ItemData> item data from Odm export
     */
    public CrfAttributes(String studySubjectId,
                         String studyEventOid,
                         Integer eventRepeatKey,
                         Double sequenceNumber,
                         String formOid,
                         List<ItemData> items) {
        this.studySubjectId = studySubjectId;
        this.studyEventOid = studyEventOid;

        this.eventRepeatKey = 0;
        if (eventRepeatKey != null) {
            this.eventRepeatKey = eventRepeatKey;
        }

        this.sequenceNumber = sequenceNumber;
        this.formOid = formOid;
        this.items = items;
    }

    public double getSequenceNumber() {
        return sequenceNumber;
    }

    public String getStudyEventOid() {
        return studyEventOid;
    }

    public Integer getEventRepeatKey() {
        return eventRepeatKey;
    }

    public String getFormOid() {
        return formOid;
    }

    public List<ItemData> getItems() {
        return items;
    }

    public CellProcessor[] getCellProcessors() {
        List<CellProcessor> cellProcessorList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            cellProcessorList.add(new Optional());
        }

        for (int i = 0; i < items.size(); i++) {
            cellProcessorList.add(new Optional());
        }

        CellProcessor[] array = new CellProcessor[cellProcessorList.size()];

        return cellProcessorList.toArray(array);
    }

    public List<Object> getValues() {
        List<Object> valueList = new ArrayList<>();

        valueList.add(valueOf(this.sequenceNumber));
        valueList.add(this.studySubjectId);
        valueList.add(this.studyEventOid);
        valueList.add(this.eventRepeatKey);
        valueList.add(this.formOid);

        for (ItemData item : items) {
            valueList.add(item.getValue());
        }

        return valueList;
    }

}
