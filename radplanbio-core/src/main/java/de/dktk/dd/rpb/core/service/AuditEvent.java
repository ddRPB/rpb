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

package de.dktk.dd.rpb.core.service;

/**
 * AuditEvent
 *
 * @author tomas@skripcak.net
 * @since 27 January 2015
 */
public enum AuditEvent {

    ApplicationStartup("ApplicationStartup"),
    ApplicationShutdown("ApplicationShutdown"),

    LoginFailed("LoginFailed"),
    LoginSuccessful("LoginSuccessful"),
    LogoutSuccessful("LogoutSuccessful"),

    Creation("Creation"),
    Deletion("Deletion"),
    Modification("Modification"),

    PIDCreation("PIDCreation"),
    PIDModification("PIDModification"),
    PIDLoad("PIDLoad"),
    PIDSearch("PIDSearch"),
    PIDDepseudonymisation("PIDDepseudonymisation"),
    PIDUnsure("PIDUnsure"),

    EDCStudySubjectEnrollment("EDCStudySubjectEnrollment"),
    EDCStudyEventSchedule("EDCStudyEventSchedule"),
    EDCDataCreation("EDCDataCreation"),
    EDCDataModification("EDCDataModification"),
    EDCDataImport("EDCDataImport"),

    EDCParticipateNewForm("EDCParticipateNewForm"),
    EDCParticipateEditableForm("EDCParticipateEditableForm"),

    PACSDataUpload("PACSDataUpload"),
    PACSDataView("PACSDataView"),
    PACSDataDownload("PACSDataDownload"),
    PACSDataCreation("PACSDataCreation"),
    PACSDataDeletion("PACSDataDeletion"),
    PACSDataModification("PACSDataModification"),

    BIOPatientCreation("BIOPatientCreation"),
    BIOPatientLoad("BIOPatientLoad"),

    CTPLookupUpdate("CTPLookupUpdate"),

    LABOdmReload("LABOdmReload");

    private String label;

    AuditEvent(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}