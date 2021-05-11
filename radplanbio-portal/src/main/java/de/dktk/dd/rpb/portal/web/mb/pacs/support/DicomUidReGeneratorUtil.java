package de.dktk.dd.rpb.portal.web.mb.pacs.support;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;

/**
 * Re-generator for DICOM UIDs in the context of research pacs.
 * This component allows to generate DICOM UIDs for copies of studies on stage 1 and stage 2 level.
 */
public class DicomUidReGeneratorUtil {

    /**
     * Generates a DICOM UID for stage 1, based on the clinical DICOM uid
     *
     * @param dicomUidPrefix   String Prefix, assigned according to the DICOM standard to the organisation
     * @param clinicalStudyUid String clinical DICOM UID
     * @return String DICOM UID for stage 1 representation
     */
    public static String generateStageOneUid(String dicomUidPrefix, String clinicalStudyUid) {
        String result = "";
        if (clinicalStudyUid != null) {
            result = getDicomUidString(dicomUidPrefix, clinicalStudyUid);
        }
        return result;
    }

    private static String getDicomUidString(String dicomUidPrefix, String originUidAndSalt) {
        String hashHex = DigestUtils.md5Hex(originUidAndSalt);
        String hashBigintString = new BigInteger(hashHex, 16).toString();
        String extra = hashBigintString.startsWith("0") ? "9" : "";
        return (dicomUidPrefix + extra + hashBigintString).substring(0, 64);
    }

    /**
     * Generates a DICOM UID for stage 2, based on the DICOM uid for stage 2
     *
     * @param dicomUidPrefix  String Prefix, assigned according to the DICOM standard to the organisation
     * @param partnerSideCode String references the specific partner side, for example: DD
     * @param edcCode         String EDC Code for the Study
     * @param stageOneUid     String DICOM UID of the stage 1 representation of the data
     * @return String DICOM UID for the stage 2 representation
     */
    public static String generateStageTwoUid(String dicomUidPrefix, String partnerSideCode, String edcCode, String stageOneUid) {
        String result = "";
        if (stageOneUid != null) {
            String salt = edcCode + partnerSideCode;
            result = getDicomUidString(dicomUidPrefix, stageOneUid + salt);
        }
        return result;
    }
}