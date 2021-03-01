package com.revature.util;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.revature.dtos.Principal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtParser {
    private static final Logger logger = LogManager.getLogger(JwtParser.class);

    public static void checkToken(HttpServletRequest req) {
        // get HTTP cookie named authorization
        String token = "";
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
            }
        }

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

        try {
            // automatically decrypts
            Claims claims = Jwts.parser()
                                .setSigningKey(key)
                                .parseClaimsJws(token)
                                .getBody();
            Principal principal = new Principal();
            principal.setId(Integer.parseInt(claims.getId()));  
            principal.setUsername(claims.getSubject());
            // principal.setFirstName(claims.get("firstName", String.class));

            req.setAttribute("principal", principal);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }

    }
    
}
