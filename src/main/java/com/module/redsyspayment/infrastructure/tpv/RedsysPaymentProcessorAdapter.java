package com.module.redsyspayment.infrastructure.tpv;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        params.put("Ds_Merchant_Amount", amountToCents(payment.getAmount()));
        params.put("Ds_Merchant_Order", payment.getOrderNumber());
        params.put("Ds_Merchant_MerchantCode", merchantCode);
        params.put("Ds_Merchant_Currency", currency);
        params.put("Ds_Merchant_TransactionType", transactionType);
        params.put("Ds_Merchant_Terminal", terminal);
        params.put("Ds_Merchant_UrlOK", merchantUrlOk);
        params.put("Ds_Merchant_UrlKO", merchantUrlKo);
        params.put("Ds_Merchant_MerchantURL", merchantUrlNotification);
        // Si quieres pagar solo con tarjeta (C = card)
        // params.put("Ds_Merchant_PayMethods", "C");

        String json = objectMapper.writeValueAsString(params);
        String dsMerchantParameters = Base64.getEncoder()
                .encodeToString(json.getBytes(StandardCharsets.UTF_8));

        String dsSignatureVersion = "HMAC_SHA256_V1";
        String dsSignature = signatureService.signRequest(
                payment.getOrderNumber(),
                dsMerchantParameters
        );

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
    public PaymentNotificationResult processNotification(String signatureVersion,
                                                        String merchantParameters,
                                                        String signature) {
        try {
            boolean valid = signatureService.validateResponse(merchantParameters, signature);

            byte[] decoded = Base64.getDecoder().decode(merchantParameters);
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
            return new PaymentNotificationResult(null, false, false, null);
        }
    }


    private String amountToCents(BigDecimal amount) {
        int cents = amount
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .intValueExact();
        return Integer.toString(cents);
    }

    private boolean isAuthorized(String dsResponse) {
        if (dsResponse == null) return false;
        int code = Integer.parseInt(dsResponse);
        return code >= 0 && code < 100; // 0-99 = autorizado
    }
}
