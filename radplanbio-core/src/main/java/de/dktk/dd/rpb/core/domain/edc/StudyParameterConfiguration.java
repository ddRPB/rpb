/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2015 Tomas Skripcak
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

/**
 * StudyParameterConfiguration - wrapper around OpenClinica study parameters configuration
 *
 * @author tomas@skripcak.net
 * @since 12 Sep 2013
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="StudyParameterConfiguration", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
public class StudyParameterConfiguration implements Serializable {

    //region Enums

    public enum RON {
        required,
        optional,
        not_used
    }

    public enum StudySubjectIdGeneration {
        manual,
        auto_editable,
        auto
    }

    //endregion

    //region Members

    private boolean collectSubjectDayOfBirth;
    private boolean allowDiscrepancyManagement;
    private boolean sexRequired;
    private boolean personIdShownOnCrf;
    private boolean secondaryLabelViewable;
    private boolean adminForcedReasonForChange;

    private StudySubjectIdGeneration studySubjectIdGeneration;
    private RON personIdRequired;
    private RON locationRequired;

    @XmlElement(name="StudyParameterListRef", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private List<StudyParameter> studyParameterList;

    //endregion

    //region Properties

    public boolean getCollectSubjectDayOfBirth() {
        return this.collectSubjectDayOfBirth;
    }

    public void setCollectSubjectDayOfBirth(Boolean value) {
        this.collectSubjectDayOfBirth = value;
    }

    public boolean getAllowDiscrepancyManagement() {
        return this.allowDiscrepancyManagement;
    }

    public void setAllowDiscrepancyManagement(Boolean value) {
        this.allowDiscrepancyManagement = value;
    }

    public boolean getSexRequired() {
        return this.sexRequired;
    }

    public void setSexRequired(Boolean value) {
        this.sexRequired = value;
    }

    public boolean getPersonIdShownOnCrf() {
        return this.personIdShownOnCrf;
    }

    public void setPersonIdShownOnCrf(Boolean value) {
        this.personIdShownOnCrf = value;
    }

    public boolean getSecondaryLabelViewable() {
        return this.secondaryLabelViewable;
    }

    public void setSecondaryLabelViewable(Boolean value) {
        this.secondaryLabelViewable = value;
    }

    public boolean getAdminForcedReasonForChange() {
        return this.adminForcedReasonForChange;
    }

    public void setAdminForcedReasonForChange(Boolean value) {
        this.adminForcedReasonForChange = value;
    }

    public StudySubjectIdGeneration getStudySubjectIdGeneration() {
        return this.studySubjectIdGeneration;
    }

    public void setStudySubjectIdGeneration(String value) {
        if (value.equals("manual")) {
            this.studySubjectIdGeneration = StudySubjectIdGeneration.manual;
        }
        else if (value.equals("auto editable")) {
            this.studySubjectIdGeneration = StudySubjectIdGeneration.auto_editable;
        }
        else if (value.equals("auto non-editable")) {
            this.studySubjectIdGeneration = StudySubjectIdGeneration.auto;
        }
    }

    public RON getPersonIdRequired() {
        return this.personIdRequired;
    }

    public void setPersonIdRequired(String value) {
        if (value.equals("required")) {
            this.personIdRequired = RON.required;
        }
        else if (value.equals("optional")) {
            this.personIdRequired = RON.optional;
        }
        else if (value.equals("not used")) {
            this.personIdRequired = RON.not_used;
        }
    }

    public RON getLocationRequired() {
        return this.locationRequired;
    }

    public void setLocationRequired(RON value) {
        this.locationRequired = value;
    }

    public List<StudyParameter> getStudyParameterList() {
        return this.studyParameterList;
    }

    public void setStudyParameterList(List<StudyParameter> list) {
        this.studyParameterList = list;
    }

    //endregion

    //region Methods

    public void reloadParameterFromList() {
        for (StudyParameter sp : this.studyParameterList) {
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_collectDob")) {
                if (sp.getValue() != null) {
                    this.collectSubjectDayOfBirth = sp.getValue().equals("1");
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_discrepancyManagement")) {
                if (sp.getValue() != null) {
                    this.allowDiscrepancyManagement = sp.getValue().equals("true");
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_subjectPersonIdRequired")) {
                if (sp.getValue() != null) {
                    if (sp.getValue().equals("required")) {
                        this.personIdRequired = RON.required;
                    }
                    if (sp.getValue().equals("optional")) {
                        this.personIdRequired = RON.optional;
                    }
                    if (sp.getValue().equals("not used")) {
                        this.personIdRequired = RON.not_used;
                    }
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_genderRequired")) {
                if (sp.getValue() != null) {
                    this.sexRequired = sp.getValue().equals("true");
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_subjectIdGeneration")) {
                if (sp.getValue() != null) {
                    if (sp.getValue().equals("manual")) {
                        this.studySubjectIdGeneration = StudySubjectIdGeneration.manual;
                    }
                    if (sp.getValue().equals("auto editable")) {
                        this.studySubjectIdGeneration = StudySubjectIdGeneration.auto_editable;
                    }
                    if (sp.getValue().equals("auto non-editable")) {
                        this.studySubjectIdGeneration = StudySubjectIdGeneration.auto;
                    }
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewerNameRequired")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewerNameDefault")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewerNameEditable")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewDateRequired")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewDateDefault")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewDateEditable")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_personIdShownOnCRF")) {
                if (sp.getValue() != null) {
                    this.personIdShownOnCrf = sp.getValue().equals("true");
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_secondaryLabelViewable")) {
                if (sp.getValue() != null) {
                    this.secondaryLabelViewable = sp.getValue().equals("true");
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_adminForcedReasonForChange")) {
                if (sp.getValue() != null) {
                    this.adminForcedReasonForChange = sp.getValue().equals("true");
                }
            }
            if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_eventLocationRequired")) {
                if (sp.getValue() != null) {
                    if (sp.getValue().equals("required")) {
                        this.locationRequired = RON.required;
                    }
                    if (sp.getValue().equals("optional")) {
                        this.locationRequired = RON.optional;
                    }
                    if (sp.getValue().equals("not used")) {
                        this.locationRequired = RON.not_used;
                    }
                }
            }
        }
    }

    //endregion

}
