package com.springapp.hibernatetx;

import com.springapp.clientrequests.CreateGroupRequest;
import com.springapp.hibernate.GroupsEntity;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.UsersEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.util.ArrayList;
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

        ArrayList<UsersEntity> members = new ArrayList<UsersEntity>((Set<UsersEntity>)Groups.getGroup(groupID).getUsers());
        return members;
    }
}
