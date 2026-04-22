package com.ford.intrastat.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TransactionPatchRequest {

    @Positive(message = "Must be greater than 0")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Must be greater than 0")
    private BigDecimal statisticalValueEur;

    @DecimalMin(value = "0.01", message = "Must be greater than 0")
    private BigDecimal invoiceValueEur;

    /** Validated in service: must be an active EU member state. */
    private String counterpartCountryCode;

    @Min(value = 1, message = "Must be between 1 and 9")
    @Max(value = 9, message = "Must be between 1 and 9")
    private Integer modeOfTransport;

    @Min(value = 11, message = "Must be between 11 and 99")
    @Max(value = 99, message = "Must be between 11 and 99")
    private Integer natureOfTransactionCode;

    /** Validated in service: must be a valid Incoterms code. */
    private String deliveryTerms;
}
