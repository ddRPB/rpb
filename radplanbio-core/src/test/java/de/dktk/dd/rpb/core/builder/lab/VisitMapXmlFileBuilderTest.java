package de.dktk.dd.rpb.core.builder.lab;

import de.dktk.dd.rpb.core.builder.pacs.StagedSubjectPacsResultBuilder;
import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.xmlunit.matchers.ValidationMatcher.valid;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.xml.bind.annotation.*")
@PrepareForTest({StagedSubjectPacsResultBuilder.class, Logger.class, LoggerFactory.class})
public class VisitMapXmlFileBuilderTest {

    @Before
    public void setUp() {
        mockStatic(Logger.class);
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
    }

    //region empty constructor
    @Test
    public void creates_valid_xml_content() throws JAXBException {
        VisitMapXmlFileBuilder builder = new VisitMapXmlFileBuilder();
        String xmlContent = builder.build();

        assertThat(xmlContent,
                is(valid(new StreamSource(new File("src/test/resources/xml-schemas/labkey/visitMap.xsd")))));
    }

    @Test
    public void creates_a_visit_map_out_of_a_list_of_event_definitions() {
        VisitMapXmlFileBuilder builder = new VisitMapXmlFileBuilder();

        List<EventDefinition> eventDefinitionList = new ArrayList<>();

        EventDefinition eventOne = new EventDefinition();
        eventOne.setName("Registration");
        eventOne.setOid("DummyOid");
        eventOne.setType("Scheduled");
        eventOne.setIsMandatory(true);
        eventOne.setIsRepeating(false);

        eventDefinitionList.add(eventOne);

        //HashMap<String, Integer> sequenceMapping = builder.createEventDefinitions(eventDefinitionList);

        /*assertEquals(1,sequenceMapping.size());
        assertEquals(new Integer(1),sequenceMapping.get("DummyOid"));*/
    }

    @Test
    public void creates_a_visit_map_corresponding_to_event_definitions() throws JAXBException {
        VisitMapXmlFileBuilder builder = new VisitMapXmlFileBuilder();

        List<EventDefinition> eventDefinitionList = new ArrayList<>();

        EventDefinition eventOne = new EventDefinition();
        eventOne.setName("Registration");
        eventOne.setOid("DummyOid");
        eventOne.setType("Scheduled");
        eventOne.setDescription("Dummydescription");
        eventOne.setIsMandatory(true);
        eventOne.setIsRepeating(false);

        eventDefinitionList.add(eventOne);

       /* HashMap<String, Integer> sequenceMapping = builder.createEventDefinitions(eventDefinitionList);

        String xmlContent = builder.build();


        assertThat(xmlContent,
                is(valid(new StreamSource(new File("src/test/resources/xml-schemas/labkey/visitMap.xsd")))));

        VisitMap visitMap = JAXBHelper.unmashalString2(VisitMap.class,xmlContent);
        VisitMap.Visit visit = visitMap.getVisit().get(0);
        String oid = visit.getLabel();

        assertEquals("DummyOid",oid);
        assertEquals(new Integer(1),sequenceMapping.get(oid));*/
    }
    //endregion


}
