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
@PrepareForTest({DicomSeries.class, Logger.class})
public class DicomSeriesTest {
    private Logger logger;
    private DicomSeries dicomSeries;

    @Before
    public void setUp() throws Exception {
        mockStatic(Logger.class);
        logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);

        dicomSeries = new DicomSeries();
    }

    //region getUserViewSeriesDescription
    @Test
    public void getUserViewSeriesDescription_returns_unmodified_description() {
        String dummyDescription = "dummyDescription";
        dicomSeries.setSeriesDescription(dummyDescription);

        assertEquals(dummyDescription, dicomSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_returns_empty_string_instead_of_null() {
        String emptyString = "";
        assertEquals(emptyString, dicomSeries.getUserViewSeriesDescription());
    }

    @Test
    public void getUserViewSeriesDescription_removes_edc_prefix() {
        String dummyDescription = "dummyDescription";
        String dummyDescriptionWithEdcPrefix = "(S0)-" + dummyDescription;
        dicomSeries.setSeriesDescription(dummyDescriptionWithEdcPrefix);

        assertEquals(dummyDescription, dicomSeries.getUserViewSeriesDescription());
    }

    //endregion
}