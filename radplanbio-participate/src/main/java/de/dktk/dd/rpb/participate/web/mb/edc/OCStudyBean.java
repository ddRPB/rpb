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

package de.dktk.dd.rpb.participate.web.mb.edc;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;

import de.dktk.dd.rpb.core.context.UserContext;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.ocsoap.types.StudySubject;

import de.dktk.dd.rpb.core.service.OpenClinicaService;

import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.participate.web.mb.MainBean;
import de.dktk.dd.rpb.participate.web.util.MessageUtil;

import org.openclinica.ws.beans.*;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

/**
 * OpenClinica Study Managed Bean
 *
 * This is a ViewModel for study view
 *
 * @author tomas@skripcak.net
 * @since 05 Jun 2013
 */
@Named("mbStudy")
@Scope("view")
public class OCStudyBean implements Serializable {

    //region Finals

    private static final long serialVersionUID = 1L;

    //endregion

    //region Members

    private MainBean mainBean;
    private MessageUtil messageUtil;

    // Study (SOAP)
    private List<StudyType> studyTypeList;
    private StudyType selectedStudyType;

    // StudySite
    private List<Study> studyList;
    private Study selectedStudy;

    // StudySubject
    private List<StudySubject> studySubjectsList;
    private StudySubject selectedStudySubject;
    private de.dktk.dd.rpb.core.domain.edc.StudySubject selectedStudySubjectData;

    // StudyEvents
    private List<EventData> eventDataList;

    // Forms
    private List<FormData> selectedFormDataList;

    // CRF data
    private EventData selectedEventData;
    private FormData selectedEventFormData;
    private ItemData selectedItemData;

    //endregion

    //region Constructors

    @Inject
    public OCStudyBean(MainBean mainBean, MessageUtil messageUtil) {
        this.mainBean = mainBean;
        this.messageUtil = messageUtil;
    }

    //endregion

    //region Properties

    //region StudyTypeList

    /**
     * Get Study Type List
     *
     * @return List - Study Type List
     */
    public List<StudyType> getStudyTypeList() {
        // Use lambda exp after upgrade to Java8 to sort that stuff
        //Collections.sort(this.studyTypeList, (s1, s2) -> s1.getName().compareTo(s2.getName()));

        if (this.studyTypeList != null) {
            Collections.sort(this.studyTypeList, new Comparator<StudyType>() {
                public int compare(StudyType s1, StudyType s2) {
                    String name1 = s1.getName();
                    String name2 = s2.getName();

                    return (name1.compareToIgnoreCase(name2));
                }
            });
        }

        return this.studyTypeList;
    }

    /**
     * Set Study Type List
     *
     * @param value - Study Type List
     */
    public void setStudyTypeList(List<StudyType> value) {
        this.studyTypeList = value;
    }

    //endregion

    //region SelectedStudyType

    public StudyType getSelectedStudyType() {
        return selectedStudyType;
    }

    public void setSelectedStudyType(StudyType selectedStudyType) {
        this.selectedStudyType = selectedStudyType;
    }

    //endregion

    //region StudySubjectsList Property

    public List<StudySubject> getStudySubjectsList() {
        return this.studySubjectsList;
    }

    public void setStudySubjectsList(List<StudySubject> studySubjectsList) {
        this.studySubjectsList = studySubjectsList;
    }

    //endregion

    //region SelectedStudySubject Property

    public StudySubject getSelectedStudySubject() {
        return this.selectedStudySubject;
    }

    public void setSelectedStudySubject(StudySubject selectedStudySubject) {
        if (selectedStudySubject == null || !selectedStudySubject.equals(this.selectedStudySubject)) {
            this.selectedStudySubject = selectedStudySubject;

            // Clean selecting when selected study subject change
            this.selectedFormDataList = null;
            this.selectedEventFormData = null;
        }
    }

    //endregion

    //region SelectedStudySubject Data Property

    public de.dktk.dd.rpb.core.domain.edc.StudySubject getSelectedStudySubjectData() {
        return this.selectedStudySubjectData;
    }

    public void setSelectedStudySubjectData(de.dktk.dd.rpb.core.domain.edc.StudySubject ss) {
        this.selectedStudySubjectData = ss;
    }

    //endregion

    //region StudyEventList

    public List<EventData> getEventDataList() {
        return this.eventDataList;
    }

    public void setEventDataList(List<EventData> list) {
        this.eventDataList = list;
    }

    //endregion

    //region SelectedFormDataList

    public List<FormData> getSelectedFormDataList() {
        return this.selectedFormDataList;
    }

