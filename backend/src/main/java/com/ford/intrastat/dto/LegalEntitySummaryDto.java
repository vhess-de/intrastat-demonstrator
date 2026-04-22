package com.ford.intrastat.dto;

import com.ford.intrastat.model.LegalEntity;

public record LegalEntitySummaryDto(Long id, String name, String countryCode) {

    public static LegalEntitySummaryDto from(LegalEntity e) {
        return new LegalEntitySummaryDto(e.getId(), e.getName(), e.getCountryCode());
    }
}
