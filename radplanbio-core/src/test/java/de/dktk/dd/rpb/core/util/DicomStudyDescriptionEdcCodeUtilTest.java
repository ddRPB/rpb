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

package de.dktk.dd.rpb.core.util;

import de.dktk.dd.rpb.core.domain.pacs.DicomStudy;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static de.dktk.dd.rpb.core.util.Constants.study0EdcCode;
import static de.dktk.dd.rpb.core.util.DicomStudyDescriptionEdcCodeUtil.getEdcCodePattern;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class})
public class DicomStudyDescriptionEdcCodeUtilTest {

    @Before
    public void executedBeforeEach() {
        mockStatic(Logger.class);
        Logger logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);
    }

    // region getEdcCodePattern
    @Test
    public void adds_edc_code_patter() {
        String edcCode = "dummyCode";
        String res = DicomStudyDescriptionEdcCodeUtil.getEdcCodePattern(edcCode);
        assertEquals("(" + edcCode + ")-", res);
    }

    // endregion
    // region removeEdcCodePrefix

    @Test
    public void removes_edc_code_from_description() {
        String descriptionWithOutPrefix = "dummy description () -";
        String descriptionWithPrefix = getEdcCodePattern("abc") + descriptionWithOutPrefix;
        String res = DicomStudyDescriptionEdcCodeUtil.removeEdcCodePrefix(descriptionWithPrefix);
        assertEquals(descriptionWithOutPrefix, res);
    }

    @Test
    public void removes_first_edc_code_from_description_only() {
        String descriptionWithOutPrefix = getEdcCodePattern("cba") + "dummy description () -";
        String descriptionWithPrefix = getEdcCodePattern("abc") + descriptionWithOutPrefix;
        String res = DicomStudyDescriptionEdcCodeUtil.removeEdcCodePrefix(descriptionWithPrefix);
        assertEquals(descriptionWithOutPrefix, res);
    }

    @Test
    public void keeps_original_string_if_it_has_no_edc_code_pattern() {
        String descriptionWithOutPrefix = "dummy description () -";
        String res = DicomStudyDescriptionEdcCodeUtil.removeEdcCodePrefix(descriptionWithOutPrefix);
        assertEquals(descriptionWithOutPrefix, res);
    }

    //endregion
    //region isCorrespondingStudyZero
    @Test
    public void returns_true_if_description_and_date_are_the_same() {
        String edcCode = "PT";
        String dummyDescriptionText = "dummy text";
        String studyDate = "2010-10-01";

        String stagedStudyDescription = getEdcCodePattern(edcCode) + dummyDescriptionText;
        String studyZeroDescription = getEdcCodePattern(study0EdcCode) + dummyDescriptionText;

        DicomStudy study_one = new DicomStudy();
        study_one.setStudyDescription(stagedStudyDescription);
        study_one.setStudyDate(studyDate);

        DicomStudy study_two = new DicomStudy();
        study_two.setStudyDescription(studyZeroDescription);
        study_two.setStudyDate(studyDate);

        Boolean isEqual = DicomStudyDescriptionEdcCodeUtil.isCorrespondingStudyZero(study_one, study_two);
        assertEquals(true, isEqual);
    }

    @Test
    public void returns_false_if_description_does_not_match() {
        String edcCode = "PT";
        String dummyDescriptionText = "dummy text";
        String studyDate = "2010-10-01";

        String stagedStudyDescription = getEdcCodePattern(edcCode) + dummyDescriptionText;
        String studyZeroDescription = getEdcCodePattern(study0EdcCode) + "extra text" + dummyDescriptionText;

        DicomStudy study_one = new DicomStudy();
        study_one.setStudyDescription(stagedStudyDescription);
        study_one.setStudyDate(studyDate);

        DicomStudy study_two = new DicomStudy();
        study_two.setStudyDescription(studyZeroDescription);
        study_two.setStudyDate(studyDate);

        Boolean isEqual = DicomStudyDescriptionEdcCodeUtil.isCorrespondingStudyZero(study_one, study_two);
        assertEquals(false, isEqual);
    }

    @Test
    public void returns_false_if_date_is_not_the_same() {
        String edcCode = "PT";
        String dummyDescriptionText = "dummy text";
        String studyDateOne = "2010-10-01";
        String studyDateTwo = "2020-20-02";

        String stagedStudyDescription = getEdcCodePattern(edcCode) + dummyDescriptionText;
        String studyZeroDescription = getEdcCodePattern(study0EdcCode) + dummyDescriptionText;

        DicomStudy study_one = new DicomStudy();
        study_one.setStudyDescription(stagedStudyDescription);
        study_one.setStudyDate(studyDateOne);

        DicomStudy study_two = new DicomStudy();
        study_two.setStudyDescription(studyZeroDescription);
        study_two.setStudyDate(studyDateTwo);

        Boolean isEqual = DicomStudyDescriptionEdcCodeUtil.isCorrespondingStudyZero(study_one, study_two);
        assertEquals(false, isEqual);
    }

    @Test
    public void isCorrespondingStudyZero_returns_false_if_description_is_null() {
        String studyDate = "2010-10-01";

        DicomStudy study_one = new DicomStudy();
        study_one.setStudyDate(studyDate);

        DicomStudy study_two = new DicomStudy();
        study_two.setStudyDate(studyDate);

        assertFalse(DicomStudyDescriptionEdcCodeUtil.isCorrespondingStudyZero(study_one, study_two));
    }

    @Test(expected = NullPointerException.class)
    public void throws_if_date_is_null() {
        String edcCode = "PT";
        String dummyDescriptionText = "dummy text";

        String stagedStudyDescription = getEdcCodePattern(edcCode) + dummyDescriptionText;
        String studyZeroDescription = getEdcCodePattern(study0EdcCode) + dummyDescriptionText;

        DicomStudy study_one = new DicomStudy();
        study_one.setStudyDescription(stagedStudyDescription);

        DicomStudy study_two = new DicomStudy();
        study_two.setStudyDescription(studyZeroDescription);

        DicomStudyDescriptionEdcCodeUtil.isCorrespondingStudyZero(study_one, study_two);
    }

    //endregion
    //region hasStudyZeroPrefix

    @Test
    public void returns_true_if_string_starts_with_study_zero_prefix() {
        String dummyDescriptionText = "dummy text";
        String studyZeroDescription = getEdcCodePattern(study0EdcCode) + dummyDescriptionText;

        assertTrue(DicomStudyDescriptionEdcCodeUtil.hasStudyZeroPrefix(studyZeroDescription));
    }

    @Test
    public void returns_false_if_string_not_starts_with_study_zero_prefix() {
        String dummyDescriptionText = "dummy text";
        String studyZeroDescription = getEdcCodePattern("abc") + dummyDescriptionText;

        assertFalse(DicomStudyDescriptionEdcCodeUtil.hasStudyZeroPrefix(studyZeroDescription));
    }

    //endregion
}