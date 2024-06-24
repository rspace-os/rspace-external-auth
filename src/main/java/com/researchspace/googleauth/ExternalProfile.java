package com.researchspace.googleauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalProfile {

	String email, name, pictureUrl, locale, familyName, givenName;

}
