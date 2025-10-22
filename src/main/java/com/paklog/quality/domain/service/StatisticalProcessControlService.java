package com.paklog.quality.domain.service;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Statistical Process Control (SPC) service
 * Implements control charts and process capability analysis
 */
@Slf4j
@Service
public class StatisticalProcessControlService {

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

    @Data
    @Builder
    public static class SPCMetrics {
        private double mean;
        private double standardDeviation;
        private double upperControlLimit;
        private double lowerControlLimit;
        private int dataPoints;
        private List<String> violations;
        private boolean inControl;
    }

    @Data
    @Builder
    public static class ProcessCapability {
        private double cp;
        private double cpk;
        private double mean;
        private double standardDeviation;
        private double lowerSpecLimit;
        private double upperSpecLimit;
        private String capability;
    }
}
