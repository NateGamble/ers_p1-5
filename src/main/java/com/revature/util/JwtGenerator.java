package com.revature.util;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import com.revature.models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Generates a JWT with 15 minute expiration for a given {@code User}
 * JWT stores user id and username
 */
public class JwtGenerator {
    private static final Logger logger = LogManager.getLogger(JwtGenerator.class);

    // Common security term for thing is subject
    public static String createJwt(User subject) {
        // Pick algorithm
        SignatureAlgorithm sigAlg = SignatureAlgorithm.HS512;
        // get current time to set token expiry time
        long now = System.currentTimeMillis();

        File temp = new File("src/main/resources/application.properties");
        String key = null;

        if (temp.exists()) {
            try {
                Properties props = new Properties();

                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                InputStream input = loader.getResourceAsStream("application.properties");

                props.load(input);
                key = props.getProperty("key");
            } catch (Exception e) {
                logger.error(e.getStackTrace());
            }
        } else {
            key = System.getProperty("key");
        }

        JwtBuilder builder = Jwts.builder()
                                .setId(Integer.toString(subject.getUserId()))
                                .setSubject(subject.getUsername())
                                .setIssuer("revature")
                                // 15 minute expiration
                                .setExpiration(new Date(now + 900000))
                                // key is like a salt, should be stored in .properties file
                                .signWith(sigAlg, key);

        // generates actual string for jwt
        return builder.compact();
    }
}
