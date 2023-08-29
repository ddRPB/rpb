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

package de.dktk.dd.rpb.core.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import de.dktk.dd.rpb.core.DicomImageFactory;
import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.domain.pacs.*;
import de.dktk.dd.rpb.core.service.support.PacsPatientResponseUnmashaller;
import de.dktk.dd.rpb.core.util.CallableJerseyClient;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.JsonStringUtil;
import org.apache.commons.lang.StringUtils;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.awt.*;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static de.dktk.dd.rpb.core.util.Constants.*;

/**
 * Conquest PACS service interface implementation
 * The service provides client access to conquest specific functions and deployed lua scripts
 *
 * @author tomas@skripcak.net
 * @since 29 Nov 2013
 */
@Transactional(readOnly = true)
@Named("pacsService")
public class ConquestService implements IConquestService {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(ConquestService.class);

    private static final String mode = "?mode=";
    private static final String wado = "?requestType=WADO";
    private static final String dum = "&dum=.zip";
    private static final String archiveStudy = "&study=";
    private static final String archiveSeries = "&series=";

    private static final String patientId = "&PatientID=";
    private static final String studyUid = "&StudyUID=";
    private static final String seriesUid = "&SeriesUID=";
    private static final String sopUid = "&SopUID=";
    private static final String seriesCount = "&SeriesCount=";
    private static final String filesCount = "&FilesCount=";
    private static final String aet = "&AET=";

    //endregion

    //region Members

    private String baseUrl;
    private String username;
    private String password;
    private int threadPoolSize;
    private int structureFillTransparency = 115;
    private int isoFillTransparency = 70;
    private boolean forceRecalculateDvh = false;

    //endregion

    //region Constructors

    /**
     * Conquest service constructor
     */
    public ConquestService() {
        // NOOP
    }

    //endregion

    //region Methods

    //region Setup

