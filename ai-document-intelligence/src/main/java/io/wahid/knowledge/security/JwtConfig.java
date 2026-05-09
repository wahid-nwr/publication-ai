package io.wahid.knowledge.security;

import com.nimbusds.jose.JWSAlgorithm;

public class JwtConfig {

    private final String jwksUri;
    private final String issuer;
    private String audience;
    private final JWSAlgorithm jwsAlgorithm;

    public JwtConfig(String jwksUri, String issuer, String audience) {
        this.jwksUri = jwksUri;
        this.issuer = issuer;
        this.audience = audience;
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

    public String getAudience() {
        return audience;
    }
}
