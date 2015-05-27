package com.springapp.transactional;

import com.springapp.Config;
import com.springapp.dto.CreateRunRequest;
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
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
        //String filePath = uploadFile(request.getGpxFile());

        HashSet<GroupDAO> groups = new HashSet<>();
        for (Integer groupID : request.getGroupIDs()) {
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
            //run.setPath(filePath);
            run.setTimestamp(request.getTimestamp());
            run.setActualDistance(request.getActualDistance());
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

    public static ArrayList<RunDAO> getThisWeeksRunsByGroup(int groupID) throws GroupNotFoundException {
        ArrayList<RunDAO> resultRuns = new ArrayList<>();
        GroupDAO group;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            group = Groups.getGroup(groupID);
            session.update(group);
            Timestamp refDayTimestamp = getRefDayTimestamp(group.getRefWeekday());
            for (RunDAO run : group.getRuns()) {
                Timestamp runTimestamp = run.getTimestamp();
                if (refDayTimestamp.getTime() < runTimestamp.getTime()) {
                    resultRuns.add(run);
                    Hibernate.initialize(run.getUser());
                }
            }
            tx.commit();
        } catch(Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
        return resultRuns;
    }

    private static Timestamp getRefDayTimestamp(int refDay) {
        DateTime refDate = new LocalDate().toDateTimeAtStartOfDay();
        while (refDate.getDayOfWeek() != (refDay + 1)) {
            refDate = refDate.minusDays(1);
        }
        return new Timestamp(refDate.getMillis());
    }

    private static String uploadFile(File file) throws IOException, FileUploadException {
        String dirPath = Config.getValue("gpxDir");
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
