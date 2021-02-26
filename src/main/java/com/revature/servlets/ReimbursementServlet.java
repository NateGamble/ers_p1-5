package com.revature.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dtos.HttpStatus;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import com.revature.services.ReimbursementService;
import com.revature.services.UserService;
import com.revature.util.ErrorResponseFactory;
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
@WebServlet("/auth")
public class ReimbursementServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(UserRepository.class);
    private final ReimbursementService reimbursementService = ReimbursementService.getInstance();
    private final ErrorResponseFactory errResponseFactory = ErrorResponseFactory.getInstance();

    //No Functionality For Project: Good for Template and viewership.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json");

        //TODO: Set up JWT Here.
        User rqstr = null;

        String userIdParam = req.getParameter("userId");
        String reimbursementIdParam = req.getParameter("reimbursementId");
        String reimbursementTypeParam = req.getParameter("reimbursementType");
        String reimbursementStatusParam = req.getParameter("reimbursementStatus");

        try{
            if(reimbursementIdParam == null) {
                if (rqstr != null && rqstr.getUserRole().toString().equals("Finance Manager")) {

                    if (reimbursementTypeParam != null){
                        logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                        logger.info("Retrieving all reimbursements with Type{}" + reimbursementTypeParam);
                        List<Reimbursement> reimbursements = reimbursementService
                                .getReimbByType(ReimbursementType.getByName(reimbursementTypeParam));
                        String reimbursementsJson = mapper.writeValueAsString(reimbursements);
                        writer.write(reimbursementsJson);
                    } else if (reimbursementStatusParam != null){
                        logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                        logger.info("Retrieving all reimbursements with Status{}" + reimbursementStatusParam);
                        List<Reimbursement> reimbursements = reimbursementService
                                .getReimbByStatus(ReimbursementStatus.getByName(reimbursementStatusParam));
                        String reimbursementsJson = mapper.writeValueAsString(reimbursements);
                        writer.write(reimbursementsJson);
                    } else {
                        logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                        logger.info("Retrieving all reimbursements");
                        List<Reimbursement> reimbursements = reimbursementService.getAllReimb();
                        String reimbursementsJson = mapper.writeValueAsString(reimbursements);
                        writer.write(reimbursementsJson);
                    }
                } else if (rqstr != null && rqstr.getUserRole().toString().equals("Employee")) {

                    logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                    logger.info("Retrieving all reimbursements");
                    int desiredId = Integer.parseInt(userIdParam);
                    List<Reimbursement> reimbursements = reimbursementService.getReimbByUserId(desiredId);
                    String reimbursementsJson = mapper.writeValueAsString(reimbursements);
                    writer.write(reimbursementsJson);

                } else {
                    if (rqstr == null) {
                        logger.warn("Unauthorized request made by unknown requester");
                        resp.setStatus(401);
                        writer.write(errResponseFactory.generateErrorResponse(HttpStatus.UNAUTHORIZED).toJSON());
                    } else {
                        logger.warn("Request made by requester, {} who lacks proper authorities",
                                rqstr.getUsername());
                        resp.setStatus(403);
                        writer.write(errResponseFactory.generateErrorResponse(HttpStatus.FORBIDDEN).toJSON());
                    }
                }
            } else {
                if (rqstr != null && rqstr.getUserRole().toString().equals("Finance Manager") ||
                        rqstr != null && rqstr.getUserRole().toString().equals("Employee")) {
                    logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                    logger.info("Retrieving reimbursement by id{} ", reimbursementIdParam);
                    int desiredId = Integer.parseInt(reimbursementIdParam);
                    Reimbursement reimbursements = reimbursementService.getReimbById(desiredId);
                    String reimbursementsJson = mapper.writeValueAsString(reimbursements);
                    writer.write(reimbursementsJson);
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

        //TODO: Set up JWT Here.
        User rqstr = null;

        try{
            if (rqstr != null && rqstr.getUserRole().toString().equals("Employee")){

                logger.info("UserServlet.doPost() invoked by requester{}", rqstr);
                Reimbursement reimbursement = mapper.readValue(req.getInputStream(), Reimbursement.class);
                logger.info("Adding reimbursement");
                reimbursementService.save(reimbursement);
                String newReimbursementJSON = mapper.writeValueAsString(reimbursement);
                writer.write(newReimbursementJSON);
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json");

        //TODO: Set up JWT Here.
        User rqstr = null;

        String userIdParam = req.getParameter("userId");
        String reimbursementIdParam = req.getParameter("reimbursementId");
        String reimbursementStatusParam = req.getParameter("reimbursementStatus");
        String reimbursementApprove = req.getParameter("reimbursementApprove");
        String reimbursementDeny = req.getParameter("reimbursementDeny");

        try{
            if (rqstr != null && rqstr.getUserRole().toString().equals("Employee")){
                if (reimbursementStatusParam != null){
                    logger.info("UserServlet.doPut() invoked by requester{}", rqstr);
                    Reimbursement reimb = mapper.readValue(req.getInputStream(), Reimbursement.class);
                    if (reimb.getReimbursementStatus().toString().equals("Pending")){
                        logger.info("Updating Reimbursement Status");
                        reimbursementService.updateEMP(reimb);
                        String newUserJSON = mapper.writeValueAsString(reimb);
                        writer.write(newUserJSON);
                        resp.setStatus(200);
                    } else{
                        logger.info("Reimbursement not Pending, cannot update.");
                    }
                }
            } else if (rqstr != null && rqstr.getUserRole().toString().equals("Finance Manager")){
                if (reimbursementApprove != null){
                    logger.info("Approving Reimbursement");
                    int reimbId = Integer.parseInt(reimbursementIdParam);
                    int approver = Integer.parseInt(userIdParam);
                    reimbursementService.approve(approver, reimbId);
                    String newUserJSON = mapper.writeValueAsString(reimbId);
                    writer.write(newUserJSON);
                    resp.setStatus(200);
                } else if(reimbursementDeny != null){
                    logger.info("Denying Reimbursement");
                    int reimbId = Integer.parseInt(reimbursementIdParam);
                    int denier = Integer.parseInt(userIdParam);
                    reimbursementService.deny(denier, reimbId);
                    String newUserJSON = mapper.writeValueAsString(reimbId);
                    writer.write(newUserJSON);
                    resp.setStatus(200);
                } else{
                    logger.info("Reimbursement not confirmed as approval or denial.");
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

}
