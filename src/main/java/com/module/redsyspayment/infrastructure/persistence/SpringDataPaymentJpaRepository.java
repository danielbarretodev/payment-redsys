package com.module.redsyspayment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataPaymentJpaRepository extends JpaRepository<PaymentEntity, String> {

    Optional<PaymentEntity> findByOrderNumber(String orderNumber);
}
