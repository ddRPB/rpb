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

package de.dktk.dd.rpb.core.domain.edc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ItemGroupDataMatch
 *
 * @author tomas@skripcak.net
 * @since 07 Jun 2016
 */
public class ItemGroupDataMatch implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ItemGroupDataMatch.class);

    //endregion

    //region Members

    private String itemGroupOid;
    private String itemGroupRepeatKey;
    private Boolean match;
    private List<ItemDataMatch> itemDataMatchesList;

    //endregion

    //region Constructors

    public ItemGroupDataMatch() {
        // NOOP
    }

    public ItemGroupDataMatch(ItemGroupData sourceItemGroupData, ItemGroupData targetItemGroupData) {
        this();

        if (sourceItemGroupData.getItemGroupOid().equals(targetItemGroupData.getItemGroupOid())) {
            this.itemGroupOid = sourceItemGroupData.getItemGroupOid();
            if (sourceItemGroupData.getItemGroupRepeatKey() != null && targetItemGroupData.getItemGroupRepeatKey() != null) {
                if (sourceItemGroupData.getItemGroupRepeatKey().equals(targetItemGroupData.getItemGroupRepeatKey())) {
                    this.itemGroupRepeatKey = sourceItemGroupData.getItemGroupRepeatKey();
                }
            }
            this.itemDataMatchesList = new ArrayList<>();
            this.match = Boolean.TRUE;

            if (sourceItemGroupData.getItemDataList() != null && targetItemGroupData.getItemDataList() != null) {
                for (ItemData sid : sourceItemGroupData.getItemDataList()) {

                    boolean sourceItemFoundInTargetDataSet = false;
                    for (ItemData tid : targetItemGroupData.getItemDataList()) {
                        if (sid.getItemOid().equals(tid.getItemOid())) {
                            sourceItemFoundInTargetDataSet = true;

                            // Create a match object for item data - propagate the creation of rest down
                            this.getItemDataMatchesList().add(
                                new ItemDataMatch(sid, tid)
                            );

                            // Check if the last added item data matches
                            this.match = this.match && this.getItemDataMatchesList().get(this.getItemDataMatchesList().size() - 1).getMatch();

                            break;
                        }
                    }

                    // There is no corresponding item in target dataset (probably was not imported)
                    if (!sourceItemFoundInTargetDataSet) {

                        // Create a match object for item data - as not matching because there is no target found
                        this.getItemDataMatchesList().add(
                                new ItemDataMatch(sid, false)
                        );

                        // Check if the last added item data matches
                        this.match = this.match && this.getItemDataMatchesList().get(this.getItemDataMatchesList().size() - 1).getMatch();
                    }
                }
            }
        }
    }

    //endregion

    //region Properties

    //region ItemGroupOID

    public String getItemGroupOid() {
        return this.itemGroupOid;
    }

    public void setItemGroupOid(String value) {
        this.itemGroupOid = value;
    }

    //endregion

    //region ItemGroupRepeatKey

    public String getItemGroupRepeatKey() {
        return this.itemGroupRepeatKey;
    }

    public void setItemGroupRepeatKey(String value) {
        this.itemGroupRepeatKey = value;
    }

    //endregion

    //region Match

    public Boolean getMatch() {
        return match;
    }

    public void setMatch(Boolean value) {
        this.match = value;
    }

    //endregion

    //region ItemDataMatchList

    public List<ItemDataMatch> getItemDataMatchesList() {
        return itemDataMatchesList;
    }

    public void setItemDataMatchesList(List<ItemDataMatch> itemDataMatchesList) {
        this.itemDataMatchesList = itemDataMatchesList;
    }

    //endregion

    //endregion

}
