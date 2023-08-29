package de.dktk.dd.rpb.core.util.labkey.OdmEventMetaDataLookupTests;

import de.dktk.dd.rpb.core.domain.edc.EventDefinitionDetails;
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
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.xml.bind.annotation.*")
@PrepareForTest({ OdmEventMetaDataLookup.class, Odm.class, Logger.class, LoggerFactory.class})
public class OdmEventMetaDataLookupStudyEventDetailsTest {
    Odm odm;
    OdmEventMetaDataLookup lookup;

    @Before
    public void setUp() throws Exception {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        File file = new File("src/test/data/labkey/odm-export/metadata-export.xml");
        odm = JAXBHelper.unmarshalInputstream2(Odm.class, new FileInputStream(file));
        lookup = new OdmEventMetaDataLookup(odm.findFirstStudyOrNone().getMetaDataVersion());
    }

    @Test
    public void details_for_SE_PXBASELINE_event_are_correct() {
        String oid = "SE_PXBASELINE";
        String description = "Baseline visit event";
        String category = "Baseline";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PXTRE_event_are_correct() {
        String oid = "SE_PXTRE";
        String description = "Treatment weekly (1-6) repeating visit event";
        String category = "Treatment weekly (1-6) repeating visit event";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PXEOT_event_are_correct() {
        String oid = "SE_PXEOT";
        String description = "End-of-therapy report event (1 week after RT)";
        String category = "EndOfTherapy";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PXTFUP_event_are_correct() {
        String oid = "SE_PXTFUP";
        String description = "Telephone follow-up event (2, 4, 8, 10 weeks after RT)";
        String category = "FollowUp";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PXQLQFUP_event_are_correct() {
        String oid = "SE_PXQLQFUP";
        String description = "Quality-of-life follow-up visit event";
        String category = "FollowUp";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PX6WFUP_event_are_correct() {
        String oid = "SE_PX6WFUP";
        String description = "Follow-up visit event (6 weeks after RT)";
        String category = "FollowUp";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PXFUP_event_are_correct() {
        String oid = "SE_PXFUP";
        String description = "Follow-up visit event";
        String category = "FollowUp";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PXEOS_event_are_correct() {
        String oid = "SE_PXEOS";
        String description = "End-of-study event";
        String category = "EndOfStudy";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PXAE_event_are_correct() {
        String oid = "SE_PXAE";
        String description = "Adverse- and serious-adverse-event log";
        String category = "AdverseEvent";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PXDD_event_are_correct() {
        String oid = "SE_PXDD";
        String description = "Death-details event";
        String category = "DeathDetails";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

    @Test
    public void details_for_SE_PXDROPOUT_event_are_correct() {
        String oid = "SE_PXDROPOUT";
        String description = "Drop-out event";
        String category = "DropOut";

        EventDefinitionDetails details = lookup.getEventDefinitionDetails(oid);

        assertNotNull("Details exists for OID",details);
        assertEquals("oid",oid,details.getOid());
        assertEquals("description",description,details.getDescription());
        assertEquals("category",category,details.getCategory());

    }

}
