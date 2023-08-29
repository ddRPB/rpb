package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.domain.edc.Odm;
import de.dktk.dd.rpb.core.domain.edc.Study;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OdmStudyMetaDataLookup {
    private final List<Study> studyList;
    private final Study parentStudy;

    private final Map<String, String> studyOidSiteIdentifierLookup = new HashMap<>();

    public OdmStudyMetaDataLookup(Odm odm) {
        this.studyList = odm.getStudies();
        this.parentStudy = odm.getParentStudy();

        createStudySiteLookup();
    }

    public String getStudySiteIdentifierByOid(String oid){
        return this.studyOidSiteIdentifierLookup.get(oid);
    }

    private void createStudySiteLookup() {
        String parentStudyProtocolId = this.parentStudy.getOcStudyUniqueIdentifier();

        for (Study study : studyList) {
            String siteIdentifier = study.extractStudySiteIdentifier();

            // Exception - parent study
            if (siteIdentifier.isEmpty()) siteIdentifier = parentStudyProtocolId;

            studyOidSiteIdentifierLookup.put(study.getOid(), siteIdentifier);
        }
    }


}
