package com.ford.intrastat.dto;

import com.ford.intrastat.model.LegalEntity;

public record LegalEntityDto(Long id, String name, String countryCode) {

    public static LegalEntityDto from(LegalEntity e) {
        return new LegalEntityDto(e.getId(), e.getName(), e.getCountryCode());
    }
}
