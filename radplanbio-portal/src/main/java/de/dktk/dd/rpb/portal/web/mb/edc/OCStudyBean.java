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

package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.edc.*;
import de.dktk.dd.rpb.core.exception.DataBaseItemNotFoundException;
import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.ocsoap.types.StudySubject;
import de.dktk.dd.rpb.core.repository.edc.IStudySubjectRepository;
import de.dktk.dd.rpb.core.service.AuditEvent;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.ICtpService;
import de.dktk.dd.rpb.core.service.LabKeyService;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.PatientIdentifierUtil;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.mb.support.CrudEntityViewModel;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.StudyType;
import org.primefaces.component.tabview.TabView;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.chart.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static de.dktk.dd.rpb.core.util.Constants.RPB_IDENTIFIERSEP;

/**
 * EDC system OpenClinica Study Managed Bean
 * <p>
 * This is a ViewModel for ecrfStudies.faces View
 *
 * @author tomas@skripcak.net
 * @since 05 Jun 2013
 */
@Named("mbStudy")
@Scope("view")
public class OCStudyBean extends CrudEntityViewModel<de.dktk.dd.rpb.core.domain.edc.StudySubject, Integer> {

    //region Finals

    private static final Logger log = Logger.getLogger(OCStudyBean.class);
    private static final long serialVersionUID = 1L;

    private static Map<String, Object> genderValues;

    static {
        genderValues = new LinkedHashMap<>();
        genderValues.put("Male", "m"); //label, value
        genderValues.put("Female", "f");
    }

    //endregion

    //region Injects

    private final ICtpService svcCtp;
    private final LabKeyService labKeyService;
    private MainBean mainBean;
    private AuditLogService auditLogService;
    private StudyIntegrationFacade studyIntegrationFacade;

    //region Repository - Dummy

    @SuppressWarnings("unused")
    private IStudySubjectRepository repository;

    //endregion

    //endregion

    //region Members

    // GUI
    private List<SortMeta> sitesPreSortOrder;
    private TabView newSubjectTabView;
    // RPB study
    private de.dktk.dd.rpb.core.domain.ctms.Study rpbStudy;
    private StudyParameterConfiguration studyConfiguration;
    // Parent study
    private List<StudyType> studyTypeList;
    private StudyType selectedStudyType;
    // StudySite
    private List<Study> studyList;
    private List<Study> filteredStudies;
    private List<de.dktk.dd.rpb.core.domain.edc.StudySubject> studyZeroStudySubjectList;
    private Study selectedStudy;
    //TODO: this should be the main study subject type used in this bean
    private de.dktk.dd.rpb.core.domain.edc.StudySubject rpbSelectedStudySubject;
    //TODO: this should be the main study event type used in this bean
    private EventData selectedEventData;
    // Subject
    private Subject newSubject;
    private de.dktk.dd.rpb.core.domain.edc.StudySubject selectedStudySubject;
    private Subject selectedSubject;
    private boolean isSure;
    private boolean usePidGenerator;

    private EnumPidGenerationStrategy enumPidGenerationStrategy = EnumPidGenerationStrategy.PID_GENERATOR;

    // StudyEvent
    private Integer originalEventRepeatKey;

    // QueryStrings
    private String studyParam;
    private String studySubjectParam;

    // RPBLite URL
    @Value("${rpb.lite.url}")
    private String rpbLiteUrl;

    // CCP identity manager URL
    @Value("${ccp.idmanager.url}")
    private String ccpIdentityManagerUrl;

    // Graphs
    private LineChartModel siteEnrolmentGraphModel;

    //endregion

    //region Constructors

    @Inject
    public OCStudyBean(MainBean mainBean,
                       StudyIntegrationFacade studyIntegrationFacade,
                       AuditLogService auditLogService,
                       ICtpService svcCtp,
                       LabKeyService labKeyService) {
        this.mainBean = mainBean;
        this.studyIntegrationFacade = studyIntegrationFacade;
        this.auditLogService = auditLogService;
        this.svcCtp = svcCtp;
        this.labKeyService = labKeyService;
    }

    //endregion

    //region Properties

    //region Graph

    public LineChartModel getSiteEnrolmentGraphModel() {
        return this.siteEnrolmentGraphModel;
    }

    //endregion

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

    //region StudySite DataTable Properties

    //region StudyList Property

    public List<Study> getStudyList() {
        return this.studyList;
    }

    public void setStudyList(List<Study> studyList) {
        this.studyList = studyList;
    }

    //endregion

    //region FilteredStudies Property

    public List<Study> getFilteredStudies() {
        return this.filteredStudies;
    }

    public void setFilteredStudies(List<Study> value) {
        this.filteredStudies = value;
    }

    //endregion

    //region SelectedStudy Property

    public Study getSelectedStudy() {
        return this.selectedStudy;
    }

    public void setSelectedStudy(Study selectedStudy) {
        this.selectedStudy = selectedStudy;
    }

    //endregion

    //region PreSortOrder

    public List<SortMeta> getSitesPreSortOrder() {
        return this.sitesPreSortOrder;
    }

    public void setSitesPreSortOrder(List<SortMeta> sortOrder) {
        this.sitesPreSortOrder = sortOrder;
    }

    //endregion

    //endregion

    //region SelectedStudySubject Property

    public de.dktk.dd.rpb.core.domain.edc.StudySubject getRpbSelectedStudySubject() {
        return rpbSelectedStudySubject;
    }

    public void setRpbSelectedStudySubject(de.dktk.dd.rpb.core.domain.edc.StudySubject rpbSelectedStudySubject) {
        this.rpbSelectedStudySubject = rpbSelectedStudySubject;
    }

    //endregion

    //region SelectedEvent

    public EventData getSelectedEventData() {
        return this.selectedEventData;
    }

    public void setSelectedEventData(EventData selectedEventData) {
        this.selectedEventData = selectedEventData;
    }

    public Integer getOriginalEventRepeatKey() {
        return this.originalEventRepeatKey;
    }

    public TabView getNewSubjectTabView() {
        return newSubjectTabView;
    }

    public void setNewSubjectTabView(TabView newSubjectTabView) {
        this.newSubjectTabView = newSubjectTabView;
    }


    //endregion

    //region OriginalEventRepeatKey

    public void setOriginalEventRepeatKey(Integer originalEventRepeatKey) {
        this.originalEventRepeatKey = originalEventRepeatKey;
    }

    //endregion

    //region RadPlanBio Study

    public de.dktk.dd.rpb.core.domain.ctms.Study getRpbStudy() {
        return this.rpbStudy;
    }

    //endregion

    //region StudyParameterConfiguration

