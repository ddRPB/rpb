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
public class OdmEventMetaDataLookupStudyEventIsRepeatingTest {
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
    public void events_have_correct_repeating_flag() {
        OdmEventMetaDataLookup lookup = new OdmEventMetaDataLookup(odm.findFirstStudyOrNone().getMetaDataVersion());
        assertEquals("SE_PXBASELINE", false, lookup.getStudyEventIsRepeating("SE_PXBASELINE"));
        assertEquals("SE_PXTRE", true, lookup.getStudyEventIsRepeating("SE_PXTRE"));
        assertEquals("SE_PXEOT", false, lookup.getStudyEventIsRepeating("SE_PXEOT"));
        assertEquals("SE_PXTFUP", true, lookup.getStudyEventIsRepeating("SE_PXTFUP"));
        assertEquals("SE_PXQLQFUP", true, lookup.getStudyEventIsRepeating("SE_PXQLQFUP"));
        assertEquals("SE_PX6WFUP", false, lookup.getStudyEventIsRepeating("SE_PX6WFUP"));
        assertEquals("SE_PXFUP", true, lookup.getStudyEventIsRepeating("SE_PXFUP"));
        assertEquals("SE_PXEOS", false, lookup.getStudyEventIsRepeating("SE_PXEOS"));
        assertEquals("SE_PXAE", true, lookup.getStudyEventIsRepeating("SE_PXAE"));
        assertEquals("SE_PXDD", false, lookup.getStudyEventIsRepeating("SE_PXDD"));
        assertEquals("SE_PXDROPOUT", false, lookup.getStudyEventIsRepeating("SE_PXDROPOUT"));

    }
}
