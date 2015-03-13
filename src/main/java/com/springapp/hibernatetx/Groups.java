package com.springapp.hibernatetx;

import com.springapp.clientrequests.CreateGroupRequest;
import com.springapp.hibernate.GroupsEntity;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.UsersEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.collection.internal.PersistentSet;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by franschl on 28.02.15.
 */
public class Groups {
    public static ArrayList<GroupsEntity> groups = new ArrayList<GroupsEntity>();

    public static GroupsEntity getGroup(int id) {
        GroupsEntity group = new GroupsEntity();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            group = (GroupsEntity) session.createQuery("from GroupsEntity where groupID=" + id).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return group;
    }

    @Transactional
    // careful: Request only contains IDs to admin and members
    public static GroupsEntity createGroup(CreateGroupRequest request) {
        GroupsEntity group = new GroupsEntity();

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

    public static ArrayList<GroupsEntity> getGroupList() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            groups = (ArrayList<GroupsEntity>) session.createQuery("FROM GroupsEntity").list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return groups;
    }

    public static ArrayList<UsersEntity> getMembers(int groupID) {
        ArrayList<UsersEntity> members;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            GroupsEntity group = Groups.getGroup(groupID);
            
            //Update the persistent instance with the identifier of the given detached instance.
            session.update(group);

            PersistentSet members1 = (PersistentSet)group.getUsers();
            members = new ArrayList<>(members1);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
        return members;
    }

    public static UsersEntity getMember(int groupID, int userID) {
        for (UsersEntity member: Groups.getMembers(groupID)) {
            if (userID == member.getUserID()) return member;
        }
        return null;
    }

    public static UsersEntity getAdmin(int groupID) {
        UsersEntity admin = new UsersEntity();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // TODO admin ausgeben, session ist nicht threadsafe!!
            int adminID = Integer.parseInt(session.createSQLQuery("Select user_id from user_group where is_admin=1 and group_id=" + groupID).uniqueResult().toString());
            admin = (UsersEntity) session.createQuery("from UsersEntity where userID=" + adminID).uniqueResult();

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
