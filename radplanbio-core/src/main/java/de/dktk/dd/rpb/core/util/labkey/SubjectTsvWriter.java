/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
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

package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.converter.SubjectConverter;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.lab.LabKeyExportConfiguration;
import de.dktk.dd.rpb.core.domain.lab.SubjectAttributes;
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

/**
 * Creates the tsv files for the SubjectsAttributesTable in the LabKey Export
 */
public class SubjectTsvWriter {

    public static ByteArrayOutputStream getByteArrayOutputStream(List<StudySubject> studySubjects, String subjectColumnName, LabKeyExportConfiguration exportConfiguration) throws IOException {
        String[] headerArray = getHeaderArray(subjectColumnName, exportConfiguration);

        ICsvListWriter listWriter = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            listWriter = new CsvListWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8),
                    CsvPreference.TAB_PREFERENCE);

            listWriter.writeHeader(headerArray);

            for (StudySubject subject : studySubjects) {
                SubjectAttributes labkeySubject = SubjectConverter.convertToLabKey(subject, exportConfiguration);
                if (labkeySubject.getUniqueIdentifier() != null && !labkeySubject.getUniqueIdentifier().isEmpty()) {

                    listWriter.write(labkeySubject.getValues(), labkeySubject.getCellProcessors());
                }
            }
        } catch (IOException e) {
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

    private static String[] getHeaderArray(String subjectColumnName, LabKeyExportConfiguration exportConfiguration) {
        List<String> headerList = new ArrayList<>();
        headerList.add(LABKEY_SEQUENCE_NUMBER);
        headerList.add(subjectColumnName);
        headerList.add(LABKEY_PATIENT_ID);
        headerList.add(LABKEY_SECONDARY_ID);

        if (exportConfiguration.isSexRequired()) {
            headerList.add(LABKEY_GENDER);
        }

        if (exportConfiguration.isFullDateOfBirthRequired()) {
            headerList.add(LABKEY_BIRTHDATE);
        }

        if (exportConfiguration.isYearOfBirthRequired()) {
            headerList.add(LABKEY_BIRTHYEAR);
        }

        headerList.add(LABKEY_ENROLLMENT);
        headerList.add(LABKEY_STATUS);
        String[] headerArray = new String[headerList.size()];
        headerList.toArray(headerArray);
        return headerArray;
    }

}
