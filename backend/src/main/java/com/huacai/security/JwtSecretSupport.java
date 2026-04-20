package com.huacai.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;

final class JwtSecretSupport {

    private JwtSecretSupport() {
    }

    static byte[] decodeAndValidate(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("HUACAI_JWT_SECRET is required and must be a Base64 value.");
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException | DecodingException exception) {
            throw new IllegalStateException("HUACAI_JWT_SECRET must be valid Base64.", exception);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("HUACAI_JWT_SECRET must decode to at least 32 bytes.");
        }
        return keyBytes;
    }
}
