package com.ford.intrastat.controller;

import com.ford.intrastat.dto.CountryDto;
import com.ford.intrastat.dto.LegalEntityDto;
import com.ford.intrastat.dto.PartDto;
import com.ford.intrastat.service.ReferenceDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reference Data", description = "Reference data endpoints for dropdowns")
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    @GetMapping("/legal-entities")
    @Operation(summary = "List all active legal entities")
    public ResponseEntity<List<LegalEntityDto>> getLegalEntities() {
        return ResponseEntity.ok(referenceDataService.getLegalEntities());
    }

    @GetMapping("/countries")
    @Operation(summary = "List all active EU member states, sorted by name")
    public ResponseEntity<List<CountryDto>> getCountries() {
        return ResponseEntity.ok(referenceDataService.getCountries());
    }

    @GetMapping("/parts")
    @Operation(summary = "List all parts sorted by part code")
    public ResponseEntity<List<PartDto>> getParts() {
        return ResponseEntity.ok(referenceDataService.getParts());
    }
}
