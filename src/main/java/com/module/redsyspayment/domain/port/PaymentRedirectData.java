package com.module.redsyspayment.domain.port;

public record PaymentRedirectData(String tpvUrl,
                                  String dsSignatureVersion,
                                  String dsMerchantParameters,
                                  String dsSignature) {
}
