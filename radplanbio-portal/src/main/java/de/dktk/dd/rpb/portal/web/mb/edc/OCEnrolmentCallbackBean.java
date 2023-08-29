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

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.edc.EnumGender;
import de.dktk.dd.rpb.core.domain.edc.StudyParameterConfiguration;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.domain.randomisation.TreatmentArm;
import de.dktk.dd.rpb.core.exception.StringNotConvertibleToEnumException;
import de.dktk.dd.rpb.core.exception.WrongDateFormatException;
import de.dktk.dd.rpb.core.ocsoap.connect.OCConnectorException;
import de.dktk.dd.rpb.core.repository.support.Repository;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.CtpService;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static de.dktk.dd.rpb.core.util.Constants.RPB_IDENTIFIERSEP;

/**
 * ECRF Patient Identifier Callback Handler Bean
 * <p>
 * This is a ViewModel for oCEnrolmentCallbackBean.faces View
 *
 * @author RPB Team
 * @since 06 Feb 2020
 */
@Named("oCEnrolmentCallbackBean")
@Scope("view")
public class OCEnrolmentCallbackBean extends CrudEntityViewModel<de.dktk.dd.rpb.core.domain.edc.StudySubject, Integer> {

    private final MainBean mainBean;
    private final StudyIntegrationFacade studyIntegrationFacade;
    private final AuditLogService auditLogService;
    private final MessageUtil messageUtil;
    private final CtpService ctpService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private StudyParameterConfiguration studyParameterConfiguration;
    private TreatmentArm treatmentArm;
    // RPB study
    private de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy;

    private String studyId;
    private String siteSpecificStudyId;
    private String subjectId;
    private String secondSubjectId;
    private String enrollmentDate;
    private String enrollmentArmId;
    private String genderString;
    private String pseudonym;
    private String dateOfBirthString;
    private String yearOfBirthString;

    @Inject
    public OCEnrolmentCallbackBean(MainBean mainBean,
                                   StudyIntegrationFacade studyIntegrationFacade,
                                   AuditLogService auditLogService,
                                   MessageUtil messageUtil, CtpService ctpService) {
        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.auditLogService = auditLogService;
        this.messageUtil = messageUtil;
        this.ctpService = ctpService;
    }

    public StudyParameterConfiguration getStudyParameterConfiguration() {
        return studyParameterConfiguration;
    }

    public Study getRpbStudy() {
        return rpbStudy;
    }

    public String getEnrollmentArmId() {
        return enrollmentArmId;
    }

    public void setEnrollmentArmId(String enrollmentArmId) {
        this.enrollmentArmId = this.decodeUrlComponent(enrollmentArmId);
    }


    public String getGenderString() {
        return genderString;
    }

    public void setGenderString(String genderString) {
        this.genderString = this.decodeUrlComponent(genderString);
    }

    public String getYearOfBirthString() {
        return yearOfBirthString;
    }

    public void setYearOfBirthString(String yearOfBirthString) {
        this.yearOfBirthString = this.decodeUrlComponent(yearOfBirthString);
    }

    public TreatmentArm getTreatmentArm() {
        return treatmentArm;
    }

    public void setTreatmentArm(TreatmentArm treatmentArm) {
        this.treatmentArm = treatmentArm;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = this.decodeUrlComponent(studyId);
    }

    public String getSiteSpecificStudyId() {
        return siteSpecificStudyId;
    }

    public void setSiteSpecificStudyId(String siteSpecificStudyId) {
        this.siteSpecificStudyId = this.decodeUrlComponent(siteSpecificStudyId);
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = this.decodeUrlComponent(subjectId);
    }

    public String getSecondSubjectId() {
        return secondSubjectId;
    }

    public void setSecondSubjectId(String secondSubjectId) {
        this.secondSubjectId = this.decodeUrlComponent(secondSubjectId);
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = this.decodeUrlComponent(enrollmentDate);
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = this.decodeUrlComponent(pseudonym);
    }

    public String getDateOfBirthString() {
        return dateOfBirthString;
    }

    public void setDateOfBirthString(String dateOfBirthString) {
        this.dateOfBirthString = this.decodeUrlComponent(dateOfBirthString);
    }

