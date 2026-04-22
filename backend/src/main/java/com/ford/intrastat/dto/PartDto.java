package com.ford.intrastat.dto;

import com.ford.intrastat.model.Part;

public record PartDto(Long id, String partCode, String name, String cn8Code) {

    public static PartDto from(Part p) {
        return new PartDto(p.getId(), p.getPartCode(), p.getName(), p.getCn8Code());
    }
}
