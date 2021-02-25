import com.fasterxml.classmate.AnnotationConfiguration;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.repositories.ReimbursementsRepository;
import com.revature.services.ReimbursementService;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class Driver {

    private static SessionFactory factory;
    public static void main(String [] args) throws IOException {

        try{
            factory = new Configuration().configure().addAnnotatedClass(User.class).
                    addAnnotatedClass(Reimbursement.class).buildSessionFactory();

        } catch (Throwable ex){
            System.err.println("This is an error because we didn't set something up right.");
            throw new ExceptionInInitializerError(ex);
        }



    }

    public static void getUsers(){
        Transaction tx = null;
        Session session = factory.openSession();

        try {
            tx = session.beginTransaction();
            //newUser.setUserId((Integer) session.save(newUser));
            List users = session.createQuery("FROM User").list();

            System.out.println(users.size());
            for (Iterator iterator = users.iterator(); iterator.hasNext();){
                User getUser = (User) iterator.next();
                System.out.println("First name: " + getUser.getFirstname());
            }
            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void addUser(User user){
        Transaction tx = null;
        Session session = factory.openSession();

        try {
            tx = session.beginTransaction();
            session.save(user);

            System.out.println();

            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void addReimbursement(Reimbursement reimbursement){
        Transaction tx = null;
        Session session = factory.openSession();

        try {
            tx = session.beginTransaction();
            session.save(reimbursement);

            System.out.println();

            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void getReimbursements(){
        Transaction tx = null;
        Session session = factory.openSession();

        try {
            tx = session.beginTransaction();
            //newUser.setUserId((Integer) session.save(newUser));
            List reimbursements = session.createQuery("FROM Reimbursement").list();

            System.out.println(reimbursements.size());
            for (Iterator iterator = reimbursements.iterator(); iterator.hasNext();){
                Reimbursement getReimbursement = (Reimbursement) iterator.next();
                System.out.println("description: " + getReimbursement.getDescription());
            }
            tx.commit();

        } catch (HibernateException e) {
            if (tx!= null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}