package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.edc.SubjectGroupData;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.*;
import static java.lang.String.valueOf;

/**
 * Helper to create a tsv file for the LabKey export that will be used to create the SubjectGroupAttributes dataset.
 * The SubjectGroupAttributes table reflects the assignment of subjects to subject groups / cohorts.
 */
public class SubjectGroupTsvWriter {

    public static ByteArrayOutputStream getByteArrayOutputStream(List<StudySubject> studySubjects, String subjectColumnName) throws IOException {
        String[] headerArray = getHeaderArray(subjectColumnName);

        ICsvListWriter listWriter = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            listWriter = new CsvListWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8),
                    CsvPreference.TAB_PREFERENCE);

            listWriter.writeHeader(headerArray);

            for (StudySubject subject : studySubjects) {

                List<Object> valueList = null;
                if (subject.getSubjectGroupDataList().size() != 0) {
                    Double labkeySequenceNumber = new Double(0);

                    for (SubjectGroupData subjectGroupData:subject.getSubjectGroupDataList()) {
                        valueList = new ArrayList<>();

                        valueList.add(valueOf(labkeySequenceNumber));
                        valueList.add(subject.getStudySubjectId());
                        valueList.add(subject.getPid());


                        valueList.add(subjectGroupData.getStudyGroupClassID());
                        valueList.add(subjectGroupData.getStudyGroupClassName());
                        valueList.add(subjectGroupData.getStudyGroupName());

                        listWriter.write(valueList, getCellProcessors());
                    }
                }
            }
        } catch (IOException e) {
            String errorDescription = "There was a problem during writing SubjectGroup Attribute table.";
            throw new IOException(errorDescription, e);
        } finally {
            if (listWriter != null) {
                listWriter.close();
            }
        }

        return byteArrayOutputStream;
    }

    /**
     * Header for the SubjectGroupAttributes dataset tsv file
     * @param subjectColumnName name of the subject column configured for the study in LabKey
     * @return String[] Header
     */
    private static String[] getHeaderArray(String subjectColumnName) {
        List<String> headerList = new ArrayList<>();
        headerList.add(LABKEY_SEQUENCE_NUMBER);
        headerList.add(subjectColumnName);
        headerList.add(LABKEY_PATIENT_ID);
        headerList.add(LABKEY_STUDY_GROUP_CLASS_ID);
        headerList.add(LABKEY_STUDY_GROUP_CLASS_NAME);
        headerList.add(LABKEY_STUDY_GROUP_NAME);
        String[] headerArray = new String[headerList.size()];
        headerList.toArray(headerArray);
        return headerArray;
    }

    /**
     * Provides CellProcessor Array for the tsv writer, based on the header count
     * @return CellProcessor[]
     */
    public static CellProcessor[] getCellProcessors() {
        List<CellProcessor> cellProcessorList = new ArrayList<>();

        for (int i = 0; i < SubjectGroupTsvWriter.getHeaderArray("").length; i++) {
            cellProcessorList.add(new Optional());
        }
        CellProcessor[] array = new CellProcessor[cellProcessorList.size()];

        return cellProcessorList.toArray(array);
    }

}
