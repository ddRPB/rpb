package de.dktk.dd.rpb.core.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.dktk.dd.rpb.core.domain.edc.StudySubject;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static de.dktk.dd.rpb.core.util.Constants.study0EdcCode;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@SuppressWarnings("ConstantConditions")
@RunWith(PowerMockRunner.class)
@PrepareForTest({CtpService.class, Logger.class, Client.class})
public class CtpServiceTest {
    private Client clientMock;
    private ClientResponse responseMock;
    private WebResource webResourceMock;
    private final String method = "lookup";

    @InjectMocks
    CtpService ctpService;

    @Before
    public void setUp() {
        // Logger
        mockStatic(Logger.class);
        Logger logger = mock(Logger.class);
        when(Logger.getLogger(any(Class.class))).thenReturn(logger);

        // Client
        mockStatic(Client.class);
        clientMock = mock(Client.class);
        when(Client.create()).thenReturn(clientMock);
        webResourceMock = mock(WebResource.class);
        responseMock = mock(ClientResponse.class);

        when(clientMock.resource(anyString())).thenReturn(webResourceMock);
        when(webResourceMock.queryParam(anyString(), anyString())).thenReturn(webResourceMock);

        when(webResourceMock.get(any(Class.class))).thenReturn(responseMock);
        when(webResourceMock.put(any(Class.class))).thenReturn(responseMock);
        when(responseMock.getStatus()).thenReturn(200);
        when(responseMock.hasEntity()).thenReturn(true);

    }

    //region updateStudySubjectPseudonym

    @Test
    public void updateStudySubjectPseudonym_returns_true_if_response_is_two_hundred() {
        String edcCode = "fakeEDCCode";
        String hisUid = "HospitalId";
        String pid = "PatientId";
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateStudySubjectPseudonym(edcCode, hisUid, pid);
        assertTrue(success);
    }

    @Test
    public void updateStudySubjectPseudonym_returns_false_if_response_status_is_not_two_hundred() {
        when(responseMock.getStatus()).thenReturn(400);
        String edcCode = "fakeEDCCode";
        String hisUid = "HospitalId";
        String pid = "PatientId";
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateStudySubjectPseudonym(edcCode, hisUid, pid);
        assertFalse(success);
    }

    @Test
    public void updateStudySubjectPseudonym_calls_with_correct_url_parameter() {
        String edcCode = "fakeEDCCode";
        String hisUid = "HospitalId";
        String pid = "PatientId";
        String fakeUrl = "http://fake.url:8080";


        ctpService.setupConnection(fakeUrl, "ab", "ab");
        ctpService.updateStudySubjectPseudonym(edcCode, hisUid, pid);

        verify(clientMock).resource(fakeUrl + method);
        verify(webResourceMock).queryParam("id", edcCode);
        verify(webResourceMock).queryParam("key", "PID/" + hisUid);
        verify(webResourceMock).queryParam("value", pid);
    }

    // endregion

    // region updateStudySubjectId

    @Test
    public void updateStudySubjectId_returns_true_if_response_is_two_hundred() {
        String edcCode = "fakeEDCCode";
        String studySubjectId = "StudySubjectId";
        String pid = "PatientId";
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateStudySubjectId(edcCode, studySubjectId, pid);
        assertTrue(success);
    }

    @Test
    public void updateStudySubjectId_returns_false_if_response_status_is_not_two_hundred() {
        when(responseMock.getStatus()).thenReturn(400);
        String edcCode = "fakeEDCCode";
        String studySubjectId = "StudySubjectId";
        String pid = "PatientId";
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateStudySubjectId(edcCode, studySubjectId, pid);
        assertFalse(success);
    }

    @Test
    public void updateStudySubjectId_calls_with_correct_url_parameter() {
        String edcCode = "fakeEDCCode";
        String studySubjectId = "StudySubjectId";
        String pid = "PatientId";
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        ctpService.updateStudySubjectId(edcCode, studySubjectId, pid);

        verify(clientMock).resource(fakeUrl + method);
        verify(webResourceMock).queryParam("id", edcCode);
        verify(webResourceMock).queryParam("key", "SSID/" + studySubjectId);
        verify(webResourceMock).queryParam("value", pid);
    }

    //endregion

    // region updateSubjectLookupEntry

    @Test
    public void updateSubjectLookupEntry_returns_false_if_edcCode_is_null() {
        String edcCode = null;
        StudySubject subject = new StudySubject();
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateSubjectLookupEntry(edcCode, subject);
        assertFalse(success);
    }

    @Test
    public void updateSubjectLookupEntry_returns_false_if_edcCode_is_empty() {
        String edcCode = "";
        StudySubject subject = new StudySubject();
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateSubjectLookupEntry(edcCode, subject);
        assertFalse(success);
    }

    @Test
    public void updateSubjectLookupEntry_returns_false_if_subject_is_null() {
        String edcCode = "edcCode";
        StudySubject subject = null;
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        @SuppressWarnings("ConstantConditions") boolean success = ctpService.updateSubjectLookupEntry(edcCode, subject);
        assertFalse(success);
    }

    @Test
    public void updateSubjectLookupEntry_returns_false_if_subject_subject_id_is_empty() {
        String studySubjectId = "";
        String pid = "PatientId";
        String edcCode = "edcCode";
        StudySubject subject = new StudySubject();
        subject.setStudySubjectId(studySubjectId);
        subject.setPid(pid);
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateSubjectLookupEntry(edcCode, subject);
        assertFalse(success);
    }

    @Test
    public void updateSubjectLookupEntry_returns_false_if_pid_id_is_empty() {
        String studySubjectId = "StudySubjectId";
        String pid = "";
        String edcCode = "edcCode";
        StudySubject subject = new StudySubject();
        subject.setStudySubjectId(studySubjectId);
        subject.setPid(pid);
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateSubjectLookupEntry(edcCode, subject);
        assertFalse(success);
    }

    @Test
    public void updateSubjectLookupEntry_updates_pseudonym_of_study_zero() {
        String studySubjectId = "StudySubjectId";
        String pid = "PID";
        String edcCode = study0EdcCode;
        StudySubject subject = new StudySubject();
        subject.setStudySubjectId("HIS-" + studySubjectId);
        subject.setPid(pid);
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateSubjectLookupEntry(edcCode, subject);
        assertTrue(success);

        verify(clientMock).resource(fakeUrl + method);
        verify(webResourceMock).queryParam("id", edcCode);
        verify(webResourceMock).queryParam("key", "PID/" + studySubjectId);
        verify(webResourceMock).queryParam("value", pid);
    }

    @Test
    public void updateSubjectLookupEntry_updates_study_subject_id_mapping_of__other_study() {
        String studySubjectId = "StudySubjectId";
        String pid = "PID";
        String edcCode = "edcCode";
        StudySubject subject = new StudySubject();
        subject.setStudySubjectId(studySubjectId);
        subject.setPid(pid);
        String fakeUrl = "http://fake.url:8080";

        ctpService.setupConnection(fakeUrl, "ab", "ab");
        boolean success = ctpService.updateSubjectLookupEntry(edcCode, subject);
        assertTrue(success);

        verify(clientMock).resource(fakeUrl + method);
        verify(webResourceMock).queryParam("id", edcCode);
        verify(webResourceMock).queryParam("key", "SSID/" + pid);
        verify(webResourceMock).queryParam("value", studySubjectId);
    }
    //endregion
}