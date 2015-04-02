package com.springapp.transactional;

import com.springapp.clientrequests.CreateRunRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.RunNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.GroupDAO;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.RunDAO;
import com.springapp.hibernate.UserDAO;
import org.apache.commons.fileupload.FileUploadException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by franschl on 17.03.15.
 */
public class Runs {

    public static void deleteRun(int userID, int runID) throws UserNotFoundException, RunNotFoundException {
        RunDAO run;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            run = Runs.getRunByID(userID, runID);
            Hibernate.initialize(run);
            session.delete(run);
            tx.commit();
        } catch(Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
    }

    public static RunDAO createRun(CreateRunRequest request, int userID) throws FileUploadException, IOException, UserNotFoundException, GroupNotFoundException {
        String filePath = uploadFile(request.getGpxFile());

        HashSet<GroupDAO> groups = new HashSet<>();
        for (Integer groupID : request.getGroups()) {
            groups.add(Groups.getGroup(groupID));
        }

        UserDAO user = Users.getUser(userID);
        RunDAO run = new RunDAO();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            //Update the persistent instance with the identifier of the given detached instance.
            for (GroupDAO group : groups) {
                session.update(group);
            }
            session.update(user);

            run.setDistance(request.getDistance());
            run.setUser(user);
            run.setDuration(request.getDuration());
            run.setPath(filePath);
            run.setTimestamp(request.getTimestamp());
            run.setScore(request.getScore());
            run.setGroups(groups);

            session.save(run);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }

        return run;
    }

    public static ArrayList<RunDAO> getAllByUser(int userID) throws UserNotFoundException {
        ArrayList<RunDAO> runs = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            UserDAO user = Users.getUser(userID);
            session.update(user);
            runs = (ArrayList<RunDAO>) session.createQuery("from RunDAO where user=?").setParameter(0, user).list();
            for (RunDAO run : runs) {
                Hibernate.initialize(run.getGroups());
            }
            tx.commit();
        } catch(Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
        return runs;
    }

    public static RunDAO getRunByID(int userID, int runID) throws RunNotFoundException, UserNotFoundException {
        RunDAO result;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            result = (RunDAO) session.createQuery("from RunDAO where runID=?").setParameter(0, runID).uniqueResult();
            if (result == null) {
                throw new RunNotFoundException();
            }
            Hibernate.initialize(result.getGroups());
            result.setUser(Users.getUser(userID));
            tx.commit();
        } catch(Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    private static String uploadFile(File file) throws IOException, FileUploadException {
        //TODO change to proper directory
        String dirPath = "/home/franschl/Documents/Studienarbeit/gpx/";
        String fileName = String.valueOf(System.currentTimeMillis()) + ".gpx";
        if (file == null || !file.exists()) {
            throw new FileUploadException();
        }
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(fis);

        //file name collision handling, though it is very unlikely
        while (Files.exists(Paths.get(dirPath + fileName))) {
            fileName = String.valueOf(System.currentTimeMillis()) + ".gpx";
        }

        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(dirPath + fileName)));
        stream.write(bytes);
        stream.close();
        return dirPath + fileName;
    }
}
