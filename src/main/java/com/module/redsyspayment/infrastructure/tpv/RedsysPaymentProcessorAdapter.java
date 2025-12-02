package com.module.redsyspayment.infrastructure.tpv;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.redsyspayment.domain.model.Payment;
import com.module.redsyspayment.domain.port.PaymentNotificationResult;
import com.module.redsyspayment.domain.port.PaymentProcessorPort;
import com.module.redsyspayment.domain.port.PaymentRedirectData;

@Component
public class RedsysPaymentProcessorAdapter implements PaymentProcessorPort {

    private final String merchantCode;
    private final String terminal;
    private final String currency; // "978"
    private final String transactionType; // "0"
    private final String merchantUrlOk;
    private final String merchantUrlKo;
    private final String merchantUrlNotification;
    private final String redsysUrl;
    private final RedsysSignatureService signatureService;
    private final ObjectMapper objectMapper;

    public RedsysPaymentProcessorAdapter(
            @Value("${payment.redsys.merchant-code}") String merchantCode,
            @Value("${payment.redsys.terminal}") String terminal,
            @Value("${payment.redsys.currency}") String currency,
            @Value("${payment.redsys.transaction-type}") String transactionType,
            @Value("${payment.redsys.url-ok}") String merchantUrlOk,
            @Value("${payment.redsys.url-ko}") String merchantUrlKo,
            @Value("${payment.redsys.url-notification}") String merchantUrlNotification,
            @Value("${payment.redsys.tpv-url}") String redsysUrl,
            RedsysSignatureService signatureService,
            ObjectMapper objectMapper
    ) {
        this.merchantCode = merchantCode;
        this.terminal = terminal;
        this.currency = currency;
        this.transactionType = transactionType;
        this.merchantUrlOk = merchantUrlOk;
        this.merchantUrlKo = merchantUrlKo;
        this.merchantUrlNotification = merchantUrlNotification;
        this.redsysUrl = redsysUrl;
        this.signatureService = signatureService;
        this.objectMapper = objectMapper;
    }


    @Override
    public PaymentRedirectData initPayment(Payment payment) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("DS_MERCHANT_AMOUNT", amountToCents(payment.getAmount()));
            params.put("DS_MERCHANT_ORDER", payment.getOrderNumber());
            params.put("DS_MERCHANT_MERCHANTCODE", merchantCode);
            params.put("DS_MERCHANT_CURRENCY", currency);
            params.put("DS_MERCHANT_TRANSACTIONTYPE", transactionType);
            params.put("DS_MERCHANT_TERMINAL", terminal);
            params.put("DS_MERCHANT_URLOK", merchantUrlOk);
            params.put("DS_MERCHANT_URLKO", merchantUrlKo);
            params.put("DS_MERCHANT_MERCHANTURL", merchantUrlNotification);

            // 1. JSON compacto
            String json = objectMapper.writeValueAsString(params);

            // 2. Base64
            String dsMerchantParameters = java.util.Base64.getEncoder()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));

            // 3. Firma
            String dsSignatureVersion = "HMAC_SHA256_V1";
            String dsSignature = signatureService.signRequest(payment.getOrderNumber(), dsMerchantParameters);

            return new PaymentRedirectData(
                    redsysUrl,
                    dsSignatureVersion,
                    dsMerchantParameters,
                    dsSignature
            );
        } catch (Exception e) {
            throw new RuntimeException("Error generating Redsys payment", e);
        }
    }

    @Override
    public PaymentNotificationResult processNotification(String merchantParameters,
                                                         String signature,
                                                         String signatureVersion) {
        try {
            // 1. Validar firma de respuesta
            boolean valid = signatureService.validateResponse(merchantParameters, signature);

            // 2. Decodificar MerchantParameters
            byte[] decoded = java.util.Base64.getDecoder().decode(merchantParameters);
            String json = new String(decoded, StandardCharsets.UTF_8);

            Map<String, Object> params = objectMapper.readValue(json, Map.class);

            String dsOrder = (String) params.get("Ds_Order");
            String dsResponse = (String) params.get("Ds_Response");

            boolean authorized = isAuthorized(dsResponse);

            return new PaymentNotificationResult(
                    dsOrder,
                    valid,
                    authorized,
                    dsResponse
            );
        } catch (Exception e) {
            // En caso de error, devolvemos firma no válida para que el caso de uso no toque el pago
            return new PaymentNotificationResult(null, false, false, null);
        }
    }

    private String amountToCents(BigDecimal amount) {
        return amount.movePointRight(2).toBigInteger().toString();
    }

    private boolean isAuthorized(String dsResponse) {
        if (dsResponse == null) return false;
        int code = Integer.parseInt(dsResponse);
        return code >= 0 && code < 100; // 0-99 = autorizado
    }
}
