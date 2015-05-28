package com.springapp.transactional;

import com.springapp.dto.CreateGroupRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.GroupDAO;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.UserDAO;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.transaction.Transactional;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by franschl on 28.02.15.
 */
public class Groups {

    public static void deleteGroup(int groupID) throws GroupNotFoundException{
        GroupDAO group;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            group = Groups.getGroup(groupID);
            Hibernate.initialize(group);
            session.delete(group);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
    }

    public static GroupDAO getGroup(int groupID) throws GroupNotFoundException {
        GroupDAO group = new GroupDAO();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            group = (GroupDAO) session.createQuery("from GroupDAO where groupID=?").setParameter(0, groupID).uniqueResult();
            Hibernate.initialize(group.getUsers());
            if (group == null) {
                throw new GroupNotFoundException();
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return group;
    }

    @Transactional
    // careful: Request only contains IDs to admin and members
    public static GroupDAO createGroup(CreateGroupRequest request) throws UserNotFoundException {
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
            throw e;
        } finally {
            session.close();
        }
        return group;

    }

    public static ArrayList<GroupDAO> getGroupList() {
        ArrayList<GroupDAO> groups = new ArrayList<GroupDAO>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            groups = (ArrayList<GroupDAO>) session.createQuery("FROM GroupDAO").list();
            for (GroupDAO group : groups) {
                Hibernate.initialize(group.getUsers());
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return groups;
    }

    public static Set<GroupDAO> getGroupsWithUserInIt(int userID) throws UserNotFoundException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Set<GroupDAO> groupList = new HashSet<>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            UserDAO user = Users.getUser(userID);
            session.update(user);
            Hibernate.initialize(user.getGroups());
            groupList = user.getGroups();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }

        return groupList;
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
            if (tx != null) tx.rollback();
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
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return admin;
    }

    public static GroupDAO addMember(int groupID, int userID) throws GroupNotFoundException, UserNotFoundException {
        GroupDAO group;
        UserDAO user;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            group = Groups.getGroup(groupID);
            user = Users.getUser(userID);

            session.update(group);

            group.addUser(user);
            session.update(group);

            tx.commit();
        } catch(Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return group;
    }

    public static void removeMember(int groupID, int userID) throws UserNotFoundException, GroupNotFoundException {
        GroupDAO group;
        UserDAO user;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            group = Groups.getGroup(groupID);
            user = Users.getUser(userID);

            // get current group state (including members)
            session.update(group);
            // remove from list
            group.removeUser(user);
            // update group state
            session.update(group);
            tx.commit();
        } catch(Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
    }
}
