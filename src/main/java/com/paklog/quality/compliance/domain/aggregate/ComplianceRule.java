package com.paklog.quality.compliance.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * ComplianceRule Aggregate Root
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "compliancerules")
public class ComplianceRule {

    @Id
    private String id;

    private Instant createdAt;
    private Instant updatedAt;

    // Domain logic methods here
}