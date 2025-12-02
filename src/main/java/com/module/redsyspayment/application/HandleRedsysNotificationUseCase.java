package com.module.redsyspayment.application;

import com.module.redsyspayment.domain.model.Payment;
import com.module.redsyspayment.domain.port.PaymentNotificationResult;
import com.module.redsyspayment.domain.port.PaymentProcessorPort;
import com.module.redsyspayment.domain.port.PaymentRepository;

public class HandleRedsysNotificationUseCase {

    private final PaymentProcessorPort paymentProcessorPort;
    private final PaymentRepository paymentRepository;

    public HandleRedsysNotificationUseCase(PaymentProcessorPort paymentProcessorPort,
                                           PaymentRepository paymentRepository) {
        this.paymentProcessorPort = paymentProcessorPort;
        this.paymentRepository = paymentRepository;
    }

    public void handleNotification(String merchantParameters,
                                   String signature,
                                   String signatureVersion) {

        // 1. Dejar al adaptador Redsys validar firma y parsear parámetros
        PaymentNotificationResult result = paymentProcessorPort.processNotification(
                merchantParameters,
                signature,
                signatureVersion
        );

        if (!result.validSignature()) {
            // log de intento fraudulento, no actualizar nada
            return;
        }

        // 2. Buscar el Payment por orderNumber (Ds_Order)
        Payment payment = paymentRepository.findByOrderNumber(result.orderNumber())
                .orElseThrow(() -> new IllegalStateException("Payment not found for order " + result.orderNumber()));

        // 3. Actualizar estado según Ds_Response
        if (result.authorized()) {
            payment.authorize();
        } else {
            payment.deny();
        }

        paymentRepository.save(payment);
    }
}
