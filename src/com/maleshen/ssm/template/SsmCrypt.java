package com.maleshen.ssm.template;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SsmCrypt {
    private static int workload = 4;

    //Getting MD5
    static String getMD5(String s) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(s.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }

    //Hash password by BCrypt
    public static String getHashPass(String s){
        String salt = BCrypt.gensalt(workload);
        return s != null ? BCrypt.hashpw(s, salt) : null;
    }

    public static boolean check(String s1, String s2) {
        return !(s1 == null || s2 == null) && BCrypt.checkpw(s1, s2);
    }
}
