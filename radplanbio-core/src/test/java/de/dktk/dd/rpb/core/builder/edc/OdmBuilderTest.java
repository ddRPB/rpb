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

package de.dktk.dd.rpb.core.builder.edc;

import de.dktk.dd.rpb.core.domain.edc.ClinicalData;
import de.dktk.dd.rpb.core.domain.edc.Odm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OdmBuilder.class, Logger.class, LoggerFactory.class})
public class OdmBuilderTest {
    private OdmBuilder odmBuilder;

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
        this.odmBuilder = OdmBuilder.getInstance();
    }

    @Test
    public void get_instance() {
        assertNotNull(this.odmBuilder);
    }

    @Test
    public void build_returns_a_valid_odm_instance() {
        Odm odm = this.odmBuilder.build();
        assertTrue(odm != null);
    }

    @Test
    public void addClinicalData_adds_clinical_data_to_odm_instance() {
        ClinicalData clinicalData = new ClinicalData();
        Odm odm = this.odmBuilder.
                addClinicalData(clinicalData).
                build();
        assertEquals(clinicalData, odm.getClinicalDataList().get(0));
    }

    @Test
    public void addClinicalData_adds_clinical_data_once_only() {
        ClinicalData clinicalData = new ClinicalData();
        clinicalData.setStudyOid("dummyStudyOid");
        Odm odm = this.odmBuilder.
                addClinicalData(clinicalData).
                addClinicalData(clinicalData).
                build();
        assertEquals(1, odm.getClinicalDataList().size());
    }

}
