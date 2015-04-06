package com.springapp.transactional;

import com.springapp.dto.CreateExtInviteRequest;
import com.springapp.exceptions.ExtInviteNotFoundException;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.ExtInvDAO;
import com.springapp.hibernate.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;

/**
 * Created by franschl on 30.03.15.
 */
public class ExtInvites {
    public static ArrayList<ExtInvDAO> getAll() {
        ArrayList<ExtInvDAO> inviteList = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            inviteList = (ArrayList<ExtInvDAO>) session.createQuery("from ExtInvDAO").list();
            for (ExtInvDAO invite : inviteList) {
                Hibernate.initialize(invite.getGroup());
                Hibernate.initialize(invite.getHost());
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return inviteList;
    }

    public static ExtInvDAO getByID(int extInvID) throws ExtInviteNotFoundException {
        ExtInvDAO invite = new ExtInvDAO();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            invite = (ExtInvDAO) session.createQuery("from ExtInvDAO where extInvID=?").setParameter(0, extInvID).uniqueResult();
            if (invite == null) throw new ExtInviteNotFoundException();
            Hibernate.initialize(invite.getGroup());
            Hibernate.initialize(invite.getHost());
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return invite;
    }

    public static ExtInvDAO createExtInv(CreateExtInviteRequest request) throws GroupNotFoundException, UserNotFoundException {
        ExtInvDAO invite = new ExtInvDAO();
        int pin;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            pin = generatePin();
            // taking care of PIN collisions
            while ((ExtInvDAO)session.createQuery("from ExtInvDAO where pin=?").setParameter(0, pin).uniqueResult() != null){
                pin = generatePin();
            }

            invite.setPin(pin);
            invite.setGroup(Groups.getGroup(request.getGroupID()));
            invite.setHost(Users.getUser(request.getHostID()));
            session.save(invite);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return invite;
    }

    private static int generatePin() {
        return (int) (Math.random() * 1000000000);
    }
}
