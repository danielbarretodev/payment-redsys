package com.module.redsyspayment.infrastructure.tpv;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedsysSignatureService {

    private final String secretKey;
    private final RedsysService redsysService; // tu clase probada

    public RedsysSignatureService(
            @Value("${payment.redsys.secret-key}") String secretKey,
            RedsysService redsysService
    ) {
        this.secretKey = secretKey;
        this.redsysService = redsysService;
    }

    public String signRequest(String orderNumber, String merchantParameters) {
        // Igual que en Quarkus:
        try {
            return redsysService.createSignature(secretKey, orderNumber, merchantParameters);
        } catch (Exception e) {
            throw new RuntimeException("Error signing Redsys request", e);
        }
    }

    public boolean validateResponse(String merchantParameters, String receivedSignature) {
        // Igual que en Quarkus:
        String order = redsysService.extractOrderFromMerchantParameters(merchantParameters);
        try {
            return redsysService.validateSignature(secretKey, order, merchantParameters, receivedSignature);
        } catch (Exception e) {
            throw new RuntimeException("Error validating Redsys response", e);
        }
    }
}
