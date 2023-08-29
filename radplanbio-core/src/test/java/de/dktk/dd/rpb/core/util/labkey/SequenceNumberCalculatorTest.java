package de.dktk.dd.rpb.core.util.labkey;

import de.dktk.dd.rpb.core.exception.MissingPropertyException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SequenceNumberCalculatorTest {

    @Test
    public void creates_correct_sequence_number_for_repeating_events() throws MissingPropertyException {
        Double sequenceNumber = SequenceNumberCalculator.calculateLabKeySequenceNumber(true,"2", new Integer(4));

        assertEquals(new Double(4.0002),sequenceNumber);
    }

    @Test
    public void creates_correct_sequence_number_for_not_repeating_events() throws MissingPropertyException {
        Double sequenceNumber = SequenceNumberCalculator.calculateLabKeySequenceNumber(false,"2", new Integer(4));

        assertEquals(new Double(4),sequenceNumber);
    }
}