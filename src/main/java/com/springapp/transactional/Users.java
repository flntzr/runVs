package com.springapp.transactional;

import com.springapp.dto.CreateUserRequest;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.UserDAO;
import org.hibernate.Hibernate;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by franschl on 02.02.15.
 */
public class Users {

    public static void deleteUser(int userID) throws UserNotFoundException {
        UserDAO user;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            user = Users.getUser(userID);
            Hibernate.initialize(user);
            session.delete(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
    }

    public static ArrayList<UserDAO> getUserList() {
        ArrayList<UserDAO> userList = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            userList = (ArrayList<UserDAO>) session.createQuery("FROM UserDAO").list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }

        return userList;
    }

    public static UserDAO getUser(int id) throws UserNotFoundException {
        UserDAO user = new UserDAO();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            user = (UserDAO) session.createQuery("FROM UserDAO WHERE userID=? ").setParameter(0, id).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    public static UserDAO createUser(CreateUserRequest request) throws UnsupportedEncodingException {
        UserDAO user = new UserDAO();

        // TODO proper salt, encoding errors everywhere
        byte[] byteSalt = KeyGenerators.secureRandom(128).generateKey();
        String salt = new String(byteSalt, "UTF-16");
        user.setSalt(salt);

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if ((UserDAO) session.createQuery("FROM UserDAO WHERE nick=? ").setParameter(0, request.getNick()).uniqueResult() != null) {
                // Username already exists
                throw new NonUniqueResultException(1); //TODO dat 1
            }
            user.setNick(request.getNick());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            session.save(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return user;
    }

    public static UserDAO editUser(CreateUserRequest request, int userID) throws UserNotFoundException {
        UserDAO user;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            user = Users.getUser(userID);
            user.setNick(request.getNick());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());

            if ((UserDAO) session.createQuery("FROM UserDAO WHERE nick=? ").setParameter(0, request.getNick()).uniqueResult() != null) {
                // Username already exists
                throw new NonUniqueResultException(1); // TODO no internet and no default constructor --> pass dat 1
            }
            session.update(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return user;
    }

}
