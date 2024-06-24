package com.researchspace.googleauth.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.researchspace.googleauth.ExternalAuthTokenVerifier;
import com.researchspace.googleauth.ExternalProfile;
import com.researchspace.googleauth.GoogleAuth;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleTokenVerifier implements ExternalAuthTokenVerifier {

	private static final String GOOGLE_TOKEN_VALIDATION_ENDPOINT = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=";
	private static final String ACCOUNTS_GOOGLE_COM = "accounts.google.com";

	public Optional<ExternalProfile> verify(String clientId, String idTokenString) {
		// code taken from Google example code
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(),
				new JacksonFactory()).setAudience(Collections.singletonList(clientId))
						.setIssuer(ACCOUNTS_GOOGLE_COM).build();

		// (Receive idTokenString by HTTPS POST)

		GoogleIdToken idToken = null;

		try {
			idToken = verifier.verify(idTokenString);
			if (idToken != null) {
				Payload payload = idToken.getPayload();
				// Print user identifier
				String userId = payload.getSubject();
				log.info("User ID: {}", userId);
				return Optional.of(new ExternalProfile(payload.getEmail(), (String) payload.get("name"),
						(String) payload.get("picture"), (String) payload.get("locale"),
						(String) payload.get("family_name"), (String) payload.get("given_name")));
			} else {
				log.warn("Invalid ID token: {}", idTokenString);
				log.info("Verifying by REST call");
				return verifyByRest(clientId, idTokenString);
			}
		} catch (GeneralSecurityException | IOException e) {
			log.error("Error verifying token {}:{}", idToken, e.getMessage());
		}
		return Optional.empty();

	}
	
	public Optional<ExternalProfile> verifyByRest(String clientId, String idTokenString) {
		RestTemplate rt = new RestTemplate();
		String url = GOOGLE_TOKEN_VALIDATION_ENDPOINT + idTokenString;
		ResponseEntity<GoogleAuth> resp = rt.getForEntity(url, GoogleAuth.class);
		GoogleAuth entity = resp.getBody();
		if(!resp.getStatusCode().equals(HttpStatus.OK) || !clientId.equals(entity.getAzp())) {
			log.warn("Bad response: {}", entity);
			return Optional.empty();
		}
		return Optional.of(new ExternalProfile(entity.getEmail(), entity.getName(),
				entity.getPicture(), entity.getLocale(),
				entity.getFamily_name(), entity.getGiven_name()));	
	}
}
