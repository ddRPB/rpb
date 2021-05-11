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

import de.dktk.dd.rpb.core.domain.Identifiable;
import org.apache.log4j.Logger;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * ItemDataMatch
 *
 * @author tomas@skripcak.net
 * @since 07 Jun 2016
 */
public class ItemDataMatch implements Identifiable<Integer>, Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ItemDataMatch.class);

    //endregion

    //region Members

    private Integer id;
    private String itemOid;
    private Boolean match;

    //endregion

    //region Constructors

    public ItemDataMatch() {
        // NOOP
    }

    public ItemDataMatch(ItemData sourceItemData, ItemData targetItemData) {
        this();

        // OID match is mandatory
        if (sourceItemData.getItemOid().equals(targetItemData.getItemOid())) {
            this.itemOid = sourceItemData.getItemOid();

            // If target requires value equality
            if (targetItemData.getCheckValueEquality()) {
                this.match = Boolean.FALSE;

                // Values are provided
                if (sourceItemData.getValue() != null && sourceItemData.getValue() != null) {
                    this.match = sourceItemData.getValue().equals(targetItemData.getValue());
                }
            }
            // Target considered equal to source data regardless the value
            else {
                this.match = Boolean.TRUE;
            }
        }
    }

    public ItemDataMatch(ItemData sourceItemData, boolean match) {
        this();

        this.itemOid = sourceItemData.getItemOid();
        this.match = match;
    }

    //endregion

    //region Properties

    //region Id

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }

    @Transient
    public boolean isIdSet() {
        return this.id != null;
    }

    //endregion

    //region ItemOID

    public String getItemOid() {
        return this.itemOid;
    }

    public void setItemOid(String value) {
        this.itemOid = value;
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

    //endregion

}