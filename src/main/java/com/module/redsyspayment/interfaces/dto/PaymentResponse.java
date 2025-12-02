package com.module.redsyspayment.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "PaymentResponse",
    description = """
        Response containing all the required fields to redirect the customer \
        to the Redsys TPV payment gateway.
        
        The frontend must create a hidden form and issue a POST request \
        to the TPV URL using these parameters.
        """
)
public record PaymentResponse(

        @Schema(
            description = "URL of Redsys TPV where the frontend must POST the form",
            example = "https://sis-t.redsys.es:25443/sis/realizarPago"
        )
        String tpvUrl,

        @Schema(
            description = "Signature version used by Redsys",
            example = "HMAC_SHA256_V1"
        )
        String dsSignatureVersion,

        @Schema(
            description = "Base64-encoded JSON payload expected by Redsys",
            example = "eyJEU19NRVJDSEFOVF9BTU9VTlQiOiI0OTkwIiwiRFNfTUVSQ0hBTlRfT1JERVIiOiIxMjM0NTY3ODkwIi..."
        )
        String dsMerchantParameters,

        @Schema(
            description = "Base64-encoded HMAC SHA256 signature",
            example = "x9Q5aL1H2aF3Kjd... (truncated)"
        )
        String dsSignature
) {}
