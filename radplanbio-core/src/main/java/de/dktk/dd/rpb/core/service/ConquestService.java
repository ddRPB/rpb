/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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

import de.dktk.dd.rpb.core.domain.edc.ItemDefinition;
import de.dktk.dd.rpb.core.domain.pacs.*;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.json.JSONObject;
import org.json.JSONArray;

import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Transactional;
import javax.inject.Named;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Conquest PACS service interface implementation
 *
 * The service provides client access to conquest specific functions and also deployed lua scripts
 *
 * @author tomas@skripcak.net
 * @since 29 Nov 2013
 */
@Transactional(readOnly = true)
@Named("pacsService")
public class ConquestService implements IConquestService {

    //region Enums

    /**
     * OpenClinica REST full URLs casebook formats
     */
    public enum ConquestMode {
        JSON_STUDIES("radplanbiostudies"), JSON_SERIES("radplanbioseries");

        @SuppressWarnings("unused")
        private String value;

        ConquestMode(String value) {
            this.value = value;
        }
    }

    //endregion

    //region Finals

    private static final Logger log = Logger.getLogger(ConquestService.class);
    private static final String mode = "?mode=";
    private static final String wado = "?requestType=WADO";

    //endregion

    //region Members

    private String baseUrl;

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
    public void setupConnection(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    //endregion

    //region RT treatment case

    public RtTreatmentCase loadRtTreatmentCase(String dicomPatientId, DicomStudy dicomStudy) {
        RtTreatmentCase result = new RtTreatmentCase();

        if (dicomStudy != null) {

            // RTSTRUCT
            List<DicomSerie> rtStructSeries = dicomStudy.getSeriesByModality("RTSTRUCT");
            if (rtStructSeries != null) {
                for (DicomSerie rtStructSerie : rtStructSeries) {
                    DicomSerie dicomSeriesWithImages = this.loadStudySeries(
                            dicomPatientId,
                            dicomStudy.getStudyInstanceUID(),
                            rtStructSerie.getSeriesInstanceUID()
                    );

                    if (dicomSeriesWithImages != null && dicomSeriesWithImages.getSerieImages() != null && dicomSeriesWithImages.getSerieImages().size() > 0) {

                        List<DicomRtStructureSet> structureSets = new ArrayList<DicomRtStructureSet>();
                        for (DicomImage instance : dicomSeriesWithImages.getSerieImages()) {
                            structureSets.add(
                                    this.loadDicomRtStructureSet(
                                            dicomStudy.getStudyInstanceUID(),
                                            rtStructSerie.getSeriesInstanceUID(),
                                            dicomSeriesWithImages.getSerieImages().get(0).getSopInstanceUID()
                                    )
                            );
                        }

                        result.setRtStructureSets(structureSets);
                    }
                }
            }

            // RTPLANs
            List<DicomSerie> rtPlanSeries = dicomStudy.getSeriesByModality("RTPLAN");
            for (DicomSerie planSerie : rtPlanSeries) {
                DicomSerie dicomSeriesWithImages = this.loadStudySeries(
                        dicomPatientId,
                        dicomStudy.getStudyInstanceUID(),
                        planSerie.getSeriesInstanceUID()
                );

                if (dicomSeriesWithImages != null && dicomSeriesWithImages.getSerieImages() != null) {

                    List<DicomRtPlan> plans = new ArrayList<DicomRtPlan>();
                    for (DicomImage instance : dicomSeriesWithImages.getSerieImages()) {
                        plans.add(
                            this.loadDicomRtPlan(
                                    dicomStudy.getStudyInstanceUID(),
                                    planSerie.getSeriesInstanceUID(),
                                    instance.getSopInstanceUID()
                                )
                        );
                    }

                    result.setRtPlans(plans);
                }
            }

            // RTDOSEs
            List<DicomSerie> rtDoseSeries = dicomStudy.getSeriesByModality("RTDOSE");
            for (DicomSerie doseSerie : rtDoseSeries) {
                DicomSerie dicomSeriesWithImages = this.loadStudySeries(
                        dicomPatientId,
                        dicomStudy.getStudyInstanceUID(),
                        doseSerie.getSeriesInstanceUID()
                );

                if (dicomSeriesWithImages != null && dicomSeriesWithImages.getSerieImages() != null) {

                    List<DicomRtDose> doses = new ArrayList<DicomRtDose>();
                    for (DicomImage instance : dicomSeriesWithImages.getSerieImages()) {
                        doses.add(
                                this.loadDicomRtDose(
                                        dicomStudy.getStudyInstanceUID(),
                                        doseSerie.getSeriesInstanceUID(),
                                        instance.getSopInstanceUID()
                                )
                        );
                    }

                    result.setRtDoses(doses);
                }

            }
        }

        return result;
    }

    //endregion

    //region DICOM studies

    /**
     * {@inheritDoc}
     */
    public DicomStudy loadPatientStudy(String dicomPatientId, String dicomStudyUid) {
        DicomStudy result = null;

        try {
            String compositeQueryUrl = this.baseUrl + mode + ConquestMode.JSON_STUDIES.value;
            if (!"".equals(dicomPatientId)) {
                compositeQueryUrl += "&patientidmatch=" + dicomPatientId;
            }
            if (!"".equals(dicomStudyUid)) {
                compositeQueryUrl +=  "&studyUID=" + dicomStudyUid;
            }

            Client client = Client.create();
            WebResource webResource = client.resource(compositeQueryUrl);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (response.hasEntity()) {
                String jsonstring = response.getEntity(String.class);
                jsonstring = jsonstring.replaceAll("(\\r|\\n)", ""); // Get rid of carriage returns and new rows that will mess up string to JSON conversion

                JSONObject json = new JSONObject(jsonstring);
                JSONArray jsonStudies = json.getJSONArray("Studies");

                for (int i = 0; i < jsonStudies.length(); i++) {

                    JSONObject jsonStudy = jsonStudies.getJSONObject(i);
                    result = new DicomStudy();

                    result.setStudyInstanceUID(jsonStudy.getString("StudyInstanceUID"));

                    // Later it would be better to pass this directly to ConQuest query so I do not have to filter it here
                    // When the study query parameter is specified and it does not match to current study skip to next
                    if (dicomStudyUid != null && !dicomStudyUid.equals(result.getStudyInstanceUID())) {
                        continue;
                    }

                    result.setStudyDate(jsonStudy.optString("StudyDate"));
                    result.setStudyDescription(jsonStudy.optString("StudyDescription"));

                    JSONArray jsonSeries = jsonStudy.getJSONArray("Series");
                    result.setStudySeries(
                            this.unmarshallDicomSeries(jsonSeries)
                    );
                }
            }
        }
        catch (Exception err) {
            String message= "Loading the DICOM series failed!\nThere appears to be a problem with the server connection.";
            log.error(message, err);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<DicomStudy> loadPatientStudies(String dicomPatientId) {
        List<DicomStudy> resultStudies = new ArrayList<DicomStudy>();

        try {
            String compositeQueryUrl = this.baseUrl + mode + ConquestMode.JSON_STUDIES.value;
            if (!"".equals(dicomPatientId)) {
                compositeQueryUrl += "&patientidmatch=" + dicomPatientId;
            }

            Client client = Client.create();
            WebResource webResource = client.resource(compositeQueryUrl);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (response.hasEntity()) {
                String jsonstring = response.getEntity(String.class);
                jsonstring = jsonstring.replaceAll("(\\r|\\n)", ""); // Get rid of carriage returns and new rows that will mess up string to JSON conversion

                JSONObject json = new JSONObject(jsonstring);
                JSONArray jsonStudies = json.getJSONArray("Studies");

                for (int i = 0; i < jsonStudies.length(); i++) {

                    JSONObject jsonStudy = jsonStudies.getJSONObject(i);
                    DicomStudy study = new DicomStudy();

                    study.setStudyInstanceUID(jsonStudy.getString("StudyInstanceUID"));

                    study.setStudyDate(jsonStudy.optString("StudyDate"));
                    study.setStudyDescription(jsonStudy.optString("StudyDescription"));

                    JSONArray jsonSeries = jsonStudy.getJSONArray("Series");
                    study.setStudySeries(
                            this.unmarshallDicomSeries(jsonSeries)
                    );

                    resultStudies.add(study);
                }
            }
        }
        catch (Exception err) {
            String message= "Loading the DICOM series failed!\nThere appears to be a problem with the server connection.";
            log.error(message, err);
        }

        return resultStudies;
    }

    /**
     * {@inheritDoc}
     */
    public List<DicomStudy> loadPatientStudies(String dicomPatientId, List<ItemDefinition> dicomStudyUidList) {
        List<DicomStudy> resultStudies = new ArrayList<DicomStudy>();

        try {
            // I have to think if PACS connection should depend on RPB study partner site or RPB user account partner site
            String compositeQueryUrl = this.baseUrl + mode + ConquestMode.JSON_STUDIES.value + "&patientidmatch=" + dicomPatientId;

            // If there is just one study in dicomStudyUidList load only this one (otherwise query all studies)
            if (dicomStudyUidList.size() == 1) {
                compositeQueryUrl += "&studyUID=" + dicomStudyUidList.get(0).getValue();
            }

            Client client = Client.create();
            WebResource webResource = client.resource(compositeQueryUrl);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (response.hasEntity()) {
                String jsonstring = response.getEntity(String.class);
                jsonstring = jsonstring.replaceAll("(\\r|\\n)", ""); // Get rid of carriage returns and new rows that will mess up string to JSON conversion

                if (!jsonstring.equals("")) {
                    JSONObject json = new JSONObject(jsonstring);
                    JSONArray jsonStudies = json.getJSONArray("Studies");

                    for (int i = 0; i < jsonStudies.length(); i++) {

                        JSONObject jsonStudy = jsonStudies.getJSONObject(i);
                        DicomStudy study = new DicomStudy();

                        study.setStudyInstanceUID(jsonStudy.getString("StudyInstanceUID"));

                        // Filter to return only studies listed in parameter
                        Boolean studyFound = false;
                        for (ItemDefinition itemDef : dicomStudyUidList) {
                            if (itemDef.getValue() != null && itemDef.getValue().equals(study.getStudyInstanceUID())) {
                                studyFound = true;
                                break;
                            }
                        }
                        if (!studyFound) { continue; }

                        study.setStudyDate(jsonStudy.optString("StudyDate"));
                        study.setStudyDescription(jsonStudy.optString("StudyDescription"));

                        JSONArray jsonSeries = jsonStudy.getJSONArray("Series");
                        study.setStudySeries(
                                this.unmarshallDicomSeries(jsonSeries)
                        );

                        resultStudies.add(study);
                    }
                }
            }
        }
        catch (Exception err) {
            String message= "Loading the DICOM series failed!\nThere appears to be a problem with the server connection.";
            log.error(message, err);
        }

        return resultStudies;
    }

    //endregion

    //region DICOM series

    public DicomSerie loadStudySeries(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid) {
        DicomSerie result = null;

        try {
            String compositeQueryUrl = this.baseUrl + mode + ConquestMode.JSON_SERIES.value;
            if (!"".equals(dicomPatientId)) {
                compositeQueryUrl += "&patientidmatch=" + dicomPatientId;
            }
            if (!"".equals(dicomStudyUid)) {
                compositeQueryUrl +=  "&studyUID=" + dicomStudyUid;
            }
            if (!"".equals(dicomSeriesUid)) {
                compositeQueryUrl += "&seriesUID=" + dicomSeriesUid;
            }

            Client client = Client.create();
            WebResource webResource = client.resource(compositeQueryUrl);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (response.hasEntity()) {
                String jsonstring = response.getEntity(String.class);
                jsonstring = jsonstring.replaceAll("(\\r|\\n)", ""); // Get rid of carriage returns and new rows that will mess up string to JSON conversion

                JSONObject json = new JSONObject(jsonstring);
                JSONArray jsonSeries = json.getJSONArray("Series");

                for (int i = 0; i < jsonSeries.length(); i++) {

                    JSONObject jsonStudy = jsonSeries.getJSONObject(i);
                    result = new DicomSerie();

                    result.setSeriesInstanceUID(jsonStudy.getString("SeriesInstanceUID"));
                    result.setSeriesDescription(jsonStudy.optString("SeriesDescription"));

                    JSONArray jsonImages = jsonStudy.getJSONArray("Images");
                    result.setSerieImages(
                            this.unmarshallDicomImages(jsonImages)
                    );
                }
            }
        }
        catch (Exception err) {
            String message= "Loading the DICOM images failed!\nThere appears to be a problem with the server connection.";
            log.error(message, err);
        }

        return result;
    }

    //endregion

    //region DICOM RTSTRUCT

    public DicomRtStructureSet loadDicomRtStructureSet(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid) {
        DicomRtStructureSet structureSet = new DicomRtStructureSet();

        // Load DICOM data
        Attributes dcmAttributes = this.loadWadoDicomInstance(
                studyInstanceUid,
                seriesInstanceUid,
                sopInstanceUid
        );

        // Construct StructureSet object
        structureSet.setLabel(
            dcmAttributes.getString(Tag.StructureSetLabel)
        );

        // Construct list of structures
        Sequence ssRoiSeq = dcmAttributes.getSequence(Tag.StructureSetROISequence);
        List<DicomRtStructure> structures = new ArrayList<DicomRtStructure>();
        for (Attributes ssRoiAttributes : ssRoiSeq) {
            DicomRtStructure rtStructure = new DicomRtStructure();
            rtStructure.setRoiName(
                ssRoiAttributes.getString(Tag.ROIName)
            );
            rtStructure.setRoiNumber(
                ssRoiAttributes.getInt(Tag.ROINumber, 0)
            );

            structures.add(rtStructure);
        }

        // Add structures to the structure set
        structureSet.setStructures(
                structures
        );

        Sequence rtRoiObservationSeq = dcmAttributes.getSequence(Tag.RTROIObservationsSequence);
        for (Attributes rtRoiObservationAttributes : rtRoiObservationSeq) {
            DicomRtStructure rtStructure = structureSet.getRtStructureByRoiNumber(
                rtRoiObservationAttributes.getInt(Tag.ReferencedROINumber, 0)
            );

            rtStructure.setRtRoiInterpretedType(
                rtRoiObservationAttributes.getString(Tag.RTROIInterpretedType)
            );
        }

        return structureSet;
    }

    //endregion

    //region DICOM RTDOSE

    public DicomRtDose loadDicomRtDose(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid) {
        DicomRtDose rtDose = new DicomRtDose();

        // Load DICOM data
        Attributes dcmAttributes = this.loadWadoDicomInstance(
                studyInstanceUid,
                seriesInstanceUid,
                sopInstanceUid
        );

        rtDose.setSopInstanceUid(
            dcmAttributes.getString(Tag.SOPInstanceUID)
        );
        rtDose.setDoseUnit(
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
            List<DicomRtDvh> dvhs = new ArrayList<DicomRtDvh>();
            for (Attributes dvhAttributes : dvhSeq) {

                // Need to refer to delineated contour
                DicomRtDvh rtDvh = null;
                Sequence dvhRefRoiSeq = dvhAttributes.getSequence(Tag.DVHReferencedROISequence);
                if (dvhRefRoiSeq == null) {
                    continue;
                }
                else if (dvhRefRoiSeq.size() == 1) {
                    rtDvh = new DicomRtDvh();
                    Attributes dvhRefRoiAttributes = dvhRefRoiSeq.get(0);
                    rtDvh.setReferencedRoiNumber(
                        dvhRefRoiAttributes.getInt(Tag.ReferencedROINumber, -1)
                    );
                }

                if (rtDvh != null) {
                    // Convert Differential DVH to Cumulative
                    if (dvhSeq.get(0).getString(Tag.DVHType).equals("DIFFERENTIAL")) {

                    }
                    // Cumulative
                    else {
                        rtDvh.setDvhData(
                            dvhAttributes.getDoubles(Tag.DVHData)
                        );
                        rtDvh.setDvhNumberOfBins(
                            dvhAttributes.getInt(Tag.DVHNumberOfBins, 0)
                        );
                    }

                    rtDvh.setType(
                        dvhAttributes.getString(Tag.DVHType)
                    );
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

    public DicomRtPlan loadDicomRtPlan(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid) {
        DicomRtPlan rtPlan = new DicomRtPlan();

        // Load DICOM data
        Attributes dcmAttributes = this.loadWadoDicomInstance(
                studyInstanceUid,
                seriesInstanceUid,
                sopInstanceUid
        );

        rtPlan.setLabel(
            dcmAttributes.getString(Tag.RTPlanLabel)
        );
        rtPlan.setName(
            dcmAttributes.getString(Tag.RTPlanName)
        );


        // When DoseRefSeq is defined - get prescribed dose from there (in cGy unit)
        Sequence doseRefSeq = dcmAttributes.getSequence(Tag.DoseReferenceSequence);
        if (doseRefSeq != null) {
            for (Attributes doseRefAttributes : doseRefSeq) {
                if (doseRefAttributes.getString(Tag.DoseReferenceStructureType).equals("SITE")) {
                    float rxDose = doseRefAttributes.getFloat(Tag.TargetPrescriptionDose, 0.0f) * 100;
                    if (rxDose > rtPlan.getRxDose()) {
                        rtPlan.setRxDose(rxDose);
                    }
                }
                else if (doseRefAttributes.getString(Tag.DoseReferenceStructureType).equals("VOLUME")) {
                    float rxDose = doseRefAttributes.getFloat(Tag.TargetPrescriptionDose, 0.0f) * 100;
                    rtPlan.setRxDose(rxDose);
                }
            }
        }

        // When fractionation group sequence is defined get prescribed dose from there (in cGy unit)
        Sequence fractionGroupSeq = dcmAttributes.getSequence(Tag.FractionGroupSequence);
        if (fractionGroupSeq != null && rtPlan.getRxDose() == 0) {
            Attributes fractionGroupAttributes = fractionGroupSeq.get(0);

            Sequence referencedBeamSeq = fractionGroupAttributes.getSequence(Tag.ReferencedBeamSequence);
            int numberOfFractionsPlanned = fractionGroupAttributes.getInt(Tag.NumberOfBeams, -1);

            if (referencedBeamSeq != null && numberOfFractionsPlanned != -1) {
                for (Attributes referencedBeam : referencedBeamSeq) {
                    float beamDose = referencedBeam.getFloat(Tag.BeamDose, 0.0f);
                    rtPlan.setRxDose(
                            rtPlan.getRxDose() + beamDose * numberOfFractionsPlanned * 100
                    );
                }
            }
        }

        return rtPlan;
    }

    //endregion

    //region WADO

    public Attributes loadWadoDicomInstance(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid) {
        Attributes dcmAttributes = null;

        try {
            String compositeQueryUrl = this.baseUrl + wado;
            compositeQueryUrl += "&contentType=" + "application/dicom" +
                    "&studyUID=" + studyInstanceUid +
                    "&seriesUID=" + seriesInstanceUid +
                    "&objectUID=" + sopInstanceUid;

            Client client = Client.create();
            WebResource webResource = client.resource(compositeQueryUrl);
            ClientResponse response = webResource.get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            if (response.hasEntity()) {
                DicomInputStream din = null;
                try {
                    din = new DicomInputStream(
                        response.getEntityInputStream()
                    );
                    dcmAttributes = din.readDataset(-1, -1);
                }
                catch (IOException e) {
                    log.error(e);
                }
                finally {
                    try {
                        if (din != null) {
                            din.close();
                        }
                    }
                    catch (IOException ignore) {
                        log.error(ignore);
                    }
                }
            }
        }
        catch (Exception err) {
            String message= "WADO loading the DICOM instance failed!";
            log.error(message, err);
        }

        return dcmAttributes;
    }

    //endregion

    //endregion

    //region Private methods

    private List<DicomSerie> unmarshallDicomSeries(JSONArray jsonDicomSeries) {
        List<DicomSerie> series = new ArrayList<DicomSerie>();

        try {
            for (int j = 0; j < jsonDicomSeries.length(); j++) {
                JSONObject jsonSerie = jsonDicomSeries.getJSONObject(j);
                DicomSerie serie = new DicomSerie();

                serie.setSeriesInstanceUID(jsonSerie.getString("SeriesInstanceUID"));
                serie.setSeriesDescription(jsonSerie.optString("SeriesDescription"));
                serie.setSeriesModality(jsonSerie.optString("Modality"));
                serie.setSeriesTime(jsonSerie.optString("SeriesTime"));

                series.add(serie);
            }
        }
        catch (Exception err) {
            String message= "Cannot umarshall DICOM series from JSON!";
            log.error(message, err);
        }

        return series;
    }

    private List<DicomImage> unmarshallDicomImages(JSONArray jsonDicomImages) {
        List<DicomImage> images = new ArrayList<DicomImage>();

        try {
            for (int j = 0; j < jsonDicomImages.length(); j++) {
                JSONObject jsonImage = jsonDicomImages.getJSONObject(j);
                DicomImage image = new DicomImage();
                image.setSopInstanceUID(jsonImage.getString("SOPInstanceUID"));
                images.add(image);
            }
        }
        catch (Exception err) {
            String message= "Cannot umarshall DICOM series from JSON!";
            log.error(message, err);
        }

        return images;
    }

    //endregion

}
