package com.ford.intrastat.dto;

import java.math.BigDecimal;

public record ReportLineDto(
        Long legalEntityId,
        String legalEntityName,
        String legalEntityCountry,
        String flowType,
        String counterpartCountryCode,
        String counterpartCountryName,
        String cn8Code,
        String partName,
        long totalQuantity,
        BigDecimal totalNetMassKg,
        BigDecimal totalStatisticalValueEur,
        BigDecimal totalInvoiceValueEur,
        int modeOfTransport,
        String modeOfTransportLabel,
        int natureOfTransactionCode,
        String deliveryTerms,
        long transactionCount
) {}
