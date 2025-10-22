package com.paklog.quality.application.command;

import com.paklog.quality.domain.valueobject.*;
import lombok.*;
import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformInspectionCommand {
    @NotNull
    private InspectionType type;
    @NotBlank
    private String itemId;
    @NotBlank
    private String inspectorId;
    @NotNull
    private SamplingStrategy samplingStrategy;
    private int sampleSize;
    private String orderId;
    private String shipmentId;
}
