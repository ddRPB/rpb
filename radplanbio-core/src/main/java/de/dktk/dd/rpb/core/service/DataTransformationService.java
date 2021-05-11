/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2017 Tomas Skripcak
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

package de.dktk.dd.rpb.core.service;

import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.domain.edc.mapping.*;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.FileUtil;

import org.apache.log4j.Logger;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Data transformation service
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@Named("dataTransformationService")
public class DataTransformationService {

    //region Finals

    private static final Logger log = Logger.getLogger(DataTransformationService.class);

    //endregion

    //region Members

    protected FileUtil fileUtil;
    private ICsvListReader reader;

    //endregion

    //region Constructors

    @Inject
    public DataTransformationService(FileUtil fileUtil) {

        this.fileUtil = fileUtil;
        this.reader = null;
    }

    //endregion

    //region Methods
    
    public Odm transformToOdm(Odm metadata, Mapping map, InputStream input, String filename) {
        Odm result = null;

        // Depending on the file extension I have to decide how to process it
        String extension = this.fileUtil.getExtension(filename);
        if (extension.equalsIgnoreCase(".xml")) {
            result = this.transformXmlToOdm(metadata, input);
        }
        else if (extension.equalsIgnoreCase(".csv")) {
            result = this.transformCsvToOdm(metadata, input, map.getMappingRecords());
        }

        if (result != null) {
            result.sortItemGroupData();
        }

        return result;
    }

    public String transformOdmToString(Odm odm) {
        String result = "";
        try {
            StringWriter sw = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(Odm.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            m.marshal(odm, sw);
            result = sw.toString();
        }
        catch (Exception err) {
            // NOOP
        }

        return result;
    }

    public File transformOdmToXmlFile(Odm odm, String fileName) {
        File odmFile = null;

        try {
            odmFile = new File(fileName);
            JAXBContext context = JAXBContext.newInstance(Odm.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            m.marshal(odm, odmFile);
        }
        catch (Exception err) {
            // NOOP
        }

        return odmFile;
    }

    public Odm transformXmlToOdm(InputStream input) {
        Odm result = null;

        try {
            JAXBContext context = JAXBContext.newInstance(Odm.class);
            Unmarshaller un = context.createUnmarshaller();
            result = (Odm) un.unmarshal(input);
        }
        catch (Exception err) {
            err.printStackTrace();
        }

        return result;
    }

    public List<AbstractMappedItem> extractMappedDataItemDefinitions(InputStream input, String filename) {
        List<AbstractMappedItem> result = new ArrayList<>();

        // Depending on the file extension I have to decide how to process it
        String extension = this.fileUtil.getExtension(filename);
        if (extension.equalsIgnoreCase(".csv")) {
            result = this.extractMappedDataItemDefinitionsFromCsv(input);
        }

        return result;
    }

    public List<AbstractMappedItem> extractMappedDataItemDefinitions(Odm metadata) {
        return this.extractMappedDataItemDefinitionsFromOdm(metadata);
    }

    //endregion

    //region Private methods

    @SuppressWarnings("unused")
    private Odm transformXmlToOdm(Odm metadata, InputStream input) {
        Odm result = null;

        try {
            JAXBContext context = JAXBContext.newInstance(Odm.class);
            Unmarshaller un = context.createUnmarshaller();
            result = (Odm) un.unmarshal(input);
        }
        catch (Exception err) {
            err.printStackTrace();
        }

        return result;
    }
    
    private Odm transformCsvToOdm(Odm metadata, InputStream input, List<MappingRecord> mappingRecords) {
        // The resulting odm has to be formed according to the metadata
        Odm odmResult = null;

        try {
            odmResult = new Odm(metadata);
            List<StudySubject> subjects = new ArrayList<>();

            // Depending on header
            reader = new CsvListReader(new InputStreamReader(input), CsvPreference.STANDARD_PREFERENCE);
            String[] sourceHeader = this.reader.getHeader(true); // Ignore the header (first line)

            List<String> resultList;
            while ((resultList = reader.read()) != null) {

                // Prepare fresh new study subject (conforming the SSID generation strategy)
                StudySubject subject = new StudySubject(
                    metadata.getStudyDetails().getStudyParameterConfiguration().getStudySubjectIdGeneration()
                );

                String eventStartDate = null;
                int i = 0;
                for (String item : sourceHeader) {

                    // Get rid of starting and trailing spaces from column name
                    item = item.trim();

                    // Find appropriate mapping record for each header item element
                    List<MappingRecord> applicableMappings = new ArrayList<>();
                    for (MappingRecord mr : mappingRecords) {
                        if (((MappedCsvItem)mr.getSource()).getHeader().equals(item) && resultList.get(i) != null) {
                            applicableMappings.add(mr);
                        }
                    }

                    // Sort according the priority (if multiple mrs have the same target try to execute all according priority)
                    if (applicableMappings.size() > 1) {
                        Collections.sort(applicableMappings, new Comparator<MappingRecord>() {
                            public int compare(MappingRecord o1, MappingRecord o2) {
                                return o2.getPriority().compareTo(o1.getPriority());
                            }
                        });
                    }

                    // Apply mappings, continue to next applicable mapping when this one was mapping applied
                    for (MappingRecord mr : applicableMappings) {
                        // Mapped target
                        MappedOdmItem odmTarget = (MappedOdmItem)mr.getTarget();

                        // Subject specific attributes
                        if (odmTarget.getItemOid().equals(Constants.SS_PERSONID)) {
                            subject.setPid(resultList.get(i));
                            if (subject.getPerson() != null) {
                                subject.getPerson().setPid(resultList.get(i));
                                continue;
                            }
                        }
                        else if (odmTarget.getItemOid().equals(Constants.SS_STUDYSUBJECTID)) {
                            subject.setStudySubjectId(resultList.get(i));
                            continue;
                        }
                        else if (odmTarget.getItemOid().equals(Constants.SS_SECONDARYID)) {
                            subject.setSecondaryId(resultList.get(i));
                            continue;
                        }
                        else if (odmTarget.getItemOid().equals(Constants.SS_GENDER)) {
                            String sourceValue = resultList.get(i);
                            String result;
                            if (mr.getMapping() != null) {
                                result = mr.getMapping().get(sourceValue.trim());
                            }
                            // Otherwise use the codes directly (they should match) = no mapping specified
                            else {
                                result = sourceValue.trim();
                            }
                            subject.setSex(result);
                            continue;
                        }
                        else if (odmTarget.getItemOid().equals(Constants.SS_DATEOFBIRTH)) {
                            String sourceValue = resultList.get(i);
                            SimpleDateFormat sourceFormat = new SimpleDateFormat(mr.getDateFormatString());
                            SimpleDateFormat ocFormat = new SimpleDateFormat(Constants.OC_DATEFORMAT);
                            String result = "";
                            try {
                                result = ocFormat.format(sourceFormat.parse(sourceValue));
                            }
                            catch (ParseException e) {
                                //result = this.parseDate(sourceValue);
                            }

                            subject.setDateOfBirth(result);

                            if (subject.getPerson() != null) {
                                if (!result.equals("")) {
                                    subject.getPerson().setBirthdate(ocFormat.parse(result));
                                    continue;
                                }
                            }
                        }
                        else if (odmTarget.getItemOid().equals(Constants.SS_YEAROFBIRTH)) {
                            subject.setYearOfBirth(Integer.parseInt(resultList.get(i)));
                            continue;
                        }
                        // In case we are automating PID generation these can come handy
                        else if (odmTarget.getItemOid().equals(Constants.SS_FIRSTNAME)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setFirstname(resultList.get(i));
                                continue;
                            }
                        }
                        else if (odmTarget.getItemOid().equals(Constants.SS_LASTNAME)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setSurname(resultList.get(i));
                                continue;
                            }
                        }
                        else if (odmTarget.getItemOid().equals(Constants.SS_BIRTHNAME)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setBirthname(resultList.get(i));
                                continue;
                            }
                        }
                        else if (odmTarget.getItemOid().equals(Constants.SS_CITY)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setCity(resultList.get(i));
                                continue;
                            }
                        }
                        else if (odmTarget.getItemOid().equals(Constants.SS_ZIP)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setZipcode(resultList.get(i));
                                continue;
                            }
                        }
                        else if (odmTarget.getItemOid().startsWith(Constants.SE_STARTDATE + "_SE")) {
                            String sourceValue = resultList.get(i);
                            SimpleDateFormat sourceFormat = new SimpleDateFormat(mr.getDateFormatString());
                            SimpleDateFormat ocFormat = new SimpleDateFormat(Constants.OC_DATEFORMAT);
                            String result = "";
                            try {
                                result = ocFormat.format(sourceFormat.parse(sourceValue));
                            }
                            catch (ParseException e) {
                                //result = this.parseDate(sourceValue);
                            }

                            eventStartDate = result;
                        }
                        // The other study items (data fields)
                        else {
                            String sourceValue = resultList.get(i);
                            String targetValue = mr.process(sourceValue, metadata.getItemDefinition((MappedOdmItem)mr.getTarget()));

                            // Target value is empty string and this is not the default value
                            if ("".equals(targetValue) && !targetValue.equals(mr.getDefaultValue())) {
                                log.info("Mapping not be applied, going to next mapping record based on priority");
                            }
                            // According to target mapping result create the appropriate structure in ODM
                            else {
                                subject.populateDataField(mr, targetValue, eventStartDate);
                                continue;
                            }
                        }
                    }

                    // Take next value
                    i++;
                }

