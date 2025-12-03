package com.module.redsyspayment.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.module.redsyspayment.domain.model.Payment;
import com.module.redsyspayment.domain.port.PaymentRepository;
import com.module.redsyspayment.infrastructure.mapper.PaymentEntityMapper;

import java.util.Optional;

@Repository
public class JpaPaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentJpaRepository jpaRepository;
    private final PaymentEntityMapper paymentEntityMapper;

   
    public JpaPaymentRepositoryAdapter(SpringDataPaymentJpaRepository jpaRepository, PaymentEntityMapper paymentEntityMapper) {
        this.jpaRepository = jpaRepository;
        this.paymentEntityMapper = paymentEntityMapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = paymentEntityMapper.toEntity(payment);
        PaymentEntity saved = jpaRepository.save(entity);
        return paymentEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException("paymentId cannot be null");
        }
        return jpaRepository.findById(paymentId).map(paymentEntityMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderNumber(String orderNumber) {
        return jpaRepository.findByOrderNumber(orderNumber)
                .map(paymentEntityMapper::toDomain);
    }
}
