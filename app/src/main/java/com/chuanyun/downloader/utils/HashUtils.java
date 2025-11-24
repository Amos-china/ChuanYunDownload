package com.chuanyun.downloader.utils;

import java.security.MessageDigest;


public class HashUtils {
    public static String getMd5Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
