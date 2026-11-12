package com.nawasenahost.authservice.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class Jwks {

    private static final Logger log = LoggerFactory.getLogger(Jwks.class);

    // Base64 of the DER encoded keys (PKCS#8 private, X.509 public), see docs/DEPLOYMENT.md
    // When they are set every auth-service instance signs with the same key, so the
    // service tokens stay valid behind a load balancer and after a restart.
    @Value("${jwt.rsa.private-key:}")
    private String privateKeyBase64;

    @Value("${jwt.rsa.public-key:}")
    private String publicKeyBase64;

    @Bean
    public RSAKey rsaKey() throws Exception {

        RSAPublicKey publicKey;
        RSAPrivateKey privateKey;
        String keyId;

        if (!privateKeyBase64.isBlank() && !publicKeyBase64.isBlank()) {

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            privateKey = (RSAPrivateKey) keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64.trim())));
            publicKey = (RSAPublicKey) keyFactory.generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64.trim())));

            // fixed key id, derived from the key itself so every instance agrees on it
            keyId = Integer.toHexString(publicKey.getModulus().hashCode());
        } else {

            log.warn("JWT_RSA_PRIVATE_KEY / JWT_RSA_PUBLIC_KEY are not set, generating a temporary key. " +
                    "Service tokens will break when auth-service restarts or runs more than one instance.");

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);

            KeyPair keyPair = generator.generateKeyPair();

            publicKey = (RSAPublicKey) keyPair.getPublic();
            privateKey = (RSAPrivateKey) keyPair.getPrivate();
            keyId = java.util.UUID.randomUUID().toString();
        }

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {

        JWKSet jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, securityContext) ->
                jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}
