package com.module.redsyspayment.application;

import java.math.BigDecimal;
import java.util.UUID;

import com.module.redsyspayment.domain.model.Payment;
import com.module.redsyspayment.domain.model.PaymentId;
import com.module.redsyspayment.domain.port.PaymentProcessorPort;
import com.module.redsyspayment.domain.port.PaymentRedirectData;
import com.module.redsyspayment.domain.port.PaymentRepository;

public class CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentProcessorPort paymentProcessorPort;

    public CreatePaymentUseCase(PaymentRepository paymentRepository,
                                PaymentProcessorPort paymentProcessorPort) {
        this.paymentRepository = paymentRepository;
        this.paymentProcessorPort = paymentProcessorPort;
    }

    public PaymentRedirectData createPayment(BigDecimal amount, String currency) {
        // 1. Generar Ds_Order (único, numérico, longitud válida)
        String orderNumber = generateOrderNumber();

        // 2. Crear agregado Payment
        Payment payment = Payment.builder()
            .id(new PaymentId(UUID.randomUUID().toString()))
            .orderNumber(orderNumber)
            .amount(amount)
            .currency(currency)
            .build();

        // 3. Persistir en estado PENDING
        paymentRepository.save(payment);

        // 4. Pedir al puerto (Redsys) datos para redirigir al TPV
        PaymentRedirectData redirectData = paymentProcessorPort.initPayment(payment);

        // 5. Actualizar estado (opcional) a REDIRECTED_TO_TPV
        payment.markRedirectedToTpv();
        paymentRepository.save(payment);

        return redirectData;
    }

    private String generateOrderNumber() {
        // Generar 10-12 dígitos numéricos, por ejemplo:
        long nano = System.nanoTime();
        String raw = String.valueOf(nano);
        return raw.substring(raw.length() - 10); // ejemplo simple
    }
}
