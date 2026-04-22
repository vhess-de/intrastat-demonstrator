package com.ford.intrastat.dto;

import com.ford.intrastat.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record TransactionDto(
        Long id,
        LegalEntitySummaryDto legalEntity,
        String period,
        String flowType,
        String counterpartCountryCode,
        String counterpartCountryName,
        PartSummaryDto part,
        Integer quantity,
        BigDecimal netMassKg,
        BigDecimal statisticalValueEur,
        BigDecimal invoiceValueEur,
        Integer natureOfTransactionCode,
        Integer modeOfTransport,
        String deliveryTerms,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    private static final DateTimeFormatter PERIOD_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    public static TransactionDto from(Transaction t) {
        return new TransactionDto(
                t.getId(),
                LegalEntitySummaryDto.from(t.getLegalEntity()),
                t.getPeriod().format(PERIOD_FMT),
                t.getFlowType().name(),
                t.getCounterpartCountry().getCode(),
                t.getCounterpartCountry().getName(),
                PartSummaryDto.from(t.getPart()),
                t.getQuantity(),
                t.getNetMassKg(),
                t.getStatisticalValueEur(),
                t.getInvoiceValueEur(),
                t.getNatureOfTransactionCode(),
                t.getModeOfTransport(),
                t.getDeliveryTerms(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
