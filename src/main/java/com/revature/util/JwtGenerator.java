package com.revature.util;

import java.util.Date;

import com.revature.models.User;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtGenerator {
    // Common security term for thing is subject
    public static String createJwt(User subject) {
        // Pick algorithm
        SignatureAlgorithm sigAlg = SignatureAlgorithm.HS512;
        // get current time to set token expiry time
        long now = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                                .setId(Integer.toString(subject.getUserId()))
                                .setSubject(subject.getUsername())
                                .setIssuer("revature")
                                // Can add multiple claims
                                .claim("firstName", subject.getFirstname())
                                // 5 minute expiration
                                .setExpiration(new Date(now + 300000))
                                // key is like a salt, should be stored in .properties file
                                .signWith(sigAlg, "super-secret-key");

        // generates actual string for jwt
        return builder.compact();
    }
}