    public void setSelectedFormDataList(List<FormData> formDataList) {
        this.selectedFormDataList = formDataList;
    }

    //endregion

    //region SelectedEventData

    public EventData getSelectedEventData() {
        return this.selectedEventData;
    }

    public void setSelectedEventData(EventData eventData) {
        this.selectedEventData = eventData;
    }

    //endregion

    //region SelectedEventFormData

    public FormData getSelectedEventFormData() {
        return selectedEventFormData;
    }

    public void setSelectedEventFormData(FormData fd) {
        this.selectedEventFormData = fd;
    }

    //endregion

    //region SelectedItemData

    public ItemData getSelectedItemData() {
        return this.selectedItemData;
    }

    public void setSelectedItemData(ItemData itemData) {
        this.selectedItemData = itemData;
    }

    //endregion

    //endregion

    //region Init

    @PostConstruct
    public void init() {
        this.reloadStudies();
        this.loadUserActiveEdcStudy();
        this.selectedParentStudyChanged();
    }

    //endregion

    //region Commands

    //region Study

    /**
     * Reload studies available for user from OpenClinica EDC
     * This fills the StudyType list (this represents the hierarchy study/sites)
     */
    public void reloadStudies() {
        try {
            this.studyTypeList = this.mainBean.getOpenClinicaService().listAllStudies();
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Parent EDC study changed
     */
    public void selectedParentStudyChanged() {
        this.reloadStudySites();
        this.clearSelection();

        //TODO: make this selectable from UI partner sites will not work out of the RPB scope

        // If one site (mono-centre) automatically select
        if (this.studyList != null && this.studyList.size() == 1) {
            this.selectedStudy = this.studyList.get(0);
        }
        // Otherwise select study site study according to partner site identifier
        else if (this.studyList != null) {
            for (Study s : this.studyList) {
                if (s.getSiteName().startsWith("DD-")) {
                    this.selectedStudy = s;
                    break;
                }
            }
        }

        // Load corresponding RPB study entity
        if (this.selectedStudy != null) {

            // Need to have this study as active otherwise cannot use RESTfull URLs
            this.changeActiveStudy();
        }

        // Reload study subjects
        this.reloadStudySubjects();
    }

    /**
     * Reload Study Sites depending on selected study name
     */
    public void reloadStudySites() {
        try {
            this.studyList = null;

            if (this.selectedStudyType != null) {
                for (StudyType st : this.studyTypeList) {
                    if (st.getName().equals(this.selectedStudyType.getName())) {

                        // Representing collection of Study Sites
                        ArrayList<Study> tempStudyList = new ArrayList<>();

                        // Multi-centric study
                        if (st.getSites() != null) {
                            for (SiteType site : st.getSites().getSite()) {
                                Study studySite = new Study(st);
                                studySite.setStudyOID(site.getOid());
                                studySite.setSiteName(site.getIdentifier());
                                studySite.setRealSiteName(site.getName());
                                tempStudyList.add(studySite);
                            }
                        }
                        // Mono-centric study
                        else {
                            Study study = new Study(st);
                            study.setSiteName(study.getStudyIdentifier());
                            tempStudyList.add(study);
                        }

                       this.studyList = tempStudyList;
                    }
                }
            }
        }
        catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //region Study Subject

    /**
     * Reload study subject registered for selected study/site from OC using web service
     */
    public void reloadStudySubjects() {
        FacesContext context = FacesContext.getCurrentInstance();

        this.studySubjectsList = null;
        try {
            // Study was selected
            if (this.mainBean.getOpenClinicaService() != null && this.selectedStudy != null) {

                List<StudySubject> subjectList = new ArrayList<>();

                for (StudySubjectWithEventsType sswe : this.mainBean.getOpenClinicaService().listAllStudySubjectsByStudy(this.selectedStudy)) {
                    StudySubject ss = null;

                    try {
                        ss = new StudySubject(this.selectedStudy, sswe);
                    }
                    catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }

                    subjectList.add(ss);
                }

                this.studySubjectsList = subjectList;

                // Reset the selection
                this.selectedStudySubject = null;
                this.selectedStudySubjectData = null;
                this.eventDataList = null;
            }
        }
        catch (Exception err) {
            context.addMessage(null, new FacesMessage("Error", err.getMessage()));
        }
    }

    /**
     * Autocomplete StudySubject while typing
     * @param query study subject ID
     * @return list of filtered StudySubject entities
     */
    public List<StudySubject> completeStudySubjects(String query) {
        List<StudySubject> filteredResults = new ArrayList<>();

        if (this.studySubjectsList != null) {
            for (StudySubject ss : this.studySubjectsList) {
                if (this.selectedStudy.getStudyIdentifier().equals(Constants.study0Identifier) && !query.startsWith(Constants.HISprefix)) {
                    if (ss.getStudySubjectLabel().startsWith(Constants.HISprefix + query)) {
                        filteredResults.add(ss);
                    }
                }
                else {
                    if (ss.getStudySubjectLabel().startsWith(query)) {
                        filteredResults.add(ss);
                    }
                }
            }
        }

        return filteredResults;
    }

    public void onItemSelect() {
        // Reload events with forms
        this.reloadEvents();
    }

    //endregion

    //region Events

    public void reloadEvents() {
        // Load ODM resource for selected study subject
        String queryOdmXmlPath = this.selectedStudy.getStudyOID() + "/" + this.selectedStudySubject.getStudySubjectLabel() + "/*/*";
        Odm selectedStudySubjectOdm = this.mainBean.getOpenClinicaService().getStudyCasebookOdm(
                OpenClinicaService.CasebookFormat.XML,
                OpenClinicaService.CasebookMethod.VIEW,
                queryOdmXmlPath
        );

        if (selectedStudySubjectOdm != null) {

            // Create Definitions from References in metadata
            selectedStudySubjectOdm.updateHierarchy();

            this.selectedStudySubjectData = selectedStudySubjectOdm.findUniqueStudySubjectOrNone(
                    this.selectedStudySubject.getStudySubjectLabel()
            );

            // Link metadata to selectedStudySubject clinical data
            if (this.selectedStudySubjectData != null) {
                this.selectedStudySubjectData.linkOdmDefinitions(selectedStudySubjectOdm);
            }

            // Load scheduled ePRO events
            if (this.selectedStudySubjectData != null) {
                this.eventDataList = this.mainBean.getOpenClinicaService()
                        .loadParticipateEvents(
                                this.selectedStudy.getStudyOID(),
                                this.selectedStudySubjectData.getSubjectKey()

                        );
            }
            else {
                this.eventDataList = null;
            }
        }
    }

    //endregion

    //region Enketo Form

    /**
     * Redirect to Enketo chained forms URL of corresponding to selected EDC CRFs
     */
    public void redirect() {
        try {
            if (this.selectedFormDataList != null && this.selectedFormDataList.size() > 0) {
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

                String chainedFormsUrl = "";
                for (FormData form : this.selectedFormDataList) {

                    // Form is available for data entry
                    if (!form.getStatus().equals(EnumFormDataStatus.UNAVAILABLE.toString())) {

                        // New form
                        if (form.getStatus().equals(EnumFormDataStatus.NOT_STARTED.toString())) {
//                            this.auditLogService.event(
//                                    AuditEvent.EDCParticipateNewForm,
//                                    this.selectedStudy.getStudyOID(),
//                                    this.selectedStudySubject.getStudySubjectLabel(),
//                                    form.getFormOid()
//                            );

                            if (chainedFormsUrl.equals("")) {
                                chainedFormsUrl = form.getUrl();
                            }
                            // Encoding the following return url
                            else {
                                // First occurrence of returnUrl is not encoded
                                if (!chainedFormsUrl.contains("&returnUrl=")) {
                                    chainedFormsUrl += "&returnUrl=" +  URLEncoder.encode(form.getUrl(), "UTF-8");
                                }
                                // For each other encode also returnUrl query string
                                else {
                                    chainedFormsUrl += URLEncoder.encode("&returnUrl=" + form.getUrl(), "UTF-8");
                                }
                            }
                        }
                        // Editable form
                        else if (form.getStatus().equals(EnumFormDataStatus.AVAILABLE.toString())) {
//                            this.auditLogService.event(
//                                    AuditEvent.EDCParticipateEditableForm,
//                                    this.selectedStudy.getStudyOID(),
//                                    this.selectedStudySubject.getStudySubjectLabel(),
//                                    form.getFormOid()
//                            );

                            String editableFormUrl = this.mainBean.getOpenClinicaService()
                                    .loadEditableUrl(
                                            form.getUrl(),
                                            ""
                                    );
                            // Cleanup
                            editableFormUrl = editableFormUrl.replace("&returnUrl=", "");

                            if (!"".equals(editableFormUrl)) {
                                if (chainedFormsUrl.equals("")) {
                                    chainedFormsUrl = editableFormUrl;
                                }
                                // Encoding the following return url
                                else {
                                    // First occurrence of returnUrl is not encoded
                                    if (!chainedFormsUrl.contains("&returnUrl=")) {
                                        chainedFormsUrl += "&returnUrl=" + URLEncoder.encode(editableFormUrl, "UTF-8");
                                    }
                                    // For each other encode also returnUrl query string
                                    else {
                                        chainedFormsUrl += URLEncoder.encode("&returnUrl=" + editableFormUrl, "UTF-8");
                                    }
                                }
                            }
                        }
                    }
                }

                // First occurrence of returnUrl is not encoded
                if (!chainedFormsUrl.contains("&returnUrl=")) {
                    chainedFormsUrl += "&returnUrl=" + URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");
                }
                // Last encoding the redirect back to participate
                else {
                    chainedFormsUrl += URLEncoder.encode("&returnUrl=" + request.getRequestURL().toString(), "UTF-8");
                }

                // Log out from participate first
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    new SecurityContextLogoutHandler().logout(request, null, null);
                }
                SecurityContextHolder.getContext().setAuthentication(null);

                // Redirect to form
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect(chainedFormsUrl);
            }
            else {
                throw new Exception("eCRF need to be selected.");
            }
        }
        catch(Exception err){
            this.messageUtil.error(err);
        }
    }

    /**
     * Mark selected CRFs as complete in EDC
     */
    public void markAsComplete(EventData eventData) {
        try {
            if (this.selectedFormDataList != null && this.selectedFormDataList.size() > 0) {
                for (FormData form : this.selectedFormDataList) {

                    // Form data is available
                    if (form.getStatus().equals(EnumFormDataStatus.AVAILABLE.toString())) {

                        // Get EDC user ApiKey
                        UserAccount ocAccount = this.mainBean.getOpenClinicaService().loginUserAccount(
                                UserContext.getUsername(),
                                UserContext.getClearPassword()
                        );

                        // ApiKey exists
                        if (ocAccount.getApiKey() != null && !ocAccount.getApiKey().equals("")) {

                            // Mark event occurrence as completed
                            boolean success = this.mainBean.getOpenClinicaService().completeParticipantEvent(
                                    this.selectedStudySubjectData.getSubjectKey(),
                                    eventData.getStudyEventOid(),
                                    eventData.getStudyEventRepeatKey(),
                                    ocAccount.getApiKey()
                            );

                            // Result
                            if (!success) {
                                throw new Exception("Failed to mark the event in EDC as completed.");
                            }
                            else {
                                this.messageUtil.infoText("Participant event marked as completed.");

                                // Reload events with forms
                                this.reloadEvents();
                            }
                        }
                    }
                }
            }
            else {
                throw new Exception("eCRF need to be selected.");
            }
        }
        catch(Exception err) {
            this.messageUtil.error(err);
        }
    }

    //endregion

    //endregion

    //region Private Methods

    private StudyType studyTypeLookup(String studyIdentifier) {
        if (studyTypeList != null) {
            for (StudyType st : this.studyTypeList) {
                // Multi-centric study
                if (st.getSites() != null) {
                    for (SiteType site : st.getSites().getSite()) {
                        if (site.getIdentifier().equals(studyIdentifier)) {
                            // Select the master study in combo box
                            return st;
                        }
                    }
                }

                // Try to mach parent study also for Mono-centric study
                if (st.getIdentifier().equals(studyIdentifier)) {
                    // Select the master study in combo box
                    return st;
                }
            }
        }

        return null;
    }

    private void loadUserActiveEdcStudy() {
        // Auto load active study
        if (this.mainBean.getActiveStudy() != null) {

            this.selectedStudyType = this.studyTypeLookup(this.mainBean.getActiveStudy().getUniqueIdentifier());
        }
    }

    /**
     * Set all selected entities to null
     */
    private void clearSelection() {
        this.selectedStudy = null;
        this.selectedStudySubject = null;
        this.selectedStudySubjectData = null;
        this.selectedEventData = null;
        this.selectedEventFormData = null;
        this.selectedFormDataList = null;
        this.selectedItemData = null;
    }

    /**
     *  Change active study in open clinica for current user
     */
    private void changeActiveStudy() {
//        try {
//            if (this.mainBean.getMyAccount().isLdapUser()) {
//                this.mainBean.changeUserActiveStudy(
//                        this.mainBean.getSvcWebApi()
//                                .loadEdcStudy(
//                                        this.selectedStudy.getSiteName()
//                                )
//                );
//            }
//            else {
//                this.mainBean.changeUserActiveStudy(
//                        this.mainBean.getSvcRpb()
//                                .loadOCStudyByIdentifier(
//                                        this.selectedStudy.getSiteName()
//                                )
//                );
//            }
//        }
//        catch (Exception err) {
//            this.messageUtil.error(err);
//        }
    }

    //endregion

}