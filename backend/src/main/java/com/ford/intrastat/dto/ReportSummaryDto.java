package com.ford.intrastat.dto;

import java.math.BigDecimal;

public record ReportSummaryDto(
        int totalLines,
        BigDecimal totalStatisticalValueEur,
        BigDecimal totalNetMassKg,
        long totalQuantity,
        long arrivalLines,
        long dispatchLines,
        BigDecimal arrivalValueEur,
        BigDecimal dispatchValueEur
) {
    public static ReportSummaryDto empty() {
        return new ReportSummaryDto(0, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0,
                BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
