/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.common.util;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Ryan Heaton
 * @author Dave Syer
 */
public abstract class OAuth2Utils {

	/**
	 * Constant to use while parsing and formatting parameter maps for OAuth2 requests
	 */
	public static final String CLIENT_ID = "client_id";

	/**
	 * Constant to use while parsing and formatting parameter maps for OAuth2 requests
	 */
	public static final String STATE = "state";

	/**
	 * Constant to use while parsing and formatting parameter maps for OAuth2 requests
	 */
	public static final String SCOPE = "scope";

	/**
	 * Constant to use while parsing and formatting parameter maps for OAuth2 requests
	 */
	public static final String REDIRECT_URI = "redirect_uri";

	/**
	 * Constant to use while parsing and formatting parameter maps for OAuth2 requests
	 */
	public static final String RESPONSE_TYPE = "response_type";

	/**
	 * Constant to use while parsing and formatting parameter maps for OAuth2 requests
	 */
	public static final String USER_OAUTH_APPROVAL = "user_oauth_approval";

	/**
	 * Constant to use as a prefix for scope approval
	 */
	public static final String SCOPE_PREFIX = "scope.";

	/**
	 * Constant to use while parsing and formatting parameter maps for OAuth2 requests
	 */
	public static final String GRANT_TYPE = "grant_type";

	/**
	 * Parses a string parameter value into a set of strings.
	 * 
	 * @param values The values of the set.
	 * @return The set.
	 */
	public static Set<String> parseParameterList(String values) {
		Set<String> result = new TreeSet<String>();
		if (values != null && values.trim().length() > 0) {
			// the spec says the scope is separated by spaces
			String[] tokens = values.split("[\\s+]");
			result.addAll(Arrays.asList(tokens));
		}
		return result;
	}
}
