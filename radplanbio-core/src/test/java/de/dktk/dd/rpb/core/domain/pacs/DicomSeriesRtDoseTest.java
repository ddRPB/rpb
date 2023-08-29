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

package de.dktk.dd.rpb.core.domain.pacs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DicomSeriesRtDose.class, Logger.class, LoggerFactory.class})
public class DicomSeriesRtDoseTest {
    private Logger logger;
    private DicomSeriesRtDose rtDoseSeries;

    @Before
    public void setUp() throws Exception {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        rtDoseSeries = new DicomSeriesRtDose();
    }

    //region getUserViewSeriesDescription
    @Test
    public void getSeriesDescription_returns_unmodified_description() {
        String dummyDescription = "dummyDescription";
        rtDoseSeries.setSeriesDescription(dummyDescription);

        assertEquals(dummyDescription, rtDoseSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getSeriesDescription_returns_doseComment_if_description_is_empty() {
        String dummyDescription = "";
        String rtDoseComment = "dummy RtDoseComment";
        rtDoseSeries.setSeriesDescription(dummyDescription);
        rtDoseSeries.setDoseComment(rtDoseComment);

        assertEquals(rtDoseComment, rtDoseSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_empty_description_if_properties_are_null() {
        String emptyString = "";
        rtDoseSeries.setSeriesDescription(emptyString);

        assertEquals(emptyString, rtDoseSeries.getUserViewSeriesDescription());
    }
    //endregion
}
