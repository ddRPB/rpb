package de.dktk.dd.rpb.core.facade;

import de.dktk.dd.rpb.core.domain.ctms.Study;
import de.dktk.dd.rpb.core.domain.ctms.StudyTag;
import de.dktk.dd.rpb.core.domain.ctms.StudyTagType;
import de.dktk.dd.rpb.core.domain.edc.EnumStudySubjectStatus;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import de.dktk.dd.rpb.core.repository.edc.IOpenClinicaDataRepository;
import de.dktk.dd.rpb.core.service.CtpService;
import de.dktk.dd.rpb.core.service.IOpenClinicaService;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openclinica.ws.beans.SiteType;
import org.openclinica.ws.beans.SitesType;
import org.openclinica.ws.beans.StudyType;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CtpPipelineLookupTableUpdaterFacade.class, Logger.class})
public class CtpPipelineLookupTableUpdaterFacadeTest {
    @Mock
    private IOpenClinicaDataRepository openClinicaDataRepository;
    @Mock
    private IOpenClinicaService engineOpenClinicaService;
    @Mock
    private CtpService svcCtp;

    @InjectMocks
    CtpPipelineLookupTableUpdaterFacade ctpPipelineLookupTableUpdaterFacade;

    @Before
    public void executedBeforeEach() {
        mockStatic(Logger.class);
        Logger logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);

        List<StudyType> studyTypeList = new ArrayList<>();

        // multi-centric-study
        StudyType studyTypeOne = new StudyType();
        studyTypeOne.setIdentifier("studyId");
        SiteType site = new SiteType();
        site.setIdentifier("abc-" + "studyId");
        List<SiteType> siteList = new ArrayList<>();
        siteList.add(site);

        SitesType sites = mock(SitesType.class);
        when(sites.getSite()).thenReturn(siteList);
        studyTypeOne.setSites(sites);

        // abc-study
        StudyType studyTypeTwo = new StudyType();
        studyTypeTwo.setIdentifier("abc-" + "studyId");

        // single-centric-study
        StudyType studyTypeThree = new StudyType();
        studyTypeThree.setIdentifier("fakeStudyId");


        studyTypeList.add(studyTypeOne);
        studyTypeList.add(studyTypeTwo);
        studyTypeList.add(studyTypeThree);

        when(engineOpenClinicaService.listAllStudies()).thenReturn(studyTypeList);
    }

    //region updateSubjectLookupForStudy
    @Test
    public void updateSubjectLookupForStudy_triggers_update_for_one_active_subject_on_multi_centric_study() {
        String studyTagName = "EDC-code";
        Study study = new Study();

        study.setProtocolId("abc-" + "studyId");

        List<StudyTag> studyTagsList = new ArrayList<>();
        StudyTagType tagType = new StudyTagType();
        tagType.setName(studyTagName);
        StudyTag edcCodeTag = new StudyTag(tagType);
        edcCodeTag.setValue("EDCcode123");
        studyTagsList.add(edcCodeTag);
        study.setTags(studyTagsList);

        List<StudySubject> subjects = getDummyStudySubjects();

        when(openClinicaDataRepository.findStudySubjectsByStudy(anyString())).thenReturn(subjects);
        when(svcCtp.updateSubjectLookupEntry(anyString(), (StudySubject) any())).thenReturn(true);

        List<Boolean> successList = ctpPipelineLookupTableUpdaterFacade.updateSubjectLookupForStudy(study, "abc");
        assertEquals(1, successList.size());
        assertFalse(successList.contains(false));
        assertTrue(successList.contains(true));
    }

    @Test
    public void updateSubjectLookupForStudy_triggers_update_for_one_active_subject_on_decentral_study() {
        String studyTagName = "EDC-code";
        Study study = new Study();

        study.setProtocolId("fakeStudyId");

        List<StudyTag> studyTagsList = new ArrayList<>();
        StudyTagType tagType = new StudyTagType();
        tagType.setName(studyTagName);
        StudyTag edcCodeTag = new StudyTag(tagType);
        edcCodeTag.setValue("EDCcode123");
        studyTagsList.add(edcCodeTag);
        study.setTags(studyTagsList);

        List<StudySubject> subjects = getDummyStudySubjects();

        when(openClinicaDataRepository.findStudySubjectsByStudy(anyString())).thenReturn(subjects);
        when(svcCtp.updateSubjectLookupEntry(anyString(), (StudySubject) any())).thenReturn(true);

        List<Boolean> successList = ctpPipelineLookupTableUpdaterFacade.updateSubjectLookupForStudy(study, "abc");
        assertEquals(1, successList.size());
        assertFalse(successList.contains(false));
        assertTrue(successList.contains(true));
    }

    private List<StudySubject> getDummyStudySubjects() {
        List<StudySubject> subjects = new ArrayList<>();

        StudySubject subject1 = new StudySubject();
        subject1.setPid("DD-12345");
        subject1.setStudySubjectId("dummyStudySubjectId");
        subject1.setStatus(EnumStudySubjectStatus.AVAILABLE.toString());
        subjects.add(subject1);

        StudySubject subject2 = new StudySubject();
        subject2.setPid("DD-23456");
        subject2.setStudySubjectId("dummyStudySubjectId-2");
        subjects.add(subject2);
        return subjects;
    }

    //endregion

    //endregion
}