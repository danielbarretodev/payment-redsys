package com.module.redsyspayment.domain.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;



@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    private PaymentId id;
    private String orderNumber;  // Ds_Order
    private BigDecimal amount;
    private String currency;     // "978" para EUR
    private PaymentStatus status;
    private Instant createdAt;
    private Instant updatedAt;

   
    public void markRedirectedToTpv() {
        this.status = PaymentStatus.REDIRECTED_TO_TPV;
        this.updatedAt = Instant.now();
    }

    public void authorize() {
        this.status = PaymentStatus.AUTHORIZED;
        this.updatedAt = Instant.now();
    }

    public void deny() {
        this.status = PaymentStatus.DENIED;
        this.updatedAt = Instant.now();
    }

    public void error() {
        this.status = PaymentStatus.ERROR;
        this.updatedAt = Instant.now();
    }

    // getters generados por Lombok
}