package de.dktk.dd.rpb.portal.web.mb.pacs.support;

import de.dktk.dd.rpb.core.domain.edc.Subject;

import java.util.List;

/**
 * Represents a subject that has been staged in PACS for research
 */
public class StagedSubject extends Subject {

    private List<StagedDicomStudy> stagedStudies;

    public StagedSubject() {
        super();
    }

    public List<StagedDicomStudy> getStagedStudies() {
        return stagedStudies;
    }

    public void setStagedStudies(List<StagedDicomStudy> stagedStudies) {
        this.stagedStudies = stagedStudies;
    }
}
