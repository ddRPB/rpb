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
@PrepareForTest({DicomSeriesRtPlan.class, Logger.class, LoggerFactory.class})
public class DicomSeriesRtPlanTest {
    private Logger logger;
    private DicomSeriesRtPlan rtPlanSeries;

    @Before
    public void setUp() throws Exception {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        rtPlanSeries = new DicomSeriesRtPlan();
    }

    //region getUserViewSeriesDescription
    @Test
    public void getUserViewSeriesDescription_returns_unmodified_description() {
        String dummyDescription = "dummyDescription";
        rtPlanSeries.setSeriesDescription(dummyDescription);

        assertEquals(dummyDescription, rtPlanSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_RtPlanLabel_if_description_is_empty() {
        String dummyDescription = "";
        String rtPlanLabel = "dummy RtPlanLabel";
        rtPlanSeries.setSeriesDescription(dummyDescription);
        rtPlanSeries.setRtPlanLabel(rtPlanLabel);

        assertEquals(rtPlanLabel, rtPlanSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_RTPlanName_if_RtPlanLabel_is_empty() {
        String emptyString = "";
        String rtPlanName = "dummy RtPlanName";
        rtPlanSeries.setSeriesDescription(emptyString);
        rtPlanSeries.setRtPlanLabel(emptyString);
        rtPlanSeries.setRtPlanName(rtPlanName);

        assertEquals(rtPlanName, rtPlanSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_RTPlanName_if_RTPlanName_is_empty() {
        String emptyString = "";
        String rtPlanDescription = "dummy RtPlanDescription";
        rtPlanSeries.setSeriesDescription(emptyString);
        rtPlanSeries.setRtPlanLabel(emptyString);
        rtPlanSeries.setRtPlanName(emptyString);
        rtPlanSeries.setRtPlanDescription(rtPlanDescription);

        assertEquals(rtPlanDescription, rtPlanSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_empty_description_if_properties_are_null() {
        String emptyString = "";
        rtPlanSeries.setSeriesDescription(emptyString);

        assertEquals(emptyString, rtPlanSeries.getUserViewSeriesDescription());
    }
    //endregion
}
