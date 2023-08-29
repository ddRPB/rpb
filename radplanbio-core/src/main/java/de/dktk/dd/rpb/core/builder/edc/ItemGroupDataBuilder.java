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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Builder for the ItemGroupData object,
 * which can be used to create the ItemGroupData part of an ODM-XML object.
 */
public class ItemGroupDataBuilder {
    //region Finals

    private static final Logger log = LoggerFactory.getLogger(ItemGroupDataBuilder.class);
    private final ItemGroupData itemGroupData = new ItemGroupData();

    // endregion

    // region constructor

    private ItemGroupDataBuilder() {
    }

    public static ItemGroupDataBuilder getInstance() {
        return new ItemGroupDataBuilder();
    }

    // endregion

    /**
     * Builds the ItemGroupData instance out of the current configuration properties.
     *
     * @return ItemGroupData
     */
    public ItemGroupData build() throws MissingPropertyException {
        if (this.itemGroupData.getItemGroupOid() == null || this.itemGroupData.getItemGroupOid().isEmpty()) {
            throw new MissingPropertyException("ItemGroupOid is missing.");
        }
        return this.itemGroupData;
    }

    /**
     * Set the itemGroupOid property for the ItemGroupData object
     *
     * @param itemGroupOid String identifier for ItemGroupData object
     * @return ItemGroupDataBuilder
     */
    public ItemGroupDataBuilder setItemGroupOid(String itemGroupOid) {
        this.itemGroupData.setItemGroupOid(itemGroupOid);
        return this;
    }

    /**
     * Set the itemGroupRepeatKey property for the ItemGroupData object.
     *
     * @param itemGroupRepeatKey String identifier for repeating events (in context with the itemGroupOid)
     * @return ItemGroupDataBuilder
     */
    public ItemGroupDataBuilder setItemGroupRepeatKey(String itemGroupRepeatKey) {
        this.itemGroupData.setItemGroupRepeatKey(itemGroupRepeatKey);
        return this;
    }

    /**
     * Adding an additional ItemData to the ItemGroupData
     *
     * @param itemData ItemData instance
     * @return ItemGroupDataBuilder
     */
    public ItemGroupDataBuilder addItemData(ItemData itemData) {
        this.itemGroupData.addItemData(itemData);
        return this;
    }
}
