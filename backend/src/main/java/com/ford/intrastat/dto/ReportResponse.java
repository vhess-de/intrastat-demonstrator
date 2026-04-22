package com.ford.intrastat.dto;

import java.time.Instant;
import java.util.List;

public record ReportResponse(
        String period,
        Instant generatedAt,
        ReportSummaryDto summary,
        List<ReportLineDto> lines
) {}
