package com.module.redsyspayment.infrastructure.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.module.redsyspayment.domain.model.Payment;
import com.module.redsyspayment.infrastructure.persistence.PaymentEntity;


@Mapper(componentModel = "spring")
public interface PaymentEntitySimpleMapper {
	@Mapping(target = "id", expression = "java(new com.module.redsyspayment.domain.model.PaymentId(entity.getId()))")
	@Mapping(target = "status", expression = "java(entity.getStatus() != null ? com.module.redsyspayment.domain.model.PaymentStatus.valueOf(entity.getStatus()) : com.module.redsyspayment.domain.model.PaymentStatus.PENDING)")
	Payment toDomain(PaymentEntity entity);

	@Mapping(target = "id", expression = "java(payment.getId() != null ? payment.getId().getValue() : null)")
	@Mapping(target = "status", expression = "java(payment.getStatus() != null ? payment.getStatus().name() : com.module.redsyspayment.domain.model.PaymentStatus.PENDING.name())")
	@Mapping(target = "createdAt", expression = "java(payment.getCreatedAt() != null ? payment.getCreatedAt() : java.time.Instant.now())")
	@Mapping(target = "updatedAt", expression = "java(payment.getUpdatedAt() != null ? payment.getUpdatedAt() : java.time.Instant.now())")
	PaymentEntity toEntity(Payment payment);

	java.util.List<Payment> toDomainList(java.util.List<PaymentEntity> entities);
	java.util.List<PaymentEntity> toEntityList(java.util.List<Payment> payments);

}
