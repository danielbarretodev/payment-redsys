package com.module.redsyspayment.domain.port;

public record PaymentNotificationResult(String orderNumber,
                                        boolean validSignature,
                                        boolean authorized,
                                        String dsResponseCode) {
}
