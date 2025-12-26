package com.api.autenticacao.infra.exceptions;

import java.time.Instant;

public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        Instant timestamp
) {}
