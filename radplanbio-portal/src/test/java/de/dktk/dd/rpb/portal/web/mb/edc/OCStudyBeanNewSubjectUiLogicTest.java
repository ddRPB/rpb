package de.dktk.dd.rpb.portal.web.mb.edc;

import de.dktk.dd.rpb.core.domain.admin.DefaultAccount;
import de.dktk.dd.rpb.core.domain.ctms.PartnerSite;
import de.dktk.dd.rpb.core.domain.ctms.Person;
import de.dktk.dd.rpb.core.domain.ctms.StudyTag;
import de.dktk.dd.rpb.core.domain.ctms.StudyTagType;
import de.dktk.dd.rpb.core.domain.edc.Edc;
import de.dktk.dd.rpb.core.domain.edc.Subject;
import de.dktk.dd.rpb.core.ocsoap.types.Study;
import de.dktk.dd.rpb.core.service.AuditLogService;
import de.dktk.dd.rpb.core.service.CtpService;
import de.dktk.dd.rpb.core.service.LabKeyService;
import de.dktk.dd.rpb.core.util.Constants;
import de.dktk.dd.rpb.core.util.ResourcesUtil;
import de.dktk.dd.rpb.portal.facade.StudyIntegrationFacade;
import de.dktk.dd.rpb.portal.web.mb.MainBean;
import de.dktk.dd.rpb.portal.web.util.DataTableUtil;
import de.dktk.dd.rpb.portal.web.util.MessageUtil;
import de.dktk.dd.rpb.portal.web.util.UserContextUtil;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openclinica.ws.beans.StudyType;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        OCStudyBean.class,
        Logger.class,
        MainBean.class,
        StudyIntegrationFacade.class,
        AuditLogService.class,
        CtpService.class,
        LabKeyService.class,
        DataTableUtil.class,
        org.slf4j.Logger.class,
        LoggerFactory.class
})

public class OCStudyBeanNewSubjectUiLogicTest {
    OCStudyBean bean;

    @Mock
    MainBean mainBean;

    StudyIntegrationFacade studyIntegrationFacade;

    AuditLogService auditLogService;

    CtpService ctpService;

    @Mock
    LabKeyService labKeyService;

    ResourcesUtil resourcesUtil;
    PartnerSite partnerSite;
    List<StudyTag> studyTagList;
    UserContextUtil userContext;

