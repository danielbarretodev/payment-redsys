package com.module.redsyspayment.domain.model;

import java.security.SecureRandom;

import lombok.Data;

@Data
public class OrderNumber {
    private final String value;

    private OrderNumber(String value) {
        validate(value);
        this.value = value;
    }

    public static OrderNumber generate(SecureRandom random) {
        // lógica actual de generación
         // 4 dígitos numéricos (0000 - 9999)
        String first4 = String.format("%04d", random.nextInt(10000));

        // 8 alfanuméricos
        String alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int idx = random.nextInt(alphanum.length());
            sb.append(alphanum.charAt(idx));
        }

        String order = first4 + sb.toString();

        // Validación extra por si acaso
        if (!order.matches("^[0-9]{4}[A-Za-z0-9]{8}$")) {
            throw new IllegalStateException("Generated Ds_Order invalid: " + order);
        }

        return new OrderNumber(order);
    }

    private void validate(String value) {
        if (!value.matches("^[0-9]{4}[A-Za-z0-9]{8}$")) {
            throw new IllegalArgumentException("Invalid Ds_Order: " + value);
        }
    }

    public String value() { return value; }
}
