package com.springapp.transactional;

import com.springapp.dto.CreateIntInviteRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.IntInvDAO;
import com.springapp.hibernate.UserDAO;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;

/**
 * Created by franschl on 28.03.15.
 */
public class IntInvites {

    public static IntInvDAO createIntInvite(CreateIntInviteRequest request, int inviteeID) throws GroupNotFoundException, UserNotFoundException  {
        IntInvDAO intInvite = new IntInvDAO();
        intInvite.setGroup(Groups.getGroup(request.getGroupID()));
        intInvite.setHost(Users.getUser(request.getHostID()));
        intInvite.setInvitee(Users.getUser(inviteeID));

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(intInvite);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }

        return intInvite;
    }

    public static ArrayList<IntInvDAO> getByUserID(int inviteeID) throws UserNotFoundException {
        ArrayList<IntInvDAO> inviteList = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            UserDAO invitee = Users.getUser(inviteeID);
            inviteList = (ArrayList<IntInvDAO>) session.createQuery("from IntInvDAO where invitee=?").setParameter(0, invitee).list();

            // take care of lazily loaded elements
            for (IntInvDAO invite : inviteList) {
                Hibernate.initialize(invite.getHost());
                Hibernate.initialize(invite.getInvitee());
                Hibernate.initialize(invite.getGroup());
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }

        return inviteList;
    }
}
