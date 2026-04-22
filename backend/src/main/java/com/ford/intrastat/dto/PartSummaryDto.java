package com.ford.intrastat.dto;

import com.ford.intrastat.model.Part;

public record PartSummaryDto(Long id, String partCode, String name, String cn8Code) {

    public static PartSummaryDto from(Part p) {
        return new PartSummaryDto(p.getId(), p.getPartCode(), p.getName(), p.getCn8Code());
    }
}
