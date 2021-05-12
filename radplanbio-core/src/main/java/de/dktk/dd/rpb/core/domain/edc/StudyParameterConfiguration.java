/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2020 RPB Team
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

    //region Members

    private boolean allowDiscrepancyManagement;
    private boolean sexRequired;
    private boolean personIdShownOnCrf;
    private boolean secondaryLabelViewable;
    private boolean adminForcedReasonForChange;

    private EnumCollectSubjectDob collectSubjectDob;
    private EnumStudySubjectIdGeneration studySubjectIdGeneration;
    private EnumRequired personIdRequired;
    private EnumRequired locationRequired;

    @XmlElement(name="StudyParameterListRef", namespace="http://www.openclinica.org/ns/odm_ext_v130/v3.1")
    private List<StudyParameter> studyParameterList;

    //endregion

    //region Properties

    public EnumCollectSubjectDob getCollectSubjectDob() {
        return this.collectSubjectDob;
    }

    public void setCollectSubjectDob(EnumCollectSubjectDob dobEnum) {
        this.collectSubjectDob = dobEnum;
    }

    public void setCollectSubjectDob(String value) {
        this.collectSubjectDob = EnumCollectSubjectDob.fromString(value);
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

    public EnumStudySubjectIdGeneration getStudySubjectIdGeneration() {
        return this.studySubjectIdGeneration;
    }

    public void setStudySubjectIdGeneration(EnumStudySubjectIdGeneration enumStudySubjectIdGeneration) {
        this.studySubjectIdGeneration = enumStudySubjectIdGeneration;
    }

    public void setStudySubjectIdGeneration(String value) {
        this.studySubjectIdGeneration = EnumStudySubjectIdGeneration.fromString(value);
    }

    public EnumRequired getPersonIdRequired() {
        return this.personIdRequired;
    }

    public void setPersonIdRequired(String value) {
        this.personIdRequired = EnumRequired.fromString(value);
    }

    public EnumRequired getLocationRequired() {
        return this.locationRequired;
    }

    public void setLocationRequired(EnumRequired value) {
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
                    this.collectSubjectDob = EnumCollectSubjectDob.fromString(sp.getValue());
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_discrepancyManagement")) {
                if (sp.getValue() != null) {
                    this.allowDiscrepancyManagement = sp.getValue().equals("true");
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_subjectPersonIdRequired")) {
                if (sp.getValue() != null) {
                    this.personIdRequired = EnumRequired.fromString(sp.getValue());
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_genderRequired")) {
                if (sp.getValue() != null) {
                    this.sexRequired = sp.getValue().equals("true");
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_subjectIdGeneration")) {
                if (sp.getValue() != null) {
                    this.studySubjectIdGeneration = EnumStudySubjectIdGeneration.fromString(sp.getValue());
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewerNameRequired")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewerNameDefault")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewerNameEditable")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewDateRequired")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewDateDefault")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_interviewDateEditable")) {
                if (sp.getValue() != null) {
                    // NOOP
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_personIdShownOnCRF")) {
                if (sp.getValue() != null) {
                    this.personIdShownOnCrf = sp.getValue().equals("true");
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_secondaryLabelViewable")) {
                if (sp.getValue() != null) {
                    this.secondaryLabelViewable = sp.getValue().equals("true");
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_adminForcedReasonForChange")) {
                if (sp.getValue() != null) {
                    this.adminForcedReasonForChange = sp.getValue().equals("true");
                }
            }
            else if (sp.getStudyParameterListId() != null && sp.getStudyParameterListId().equals("SPL_eventLocationRequired")) {
                if (sp.getValue() != null) {
                    this.locationRequired = EnumRequired.fromString(sp.getValue());
                }
            }
        }
    }

    //endregion

}