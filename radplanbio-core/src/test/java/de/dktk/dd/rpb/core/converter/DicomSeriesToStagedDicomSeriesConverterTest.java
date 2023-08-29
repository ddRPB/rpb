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

package de.dktk.dd.rpb.core.converter;

import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomSeries;
import de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class, LoggerFactory.class})
public class DicomSeriesToStagedDicomSeriesConverterTest {
    List<DicomSeries> clinicalSeriesList;
    List<DicomSeries> stageOneSeriesList;
    List<DicomSeries> stageTwoSeriesList;
    String uidPrefix = "1.2.826.0.1.3680043.9.7275.0.";
    String partnerSideCode = "DD";
    String edcCode = "PR";

    @Before
    public void setUp() throws Exception {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        clinicalSeriesList = new ArrayList<DicomSeries>();
        stageOneSeriesList = new ArrayList<DicomSeries>();
        stageTwoSeriesList = new ArrayList<DicomSeries>();
    }

    // region getStagedDicomSeries (Stage one / Study zero context)

    @Test
    public void getStagedDicomSeries_returns_empty_list_if_study_lists_are_empty() {
        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                stageOneSeriesList, stageTwoSeriesList, uidPrefix, partnerSideCode, edcCode);

        assertEquals(0, stagedSeries.size());
    }

    @Test
    public void getStagedDicomSeries_returns_stage_one_element() {
        String dummyDescriptionOne = "dummy-uid-one";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID("dummy-uid-one");
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        stageOneSeriesList.add(seriesOne);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                stageOneSeriesList, null, uidPrefix, partnerSideCode, edcCode);

        assertEquals("Should have one element", 1, stagedSeries.size());
        assertEquals(
                "Should be original description, because it has no prefix",
                dummyDescriptionOne, stagedSeries.get(0).getSeriesDescription()
        );
    }

    @Test
    public void getStagedDicomSeries_returns_stage_one_element_with_description_without_edc_code_prefix() {
        String edcPrefix = DicomStudyDescriptionEdcCodeUtil.getEdcCodePattern("Edc-Code");
        String dummyDescriptionOne = "dummy-uid-one";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID(edcPrefix + "dummy-uid-one");
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        stageOneSeriesList.add(seriesOne);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                stageOneSeriesList, stageTwoSeriesList, uidPrefix, partnerSideCode, edcCode);

        assertEquals("Should have one element", 1, stagedSeries.size());
        assertEquals(
                "Should be the description without prefix",
                dummyDescriptionOne, stagedSeries.get(0).getSeriesDescription()
        );
    }

    @Test
    public void getStagedDicomSeries_returns_two_stage_one_elements() {
        String dummyDescriptionOne = "dummy-uid-one";
        String dummyDescriptionTwo = "dummy-uid-two";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID("dummy-uid-one");
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        stageOneSeriesList.add(seriesOne);

        DicomSeries serieTwo = new DicomSeries();
        serieTwo.setSeriesModality("dummy-modality");
        serieTwo.setSeriesInstanceUID("dummy-uid-one");
        serieTwo.setSeriesDescription(dummyDescriptionTwo);
        stageOneSeriesList.add(serieTwo);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                stageOneSeriesList, stageTwoSeriesList, uidPrefix, partnerSideCode, edcCode);

        assertEquals("Should have two elements", 2, stagedSeries.size());
        assertEquals(
                "Should be the description of the second element",
                dummyDescriptionTwo,
                stagedSeries.get(1).getSeriesDescription());
    }

    @Test
    public void getStagedDicomSeries_stage_one_element_is_marked_if_stage_two_representation_exists() {
        String dummyDescriptionOne = "dummy-uid-one";
        String dummyDescriptionTwo = "dummy-uid-two";

        String stageOneUid = "1.2.826.0.1.3680043.9.7275.0.25694151917667784091202966663655420";
        String stageTwoUid = "1.2.826.0.1.3680043.9.7275.0.28119988438981462564481418230477521";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID(stageOneUid);
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        stageOneSeriesList.add(seriesOne);

        DicomSeries seriesOneStageTwo = new DicomSeries();
        BeanUtils.copyProperties(seriesOne, seriesOneStageTwo);
        seriesOneStageTwo.setSeriesInstanceUID(stageTwoUid);
        stageTwoSeriesList.add(seriesOneStageTwo);

        DicomSeries seriesTwo = new DicomSeries();
        seriesTwo.setSeriesModality("dummy-modality");
        seriesTwo.setSeriesInstanceUID("dummy-uid-one");
        seriesTwo.setSeriesDescription(dummyDescriptionTwo);
        stageOneSeriesList.add(seriesTwo);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                stageOneSeriesList, stageTwoSeriesList, uidPrefix, partnerSideCode, edcCode);

        assertEquals("Should have two elements", 2, stagedSeries.size());
        assertEquals(
                "Has a second stage representation",
                true,
                stagedSeries.get(0).isStageTwoRepresentation()
        );
    }

    // endregion

    // region getStagedDicomSeries (clinical context)

    @Test
    public void getStagedDicomSeries_clinical_context_returns_empty_list_if_study_lists_are_empty() {
        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                clinicalSeriesList, stageOneSeriesList, uidPrefix);

        assertEquals(0, stagedSeries.size());
    }

    @Test
    public void getStagedDicomSeries_clinical_context_returns_clinical_element() {
        String dummyDescriptionClinic = "dummy-uid-clinic";

        DicomSeries clinicalSeries = new DicomSeries();
        clinicalSeries.setSeriesModality("dummy-modality");
        clinicalSeries.setSeriesInstanceUID("dummy-uid-one");
        clinicalSeries.setSeriesDescription(dummyDescriptionClinic);
        clinicalSeriesList.add(clinicalSeries);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                clinicalSeriesList, stageOneSeriesList, uidPrefix);

        assertEquals("Should have one element", 1, stagedSeries.size());
        assertEquals(
                "Should be the original description",
                dummyDescriptionClinic,
                stagedSeries.get(0).getSeriesDescription()
        );
    }

    @Test
    public void getStagedDicomSeries_clinical_context_returns_two_clinical_elements() {
        String dummyDescriptionOne = "dummy-uid-one";
        String dummyDescriptionTwo = "dummy-uid-two";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID("dummy-uid-one");
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        clinicalSeriesList.add(seriesOne);

        DicomSeries serieTwo = new DicomSeries();
        serieTwo.setSeriesModality("dummy-modality");
        serieTwo.setSeriesInstanceUID("dummy-uid-one");
        serieTwo.setSeriesDescription(dummyDescriptionTwo);
        clinicalSeriesList.add(serieTwo);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                clinicalSeriesList, stageOneSeriesList, uidPrefix);

        assertEquals("Should have two elements", 2, stagedSeries.size());
        assertEquals(
                "Should be the description of the second element",
                dummyDescriptionTwo,
                stagedSeries.get(1).getSeriesDescription()
        );
    }

    @Test
    public void getStagedDicomSeries_clinical_context_one_element_is_marked_if_stage_one_representation_exists() {
        String dummyDescriptionOne = "dummy-uid-one";
        String dummyDescriptionTwo = "dummy-uid-two";

        String clinicalUid = "1.2.826.0.1.3680043.9.7275.0.25694151917667784091202966663655420";
        String stageOneUid = "1.2.826.0.1.3680043.9.7275.0.31376055617401213524125362159252274";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID(clinicalUid);
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        clinicalSeriesList.add(seriesOne);

        DicomSeries seriesOneStageOne = new DicomSeries();
        BeanUtils.copyProperties(seriesOne, seriesOneStageOne);
        seriesOneStageOne.setSeriesInstanceUID(stageOneUid);
        stageOneSeriesList.add(seriesOneStageOne);

        DicomSeries seriesTwo = new DicomSeries();
        seriesTwo.setSeriesModality("dummy-modality");
        seriesTwo.setSeriesInstanceUID("dummy-uid-one");
        seriesTwo.setSeriesDescription(dummyDescriptionTwo);
        clinicalSeriesList.add(seriesTwo);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(
                clinicalSeriesList, stageOneSeriesList, uidPrefix);

        assertEquals("Should have two elements", 2, stagedSeries.size());
        assertEquals(
                "Has a stage one representation",
                true,
                stagedSeries.get(0).isStageOneRepresentation()
        );
    }
    // endregion
}
