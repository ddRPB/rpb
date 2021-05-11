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

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RtStructDicomSeries.class, Logger.class})
public class RtStructDicomSeriesTest {
    private Logger logger;
    private RtStructDicomSeries rtStructSeries;

    @Before
    public void setUp() throws Exception {
        mockStatic(Logger.class);
        logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);

        rtStructSeries = new RtStructDicomSeries();
    }

    //region getUserViewSeriesDescription
    @Test
    public void getUserViewSeriesDescription_returns_unmodified_description() {
        String dummyDescription = "dummyDescription";
        rtStructSeries.setSeriesDescription(dummyDescription);

        assertEquals(dummyDescription, rtStructSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_structureSetLabel_if_description_is_empty() {
        String dummyDescription = "";
        String structureSetLabel = "dummy structureSetLabel";
        rtStructSeries.setSeriesDescription(dummyDescription);
        rtStructSeries.setStructureSetLabel(structureSetLabel);

        assertEquals(structureSetLabel, rtStructSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_structureSetName_if_structureSetLabel_is_empty() {
        String emptyString = "";
        String structureSetName = "dummy structureSetName";
        rtStructSeries.setSeriesDescription(emptyString);
        rtStructSeries.setStructureSetLabel(emptyString);
        rtStructSeries.setStructureSetName(structureSetName);

        assertEquals(structureSetName, rtStructSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_structureSetDescription_if_structureSetName_is_empty() {
        String emptyString = "";
        String structureSetDescription = "dummy structureSetName";
        rtStructSeries.setSeriesDescription(emptyString);
        rtStructSeries.setStructureSetLabel(emptyString);
        rtStructSeries.setStructureSetName(emptyString);
        rtStructSeries.setStructureSetDescription(structureSetDescription);

        assertEquals(structureSetDescription, rtStructSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_empty_description_if_properties_are_null() {
        String emptyString = "";
        rtStructSeries.setSeriesDescription(emptyString);

        assertEquals(emptyString, rtStructSeries.getUserViewSeriesDescription());
    }
}