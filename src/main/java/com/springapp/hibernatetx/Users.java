package com.springapp.hibernatetx;

import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.UsersEntity;
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

    private static ArrayList<UsersEntity> userList = new ArrayList<>();

    public static ArrayList<UsersEntity> getUserList() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            userList = (ArrayList<UsersEntity>) session.createQuery("FROM UsersEntity").list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return userList;
    }

    // this is a bloated n(o²) operation just to transofrm an ID list into a user list, if possible do some refactoring
    public static ArrayList<UsersEntity> getUserListByIDList(ArrayList<Integer> idList) {
        ArrayList<UsersEntity> subList = new ArrayList<>();
        for (UsersEntity user : getUserList()) {
            for (Integer id : idList) {
                if (user.getUserID() == id) subList.add(user);
            }
        }
        return subList;
    }

    public static UsersEntity getUser(int id) {
        UsersEntity user = new UsersEntity();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            user = (UsersEntity) session.createQuery("FROM UsersEntity WHERE userID=? ").setParameter(0, id).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return user;
    }

    // returns user ID
    public static UsersEntity createUser(UsersEntity user) throws UnsupportedEncodingException {

        // create Salt
        byte[] byteSalt = KeyGenerators.secureRandom(128).generateKey();
        String salt = new String(byteSalt, "UTF-16");
        user.setSalt(salt);

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(user);
            user = (UsersEntity) session.createQuery("FROM UsersEntity WHERE nick=? ").setParameter(0, user.getNick()).uniqueResult();
            tx.commit();
        } catch (NonUniqueResultException e) {
            if (tx != null) tx.rollback();
            System.err.println("Username " + user.getNick() + " already exists. Throwing 409.");
            throw e;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return user;
    }

}
