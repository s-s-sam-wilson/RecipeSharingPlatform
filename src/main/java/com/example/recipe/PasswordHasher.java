package com.example.recipe;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String hashPassword(String password) {
        try {
            // Create a SHA-256 digest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Apply the digest to the password bytes
            byte[] hashBytes = digest.digest(password.getBytes());
            // Convert the byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle exception (e.g., log error)
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyPassword(String hashedPassword, String password) {
        // Hash the input password and compare with the hashed password
        return hashPassword(password).equals(hashedPassword);
    }
}