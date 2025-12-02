package com.module.redsyspayment.interfaces.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "PaymentRequest",
    description = "Payload used to request the creation of a new payment"
)
public record PaymentRequest(

        @Schema(
            description = "Total amount of the payment in EUR",
            example = "49.90",
            minimum = "0.01"
        )
        BigDecimal amount,

        @Schema(
            description = "ISO 4217 currency code (numeric). Default: 978 (EUR)",
            example = "978",
            defaultValue = "978"
        )
        String currency
) {}
