package com.huacai.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class JwtSecretValidatorTest {

    @Test
    void acceptsBase64EncodedSecretWithAtLeast32Bytes() throws Exception {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(Base64.getEncoder().encodeToString("12345678901234567890123456789012".getBytes()));

        JwtSecretValidator validator = new JwtSecretValidator(properties);

        assertThatCode(validator::afterPropertiesSet).doesNotThrowAnyException();
    }

    @Test
    void rejectsBlankSecret() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("  ");

        JwtSecretValidator validator = new JwtSecretValidator(properties);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("HUACAI_JWT_SECRET");
    }

    @Test
    void rejectsShortDecodedSecret() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(Base64.getEncoder().encodeToString("too-short".getBytes()));

        JwtSecretValidator validator = new JwtSecretValidator(properties);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 bytes");
    }
}
