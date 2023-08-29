package de.dktk.dd.rpb.core.util.labkey.OdmEventMetaDataLookupTests;

import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import de.dktk.dd.rpb.core.util.labkey.OdmEventMetaDataLookup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.xml.bind.annotation.*")
@PrepareForTest({Odm.class, JAXBHelper.class, LoggerFactory.class, Logger.class})
public class OdmEventMetaDataLookupStudyEventNameLookupTest {
    Odm odm;

    @Before
    public void setUp() throws Exception {
        // prepare logger
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        File file = new File("src/test/data/labkey/odm-export/metadata-export.xml");
        odm = JAXBHelper.unmarshalInputstream2(Odm.class, new FileInputStream(file));

    }

    @Test
    public void events_have_correct_names() {
        OdmEventMetaDataLookup lookup = new OdmEventMetaDataLookup(odm.findFirstStudyOrNone().getMetaDataVersion());
        assertEquals("SE_PXBASELINE","Baseline",lookup.getStudyEventName("SE_PXBASELINE"));
        assertEquals("SE_PXTRE","Treatment",lookup.getStudyEventName("SE_PXTRE"));
        assertEquals("SE_PXEOT","End-of-Therapy",lookup.getStudyEventName("SE_PXEOT"));
        assertEquals("SE_PXTFUP","TEL-FUP",lookup.getStudyEventName("SE_PXTFUP"));
        assertEquals("SE_PXQLQFUP","QLQ-FUP",lookup.getStudyEventName("SE_PXQLQFUP"));
        assertEquals("SE_PX6WFUP","6-W-FUP",lookup.getStudyEventName("SE_PX6WFUP"));
        assertEquals("SE_PXFUP","Follow-Up",lookup.getStudyEventName("SE_PXFUP"));
        assertEquals("SE_PXEOS","End-of-Study",lookup.getStudyEventName("SE_PXEOS"));
        assertEquals("SE_PXAE","Adverse-Event",lookup.getStudyEventName("SE_PXAE"));
        assertEquals("SE_PXDD","Death-Details",lookup.getStudyEventName("SE_PXDD"));
        assertEquals("SE_PXDROPOUT","Drop-Out",lookup.getStudyEventName("SE_PXDROPOUT"));

    }
}
