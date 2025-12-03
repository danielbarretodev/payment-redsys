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

    public void handleNotification(String signatureVersion,
                                   String merchantParameters,
                                   String signature) {

        // 1. Delegar en el adaptador Redsys (puerto) la validación / parseo
        PaymentNotificationResult result = paymentProcessorPort.processNotification(
            signatureVersion,
            merchantParameters,
            signature
        );

        if (!result.validSignature()) {
            // aquí puedes loggear intento fraudulento
            return;
        }

        // 2. Buscar el agregado Payment por Ds_Order
        Payment payment = paymentRepository.findByOrderNumber(result.orderNumber())
                .orElseThrow(() ->
                        new IllegalStateException("Payment not found for order " + result.orderNumber())
                );

        // 3. Actualizar estado según Ds_Response
        if (result.authorized()) {
            payment.authorize(); // método de dominio
        } else {
            payment.deny(); // método de dominio
        }

        // 4. Persistir cambios
        paymentRepository.save(payment);
    }
}
