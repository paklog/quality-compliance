package com.paklog.quality.infrastructure.web.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paklog.quality.application.command.PerformInspectionCommand;
import com.paklog.quality.application.port.in.QualityControlUseCase;
import com.paklog.quality.domain.aggregate.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/quality")
@Tag(name = "Quality Control", description = "Quality control and compliance")
public class QualityController {
    private static final Logger log = LoggerFactory.getLogger(QualityController.class);


    private final QualityControlUseCase qualityUseCase;
    public QualityController(QualityControlUseCase qualityUseCase) {
        this.qualityUseCase = qualityUseCase;
    }


    @PostMapping("/inspections")
    @Operation(summary = "Perform inspection")
    public ResponseEntity<String> performInspection(@Valid @RequestBody PerformInspectionCommand command) {
        String inspectionId = qualityUseCase.performInspection(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(inspectionId);
    }

    @PostMapping("/inspections/{id}/defects")
    @Operation(summary = "Add defect to inspection")
    public ResponseEntity<Void> addDefect(@PathVariable String id, @RequestBody Defect defect) {
        qualityUseCase.addDefect(id, defect);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inspections/{id}/complete")
    @Operation(summary = "Complete inspection")
    public ResponseEntity<Void> completeInspection(@PathVariable String id) {
        qualityUseCase.completeInspection(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inspections/{id}")
    @Operation(summary = "Get inspection")
    public ResponseEntity<InspectionRecord> getInspection(@PathVariable String id) {
        return ResponseEntity.ok(qualityUseCase.getInspection(id));
    }
}
