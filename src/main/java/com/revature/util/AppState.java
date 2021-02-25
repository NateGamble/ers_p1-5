package com.revature.util;

import com.revature.models.*;

import com.revature.services.ReimbursementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class AppState {

    private static final Logger logger = LogManager.getLogger(ReimbursementService.class);

    public static final AppState app = new AppState();

    private SessionFactory factory;

    private AppState() {
        try{
            logger.info("Initializing Appstate, and configuring Hibernate Factory.");
            factory = new Configuration().configure()
                            .addAnnotatedClass(User.class)
                            .addAnnotatedClass(Reimbursement.class)
                            .buildSessionFactory();

        } catch (Throwable ex){
            logger.error("An error occurred while configuring Hibernate Factory.");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public AppState getApp() { return app; }

    public SessionFactory getFactory() { return factory; }

    
    
}
