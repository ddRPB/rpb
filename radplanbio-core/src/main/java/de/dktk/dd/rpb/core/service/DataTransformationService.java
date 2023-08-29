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
import de.dktk.dd.rpb.core.domain.edc.mapping.AbstractMappedItem;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappedCsvItem;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappedOdmItem;
import de.dktk.dd.rpb.core.domain.edc.mapping.Mapping;
import de.dktk.dd.rpb.core.domain.edc.mapping.MappingRecord;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.FileUtil;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.dktk.dd.rpb.core.util.Constants.PSEUDO_DATE;

/**
 * Data transformation service
 *
 * @author tomas@skripcak.net
 * @since 24 Feb 2015
 */
@Named("dataTransformationService")
public class DataTransformationService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(DataTransformationService.class);

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

    /**
     * Transforms an input stream that consists ODM XML or csv data to an ODM object.
     * Additionally, it applies mapping rules to the data to match the identifiers and of the target system.
     *
     * @param metadata Odm meta data that describe the target
     * @param map      Mapping mapping rules
     * @param input    InputStream
     * @param filename String filename to identify the data format of the input
     * @return Odm Odm with data mapped for the target system (without data that do not have a mapping rule)
     */
    public Odm transformToOdm(Odm metadata, Mapping map, InputStream input, String filename) {
        Odm result = null;

        // Depending on the file extension I have to decide how to process it
        String extension = this.fileUtil.getExtension(filename);
        if (extension.equalsIgnoreCase(".xml")) {
            result = this.transformXmlToOdm(input);
            // set oid of the study according to the target system
            result.findUniqueClinicalDataOrNone().setStudyOid(metadata.findFirstStudyOrNone().getOid());
            // map events, forms, item groups and items
            result = this.mapItemsWithMappingRecord(result, map.getMappingRecords());

            List<StudySubject> updatedStudySubjectList = new ArrayList<>();
            for (StudySubject subject : result.getClinicalDataList().get(0).getStudySubjects()) {
                StudySubject updatedSubject = subject;
                List<EventData> events = addArtificialEventsToFillGapsInRepeatKeyFlow(subject.getStudyEventDataList());
                updatedSubject.setStudyEventDataList(events);
                updatedStudySubjectList.add(updatedSubject);
            }

            result.getClinicalDataList().get(0).setStudySubjects(updatedStudySubjectList);


        } else if (extension.equalsIgnoreCase(".csv")) {
            result = this.transformCsvToOdm(metadata, input, map.getMappingRecords());
        }

        if (result != null) {
            result.sortItemGroupData();
        }

        return result;
    }

    /**
     * Adds artificial events if there is a gap in the flow of RepeatKeys. Otherwise the OpenClinicaService will schedule
     * the events with a wrong RepeatKey, because Openclinica simply increases a counter when a new events is scheduled.
     *
     * @param events List<EventData> that will be scanned for gaps in the RepeatKey flow.
     * @return List<EventData> series of events without gaps in the RepeatKey flow. Gaps are filled with artificial events with a PSEUDO_DATE.
     */
    private List<EventData> addArtificialEventsToFillGapsInRepeatKeyFlow(List<EventData> events) {
        Collections.sort(events);
        String previousOid = "";
        Integer i = 2;
        List<EventData> additionalEvents = new ArrayList<>();
        for (EventData event : events) {
            if (event.getStudyEventOid().equalsIgnoreCase(previousOid)) {
                if (event.getStudyEventRepeatKey().equalsIgnoreCase(i.toString())) {
                    // no gap between the events
                    i++;
                } else {
                    for (int x = i; x < Integer.valueOf(event.getStudyEventRepeatKey()); x++) {
                        EventData additionalEvent = new EventData();
                        additionalEvent.setStudyEventRepeatKey(String.valueOf(x));
                        additionalEvent.setStudyEventOid(event.getStudyEventOid());
                        additionalEvent.setStartDate(PSEUDO_DATE);
                        additionalEvent.setStatus("completed");
                        // events with null would be filtered out later in the process
                        additionalEvent.setFormDataList(new ArrayList<>());
                        additionalEvents.add(additionalEvent);

                    }
                    i = Integer.valueOf(event.getStudyEventRepeatKey()) + 1;
                }

            } else {
                previousOid = event.getStudyEventOid();
                i = 2;
            }
        }
        events.addAll(additionalEvents);
        Collections.sort(events);
        return events;
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
        } catch (Exception err) {
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
        } catch (Exception err) {
            // NOOP
        }

        return odmFile;
    }

    /**
     * Extracts a list of mappings from an input stream
     *
     * @param input    InputStream
     * @param filename String name of the file to identify the format
     * @return List<AbstractMappedItem> mapping information
     */
    public List<AbstractMappedItem> extractMappedDataItemDefinitions(InputStream input, String filename) {
        List<AbstractMappedItem> result = new ArrayList<>();

        // Depending on the file extension I have to decide how to process it
        String extension = this.fileUtil.getExtension(filename);
        if (extension.equalsIgnoreCase(".csv")) {
            result = this.extractMappedDataItemDefinitionsFromCsv(input);
        } else if (extension.equalsIgnoreCase(".xml")) {
            try {
                Odm odm = JAXBHelper.unmarshalInputstream2(Odm.class, input);
                odm.updateHierarchy();
                result = this.extractMappedDataItemDefinitionsFromOdm(odm);
            } catch (JAXBException e) {
                log.error(e.getMessage(), e);
            }
        }

        return result;
    }

    //endregion

    //region Private methods

    /**
     * Unmarshal XML to Odm object
     *
     * @param input InputStream
     * @return Odm
     */
    private Odm transformXmlToOdm(InputStream input) {
        Odm result = null;

        try {
            JAXBContext context = JAXBContext.newInstance(Odm.class);
            Unmarshaller un = context.createUnmarshaller();
            result = (Odm) un.unmarshal(input);
        } catch (Exception err) {
            log.error(err.getMessage(),err);
        }

        return result;
    }

    /**
     * Preview simple function that maps Events, FormDefinitions, ItemGroups and ItemsDefinitions, based on mapping rules
     *
     * @param odm            Odm with data that needs to be mapped
     * @param mappingRecords List<MappingRecord> mapping rules
     * @return Odm data mapped to be consumed by the target system
     */
    private Odm mapItemsWithMappingRecord(Odm odm, List<MappingRecord> mappingRecords) {


        if (odm.findUniqueClinicalDataOrNone().getStudySubjects() != null && odm.findUniqueClinicalDataOrNone().getStudySubjects().size() > 0) {

            MappedOdmItem firstSourceItem = (MappedOdmItem) mappingRecords.get(0).getSource();
            String sourceEventOid = firstSourceItem.getStudyEventOid();
            String sourceFormOid = firstSourceItem.getFormOid();
            String sourceItemGroupOid = firstSourceItem.getItemGroupOid();

            MappedOdmItem firstTargetItem = (MappedOdmItem) mappingRecords.get(0).getTarget();
            String targetEventOid = firstTargetItem.getStudyEventOid();
            String targetFormOid = firstTargetItem.getFormOid();
            String targetItemGroupOid = firstTargetItem.getItemGroupOid();

            Map<String, String> itemMapping = new HashMap<>();

            itemMapping.putAll(createItemMappingMap(mappingRecords));


            List<StudySubject> studySubjectList = odm.findUniqueClinicalDataOrNone().getStudySubjects();
            for (StudySubject studySubject : studySubjectList) {

                List<EventData> eventList = studySubject.getStudyEventDataList();
                List<EventData> mappedEventDataList = new ArrayList<>();


                for (EventData event : eventList) {
                    if (event.getStudyEventOid().equalsIgnoreCase(sourceEventOid)) {
                        event.setStudyEventOid(targetEventOid);

                        List<FormData> mappedFormDataList = new ArrayList<>();
                        for (FormData formData : event.getFormDataList()) {
                            if (formData.getFormOid().equalsIgnoreCase(sourceFormOid)) {
                                formData.setFormOid(targetFormOid);
                                List<ItemGroupData> sourceItemGroupList = formData.getItemGroupDataList();

                                List<ItemGroupData> mappedItemGroupList = getMappedItemGroupData(sourceItemGroupOid, targetItemGroupOid, itemMapping, sourceItemGroupList);
                                formData.setItemGroupDataList(mappedItemGroupList);
                                mappedFormDataList.add(formData);
                            }
                        }
                        event.setFormDataList(mappedFormDataList);
                        mappedEventDataList.add(event);
                    }
                }

                studySubject.setStudyEventDataList(mappedEventDataList);
            }
            odm.getClinicalDataList().get(0).setStudySubjects(studySubjectList);
        }
        return odm;
    }

    private Map<String, String> createItemMappingMap(List<MappingRecord> mappingRecords) {
        Map<String, String> itemMapping = new HashMap<>();
        for (MappingRecord mappingRecord : mappingRecords) {
            String sourceItemOid = ((MappedOdmItem) mappingRecord.getSource()).getItemOid();
            String targetItemOid = ((MappedOdmItem) mappingRecord.getTarget()).getItemOid();
            itemMapping.put(sourceItemOid, targetItemOid);
        }
        return itemMapping;
    }

    private List<ItemGroupData> getMappedItemGroupData(String sourceItemGroupOid, String targetItemGroupOid, Map<String, String> itemMapping, List<ItemGroupData> sourceItemGroupList) {
        List<ItemGroupData> mappedItemGroupList = new ArrayList<>();
        for (ItemGroupData itemGroupData : sourceItemGroupList) {
            if (itemGroupData.getItemGroupOid().equalsIgnoreCase(sourceItemGroupOid)) {
                itemGroupData.setItemGroupOid(targetItemGroupOid);

                List<ItemData> mappedItems = getMapppedItemData(itemMapping, itemGroupData);
                itemGroupData.setItemDataList(mappedItems);
                mappedItemGroupList.add(itemGroupData);
            }
        }
        return mappedItemGroupList;
    }

    private List<ItemData> getMapppedItemData(Map<String, String> itemMapping, ItemGroupData itemGroupData) {
        List<ItemData> mappedItems = new ArrayList<>();
        for (ItemData itemData : itemGroupData.getItemDataList()) {
            String sourceItemOid = itemData.getItemOid();
            if (itemMapping.containsKey(sourceItemOid)) {
                itemData.setItemOid(itemMapping.get(sourceItemOid));
                mappedItems.add(itemData);
            } else {
                this.log.debug("The item key is not on the mapping list: " + sourceItemOid);
            }
        }
        return mappedItems;
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
                        if (((MappedCsvItem) mr.getSource()).getHeader().equals(item) && resultList.get(i) != null) {
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
                        MappedOdmItem odmTarget = (MappedOdmItem) mr.getTarget();

                        // Subject specific attributes
                        if (odmTarget.getItemOid().equals(Constants.SS_PERSONID)) {
                            subject.setPid(resultList.get(i));
                            if (subject.getPerson() != null) {
                                subject.getPerson().setPid(resultList.get(i));
                                continue;
                            }
                        } else if (odmTarget.getItemOid().equals(Constants.SS_STUDYSUBJECTID)) {
                            subject.setStudySubjectId(resultList.get(i));
                            continue;
                        } else if (odmTarget.getItemOid().equals(Constants.SS_SECONDARYID)) {
                            subject.setSecondaryId(resultList.get(i));
                            continue;
                        } else if (odmTarget.getItemOid().equals(Constants.SS_GENDER)) {
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
                        } else if (odmTarget.getItemOid().equals(Constants.SS_DATEOFBIRTH)) {
                            String sourceValue = resultList.get(i);
                            SimpleDateFormat sourceFormat = new SimpleDateFormat(mr.getDateFormatString());
                            SimpleDateFormat ocFormat = new SimpleDateFormat(Constants.OC_DATEFORMAT);
                            String result = "";
                            try {
                                result = ocFormat.format(sourceFormat.parse(sourceValue));
                            } catch (ParseException e) {
                                //result = this.parseDate(sourceValue);
                            }

                            subject.setDateOfBirth(result);

                            if (subject.getPerson() != null) {
                                if (!result.equals("")) {
                                    subject.getPerson().setBirthdate(ocFormat.parse(result));
                                    continue;
                                }
                            }
                        } else if (odmTarget.getItemOid().equals(Constants.SS_YEAROFBIRTH)) {
                            subject.setYearOfBirth(Integer.parseInt(resultList.get(i)));
                            continue;
                        }
                        // In case we are automating PID generation these can come handy
                        else if (odmTarget.getItemOid().equals(Constants.SS_FIRSTNAME)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setFirstname(resultList.get(i));
                                continue;
                            }
                        } else if (odmTarget.getItemOid().equals(Constants.SS_LASTNAME)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setSurname(resultList.get(i));
                                continue;
                            }
                        } else if (odmTarget.getItemOid().equals(Constants.SS_BIRTHNAME)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setBirthname(resultList.get(i));
                                continue;
                            }
                        } else if (odmTarget.getItemOid().equals(Constants.SS_CITY)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setCity(resultList.get(i));
                                continue;
                            }
                        } else if (odmTarget.getItemOid().equals(Constants.SS_ZIP)) {
                            if (subject.getPerson() != null) {
                                subject.getPerson().setZipcode(resultList.get(i));
                                continue;
                            }
                        } else if (odmTarget.getItemOid().startsWith(Constants.SE_STARTDATE + "_SE")) {
                            String sourceValue = resultList.get(i);
                            SimpleDateFormat sourceFormat = new SimpleDateFormat(mr.getDateFormatString());
                            SimpleDateFormat ocFormat = new SimpleDateFormat(Constants.OC_DATEFORMAT);
                            String result = "";
                            try {
                                result = ocFormat.format(sourceFormat.parse(sourceValue));
                            } catch (ParseException e) {
                                //result = this.parseDate(sourceValue);
                            }

                            eventStartDate = result;
                        }
                        // The other study items (data fields)
                        else {
                            String sourceValue = resultList.get(i);
                            String targetValue = mr.process(sourceValue, metadata.getItemDefinition((MappedOdmItem) mr.getTarget()));

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
        } catch (Exception err) {
            log.error(err.getMessage(),err);
        } finally {
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
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
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

            if (ed.getFormDefs() != null) {
                for (FormDefinition fd : ed.getFormDefs()) {

                    if (fd.getItemGroupDefs() != null) {
                        for (ItemGroupDefinition igd : fd.getItemGroupDefs()) {

                            if (igd.getItemDefs() != null) {
                                for (ItemDefinition id : igd.getItemDefs()) {
                                    MappedOdmItem moi = new MappedOdmItem(ed, fd, igd, id);
                                    result.add(moi);
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    //endregion

}
