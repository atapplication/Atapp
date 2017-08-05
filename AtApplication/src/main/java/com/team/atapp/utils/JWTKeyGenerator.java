package com.team.atapp.utils;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.http.HttpStatus;

import com.team.atapp.dto.UserLoginDTO;
import com.team.atapp.exception.AtAppException;
import com.team.atapp.logger.AtLogger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTKeyGenerator {

	private static final AtLogger logger = AtLogger.getLogger(JWTKeyGenerator.class);
	
	public static void validateXToken(String xToken) throws AtAppException {
		if (xToken == null) {
			throw new AtAppException("Invalid XToken Value", HttpStatus.UNAUTHORIZED);
		}
	}

	public static UserLoginDTO createJWTAccessToken(String serviceInvokerKey, String id, String subject, long ttlMillis) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		logger.debug("nowMillis",nowMillis);
		logger.debug("nowMillisInSec",nowMillis/1000);
		
		Date now = new Date(nowMillis);
			

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(serviceInvokerKey);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		String issuerName = "SHAN";
		//String issuerName = SpringPropertiesUtil.getProperty(MightyAppConstants.TOKEN_ISSUER_KEY);
		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuerName)
				.signWith(signatureAlgorithm, signingKey);

		// if it has been specified, let's add the expiration
		UserLoginDTO dto=null;
		if (ttlMillis >= 0) {
			dto=new UserLoginDTO();
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			dto.setAccessTokenExpDate(exp);
			builder.setExpiration(exp);
		}
		//logger.debug("token as",builder.compact());
		
		// Builds the JWT and serializes it to a compact, URL-safe string
		dto.setApiToken(builder.compact());
		
		return dto;
	}
	
	
	public static UserLoginDTO createJWTBaseToken(String serviceInvokerKey, String id, String subject, long ttlMillis) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		logger.debug("nowMillis",nowMillis);
				
		Date now = new Date(nowMillis);
			

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(serviceInvokerKey);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		String issuerName = "SHAN";
		//String issuerName = SpringPropertiesUtil.getProperty(MightyAppConstants.TOKEN_ISSUER_KEY);
		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuerName)
				.signWith(signatureAlgorithm, signingKey);

		// if it has been specified, let's add the expiration
		UserLoginDTO dto=null;
		if (ttlMillis >= 0) {
			dto=new UserLoginDTO();
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			dto.setBaseTokenExpDate(exp);
			builder.setExpiration(exp);
		}
		//logger.debug("token as",builder.compact());
		
		// Builds the JWT and serializes it to a compact, URL-safe string
		dto.setBaseToken(builder.compact());
		
		return dto;
	}

	public static void validateJWTToken(String serviceInvokerKey, String jwt) throws AtAppException {

		// This line will throw an exception if it is not a signed JWS (as
		// expected)
		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(serviceInvokerKey))
					.parseClaimsJws(jwt).getBody();
			logger.info("ID: " + claims.getId());
			logger.info("Subject: " + claims.getSubject());
			logger.info("Issuer: " + claims.getIssuer());
			logger.info("Expiration: " + claims.getExpiration());
		} catch(Exception e) {
			throw new AtAppException("X-Access-token Value Expired", HttpStatus.UNAUTHORIZED);
		}
		
		
	}

	public static void main(String[] args) {
		UserLoginDTO token = createJWTAccessToken("MYSECRETKEY", "ID", "SUB", TimeUnit.DAYS.toMillis(60));
		//String xToken="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJNT0JJTEVfTE9HSU4iLCJpYXQiOjE0ODMwOTA4MzYsInN1YiI6Ik1PQklMRV9TRUNVUkUiLCJpc3MiOiJTSEFOIiwiZXhwIjoxNDgzMDkwODY2fQ.UfHLc4iyxIM9itZuLm04msfwjGcFP022ei9TmuOwJM4";
		//String token="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJJRCIsImlhdCI6MTQ4MzA5ODc2OSwic3ViIjoiU1VCIiwiaXNzIjoiU0hBTiIsImV4cCI6MTQ4MzA5ODgyOX0.JNrYO3VU1jQ4eo-Ws_3ECIJv1sWM_hikA2iAClAHu4Q";
		validateJWTToken("MYSECRETKEY", token.getApiToken());
	}

}
