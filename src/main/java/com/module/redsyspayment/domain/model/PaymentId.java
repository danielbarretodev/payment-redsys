package com.module.redsyspayment.domain.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentId {

    private String value;

      public static PaymentId newRandom() {
        return new PaymentId(UUID.randomUUID().toString());
    }
}