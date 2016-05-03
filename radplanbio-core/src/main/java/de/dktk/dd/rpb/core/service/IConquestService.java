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
import org.dcm4che3.data.Attributes;

import java.util.List;

/**
 * Conquest PACS service interface
 *
 * The service provides client access to conquest specific functions and also deployed lua scripts
 *
 * @author tomas@skripcak.net
 * @since 29 Nov 2013
 */
public interface IConquestService {

    //region Methods

    //region Setup

    /**
     * Setup web based communication with Conquest PACS
     * @param baseUrl web location of conquest dgate
     */
    void setupConnection(String baseUrl);

    //endregion

    //region RT treatment case

    RtTreatmentCase loadRtTreatmentCase(String dicomPatientId, DicomStudy dicomStudy);

    //endregion

    //region DICOM studies

    /**
     * Load a DICOM study for specific patient and study
     * @param dicomPatientId DICOM PatientID tag
     * @param dicomStudyUid DICOM StudyInstanceUid tag
     * @return DICOM study entity
     */
    DicomStudy loadPatientStudy(String dicomPatientId, String dicomStudyUid);

    /**
     * Load a list of DICOM studies for specific patient
     * @param dicomPatientId DICOM PatientID tag
     * @return list of DICOM studies
     */
    List<DicomStudy> loadPatientStudies(String dicomPatientId);

    /**
     * Load a list of DICOM studies for specific patient and studies
     * @param dicomPatientId DICOM PatientID tag
     * @param dicomStudyUidList List of DICOM StudyInstanceUid tags
     * @return list of DICOM studies
     */
    List<DicomStudy> loadPatientStudies(String dicomPatientId,  List<ItemDefinition> dicomStudyUidList);

    //endregion

    //region DICOM series

    DicomSerie loadStudySeries(String dicomPatientId, String dicomStudyUid, String dicomSeriesUid);

    //endregion

    //region DICOM RTSTRUCT

    DicomRtStructureSet loadDicomRtStructureSet(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid);

    //endregion

    //region DICOM RTDOSE

    DicomRtDose loadDicomRtDose(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid);

    //endregion

    //region DICOM RTPLAN

    DicomRtPlan loadDicomRtPlan(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid);

    //endregion

    //region WADO

    Attributes loadWadoDicomInstance(String studyIntanceUid, String seriesInstanceUid, String sopIntanceUid);

    //endregion

    //endregion

}
