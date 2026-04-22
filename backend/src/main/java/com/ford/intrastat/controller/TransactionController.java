package com.ford.intrastat.controller;

import com.ford.intrastat.dto.PagedResponse;
import com.ford.intrastat.dto.TransactionDto;
import com.ford.intrastat.dto.TransactionPatchRequest;
import com.ford.intrastat.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management endpoints")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @Operation(summary = "List transactions with optional filters and pagination")
    public ResponseEntity<PagedResponse<TransactionDto>> list(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String flowType,
            @RequestParam(required = false) Long legalEntityId,
            @RequestParam(required = false) String counterpartCountry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        return ResponseEntity.ok(
                transactionService.list(period, flowType, legalEntityId, counterpartCountry, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single transaction by ID")
    public ResponseEntity<TransactionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a transaction")
    public ResponseEntity<TransactionDto> patch(
            @PathVariable Long id,
            @Valid @RequestBody TransactionPatchRequest request) {

        return ResponseEntity.ok(transactionService.patch(id, request));
    }
}
