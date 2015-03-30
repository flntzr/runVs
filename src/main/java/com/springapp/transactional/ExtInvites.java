package com.springapp.transactional;

import com.springapp.clientrequests.CreateExtInviteRequest;
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
    public static ArrayList<ExtInvDAO> getAll(CreateExtInviteRequest request) {
        ArrayList<ExtInvDAO> inviteList = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            inviteList = (ArrayList<ExtInvDAO>) session.createQuery("from ExtInvDAO").list();
            for (ExtInvDAO invite : inviteList) {
                Hibernate.initialize(invite.getGroups());
                Hibernate.initialize(invite.getUser());
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
}
