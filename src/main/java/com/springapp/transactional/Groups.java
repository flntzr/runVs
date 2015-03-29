package com.springapp.transactional;

import com.springapp.clientrequests.CreateGroupRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.GroupDAO;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.UserDAO;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.transaction.Transactional;
import java.util.ArrayList;

/**
 * Created by franschl on 28.02.15.
 */
public class Groups {
    public static ArrayList<GroupDAO> groups = new ArrayList<GroupDAO>();

    public static GroupDAO getGroup(int id) throws GroupNotFoundException {
        GroupDAO group = new GroupDAO();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            group = (GroupDAO) session.createQuery("from GroupDAO where groupID=" + id).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        if (group == null) {
            throw new GroupNotFoundException();
        }
        return group;
    }

    @Transactional
    // careful: Request only contains IDs to admin and members
    public static GroupDAO createGroup(CreateGroupRequest request) {
        GroupDAO group = new GroupDAO();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            group.setName(request.getName());

            for (int userID : request.getMembers()) {
                group.addUser(Users.getUser(userID));
            }

            group.addUser(Users.getUser(request.getAdmin()));
            group.setDistance(request.getDistance());

            session.save(group);
            tx.commit();

            // Easy workaround: assign admin in group_run
            tx = null;
            tx = session.beginTransaction();
            session.createSQLQuery("UPDATE user_group set is_admin=1 where user_id=" + request.getAdmin()).executeUpdate();
            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return group;

    }

    public static ArrayList<GroupDAO> getGroupList() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            groups = (ArrayList<GroupDAO>) session.createQuery("FROM GroupDAO").list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return groups;
    }

    public static ArrayList<UserDAO> getMembers(int groupID) throws GroupNotFoundException {
        ArrayList<UserDAO> members;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            GroupDAO group = Groups.getGroup(groupID);

            //Update the persistent instance with the identifier of the given detached instance.
            session.update(group);

            members = new ArrayList<>(group.getUsers());
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return members;
    }

    public static UserDAO getMember(int groupID, int userID) throws GroupNotFoundException, UserNotFoundException {
        for (UserDAO member: Groups.getMembers(groupID)) {
            if (userID == member.getUserID()) return member;
        }
        throw new UserNotFoundException();
    }

    public static UserDAO getAdmin(int groupID) {
        UserDAO admin = new UserDAO();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            int adminID = Integer.parseInt(session.createSQLQuery("Select user_id from user_group where is_admin=1 and group_id=" + groupID).uniqueResult().toString());
            admin = (UserDAO) session.createQuery("from UserDAO where userID=" + adminID).uniqueResult();

            tx.commit();
        } catch(NullPointerException e) {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return admin;
    }
}
