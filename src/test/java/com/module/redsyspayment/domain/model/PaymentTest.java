package com.module.redsyspayment.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    /*
    test sencillo sobre el estado inicial de un payment
    */
    @Test
    void createNew_initialStatusIsPending() {
        Payment payment = Payment.createNew(
                PaymentId.newRandom(),
                OrderNumber.generate(new SecureRandom()),
                new BigDecimal("20.00"),
                "978"
        );

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isNotNull();
    }

    /*
    comprobamos que al autorizar un payment, su estado cambia a AUTHORIZED
    */
    @Test
    void authorize_changesStatusToAuthorized() {
        Payment payment = Payment.createNew(
                PaymentId.newRandom(),
                OrderNumber.generate(new SecureRandom()),
                new BigDecimal("20.00"),
                "978"
        );

        payment.authorize();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }
}
