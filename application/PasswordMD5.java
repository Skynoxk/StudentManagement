package application;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordMD5 {
	 public static String hashPassword(String password) {
	        try {
	            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(password.getBytes()));
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException("Error hashing password", e);
	        }
	    }
}