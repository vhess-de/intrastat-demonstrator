package com.ford.intrastat.dto;

import java.util.List;

public record ValidationErrorResponse(List<FieldError> errors) {

    public record FieldError(String field, String message) {}
}
