package com.all.credstore.models;

import android.util.Log;

import com.all.credstore.utils.Constants;
import com.all.credstore.utils.Util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;

public final class Encoder {
    private static final String salt = "1021pwdusr2345test12";

    public static String encode(String password) {
        try {
            String saltedPassword = salt + password;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(saltedPassword.getBytes("UTF-8"));
            return toHexString(hash);
        } catch (Exception ex) {
            Log.i(Constants.LOG_TAG, "Exception in Encoder.encode: " + ex.getMessage());
        }
        return null;
    }

    private static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public static boolean validPassword(String dbEncodedPwd, String usrPwd) {
        if(Util.isEmpty(dbEncodedPwd) || Util.isEmpty(usrPwd)) {
            return false;
        }

        return encode(usrPwd).equals(dbEncodedPwd);
    }

    public static String getSecuredPassword(String password) {
        try {
            if(Util.isEmpty(password)) {
                return password;
            }
            String shaEncrypedPass = encode(password);
            String base64Password = base64Encode(password);
            int base64PasswordSize = base64Password.length();
            int base64PasswordSizeLength = String.valueOf(base64PasswordSize).length();
            return base64Encode(shaEncrypedPass + base64Password + base64PasswordSize + base64PasswordSizeLength + encode(salt));
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(Constants.LOG_TAG, "Exception in Encoder.getSecuredPassword: " + ex.getMessage());
            return null;
        }
    }

    public static String getDecodedSecuredPassword(String encodedPassword) {
        try {
            if(Util.isEmpty(encodedPassword)) {
                return encodedPassword;
            }
            String base64DecodeString = base64Decode(encodedPassword);
            String saltRemovedStr = base64DecodeString.replace(encode(salt), "");
            int base64PasswordSizeLength = Integer.parseInt(saltRemovedStr.substring(saltRemovedStr.length()-1));
            String base64PasswordSizeLengthRemovedStr = saltRemovedStr.substring(0, saltRemovedStr.length()-1);
            int base64PasswordSize = Integer.parseInt(base64PasswordSizeLengthRemovedStr.substring(base64PasswordSizeLengthRemovedStr.length()-base64PasswordSizeLength, base64PasswordSizeLengthRemovedStr.length()));
            String base64PasswordSizeRemovedStr = base64PasswordSizeLengthRemovedStr.substring(0, base64PasswordSizeLengthRemovedStr.length()-base64PasswordSizeLength);
            String base64Password = base64PasswordSizeRemovedStr.substring(base64PasswordSizeRemovedStr.length()-base64PasswordSize);
            return base64Decode(base64Password);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(Constants.LOG_TAG, "Exception in Encoder.getDecodedSecuredPassword: " + ex.getMessage());
            return null;
        }
    }

    private static String base64Encode(String inputString) throws Exception {
        if(Util.isEmpty(inputString)) {
            return inputString;
        }
        return Base64.getEncoder().encodeToString(inputString.getBytes("utf-8"));
    }
    private static String base64Decode(String base64EncodedString) throws Exception {
        if(Util.isEmpty(base64EncodedString)) {
            return base64EncodedString;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedString);
        return new String(decodedBytes, "utf-8");
    }
}
