package com.revature.util;

import com.revature.models.*;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class AppState {

    public static final AppState app = new AppState();

    private SessionFactory factory;

    private AppState() {
        try{
            factory = new Configuration().configure()
                            .addAnnotatedClass(User.class)
                            .addAnnotatedClass(Reimbursement.class)
                            .addAnnotatedClass(ReimbursementStatus.class)
                            .addAnnotatedClass(Role.class)
                            .addAnnotatedClass(ReimbursementType.class)
                            .buildSessionFactory();

        } catch (Throwable ex){
            System.err.println("This is an error because we didn't set something up right.");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public AppState getApp() { return app; }

    public SessionFactory getFactory() { return factory; }

    
    
}
