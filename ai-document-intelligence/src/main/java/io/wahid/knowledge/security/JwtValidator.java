package io.wahid.knowledge.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import java.net.URL;

public class JwtValidator {

    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public JwtValidator(String jwkUrl) throws Exception {

        JWKSource<SecurityContext> keySource =
                new RemoteJWKSet<>(new URL(jwkUrl));

        jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(
                        JWSAlgorithm.RS256, keySource);

        jwtProcessor.setJWSKeySelector(keySelector);
    }

    public JWTClaimsSet validate(String token) throws Exception {
        return jwtProcessor.process(token, null);
    }
}
