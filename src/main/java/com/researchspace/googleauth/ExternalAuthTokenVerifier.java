package com.researchspace.googleauth;

import java.util.Optional;

/**
 * Abstracts external verification of tokens issued by 3rd party identifiers. 
 *
 */
public interface ExternalAuthTokenVerifier {

	/**
	 * Validates authentication token against external authenticator and returns a user profile, or <code>null</code>
	 *  if no such profile could be returned.
   */
	 Optional<ExternalProfile> verify (String clientId, String idTokenString);
}
