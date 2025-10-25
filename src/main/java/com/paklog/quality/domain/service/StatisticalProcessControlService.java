package com.paklog.quality.domain.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Statistical Process Control (SPC) service
 * Implements control charts and process capability analysis
 */
@Service
public class StatisticalProcessControlService {
    private static final Logger log = LoggerFactory.getLogger(StatisticalProcessControlService.class);


    /**
     * Calculate SPC metrics for a data series
     */
    public SPCMetrics calculateSPCMetrics(List<Double> dataPoints) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            return SPCMetrics.builder().build();
        }

        double mean = calculateMean(dataPoints);
        double stdDev = calculateStdDev(dataPoints, mean);
        double ucl = mean + (3 * stdDev);  // Upper Control Limit
        double lcl = mean - (3 * stdDev);  // Lower Control Limit

        List<String> violations = detectViolations(dataPoints, mean, ucl, lcl);

        return SPCMetrics.builder()
            .mean(mean)
            .standardDeviation(stdDev)
            .upperControlLimit(ucl)
            .lowerControlLimit(lcl)
            .dataPoints(dataPoints.size())
            .violations(violations)
            .inControl(violations.isEmpty())
            .build();
    }

    /**
     * Calculate process capability indices
     */
    public ProcessCapability calculateCapability(List<Double> dataPoints,
                                                 double lowerSpecLimit,
                                                 double upperSpecLimit) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            return ProcessCapability.builder().build();
        }

        double mean = calculateMean(dataPoints);
        double stdDev = calculateStdDev(dataPoints, mean);

        // Cp - Process Capability Index (ignores centering)
        double cp = (upperSpecLimit - lowerSpecLimit) / (6 * stdDev);

        // Cpk - Process Capability Index (considers centering)
        double cpk = Math.min(
            (upperSpecLimit - mean) / (3 * stdDev),
            (mean - lowerSpecLimit) / (3 * stdDev)
        );

        // Classify process capability
        String capability = classifyCapability(cpk);

        return ProcessCapability.builder()
            .cp(cp)
            .cpk(cpk)
            .mean(mean)
            .standardDeviation(stdDev)
            .lowerSpecLimit(lowerSpecLimit)
            .upperSpecLimit(upperSpecLimit)
            .capability(capability)
            .build();
    }

    /**
     * Detect SPC rule violations (Western Electric Rules)
     */
    private List<String> detectViolations(List<Double> data, double mean, double ucl, double lcl) {
        List<String> violations = new ArrayList<>();

        // Rule 1: One point beyond 3 sigma
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) > ucl || data.get(i) < lcl) {
                violations.add("Point " + i + " beyond 3 sigma limits");
            }
        }

        // Rule 2: Nine consecutive points on same side of center line
        int consecutiveSameSide = 0;
        boolean aboveMean = data.get(0) > mean;

        for (double value : data) {
            boolean currentAboveMean = value > mean;

            if (currentAboveMean == aboveMean) {
                consecutiveSameSide++;
                if (consecutiveSameSide >= 9) {
                    violations.add("9 consecutive points on same side of mean");
                    break;
                }
            } else {
                consecutiveSameSide = 1;
                aboveMean = currentAboveMean;
            }
        }

        // Rule 3: Six consecutive increasing or decreasing points
        if (data.size() >= 6) {
            for (int i = 0; i <= data.size() - 6; i++) {
                boolean increasing = true;
                boolean decreasing = true;

                for (int j = i; j < i + 5; j++) {
                    if (data.get(j) >= data.get(j + 1)) {
                        increasing = false;
                    }
                    if (data.get(j) <= data.get(j + 1)) {
                        decreasing = false;
                    }
                }

                if (increasing || decreasing) {
                    violations.add("6 consecutive " + (increasing ? "increasing" : "decreasing") + " points at position " + i);
                }
            }
        }

        return violations;
    }

    private double calculateMean(List<Double> data) {
        return data.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
    }

    private double calculateStdDev(List<Double> data, double mean) {
        double variance = data.stream()
            .mapToDouble(value -> Math.pow(value - mean, 2))
            .average()
            .orElse(0.0);

        return Math.sqrt(variance);
    }

    private String classifyCapability(double cpk) {
        if (cpk >= 2.0) {
            return "EXCELLENT";
        } else if (cpk >= 1.33) {
            return "ADEQUATE";
        } else if (cpk >= 1.0) {
            return "MARGINAL";
        } else {
            return "INADEQUATE";
        }
    }

    public static class SPCMetrics {
        private final double mean;
        private final double standardDeviation;
        private final double upperControlLimit;
        private final double lowerControlLimit;
        private final int dataPoints;
        private final List<String> violations;
        private final boolean inControl;

        private SPCMetrics(Builder builder) {
            this.mean = builder.mean;
            this.standardDeviation = builder.standardDeviation;
            this.upperControlLimit = builder.upperControlLimit;
            this.lowerControlLimit = builder.lowerControlLimit;
            this.dataPoints = builder.dataPoints;
            this.violations = builder.violations;
            this.inControl = builder.inControl;
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters
        public double getMean() { return mean; }
        public double getStandardDeviation() { return standardDeviation; }
        public double getUpperControlLimit() { return upperControlLimit; }
        public double getLowerControlLimit() { return lowerControlLimit; }
        public int getDataPoints() { return dataPoints; }
        public List<String> getViolations() { return violations; }
        public boolean isInControl() { return inControl; }

        public static class Builder {
            private double mean;
            private double standardDeviation;
            private double upperControlLimit;
            private double lowerControlLimit;
            private int dataPoints;
            private List<String> violations;
            private boolean inControl;

            public Builder mean(double mean) {
                this.mean = mean;
                return this;
            }

            public Builder standardDeviation(double standardDeviation) {
                this.standardDeviation = standardDeviation;
                return this;
            }

            public Builder upperControlLimit(double upperControlLimit) {
                this.upperControlLimit = upperControlLimit;
                return this;
            }

            public Builder lowerControlLimit(double lowerControlLimit) {
                this.lowerControlLimit = lowerControlLimit;
                return this;
            }

            public Builder dataPoints(int dataPoints) {
                this.dataPoints = dataPoints;
                return this;
            }

            public Builder violations(List<String> violations) {
                this.violations = violations;
                return this;
            }

            public Builder inControl(boolean inControl) {
                this.inControl = inControl;
                return this;
            }

            public SPCMetrics build() {
                return new SPCMetrics(this);
            }
        }
    }

    public static class ProcessCapability {
        private final double cp;
        private final double cpk;
        private final double mean;
        private final double standardDeviation;
        private final double lowerSpecLimit;
        private final double upperSpecLimit;
        private final String capability;

        private ProcessCapability(Builder builder) {
            this.cp = builder.cp;
            this.cpk = builder.cpk;
            this.mean = builder.mean;
            this.standardDeviation = builder.standardDeviation;
            this.lowerSpecLimit = builder.lowerSpecLimit;
            this.upperSpecLimit = builder.upperSpecLimit;
            this.capability = builder.capability;
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters
        public double getCp() { return cp; }
        public double getCpk() { return cpk; }
        public double getMean() { return mean; }
        public double getStandardDeviation() { return standardDeviation; }
        public double getLowerSpecLimit() { return lowerSpecLimit; }
        public double getUpperSpecLimit() { return upperSpecLimit; }
        public String getCapability() { return capability; }

        public static class Builder {
            private double cp;
            private double cpk;
            private double mean;
            private double standardDeviation;
            private double lowerSpecLimit;
            private double upperSpecLimit;
            private String capability;

            public Builder cp(double cp) {
                this.cp = cp;
                return this;
            }

            public Builder cpk(double cpk) {
                this.cpk = cpk;
                return this;
            }

            public Builder mean(double mean) {
                this.mean = mean;
                return this;
            }

            public Builder standardDeviation(double standardDeviation) {
                this.standardDeviation = standardDeviation;
                return this;
            }

            public Builder lowerSpecLimit(double lowerSpecLimit) {
                this.lowerSpecLimit = lowerSpecLimit;
                return this;
            }

            public Builder upperSpecLimit(double upperSpecLimit) {
                this.upperSpecLimit = upperSpecLimit;
                return this;
            }

            public Builder capability(String capability) {
                this.capability = capability;
                return this;
            }

            public ProcessCapability build() {
                return new ProcessCapability(this);
            }
        }
    }
}
