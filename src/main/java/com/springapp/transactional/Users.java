package com.springapp.transactional;

import com.springapp.dto.CreateUserRequest;
import com.springapp.exceptions.IncorrectLoginException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.UserDAO;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.persistence.Convert;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by franschl on 02.02.15.
 */
public class Users {

    final static Logger logger = Logger.getLogger(Users.class);

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
            throw e;
        } finally {
            session.close();
        }
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    public static UserDAO getUserByName(String name) throws UserNotFoundException {
        UserDAO user = new UserDAO();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            user = (UserDAO) session.createQuery("FROM UserDAO WHERE nick=? ").setParameter(0, name).uniqueResult();
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

    public static UserDAO setToken(int userID, String token, long tokenExpiry) throws UserNotFoundException {
        UserDAO user = new UserDAO();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createQuery("update UserDAO set authToken=? where id=?")
                    .setParameter(0, token)
                    .setParameter(1, userID)
                    .executeUpdate();
            session.createQuery("update UserDAO set tokenExpiry=? where id=?")
                    .setParameter(0, new Timestamp(tokenExpiry))
                    .setParameter(1, userID)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
        return user;
    }

    private static String calculateSaltedPasswordHash(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md5er = MessageDigest.getInstance("MD5");
        byte[] pwSaltByteArray = new byte[password.length() + salt.length()];
        System.arraycopy(password.getBytes(), 0, pwSaltByteArray, 0, password.length());
        System.arraycopy(salt.getBytes(), 0, pwSaltByteArray, password.length(), salt.length());
        return new String(md5er.digest(pwSaltByteArray));
    }

    public static boolean areCredentialsValid(UserDAO user, String password) throws UserNotFoundException {
        String pwHash;
        boolean credentialsValid = false;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            pwHash = calculateSaltedPasswordHash(password, user.getSalt());
            credentialsValid = pwHash.equals(user.getPassword());
            tx.commit();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
        if (user == null) {
            throw new UserNotFoundException();
        }
        return credentialsValid;
    }

    public static UserDAO createUser(CreateUserRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserDAO user = new UserDAO();

        byte[] byteSalt = KeyGenerators.secureRandom(128).generateKey();
        String salt = new String(byteSalt, "US-ASCII");
        user.setSalt(salt);

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (session.createQuery("FROM UserDAO WHERE nick=? ").setParameter(0, request.getNick()).uniqueResult() != null) {
                // Username already exists
                throw new NonUniqueResultException(1);
            }
            user.setNick(request.getNick());
            user.setPassword(calculateSaltedPasswordHash(request.getPassword(), salt));
            user.setEmail(request.getEmail());
            session.save(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
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

            if (session.createQuery("FROM UserDAO WHERE nick=? ").setParameter(0, request.getNick()).uniqueResult() != null) {
                // Username already exists
                throw new NonUniqueResultException(1);
            }
            session.update(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
        return user;
    }

}
