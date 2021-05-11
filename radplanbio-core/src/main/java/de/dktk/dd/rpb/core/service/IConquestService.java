/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
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

import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.domain.pacs.*;
import org.dcm4che3.data.Attributes;

import java.io.InputStream;
import java.util.List;

/**
 * Conquest PACS service interface
 * The service provides client access to conquest specific functions and deployed lua scripts
 *
 * @author tomas@skripcak.net
 * @since 29 Nov 2013
 */
public interface IConquestService {

    //region Methods

    //region Setup

    /**
     * Setup web based communication with Conquest PACS
     *
     * @param baseUrl web location of conquest dgate
     */
    void setupConnection(String baseUrl);

    //endregion

    //region RT Treatment Case

    RtTreatmentCase loadRtTreatmentCase(String dicomPatientId, DicomStudy dicomStudy);

    //endregion

    //region DICOM Patient


    /**
     * Loads an array of patient information including studies per patient
     *
     * @param dicomPatientId PatientId (* for all)
     * @return List of subjects
     */
    List<Subject> loadPatient(String dicomPatientId) throws Exception;

    /**
     * Loads an array of patient information including studies per patient
     *
     * @param studySubjectList List of StudySubjects
     * @return List of subjects
     */
    List<Subject> loadPatients(List<StudySubject> studySubjectList) throws Exception;

    boolean movePatient(String dicomPatientId, String destinationAet);

    //endregion

    //region DICOM Studies

    /**
     * Load a DICOM study for specific patient and study
     *
     * @param dicomPatientId DICOM PatientID tag value
     * @param dicomStudyUid  DICOM StudyInstanceUID tag value
     * @return DICOM study entity
     */
    DicomStudy loadPatientStudy(String dicomPatientId, String dicomStudyUid);

    /**
     * Load a list of DICOM studies for specific patient
     *
     * @param dicomPatientId DICOM PatientID tag  value
     * @return list of DICOM studies
     */
    List<DicomStudy> loadPatientStudies(String dicomPatientId);

    /**
     * Load a list of DICOM studies for specific patient and studies
     *
     * @param dicomPatientId    DICOM PatientID tag
     * @param dicomStudyUidList List of DICOM StudyInstanceUid tags
     * @return list of DICOM studies
     */
    List<DicomStudy> loadPatientStudies(String dicomPatientId, List<ItemData> dicomStudyUidList);

    /**
     * Cache specified DICOM study into the DICOM proxy (if present) from DICOM leaf nodes
     *
     * @param dicomPatientId   DICOM PatientID tag value
     * @param dicomStudyUid    DICOM StudyInstanceUID tag value
     * @param dicomSeriesCount Number of DICOM series within specified study
     */
    void cacheDicomStudy(String dicomPatientId, String dicomStudyUid, int dicomSeriesCount);

    /**
     * Create zip archive stream from specified DICOM study
     *
     * @param dicomPatientId DICOM PatientID tag value
     * @param dicomStudyUid  DICOM StudyInstanceUID tag value
     * @return zip archive stream
     */
    InputStream archivePatientStudy(String dicomPatientId, String dicomStudyUid);

    /**
     * Sends a specific DICOM study for a specific patient to another DICOM provider
     *
     * @param dicomPatientId DICOM PatientID tag value
     * @param dicomStudyUid  DICOM StudyInstanceUID tag value
     * @param destinationAet DICOM Application Entity of the destination
     * @return boolean success
     */
    boolean moveDicomStudy(String dicomPatientId, String dicomStudyUid, String destinationAet);

    //endregion

    //region DICOM Series

    /**
     * Load a DICOM series for specific patient and study and series
     *
     * @param dicomPatientId DICOM PatientID tag value
     * @param dicomStudyUid  DICOM StudyInstanceUID tag value
     * @param dicomSeriesUid DICOM SeriesInstanceUID tag value
     * @return DICOM series entity
     */
    DicomSeries loadStudySeries(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid);

    /**
     * Cache specified DICOM series into the DICOM proxy (if present) from DICOM leaf nodes
     *
     * @param dicomPatientId  DICOM PatientID tag value
     * @param dicomStudyUid   DICOM StudyInstanceUID tag value
     * @param dicomSeriesUid  DICOM SeriesIntanceUID tag value
     * @param dicomFilesCount Number of DICOM SOP instances within specified series
     */
    void cacheDicomSeries(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid, int dicomFilesCount);

    /**
     * Create zip archive stream from specified DICOM series
     *
     * @param dicomPatientId DICOM PatientID tag value
     * @param dicomSeriesUid  DICOM SeriesInstanceUID tag value
     * @return zip archive stream
     */
    InputStream archivePatientSeries(String dicomPatientId, String dicomSeriesUid);

    /**
     * Sends a specific DICOM series for a specific study of a patient to another DICOM provider
     *
     * @param dicomPatientId DICOM PatientID tag value
     * @param dicomStudyUid  DICOM StudyInstanceUID tag value
     * @param dicomSeriesUid DICOM SeriesInstanceUID tag value
     * @param destinationAet DICOM Application Entity of the destination
     * @return boolean success
     */
    boolean moveDicomSeries(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid, String destinationAet);

    //endregion

    //region DICOM Instances

    boolean instanceExists(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid, String dicomInstanceUid);

    //endregion

    //region DICOM RTSTRUCT

    DicomRtStructureSet loadDicomRtStructureSet(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid, RtTreatmentCase tc);

    //endregion

    //region DICOM RTDOSE

    DicomRtDose loadDicomRtDose(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid);

    //endregion

    //region DICOM RTPLAN

    DicomRtPlan loadDicomRtPlan(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid);

    //endregion

    //region WADO

    InputStream loadWadoDicomStream(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid);

    Attributes loadWadoDicomInstance(String studyIntanceUid, String seriesInstanceUid, String sopInstanceUid);

    //endregion

    //endregion

}
