package com.module.redsyspayment.domain.model;

public enum PaymentStatus {
    PENDING,
    REDIRECTED_TO_TPV,
    AUTHORIZED,
    DENIED,
    ERROR,
    REFUNDED
}
