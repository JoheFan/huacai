package com.huacai.security;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class JwtSecretValidator implements InitializingBean {

    private final JwtProperties jwtProperties;

    public JwtSecretValidator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void afterPropertiesSet() {
        JwtSecretSupport.decodeAndValidate(jwtProperties.getSecret());
    }
}
