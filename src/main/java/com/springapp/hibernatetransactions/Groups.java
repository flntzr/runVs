package com.springapp.hibernatetransactions;

import com.springapp.fromclient.CreateGroupRequest;
import com.springapp.hibernate.GroupsEntity;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.UsersEntity;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by franschl on 28.02.15.
 */
public class Groups {
    public static ArrayList<GroupsEntity> groups = new ArrayList<GroupsEntity>();

    @Transactional
    // careful: Request only contains IDs to admin and members
    public static GroupsEntity createGroup(CreateGroupRequest request) {
        GroupsEntity group = new GroupsEntity();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        tx = session.beginTransaction();

        try {
            group.setName(request.getName());

            for (int userID : request.getMembers()) {
                group.addUser(Users.getUser(userID));
            }

            group.addUser(Users.getUser(request.getAdmin()));
            group.setDistance(request.getDistance());

            //TEST
            //group.setGroupID(4);

            session.save(group);
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
}