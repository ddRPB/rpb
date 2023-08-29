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
public class OdmEventMetaDataLookupStudyEventOrderTest {
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
    public void events_have_correct_order() {
        OdmEventMetaDataLookup lookup = new OdmEventMetaDataLookup(odm.findFirstStudyOrNone().getMetaDataVersion());
        assertEquals("SE_PXBASELINE",new Integer("1"),lookup.getStudyEventOrdinal("SE_PXBASELINE"));
        assertEquals("SE_PXTRE",new Integer("2"),lookup.getStudyEventOrdinal("SE_PXTRE"));
        assertEquals("SE_PXEOT",new Integer("3"),lookup.getStudyEventOrdinal("SE_PXEOT"));
        assertEquals("SE_PXTFUP",new Integer("4"),lookup.getStudyEventOrdinal("SE_PXTFUP"));
        assertEquals("SE_PXQLQFUP",new Integer("5"),lookup.getStudyEventOrdinal("SE_PXQLQFUP"));
        assertEquals("SE_PX6WFUP",new Integer("6"),lookup.getStudyEventOrdinal("SE_PX6WFUP"));
        assertEquals("SE_PXFUP",new Integer("7"),lookup.getStudyEventOrdinal("SE_PXFUP"));
        assertEquals("SE_PXEOS", new Integer("8"),lookup.getStudyEventOrdinal("SE_PXEOS"));
        assertEquals("SE_PXAE", new Integer("9"),lookup.getStudyEventOrdinal("SE_PXAE"));
        assertEquals("SE_PXDD",new Integer("10"),lookup.getStudyEventOrdinal("SE_PXDD"));
        assertEquals("SE_PXDROPOUT", new Integer("11"),lookup.getStudyEventOrdinal("SE_PXDROPOUT"));

    }
}