    public StudyParameterConfiguration getStudyParameterConfiguration() {
        return this.studyConfiguration;
    }

    //endregion

    //region Subject

    public Subject getSelectedSubject() {
        return this.selectedSubject;
    }

    public void setSelectedSubject(Subject value) {
        this.selectedSubject = value;
    }

    public boolean getIsSure() {
        return this.isSure;
    }

    public void setIsSure(boolean value) {
        this.isSure = value;
    }

    public boolean getUsePidGenerator() {
        return this.usePidGenerator;
    }

    public void setUsePidGenerator(boolean value) {
        this.usePidGenerator = value;
    }

    public Subject getNewSubject() {
        return this.newSubject;
    }

    public void setNewSubject(Subject value) {
        this.newSubject = value;
    }

    public de.dktk.dd.rpb.core.domain.edc.StudySubject getSelectedStudySubject() {
        return selectedStudySubject;
    }

    public void setSelectedStudySubject(de.dktk.dd.rpb.core.domain.edc.StudySubject selectedStudySubject) {
        this.selectedStudySubject = selectedStudySubject;
    }

    public EnumPidGenerationStrategy getEnumPidGenerationStrategy() {
        return enumPidGenerationStrategy;
    }

    public void setEnumPidGenerationStrategy(EnumPidGenerationStrategy enumPidGenerationStrategy) {
        // In the past, usePidGenerator was set within the UI and it is used within the bean. Need to keep it in sync.
        this.usePidGenerator = enumPidGenerationStrategy == EnumPidGenerationStrategy.PID_GENERATOR;
        this.enumPidGenerationStrategy = enumPidGenerationStrategy;
    }

    public boolean getCanDepseudonymisePatient() {
        if (this.selectedStudy != null && this.selectedStudy.isMulticentric()) {
            return this.selectedStudy.getSiteName().contains(this.mainBean.getMyAccount().getPartnerSite().getIdentifier()) &&
                    this.selectedStudy.getSiteName().startsWith(this.mainBean.getMyAccount().getPartnerSite().getIdentifier());
        }

        return true;
    }

    //endregion

    //region GenderValues

    public Map<String, Object> getGenderValues() {
        return genderValues;
    }

    //endregion

    //region StudyParam

    public String getStudyParam() {
        return studyParam;
    }

    public void setStudyParam(String studyParam) {
        this.studyParam = studyParam;
    }

    //endregion

    //region StudySubjectParam

    public String getStudySubjectParam() {
        return studySubjectParam;
    }

    public void setStudySubjectParam(String studySubjectParam) {
        this.studySubjectParam = studySubjectParam;
    }

    //endregion

    //endregion

    //region Init

    /**
     * Initialisation of the bean
     */
    @PostConstruct
    public void init() {
        // Initialize new patient for dialog binding
        Person person = new Person();
        this.newSubject = new Subject();
        this.newSubject.setPerson(person);

        this.isSure = true;
        this.usePidGenerator = true;

        // Study configuration according to metadata
        this.studyConfiguration = new StudyParameterConfiguration();

        try {
            if (this.mainBean.getMyAccount().getPartnerSite() == null) {
                throw new Exception("Your account is not associated with any partner site.");
            } else if (this.mainBean.getMyAccount().getPartnerSite().getEdc() == null) {
                throw new Exception("Your partner site does not have associated OpenClinica EDC");
            }

            // PID generator is not required
            else if (!this.mainBean.getMyAccount().getPartnerSite().hasEnabledPid()) {
                this.usePidGenerator = false;
            }

            if (!this.mainBean.getMyAccount().hasOpenClinicaAccount()) {
                throw new Exception("There is now OpenClinica user account for you.");
            }

            this.setColumnVisibilityList(
                    this.buildColumnVisibilityList()
            );
            this.setSitesPreSortOrder(
                    this.buildSitesSortOrder()
            );
            this.setPreSortOrder(
                    this.buildSortOrder()
            );

            // Init Facade to use service that are valid for logged user PartnerSite (from mainBean)
            this.studyIntegrationFacade.init(this.mainBean);
            this.studyIntegrationFacade.setRetrieveStudySubjectOID(Boolean.FALSE);

            // Load initial data
            this.reloadStudies();
            this.selectedParentStudyChanged();
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * After initialisation, in case of pid query parameter present initialise the search
     */
    public void onLoad() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (this.studyParam != null && this.studySubjectParam != null) {
                if (!"".equals(this.studyParam) && !"".equals(this.studySubjectParam)) {
                    this.selectedStudyType = this.studyTypeLookup(this.studyParam);
                    if (this.selectedStudyType != null) {
                        this.selectedParentStudyChanged();

                        this.tab.setActiveIndex(1);
                    }
                }
            }

        }
    }

    //endregion

    //region Commands

    //region Clear

    /**
     * Set all selected entities to null
     */
    public void clearSelection() {
        this.setSelectedStudy(null);
        this.setSelectedSubject(null);
        this.setSelectedEntity(null);
        this.setEntityList(null);
    }

    //endregion

    //region Study

