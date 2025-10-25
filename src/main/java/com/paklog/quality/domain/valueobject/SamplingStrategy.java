package com.paklog.quality.domain.valueobject;


public enum SamplingStrategy {
    FULL_INSPECTION(100.0),      // 100%
    AQL_2_5(2.5),                // AQL 2.5%
    AQL_4_0(4.0),                // AQL 4.0%
    RANDOM_10(10.0),             // 10% random
    RANDOM_25(25.0);             // 25% random

    private final double sampleRate;

    SamplingStrategy(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public boolean shouldInspect(int itemNumber, int totalItems) {
        if (this == FULL_INSPECTION) {
            return true;
        }

        // For AQL, use systematic sampling
        if (this == AQL_2_5 || this == AQL_4_0) {
            int interval = (int) Math.ceil(100.0 / sampleRate);
            return itemNumber % interval == 0;
        }

        // For random sampling, use modulo as approximation
        int interval = (int) Math.ceil(100.0 / sampleRate);
        return itemNumber % interval == 0;
    }
}
