package com.paklog.quality.domain.aggregate;

import com.paklog.quality.domain.valueobject.*;
import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Defect {

    private String defectId;
    private DefectType type;
    private SeverityLevel severity;
    private String description;

    private String location;
    private int quantity;

    private String photoUrl;
    private String reportedBy;
    private Instant reportedAt;

    private String rootCause;
    private String correctionAction;

    public boolean isCritical() {
        return severity == SeverityLevel.CRITICAL;
    }

    public boolean requiresImmediate Action() {
        return severity == SeverityLevel.CRITICAL || severity == SeverityLevel.HIGH;
    }
}
