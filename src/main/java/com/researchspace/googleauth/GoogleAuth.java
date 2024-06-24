package com.researchspace.googleauth;

import lombok.Data;

@Data
public class GoogleAuth {
	private String iss,
	 at_hash,
	 aud,
	 sub,
	 email_verified,
	 azp,
	 email,
	 iat,
	 exp,
	 name,
	 picture,
	 given_name,
	 family_name,
	 locale,
	 alg,
	 kid;
}
