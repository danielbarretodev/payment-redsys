package com.module.redsyspayment.application;

import com.module.redsyspayment.domain.model.*;
import com.module.redsyspayment.domain.port.PaymentNotificationResult;
import com.module.redsyspayment.domain.port.PaymentProcessorPort;
import com.module.redsyspayment.domain.port.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Optional;

import static org.mockito.Mockito.*;

class HandleRedsysNotificationUseCaseTest {

    /*
    comprobamos si la firma es valida
    se comprueba si esta buscando el payment por order number
    se comprueba si se guarda el payment actualizado
    */
    @Test
    void handleNotification_authorized_callsRepositoryAndUpdatesPayment() {
        // given
        PaymentProcessorPort processorPort = mock(PaymentProcessorPort.class);
        PaymentRepository paymentRepository = mock(PaymentRepository.class);

        HandleRedsysNotificationUseCase useCase =
                new HandleRedsysNotificationUseCase(processorPort, paymentRepository);

        String orderNumber = "1234ABCDEFGH";

        PaymentNotificationResult result = new PaymentNotificationResult(
                orderNumber,
                true,   // firma válida
                true,   // autorizado
                "0000"
        );

        when(processorPort.processNotification("v", "mp", "sig"))
                .thenReturn(result);

        Payment payment = Payment.createNew(
                PaymentId.newRandom(),
                OrderNumber.generate(new SecureRandom()),
                new BigDecimal("10.00"),
                "978"
        );

        when(paymentRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(payment));

        // when
        useCase.handleNotification("v", "mp", "sig");

        // then
        verify(paymentRepository).findByOrderNumber(orderNumber);
        verify(paymentRepository).save(payment);
    }

    /*
    firma invalida: no se llama al repositorio
    */
    @Test
    void handleNotification_invalidSignature_doesNotCallRepository() {
        // given
        PaymentProcessorPort processorPort = mock(PaymentProcessorPort.class);
        PaymentRepository paymentRepository = mock(PaymentRepository.class);

        HandleRedsysNotificationUseCase useCase =
                new HandleRedsysNotificationUseCase(processorPort, paymentRepository);

        PaymentNotificationResult result = new PaymentNotificationResult(
                "whatever",
                false, // firma inválida
                false,
                null
        );

        when(processorPort.processNotification("v", "mp", "sig"))
                .thenReturn(result);

        // when
        useCase.handleNotification("v", "mp", "sig");

        // then
        verifyNoInteractions(paymentRepository);
    }
}

