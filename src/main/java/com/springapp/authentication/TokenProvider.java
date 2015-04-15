package com.springapp.authentication;

import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import com.springapp.exceptions.UserNotFoundException;
import com.springapp.hibernate.UserDAO;
import com.springapp.transactional.Users;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by franschl on 06.04.15.
 */
public class TokenProvider {

    static String FENCE_POST = "!!!";
    private static final String NEWLINE = "\r\n";
    /*
    * Token:
    * username!!!expiry!!!hashedToken (Base64-encoded)
    *
    * hashedToken:
    * username!!!salt!!!expiry (MD5-Hashed)
    * */

    /*public TokenProvider(String secretKey) {
        try {
            md5er = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot find MD5 algorithm",e);
        }

        if(StringUtils.isEmpty(secretKey)){
            throw new IllegalArgumentException("Secret key must be set");
        }

        this.secretKey = secretKey;
    }*/

    public String getToken(UserDAO user) {
        return getToken(user, DateTime.now().plusDays(1).getMillis());
    }

    public String getToken(UserDAO user, long expirationDateInMillis) {

        StringBuilder tokenBuilder = new StringBuilder();

        byte[] token = tokenBuilder
                .append(user.getNick())
                .append(FENCE_POST)
                .append(expirationDateInMillis)
                .append(FENCE_POST)
                .append(new String(buildTokenKey(expirationDateInMillis, user)))
                .toString().getBytes();

        // returns a value ending in a newline, remove it
        return Base64.encodeBase64String(token).replace(NEWLINE, "");
    }

    public boolean isTokenValid(String encodedToken) {
        String[] components = decodeAndDissectToken(encodedToken);

        if (components == null || components.length != 3) {
            return false;
        }

        String nick = components[0];
        long externalDate = Long.parseLong(components[1]);
        String externalKey  = components[2];

        UserDAO user;
        try {
            user = Users.getUserByName(nick);
        } catch (UserNotFoundException e) {
            return false;
        }
        String expectedKey = user.getAuthToken();

        byte[] expectedKeyBytes = expectedKey.getBytes();
        byte[] externalKeyBytes = externalKey.getBytes();

        if (!MessageDigest.isEqual(expectedKeyBytes, externalKeyBytes)) {
            return false;
        }

        //TODO make dateTime anonymous
        DateTime tokenTime = new DateTime(externalDate * 1000l);
        DateTime now = new DateTime();

        if (tokenTime.isBeforeNow()) {
            return false;
        }

        return true;
    }

    private byte[] buildTokenKey(long expirationDateInMillis, UserDAO user) {
        StringBuilder keyBuilder = new StringBuilder();
        String key = keyBuilder
                .append(user.getNick())
                .append(FENCE_POST)
                .append(user.getSalt())
                .append(FENCE_POST)
                .append(expirationDateInMillis)
                .toString();

        byte[] keyBytes = key.getBytes();
        try {
            MessageDigest md5er = MessageDigest.getInstance("MD5");
            return md5er.digest(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserDAO getUserFromToken(String token) {
        if (!isTokenValid(token)) { return null; }
        String[] components = decodeAndDissectToken(token);
        if (components == null || components.length != 3) { return null; }
        String nick = components[0];
        try {
            return Users.getUserByName(nick);
        } catch (UserNotFoundException e) {
            return null;
        }
    }

    private String[] decodeAndDissectToken(String encodedToken) {
        if(StringUtils.isEmpty(encodedToken) || !Base64.isArrayByteBase64(encodedToken.getBytes())) {
            return null;
        }

        // Apache Commons Base64 expects encoded strings to end with a newline, add one
        if(!encodedToken.endsWith(NEWLINE)) { encodedToken = encodedToken + NEWLINE; }

        String token = new String(Base64.decodeBase64(encodedToken));

        if(!token.contains(FENCE_POST) || token.split(FENCE_POST).length != 3) {
            return null;
        }

        return token.split(FENCE_POST);
    }
}

