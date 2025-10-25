package com.paklog.quality.application.command;

import com.paklog.quality.domain.valueobject.InspectionType;
import com.paklog.quality.domain.valueobject.SamplingStrategy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PerformInspectionCommand(
    @NotNull
    InspectionType type,
    @NotBlank
    String itemId,
    @NotBlank
    String inspectorId,
    @NotNull
    SamplingStrategy samplingStrategy,
    int sampleSize,
    String orderId,
    String shipmentId
) {}
