package com.ford.intrastat.controller;

import com.ford.intrastat.dto.ReportResponse;
import com.ford.intrastat.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Tag(name = "Report", description = "Intrastat aggregated report endpoints")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @Operation(summary = "Aggregated Intrastat report for a reporting period")
    public ResponseEntity<ReportResponse> getReport(
            @RequestParam String period,
            @RequestParam(required = false) Long legalEntityId,
            @RequestParam(required = false) String flowType) {

        return ResponseEntity.ok(reportService.getReport(period, legalEntityId, flowType));
    }

    @GetMapping("/periods")
    @Operation(summary = "List available reporting periods, newest first")
    public ResponseEntity<List<String>> getPeriods() {
        return ResponseEntity.ok(reportService.getPeriods());
    }
}
