import com.fasterxml.classmate.AnnotationConfiguration;
import com.revature.models.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.*;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;

public class Driver {

    private static SessionFactory factory;
    public static void main(String [] args){

        try{
            factory = new Configuration().configure().addAnnotatedClass(User.class).buildSessionFactory();
        } catch (Throwable ex){
            System.err.println("This is an error because we didn't set something up right.");
            throw new ExceptionInInitializerError(ex);
        }

        User newUser = new User();

        newUser.setUserRole(1);
        newUser.setActive(true);
        newUser.setEmail("BriansEmail@Gmail.com");
        newUser.setFirstname("Brian");
        newUser.setUsername("Briguy101");
        newUser.setLastname("Withrow");
        newUser.setPassword("BadPassword");

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

        //System.out.println(newUser.getUserId());
    }
}
