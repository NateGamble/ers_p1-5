package com.revature.util;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import com.revature.models.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

public class AppState {

    private static final Logger logger = LogManager.getLogger(AppState.class);

    public static final AppState app = new AppState();

    private SessionFactory factory;

    // Adapted from Eli's implementation of HibernateUtil
    private AppState() {
        try {
            Configuration configuration = new Configuration();

            // Hibernate settings equivalent to hibernate.cfg.xml's properties
            Properties settings = new Properties();

            File temp = new File("src/main/resources/application.properties");

            if (temp.exists()) {
                try {
                    Properties props = new Properties();

                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    InputStream input = loader.getResourceAsStream("application.properties");

                    props.load(input);

                    settings.put(Environment.DRIVER, "org.postgresql.Driver");
                    settings.put(Environment.URL, props.getProperty("url"));
                    settings.put(Environment.USER, props.getProperty("username"));
                    settings.put(Environment.PASS, props.getProperty("password"));
                    settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgresPlusDialect");
                } catch (Exception e) {
                    logger.fatal(e.getStackTrace());
                }
            }
            else {
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, System.getProperty("url"));
                settings.put(Environment.USER, System.getProperty("username"));
                settings.put(Environment.PASS, System.getProperty("password"));
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgresPlusDialect");
            }

            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");


            configuration.setProperties(settings);

            configuration.addAnnotatedClass(Reimbursement.class)
                            .addAnnotatedClass(User.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            logger.fatal("This ocurred because we set something up wrong");
            logger.fatal(e.getStackTrace());
        }
    }

    public AppState getApp() { return app; }

    public SessionFactory getFactory() { return factory; }

    
    
}