    @Before
    public void setUp() throws Exception {
        mockStatic(org.slf4j.Logger.class);
        mockStatic(LoggerFactory.class);
        org.slf4j.Logger logger = mock(org.slf4j.Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        studyIntegrationFacade = mock(StudyIntegrationFacade.class);
        auditLogService = mock(AuditLogService.class);
        ctpService = mock(CtpService.class);

        prepareBasicBean();
    }

    private void prepareBasicBean() {
        // user account specific settings
        DefaultAccount account = new DefaultAccount();
        account.setOcUsername("dummyUser"); // solves hasOpenClinicaAccount

        partnerSite = mock(PartnerSite.class);
        Edc edc = new Edc(1);
        when(partnerSite.getEdc()).thenReturn(edc);
        when(partnerSite.hasEnabledPid()).thenReturn(true);
        when(partnerSite.getIdentifier()).thenReturn("DD");

        account.setPartnerSite(partnerSite);
        when(mainBean.getMyAccount()).thenReturn(account);

        // MessageUtil used to locate the portal site
        MessageUtil messageUtil = mock(MessageUtil.class);
        resourcesUtil = mock(ResourcesUtil.class);
        when(messageUtil.getResourcesUtil()).thenReturn(resourcesUtil);

        // mock build sort order to be able to run bean.init()
        mockStatic(DataTableUtil.class);
        when(DataTableUtil.buildSortOrder(any(String.class), any(String.class), any(
                org.primefaces.model.SortOrder.class))).thenReturn(new ArrayList<>());

        // UserContextUtil userContext
        userContext = mock(UserContextUtil.class);
        when(userContext.hasRole("ROLE_PID_NEW")).thenReturn(true);

        bean = new OCStudyBean(mainBean, studyIntegrationFacade, auditLogService, ctpService, labKeyService);
        ReflectionTestUtils.setField(bean, "messageUtil", messageUtil);
        ReflectionTestUtils.setField(bean, "userContext", userContext);
        bean.init();

        // StudiesTypeList is loaded from OpenClinica to list studies where the specific user has access
        List<Study> studiesTypeList = new ArrayList<>();
        bean.setStudyList(studiesTypeList);

        de.dktk.dd.rpb.core.domain.ctms.Study ctmsStudy = new de.dktk.dd.rpb.core.domain.ctms.Study();
        studyTagList = new ArrayList<>();
        ctmsStudy.setTags(studyTagList);

        ReflectionTestUtils.setField(bean, "rpbStudy", ctmsStudy);

        // set an empty study as selected stud
        Study ocsoapStudy = new Study();
        bean.setSelectedStudy(ocsoapStudy);

        // subject will be checked in the code and can't be null
        Person person = new Person();
        Subject subject = new Subject();
        subject.setPerson(person);
        bean.setNewSubject(subject);
    }

    private void setPortalSiteIdentifier(String portalSiteIdentifier) {
        when(resourcesUtil.getProperty("partner_site_identifier")).thenReturn(portalSiteIdentifier);
    }

    // region selectedStudyIsStudyZero

    @Test
    public void selectedStudyIsStudyZero_true_if_study_zero_is_selected() {
        bean.getSelectedStudy().setStudyIdentifier(Constants.study0Identifier);
        assertTrue(bean.selectedStudyIsStudyZero());
    }

    @Test
    public void selectedStudyIsStudyZero_false_if_study_zero_is_not_selected() {
        bean.getSelectedStudy().setStudyIdentifier("dummyIdentifier");
        assertFalse(bean.selectedStudyIsStudyZero());
    }

    // endregion

    // region showSubjectFromStudyZeroEnrollmentDialog

    @Test
    public void showSubjectFromStudyZeroEnrollmentDialog() {
        when(userContext.hasRole("ROLE_REIDENTIFY")).thenReturn(true);
        // Portal runs in DD
        prepareMulticentricStudy("DD", "DD-");

        // selected Study is not study zero
        bean.getSelectedStudy().setStudyIdentifier("not-study-zero");

        // has access to study zero
        StudyType studyZeroStudyType = new StudyType();
        studyZeroStudyType.setIdentifier(Constants.study0Identifier);
        List<StudyType> studyTypeList = new ArrayList<>();
        studyTypeList.add(studyZeroStudyType);
        bean.setStudyTypeList(studyTypeList);

        assertTrue(bean.showSubjectFromStudyZeroEnrollmentDialog());

        // set Pid generator to false
        bean.setUsePidGenerator(false);
        assertFalse(bean.showSubjectFromStudyZeroEnrollmentDialog());

        bean.setUsePidGenerator(true);

        // Portal runs on different site
        prepareMulticentricStudy("DD", "O-");
        assertFalse(bean.showSubjectFromStudyZeroEnrollmentDialog());

        prepareMulticentricStudy("DD", "DD-");

        // select study zero
        bean.getSelectedStudy().setStudyIdentifier(Constants.study0Identifier);
        assertFalse(bean.showSubjectFromStudyZeroEnrollmentDialog());

        bean.getSelectedStudy().setStudyIdentifier("DD");
    }

    // endregion

    // region showGeneratePidSectionInNewSubjectDialog

    @Test
    public void showGeneratePidSectionInNewSubjectDialog() {
        when(userContext.hasRole("ROLE_PID_NEW")).thenReturn(true);
        bean.setUsePidGenerator(true);
        prepareMulticentricStudy("DD", "DD-");
        setStudyFlag("IDAT", true, "true");

        assertTrue(bean.showGeneratePidSectionInNewSubjectDialog());

        // Study should have IDAT tag true
        setStudyFlag("IDAT", true, "false");
        assertFalse(bean.showGeneratePidSectionInNewSubjectDialog());

        setStudyFlag("IDAT", true, "true");

        // UsePidGenerator should be true
        bean.setUsePidGenerator(false);
        assertFalse(bean.showGeneratePidSectionInNewSubjectDialog());

        bean.setUsePidGenerator(true);

        // Portal and StudySite need to be on the same site
        prepareMulticentricStudy("DD", "TT-");
        assertFalse(bean.showGeneratePidSectionInNewSubjectDialog());

        prepareMulticentricStudy("DD", "DD-");

        // user should have "ROLE_PID_NEW" role
        when(userContext.hasRole("ROLE_PID_NEW")).thenReturn(false);
        assertFalse(bean.showGeneratePidSectionInNewSubjectDialog());
    }

    //    endregion

    // region PID tab

    @Test
    public void showGeneratePidButtonInNewSubjectDialog() {
        when(userContext.hasRole("ROLE_PID_NEW")).thenReturn(true);
        //when(userContext.hasRole("ROLE_REIDENTIFY")).thenReturn(true);

        bean.setUsePidGenerator(true);
        prepareMulticentricStudy("DD", "DD-");
        setStudyFlag("IDAT", true, "true");
        bean.getNewSubject().getPerson().setPid(null);

        assertTrue(bean.showGeneratePidButtonInNewSubjectDialog());


        bean.getNewSubject().getPerson().setPid("dummyPid");
        // false because the person has a pid already
        assertFalse(bean.showGeneratePidButtonInNewSubjectDialog());
    }

    private void setStudyFlag(String tagName, boolean isBoolean, String value) {
        if (updateExistingTagIfExist(tagName, value)) return;

        addNewTagToList(tagName, isBoolean, value);
    }

    private void addNewTagToList(String tagName, boolean isBoolean, String value) {
        StudyTag studyTag = new StudyTag();
        studyTagList.add(studyTag);

        StudyTagType studyTagType = new StudyTagType();
        studyTagType.setName(tagName);
        studyTagType.setIsBoolean(isBoolean);
        studyTag.setValue(value);
        studyTag.setType(studyTagType);
    }

    private boolean updateExistingTagIfExist(String tagName, String value) {
        for (StudyTag studyTag : studyTagList) {
            if (studyTag.getType().getName().equals(tagName)) {
                studyTag.setValue(value);
                return true;
            }
        }
        return false;
    }

    // endregion

    // region hasIdatFlag

    @Test
    public void hasIdatFlag_returns_true_if_flag_is_true() {
        setStudyFlag("IDAT", true, "true");

        assertTrue(bean.hasIdatFlag());
    }

    @Test
    public void hasIdatFlag_returns_false_if_flag_is_false() {
        setStudyFlag("IDAT", true, "false");

        assertFalse(bean.hasIdatFlag());
    }

    // endregion

    // region hasCcpFlag

    @Test
    public void hasCcpFlag_returns_true_if_flag_is_true() {
        setStudyFlag("CCP", true, "true");

        assertTrue(bean.hasCcpFlag());
    }

    @Test
    public void hasCcpFlag_returns_false_if_flag_is_false() {
        setStudyFlag("CCP", true, "false");

        assertFalse(bean.hasCcpFlag());
    }

    // endregion

    // region getCcpCode

    @Test
    public void getCcpcode_returns_empty_string_if_flag_is_not_set() {
        assertEquals("", bean.getCcpCode());
    }

    @Test
    public void getCcpcode_returns_url() {
        String url = "dummy-url";

        setStudyFlag("CCP-code", false, url);
        assertEquals(url, bean.getCcpCode());
    }

    // endregion

    // region portalRunsOnSamePartnerSide

    @Test
    public void portalRunsOnSamePartnerSide_returns_true_in_multi_centric_setup_if_partner_side_is_the_same() {
        prepareMulticentricStudy("DD", "DD-");

        assertTrue(bean.portalRunsOnSamePartnerSide());
    }

    private void prepareMulticentricStudy(String portalSiteIdentifier, String studySidePrefix) {
        setPortalSiteIdentifier(portalSiteIdentifier);

        String studyIdentifier = "dummyStudy";
        String siteIdentifier = studySidePrefix + studyIdentifier;

        prepareBeanWithStudyValues(studyIdentifier, siteIdentifier, portalSiteIdentifier);
    }

    @Test
    public void portalRunsOnSamePartnerSide_returns_true_in_mono_centric_setup_if_partner_side_is_the_same() {
        String portalSiteIdentifier = "DD";
        setPortalSiteIdentifier(portalSiteIdentifier);

        String studyIdentifier = "dummyStudy";
        String siteIdentifier = studyIdentifier;

        prepareBeanWithStudyValues(studyIdentifier, siteIdentifier, portalSiteIdentifier);

        assertTrue(bean.portalRunsOnSamePartnerSide());
    }

    @Test
    public void portalRunsOnSamePartnerSide_returns_false_in_multi_centric_setup_if_partner_side_is_the_different() {
        prepareMulticentricStudy("B", "DD-");

        assertFalse(bean.portalRunsOnSamePartnerSide());
    }

    @Test
    public void portalRunsOnSamePartnerSide_returns_false_in_mono_centric_setup_if_partner_side_is_the_same() {
        String portalSiteIdentifier = "B";
        setPortalSiteIdentifier(portalSiteIdentifier);

        String studyIdentifier = "dummyStudy";
        String siteIdentifier = studyIdentifier;

        String studySiteId = "DD";

        prepareBeanWithStudyValues(studyIdentifier, siteIdentifier, studySiteId);

        assertFalse(bean.portalRunsOnSamePartnerSide());
    }


    private void prepareBeanWithStudyValues(String studyIdentifier, String studySiteIdentifier, String siteIdMonoCentricStudy) {
        Study study = new Study();
        study.setStudyIdentifier(studyIdentifier);
        study.setSiteName(studySiteIdentifier);
        bean.setSelectedStudy(study);

        de.dktk.dd.rpb.core.domain.ctms.Study ctmsStudy = new de.dktk.dd.rpb.core.domain.ctms.Study();
        when(partnerSite.getIdentifier()).thenReturn(siteIdMonoCentricStudy);
        ctmsStudy.setPartnerSite(partnerSite);
        ctmsStudy.setTags(studyTagList);

        ReflectionTestUtils.setField(bean, "rpbStudy", ctmsStudy);

    }

    // endregion
}