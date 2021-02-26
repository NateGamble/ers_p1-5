package com.revature.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.dtos.Credentials;
import com.revature.dtos.HttpStatus;
import com.revature.exceptions.AuthenticationException;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import com.revature.services.UserService;
import com.revature.util.ErrorResponseFactory;
import com.revature.util.JwtGenerator;
import com.revature.util.JwtParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Taken from Quizzard project at
 * https://github.com/210119-java-enterprise/quizzard
 */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    public final UserService userService = UserService.getInstance();
    private final ErrorResponseFactory errRespFactory = ErrorResponseFactory.getInstance();
    private static final Logger logger = LogManager.getLogger(UserServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");

        try {

            Credentials creds = mapper.readValue(req.getInputStream(), Credentials.class);

            logger.info("Attempting to authenticate user, {}, with provided credentials", creds.getUsername());
            User authUser = userService.authenticate(creds.getUsername(), creds.getPassword());

            writer.write(mapper.writeValueAsString(authUser));

            logger.info("Establishing a JWT for user, {}", creds.getUsername());
            String token = JwtGenerator.createJwt(authUser);
            Cookie tokenCookie = new Cookie("token", token);
            tokenCookie.setHttpOnly(true);
            resp.addCookie(tokenCookie);

        } catch (MismatchedInputException e) {
            logger.warn(e.getMessage());
            resp.setStatus(400);
            writer.write(errRespFactory.generateErrorResponse(HttpStatus.BAD_REQUEST).toJSON());
        } catch (AuthenticationException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            resp.setStatus(401);
            writer.write(errRespFactory.generateErrorResponse(401, e.getMessage()).toJSON());
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
            resp.setStatus(500);
            writer.write(errRespFactory.generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR).toJSON());
        }

    }

}