package com.paklog.quality.domain.aggregate;

import com.paklog.quality.domain.valueobject.*;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "compliance_rules")
public class ComplianceRule {

    @Id
    private String id;

    private String ruleName;
    private String ruleCode;
    private String description;

    private ComplianceLevel level;
    private InspectionType applicableTo;

    private String condition;  // Rule expression
    private double threshold;

    private boolean active;
    private boolean mandatory;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Evaluate rule against inspection data
     */
    public boolean evaluate(Map<String, Object> inspectionData) {
        if (!active) {
            return true;
        }

        // Simple rule evaluation logic
        // In production, this would use a rules engine like Drools

        String field = condition.split(" ")[0];
        Object value = inspectionData.get(field);

        if (value == null) {
            return !mandatory;
        }

        if (value instanceof Number) {
            double numValue = ((Number) value).doubleValue();

            if (condition.contains(">")) {
                return numValue > threshold;
            } else if (condition.contains("<")) {
                return numValue < threshold;
            } else if (condition.contains(">=")) {
                return numValue >= threshold;
            } else if (condition.contains("<=")) {
                return numValue <= threshold;
            } else if (condition.contains("==")) {
                return Math.abs(numValue - threshold) < 0.001;
            }
        }

        return true;
    }
}
