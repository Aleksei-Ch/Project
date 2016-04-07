package com.maleshen.ssm.sapp.model.security;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class Crypt {
    private static int workload = 4;

    //Hash password by BCrypt
    @Nullable
    public static String getHashPass(String s) {
        String salt = BCrypt.gensalt(workload);
        return s != null ? BCrypt.hashpw(s, salt) : null;
    }

    //Check authority password
    @Contract("null, _ -> false; !null, null -> false")
    public static boolean check(String s1, String s2) {
        return !(s1 == null || s2 == null) && BCrypt.checkpw(s1, s2);
    }
}