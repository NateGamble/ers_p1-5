package com.revature.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dtos.HttpStatus;
import com.revature.dtos.Principal;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.*;
import com.revature.repositories.UserRepository;
import com.revature.services.ReimbursementService;
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
@WebServlet("/reimbursements")
public class ReimbursementServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(UserRepository.class);
    private final ReimbursementService reimbursementService = ReimbursementService.getInstance();
    private final ErrorResponseFactory errResponseFactory = ErrorResponseFactory.getInstance();
    private final UserService userService = UserService.getInstance();

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

        String reimbursementIdParam = req.getParameter("reimbursementId");
        String reimbursementTypeParam = req.getParameter("reimbursementType");
        String reimbursementStatusParam = req.getParameter("reimbursementStatus");

        try{
            //View All Reimbursements (Finance Manager OR Employee)
            if(reimbursementIdParam == null) {
                //Finance Manager's options for viewing reimbursements
                if (rqstr != null && rqstr.getUserRole() == Role.FINANCE_MANAGER) {
                    //If a Reimbursement type is given, filters by Type.
                    if (reimbursementTypeParam != null) {
                        logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                        logger.info("Retrieving all reimbursements with Type{}" + reimbursementTypeParam);
                        List<Reimbursement> reimbursements = reimbursementService
                                .getReimbByType(ReimbursementType.getByName(reimbursementTypeParam));
                        String reimbursementsJson = mapper.writeValueAsString(reimbursements);
                        writer.write(reimbursementsJson);
                    } //If a Reimbursement status is given, filters by Status.
                    else if (reimbursementStatusParam != null){
                        logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                        logger.info("Retrieving all reimbursements with Status{}" + reimbursementStatusParam);
                        List<Reimbursement> reimbursements = reimbursementService
                                .getReimbByStatus(ReimbursementStatus.getByName(reimbursementStatusParam));
                        String reimbursementsJson = mapper.writeValueAsString(reimbursements);
                        writer.write(reimbursementsJson);
                    } //If neither is given, search all Reimbursements.
                    else {
                        logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                        logger.info("Retrieving all reimbursements");
                        List<Reimbursement> reimbursements = reimbursementService.getAllReimb();
                        String reimbursementsJson = mapper.writeValueAsString(reimbursements);
                        writer.write(reimbursementsJson);
                    }
                } //If Employee is the requester, they view all authored reimbursements rather than filter.
                else if (rqstr != null && rqstr.getUserRole() == Role.EMPLOYEE) {
                    logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                    logger.info("Retrieving all reimbursements");
                    List<Reimbursement> reimbursements = reimbursementService.getReimbByUserId(rqstr.getUserId());    
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
            } // Finance Manger OR Employee can get a specific Reimbursement by ID.
            else {
                if (rqstr != null && rqstr.getUserRole() == Role.FINANCE_MANAGER ||
                        rqstr != null && rqstr.getUserRole() == Role.EMPLOYEE) {
                    logger.info("ReimbursementServlet.doGet() invoked by requester{}", rqstr);
                    logger.info("Retrieving reimbursement by id{} ", reimbursementIdParam);
                    int desiredId = Integer.parseInt(reimbursementIdParam);
                    Reimbursement reimbursements = reimbursementService.getReimbById(desiredId);
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
            //Check that Employee is the requester attempting to submit a reimbursement.
            if (rqstr != null && rqstr.getUserRole() == Role.EMPLOYEE) {

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

        String reimbursementIdParam = req.getParameter("reimbursementId");
        String reimbursementApprove = req.getParameter("reimbursementApprove");
        String reimbursementDeny = req.getParameter("reimbursementDeny");

        try{
            //Checks if rqstr is an Employee, and that they have a PENDING reimbursement.
            if (rqstr != null && rqstr.getUserRole() == Role.EMPLOYEE) {
                logger.info("UserServlet.doPut() invoked by requester{}", rqstr);
                Reimbursement newReimb = mapper.readValue(req.getInputStream(), Reimbursement.class);
                Reimbursement reimb = reimbursementService.getReimbById(newReimb.getId());
                if (reimb.getReimbursementStatus().toString().equals("Pending")){
                    logger.info("Updating Reimbursement Status");
                    reimbursementService.updateEMP(newReimb);
                    String newUserJSON = mapper.writeValueAsString(newReimb);
                    writer.write(newUserJSON);
                    resp.setStatus(200);
                } else{
                    logger.info("Reimbursement not Pending, cannot update.");
                }
            } // Checks if rqstr is a Finance Manager, and whether or not they will be Approving/Denying.
            else if (rqstr != null && rqstr.getUserRole() == Role.FINANCE_MANAGER){
                
                if (reimbursementApprove != null){
                    logger.info("Approving Reimbursement");
                    int reimbId = Integer.parseInt(reimbursementIdParam);
                    int approver = rqstr.getUserId();
                    reimbursementService.approve(approver, reimbId);
                    String newUserJSON = mapper.writeValueAsString(reimbId);
                    writer.write(newUserJSON);
                    resp.setStatus(200);
                } else if(reimbursementDeny != null){
                    logger.info("Denying Reimbursement");
                    int reimbId = Integer.parseInt(reimbursementIdParam);
                    int denier = rqstr.getUserId();
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

}
