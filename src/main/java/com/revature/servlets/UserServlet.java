package com.revature.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dtos.HttpStatus;
import com.revature.dtos.Principal;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Role;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import com.revature.services.UserService;
import com.revature.util.ErrorResponseFactory;
import com.revature.util.JwtParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Modified from Quizzard project at
 * https://github.com/210119-java-enterprise/quizzard
 */
@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(UserRepository.class);
    private final UserService userService = UserService.getInstance();
    private final ErrorResponseFactory errResponseFactory = ErrorResponseFactory.getInstance();


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json");

        // JWT check user isn't deleted
        JwtParser.checkToken(req);
        Principal p = (Principal) req.getAttribute("principal");
        if (p == null) {
            logger.warn("Unauthorized request made by unknown requester");
            resp.setStatus(401);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.UNAUTHORIZED).toJSON());
            return;
        }
        User rqstr = userService.getUserByUsername(p.getUsername());

        writer.write(mapper.writeValueAsString(rqstr));

        String userIdParam = req.getParameter("userId");

        try{
            if (rqstr != null && rqstr.getUserRole() == Role.ADMIN){

                logger.info("UserServlet.doDelete() invoked by requester{}", rqstr);
                int desiredId = Integer.parseInt(userIdParam);
                logger.info("Retrieving users with id, {}", desiredId);
                boolean deleted = userService.deleteUserById(desiredId);

                // writer.write(mapper.writeValueAsString(deleted));

                // String validationJson = mapper.writeValueAsString(deleted);
                // writer.write(validationJson);
                resp.setStatus(200);
                writer.write(errResponseFactory.generateErrorResponse(HttpStatus.OK).toJSON());

            } else {
                if (rqstr == null) {
                    logger.warn("Unauthorized request made by unknown requester");
                    resp.setStatus(401);
                    writer.write(errResponseFactory.generateErrorResponse(HttpStatus.UNAUTHORIZED).toJSON());
                } else{
                    logger.warn("Request made by requester, {} who lacks proper authorities",
                            rqstr.getUsername());
                    resp.setStatus(403);
                    writer.write(errResponseFactory.generateErrorResponse(HttpStatus.FORBIDDEN).toJSON());
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(writer);
            logger.warn(e.getMessage());
            resp.setStatus(400);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.BAD_REQUEST).toJSON());
        } catch (ResourceNotFoundException e){
            e.printStackTrace(writer);
            logger.warn(e.getMessage());
            resp.setStatus(404);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.NOT_FOUND).toJSON());
        } catch (Exception e) {
            e.printStackTrace(writer);
            logger.error(e.getMessage());
            resp.setStatus(500);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR).toJSON());
        }
    }

    //No Functionality For Project: Good for Template and viewership.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json");

        // JWT check user isn't deleted
        JwtParser.checkToken(req);
        Principal p = (Principal) req.getAttribute("principal");
        if (p == null) {
            logger.warn("Unauthorized request made by unknown requester");
            resp.setStatus(401);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.UNAUTHORIZED).toJSON());
            return;
        }
        User rqstr = userService.getUserByUsername(p.getUsername());

        writer.write(mapper.writeValueAsString(rqstr));

        String userIdParam = req.getParameter("userId");

        try{
            if (rqstr != null && rqstr.getUserRole() == Role.ADMIN){

                logger.info("UserServlet.doGet() invoked by requester{}", rqstr);

                if (userIdParam == null){
                    logger.info("Retrieving all users");
                    List<User> users = userService.getAllUsers();
                    String usersJson = mapper.writeValueAsString(users);
                    writer.write(usersJson);
                }

            } else {
                if (rqstr == null) {
                    logger.warn("Unauthorized request made by unknown requester");
                    resp.setStatus(401);
                    writer.write(errResponseFactory.generateErrorResponse(HttpStatus.UNAUTHORIZED).toJSON());
                } else{
                    logger.warn("Request made by requester, {} who lacks proper authorities",
                            rqstr.getUsername());
                    resp.setStatus(403);
                    writer.write(errResponseFactory.generateErrorResponse(HttpStatus.FORBIDDEN).toJSON());
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logger.warn(e.getMessage());
            resp.setStatus(400);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.BAD_REQUEST).toJSON());
        } catch (ResourceNotFoundException e){
            e.printStackTrace();
            logger.warn(e.getMessage());
            resp.setStatus(404);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.NOT_FOUND).toJSON());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            resp.setStatus(500);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR).toJSON());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json");

        // JWT check user isn't deleted
        JwtParser.checkToken(req);
        Principal p = (Principal) req.getAttribute("principal");
        if (p == null) {
            logger.warn("Unauthorized request made by unknown requester");
            resp.setStatus(401);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.UNAUTHORIZED).toJSON());
            return;
        }
        User rqstr = userService.getUserByUsername(p.getUsername());

        try{
            if (rqstr != null && rqstr.getUserRole() == Role.ADMIN){

                logger.info("UserServlet.doPost() invoked by requester{}", rqstr);
                User user = mapper.readValue(req.getInputStream(), User.class);

                // writer.write(mapper.writeValueAsString(user));

                userService.register(user);
                String newUserJSON = mapper.writeValueAsString(user);
                writer.write(newUserJSON);
                resp.setStatus(201);


            } else {
                if (rqstr == null) {
                    logger.warn("Unauthorized request made by unknown requester");
                    resp.setStatus(401);
                    writer.write(errResponseFactory.generateErrorResponse(HttpStatus.UNAUTHORIZED).toJSON());
                } else{
                    logger.warn("Request made by requester, {} who lacks proper authorities",
                            rqstr.getUsername());
                    resp.setStatus(403);
                    writer.write(errResponseFactory.generateErrorResponse(HttpStatus.FORBIDDEN).toJSON());
                    writer.write("Request made by requester, " + rqstr.getUsername() + 
                                    " who lacks proper authorities. They are a " + rqstr.getUserRole());
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(writer);
            logger.warn(e.getMessage());
            resp.setStatus(400);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.BAD_REQUEST).toJSON());
        } catch (ResourceNotFoundException e){
            e.printStackTrace(writer);
            logger.warn(e.getMessage());
            resp.setStatus(404);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.NOT_FOUND).toJSON());
        } catch (Exception e) {
            e.printStackTrace(writer);
            logger.error(e.getMessage());
            resp.setStatus(500);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR).toJSON());
            e.printStackTrace(writer);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json");

        // JWT check user isn't deleted
        JwtParser.checkToken(req);
        Principal p = (Principal) req.getAttribute("principal");
        if (p == null) {
            logger.warn("Unauthorized request made by unknown requester");
            resp.setStatus(401);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.UNAUTHORIZED).toJSON());
            return;
        }
        User rqstr = userService.getUserByUsername(p.getUsername());

        try{
            if (rqstr != null && rqstr.getUserRole() == Role.ADMIN) {

                logger.info("UserServlet.doPut() invoked by requester{}", rqstr);
                User user = mapper.readValue(req.getInputStream(), User.class);
                logger.info("Updating user");

                writer.write(mapper.writeValueAsString(user));

                userService.update(user);
                String newUserJSON = mapper.writeValueAsString(user);
                writer.write(newUserJSON);
                resp.setStatus(200);

            } else {
                if (rqstr == null) {
                    logger.warn("Unauthorized request made by unknown requester");
                    resp.setStatus(401);
                    writer.write(errResponseFactory.generateErrorResponse(HttpStatus.UNAUTHORIZED).toJSON());
                } else{
                    logger.warn("Request made by requester, {} who lacks proper authorities",
                            rqstr.getUsername());
                    resp.setStatus(403);
                    writer.write(errResponseFactory.generateErrorResponse(HttpStatus.FORBIDDEN).toJSON());
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(writer);
            logger.warn(e.getMessage());
            resp.setStatus(400);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.BAD_REQUEST).toJSON());
        } catch (ResourceNotFoundException e){
            e.printStackTrace(writer);
            logger.warn(e.getMessage());
            resp.setStatus(404);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.NOT_FOUND).toJSON());
        } catch (Exception e) {
            e.printStackTrace(writer);
            logger.error(e.getMessage());
            resp.setStatus(500);
            writer.write(errResponseFactory.generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR).toJSON());
        }
    }
    

    
}
