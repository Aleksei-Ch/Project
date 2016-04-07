package com.maleshen.ssm.sapp.model.security;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MsgManager {
    @Contract(pure = true)
    public static String createMsg(String header) {
        return header + "\n";
    }

    @Contract(pure = true)
    public static String createMsg(String header, String msg) {
        return header + msg + "\n";
    }

    @NotNull
    public static String getMsgData(String header, String msg) {
        return msg.substring(header.toCharArray().length);
    }
}
