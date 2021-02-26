package com.revature.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dtos.HttpStatus;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import com.revature.services.UserService;
import com.revature.util.ErrorResponseFactory;
import jdk.internal.jline.internal.Log;
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

        //TODO: Set up JWT Here.
        User rqstr = null;

        String userIdParam = req.getParameter("userId");

        try{
            if (rqstr != null && rqstr.getUserRole().toString().equals("ADMIN")){

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json");

        //TODO: Set up JWT Here.
        User rqstr = null;

        String userIdParam = req.getParameter("userId");

        try{
            if (rqstr != null && rqstr.getUserRole().toString().equals("ADMIN")){

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
        // TODO Auto-generated method stub
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doPut(req, resp);
    }
    

    
}
