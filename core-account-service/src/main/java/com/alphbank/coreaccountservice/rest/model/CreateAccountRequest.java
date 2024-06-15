package com.alphbank.coreaccountservice.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Locale;
import java.util.UUID;

public record CreateAccountRequest (
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID customerId,
        @Schema(example = "sv_SE", requiredMode = Schema.RequiredMode.REQUIRED)
        Locale locale) {
}
