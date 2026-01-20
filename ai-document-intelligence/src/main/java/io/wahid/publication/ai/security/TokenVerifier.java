package io.wahid.publication.ai.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public class TokenVerifier {

    public static FirebaseToken verify(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;

        String idToken = authHeader.substring("Bearer ".length());

        try {
            // Verify Firebase ID token
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (Exception e) {
            return null;
        }
    }
}
