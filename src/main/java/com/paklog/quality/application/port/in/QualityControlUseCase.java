package com.paklog.quality.application.port.in;

import com.paklog.quality.application.command.PerformInspectionCommand;
import com.paklog.quality.domain.aggregate.*;

public interface QualityControlUseCase {
    String performInspection(PerformInspectionCommand command);
    void addDefect(String inspectionId, Defect defect);
    void completeInspection(String inspectionId);
    InspectionRecord getInspection(String inspectionId);
}
