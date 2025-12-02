package com.module.redsyspayment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.module.redsyspayment.application.CreatePaymentUseCase;
import com.module.redsyspayment.application.HandleRedsysNotificationUseCase;
import com.module.redsyspayment.domain.port.PaymentProcessorPort;
import com.module.redsyspayment.domain.port.PaymentRepository;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreatePaymentUseCase createPaymentUseCase(PaymentRepository paymentRepository,
                                                     PaymentProcessorPort paymentProcessorPort) {
        return new CreatePaymentUseCase(paymentRepository, paymentProcessorPort);
    }

    @Bean
    public HandleRedsysNotificationUseCase handleRedsysNotificationUseCase(PaymentRepository paymentRepository,
                                                                           PaymentProcessorPort paymentProcessorPort) {
        return new HandleRedsysNotificationUseCase(paymentProcessorPort, paymentRepository);
    }
}
