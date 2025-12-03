package com.module.redsyspayment.application;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.UUID;

import com.module.redsyspayment.domain.model.OrderNumber;
import com.module.redsyspayment.domain.model.Payment;
import com.module.redsyspayment.domain.model.PaymentId;
import com.module.redsyspayment.domain.port.PaymentProcessorPort;
import com.module.redsyspayment.domain.port.PaymentRedirectData;
import com.module.redsyspayment.domain.port.PaymentRepository;

public class CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentProcessorPort paymentProcessorPort;
    private final SecureRandom random = new SecureRandom();

    public CreatePaymentUseCase(PaymentRepository paymentRepository,
                                PaymentProcessorPort paymentProcessorPort) {
        this.paymentRepository = paymentRepository;
        this.paymentProcessorPort = paymentProcessorPort;
    }

    public PaymentRedirectData createPayment(BigDecimal amount, String currency) {

        // 1. Generar Ds_Order válido para Redsys
        OrderNumber orderNumber = OrderNumber.generate(random);

        // 2. Crear el agregado
        Payment payment = Payment.createNew(
            PaymentId.newRandom(),
            orderNumber,
            amount,
            currency
        );

        // 3. Persistir en estado inicial
        paymentRepository.save(payment);

        // 4. Llamar al puerto para obtener datos de redirección al TPV
        PaymentRedirectData redirectData = paymentProcessorPort.initPayment(payment);

        // 5. Cambiar estado del agregado
        payment.markRedirectedToTpv();

        // 6. Guardar cambios
        paymentRepository.save(payment);

        return redirectData;
    }
}
