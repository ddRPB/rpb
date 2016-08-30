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
 * OdmMatch
 *
 * @author tomas@skripcak.net
 * @since 07 Jun 2016
 */
public class OdmMatch implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(OdmMatch.class);

    //endregion

    //region Members

    private String fileOid;
    private Boolean match;
    private List<ClinicalDataMatch> clinicalDataMatchList;

    //endregion

    //region Constructors

    public OdmMatch() {
        // NOOP
    }

    public OdmMatch(Odm sourceOdm, Odm targetOdm) {
        this();

        // Matching clinical data
        if (sourceOdm.getClinicalDataList() != null && targetOdm.getClinicalDataList() != null) {

            // Initialize emtpy collection for clinical data matching
            this.clinicalDataMatchList = new ArrayList<>();
            this.match = Boolean.TRUE;

            // Set OID from source or target (depending where it is defined)
            if (sourceOdm.getFileOid() != null) {
                this.fileOid = sourceOdm.getFileOid();
            }
            else if (targetOdm.getFileOid() != null) {
                this.fileOid = targetOdm.getFileOid();
            }

            // Lookup for study with the same OID in source and target
            for (ClinicalData scd : sourceOdm.getClinicalDataList()) {
                for (ClinicalData tcd : targetOdm.getClinicalDataList()) {
                    if (scd.getStudyOid().equals(tcd.getStudyOid())) {

                        // Create a match object for clinical data - propagate the creation of rest down
                        this.getClinicalDataMatchList().add(
                                new ClinicalDataMatch(scd, tcd)
                        );

                        // Check if the last added event data matches
                        this.match = this.match && this.getClinicalDataMatchList().get(this.getClinicalDataMatchList().size() - 1).getMatch();

                        break;
                    }
                }
            }
        }
    }

    //endregion

    //region Properties

    //region FileOID

    public String getFileOid() {
        return this.fileOid;
    }

    public void setFileOid(String value) {
        this.fileOid = value;
    }

    //endregion

    //region Match

    public Boolean getMatch() {
        return this.match;
    }

    public void setMatch(Boolean value) {
        this.match = value;
    }

    //endregion

    //region ClinicalDataMatchList

    public List<ClinicalDataMatch> getClinicalDataMatchList() {
        return clinicalDataMatchList;
    }

    public void setClinicalDataMatchList(List<ClinicalDataMatch> clinicalDataMatchList) {
        this.clinicalDataMatchList = clinicalDataMatchList;
    }

    //endregion

    //endregion

    //region Methods

    //TODO: calculate error summary list for GUI (not hierarchical)

    //endregion

}