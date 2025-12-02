package com.module.redsyspayment.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.module.redsyspayment.domain.model.Payment;
import com.module.redsyspayment.domain.port.PaymentRepository;
import com.module.redsyspayment.infrastructure.mapper.PaymentEntitySimpleMapper;

import java.util.Optional;

@Repository
public class JpaPaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentJpaRepository jpaRepository;
    private final PaymentEntitySimpleMapper paymentEntityMapper;

   
    public JpaPaymentRepositoryAdapter(SpringDataPaymentJpaRepository jpaRepository, PaymentEntitySimpleMapper paymentEntityMapper) {
        this.jpaRepository = jpaRepository;
        this.paymentEntityMapper = paymentEntityMapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = paymentEntityMapper.toEntity(payment);
        PaymentEntity saved = jpaRepository.save(entity != null ? entity : new PaymentEntity());
        return paymentEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        // El repositorio espera @NonNull String, así que forzamos el valor
        return jpaRepository.findById(paymentId != null ? paymentId : "")
                .map(paymentEntityMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderNumber(String orderNumber) {
        return jpaRepository.findByOrderNumber(orderNumber)
                .map(paymentEntityMapper::toDomain);
    }
}