    /**
     * Initialisation of the bean
     */
    @PostConstruct
    public void init() {

        if (!FacesContext.getCurrentInstance().isPostback()) {
            try {
                if (this.mainBean.getMyAccount().getPartnerSite() == null) {
                    throw new Exception("Your account is not associated with any partner site.");
                } else if (this.mainBean.getMyAccount().getPartnerSite().getEdc() == null) {
                    throw new Exception("Your partner site does not have associated OpenClinica EDC");
                }

                if (!this.mainBean.getMyAccount().hasOpenClinicaAccount()) {
                    throw new Exception("There is now OpenClinica user account for you.");
                }

                this.setPreSortOrder(
                        this.buildSortOrder()
                );

                // Init Facade to use service that are valid for logged user PartnerSite (from mainBean)
                this.studyIntegrationFacade.init(this.mainBean);
                this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);

            } catch (Exception err) {
                this.messageUtil.error(err);
            }
        }
    }

    /***
     * Prepare bean on load
     */
    public void onLoad() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (this.studyId == null || this.siteSpecificStudyId == null) {
                messageUtil.error("Please provide a study and a site identifier as parameter of your request");
            } else {
                loadRpbStudy();
                loadStudyParameterConfiguration();
                loadTreatmentArmIfProvided();
            }
        }
    }

    private void loadStudyParameterConfiguration() {
        if (this.rpbStudy == null || this.rpbStudy.getEdcStudy() == null) {
            messageUtil.errorText("There was a problem to load your study. Please try again.");
            return;
        }

        try {
            this.studyParameterConfiguration = this.rpbStudy.getEdcStudy().getMetaDataVersion().getStudyDetails().getStudyParameterConfiguration();
        } catch (Exception e) {
            String errorMessage = "There is a problem loading the study parameter configuration.";
            log.error(errorMessage, e);
            messageUtil.errorText("There is a problem loading the study parameter configuration.");
        }
    }

    private void loadRpbStudy() {
        try {
            // Using the parent study always to get valid the studyParameterConfiguration.
            // OpenClinica does not propagate these settings from the parent study to the side specific studies
            this.rpbStudy = this.studyIntegrationFacade.loadStudyWithMetadataByIdentifierViaEngineUser(this.studyId, this.studyId);
        } catch (NullPointerException e) {
            String errorMessage = "There is a problem loading the meta date of your study.";
            log.error(errorMessage, e);
            messageUtil.error(errorMessage);
        }
    }

    private void loadTreatmentArmIfProvided() {
        if (this.enrollmentArmId != null && this.enrollmentArmIdIsInteger()) {


            if (this.getTreatmentArmById(this.enrollmentArmId) == null) {
                messageUtil.error("There is no treatment arm with the identifier \"" + this.enrollmentArmId +
                        "\" in your study. Please check the combination of study, site and treatment arm identifier");
            } else {
                this.treatmentArm = this.getTreatmentArmById(this.enrollmentArmId);
            }
        }
    }

    private boolean enrollmentArmIdIsInteger() {
        try {
            Integer.parseInt(this.enrollmentArmId);
        } catch (NumberFormatException e) {
            String errorMessage = "The enrollment_arm parameter is probably not an integer. Value: " + this.enrollmentArmId;
            this.log.error(errorMessage, e);
            this.messageUtil.error(errorMessage);
            return false;
        }
        return true;
    }

    public TreatmentArm getTreatmentArmById(String id) {
        int treatmentArmId = Integer.parseInt(id);
        if (this.rpbStudy != null && this.rpbStudy.getTreatmentArms() != null) {
            for (TreatmentArm treatmentArm : this.rpbStudy.getTreatmentArms()) {
                if (treatmentArm.getId().equals(treatmentArmId)) {
                    return treatmentArm;
                }
            }
        }
        return null;
    }

    /***
     * Creates a new study subject in OpenClinica and redirects to /edc/ecrfStudies.faces page.
     *
     * @throws IOException
     * @throws WrongDateFormatException
     */
    public void createNewStudySubject() throws IOException, WrongDateFormatException, DatatypeConfigurationException {
        StudySubject studySubject = getEdcStudySubject();

        String ocStudySubjectId = null;
        try {
            ocStudySubjectId = studyIntegrationFacade.createNewOpenClinicaStudySubject(
                    this.studyId,
                    this.siteSpecificStudyId,
                    studySubject,
                    studyParameterConfiguration);
        } catch (OCConnectorException e) {
            String errorMessage = "There is a problem creating a new StudySubject.";
            this.log.error(errorMessage, e);
            this.messageUtil.error(e);
            return;
        }

        if (ocStudySubjectId != null && !ocStudySubjectId.isEmpty()) {
            studySubject.setStudySubjectId(ocStudySubjectId);
            addCtpEntry(studySubject);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.redirect("/edc/ecrfStudies.faces?study=" + this.studyId + "&studySubject=" + ocStudySubjectId);
        } else {
            messageUtil.error("Creating the subject failed, because the response is null or undefined.");
        }


    }

    private void addCtpEntry(StudySubject studySubject) {
        if (portalRunsOnSamePartnerSide()) {
            String edcCode = this.rpbStudy.getTagValue("EDC-code");
            try {
                this.ctpService.updateSubjectLookupEntry(edcCode, studySubject);
            } catch (Exception e) {
                this.log.debug("Problem adding CTP entry " + "study: " + this.rpbStudy.getName() +
                        " study subject: " + studySubject.toString(), e);
            }
        } else {
            this.log.debug("Most likely, the portal does not run within the same side as the study. " +
                    "Since we invoke a service that is bound to the specific location, " +
                    "please trigger an update of the overall list on the portal that is located on this side." +
                    " As an admin you can find this functionality under /admin/ctms/edcStudyCRUD.faces");
        }
    }

    private boolean portalRunsOnSamePartnerSide() {
        String partnerSideIdentifierPortal = this.messageUtil.getResourcesUtil().getProperty("partner_site_identifier");
        String partnerSideIdentifierStudy = "";

        if (this.studyId.equals(this.siteSpecificStudyId)) {
            partnerSideIdentifierStudy = this.rpbStudy.getPartnerSite().getIdentifier();
        } else {
            if (this.siteSpecificStudyId.contains(RPB_IDENTIFIERSEP + this.studyId)) {
                partnerSideIdentifierStudy = this.siteSpecificStudyId.
                        replace(RPB_IDENTIFIERSEP + this.studyId, "");
            } else {
                String errorMessage = "This study is supposed to be multi centric and the actual convention is " +
                        "that the site identifier includes the study identifier plus a side specific prefix," +
                        " for example: studyIdentifier = \"ABCSTUDY \" -> siteIdentifier = \" DD-ABCSTUDY \". " +
                        "Thereby, DD is specific for all studies of the site Dresden";
                this.log.error(errorMessage);
                return false;
            }
        }

        return partnerSideIdentifierPortal.equals(partnerSideIdentifierStudy);
    }

    private StudySubject getEdcStudySubject() throws WrongDateFormatException {
        StudySubject studySubject = new StudySubject();
        studySubject.setPerson(new Person());
        studySubject.setStudySubjectId(this.optPropertyString(this, "subjectId"));
        studySubject.setPid(
                this.mainBean.constructMySubjectFullPid(this.optPropertyString(this, "pseudonym")));
        studySubject.setSecondaryId(this.optPropertyString(this, "secondSubjectId"));
        this.populateGender(studySubject);
        this.populateBirthDate(studySubject);
        this.populateEnrollmentDate(studySubject);
        this.populateYearOfBirth(studySubject);

        return studySubject;
    }

    private void populateEnrollmentDate(StudySubject studySubject) throws WrongDateFormatException {
        if (enrollmentDate != null) {
            Date date = this.parseDateStringRpbFormat(enrollmentDate);
            studySubject.setEnrollmentDate(date);
        }
    }

    private void populateYearOfBirth(StudySubject studySubject) {
        if (this.yearOfBirthString != null) {
            Integer yearOfBirth = null;
            try {
                yearOfBirth = Integer.parseInt(this.yearOfBirthString);
            } catch (NumberFormatException e) {
                String errorMessage = "Failed to parse year of birth string: " + this.yearOfBirthString;
                log.error(errorMessage, e);
                messageUtil.error(errorMessage, e);
            }
            studySubject.setYearOfBirth(yearOfBirth);
        }
    }

    private void populateBirthDate(StudySubject studySubject) throws WrongDateFormatException {
        if (this.dateOfBirthString != null) {
            Date date = this.parseDateStringRpbFormat(this.dateOfBirthString);
            studySubject.getPerson().setBirthdate(date);
        }
        studySubject.setDateOfBirth(optPropertyString(this, "dateOfBirthString"));

    }

    private Date parseDateStringRpbFormat(String dateString) throws WrongDateFormatException {
        DateFormat format = new SimpleDateFormat(Constants.RPB_DATEFORMAT);
        try {
            return format.parse(dateString);

        } catch (ParseException e) {
            WrongDateFormatException exception = new WrongDateFormatException(dateString, Constants.RPB_DATEFORMAT);
            this.log.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private void populateGender(StudySubject studySubject) {
        if (this.genderString != null) {
            try {
                EnumGender.fromString(this.genderString);
                studySubject.setSex(this.genderString);
            } catch (StringNotConvertibleToEnumException e) {
                String errorMessage = "Parsing the gender failed for: " + this.genderString;
                this.log.error(errorMessage, e);
                this.messageUtil.error(errorMessage);
            }
        } else {
            //avoid null pointer exceptions
            studySubject.setSex("");

        }
    }

    /***
     * Returns the property string or an empty string if the property is null
     *
     * @param object Object which contains the requested property
     * @param propertyName String Name of the property
     * @return String Property value
     */
    private String optPropertyString(Object object, String propertyName) {
        BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
        String propertyString = "";
        try {
            propertyString = beanUtilsBean.getProperty(object, propertyName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            String errorMessage = "Parsing failed for property: \"" + propertyName + "\" on object: \"" + object.toString() + "\"";
            this.log.debug(errorMessage, e);
        } finally {
            return propertyString;
        }
    }

    /**
     * Verification if treatment/enrollment arm is valid if provided as parameter
     *
     * @return boolean true if enrollment_arm is not provided or identifier allows to load a treatment arm associated with the study
     */
    public boolean treatmentArmSettingsAreValid() {
        return this.enrollmentArmId == null || this.enrollmentArmId.isEmpty() || this.treatmentArm != null;
    }


    private String decodeUrlComponent(String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            String errorMessage = "There was a problem decoding the URL parameter " + string;
            log.debug(errorMessage, e);
            messageUtil.errorText(errorMessage);
        }
        return string;
    }

    /**
     * Get Repository
     *
     * @return repository
     */
    @Override
    protected Repository<StudySubject, Integer> getRepository() {
        return null;
    }

    /**
     * Prepare new entity
     */
    @Override
    public void prepareNewEntity() {

    }

    /**
     * Need to build an initial sort order for data table multi sort
     */
    @Override
    protected List<SortMeta> buildSortOrder() {
        return null;
    }
}
