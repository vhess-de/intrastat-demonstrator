package com.ford.intrastat.dto;

import com.ford.intrastat.model.EuCountry;

public record CountryDto(String code, String name) {

    public static CountryDto from(EuCountry c) {
        return new CountryDto(c.getCode(), c.getName());
    }
}
