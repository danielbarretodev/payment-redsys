package com.module.redsyspayment.interfaces.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.module.redsyspayment.application.CreatePaymentUseCase;
import com.module.redsyspayment.domain.port.PaymentRedirectData;
import com.module.redsyspayment.interfaces.dto.PaymentRequest;
import com.module.redsyspayment.interfaces.dto.PaymentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("${api.base-path}/payments")
@Tag(name = "Payments", description = "Operations related to payment creation and TPV redirection")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;

    public PaymentController(CreatePaymentUseCase createPaymentUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
    }

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Create a new payment",
        description = """
            Creates a new payment in PENDING state and returns all the necessary data \
            to redirect the user to Redsys TPV (payment gateway).
            
            The frontend must build an HTML form and submit it (POST) to the TPV URL \
            using the parameters returned in this response.
            """
    )
    @ApiResponse(
        responseCode = "201",
        description = "Payment successfully created",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = PaymentResponse.class)
        )
    )
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public PaymentResponse createPayment(@Valid @RequestBody PaymentRequest request) {

        PaymentRedirectData redirectData = createPaymentUseCase.createPayment(
                request.amount(),
                request.currency() != null ? request.currency() : "978" // EUR
        );

        return new PaymentResponse(
                redirectData.tpvUrl(),
                redirectData.dsSignatureVersion(),
                redirectData.dsMerchantParameters(),
                redirectData.dsSignature()
        );
    }
}
