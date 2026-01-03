package com.app.gateway.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT utility component for validating and extracting claims from JWT tokens.
 * Provides methods for token validation and claim extraction.
 */
@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String jwtSecret;

	/**
	 * Get the signing key for JWT validation.
	 * Uses the same approach as auth-service: plain text UTF-8 conversion.
	 */
	private Key getSigningKey() {
	    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Extract all claims from the JWT token.
	 */
	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	/**
	 * Check if password change is forced for the user.
	 */
    public boolean getForcePwdChange(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Boolean value = claims.get("forcePwdChange", Boolean.class);
        return value != null && value;
    }

	/**
	 * Validate JWT token signature and expiration.
	 */
	public boolean validate(String token) {
		try {
			extractAllClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Extract username (subject) from JWT token.
	 */
	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	/**
	 * Extract roles from JWT token claims.
	 * The auth-service stores roles as a single "role" claim with format like "ROLE_USER" or "USER".
	 */
	public List<String> extractRoles(String token) {
		Claims claims = extractAllClaims(token);

		// Try to get single role claim first (auth-service format)
		Object roleObj = claims.get("role");
		if (roleObj != null) {
			String roleStr = roleObj.toString().trim();
			if (!roleStr.isEmpty()) {
				// Ensure it has ROLE_ prefix
				String role = roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr;
				return List.of(role);
			}
		}

		// Fallback to plural "roles" claim (comma-separated, legacy format)
		Object rolesObj = claims.get("roles");
		if (rolesObj == null)
			return List.of();

		String s = rolesObj.toString().replace("[", "").replace("]", "").replace(" ", "");

		if (s.isEmpty())
			return List.of();

		return Arrays.stream(s.split(","))
				.map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
				.collect(Collectors.toList());
	}
}