    /**
     * {@inheritDoc}
     */
    public void setupConnection(String baseUrl, int threadPoolSize) {
        this.baseUrl = baseUrl;
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * {@inheritDoc}
     */
    public void setupConnection(String baseUrl, int threadPoolSize, String user, String password) {
        this.baseUrl = baseUrl;
        this.threadPoolSize = threadPoolSize;
        this.username = user;
        this.password = password;
    }

    //endregion

    //region RT Treatment Case

    public RtTreatmentCase loadRtTreatmentCase(String dicomPatientId, DicomStudy dicomStudy) {
        RtTreatmentCase tc = new RtTreatmentCase();

        if (dicomStudy != null) {

            // RTSTRUCT list
            List<DicomSeries> rtStructSeries = dicomStudy.getSeriesByModality("RTSTRUCT");
            if (rtStructSeries != null) {
                for (DicomSeries rtStructSerie : rtStructSeries) {
                    DicomSeries dicomSeriesWithImages = this.loadStudySeries(
                            dicomPatientId,
                            dicomStudy.getStudyInstanceUID(),
                            rtStructSerie.getSeriesInstanceUID()
                    );

                    if (dicomSeriesWithImages != null && dicomSeriesWithImages.getSeriesImages() != null && dicomSeriesWithImages.getSeriesImages().size() > 0) {

                        List<DicomRtStructureSet> structureSets = new ArrayList<>();
                        for (DicomImage instance : dicomSeriesWithImages.getSeriesImages()) {
                            structureSets.add(
                                    this.loadDicomRtStructureSet(
                                            dicomStudy.getStudyInstanceUID(),
                                            rtStructSerie.getSeriesInstanceUID(),
                                            dicomSeriesWithImages.getSeriesImages().get(0).getSopInstanceUID(),
                                            tc
                                    )
                            );
                        }

                        tc.setRtStructureSets(structureSets);
                    }
                }
            }

            // RTPLAN list
            List<DicomSeries> rtPlanSeries = dicomStudy.getSeriesByModality("RTPLAN");
            for (DicomSeries planSerie : rtPlanSeries) {
                DicomSeries dicomSeriesWithImages = this.loadStudySeries(
                        dicomPatientId,
                        dicomStudy.getStudyInstanceUID(),
                        planSerie.getSeriesInstanceUID()
                );

                if (dicomSeriesWithImages != null && dicomSeriesWithImages.getSeriesImages() != null) {

                    List<DicomSeriesRtPlan> plans = new ArrayList<>();
                    for (DicomImage instance : dicomSeriesWithImages.getSeriesImages()) {
                        plans.add(
                                this.loadDicomRtPlan(
                                        dicomStudy.getStudyInstanceUID(),
                                        planSerie.getSeriesInstanceUID(),
                                        instance.getSopInstanceUID()
                                )
                        );
                    }

                    tc.setRtPlans(plans);
                }
            }

            // RTDOSE list
            List<DicomSeries> rtDoseSeries = dicomStudy.getSeriesByModality("RTDOSE");
            for (DicomSeries doseSerie : rtDoseSeries) {
                DicomSeries dicomSeriesWithImages = this.loadStudySeries(
                        dicomPatientId,
                        dicomStudy.getStudyInstanceUID(),
                        doseSerie.getSeriesInstanceUID()
                );

                if (dicomSeriesWithImages != null && dicomSeriesWithImages.getSeriesImages() != null) {

                    List<DicomSeriesRtDose> doses = new ArrayList<>();
                    for (DicomImage instance : dicomSeriesWithImages.getSeriesImages()) {
                        doses.add(
                                this.loadDicomRtDose(
                                        dicomStudy.getStudyInstanceUID(),
                                        doseSerie.getSeriesInstanceUID(),
                                        instance.getSopInstanceUID()
                                )
                        );
                    }

                    tc.setRtDoses(doses);
                }

            }
        }

        return tc;
    }

    //endregion

    //region DICOM Patient

    /**
     * {@inheritDoc}
     */
    public List<Subject> loadPatient(String dicomPatientId) throws Exception {

        String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.JSON_PATIENTS.toString() + patientId + dicomPatientId;
        List<Subject> results;

        ClientResponse response = queryConquest(compositeQueryUrl);
        throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);

        if (response.hasEntity()) {
            String queryResultString = response.getEntity(String.class);
            queryResultString = JsonStringUtil.trimJsonString(queryResultString);
            results = handlePacsPatientResponse(queryResultString);
        } else {
            String errorMessage = "There was a problem with your request, because the response had no entity.";
            String errorDetails = "Status: 200 " + "URL: " + compositeQueryUrl;
            String userAdvice = "Please try again or contact your administrator";
            log.error(errorMessage + " " + errorDetails);
            throw new Exception(errorMessage + " " + userAdvice);
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    public List<Subject> loadPatients(List<StudySubject> studySubjectList) throws Exception {

        List<List<StudySubject>> listOfStudySubjectLists = splitIntoSubLists(studySubjectList);
        List<Subject> loadedSubjects = new ArrayList<>();
        for (List<StudySubject> studySubjectSubList : listOfStudySubjectLists) {
            loadedSubjects.addAll(loadPatientsForSublist(studySubjectSubList));
        }

        return loadedSubjects;
    }

    /**
     * {@inheritDoc}
     */
    public boolean movePatient(String dicomPatientId, String destinationAet) {
        boolean result = Boolean.FALSE;

        String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.MOVE_PATIENTS.toString();

        if (!"".equals(dicomPatientId)) {
            compositeQueryUrl += patientId + dicomPatientId;
        }
        if (!"".equals(destinationAet)) {
            compositeQueryUrl += aet + destinationAet;
        }

        ClientResponse response = queryConquest(compositeQueryUrl);

        if (response.getStatus() == 200) {
            result = Boolean.TRUE;
        }

        return result;
    }

    //endregion

    //region DICOM Studies

    /**
     * {@inheritDoc}
     */
    public DicomStudy loadPatientStudy(String dicomPatientId, String dicomStudyUid) {
        DicomStudy result = new DicomStudy();

        try {
            String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.JSON_STUDIES.toString();
            compositeQueryUrl += patientId + dicomPatientId;
            compositeQueryUrl += studyUid + dicomStudyUid;

            ClientResponse response = queryConquest(compositeQueryUrl);
            throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);

            if (response.hasEntity()) {
                JSONArray jsonStudies = getJsonEntityFromString(response, "Studies");
                if (jsonStudies.length() > 0) {
                    JSONObject jsonStudy = jsonStudies.getJSONObject(0);
                    unmarshalStudyProperties(result, jsonStudy);
                }
            } else {
                log.error("The response on the request with query: " + compositeQueryUrl + "had no entity");
                log.debug(response.toString());
            }
        } catch (Exception err) {
            String message = "Loading the DICOM series failed!\nThere appears to be a problem with the server connection.";
            log.error(message, err);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<DicomStudy> loadPatientStudies(String dicomPatientId) {
        List<DicomStudy> resultStudies = new ArrayList<>();

        try {
            String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.JSON_STUDIES.toString();
            if (!"".equals(dicomPatientId)) {
                compositeQueryUrl += patientId + dicomPatientId;
            }

            ClientResponse response = queryConquest(compositeQueryUrl);
            throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);

            if (response.hasEntity()) {
                JSONArray jsonStudies = getJsonEntityFromString(response, "Studies");

                for (int i = 0; i < jsonStudies.length(); i++) {
                    DicomStudy study = new DicomStudy();
                    JSONObject jsonStudy = jsonStudies.getJSONObject(i);
                    unmarshalStudyProperties(study, jsonStudy);
                    resultStudies.add(study);
                }
            }
        } catch (Exception err) {
            String message = "Loading the DICOM series failed!\nThere appears to be a problem with the server connection.";
            log.error(message, err);
        }
        return resultStudies;
    }

    /**
     * {@inheritDoc}
     */
    public List<DicomStudy> loadPatientStudies(String dicomPatientId, List<ItemData> dicomStudyUidList) {
        List<DicomStudy> resultStudies = new ArrayList<>();

        // Check if there are DICOM StudyInstanceUIDs stored in CRF items
        boolean dicomStudyIsReferenced = false;
        if (dicomStudyUidList != null) {
            for (ItemData itemData : dicomStudyUidList) {
                if (StringUtils.isNotEmpty(itemData.getValue())) {
                    dicomStudyIsReferenced = true;
                    break;
                }
            }
        }

        // If there is a patient to query and DICOM data is referenced
        if (StringUtils.isNotEmpty(dicomPatientId) && dicomStudyIsReferenced) {
            try {
                // I have to think if PACS connection should depend on RPB study partner site or RPB user account partner site
                String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.JSON_STUDIES.toString() + patientId + dicomPatientId;

                // If there is just one study in dicomStudyUidList load only this one (otherwise query all studies)
                if (dicomStudyUidList.size() == 1) {
                    compositeQueryUrl += studyUid + dicomStudyUidList.get(0).getValue();
                }

                ClientResponse response = queryConquest(compositeQueryUrl);

                throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);

                if (response.hasEntity()) {
                    String jsonstring = response.getEntity(String.class);
                    jsonstring = JsonStringUtil.trimJsonString(jsonstring);

                    if (!"".equals(jsonstring)) {
                        JSONObject json = new JSONObject(jsonstring);
                        JSONArray jsonStudies = json.getJSONArray("Studies");

                        for (int i = 0; i < jsonStudies.length(); i++) {

                            JSONObject jsonStudy = jsonStudies.getJSONObject(i);
                            DicomStudy study = new DicomStudy();

                            study.setStudyInstanceUID(jsonStudy.getString("StudyInstanceUID"));

                            // filter to return only studies listed in parameter
                            boolean studyFound = false;
                            for (ItemData itemData : dicomStudyUidList) {
                                if (itemData.getValue() != null && itemData.getValue().equals(study.getStudyInstanceUID())) {
                                    studyFound = true;
                                    break;
                                }
                            }
                            if (!studyFound) {
                                continue;
                            }

                            study.setStudyDate(jsonStudy.optString("StudyDate"));
                            study.setStudyDescription(jsonStudy.optString("StudyDescription"));

                            JSONArray jsonSeries = jsonStudy.getJSONArray("Series");
                            study.setStudySeries(
                                    this.unmarshalDicomSeries(jsonSeries)
                            );

                            resultStudies.add(study);
                        }
                    }
                }
            } catch (Exception err) {
                String message = "Loading the DICOM series failed!\nThere appears to be a problem with the server connection.";
                log.error(message, err);
            }
        }

        return resultStudies;
    }

    /**
     * {@inheritDoc}
     */
    public void cacheDicomStudy(String dicomPatientId, String dicomStudyUid, int dicomSeriesCount) {

        String url = this.baseUrl + mode + EnumConquestMode.CACHE_STUDIES.toString();
        if (!"".equals(dicomPatientId)) {
            url += patientId + dicomPatientId;
        }
        if (!"".equals(dicomStudyUid)) {
            url += studyUid + dicomStudyUid;
        }
        url += seriesCount + dicomSeriesCount;

        this.cacheDicom(url);
    }

    /**
     * {@inheritDoc}
     */
    public InputStream archivePatientStudy(String dicomPatientId, String dicomStudyUid) {

        return this.archiveDicom(EnumConquestMode.ZIP_STUDY.toString(), archiveStudy, dicomPatientId, dicomStudyUid);
    }

    /**
     * {@inheritDoc}
     */
    public boolean moveDicomStudy(String dicomPatientId, String dicomStudyUid, String destinationAet) {
        String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.MOVE_STUDIES.toString() + patientId + dicomPatientId + studyUid + dicomStudyUid + aet + destinationAet;
        log.debug("Study move triggered with url " + compositeQueryUrl);

        ClientResponse response = queryConquest(compositeQueryUrl);

        try {
            throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);
        } catch (Exception e) {
            log.error("Failed to process query: " + compositeQueryUrl);
            log.debug(e.getMessage(), e);
            return false;
        }

        return true;
    }

