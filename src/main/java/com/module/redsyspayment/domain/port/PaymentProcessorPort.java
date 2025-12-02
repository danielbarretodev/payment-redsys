package com.module.redsyspayment.domain.port;

import com.module.redsyspayment.domain.model.Payment;

public interface PaymentProcessorPort{

    /**
     * Inicia un pago con el proveedor (Redsys en nuestro caso)
     * y devuelve la URL o datos necesarios para redirigir al TPV.
     */
    PaymentRedirectData initPayment(Payment payment);

    /**
     * Procesa una notificación asíncrona del TPV (Redsys).
     * Devuelve el Ds_Order para que el caso de uso sepa qué Payment actualizar.
     */
    PaymentNotificationResult processNotification(String merchantParameters,
                                                  String signature,
                                                  String signatureVersion);
}
