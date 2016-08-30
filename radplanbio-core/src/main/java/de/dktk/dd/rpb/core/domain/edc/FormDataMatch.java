/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2016 Tomas Skripcak
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

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * FormDataMatch
 *
 * @author tomas@skripcak.net
 * @since 07 Jun 2016
 */
public class FormDataMatch implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(FormDataMatch.class);

    //endregion

    //region Members

    private String formOid;
    private Boolean match;
    private List<ItemGroupDataMatch> itemGroupDataMatchList;

    //endregion

    //region Constructors

    public FormDataMatch() {
        // NOOP
    }

    public FormDataMatch(FormData sourceFormData, FormData targetFormData) {
        this();

        if (sourceFormData.getFormOid().equals(targetFormData.getFormOid())) {
            this.formOid = sourceFormData.getFormOid();
            this.itemGroupDataMatchList = new ArrayList<>();
            this.match = Boolean.TRUE;

            if (sourceFormData.getItemGroupDataList() != null && targetFormData.getItemGroupDataList() != null) {
                for (ItemGroupData sigd : sourceFormData.getItemGroupDataList()) {
                    for (ItemGroupData tigd : targetFormData.getItemGroupDataList()) {
                        if (sigd.getItemGroupOid().equals(tigd.getItemGroupOid())) {
                            if (sigd.getItemGroupRepeatKey() != null && tigd.getItemGroupRepeatKey() != null) {
                                if (sigd.getItemGroupRepeatKey().equals(tigd.getItemGroupRepeatKey())) {
                                    // Create a match object for item group data - propagate the creation of rest down
                                    this.getItemGroupDataMatchList().add(
                                            new ItemGroupDataMatch(sigd, tigd)
                                    );

                                    // Check if the last added item group data matches
                                    this.match = this.match && this.getItemGroupDataMatchList().get(this.getItemGroupDataMatchList().size() - 1).getMatch();

                                    break;
                                }
                            }
                            else {
                                // Create a match object for item group data - propagate the creation of rest down
                                this.getItemGroupDataMatchList().add(
                                        new ItemGroupDataMatch(sigd, tigd)
                                );

                                // Check if the last added item group data matches
                                this.match = this.match && this.getItemGroupDataMatchList().get(this.getItemGroupDataMatchList().size() - 1).getMatch();

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    //endregion

    //region Properties

    //region FormOID

    public String getFormOid() {
        return this.formOid;
    }

    public void setFormOid(String value) {
        this.formOid = value;
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

    //region ItemGroupDataMatchList

    public List<ItemGroupDataMatch> getItemGroupDataMatchList() {
        return itemGroupDataMatchList;
    }

    public void setItemGroupDataMatchList(List<ItemGroupDataMatch> itemGroupDataMatchList) {
        this.itemGroupDataMatchList = itemGroupDataMatchList;
    }

    //endregion

    //endregion

}