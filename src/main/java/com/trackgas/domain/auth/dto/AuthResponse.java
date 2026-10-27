package com.trackgas.domain.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId,
        String name,
        String email,
        String locale
) {}
