package com.module.redsyspayment.infrastructure.mapper;


import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.module.redsyspayment.domain.model.Payment;
import com.module.redsyspayment.infrastructure.persistence.PaymentEntity;


@Mapper(componentModel = "spring")
public interface PaymentEntityMapper {

	@Mapping(target = "id", expression = "java(new com.module.redsyspayment.domain.model.PaymentId(entity.getId()))")
    @Mapping(target = "status", expression = "java(com.module.redsyspayment.domain.model.PaymentStatus.valueOf(entity.getStatus()))")
    Payment toDomain(PaymentEntity entity);

    @Mapping(target = "id", expression = "java(payment.getId().getValue())")
    @Mapping(target = "status", expression = "java(payment.getStatus().name())")
    PaymentEntity toEntity(Payment payment);


    List<Payment> toDomainList(List<PaymentEntity> entities);
    List<PaymentEntity> toEntityList(List<Payment> payments);

}
