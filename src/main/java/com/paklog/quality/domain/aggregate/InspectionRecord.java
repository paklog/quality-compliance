package com.paklog.quality.domain.aggregate;

import com.paklog.quality.domain.event.*;
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
@Document(collection = "inspection_records")
public class InspectionRecord {

    @Id
    private String id;

    private String inspectionNumber;
    private InspectionType type;
    private InspectionResult result;

    private String orderId;
    private String shipmentId;
    private String itemId;

    private String inspectorId;
    private Instant inspectedAt;

    @Builder.Default
    private List<Defect> defects = new ArrayList<>();

    @Builder.Default
    private List<String> photoUrls = new ArrayList<>();

    private SamplingStrategy samplingStrategy;
    private int sampleSize;
    private int itemsInspected;
    private int defectsFound;

    private double temperatureCelsius;
    private double weightKg;
    private double expectedWeightKg;
    private double weightTolerancePercent;

    private String barcode;
    private boolean barcodeVerified;

    private String notes;
    private String correctionAction;
    private Instant correctionCompletedAt;

    @Version
    private Long version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Transient
    @Builder.Default
    private List<DomainEvent> domainEvents = new ArrayList<>();

    // Business logic methods

    public void perform() {
        this.inspectedAt = Instant.now();
        this.result = InspectionResult.PASSED;  // Default assumption
    }

    public void addDefect(Defect defect) {
        this.defects.add(defect);
        this.defectsFound++;

        // Update result based on defect severity
        if (defect.isCritical()) {
            this.result = InspectionResult.QUARANTINE;

            addDomainEvent(DefectDetectedEvent.builder()
                .inspectionId(this.id)
                .defectType(defect.getType().name())
                .severity(defect.getSeverity().name())
                .description(defect.getDescription())
                .build());
        } else if (defect.getSeverity() == SeverityLevel.HIGH) {
            this.result = InspectionResult.FAILED;
        } else if (result == InspectionResult.PASSED) {
            this.result = InspectionResult.CONDITIONAL;
        }
    }

    public void validateWeight() {
        if (weightKg > 0 && expectedWeightKg > 0) {
            double deviation = Math.abs(weightKg - expectedWeightKg) / expectedWeightKg * 100;

            if (deviation > weightTolerancePercent) {
                Defect defect = Defect.builder()
                    .defectId(UUID.randomUUID().toString())
                    .type(DefectType.WEIGHT_DISCREPANCY)
                    .severity(SeverityLevel.MEDIUM)
                    .description(String.format("Weight deviation: %.2f%%", deviation))
                    .quantity(1)
                    .reportedAt(Instant.now())
                    .build();

                addDefect(defect);
            }
        }
    }

    public void validateTemperature(double minTemp, double maxTemp) {
        if (temperatureCelsius < minTemp || temperatureCelsius > maxTemp) {
            Defect defect = Defect.builder()
                .defectId(UUID.randomUUID().toString())
                .type(DefectType.TEMPERATURE_VIOLATION)
                .severity(SeverityLevel.CRITICAL)
                .description(String.format("Temperature out of range: %.1f°C (allowed: %.1f-%.1f)",
                    temperatureCelsius, minTemp, maxTemp))
                .quantity(1)
                .reportedAt(Instant.now())
                .build();

            addDefect(defect);
        }
    }

    public void validateBarcode(String expectedBarcode) {
        this.barcodeVerified = expectedBarcode.equals(this.barcode);

        if (!barcodeVerified) {
            Defect defect = Defect.builder()
                .defectId(UUID.randomUUID().toString())
                .type(DefectType.BARCODE_UNREADABLE)
                .severity(SeverityLevel.MAJOR)
                .description("Barcode mismatch or unreadable")
                .quantity(1)
                .reportedAt(Instant.now())
                .build();

            addDefect(defect);
        }
    }

    public void complete() {
        // Final result determination
        if (result == InspectionResult.QUARANTINE) {
            addDomainEvent(ComplianceViolationEvent.builder()
                .inspectionId(this.id)
                .violationType("CRITICAL_DEFECT")
                .description("Critical defects found - item quarantined")
                .build());
        }

        addDomainEvent(InspectionCompletedEvent.builder()
            .inspectionId(this.id)
            .inspectionType(this.type.name())
            .result(this.result.name())
            .defectsFound(this.defectsFound)
            .itemsInspected(this.itemsInspected)
            .build());
    }

    public void createNonConformance(String description) {
        addDomainEvent(NonConformanceCreatedEvent.builder()
            .inspectionId(this.id)
            .description(description)
            .defectCount(this.defectsFound)
            .build());
    }

    public void applyCorrection(String action) {
        this.correctionAction = action;
        this.correctionCompletedAt = Instant.now();

        addDomainEvent(CorrectiveActionTakenEvent.builder()
            .inspectionId(this.id)
            .action(action)
            .completedAt(this.correctionCompletedAt)
            .build());
    }

    public double getDefectRate() {
        return itemsInspected > 0 ? (double) defectsFound / itemsInspected * 100 : 0.0;
    }

    public boolean hasPhotos() {
        return !photoUrls.isEmpty();
    }

    public boolean requiresPhotos() {
        return !defects.isEmpty();
    }

    private void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }
}
