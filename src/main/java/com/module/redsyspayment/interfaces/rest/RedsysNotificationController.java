package com.module.redsyspayment.interfaces.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module.redsyspayment.application.HandleRedsysNotificationUseCase;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
@RequestMapping("/redsys")
public class RedsysNotificationController {

    private final HandleRedsysNotificationUseCase handleRedsysNotificationUseCase;

    public RedsysNotificationController(HandleRedsysNotificationUseCase handleRedsysNotificationUseCase) {
        this.handleRedsysNotificationUseCase = handleRedsysNotificationUseCase;
    }

    @PostMapping(
            value = "/notification",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<String> paymentNotification(
            @RequestParam("Ds_SignatureVersion") String signatureVersion,
            @RequestParam("Ds_MerchantParameters") String merchantParameters,
            @RequestParam("Ds_Signature") String signature
    ) {
        handleRedsysNotificationUseCase.handleNotification(
                signatureVersion,
                merchantParameters,
                signature
        );

        return ResponseEntity.ok("OK");
    }
}