    //endregion

    //region DICOM Series

    /**
     * {@inheritDoc}
     */
    public ClientResponse loadStudySeriesResponse(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid) {
        ClientResponse result = null;

        try {
            String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.JSON_SERIES.toString();
            if (!"".equals(dicomPatientId)) {
                compositeQueryUrl += patientId + dicomPatientId;
            }
            if (!"".equals(dicomStudyUid)) {
                compositeQueryUrl += studyUid + dicomStudyUid;
            }
            if (!"".equals(dicomSeriesUid)) {
                compositeQueryUrl += seriesUid + dicomSeriesUid;
            }

            ClientResponse response = queryConquest(compositeQueryUrl);

            throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);

            if (response.hasEntity()) {
                result = response;
            }
        } catch (Exception err) {
            String message = "Loading the DICOM images failed!\nThere appears to be a problem with the server connection.";
            log.error(message, err);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public DicomSeries loadStudySeries(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid) {
        DicomSeries result = null;

        try {
            String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.JSON_SERIES.toString();
            if (!"".equals(dicomPatientId)) {
                compositeQueryUrl += patientId + dicomPatientId;
            }
            if (!"".equals(dicomStudyUid)) {
                compositeQueryUrl += studyUid + dicomStudyUid;
            }
            if (!"".equals(dicomSeriesUid)) {
                compositeQueryUrl += seriesUid + dicomSeriesUid;
            }

            ClientResponse response = queryConquest(compositeQueryUrl);

            throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);

            if (response.hasEntity()) {
                JSONArray jsonSeries = getJsonEntityFromString(response, "Series");

                for (int i = 0; i < jsonSeries.length(); i++) {

                    JSONObject jsonStudy = jsonSeries.getJSONObject(i);
                    result = new DicomSeries();

                    result.setSeriesInstanceUID(jsonStudy.getString("SeriesInstanceUID"));
                    result.setSeriesDescription(jsonStudy.optString("SeriesDescription"));
                    result.setSeriesModality(jsonStudy.optString("Modality"));

                    JSONArray jsonImages = jsonStudy.getJSONArray("Images");
                    result.setSeriesImages(
                            this.unmarshalDicomImages(jsonImages)
                    );
                }
            }
        } catch (Exception err) {
            String message = "Loading the DICOM images failed!\nThere appears to be a problem with the server connection.";
            log.error(message, err);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void cacheDicomSeries(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid, int dicomFilesCount) {

        String url = this.baseUrl + mode + EnumConquestMode.CACHE_SERIES.toString();
        if (!"".equals(dicomPatientId)) {
            url += patientId + dicomPatientId;
        }
        if (!"".equals(dicomStudyUid)) {
            url += studyUid + dicomStudyUid;
        }
        if (!"".equals(dicomSeriesUid)) {
            url += seriesUid + dicomSeriesUid;
        }
        url += filesCount + dicomFilesCount;

        this.cacheDicom(url);
    }

    /**
     * {@inheritDoc}
     */
    public InputStream archivePatientSeries(String dicomPatientId, String dicomSeriesUid) {

        return this.archiveDicom(EnumConquestMode.ZIP_SERIES.toString(), archiveSeries, dicomPatientId, dicomSeriesUid);
    }

    /**
     * {@inheritDoc}
     */
    public boolean moveDicomSeries(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid, String destinationAet) {
        String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.MOVE_SERIES.toString() + patientId + dicomPatientId + studyUid + dicomStudyUid + seriesUid + dicomSeriesUid + aet + destinationAet;
        log.debug("Series move triggered with url " + compositeQueryUrl);

        ClientResponse response = queryConquest(compositeQueryUrl);

        try {
            throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);
        } catch (Exception e) {
            log.error("Failed to process query: " + compositeQueryUrl);
            log.debug(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean moveDicomSopInstance(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid, String dicomSopInstanceId, String destinationAet) {
        String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.MOVE_IMAGES.toString() + patientId + dicomPatientId + studyUid + dicomStudyUid + seriesUid + dicomSeriesUid + sopUid + dicomSopInstanceId + aet + destinationAet;
        log.debug("SOP Instance move triggered with url " + compositeQueryUrl);
        ClientResponse response = queryConquest(compositeQueryUrl);

        try {
            throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);
        } catch (Exception e) {
            log.error("Failed to process query: " + compositeQueryUrl);
            log.debug(e.getMessage(), e);
            return false;
        }
        return true;
    }

    //endregion

    // region DICOM Images

    public Future<String> getDicomImagesOfSeriesAsFuture(String dicomPatientId, String dicomStudyId, String dicomSeriesUid, ExecutorService service) {

        String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.JSON_SERIES.toString();

        if (!"".equals(dicomPatientId)) {
            compositeQueryUrl += patientId + dicomPatientId;
        }

        if (!"".equals(dicomStudyId)) {
            compositeQueryUrl += studyUid + dicomStudyId;
        }

        if (!"".equals(dicomSeriesUid)) {
            compositeQueryUrl += seriesUid + dicomSeriesUid;
        }

        return service.submit(new CallableJerseyClient(compositeQueryUrl, this.username, this.password));
    }

    // endregion

    //region DICOM SOP Instance

    public boolean instanceExists(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid, String dicomInstanceUid) {
        boolean result = false;

        try {
            String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.FILE_EXISTS.toString();
            if (!"".equals(dicomPatientId)) {
                compositeQueryUrl += patientId + dicomPatientId;
            }
            if (!"".equals(dicomStudyUid)) {
                compositeQueryUrl += studyUid + dicomStudyUid;
            }
            if (!"".equals(dicomSeriesUid)) {
                compositeQueryUrl += seriesUid + dicomSeriesUid;
            }
            if (!"".equals(dicomInstanceUid)) {
                compositeQueryUrl += sopUid + dicomSeriesUid;
            }

            ClientResponse response = queryConquest(compositeQueryUrl);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                result = true;
            }
        } catch (Exception err) {
            log.error(err.getMessage(), err);
        }

        return result;
    }

    public List<DicomImage> loadStudySeriesImages(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid) {
        List<DicomImage> result = new ArrayList<>();

        try {
            String compositeQueryUrl = this.baseUrl + mode + EnumConquestMode.JSON_SERIES.toString();
            if (!"".equals(dicomPatientId)) {
                compositeQueryUrl += patientId + dicomPatientId;
            }
            if (!"".equals(dicomStudyUid)) {
                compositeQueryUrl += studyUid + dicomStudyUid;
            }
            if (!"".equals(dicomSeriesUid)) {
                compositeQueryUrl += seriesUid + dicomSeriesUid;
            }

            ClientResponse response = queryConquest(compositeQueryUrl);

            throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);

            if (response.hasEntity()) {
                JSONArray jsonSeries = getJsonEntityFromString(response, "Series");

                for (int i = 0; i < jsonSeries.length(); i++) {

                    JSONObject jsonStudy = jsonSeries.getJSONObject(i);
                    String seriesModality = jsonStudy.optString("Modality");

                    JSONArray jsonImages = jsonStudy.getJSONArray("Images");

                    try {
                        for (int j = 0; j < jsonImages.length(); j++) {
                            JSONObject jsonImage = jsonImages.getJSONObject(j);
                            DicomImage image = DicomImageFactory.getDicomImage(seriesModality, jsonImage);

                            result.add(image);
                        }
                    } catch (Exception err) {
                        String message = "There was a problem unmarshalling DICOM series from JSON!";
                        log.error(message, err);
                    }

                }
            }
        } catch (Exception err) {
            String message = "Loading the DICOM images failed!\nThere appears to be a problem with the server connection.";
            log.error(message, err);
        }

        return result;
    }

    //endregion

    //region DICOM RTSTRUCT

    public DicomRtStructureSet loadDicomRtStructureSet(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid, RtTreatmentCase tc) {
        DicomRtStructureSet structureSet = new DicomRtStructureSet();

        // Load DICOM data
        Attributes dcmAttributes = this.loadWadoDicomInstance(
                studyInstanceUid,
                seriesInstanceUid,
                sopInstanceUid
        );

        // DICOM data exists
        if (dcmAttributes != null) {

            // Construct StructureSet object
            structureSet.setLabel(dcmAttributes.getString(Tag.StructureSetLabel));
            structureSet.setDate(dcmAttributes.getDate(Tag.StructureSetDateAndTime));

            // Locate the name and number of each ROI
            for (Attributes ssROIseq : dcmAttributes.getSequence(Tag.StructureSetROISequence)) {
                DicomRtStructure structure = new DicomRtStructure();
                structure.setRoiNumber(ssROIseq.getInt(Tag.ROINumber, -1));
                structure.setRoiName(ssROIseq.getString(Tag.ROIName));
                structureSet.put(structure.getRoiNumber(), new DicomRtStructureLayer(structure));
            }

            // Determine the observation type of each structure (PTV, organ, external, etc)
            for (Attributes rtROIObsSeq : dcmAttributes.getSequence(Tag.RTROIObservationsSequence)) {
                DicomRtStructureLayer layer = structureSet.get(rtROIObsSeq.getInt(Tag.ReferencedROINumber, -1));
                if (layer != null) {
                    layer.getStructure().setObservationNumber(rtROIObsSeq.getInt(Tag.ObservationNumber, -1));
                    layer.getStructure().setRtRoiInterpretedType(rtROIObsSeq.getString(Tag.RTROIInterpretedType));
                    layer.getStructure().setRoiObservationLabel(rtROIObsSeq.getString(Tag.ROIObservationLabel));
                }
            }

            // Coordinate data for each ROI
            // The coordinate data of each ROI is stored within ROIContourSequence
            for (Attributes roiContourSeq : dcmAttributes.getSequence(Tag.ROIContourSequence)) {
                DicomRtStructureLayer layer = structureSet.get(roiContourSeq.getInt(Tag.ReferencedROINumber, -1));
                if (layer == null) {
                    continue;
                }

                // Get the RGB color triplet for the current ROI if it exists
                String[] valColors = roiContourSeq.getStrings(Tag.ROIDisplayColor);
                int[] rgb;
                if (valColors != null && valColors.length == 3) {
                    rgb = new int[]{Integer.parseInt(valColors[0]), Integer.parseInt(valColors[1]),
                            Integer.parseInt(valColors[2])};
                } else {
                    Random rand = new Random();
                    rgb = new int[]{rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)};
                }

                Color color1 = getRGBColor(255, null, rgb);
                Color color2 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), this.structureFillTransparency);
                layer.getStructure().setColor(color2);

                // Contour planes
                Map<Double, ArrayList<DicomRtContour>> planes = new HashMap<>();
                Sequence cseq = roiContourSeq.getSequence(Tag.ContourSequence);
                if (cseq != null) {
                    // Locate the contour sequence for each referenced ROI
                    for (Attributes contour : cseq) {
                        // For each plane, initialize a new plane dictionary
                        DicomRtContour plane = new DicomRtContour(layer);

                        // Determine all the plane properties
                        plane.setGeometricType(contour.getString(Tag.ContourGeometricType));
                        plane.setContourSlabThickness(contour.getDouble(Tag.ContourSlabThickness, -1));
                        plane.setContourOffsetVector(contour.getDoubles(Tag.ContourOffsetVector));
                        plane.setContourPoints(contour.getInt(Tag.NumberOfContourPoints, -1));

                        double[] points = contour.getDoubles(Tag.ContourData);
                        if (points != null && points.length % 3 == 0) {
                            plane.setPoints(points);
                            if (plane.getContourPoints() == -1) {
                                plane.setContourPoints(points.length / 3);
                            }
                        }

                        // Each plane which coincides with a image slice will have a unique ID
                        // take the first one
                        for (Attributes images : contour.getSequence(Tag.ContourImageSequence)) {
                            String sopUID = images.getString(Tag.ReferencedSOPInstanceUID);
                            if (sopUID != null && !sopUID.equals("")) {
                                ArrayList<DicomRtContour> pls;
                                if (tc.getContourMap() == null || tc.getContourMap().get(sopUID) == null) {
                                    pls = new ArrayList<>();
                                } else {
                                    pls = tc.getContourMap().get(sopUID);
                                }
                                pls.add(plane);
                            }
                        }

                        // Add each plane to the planes dictionary of the current ROI
                        double z = plane.getCoordinateZ();

                        // If there are no contour on specific z position
                        if (!planes.containsKey(z)) {
                            planes.put(z, new ArrayList<DicomRtContour>());
                            planes.get(z).add(plane);
                        }

                    }
                }

                // Calculate the plane thickness for the current ROI
                layer.getStructure().setThickness(calculatePlaneThickness(planes));

                // Add the planes dictionary to the current ROI
                layer.getStructure().setPlanes(planes);
            }
        }

        return structureSet;
    }

    //endregion

    //region DICOM RTDOSE

    public DicomSeriesRtDose loadDicomRtDose(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid) {
        DicomSeriesRtDose rtDose = new DicomSeriesRtDose();

        // Load DICOM data
        Attributes dcmAttributes = this.loadWadoDicomInstance(
                studyInstanceUid,
                seriesInstanceUid,
                sopInstanceUid
        );

        rtDose.setSopInstanceUid(
                dcmAttributes.getString(Tag.SOPInstanceUID)
        );
        rtDose.setDoseUnits(
                dcmAttributes.getString(Tag.DoseUnits)
        );
        rtDose.setDoseType(
                dcmAttributes.getString(Tag.DoseType)
        );
        rtDose.setDoseSummationType(
                dcmAttributes.getString(Tag.DoseSummationType)
        );
        rtDose.setDoseGridScaling(
                dcmAttributes.getDouble(Tag.DoseGridScaling, 0.0)
        );

        // Check whether DVH is included
        Sequence dvhSeq = dcmAttributes.getSequence(Tag.DVHSequence);
        if (dvhSeq != null) {
            List<DicomRtDvh> dvhs = new ArrayList<>();
            for (Attributes dvhAttributes : dvhSeq) {

                // Need to refer to delineated contour
                DicomRtDvh rtDvh = null;
                Sequence dvhRefRoiSeq = dvhAttributes.getSequence(Tag.DVHReferencedROISequence);
                if (dvhRefRoiSeq == null) {
                    continue;
                } else if (dvhRefRoiSeq.size() == 1) {
                    rtDvh = new DicomRtDvh();
                    Attributes dvhRefRoiAttributes = dvhRefRoiSeq.get(0);
                    rtDvh.setReferencedRoiNumber(
                            dvhRefRoiAttributes.getInt(Tag.ReferencedROINumber, -1)
                    );

                    log.debug("Found DVH for ROI: " + rtDvh.getReferencedRoiNumber());
                }

                if (rtDvh != null) {
                    // Convert Differential DVH to Cumulative
                    if (dvhSeq.get(0).getString(Tag.DVHType).equals("DIFFERENTIAL")) {
                        log.info("Not supported: converting differential DVH to cumulative");

                        //TODO: implement
                        double[] data = dvhAttributes.getDoubles(Tag.DVHData);

//                        dDVH = np.array(data)
//                        # Separate the dose and volume values into distinct arrays
//                        dose = data[0::2]
//                        volume = data[1::2]
//
//                        # Get the min and max dose and volume values
//                        mindose = int(dose[0]*100)
//                        maxdose = int(sum(dose)*100)
//                        maxvol = sum(volume)
//
//                        # Determine the dose values that are missing from the original data
//                        missingdose = np.ones(mindose) * maxvol
//
//                        # Generate the cumulative dose and cumulative volume data
//                        k = 0
//                        cumvol = []
//                        cumdose = []
//                        while k < len(dose):
//                            cumvol += [sum(volume[k:])]
//                            cumdose += [sum(dose[:k])]
//                            k += 1
//
//                        cumvol = np.array(cumvol)
//                        cumdose = np.array(cumdose)*100
//
//                        # Interpolate the dDVH data for 1 cGy bins
//                        interpdose = np.arange(mindose, maxdose+1)
//                        interpcumvol = np.interp(interpdose, cumdose, cumvol)
//
//                        # Append the interpolated values to the missing dose values
//                        cumDVH = np.append(missingdose, interpcumvol)

                        double[] cumDvhData;
                        //rtDvh.setDvhData(cumDvhData);
                        //rtDvh.setDvhNumberOfBins(cumDvhData.length);
                    }
                    // Cumulative
                    else {
                        //TODO Remove "filer" values from DVH data array (even values are DVH values) odd are filter
                        //dvhitem['data'] = np.array(item.DVHData[1::2])
                        rtDvh.setDvhData(
                                dvhAttributes.getDoubles(Tag.DVHData)
                        );
                        rtDvh.setDvhNumberOfBins(
                                dvhAttributes.getInt(Tag.DVHNumberOfBins, 0)
                        );
                    }

                    // Always cumulative - differential was converted
                    rtDvh.setType("CUMULATIVE");

                    rtDvh.setDoseUnit(
                            dvhAttributes.getString(Tag.DoseUnits)
                    );
                    rtDvh.setDoseType(
                            dvhAttributes.getString(Tag.DoseType)
                    );
                    rtDvh.setDvhDoseScaling(
                            dvhAttributes.getDouble(Tag.DVHDoseScaling, 1.0)
                    );
                    rtDvh.setDvhVolumeUnit(
                            dvhAttributes.getString(Tag.DVHVolumeUnits)
                    );
                    // -1.0 means that it needs to be calculated later
                    rtDvh.setDvhMinimumDose(
                            dvhAttributes.getDouble(Tag.DVHMinimumDose, -1.0)
                    );
                    rtDvh.setDvhMaximumDose(
                            dvhAttributes.getDouble(Tag.DVHMaximumDose, -1.0)
                    );
                    rtDvh.setDvhMeanDose(
                            dvhAttributes.getDouble(Tag.DVHMeanDose, -1.0)
                    );

                    dvhs.add(rtDvh);
                }
            }

            rtDose.setRtDvhs(dvhs);
        }

        return rtDose;
    }

    //endregion

    //region DICOM RTPLAN

    public DicomSeriesRtPlan loadDicomRtPlan(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid) {
        DicomSeriesRtPlan rtPlan = new DicomSeriesRtPlan();

        // Load DICOM data
        Attributes dcmAttributes = this.loadWadoDicomInstance(
                studyInstanceUid,
                seriesInstanceUid,
                sopInstanceUid
        );

        rtPlan.setRtPlanLabel(
                dcmAttributes.getString(Tag.RTPlanLabel)
        );
        rtPlan.setRtPlanName(
                dcmAttributes.getString(Tag.RTPlanName)
        );
        rtPlan.setRtPlanDescription(
                dcmAttributes.getString(Tag.RTPlanDescription)
        );
        rtPlan.setRtPlanGeometry(
                dcmAttributes.getString(Tag.RTPlanGeometry)
        );

        // When DoseRefSeq is defined - get prescribed dose from there (in cGy unit)
        Sequence doseRefSeq = dcmAttributes.getSequence(Tag.DoseReferenceSequence);
        if (doseRefSeq != null) {
            for (Attributes doseRefAttributes : doseRefSeq) {

                // POINT (dose reference point specified as ROI)
                if (doseRefAttributes.getString(Tag.DoseReferenceStructureType).equals("POINT")) {
                    // NOOP
                    log.info("Not supported: dose reference point specified as ROI");
                }
                // VOLUME structure is associated with dose (dose reference volume specified as ROI)
                else if (doseRefAttributes.getString(Tag.DoseReferenceStructureType).equals("VOLUME")) {

                    if (doseRefAttributes.contains(Tag.TargetPrescriptionDose)) {
                        float rxDose = doseRefAttributes.getFloat(Tag.TargetPrescriptionDose, 0.0f) * 100;
                        rtPlan.setRxDose(rxDose);
                    }
                }
                // COORDINATES (point specified by Dose Reference Point Coordinates (300A,0018))
                else if (doseRefAttributes.getString(Tag.DoseReferenceStructureType).equals("COORDINATES")) {
                    // NOOP
                    log.info("Not supported: dose reference point specified by coordinates");
                }
                // SITE structure is associated with dose (dose reference clinical site)
                else if (doseRefAttributes.getString(Tag.DoseReferenceStructureType).equals("SITE")) {

                    if (doseRefAttributes.contains(Tag.TargetPrescriptionDose)) {
                        float rxDose = doseRefAttributes.getFloat(Tag.TargetPrescriptionDose, 0.0f) * 100;
                        if (rxDose > rtPlan.getRxDose()) {
                            rtPlan.setRxDose(rxDose);
                        }
                    }
                }
            }
        }

        // When fractionation group sequence is defined get prescribed dose from there (in cGy unit)
        Sequence fractionGroupSeq = dcmAttributes.getSequence(Tag.FractionGroupSequence);
        if (fractionGroupSeq != null && rtPlan.getRxDose() == 0) {

            Attributes fractionGroupAttributes = fractionGroupSeq.get(0);

            if (fractionGroupAttributes.contains(Tag.ReferencedBeamSequence) && fractionGroupAttributes.contains(Tag.NumberOfFractionsPlanned)) {

                int numberOfFractionsPlanned = fractionGroupAttributes.getInt(Tag.NumberOfFractionsPlanned, 0);
                for (Attributes referencedBeam : fractionGroupAttributes.getSequence(Tag.ReferencedBeamSequence)) {

                    if (referencedBeam.contains(Tag.BeamDose)) {
                        float beamDose = referencedBeam.getFloat(Tag.BeamDose, 0.0f);
                        rtPlan.setRxDose(
                                rtPlan.getRxDose() + (beamDose * numberOfFractionsPlanned * 100)
                        );
                    }
                }
            }
        }

        return rtPlan;
    }

    //endregion

    //region WADO

    public InputStream loadWadoDicomStream(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid) {
        InputStream in = null;

        try {
            String compositeQueryUrl = this.baseUrl + wado;
            compositeQueryUrl += "&contentType=" + "application/dicom" +
                    "&studyUID=" + studyInstanceUid +
                    "&seriesUID=" + seriesInstanceUid +
                    "&objectUID=" + sopInstanceUid;

            ClientResponse response = queryConquest(compositeQueryUrl);

            if (response.getStatus() == 200) {
                if (response.hasEntity()) {
                    in = response.getEntityInputStream();
                }
            } else {
                log.warn("WADO HTTP response code: " + response.getStatus());
            }
        } catch (Exception err) {
            log.error("WADO loading the DICOM instance failed!", err);
        }

        return in;
    }

    public Attributes loadWadoDicomInstance(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid) {
        Attributes dcmAttributes = null;

        try {
            String compositeQueryUrl = this.baseUrl + wado;
            compositeQueryUrl += "&contentType=" + "application/dicom" +
                    "&studyUID=" + studyInstanceUid +
                    "&seriesUID=" + seriesInstanceUid +
                    "&objectUID=" + sopInstanceUid;

            ClientResponse response = queryConquest(compositeQueryUrl);

            throwIfResponseStatusIsNotTwoHundred(compositeQueryUrl, response);

            if (response.hasEntity()) {
                DicomInputStream din = null;
                try {
                    din = new DicomInputStream(
                            response.getEntityInputStream()
                    );
                    dcmAttributes = din.readDataset(-1, -1);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    try {
                        if (din != null) {
                            din.close();
                        }
                    } catch (IOException ignore) {
                        log.error(ignore.getMessage(), ignore);
                    }
                }
            }
        } catch (Exception err) {
            String message = "WADO loading the DICOM instance failed!";
            log.error(message, err);
        }

        return dcmAttributes;
    }

    //endregion

    //endregion

    //region Private methods

    private ClientResponse queryConquest(String compositeQueryUrl) {
        Client client = Client.create();
        if (this.username != null && !this.username.isEmpty()) {
            client.addFilter(new HTTPBasicAuthFilter(this.username, this.password));
        }
        WebResource webResource = client.resource(compositeQueryUrl);
        return webResource.get(ClientResponse.class);
    }

    private void throwIfResponseStatusIsNotTwoHundred(String compositeQueryUrl, ClientResponse response) throws Exception {

        if (response.getStatus() != 200) {

            String errorMessage = "There was a problem with your request. ";
            throw new Exception(
                    errorMessage + " URL: " + compositeQueryUrl +
                            " Status code: " + response.getStatus() + " " +
                            response.getStatusInfo().toString()
            );
        }
    }

    private JSONArray getJsonEntityFromString(ClientResponse response, String entityName) throws JSONException {

        String queryResultString = response.getEntity(String.class);
        queryResultString = JsonStringUtil.trimJsonString(queryResultString);
        JSONObject json = new JSONObject(queryResultString);

        return json.getJSONArray(entityName);
    }


    private List<Subject> handlePacsPatientResponse(String queryResultString) {
        List<Subject> results = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(queryResultString);
            JSONArray jsonPatients = json.getJSONArray("Patients");
            results = PacsPatientResponseUnmashaller.unmarshalPacsPatientsResponse(jsonPatients);
        } catch (JSONException e) {
            log.error("There was a problem with the JSON String: " + queryResultString, e);
        }

        return results;
    }

    private List<List<StudySubject>> splitIntoSubLists(List<StudySubject> studySubjectList) {
        int maxElementsPerList = 15;

        List<List<StudySubject>> listOfStudySubjectLists = new ArrayList<>();
        for (int i = 0; i < studySubjectList.size(); i = i + maxElementsPerList) {
            int listSize = studySubjectList.size();
            int nextMaxIndex = i + maxElementsPerList;
            if (listSize < nextMaxIndex) {
                nextMaxIndex = listSize;
            }
            List<StudySubject> subList = studySubjectList.subList(i, nextMaxIndex);
            listOfStudySubjectLists.add(subList);
        }
        return listOfStudySubjectLists;
    }

    private List<Subject> loadPatientsForSublist(List<StudySubject> studySubjectList) throws Exception {
        String concatenatedPatients = "";
        for (StudySubject subject : studySubjectList) {
            if (concatenatedPatients.length() > 0) {
                concatenatedPatients += "%2C";
            }
            concatenatedPatients += subject.getPid();
        }
        return this.loadPatient(concatenatedPatients);
    }


    private void unmarshalStudyProperties(DicomStudy result, JSONObject jsonStudy) throws JSONException {
        result.setStudyInstanceUID(jsonStudy.getString("StudyInstanceUID"));
        result.setStudyDescription(jsonStudy.optString("StudyDescription"));
        result.setModalitiesInStudy(jsonStudy.optString("ModalitiesInStudy"));
        result.setStudyDate(jsonStudy.optString("StudyDate"));
        result.setStudyTime(jsonStudy.optString("StudyTime"));
        JSONArray jsonSeries = jsonStudy.getJSONArray("Series");
        result.setStudySeries(this.unmarshalDicomSeries(jsonSeries));
    }

    private List<DicomSeries> unmarshalDicomSeries(JSONArray jsonDicomSeries) {
        List<DicomSeries> results = new ArrayList<>();

        try {
            for (int j = 0; j < jsonDicomSeries.length(); j++) {
                JSONObject jsonSeries = jsonDicomSeries.getJSONObject(j);

                DicomSeries dicomSeries = unmarshalSingleDicomSeries(jsonSeries);

                results.add(dicomSeries);
            }
        } catch (Exception err) {
            String message = "Cannot unmarshal DICOM series from JSON!";
            log.error(message, err);
        }

        return results;
    }

    private DicomSeries unmarshalSingleDicomSeries(JSONObject jsonSeries) {
        DicomSeries dicomSeries = null;

        try {
            if (jsonSeries.has("Modality")) {
                String modality = jsonSeries.optString("Modality");
                switch (modality) {
                    case "RTPLAN":
                        dicomSeries = unmarshalRtPlanSpecificProperties(jsonSeries);
                        break;
                    case "RTDOSE":
                        dicomSeries = unmarshalRtDoseSpecificProperties(jsonSeries);
                        break;
                    case "RTSTRUCT":
                        dicomSeries = unmarshalRtStructSpecificProperties(jsonSeries);
                        break;
                    case "RTIMAGE":
                        dicomSeries = unmarshalRtImageSpecificProperties(jsonSeries);
                        break;
                    default:
                        dicomSeries = new DicomSeries();
                        dicomSeries.setSeriesNumber(jsonSeries.optString(SERIES_NUMBER));
                        log.debug("No specific case specified to handle modality " + modality +
                                " in unmarshalling process of series. Use default handling.");
                }
                unmarshalDicomSeriesProperties(jsonSeries, dicomSeries);
            }

        } catch (JSONException e) {
            String message = "Error unmarshalling DICOM series from JSON!";
            log.error(message, e);
            log.debug("JSON object: " + jsonSeries.toString());
        }
        return dicomSeries;
    }

    private void unmarshalDicomSeriesProperties(JSONObject jsonSeries, DicomSeries dicomSeries) throws JSONException {
        dicomSeries.setSeriesInstanceUID(jsonSeries.getString(DICOM_SERIES_INSTANCE_UID));
        dicomSeries.setFrameOfReferenceUid(jsonSeries.optString(DICOM_FRAME_OF_REFERENCE_UID));
        dicomSeries.setSeriesDescription(jsonSeries.optString(DICOM_SERIES_DESCRIPTION));
        dicomSeries.setSeriesModality(jsonSeries.optString(DICOM_MODALITY));
        dicomSeries.setSeriesTime(jsonSeries.optString(DICOM_SERIES_TIME));
        dicomSeries.setSeriesDate(jsonSeries.optString(DICOM_SERIES_DATE));
    }


    private DicomSeriesRtImage unmarshalRtImageSpecificProperties(JSONObject jsonSeries) {
        DicomSeriesRtImage dicomSeriesRtImage = new DicomSeriesRtImage();
        dicomSeriesRtImage.setRtImageLabel(jsonSeries.optString(RTIMAGE_LABEL));
        dicomSeriesRtImage.setRtImageName(jsonSeries.optString(RTIMAGE_NAME));
        dicomSeriesRtImage.setRtImageDescription(jsonSeries.optString(RTIMAGE_DESCRIPTION));
        dicomSeriesRtImage.setInstanceCreationDate(jsonSeries.optString(INSTANCE_CREATION_DATE));
        return dicomSeriesRtImage;
    }

    private DicomSeries unmarshalRtStructSpecificProperties(JSONObject jsonSeries) {
        DicomSeriesRtStruct dicomSeriesRtStruct = new DicomSeriesRtStruct();
        dicomSeriesRtStruct.setStructureSetLabel(jsonSeries.optString(RTSTRUCT_STRUCTURE_SET_LABEL));
        dicomSeriesRtStruct.setStructureSetName(jsonSeries.optString(RTSTRUCT_STRUCTURE_SET_NAME));
        dicomSeriesRtStruct.setStructureSetDescription(jsonSeries.optString(RTSTRUCT_STRUCTURE_SET_DESCRIPTION));
        dicomSeriesRtStruct.setStructureSetDate(jsonSeries.optString(RTSTRUCT_STRUCTURE_SET_DATE));
        return dicomSeriesRtStruct;
    }

    private DicomSeriesRtDose unmarshalRtDoseSpecificProperties(JSONObject jsonSeries) {
        DicomSeriesRtDose rtDoseDicomSeries = new DicomSeriesRtDose();
        rtDoseDicomSeries.setDoseUnits(jsonSeries.optString(DOSE_UNIT));
        rtDoseDicomSeries.setDoseType(jsonSeries.optString(DOSE_TYPE));
        rtDoseDicomSeries.setDoseComment(jsonSeries.optString(DOSE_COMMENT));
        rtDoseDicomSeries.setDoseSummationType(jsonSeries.optString(DOSE_SUMMATION_TYPE));
        rtDoseDicomSeries.setInstanceCreationDate(jsonSeries.optString(INSTANCE_CREATION_DATE));
        return rtDoseDicomSeries;
    }

    private DicomSeriesRtPlan unmarshalRtPlanSpecificProperties(JSONObject jsonSeries) {
        DicomSeriesRtPlan rtPlanDicomSeries = new DicomSeriesRtPlan();
        rtPlanDicomSeries.setRtPlanLabel(jsonSeries.optString(RTPLAN_LABEL));
        rtPlanDicomSeries.setRtPlanManufacturerModelName(jsonSeries.optString(RTPLAN_MANUFACTURER_MODEL_NAME));
        rtPlanDicomSeries.setRtPlanName(jsonSeries.optString(RTPLAN_NAME));
        rtPlanDicomSeries.setRtPlanDate(jsonSeries.optString(RTPLAN_DATE));
        rtPlanDicomSeries.setManufacturer(jsonSeries.optString(RTPLAN_MANUFACTURER));
        rtPlanDicomSeries.setRtPlanDescription(jsonSeries.optString(RTPLAN_DESCRIPTION));
        rtPlanDicomSeries.setRtPlanGeometry(jsonSeries.optString(RTPLAN_GEOMETRY));
        return rtPlanDicomSeries;
    }


    private List<DicomImage> unmarshalDicomImages(JSONArray jsonDicomImages) {
        List<DicomImage> images = new ArrayList<>();

        try {
            for (int j = 0; j < jsonDicomImages.length(); j++) {
                JSONObject jsonImage = jsonDicomImages.getJSONObject(j);
                DicomImage image = new DicomImage();
                image.setSopInstanceUID(jsonImage.getString("SOPInstanceUID"));
                image.setSize(jsonImage.optInt("Size", 1024));
                images.add(image);
            }
        } catch (Exception err) {
            String message = "Cannot unmarshalling DICOM series from JSON!";
            log.error(message, err);
        }

        return images;
    }

    //TODO: not used should be removed?
    private List<DicomImage> unmarshalDicomImagesExtended(JSONArray jsonDicomImages) {
        List<DicomImage> images = new ArrayList<>();

        try {
            for (int j = 0; j < jsonDicomImages.length(); j++) {
                JSONObject jsonImage = jsonDicomImages.getJSONObject(j);
                DicomImage image2 = DicomImageFactory.getDicomImage(Constants.DICOM_RTSTRUCT, jsonImage);
                DicomImageWithReferences image = new DicomImageWithReferences(jsonImage.getString("SOPInstanceUID"), jsonImage.optInt("Size", 1024));
                image.setReferencedSopInstance(jsonImage.optString("ReferencedRTSeriesUID"));
                images.add(image);
            }
        } catch (Exception err) {
            String message = "Cannot unmarshalling DICOM series from JSON!";
            log.error(message, err);
        }

        return images;
    }


    private void cacheDicom(String url) {
        try {
            ClientResponse response = queryConquest(url);

            // No caching endpoint, no proxy or something failed
            if (response.getStatus() != 200) {
                log.info("Configured PACS is not DICOM caching proxy, caching is not required.");
            } else { // Otherwise, wait for response regarding what was cached
                String result = response.getEntity(String.class);
                JSONObject found = new JSONObject(result);
            }
        } catch (Exception err) {
            log.error(err.getMessage(), err);
        }
    }

    private InputStream archiveDicom(String zipMode, String archiveEntityType, String dicomPatientId, String dicomEntityUid) {
        String url = this.baseUrl + mode + zipMode;

        if (!"".equals(dicomPatientId) && !"".equals(dicomEntityUid)) {
            url += archiveEntityType + dicomPatientId + ":" + dicomEntityUid;
        }
        url += dum;

        InputStream inputStream = null;
        try {
            Client client = Client.create();
            if (this.username != null && !this.username.isEmpty()) {
                client.addFilter(new HTTPBasicAuthFilter(this.username, this.password));
            }
            WebResource webResource = client.resource(url);
            inputStream = webResource.get(InputStream.class);
        } catch (Exception err) {
            log.error(err.getMessage(), err);
        }

        return inputStream;
    }

    //endregion

    //region Static methods

    private static final ICC_ColorSpace LAB = new ICC_ColorSpace(ICC_Profile.getInstance(ICC_ColorSpace.CS_sRGB));

    private static Color getRGBColor(int pGray, float[] labColour, int[] rgbColour) {
        int r, g, b;
        if (labColour != null) {
            if (LAB == null) {
                r = g = b = (int) (labColour[0] * 2.55f);
            } else {
                float[] rgb = LAB.toRGB(labColour);
                r = (int) (rgb[0] * 255);
                g = (int) (rgb[1] * 255);
                b = (int) (rgb[2] * 255);
            }
        } else if (rgbColour != null) {
            r = rgbColour[0];
            g = rgbColour[1];
            b = rgbColour[2];
            if (r > 255 || g > 255 || b > 255) {
                r >>= 8;
                g >>= 8;
                b >>= 8;
            }
        } else {
            r = g = b = pGray >> 8;
        }
        r &= 0xFF;
        g &= 0xFF;
        b &= 0xFF;
        int conv = (r << 16) | (g << 8) | b | 0x1000000;
        return new Color(conv);
    }

    /**
     * Calculates the structure plane thickness
     *
     * @return structure plane thickness
     */
    private static double calculatePlaneThickness(Map<Double, ArrayList<DicomRtContour>> planesMap) {
        // Sort the list of z coordinates
        List<Double> planes = new ArrayList<>(planesMap.keySet());
        Collections.sort(planes);

        // Set maximum thickness as initial value
        double thickness = 10000;

        // Compare z of each two next to each other planes in order to find the minimal shift in z
        for (int i = 1; i < planes.size(); i++) {
            double newThickness = planes.get(i) - planes.get(i - 1);
            if (newThickness < thickness) {
                thickness = newThickness;
            }
        }

        // When no other then initial thickness was detected, set 0
        if (thickness > 9999) {
            thickness = 0.0;
        }

        return thickness;
    }

    //endregion

}