                subjects.add(subject);
            }

            odmResult.populateSubjects(subjects);
        }
        catch (Exception err) {
            log.error(err);
        }
        finally {
            if (this.reader != null) {
                try {
                    this.reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return odmResult;
    }

    private List<AbstractMappedItem> extractMappedDataItemDefinitionsFromCsv(InputStream input) {
        List<AbstractMappedItem> result = new ArrayList<>();

        try {
            // Depending on header
            reader = new CsvListReader(new InputStreamReader(input), CsvPreference.STANDARD_PREFERENCE);
            String[] sourceHeader = this.reader.getHeader(true); // Ignore the header (first line)

           for (String sh : sourceHeader) {
               AbstractMappedItem mdi = new MappedCsvItem(sh);
               result.add(mdi);
           }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        finally {
            if (this.reader != null) {
                try {
                    this.reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private List<AbstractMappedItem> extractMappedDataItemDefinitionsFromOdm(Odm metadata) {
        List<AbstractMappedItem> result = new ArrayList<>();

        for (EventDefinition ed : metadata.getStudies().get(0).getMetaDataVersion().getStudyEventDefinitions()) {
            for (FormDefinition fd : ed.getFormDefs()) {
                for (ItemGroupDefinition igd : fd.getItemGroupDefs()) {
                    for (ItemDefinition id : igd.getItemDefs()) {
                        MappedOdmItem moi = new MappedOdmItem(ed, fd, igd, id);
                        result.add(moi);
                    }
                }
            }
        }

        return result;
    }

    //endregion

}