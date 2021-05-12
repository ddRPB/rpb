package de.dktk.dd.rpb.core.domain.edc;

public enum EnumPidGenerationStrategy {

    NO_PID_GENERATOR("No"),
    PID_GENERATOR("Yes"),
    BRIDGEHEAD_PID_GENERATOR("CCP");

    private final String value;

    EnumPidGenerationStrategy(final String v) {
        this.value = v;
    }

    public static EnumPidGenerationStrategy fromString(String value) {
        for (EnumPidGenerationStrategy enumPidGenerationStrategy : EnumPidGenerationStrategy.values()) {
            if (enumPidGenerationStrategy.value.equalsIgnoreCase(value)) {
                return enumPidGenerationStrategy;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
