package io.wahid.publication.ai.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FirebaseInit {

    public static void initialize() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        try {
            System.out.println(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
        }
    }
}
