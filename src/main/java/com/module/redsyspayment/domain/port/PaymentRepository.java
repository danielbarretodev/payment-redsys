package com.module.redsyspayment.domain.port;

import java.util.Optional;

import com.module.redsyspayment.domain.model.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(String paymentId);

    Optional<Payment> findByOrderNumber(String orderNumber);
}
