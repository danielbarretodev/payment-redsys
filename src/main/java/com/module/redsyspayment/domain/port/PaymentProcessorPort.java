package com.module.redsyspayment.domain.port;

import com.module.redsyspayment.domain.model.Payment;

public interface PaymentProcessorPort {

    PaymentRedirectData initPayment(Payment payment);

        PaymentNotificationResult processNotification(
            String signatureVersion,
            String merchantParameters,
            String signature
        );
}
