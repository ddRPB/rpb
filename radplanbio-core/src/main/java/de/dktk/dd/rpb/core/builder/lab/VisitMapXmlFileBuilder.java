package de.dktk.dd.rpb.core.builder.lab;

import de.dktk.dd.rpb.core.domain.edc.EventDefinition;
import de.dktk.dd.rpb.core.domain.edc.EventDefinitionDetails;
import de.dktk.dd.rpb.core.util.JAXBHelper;
import de.dktk.dd.rpb.core.util.labkey.OdmEventMetaDataLookup;
import org.labkey.study.xml.VisitMap;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

public class VisitMapXmlFileBuilder {
    List<VisitMap.Visit> visitList;

    public VisitMapXmlFileBuilder() {
        this.visitList = new ArrayList<>();
    }

    public VisitMapXmlFileBuilder(OdmEventMetaDataLookup odmEventMetaDataLookup) {
        this.visitList = new ArrayList<>();
        this.createRegistrationVisit();
        this.createVisits(odmEventMetaDataLookup);
    }

    public String build() throws JAXBException {

        VisitMap visitMap = new VisitMap();
        visitMap.setVisit(visitList);

        return JAXBHelper.jaxbObjectToXML(VisitMap.class, visitMap);
    }

    public void createRegistrationVisit() {
        VisitMap.Visit visit = new VisitMap.Visit();
        this.visitList.add(visit);

        visit.setLabel("Enrollment");
        visit.setSequenceNum(0.0);
        visit.setDescription("Enrollment");
    }

    private void createVisits(OdmEventMetaDataLookup odmEventMetaDataLookup) {
        for (EventDefinition event : odmEventMetaDataLookup.getEventDefinitionList()) {
            int ordinal = odmEventMetaDataLookup.getStudyEventOrdinal(event.getOid());
            EventDefinitionDetails eventDefinitionDetails = odmEventMetaDataLookup.getEventDefinitionDetails(event.getOid());

            VisitMap.Visit visit = new VisitMap.Visit();
            this.visitList.add(visit);

            visit.setLabel(event.getName());

            visit.setSequenceNum(new Double(ordinal));

            if (odmEventMetaDataLookup.getStudyEventIsRepeating(event.getOid())) {
                visit.setMaxSequenceNum(new Double(ordinal) + 0.9999);
            }

            visit.setDescription("Description: " + eventDefinitionDetails.getDescription() + " Category: " + eventDefinitionDetails.getCategory());

        }
    }
}
