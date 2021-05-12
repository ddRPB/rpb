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

import de.dktk.dd.rpb.core.domain.edc.ItemData;
import de.dktk.dd.rpb.core.domain.edc.ItemGroupData;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ItemGroupDataBuilder.class, Logger.class})
public class ItemGroupDataBuilderTest {
    private ItemGroupDataBuilder itemGroupDataBuilder;
    private final String dummyItemGroupOid = "DummyItemGroupOid";
    private final String dummyItemOid = "DummyItemOid";

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        Logger logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);
        this.itemGroupDataBuilder = ItemGroupDataBuilder.getInstance();
        this.itemGroupDataBuilder.setItemGroupOid(this.dummyItemGroupOid);
    }

    @Test
    public void get_instance() {
        assertNotNull(this.itemGroupDataBuilder);
    }

    @Test(expected = MissingPropertyException.class)
    public void build_throws_if_ItemGroupOid_is_missing() throws MissingPropertyException {
        this.itemGroupDataBuilder.setItemGroupOid(null);
        this.itemGroupDataBuilder.build();
    }

    @Test
    public void build_returns_a_valid_ItemGroupData_instance() throws MissingPropertyException {
        ItemGroupData itemGroupData = this.itemGroupDataBuilder.build();
        assertTrue(itemGroupData != null);
    }

    @Test
    public void setItemGroupOid_sets_id() throws MissingPropertyException {
        ItemGroupData itemGroupData = this.itemGroupDataBuilder.build();
        assertEquals(dummyItemGroupOid, itemGroupData.getItemGroupOid());
    }

    @Test
    public void setItemGroupRepeatKey_sets_key() throws MissingPropertyException {
        String dummyItemGroupRepeatKey = "DummyItemGroupRepeatKey";
        ItemGroupData itemGroupData = this.itemGroupDataBuilder.
                setItemGroupRepeatKey(dummyItemGroupRepeatKey).
                build();
        assertEquals(dummyItemGroupRepeatKey, itemGroupData.getItemGroupRepeatKey());
    }

    @Test
    public void addStudyEventData_adds_item_data_to_ItemGroupData_instance() throws MissingPropertyException {
        ItemData itemData = new ItemData();
        ItemGroupData itemGroupData = this.itemGroupDataBuilder.
                addItemData(itemData).
                build();
        assertEquals(itemData, itemGroupData.getItemDataList().get(0));
    }

    @Test
    public void addStudyEventData_adds_second_item_to_ItemData() throws MissingPropertyException {
        ItemData itemDataOne = new ItemData();
        itemDataOne.setItemOid(this.dummyItemOid);
        ItemData itemDataTwo = new ItemData();
        itemDataTwo.setItemOid("second-item");
        ItemGroupData itemGroupData = this.itemGroupDataBuilder.
                addItemData(itemDataOne).
                addItemData(itemDataTwo).
                build();
        assertTrue(itemGroupData.getItemDataList().indexOf(itemDataOne) >= 0);
        assertTrue(itemGroupData.getItemDataList().indexOf(itemDataTwo) >= 0);
    }
}