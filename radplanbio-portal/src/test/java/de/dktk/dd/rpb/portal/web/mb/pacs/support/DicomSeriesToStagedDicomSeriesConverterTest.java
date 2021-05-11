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

package de.dktk.dd.rpb.portal.web.mb.pacs.support;

import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;
import de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DicomSeriesToStagedDicomSeriesConverterTest {
    List<DicomSeries> stageOneSeries;
    List<DicomSeries> stageTwoSeries;
    String uidPrefix = "1.2.826.0.1.3680043.9.7275.0.";
    String partnerSideCode = "DD";
    String edcCode = "PR";

    @Before
    public void setUp() throws Exception {
        stageOneSeries = new ArrayList<DicomSeries>();
        stageTwoSeries = new ArrayList<DicomSeries>();
    }

    @Test
    public void returns_empty_list_if_study_lists_are_empty() {
        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(stageOneSeries, stageTwoSeries, uidPrefix, partnerSideCode, edcCode);
        assertEquals(0, stagedSeries.size());
    }

    @Test
    public void returns_stage_one_element() {
        String dummyDescriptionOne = "dummy-uid-one";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID("dummy-uid-one");
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        stageOneSeries.add(seriesOne);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(stageOneSeries, null, uidPrefix, partnerSideCode, edcCode);

        assertEquals("Should have one element", 1, stagedSeries.size());
        assertEquals("Should be original description, because it has no prefix", dummyDescriptionOne, stagedSeries.get(0).getSeriesDescription());
    }

    @Test
    public void returns_stage_one_element_with_description_without_edc_code_prefix() {
        String edcPrefix = DicomStudyDescriptionEdcCodeUtil.getEdcCodePattern("Edc-Code");
        String dummyDescriptionOne = "dummy-uid-one";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID(edcPrefix + "dummy-uid-one");
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        stageOneSeries.add(seriesOne);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(stageOneSeries, stageTwoSeries, uidPrefix, partnerSideCode, edcCode);

        assertEquals("Should have one element", 1, stagedSeries.size());
        assertEquals("Should be the description without prefix", dummyDescriptionOne, stagedSeries.get(0).getSeriesDescription());
    }

    @Test
    public void returns_two_stage_one_elements() {
        String dummyDescriptionOne = "dummy-uid-one";
        String dummyDescriptionTwo = "dummy-uid-two";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID("dummy-uid-one");
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        stageOneSeries.add(seriesOne);

        DicomSeries serieTwo = new DicomSeries();
        serieTwo.setSeriesModality("dummy-modality");
        serieTwo.setSeriesInstanceUID("dummy-uid-one");
        serieTwo.setSeriesDescription(dummyDescriptionTwo);
        stageOneSeries.add(serieTwo);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(stageOneSeries, stageTwoSeries, uidPrefix, partnerSideCode, edcCode);

        assertEquals("Should have two elements", 2, stagedSeries.size());
        assertEquals("Should be the description of the second element", dummyDescriptionTwo, stagedSeries.get(1).getSeriesDescription());
    }

    @Test
    public void stage_one_element_is_marked_if_stage_two_representation_exists() {
        String dummyDescriptionOne = "dummy-uid-one";
        String dummyDescriptionTwo = "dummy-uid-two";

        String stageOneUid = "1.2.826.0.1.3680043.9.7275.0.25694151917667784091202966663655420";
        String stageTwoUid = "1.2.826.0.1.3680043.9.7275.0.28119988438981462564481418230477521";

        DicomSeries seriesOne = new DicomSeries();
        seriesOne.setSeriesModality("dummy-modality");
        seriesOne.setSeriesInstanceUID(stageOneUid);
        seriesOne.setSeriesDescription(dummyDescriptionOne);
        stageOneSeries.add(seriesOne);

        DicomSeries seriesOneStageTwo = new DicomSeries();
        BeanUtils.copyProperties(seriesOne, seriesOneStageTwo);
        seriesOneStageTwo.setSeriesInstanceUID(stageTwoUid);
        stageTwoSeries.add(seriesOneStageTwo);

        DicomSeries seriesTwo = new DicomSeries();
        seriesTwo.setSeriesModality("dummy-modality");
        seriesTwo.setSeriesInstanceUID("dummy-uid-one");
        seriesTwo.setSeriesDescription(dummyDescriptionTwo);
        stageOneSeries.add(seriesTwo);

        List<StagedDicomSeries> stagedSeries = DicomSeriesToStagedDicomSeriesConverter.getStagedDicomSeries(stageOneSeries, stageTwoSeries, uidPrefix, partnerSideCode, edcCode);

        assertEquals("Should have two elements", 2, stagedSeries.size());
        assertEquals("Has a second stage representation", true, stagedSeries.get(0).hasStageTwoRepresentation());
    }
}