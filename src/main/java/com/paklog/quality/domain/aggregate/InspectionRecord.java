package com.paklog.quality.domain.aggregate;

import com.paklog.quality.domain.event.*;
import com.paklog.quality.domain.valueobject.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.*;

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

 private List<Defect> defects = new ArrayList<>();

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
 private List<DomainEvent> domainEvents = new ArrayList<>();

 // Business logic methods

 public void perform() {
 this.inspectedAt = Instant.now();
 this.result = InspectionResult.PASSED; // Default assumption
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

 public List<DomainEvent> domainEvents() {
 return getDomainEvents();
 }

 // Getters
 public String getId() { return id; }
 public String getInspectionNumber() { return inspectionNumber; }
 public InspectionType getType() { return type; }
 public InspectionResult getResult() { return result; }
 public String getOrderId() { return orderId; }
 public String getShipmentId() { return shipmentId; }
 public String getItemId() { return itemId; }
 public String getInspectorId() { return inspectorId; }
 public Instant getInspectedAt() { return inspectedAt; }
 public List<Defect> getDefects() { return defects; }
 public List<String> getPhotoUrls() { return photoUrls; }
 public SamplingStrategy getSamplingStrategy() { return samplingStrategy; }
 public int getSampleSize() { return sampleSize; }
 public int getItemsInspected() { return itemsInspected; }
 public int getDefectsFound() { return defectsFound; }
 public double getTemperatureCelsius() { return temperatureCelsius; }
 public double getWeightKg() { return weightKg; }
 public double getExpectedWeightKg() { return expectedWeightKg; }
 public double getWeightTolerancePercent() { return weightTolerancePercent; }
 public String getBarcode() { return barcode; }
 public boolean isBarcodeVerified() { return barcodeVerified; }
 public String getNotes() { return notes; }
 public String getCorrectionAction() { return correctionAction; }
 public Instant getCorrectionCompletedAt() { return correctionCompletedAt; }
 public Long getVersion() { return version; }
 public Instant getCreatedAt() { return createdAt; }
 public Instant getUpdatedAt() { return updatedAt; }

 // Setters
 public void setId(String id) { this.id = id; }
 public void setInspectionNumber(String inspectionNumber) { this.inspectionNumber = inspectionNumber; }
 public void setType(InspectionType type) { this.type = type; }
 public void setResult(InspectionResult result) { this.result = result; }
 public void setOrderId(String orderId) { this.orderId = orderId; }
 public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
 public void setItemId(String itemId) { this.itemId = itemId; }
 public void setInspectorId(String inspectorId) { this.inspectorId = inspectorId; }
 public void setInspectedAt(Instant inspectedAt) { this.inspectedAt = inspectedAt; }
 public void setDefects(List<Defect> defects) { this.defects = defects; }
 public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
 public void setSamplingStrategy(SamplingStrategy samplingStrategy) { this.samplingStrategy = samplingStrategy; }
 public void setSampleSize(int sampleSize) { this.sampleSize = sampleSize; }
 public void setItemsInspected(int itemsInspected) { this.itemsInspected = itemsInspected; }
 public void setDefectsFound(int defectsFound) { this.defectsFound = defectsFound; }
 public void setTemperatureCelsius(double temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }
 public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
 public void setExpectedWeightKg(double expectedWeightKg) { this.expectedWeightKg = expectedWeightKg; }
 public void setWeightTolerancePercent(double weightTolerancePercent) { this.weightTolerancePercent = weightTolerancePercent; }
 public void setBarcode(String barcode) { this.barcode = barcode; }
 public void setBarcodeVerified(boolean barcodeVerified) { this.barcodeVerified = barcodeVerified; }
 public void setNotes(String notes) { this.notes = notes; }
 public void setCorrectionAction(String correctionAction) { this.correctionAction = correctionAction; }
 public void setCorrectionCompletedAt(Instant correctionCompletedAt) { this.correctionCompletedAt = correctionCompletedAt; }

 public static Builder builder() {
 return new Builder();
 }

 public static class Builder {
 private String id;
 private String inspectionNumber;
 private InspectionType type;
 private InspectionResult result;
 private String orderId;
 private String shipmentId;
 private String itemId;
 private String inspectorId;
 private Instant inspectedAt;
 private List<Defect> defects = new ArrayList<>();
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

 public Builder id(String id) { this.id = id; return this; }
 public Builder inspectionNumber(String inspectionNumber) { this.inspectionNumber = inspectionNumber; return this; }
 public Builder type(InspectionType type) { this.type = type; return this; }
 public Builder result(InspectionResult result) { this.result = result; return this; }
 public Builder orderId(String orderId) { this.orderId = orderId; return this; }
 public Builder shipmentId(String shipmentId) { this.shipmentId = shipmentId; return this; }
 public Builder itemId(String itemId) { this.itemId = itemId; return this; }
 public Builder inspectorId(String inspectorId) { this.inspectorId = inspectorId; return this; }
 public Builder inspectedAt(Instant inspectedAt) { this.inspectedAt = inspectedAt; return this; }
 public Builder defects(List<Defect> defects) { this.defects = defects; return this; }
 public Builder photoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; return this; }
 public Builder samplingStrategy(SamplingStrategy samplingStrategy) { this.samplingStrategy = samplingStrategy; return this; }
 public Builder sampleSize(int sampleSize) { this.sampleSize = sampleSize; return this; }
 public Builder itemsInspected(int itemsInspected) { this.itemsInspected = itemsInspected; return this; }
 public Builder defectsFound(int defectsFound) { this.defectsFound = defectsFound; return this; }
 public Builder temperatureCelsius(double temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; return this; }
 public Builder weightKg(double weightKg) { this.weightKg = weightKg; return this; }
 public Builder expectedWeightKg(double expectedWeightKg) { this.expectedWeightKg = expectedWeightKg; return this; }
 public Builder weightTolerancePercent(double weightTolerancePercent) { this.weightTolerancePercent = weightTolerancePercent; return this; }
 public Builder barcode(String barcode) { this.barcode = barcode; return this; }
 public Builder barcodeVerified(boolean barcodeVerified) { this.barcodeVerified = barcodeVerified; return this; }
 public Builder notes(String notes) { this.notes = notes; return this; }
 public Builder correctionAction(String correctionAction) { this.correctionAction = correctionAction; return this; }
 public Builder correctionCompletedAt(Instant correctionCompletedAt) { this.correctionCompletedAt = correctionCompletedAt; return this; }

 public InspectionRecord build() {
 InspectionRecord record = new InspectionRecord();
 record.id = this.id;
 record.inspectionNumber = this.inspectionNumber;
 record.type = this.type;
 record.result = this.result;
 record.orderId = this.orderId;
 record.shipmentId = this.shipmentId;
 record.itemId = this.itemId;
 record.inspectorId = this.inspectorId;
 record.inspectedAt = this.inspectedAt;
 record.defects = this.defects;
 record.photoUrls = this.photoUrls;
 record.samplingStrategy = this.samplingStrategy;
 record.sampleSize = this.sampleSize;
 record.itemsInspected = this.itemsInspected;
 record.defectsFound = this.defectsFound;
 record.temperatureCelsius = this.temperatureCelsius;
 record.weightKg = this.weightKg;
 record.expectedWeightKg = this.expectedWeightKg;
 record.weightTolerancePercent = this.weightTolerancePercent;
 record.barcode = this.barcode;
 record.barcodeVerified = this.barcodeVerified;
 record.notes = this.notes;
 record.correctionAction = this.correctionAction;
 record.correctionCompletedAt = this.correctionCompletedAt;
 return record;
 }
 }
}
