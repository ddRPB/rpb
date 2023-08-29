package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.exception.MissingPropertyException;

public class SequenceNumberCalculator {
    public static Double calculateLabKeySequenceNumber(boolean isRepeating, String repeatKey, Integer ordinal) throws MissingPropertyException {
        Double sequenceNumber;

        if (isRepeating) {
            if(repeatKey == null) throw new MissingPropertyException("Repeatkey is null");
            if(repeatKey.isEmpty()) throw new MissingPropertyException("Repeatkey is empty");

            String formattedRepeatKey = String.format("%04d", Integer.parseInt(repeatKey));
            sequenceNumber = new Double(Integer.toString(ordinal) + "." + formattedRepeatKey);
        } else {
            sequenceNumber = new Double(ordinal);
        }

        return sequenceNumber;
    }
}
