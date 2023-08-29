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

import de.dktk.dd.rpb.core.domain.edc.FormData;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupData;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
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
@PrepareForTest({ItemGroupDataBuilder.class, Logger.class, LoggerFactory.class})
public class FormDataBuilderTest {
    private FormDataBuilder formDataBuilder;
    private final String dummyFormOid = "DummyFormOid";
    private final String dummyItemOid = "DummyItemOid";

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
        this.formDataBuilder = FormDataBuilder.getInstance();
        this.formDataBuilder.setFormOid(this.dummyFormOid);
    }

    @Test
    public void get_instance() {
        assertNotNull(this.formDataBuilder);
    }

    @Test(expected = MissingPropertyException.class)
    public void build_throws_if_FormOid_is_missing() throws MissingPropertyException {
        this.formDataBuilder.setFormOid(null);
        FormData formData = this.formDataBuilder.build();
    }

    @Test
    public void build_returns_a_valid_FormData_instance() throws MissingPropertyException {
        FormData formData = this.formDataBuilder.build();
        assertTrue(formData != null);
    }

    @Test
    public void setItemGroupOid_sets_id() throws MissingPropertyException {
        FormData formData = this.formDataBuilder.build();
        assertEquals(dummyFormOid, formData.getFormOid());
    }

    @Test
    public void addItemGroupData_adds_ItemGroupData_to_instance() throws MissingPropertyException {
        ItemGroupData itemGroupData = new ItemGroupData();
        itemGroupData.setItemGroupOid(this.dummyItemOid);
        FormData formData = this.formDataBuilder.
                addItemGroupData(itemGroupData).
                build();
        assertEquals(itemGroupData, formData.getItemGroupDataList().get(0));
    }
}
