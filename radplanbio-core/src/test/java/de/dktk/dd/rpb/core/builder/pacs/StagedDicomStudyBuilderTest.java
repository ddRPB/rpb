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

package de.dktk.dd.rpb.core.builder.pacs;

import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import de.dktk.dd.rpb.core.domain.pacs.StagedDicomStudy;
import de.dktk.dd.rpb.core.util.DicomUidReGeneratorUtil;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.study0EdcCode;
import static de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil.getEdcCodePattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StagedDicomStudyBuilder.class, Logger.class})
public class StagedDicomStudyBuilderTest {

    public static final String EDC_CODE = "PT";
    public static final String UID_PREFIX = "1.2.826.0.1.3680043.9.7275.0.";
    public static final String DUMMY_UID = "1.2.826.0.1.3680043.9.7275.0.28119988438981462564481418230477534";
    private List<DicomStudy> studies;
    private StagedDicomStudyBuilder stagedDicomStudyBuilder;

    @Before
    public void setUp() {
        studies = new ArrayList<>();

        mockStatic(Logger.class);
        Logger logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);
    }

    @Test
    public void get_instance() {
        stagedDicomStudyBuilder = StagedDicomStudyBuilder.getInstance(studies, UID_PREFIX, EDC_CODE);
        assertNotNull(stagedDicomStudyBuilder);
    }

    // region filterStudyZeroStudies
    @Test
    public void filter_does_not_throw_if_list_is_empty() {
        stagedDicomStudyBuilder = StagedDicomStudyBuilder.getInstance(studies, UID_PREFIX, EDC_CODE);
        stagedDicomStudyBuilder.filterFirstStageStudies();
        assertNotNull(stagedDicomStudyBuilder);
    }

    @Test
    public void filter_does_not_throw_if_item_has_no_StudyDescription_property() {
        DicomStudy study_one = new DicomStudy();
        studies.add(study_one);

        stagedDicomStudyBuilder = StagedDicomStudyBuilder.getInstance(studies, UID_PREFIX, EDC_CODE);
        stagedDicomStudyBuilder.filterFirstStageStudies();
        assertNotNull(stagedDicomStudyBuilder);
    }

    @Test
    public void filter_study_zero_items() {
        String studyZeroPrefix = getEdcCodePattern(study0EdcCode);
        String edcCodePrefix = getEdcCodePattern(EDC_CODE);
        String studyDescription = "dummy study description";

        DicomStudy study_one = new DicomStudy();
        study_one.setStudyDescription(edcCodePrefix + studyDescription);
        studies.add(study_one);

        DicomStudy study_two = new DicomStudy();
        study_two.setStudyDescription(studyZeroPrefix + studyDescription);
        studies.add(study_two);

        stagedDicomStudyBuilder = StagedDicomStudyBuilder.getInstance(studies, UID_PREFIX, EDC_CODE);
        stagedDicomStudyBuilder.filterFirstStageStudies();
        List<DicomStudy> resultList = stagedDicomStudyBuilder.getStageOneStudiesList();

        assertEquals(1, resultList.size());
        assertEquals(studyZeroPrefix + studyDescription, resultList.get(0).getStudyDescription());

        List<StagedDicomStudy> researchResultList = stagedDicomStudyBuilder.getStagedStudies();

        assertEquals(1, resultList.size());
        assertEquals(studyDescription, researchResultList.get(0).getStudyDescription());
    }

    //endregion

    //region filterStagedStudiesByEdcCode

    @Test
    public void filter_study_by_edc_code() {
        String edcCodePrefixOne = getEdcCodePattern(EDC_CODE);
        String edcCodePrefixTwo = getEdcCodePattern("edc code two");
        String studyDescription = "dummy study description";

        DicomStudy study_one = new DicomStudy();
        study_one.setStudyDescription(edcCodePrefixOne + studyDescription);
        studies.add(study_one);
        DicomStudy study_two = new DicomStudy();
        study_two.setStudyDescription(edcCodePrefixTwo + studyDescription);
        studies.add(study_two);

        stagedDicomStudyBuilder = StagedDicomStudyBuilder.getInstance(studies, UID_PREFIX, EDC_CODE);
        stagedDicomStudyBuilder.filterSecondStageStudiesByEdcCode();
        List<DicomStudy> resultList = stagedDicomStudyBuilder.getStageTwoStudiesList();

        assertEquals(1, resultList.size());
        assertEquals(edcCodePrefixOne + studyDescription, resultList.get(0).getStudyDescription());
    }

    @Test
    public void filter_and_process_related_item_with_two_stage_representation() {
        String studyZeroPrefix = getEdcCodePattern(study0EdcCode);
        String edcCodePrefix = getEdcCodePattern(EDC_CODE);
        String studyDescription = "dummy study description";
        String stageOneUid = DUMMY_UID;

        DicomStudy study_one = new DicomStudy();
        study_one.setStudyDescription(edcCodePrefix + studyDescription);
        study_one.setStudyInstanceUID(DicomUidReGeneratorUtil.generateStageTwoUid(UID_PREFIX, "DD", EDC_CODE, stageOneUid));
        studies.add(study_one);

        DicomStudy study_two = new DicomStudy();
        study_two.setStudyDescription(studyZeroPrefix + studyDescription);
        study_two.setStudyInstanceUID(stageOneUid);
        studies.add(study_two);

        stagedDicomStudyBuilder = StagedDicomStudyBuilder.getInstance(studies, UID_PREFIX, EDC_CODE);
        stagedDicomStudyBuilder.filterFirstStageStudies();
        stagedDicomStudyBuilder.filterSecondStageStudiesByEdcCode();

        List<StagedDicomStudy> researchResultList = stagedDicomStudyBuilder.getStagedStudies();

        assertEquals(1, researchResultList.size());
        assertEquals(studyDescription, researchResultList.get(0).getStudyDescription());
        assertEquals(true, researchResultList.get(0).hasStageOneRepresentation());
        assertEquals(true, researchResultList.get(0).hasStageTwoRepresentation());
    }

    //endregion

    // region addClinicalStudyInformation

    @Test
    public void addClinicalStudyInformation_adds_information_of_a_specific_clinical_study_to_an_existing_study_zero() {
        String studyZeroPrefix = getEdcCodePattern(study0EdcCode);
        String studyDescription = "dummy study description";
        String clinicalStudyUid = DUMMY_UID;

        DicomStudy stageOneStudy = new DicomStudy();
        stageOneStudy.setStudyDescription(studyZeroPrefix + studyDescription);
        stageOneStudy.setStudyInstanceUID(DicomUidReGeneratorUtil.generateStageOneUid(UID_PREFIX, clinicalStudyUid));
        studies.add(stageOneStudy);

        DicomStudy clinicalStudy = new DicomStudy();
        clinicalStudy.setStudyDescription(studyDescription);
        clinicalStudy.setStudyInstanceUID(clinicalStudyUid);
        List<DicomStudy> clinicalDicomStudies = new ArrayList<>();
        clinicalDicomStudies.add(clinicalStudy);

        stagedDicomStudyBuilder = StagedDicomStudyBuilder.getInstance(studies, UID_PREFIX, EDC_CODE);
        stagedDicomStudyBuilder.filterFirstStageStudies();
        stagedDicomStudyBuilder.setClinicalStudyList(clinicalDicomStudies);

        List<StagedDicomStudy> researchResultList = stagedDicomStudyBuilder.getStagedStudies();

        assertEquals(1, researchResultList.size());
        assertEquals(studyDescription, researchResultList.get(0).getStudyDescription());
        assertEquals(clinicalStudyUid, researchResultList.get(0).getClinicalStudyInstanceUid());
        assertEquals(true, researchResultList.get(0).hasStageOneRepresentation());
        assertEquals(false, researchResultList.get(0).hasStageTwoRepresentation());

    }

    @Test
    public void addClinicalStudyInformation_adds_clinical_study_if_there_is_no_corresponding_study_zero() {
        String studyDescription = "dummy study description";
        String clinicalStudyUid = DUMMY_UID;

        DicomStudy clinicalStudy = new DicomStudy();
        clinicalStudy.setStudyDescription(studyDescription);
        clinicalStudy.setStudyInstanceUID(clinicalStudyUid);
        List<DicomStudy> clinicalDicomStudies = new ArrayList<>();
        clinicalDicomStudies.add(clinicalStudy);

        stagedDicomStudyBuilder = StagedDicomStudyBuilder.getInstance(studies, UID_PREFIX, EDC_CODE);
        stagedDicomStudyBuilder.filterFirstStageStudies();
        stagedDicomStudyBuilder.setClinicalStudyList(clinicalDicomStudies);

        List<StagedDicomStudy> researchResultList = stagedDicomStudyBuilder.getStagedStudies();

        assertEquals(1, researchResultList.size());
        assertEquals(studyDescription, researchResultList.get(0).getStudyDescription());
        assertEquals(clinicalStudyUid, researchResultList.get(0).getClinicalStudyInstanceUid());
        assertEquals(false, researchResultList.get(0).hasStageOneRepresentation());
        assertEquals(false, researchResultList.get(0).hasStageTwoRepresentation());

    }

    // endregion
}