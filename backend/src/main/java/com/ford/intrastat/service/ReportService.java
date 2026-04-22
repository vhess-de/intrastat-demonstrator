package com.ford.intrastat.service;

import com.ford.intrastat.dto.ReportLineDto;
import com.ford.intrastat.dto.ReportResponse;
import com.ford.intrastat.dto.ReportSummaryDto;
import com.ford.intrastat.model.Transaction;
import com.ford.intrastat.repository.TransactionRepository;
import com.ford.intrastat.spec.TransactionSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportResponse getReport(String period, Long legalEntityId, String flowType) {
        List<Transaction> transactions = transactionRepository.findAll(
                TransactionSpec.withFilters(period, flowType, legalEntityId, null));

        if (transactions.isEmpty()) {
            return new ReportResponse(period, Instant.now(), ReportSummaryDto.empty(), List.of());
        }

        Map<GroupKey, List<Transaction>> grouped = transactions.stream()
                .collect(Collectors.groupingBy(t -> new GroupKey(
                        t.getLegalEntity().getId(),
                        t.getFlowType().name(),
                        t.getCounterpartCountry().getCode(),
                        t.getPart().getCn8Code(),
                        t.getModeOfTransport(),
                        t.getNatureOfTransactionCode(),
                        t.getDeliveryTerms())));

        List<ReportLineDto> lines = grouped.entrySet().stream()
                .map(e -> buildLine(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ReportLineDto::flowType)
                        .thenComparing(Comparator.comparing(ReportLineDto::totalStatisticalValueEur).reversed()))
                .toList();

        return new ReportResponse(period, Instant.now(), buildSummary(lines), lines);
    }

    public List<String> getPeriods() {
        return transactionRepository.findDistinctPeriods();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ReportLineDto buildLine(GroupKey key, List<Transaction> txns) {
        Transaction first = txns.get(0);
        long totalQty = txns.stream().mapToLong(t -> t.getQuantity()).sum();
        BigDecimal totalNetMass = txns.stream()
                .map(Transaction::getNetMassKg).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalStatValue = txns.stream()
                .map(Transaction::getStatisticalValueEur).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalInvValue = txns.stream()
                .map(Transaction::getInvoiceValueEur).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReportLineDto(
                key.legalEntityId(),
                first.getLegalEntity().getName(),
                first.getLegalEntity().getCountryCode(),
                key.flowType(),
                key.counterpartCountryCode(),
                first.getCounterpartCountry().getName(),
                key.cn8Code(),
                first.getPart().getName(),
                totalQty,
                totalNetMass,
                totalStatValue,
                totalInvValue,
                key.modeOfTransport(),
                transportLabel(key.modeOfTransport()),
                key.natureOfTransactionCode(),
                key.deliveryTerms(),
                txns.size());
    }

    private ReportSummaryDto buildSummary(List<ReportLineDto> lines) {
        BigDecimal totalStatValue = lines.stream()
                .map(ReportLineDto::totalStatisticalValueEur).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalNetMass = lines.stream()
                .map(ReportLineDto::totalNetMassKg).reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalQty = lines.stream().mapToLong(ReportLineDto::totalQuantity).sum();

        long arrivalLines = lines.stream().filter(l -> "ARRIVAL".equals(l.flowType())).count();
        long dispatchLines = lines.stream().filter(l -> "DISPATCH".equals(l.flowType())).count();
        BigDecimal arrivalValue = lines.stream()
                .filter(l -> "ARRIVAL".equals(l.flowType()))
                .map(ReportLineDto::totalStatisticalValueEur).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal dispatchValue = lines.stream()
                .filter(l -> "DISPATCH".equals(l.flowType()))
                .map(ReportLineDto::totalStatisticalValueEur).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReportSummaryDto(
                lines.size(), totalStatValue, totalNetMass, totalQty,
                arrivalLines, dispatchLines, arrivalValue, dispatchValue);
    }

    private static String transportLabel(int code) {
        return switch (code) {
            case 1 -> "Sea";
            case 3 -> "Road";
            case 4 -> "Air";
            case 5 -> "Post";
            case 7 -> "Fixed installations";
            case 9 -> "Own propulsion";
            default -> String.valueOf(code);
        };
    }

    private record GroupKey(
            Long legalEntityId,
            String flowType,
            String counterpartCountryCode,
            String cn8Code,
            int modeOfTransport,
            int natureOfTransactionCode,
            String deliveryTerms) {}
}
