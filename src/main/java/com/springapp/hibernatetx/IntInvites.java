package com.springapp.hibernatetx;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.springapp.clientrequests.CreateIntInviteRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.IntInvitationsEntity;
import com.springapp.hibernate.UsersEntity;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;

/**
 * Created by franschl on 28.03.15.
 */
public class IntInvites {

    public static IntInvitationsEntity createIntInvite(CreateIntInviteRequest request, int inviteeID) throws GroupNotFoundException, UserNotFoundException  {
        IntInvitationsEntity intInvite = new IntInvitationsEntity();
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

    public static ArrayList<IntInvitationsEntity> getByUserID(int inviteeID) throws UserNotFoundException {
        ArrayList<IntInvitationsEntity> inviteList = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            UsersEntity invitee = Users.getUser(inviteeID);
            inviteList = (ArrayList<IntInvitationsEntity>) session.createQuery("from IntInvitationsEntity where invitee=?").setParameter(0, invitee).list();

            // take care of lazily loaded elements
            for (IntInvitationsEntity invite : inviteList) {
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