    /**
     * Reload studies from OpenClinica
     * This fills the StudyType list (this represents the hierarchy study/sites)
     * It also automatically select active study of user
     */
    public void reloadStudies() {
        try {
            // EDC services available
            if (this.mainBean.getOpenClinicaService() != null) {

                // Load open clinica studies
                this.studyTypeList = this.mainBean.getOpenClinicaService().listAllStudies();

                // Automatically select active study/site from OC database
                if (this.mainBean.getActiveStudy() != null) {
                    this.selectedStudyType = this.studyTypeLookup(this.mainBean.getActiveStudy().getUniqueIdentifier());
                }
            }
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

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

    /**
     * Selected parent EDC study changed
     */
    public void selectedParentStudyChanged() {
        this.reloadStudySites();
        this.clearSelection();

        // Auto select site
        if (this.studyList != null && this.studyList.size() > 0) {
            // Mono-centre or multi-centre with non recruiting partner site (no study site)
            this.selectedStudy = this.studyList.get(0);
            // Multi-centre with recruiting site -> search for site according identifier
            if (this.studyList.size() > 1) {
                for (Study s : this.studyList) {
                    if (s.getSiteName().startsWith(this.mainBean.getMyAccount().getPartnerSite().getIdentifier() + RPB_IDENTIFIERSEP)) {
                        this.selectedStudy = s;
                        break;
                    }
                }
            }
        }

        // Load corresponding RPB study entity
        if (this.selectedStudy != null) {
            // Using the parent study always to get valid the studyParameterConfiguration.
            // OpenClinica does not propagate these settings from the parent study to the side specific studies.
            this.rpbStudy = this.mainBean.getStudyIntegrationFacade().loadStudyWithMetadataByIdentifierViaEngineUser(this.selectedStudy.getStudyIdentifier(), this.selectedStudy.getStudyIdentifier());

            // Reload study configuration details
            this.studyConfiguration = this.rpbStudy.getEdcStudy().getMetaDataVersion().getStudyDetails().getStudyParameterConfiguration();

            // Reload study subjects
            this.reloadStudySubjects();
        }
    }

    /**
     * Reload Study Sites depending on selected study name
     */
    public void reloadStudySites() {
        try {
            if (this.selectedStudyType != null) {
                for (StudyType st : this.studyTypeList) {
                    if (st.getName().equals(this.selectedStudyType.getName())) {

                        // Representing collection of Study Sites
                        ArrayList<Study> tempStudyList = new ArrayList<>();

                        // Multi-centric study
                        if (st.getSites() != null) {
                            for (SiteType site : st.getSites().getSite()) {
                                Study studySite = new Study(st);
                                studySite.setSiteName(site.getIdentifier());
                                studySite.setRealSiteName(site.getName());
                                studySite.setSiteOID(site.getOid());
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
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /**
     * Change active study in open clinica for current user
     */
    public void changeActiveStudy() {
        try {
            this.mainBean.changeUserActiveStudy(
                    this.mainBean.getSvcWebApi().loadEdcStudy(
                            this.mainBean.getMyAccount().getApiKey(),
                            this.selectedStudy.getSiteName()
                    )
            );

            this.mainBean.refreshActiveStudy();
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    /***
     * Verifies if the selectedStudy has the study zero identifier
     * @return boolean true if the selected study is study zero
     */
    public boolean selectedStudyIsStudyZero() {

        if (this.selectedStudy.getStudyIdentifier().equals(Constants.study0Identifier)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the IDAT flag of the selected study. Default value is true if the flag is null
     *
     * @return boolean tag value
     */
    public boolean hasIdatFlag() {
        if (this.rpbStudy.getTagValue("IDAT") != null) {
            return Boolean.parseBoolean(this.rpbStudy.getTagValue("IDAT"));
        } else {
            // study without tag == study has IDAT
            return true;
        }
    }

    /**
     * Returns the CCP Flag of the selected study. Default value is false.
     *
     * @return boolean tag value
     */
    public boolean hasCcpFlag() {
        if (this.rpbStudy.getTagValue("CCP") != null) {
            return Boolean.parseBoolean(this.rpbStudy.getTagValue("CCP"));
        } else {
            // CCP Tag
            return false;
        }
    }

    /**
     * Returns the CCP Code of the selected study. Default value is an empty string.
     *
     * @return String CCP code of the selected study
     */
    public String getCcpCode() {
        if (this.rpbStudy.getTagValue("CCP-code") != null) {
            return this.rpbStudy.getTagValue("CCP-code");
        } else {
            // CCP Tag
            return "";
        }
    }

    /**
     * returns true if users are on the portal of their partner side and they are allowed to access study zero
     *
     * @return boolean study zero can be used
     */
    public boolean canUseStudyZeroPatients() {

        if (!portalRunsOnSamePartnerSide()) {
            return false;
        }

        for(StudyType study : this.studyTypeList){

            if(study.getIdentifier().equals(Constants.study0Identifier)){
                return true;
            }

        }

        return false;
    }

    //endregion

    //region StudySubject

    /**
     * Reload view models for study partner site enrollment graphs
     */
    public void reloadSitePatientEnrollmentLineModel() {

        // Model
        this.siteEnrolmentGraphModel = new LineChartModel();
        this.siteEnrolmentGraphModel.setTitle(this.selectedStudy.getSiteName() + " Enrollment Progress");
        this.siteEnrolmentGraphModel.setLegendPosition("ne");
        this.siteEnrolmentGraphModel.setZoom(Boolean.TRUE);

        // Monthly bar series
        BarChartSeries series1 = new BarChartSeries();
        series1.setLabel("monthly");
        series1.setXaxis(AxisType.X);
        series1.setYaxis(AxisType.Y);

        CategoryAxis xAxis = new CategoryAxis("Months");
        xAxis.setTickAngle(-50);
        this.siteEnrolmentGraphModel.getAxes().put(AxisType.X, xAxis);
        this.siteEnrolmentGraphModel.getAxis(AxisType.Y).setLabel("Per Month");
        this.siteEnrolmentGraphModel.getAxis(AxisType.Y).setTickFormat("%d");
        this.siteEnrolmentGraphModel.getAxis(AxisType.Y).setMin(0);

        // Daily line series
        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("total");
        series2.setXaxis(AxisType.X);
        series2.setYaxis(AxisType.Y2);

        Axis y2Axis = new LinearAxis("Sum");
        y2Axis.setTickFormat("%d");
        y2Axis.setMin(0);
        this.siteEnrolmentGraphModel.getAxes().put(AxisType.X, xAxis);
        this.siteEnrolmentGraphModel.getAxes().put(AxisType.Y2, y2Axis);

        try {
            // Only if some patients are enrolled
            if (this.entityList != null) {

                // These will serve as base data structures for graphs models
                HashMap<String, Integer> sumProgress = new HashMap<>();
                HashMap<String, Integer> monthlyProgress = new HashMap<>();

                // Initialise hash maps for
                for (de.dktk.dd.rpb.core.domain.edc.StudySubject ss : this.entityList) {

                    GregorianCalendar c = new GregorianCalendar();
                    c.setTime(ss.getDateEnrollment());
                    XMLGregorianCalendar cXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

                    // Sum enrollment report
                    if (sumProgress.get(cXML.getYear() + "-" + String.format("%02d", cXML.getMonth())) != null) {
                        Integer value = sumProgress.get(cXML.getYear() + "-" +
                                String.format("%02d", cXML.getMonth()));
                        sumProgress.put(cXML.getYear() + "-" +
                                String.format("%02d", cXML.getMonth()), value + 1);
                    } else {
                        sumProgress.put(cXML.getYear() + "-" +
                                String.format("%02d", cXML.getMonth()), 1);
                    }

                    // Monthly enrollment report
                    if (monthlyProgress.get(cXML.getYear() + "-" + String.format("%02d", cXML.getMonth())) != null) {
                        Integer value = monthlyProgress.get(cXML.getYear() + "-" +
                                String.format("%02d", cXML.getMonth()));
                        monthlyProgress.put(cXML.getYear() + "-" +
                                String.format("%02d", cXML.getMonth()), value + 1);
                    } else {
                        monthlyProgress.put(cXML.getYear() + "-" +
                                String.format("%02d", cXML.getMonth()), 1);
                    }
                }

                // Sort monthly by key
                TreeMap<String, Integer> sortedMonthly = new TreeMap<>(monthlyProgress);

                if (!sortedMonthly.isEmpty()) {
                    int countingYear = Integer.parseInt(sortedMonthly.firstKey().substring(0, 4));
                    int countingMonth = Integer.parseInt(sortedMonthly.firstKey().substring(5, 7));
                    for (String key : sortedMonthly.keySet()) {

                        // Current year-month bar
                        int year = Integer.parseInt(key.substring(0, 4));
                        int month = Integer.parseInt(key.substring(5, 7));

                        // If there is a gap in recruitment
                        if (year > countingYear || month < countingMonth) {

                            // Fill the gap for missing years
                            for (int i = countingYear; i < year; i++) {
                                // Fill the gap in recruitment until the end of year with zero bars
                                for (int j = countingMonth; j <= 12; j++) {
                                    series1.set(i + "-" + String.format("%02d", j), 0);
                                }

                                countingMonth = 1;
                            }

                            countingYear = year;

                            // Fill the gap for missing months with zero bars
                            for (int i = countingMonth; i < month; i++) {
                                series1.set(year + "-" + String.format("%02d", i), 0);
                            }

                            countingMonth = month;
                        }
                        // Gab is within a same year
                        else if (year == countingYear) {
                            if (month > countingMonth) {
                                // Fill the gap in recruitment with zero bars
                                for (int i = countingMonth; i < month; i++) {
                                    series1.set(year + "-" + String.format("%02d", i), 0);
                                }

                                countingMonth = month;
                            }
                        }

                        // And always create the current recruitment bar
                        series1.set(key, monthlyProgress.get(key));

                        // Setup what should be the following year-month key
                        if (countingMonth == 12) {
                            countingMonth = 1;
                            countingYear += 1;
                        } else {
                            countingMonth += 1;
                            countingYear = year;
                        }
                    }

                    // Scale for X axis
                    xAxis.setMin(sortedMonthly.firstKey());
                    xAxis.setMax(sortedMonthly.lastKey());
                }

                // Sort sum be key
                TreeMap<String, Integer> sortedSum = new TreeMap<>(sumProgress);

                if (!sortedSum.isEmpty()) {
                    int sum = 0;
                    for (String key : sortedSum.keySet()) {
                        sum += sortedSum.get(key);
                        series2.set(key, sum);
                    }
                }

                this.siteEnrolmentGraphModel.addSeries(series1);
                this.siteEnrolmentGraphModel.addSeries(series2);
            }
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    public void onStudySubjectIdChange() {
        // Only apply this pseudonym generation strategy when PID generator is not used and PID is required
        if (!this.usePidGenerator &&
                this.studyConfiguration.getPersonIdRequired().equals(EnumRequired.REQUIRED)) {

            if (this.newSubject != null) {
                this.newSubject.generateUniqueIdentifier(
                        this.rpbStudy,
                        this.mainBean.getMyAccount().getPartnerSite()
                );
            }
        }
    }

    /**
     * Listener verifies that the secondary identifier has the CCP identifier-prefix
     * if the CCP flag is true which indicates that the secondary identifier is used for the CCP identifier
     */
    public void onCcpIdentifierChange() {
        // skip adding a prefix for the CCP for now
//        if (this.hasCcpFlag() && this.newSubject.getSecondaryId() != null) {
//            // Id is empty
//            if (this.newSubject.getSecondaryId().isEmpty()) {
//                return;
//            }
//
//            String updatedSecondaryId = this.getCcpIdentifier(this.newSubject.getSecondaryId());
//            this.newSubject.setSecondaryId(updatedSecondaryId);
//        }
    }

    /**
     * Reload study subject registered for selected study/site from OC using web service
     */
    public void reloadStudySubjects() {
        try {
            // Study was selected
            if (this.selectedStudy != null) {

                // Setup EDC study as query parameter
                de.dktk.dd.rpb.core.domain.edc.Study edcStudy = this.createEdcStudyQuery();
                this.entityList = this.studyIntegrationFacade.loadStudySubjects(edcStudy);
            }

            // Enrollment graph
            this.reloadSitePatientEnrollmentLineModel();

        } catch (Exception err) {

            this.messageUtil.error(err);
        }
    }

    /**
     * Reset new study subject attributes (also with generated PID withing Subject entity)
     */
    public void resetNewStudySubject() {
        if (this.newSubject != null) {
            this.newSubject.initDefaultValues();
            Person dummyPerson = new Person();
            this.newSubject.setPerson(dummyPerson);
        }

        // Reset the autocomplete component
        this.selectedStudySubject = null;
    }

    /**
     * OnClose newSubjectDialog
     */
    public void onCloseNewSubjectDialog() {
        resetNewStudySubject();
        this.selectedStudySubject = null;
    }

    public void loadSelectedSubjectDetails() {

        // Only load details if new subject is selected
        if (this.rpbSelectedStudySubject == null ||
                this.selectedEntity != null && !this.rpbSelectedStudySubject.getStudySubjectId().equals(this.selectedEntity.getStudySubjectId())) {

            // Reset
            this.rpbSelectedStudySubject = null;

            // Clear loaded collections
            this.resetSubjectEvents();

            try {
                // Setup EDC study as query parameter - because active study may be different
                de.dktk.dd.rpb.core.domain.edc.Study edcStudy = this.createEdcStudyQuery();
                this.rpbSelectedStudySubject = this.studyIntegrationFacade.loadStudySubjectWithEvents(
                        edcStudy,
                        this.selectedEntity.getStudySubjectId()
                );
            } catch (Exception err) {
                this.messageUtil.error(err);
            }
        }
    }

    /**
     * Selected study site changed
     */
    public void selectedStudySiteChanged() {
        this.reloadStudySubjects();
    }

    public boolean canEnrollIntoSelectedTreatmentArm() {
        // Check whether enrollment is possible (enrollment rules)
        if (this.rpbStudy != null && this.rpbStudy.getIsEnrollmentArmAssignmentRequired() != null &&
                this.rpbStudy.getIsEnrollmentArmAssignmentRequired()) {
            if (this.getNewSubject().getArm() == null) {
                return false;
            } else if (this.getNewSubject().getArm().getIsEnabled() == null ||
                    !this.getNewSubject().getArm().getIsEnabled()) {
                FacesContext.getCurrentInstance().validationFailed();
                this.messageUtil.warningText("Subject assignment to selected treatment arm is disabled.");
                return false;
            }
        }

        return true;
    }

    /**
     * Enrolls a new subject to EDC
     *
     * @throws IOException
     * @throws MissingPropertyException
     */
    public void handleNewSubjectDialogSubmit() throws IOException, MissingPropertyException {

        // TODO: Bridge head approach is on hold
//        if (this.enumPidGenerationStrategy == EnumPidGenerationStrategy.BRIDGEHEAD_PID_GENERATOR) {
//            this.createStudySubjectWithCcpRedirect();
//        } else {
//            System.out.println(this.newSubject.getSecondaryId());
//
//        }
//        if (this.hasCcpFlag() && this.newSubject.getSecondaryId() != null && !this.newSubject.getSecondaryId().isEmpty()) {
//            String updatedSecondaryId = this.getCcpIdentifier(this.newSubject.getSecondaryId());
//            this.newSubject.setSecondaryId(updatedSecondaryId);
//        }

        this.createNewStudySubject();
    }

    /**
     * Updates the subject data in the EDC system
     */
    public void handleSubjectUpdate() throws DataBaseItemNotFoundException {
        int updatedRows = 0;

        if (this.rpbSelectedStudySubject.getSecondaryId() != null && !this.rpbSelectedStudySubject.getSecondaryId().isEmpty()) {
            // skip adding a CCP prefix
//            String updatedSecondaryId = this.getCcpIdentifier(this.rpbSelectedStudySubject.getSecondaryId());
//            this.rpbSelectedStudySubject.setSecondaryId(updatedSecondaryId);

            String ocUserName = this.mainBean.getMyAccount().getOcUsername();
            updatedRows = this.mainBean.getOpenClinicaDataRepository().setSecondaryIdOnExistingStudySubject(
                    this.rpbSelectedStudySubject, ocUserName);
        }

        if (updatedRows == 0) {
            this.messageUtil.warning("No item updated!");
        } else {
            this.messageUtil.info("StudySubject updated!");
        }
        // reload data for the UI
        this.reloadStudySubjects();
    }

//    public void handleNewCcPPseudonymSubmit() {
//        if (this.newSubject.getSecondaryId() != null) {
//            String updatedSecondaryId = this.getCcpIdentifier(this.newSubject.getSecondaryId());
//            this.newSubject.setSecondaryId(updatedSecondaryId);
//        }
//
//        // default setting - back to the first tab
//        this.newSubjectTabView.setActiveIndex(0);
//        RequestContext.getCurrentInstance().update("newSubjectFormTabView");
//    }

    /**
     * Adds the prefix to the CCP identifier if it is not already part of the identifier
     *
     * @param identifier
     * @return String identifier with prefix
     */
    public String getCcpIdentifier(String identifier) {
        String edcCode;
        if (this.selectedStudy.isMulticentric()) {
            edcCode = PatientIdentifierUtil.getPatientIdPrefix(this.selectedStudy.getSiteName());
        } else {
            edcCode = this.mainBean.getMyAccount().getPartnerSite().getIdentifier();
        }

        String prefix = edcCode + Constants.CCP_PREFIX_PART;
        String candidatePrefix = PatientIdentifierUtil.getPatientIdPrefix(identifier);
        if (prefix.equals(candidatePrefix)) {
            return identifier;
        } else {
            return prefix + Constants.RPB_IDENTIFIERSEP + identifier;
        }
    }

    /**
     * Returns a list of subjects that match the query string and an empty array if the String does not match.
     * Thereby the Pid and the StudySubject identifier are compared.
     *
     * @param studySubjectIdQuery String query
     * @return List of Subjects
     */
    public List<de.dktk.dd.rpb.core.domain.edc.StudySubject> filterMatchingStudyZeroSubjects(String studySubjectIdQuery) {
        List<de.dktk.dd.rpb.core.domain.edc.StudySubject> resultList = new ArrayList<>();

        if (this.studyZeroStudySubjectList == null) {
            this.studyZeroStudySubjectList = this.mainBean.getOpenClinicaDataRepository()
                    .findStudySubjectsByStudy(Constants.study0Identifier);
        }


        for (de.dktk.dd.rpb.core.domain.edc.StudySubject subject : this.studyZeroStudySubjectList) {
            if (subject.getStudySubjectId() != null && subject.getPid() != null) {
                String studySubjectId = subject.getStudySubjectId();
                String pid = subject.getPid();
                if (studySubjectId.contains(studySubjectIdQuery)) {
                    resultList.add(subject);
                } else if (pid.contains(studySubjectIdQuery)) {
                    resultList.add(subject);
                }
            }
        }
        return resultList;
    }

    /**
     * List of StudySubjects if a study zero exists for the site
     *
     * @return
     */
    public List<de.dktk.dd.rpb.core.domain.edc.StudySubject> getStudyZeroStudySubjectLists() {
        return this.studyZeroStudySubjectList;
    }

    /**
     * Re-identification of the patient based on the Pid
     */
    public void identifySelectedPatient() {
        String uniqueIdentifier = this.selectedStudySubject.getPid();
        if (uniqueIdentifier.isEmpty()) {
            this.messageUtil.warning("Pid is empty");
            return;
        }

        this.newSubject.setUniqueIdentifier(uniqueIdentifier);
        Person person = getIdentity(uniqueIdentifier);

        if (person != null) {
            this.newSubject.setPerson(person);
            this.newSubject.setGender(this.selectedStudySubject.getSex());
        }
    }

    /**
     * Create a new study subject in OC using web service
     */
    public void createNewStudySubject() {
        try {
            // Check whether enrollment is possible (enrollment rules)
            if (this.rpbStudy != null && this.rpbStudy.getIsEnrollmentArmAssignmentRequired() != null &&
                    this.rpbStudy.getIsEnrollmentArmAssignmentRequired()) {

                if (this.getNewSubject().getArm() == null) {
                    throw new Exception("Enrollment arm assignment is required!");
                } else {
                    if (!this.getNewSubject().getArm().getIsEnabled()) {
                        throw new Exception("Subject assignment to selected treatment arm is disabled.");
                    }
                }
            }

            // skip adding prefixes to CCP for now
//            if (this.newSubject.getSecondaryId() != null && !this.newSubject.getSecondaryId().isEmpty()) {
//                String updatedSecondaryId = this.getCcpIdentifier(this.newSubject.getSecondaryId());
//                this.newSubject.setSecondaryId(updatedSecondaryId);
//            }

            // Prepare SOAP StudySubject from Subject data
            StudySubject studySubject = new StudySubject();

            // Whether StudySubjectID is provided depends on study configuration
            if (this.studyConfiguration.getStudySubjectIdGeneration() != null) {

                // Auto
                if (this.studyConfiguration.getStudySubjectIdGeneration().equals(EnumStudySubjectIdGeneration.AUTO)) {
                    studySubject.setStudySubjectLabel(""); // Event if auto I have to provide at least empty string
                }
                // Manual
                else if (this.studyConfiguration.getStudySubjectIdGeneration().equals(EnumStudySubjectIdGeneration.MANUAL)) {

                    // Manually entered StudySubjectID should never be number, because it hinders the automatic StudySubjectID generation
                    if (NumberUtils.isNumber(this.getNewSubject().getStudySubjectId())) {
                        throw new Exception("Number is not allowed for manually entered StudySubjectID!");
                    } else {
                        studySubject.setStudySubjectLabel(
                                this.getNewSubject().getStudySubjectId()
                        );
                    }
                }
            }
            // Could not read this information from parameters, use empty string because something is required
            else {
                studySubject.setStudySubjectLabel("");
            }

            // When PID is required
            if (this.studyConfiguration.getPersonIdRequired().equals(EnumRequired.REQUIRED)) {

                boolean autoGeneratedStudySubjectId = this.studyConfiguration.getStudySubjectIdGeneration().equals(EnumStudySubjectIdGeneration.AUTO_NONEDITABLE);
                if (this.newSubject.getUniqueIdentifier() != null && autoGeneratedStudySubjectId) {
                    this.newSubject.setUniqueIdentifier("tmp-" + UUID.randomUUID().toString());
                }

                // Determine whether subject has PID assigned
                if (this.getNewSubject().getUniqueIdentifier() == null ||
                        this.getNewSubject().getUniqueIdentifier().equals("")) {

                    throw new Exception("Patient PID (pseudonym) should not be empty!");
                }

                // Use partner site prefix and pure PID to create cross site unique Person ID
                if (this.usePidGenerator && !autoGeneratedStudySubjectId) {
                    studySubject.setPersonID(
                            this.mainBean.constructMySubjectFullPid(
                                    this.getNewSubject().getUniqueIdentifier()
                            )
                    );
                }
                // Use manually assigned PID
                else {
                    studySubject.setPersonID(
                            this.getNewSubject().getUniqueIdentifier()
                    );
                }
            } else if (this.studyConfiguration.getPersonIdRequired().equals(EnumRequired.OPTIONAL)) {

                // Determine whether subject has PID assigned
                if (this.getNewSubject().getUniqueIdentifier() != null &&
                        !this.getNewSubject().getUniqueIdentifier().equals("")) {

                    // Use partner site prefix and pure PID to create cross site unique Person ID
                    if (this.usePidGenerator) {
                        studySubject.setPersonID(
                                this.mainBean.constructMySubjectFullPid(
                                        this.getNewSubject().getUniqueIdentifier()
                                )
                        );
                    }
                    // Use manually assigned PID
                    else {
                        studySubject.setPersonID(
                                this.getNewSubject().getUniqueIdentifier()
                        );
                    }
                }
            }

            // If secondary ID is provided use it
            if (this.getNewSubject().getSecondaryId() != null && !this.getNewSubject().getSecondaryId().equals("")) {
                studySubject.setStudySubjectSecondaryLabel(this.getNewSubject().getSecondaryId());
            }

            if (this.selectedStudy != null) {
                studySubject.setStudy(this.selectedStudy);
            } else {
                throw new Exception("Study site has to be selected!");
            }

            // Gender is always required (there is a bug in OC web service)
            studySubject.setSex(this.getNewSubject().getGender());

            // Calendar
            GregorianCalendar c = new GregorianCalendar();

            // Whether day of birth will be collected for subject depend on study configuration
            if (this.studyConfiguration.getCollectSubjectDob().equals(EnumCollectSubjectDob.YES)) {
                c.setTime(this.getNewSubject().getPerson().getBirthdate());
                XMLGregorianCalendar birthDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                studySubject.setDateOfBirth(birthDateXML);
            } else if (this.studyConfiguration.getCollectSubjectDob().equals(EnumCollectSubjectDob.ONLY_YEAR)) {
                c.setTime(this.getNewSubject().getPerson().getBirthdate());
                XMLGregorianCalendar birthDateXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                studySubject.setYearOfBirth(new BigInteger(String.valueOf(birthDateXML.getYear())));
            }

            // Always collect enrollment date for subject
            studySubject.setDateOfRegistration(this.getNewSubject().getEnrollmentDate());

            // Enroll subject into EDC study
            String ssid = this.mainBean.getOpenClinicaService().createNewStudySubject(studySubject);

            if (ssid != null) {
                studySubject.setStudySubjectLabel(ssid);

                if (portalRunsOnSamePartnerSide()) {
                    this.updateCtpLookupTable(studySubject, ssid);
                } else {
                    log.debug("Most likely, the portal does not run within the same side as the study. " +
                            "Since we invoke a service that is bound to the specific location, " +
                            "please trigger an update of the overall list on the portal that is located on this side." +
                            " As an admin you can find this functionality under /admin/ctms/edcStudyCRUD.faces");
                }

                boolean studySubjectIdIsAutoNonEditable = this.studyConfiguration.getStudySubjectIdGeneration().equals(EnumStudySubjectIdGeneration.AUTO_NONEDITABLE);
                boolean pidUsed = !this.studyConfiguration.getPersonIdRequired().equals(EnumRequired.NOT_USED);
                if (studySubjectIdIsAutoNonEditable && pidUsed) {

                    this.reloadStudySubjects();
                    de.dktk.dd.rpb.core.domain.edc.StudySubject edcSubject = getStudySubjectFromEntityList(ssid);

                    this.newSubject.setStudySubjectId(ssid);

                    PartnerSite rpbPartnerSite = new PartnerSite();
                    rpbPartnerSite.setIdentifier(this.getSideIdentifierPrefix());
                    this.newSubject.generateUniqueIdentifier(this.rpbStudy, rpbPartnerSite);

                    String updatedPid = this.newSubject.getUniqueIdentifier();
                    studySubject.setPersonID(updatedPid);


                    try {
                        updatePidOnExistingEdcSubject(edcSubject, updatedPid);
                    } catch (DataBaseItemNotFoundException e) {
                        this.messageUtil.error("Updating the Pid for the new created subject in the EDC system failed.", e);
                    }

                }

                String studyIdentifier = this.selectedStudy.getStudyIdentifier();
                this.auditLogService.event(
                        AuditEvent.EDCStudySubjectEnrollment,
                        studySubject.getPersonID(), // PID
                        studyIdentifier, // Study
                        ssid // SSID
                );
                this.messageUtil.infoText("Study Subject: " + ssid + " successfully created.");
                this.reloadStudySubjects();
            } else {
                throw new Exception("EDC web-service could not create a new study subject.");
            }

            // Prepare data structure for the new study subject object binding
            Person person = new Person();
            this.newSubject = new Subject();
            this.newSubject.setPerson(person);
            this.isSure = true;
        } catch (Exception err) {
            FacesContext.getCurrentInstance().validationFailed();
            this.messageUtil.error(err);
        }
    }

    private de.dktk.dd.rpb.core.domain.edc.StudySubject getStudySubjectFromEntityList(String ssid) {
        de.dktk.dd.rpb.core.domain.edc.StudySubject edcSubject = null;
        for (de.dktk.dd.rpb.core.domain.edc.StudySubject updatedSubject : this.entityList) {
            if (updatedSubject.getStudySubjectId().equals(ssid)) {
                edcSubject = updatedSubject;
            }
        }
        return edcSubject;
    }

    private void updatePidOnExistingEdcSubject(de.dktk.dd.rpb.core.domain.edc.StudySubject edcSubject, String updatedPid) throws DataBaseItemNotFoundException {
        String ocUserName = this.mainBean.getMyAccount().getOcUsername();
        if (edcSubject != null) {
            edcSubject.setPid(updatedPid);
        }
        this.mainBean.getOpenClinicaDataRepository().setPidOnExistingStudySubject(edcSubject, ocUserName);
    }

    private boolean portalRunsOnSamePartnerSide() {
        String partnerSideIdentifierPortal = this.messageUtil.getResourcesUtil().getProperty("partner_site_identifier");

        if (partnerSideIdentifierPortal == null || partnerSideIdentifierPortal.isEmpty()) {
            log.debug("The partner side identifier should never be null or empty.");
            return false;
        }

        String studySideIdentifierPrefix = getSideIdentifierPrefix();

        return partnerSideIdentifierPortal.equals(studySideIdentifierPrefix);
    }

    private String getSideIdentifierPrefix() {
        String studySideIdentifierPrefix;

        if (this.selectedStudy.isMulticentric()) {
            //multi centric study
            String siteIdentifier = this.selectedStudy.getSiteName();
            studySideIdentifierPrefix = PatientIdentifierUtil.getPatientIdPrefix(siteIdentifier);
        } else {
            //mono centric study
            studySideIdentifierPrefix = this.rpbStudy.getPartnerSite().getIdentifier();
        }
        return studySideIdentifierPrefix;
    }

    private void updateCtpLookupTable(StudySubject studySubject, String ssid) {
        try {
            String edcCode = this.rpbStudy.getTagValue("EDC-code");

            de.dktk.dd.rpb.core.domain.edc.StudySubject edcSubject = new de.dktk.dd.rpb.core.domain.edc.StudySubject();
            edcSubject.setPid(studySubject.getPersonID());
            edcSubject.setStudySubjectId(ssid);

            this.svcCtp.updateSubjectLookupEntry(edcCode, edcSubject);
        } catch (Exception e) {
            log.error("Problem updating CTP entry " + "ssid: " + ssid, e);
        }
    }

    //TODO: we can remove it if the CCP does not support this approach
    public void createStudySubjectWithCcpRedirect() throws IOException, MissingPropertyException {
        String callbackParameters = createCallbackParameterStringForNewPatientRedirect();

        if (this.rpbLiteUrl == null || this.rpbLiteUrl.isEmpty()) {
            throw new MissingPropertyException("rpb.lite.url parameter is missing or empty. " +
                    "Please provide a valid URL with the format \"https://host:port\"");
        }

        UriComponents uriComponents = getUriComponentsForNewPatientCcpRedirect(callbackParameters);

        if (uriComponents.getScheme().equalsIgnoreCase("http")) {
            messageUtil.warning("You are using http for your request. " +
                    "Please consider using https for better security and privacy.");
        }

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(uriComponents.toUriString());
    }

    public void redirectToCcpIdentityManager() throws IOException {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString("http://carusnet.med.tu-dresden.de")
                .build()
                .encode();

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(uriComponents.toUriString());
    }

    //endregion

    //region Patient

    private UriComponents getUriComponentsForNewPatientCcpRedirect(String callbackParameters) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        return UriComponentsBuilder
                .fromUriString(this.rpbLiteUrl)
                .path("/patient/new")
                .queryParam("study", this.selectedStudy.getStudyIdentifier())
                .queryParam("site", this.selectedStudy.getSiteName())
                .queryParam("subject_id", this.newSubject.getStudySubjectId())
                .queryParam("second_subject_id", this.newSubject.getSecondaryId())
                .queryParam("enrollment_date", this.newSubject.getEnrollmentDateString())
                .queryParam("enrollment_arm", (this.newSubject.getArm() != null) ? this.newSubject.getArm().getId() : "")
                .queryParam("birth_date", newSubject.getPerson().getBirthdateString())
                .queryParam("gender", this.newSubject.getGender())
                .queryParam("callback_parameters", callbackParameters)
                .build()
                .encode();
    }

    private String createCallbackParameterStringForNewPatientRedirect() {
        String callbackParameters = "";

        if (this.studyConfiguration.getSexRequired()) {
            callbackParameters = ",gender";
        }

        EnumCollectSubjectDob collectBirthdate = this.studyConfiguration.getCollectSubjectDob();
        if (collectBirthdate == EnumCollectSubjectDob.YES) {
            callbackParameters += ",birth_date";
        }
        if (collectBirthdate == EnumCollectSubjectDob.ONLY_YEAR) {
            callbackParameters += ",birth_year";
        }

        if (!callbackParameters.isEmpty()) {
            // remove leading comma
            callbackParameters = callbackParameters.substring(1);
        }
        return callbackParameters;
    }

    /**
     * Use patient personal information to generate pseudonym PID
     */
    public void generateNewPid() {
        try {
            JSONObject finalResult = this.mainBean.getSvcPid().newSession();

            String sessionId = "";
            if (finalResult != null) {
                sessionId = finalResult.getString("sessionId");
            }

            finalResult = this.mainBean.getSvcPid().newPatientToken(sessionId);

            String tokenId = "";
            if (finalResult != null) {
                tokenId = finalResult.getString("tokenId");
            }

            Person person = this.newSubject.getPerson();

            if (this.newSubject.getPerson().getIsSure()) {
                finalResult = this.mainBean.getSvcPid().createSurePatientJson(tokenId, person);
            } else {
                finalResult = this.mainBean.getSvcPid().getCreatePatientJsonResponse(tokenId, person);
            }

            String newId;
            Boolean tentative;

            if (finalResult != null) {
                newId = finalResult.optString("newId");
                if (!newId.isEmpty()) {
                    tentative = finalResult.getBoolean("tentative");

                    this.auditLogService.event(AuditEvent.PIDCreation, newId);

                    this.messageUtil.infoText("PID generated value: " + newId + " tentative: " + tentative);
                }

                this.newSubject.setUniqueIdentifier(newId);
            }

            // Delete session if it exists (however session cleanup can be also automaticaly done by Mainzelliste)
            this.mainBean.getSvcPid().deleteSession(sessionId);
        } catch (Exception err) {
            this.messageUtil.error(err);

            // Unsure patient
            if (err.getMessage().contains("Failed : HTTP error code: 409")) {
                this.auditLogService.event(AuditEvent.PIDUnsure, this.newSubject.getPerson().toString());

                this.newSubject.getPerson().setIsSure(Boolean.FALSE);
                this.setIsSure(Boolean.FALSE);
            }
        }
    }

    /**
     * Take selected subject PID and get back patient personal information
     * Depseudonymization
     */
    public void getPersonalInformation() {
        try {
            // Pure PID (without partner site identifier)
            String pid = PatientIdentifierUtil.removePatientIdPrefix(
                    this.selectedEntity.getPid()
            );

            // Fetch IDAT
            Person patient = this.mainBean.getSvcPid().loadPatient(pid);
            this.getSelectedSubject().setPerson(patient);

            this.auditLogService.event(AuditEvent.PIDDepseudonymisation, pid);
            this.messageUtil.infoText("Patient identity data for " + pid + " loaded.");
        } catch (Exception err) {
            this.messageUtil.error(err);
        }
    }

    private Person getIdentity(String uniqueIdentifier) {
        String pid = PatientIdentifierUtil.removePatientIdPrefix(uniqueIdentifier);
        // Fetch IDAT
        Person patient = null;
        try {
            patient = this.mainBean.getSvcPid().loadPatient(pid);
            this.auditLogService.event(AuditEvent.PIDDepseudonymisation, pid);
            this.messageUtil.infoText("Patient identity data for " + pid + " loaded.");
        } catch (Exception e) {
            this.messageUtil.error(e);
        }
        return patient;

    }


    //endregion

    //region Study Event

    public void resetSubjectEvents() {
        this.selectedEventData = null;
    }

    public void updateStudyEvent() {
        try {
            // Update OC database
            boolean modified = this.mainBean.getOpenClinicaDataRepository().swapEventDataOrder(
                    this.rpbSelectedStudySubject,
                    this.selectedEventData,
                    this.originalEventRepeatKey
            );

            // When successful create an audit log entry
            if (modified) {
                this.auditLogService.event(
                        AuditEvent.EDCDataModification,
                        "StudyEventData",
                        this.selectedStudyType.getOid() + "/" + this.rpbSelectedStudySubject.getSubjectKey() + "/" + this.selectedEventData.getEventDefinition().getOid(),
                        "studyEventRepeatKey changed from [" + this.originalEventRepeatKey + "] to [" + this.selectedEventData.getStudyEventRepeatKey() + "]"
                );

                this.messageUtil.infoText("Study Event Data: successfully saved.");

                // Reload
                this.rpbSelectedStudySubject = null;
                this.loadSelectedSubjectDetails();
                this.selectedEventData = null;
                this.originalEventRepeatKey = null;
            }
        } catch (Exception err) {
            FacesContext.getCurrentInstance().validationFailed();
            this.messageUtil.error(err);
        }
    }

    public void selectedEventChanged(EventData eventData) {
        // Reset
        if (eventData != null) {
            this.originalEventRepeatKey = eventData.getStudyEventRepeatKeyInteger();
        }
    }

    public void assignNewStudyEventRepeatKey(int newStudyEventRepeatKey) {
        if (this.selectedEventData != null) {
            this.selectedEventData.setStudyEventRepeatKey(String.valueOf(newStudyEventRepeatKey));
        }
    }

    // region ccp

    public String getCcpUrl() {
        String projectId = this.getCcpCode();
        return this.ccpIdentityManagerUrl + "?projectId=" + projectId;
    }

    // endregion

    //endregion

    //endregion

    //region Overrides

    /**
     * Prepare new transient entity object for UI binding
     */
    @Override
    public void prepareNewEntity() {
        // NOOP
    }

    /**
     * Get StudySubjectRepository
     *
     * @return StudySubjectRepository
     */
    @Override
    public IStudySubjectRepository getRepository() {
        return this.repository;
    }

    @Override
    public void setSelectedEntity(de.dktk.dd.rpb.core.domain.edc.StudySubject selectedStudySubject) {

        if (selectedStudySubject == null || !selectedStudySubject.equals(this.selectedEntity)) {
            this.selectedEntity = selectedStudySubject;

            // Prepare new Subject object for UI data binding
            if (this.selectedEntity != null) {
                Subject s = new Subject();

                s.setUniqueIdentifier(this.selectedEntity.getPid());
                s.setGender(this.selectedEntity.getSex());
                this.selectedSubject = s;

                this.loadSelectedSubjectDetails();
            }
        }
    }

    /**
     * Need to build an initial sort order for data table multi sort
     *
     * @return list of sort meta elements for data table sorting
     */
    protected List<SortMeta> buildSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtStudySubjects:colStudySubjectId", "colStudySubjectId", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Need to build an initial sort order for data table multi sort
     *
     * @return list of sort meta elements for data table sorting
     */
    private List<SortMeta> buildSitesSortOrder() {
        List<SortMeta> results = DataTableUtil.buildSortOrder(":form:tabView:dtStudies:colSiteIdentifier", "colSiteIdentifier", SortOrder.ASCENDING);
        if (results != null) {
            return results;
        }

        return new ArrayList<>();
    }

    /**
     * Create column visibility list
     *
     * @return List of Boolean values determining column visibility
     */
    private List<Boolean> buildColumnVisibilityList() {
        List<Boolean> results = new ArrayList<>();

        results.add(Boolean.TRUE); // StudySubjectID
        results.add(Boolean.TRUE); // PID
        results.add(Boolean.FALSE); // Secondary ID
        results.add(Boolean.TRUE); // Gender
        results.add(Boolean.FALSE); // Date of Birth
        results.add(Boolean.TRUE); // Enrollment date

        return results;
    }

    //endregion

    //region Private methods

    private de.dktk.dd.rpb.core.domain.edc.Study createEdcStudyQuery() {
        // Setup EDC study as query parameter
        de.dktk.dd.rpb.core.domain.edc.Study edcStudy = new de.dktk.dd.rpb.core.domain.edc.Study();
        if (this.selectedStudy != null) {
            if (this.selectedStudy.isMulticentric()) {
                edcStudy.setUniqueIdentifier(this.selectedStudy.getSiteName());
            } else {
                edcStudy.setUniqueIdentifier(this.selectedStudy.getStudyIdentifier());
            }
        }

        return edcStudy;
    }

    //endregion

}