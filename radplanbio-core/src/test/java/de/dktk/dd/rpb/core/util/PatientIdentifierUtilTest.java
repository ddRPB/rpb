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

package de.dktk.dd.rpb.core.util;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PatientIdentifierUtilTest {

//region removePatientIdPrefix

    @Test
    public void removeHisCodePrefix_passes_id_without_prefix() {
        String patientId = "0000001";
        String result = PatientIdentifierUtil.removePatientIdPrefix(patientId);
        assertEquals(patientId, result);
    }

    @Test
    public void removeHisCodePrefix_passes_id_wit_capitalized_letters() {
        String patientId = "HH0KK000DD001";
        String result = PatientIdentifierUtil.removePatientIdPrefix(patientId);
        assertEquals(patientId, result);
    }

    @Test
    public void removeHisCodePrefix_removes_HIS_prefix() {
        String patientId = "0000001";
        String result = PatientIdentifierUtil.removePatientIdPrefix("HIS-" + patientId);
        assertEquals(patientId, result);
    }

    @Test
    public void removeHisCodePrefix_removes_dd_partner_side_prefix() {
        String patientId = "0000001";
        String result = PatientIdentifierUtil.removePatientIdPrefix("DD-" + patientId);
        assertEquals(patientId, result);
    }

    //endregion

    // region getPatientIdPrefix

    @Test
    public void getPatientIdPrefix_returns_empty_string_on_id_without_prefix() {
        String patientId = "0000001";
        String result = PatientIdentifierUtil.getPatientIdPrefix(patientId);
        assertEquals("", result);
    }

    @Test
    public void getPatientIdPrefix_returns_empty_string_on_id_wit_capitalized_letters() {
        String patientId = "HH0KK000DD001";
        String result = PatientIdentifierUtil.getPatientIdPrefix(patientId);
        assertEquals("", result);
    }

    @Test
    public void getPatientIdPrefix_returns_HIS_prefix() {
        String patientId = "0000001";
        String prefix = "HIS";
        String result = PatientIdentifierUtil.getPatientIdPrefix(prefix + "-" + patientId);
        assertEquals(prefix, result);
    }

    @Test
    public void getPatientIdPrefix_returns_dd_partner_side_prefix() {
        String patientId = "0000001";
        String prefix = "DD";
        String result = PatientIdentifierUtil.getPatientIdPrefix(prefix + "-" + patientId);
        assertEquals(prefix, result);
    }

    //endregion

    // region validateHospitalPatientId
    @Test
    public void validateHospitalPatientId_returns_true_if_id_is_valid() {
        String validPatientId = "002563398";
        assertTrue(PatientIdentifierUtil.validateHospitalPatientId(validPatientId));
    }

    @Test
    public void validateHospitalPatientId_returns_false_if_id_is_empty() {
        String validPatientId = "";
        assertFalse(PatientIdentifierUtil.validateHospitalPatientId(validPatientId));
    }

    @Test
    public void validateHospitalPatientId_returns_false_if_id_has_a_prefix() {
        String validPatientId = "HIS-001236985";
        assertFalse(PatientIdentifierUtil.validateHospitalPatientId(validPatientId));
    }

    // endregion


}