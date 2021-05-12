package de.dktk.dd.rpb.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DicomUidReGeneratorUtilTest {

    @Test
    public void generates_stage_one_uid() {
        String uidPrefix = "1.2.826.0.1.3680043.9.7275.0.";
        String clinicalUid = "1.2.40.0.13.0.11.9636.2.2013410561.376087.20130903111234";
        String stageOneUid = "1.2.826.0.1.3680043.9.7275.0.12022756207963656325405033208318447";

        assertEquals(stageOneUid, DicomUidReGeneratorUtil.generateStageOneUid(uidPrefix, clinicalUid));
    }

    @Test
    public void generates_stage_two_uid() {
        String uidPrefix = "1.2.826.0.1.3680043.9.7275.0.";
        String partnerSideCode = "DD";
        String edcCode = "PR";
        String stageOneUid = "1.2.826.0.1.3680043.9.7275.0.25694151917667784091202966663655420";
        String stageTwoUid = "1.2.826.0.1.3680043.9.7275.0.28119988438981462564481418230477521";

        assertEquals(stageTwoUid, DicomUidReGeneratorUtil.generateStageTwoUid(uidPrefix, partnerSideCode, edcCode, stageOneUid));
    }
}