package com.module.redsyspayment.application;

import com.module.redsyspayment.domain.model.Payment;
import com.module.redsyspayment.domain.port.PaymentProcessorPort;
import com.module.redsyspayment.domain.port.PaymentRedirectData;
import com.module.redsyspayment.domain.port.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CreatePaymentUseCaseTest {

    @Test
    /*
    comprobamos que se orquesta bien el flujo
    se llama a save
    se llama al puerto
    se vuelve a llamar a save 
    */
    void createPayment_callsRepositoryAndProcessor_andReturnsRedirectData() {
        // given
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        PaymentProcessorPort paymentProcessorPort = mock(PaymentProcessorPort.class);

        CreatePaymentUseCase useCase = new CreatePaymentUseCase(paymentRepository, paymentProcessorPort);

        BigDecimal amount = new BigDecimal("49.90");
        String currency = "978";

        PaymentRedirectData redirectData = new PaymentRedirectData(
                "https://tpv.test",
                "HMAC_SHA256_V1",
                "params",
                "signature"
        );

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentProcessorPort.initPayment(any(Payment.class)))
                .thenReturn(redirectData);

        // when
        PaymentRedirectData result = useCase.createPayment(amount, currency);

        // then
        assertThat(result).isEqualTo(redirectData);
        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(paymentProcessorPort).initPayment(any(Payment.class));
    }
}
