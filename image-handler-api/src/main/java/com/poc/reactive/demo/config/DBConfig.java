package com.poc.reactive.demo.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class DBConfig {

	@Bean
	public Firestore firestore() throws IOException {
		// Use environment variables to check if the emulator is running
		String firestoreHost = System.getenv("APP_FIRESTORE_EMULATOR");

		FirestoreOptions firestoreOptions;

		if (firestoreHost != null) {
			System.out.println("using emulator " + firestoreHost);
			// Connect to Firestore emulator
			firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder().setEmulatorHost(firestoreHost)
					.setProjectId("event-driven-sample-project").build();
		} else {
			// Connect to the actual Firestore service
			GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials)
					.setProjectId("event-driven-sample-project").build();
			FirebaseApp.initializeApp(options);

			firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder().setCredentials(credentials)
					.setProjectId("event-driven-sample-project").build();
		}

		return firestoreOptions.getService();
	}

}
