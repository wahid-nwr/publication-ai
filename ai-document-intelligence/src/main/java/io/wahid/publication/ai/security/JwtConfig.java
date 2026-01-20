package io.wahid.publication.ai.security;

import com.nimbusds.jose.JWSAlgorithm;

public class JwtConfig {

    private final String jwksUri;
    private final String issuer;
    private final JWSAlgorithm jwsAlgorithm;

    public JwtConfig(String jwksUri, String issuer) {
        this.jwksUri = jwksUri;
        this.issuer = issuer;
        this.jwsAlgorithm = JWSAlgorithm.RS256; // Google Identity Platform uses RS256
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public String getIssuer() {
        return issuer;
    }

    public JWSAlgorithm getJwsAlgorithm() {
        return jwsAlgorithm;
    }
}
