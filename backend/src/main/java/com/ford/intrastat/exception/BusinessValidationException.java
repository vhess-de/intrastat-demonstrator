package com.ford.intrastat.exception;

import java.util.List;

public class BusinessValidationException extends RuntimeException {

    private final List<FieldError> errors;

    public BusinessValidationException(List<FieldError> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public BusinessValidationException(String field, String message) {
        this(List.of(new FieldError(field, message)));
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public record FieldError(String field, String message) {}
}
