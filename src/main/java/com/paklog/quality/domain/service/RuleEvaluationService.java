package com.paklog.quality.domain.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paklog.quality.domain.aggregate.*;
import com.paklog.quality.domain.valueobject.*;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Rule evaluation engine for quality compliance
 */
@Service
public class RuleEvaluationService {
    private static final Logger log = LoggerFactory.getLogger(RuleEvaluationService.class);


    /**
     * Evaluate all applicable rules for an inspection
     */
    public RuleEvaluationResult evaluateRules(InspectionRecord inspection, List<ComplianceRule> rules) {
        log.info("Evaluating {} rules for inspection {}", rules.size(), inspection.getId());

        Map<String, Object> inspectionData = buildInspectionData(inspection);

        List<ComplianceRule> passedRules = new ArrayList<>();
        List<ComplianceRule> failedRules = new ArrayList<>();

        for (ComplianceRule rule : rules) {
            if (!rule.getApplicableTo().equals(inspection.getType())) {
                continue;  // Skip rules not applicable to this inspection type
            }

            boolean passed = rule.evaluate(inspectionData);

            if (passed) {
                passedRules.add(rule);
            } else {
                failedRules.add(rule);

                // Handle critical rule failures
                if (rule.getLevel() == ComplianceLevel.CRITICAL) {
                    log.warn("CRITICAL rule violation: {} for inspection {}", rule.getRuleName(), inspection.getId());
                }
            }
        }

        return RuleEvaluationResult.builder()
            .totalRules(rules.size())
            .passedRules(passedRules.size())
            .failedRules(failedRules.size())
            .criticalFailures((int) failedRules.stream()
                .filter(r -> r.getLevel() == ComplianceLevel.CRITICAL)
                .count())
            .failedRuleDetails(failedRules)
            .overallPassed(failedRules.stream()
                .noneMatch(r -> r.getLevel() == ComplianceLevel.CRITICAL))
            .build();
    }

    /**
     * Build inspection data map for rule evaluation
     */
    private Map<String, Object> buildInspectionData(InspectionRecord inspection) {
        Map<String, Object> data = new HashMap<>();

        data.put("temperatureCelsius", inspection.getTemperatureCelsius());
        data.put("weightKg", inspection.getWeightKg());
        data.put("expectedWeightKg", inspection.getExpectedWeightKg());
        data.put("defectRate", inspection.getDefectRate());
        data.put("defectsFound", inspection.getDefectsFound());
        data.put("itemsInspected", inspection.getItemsInspected());
        data.put("barcodeVerified", inspection.isBarcodeVerified());
        data.put("hasPhotos", inspection.hasPhotos());

        return data;
    }

    public static class RuleEvaluationResult {
        private final int totalRules;
        private final int passedRules;
        private final int failedRules;
        private final int criticalFailures;
        private final List<ComplianceRule> failedRuleDetails;
        private final boolean overallPassed;

        private RuleEvaluationResult(Builder builder) {
            this.totalRules = builder.totalRules;
            this.passedRules = builder.passedRules;
            this.failedRules = builder.failedRules;
            this.criticalFailures = builder.criticalFailures;
            this.failedRuleDetails = builder.failedRuleDetails;
            this.overallPassed = builder.overallPassed;
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters
        public int getTotalRules() { return totalRules; }
        public int getPassedRules() { return passedRules; }
        public int getFailedRules() { return failedRules; }
        public int getCriticalFailures() { return criticalFailures; }
        public List<ComplianceRule> getFailedRuleDetails() { return failedRuleDetails; }
        public boolean isOverallPassed() { return overallPassed; }

        public static class Builder {
            private int totalRules;
            private int passedRules;
            private int failedRules;
            private int criticalFailures;
            private List<ComplianceRule> failedRuleDetails;
            private boolean overallPassed;

            public Builder totalRules(int totalRules) {
                this.totalRules = totalRules;
                return this;
            }

            public Builder passedRules(int passedRules) {
                this.passedRules = passedRules;
                return this;
            }

            public Builder failedRules(int failedRules) {
                this.failedRules = failedRules;
                return this;
            }

            public Builder criticalFailures(int criticalFailures) {
                this.criticalFailures = criticalFailures;
                return this;
            }

            public Builder failedRuleDetails(List<ComplianceRule> failedRuleDetails) {
                this.failedRuleDetails = failedRuleDetails;
                return this;
            }

            public Builder overallPassed(boolean overallPassed) {
                this.overallPassed = overallPassed;
                return this;
            }

            public RuleEvaluationResult build() {
                return new RuleEvaluationResult(this);
            }
        }
    }
}
