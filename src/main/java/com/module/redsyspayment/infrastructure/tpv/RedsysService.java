package com.module.redsyspayment.infrastructure.tpv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class RedsysService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedsysService.class);

    public String createSignature(String keyBase64, String order, String merchantParameters) throws Exception {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] orderBytes = order.getBytes("UTF-8");
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "DESede");
            Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
            int padding = 8 - (orderBytes.length % 8);
            byte[] orderPadded = new byte[orderBytes.length + padding];
            System.arraycopy(orderBytes, 0, orderPadded, 0, orderBytes.length);
            IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] keyOper = cipher.doFinal(orderPadded);
            SecretKeySpec sk = new SecretKeySpec(keyOper, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(sk);
            byte[] hmac = mac.doFinal(merchantParameters.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hmac);
        } catch (Exception e) {
            LOGGER.error("[RedsysService] Error generando la firma: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean validateSignature(String keyBase64, String order, String merchantParameters, String signature) throws Exception {
        String expected = createSignature(keyBase64, order, merchantParameters);
        // Normalizar la firma recibida de Base64 URL-safe a estándar
        String signatureNormalized = signature != null ? signature.replace('-', '+').replace('_', '/') : null;
        boolean valid = signature != null && (signature.equals(expected) || expected.equals(signatureNormalized));
        if (!valid) {
            LOGGER.error("[RedsysService] Firma inválida. Esperada: {}, Recibida: {} (Normalizada: {})", expected, signature, signatureNormalized);
        } else {
            LOGGER.info("[RedsysService] Firma válida para notificación de Redsys");
        }
        return valid;
    }

    public JsonNode decodeMerchantParameters(String merchantParameters) throws Exception {
        String jsonData = new String(Base64.getDecoder().decode(merchantParameters), StandardCharsets.UTF_8);
        LOGGER.debug("[RedsysService] MerchantParameters JSON: {}", jsonData);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonData);
    }

    public String extractOrderFromMerchantParameters(String merchantParameters) {
        try {
            JsonNode params = decodeMerchantParameters(merchantParameters);
            return params.path("Ds_Order").asText();
        } catch (Exception e) {
            LOGGER.error("[RedsysService] Error extrayendo Ds_Order: {}", e.getMessage(), e);
            return null;
        }
    }
} 