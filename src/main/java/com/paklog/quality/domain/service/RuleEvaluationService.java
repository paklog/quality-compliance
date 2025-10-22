package com.paklog.quality.domain.service;

import com.paklog.quality.domain.aggregate.*;
import com.paklog.quality.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Rule evaluation engine for quality compliance
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEvaluationService {

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

    @lombok.Data
    @lombok.Builder
    public static class RuleEvaluationResult {
        private int totalRules;
        private int passedRules;
        private int failedRules;
        private int criticalFailures;
        private List<ComplianceRule> failedRuleDetails;
        private boolean overallPassed;
    }
}
