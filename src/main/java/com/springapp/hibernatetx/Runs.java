package com.springapp.hibernatetx;

import com.springapp.clientrequests.CreateRunRequest;
import com.springapp.exceptions.GroupNotFoundException;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.GroupsEntity;
import com.springapp.hibernate.HibernateUtil;
import com.springapp.hibernate.RunsEntity;
import com.springapp.hibernate.UsersEntity;
import org.apache.commons.fileupload.FileUploadException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by franschl on 17.03.15.
 */
public class Runs {

    // Careful: request contains IDs, not objects!
    public static RunsEntity createRun(CreateRunRequest request, int userID) throws FileUploadException, IOException, UserNotFoundException, GroupNotFoundException {
        String filePath = uploadFile(request.getGpxFile());

        HashSet<GroupsEntity> groups = new HashSet<>();
        for (Integer groupID : request.getGroups()) {
            groups.add(Groups.getGroup(groupID));
        }

        UsersEntity user = Users.getUser(userID);
        RunsEntity run = new RunsEntity();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            //Update the persistent instance with the identifier of the given detached instance.
            for (GroupsEntity group : groups) {
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
            e.printStackTrace();
        } finally {
            session.close();
        }

        return run;
    }

    private static String uploadFile(File file) throws IOException, FileUploadException {
        //TODO change to proper directory
        String dirPath = "/home/franschl/Documents/Studienarbeit/uploadTest/";
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
