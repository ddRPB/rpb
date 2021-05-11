package de.dktk.dd.rpb.portal.web.mb.pacs.support;

import de.dktk.dd.rpb.core.domain.pacs.DicomSeries;

public class StagedDicomSeries extends DicomSeries {
    private boolean stageTwoRepresentation = false;

    public StagedDicomSeries() {
        super();
    }

    public boolean hasStageTwoRepresentation() {
        return stageTwoRepresentation;
    }

    public void setStageTwoRepresentation(boolean stageTwoRepresentation) {
        this.stageTwoRepresentation = stageTwoRepresentation;
    }
}
