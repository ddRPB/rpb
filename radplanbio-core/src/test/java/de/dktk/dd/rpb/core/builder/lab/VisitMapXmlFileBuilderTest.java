package de.dktk.dd.rpb.core.builder.lab;

import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.ValidationMatcher.valid;

public class VisitMapXmlFileBuilderTest {

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