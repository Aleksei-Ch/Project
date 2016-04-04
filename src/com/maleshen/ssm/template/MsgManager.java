package com.maleshen.ssm.template;

public class MsgManager {
    public static String createMsg(String header){
        return header + "\n";
    }

    public static String createMsg(String header, String msg){
        return header + msg + "\n";
    }

    public static String getMsgData(String header, String msg){
        return msg.substring(header.toCharArray().length);
    }
}
