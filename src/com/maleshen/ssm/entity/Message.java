package com.maleshen.ssm.entity;

import com.maleshen.ssm.template.Flags;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    private final String msg;
    private final String fromUser;
    private final String toUser;
    private int id = 0;
    private String time;

    private static final String regexp = "^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$";
    private final Pattern timePatt = Pattern.compile(regexp);
    private Matcher matcher;

    public Message(String fromUser, String toUser, String msg, String time) {
        this.msg = msg;
        this.fromUser = fromUser;
        this.toUser = toUser;
        matcher = timePatt.matcher(time);
        this.time = matcher.matches() ? time : "00:00";
    }

    /**
     * Gets toUser.
     *
     * @return Value of toUser.
     */
    public String getToUser() {
        return toUser;
    }

    /**
     * Gets fromUser.
     *
     * @return Value of fromUser.
     */
    public String getFromUser() {
        return fromUser;
    }

    /**
     * Gets msg.
     *
     * @return Value of msg.
     */
    public String getMsg() {
        return msg;
    }



    @Override
    public String toString(){
        return  getTime() +
                Flags.MESSAGE_SPLITTER +
                getFromUser() +
                Flags.MESSAGE_SPLITTER +
                getToUser() +
                Flags.MESSAGE_SPLITTER +
                getMsg();
    }

    public static Message getFromString(String msg){
        String[] parser = msg.split(Flags.MESSAGE_SPLITTER);

        if (parser.length >= 4){
            String time = parser[0];
            String fromUser = parser[1];
            String toUser = parser[2];
            String message = "";
            if (parser.length > 4){
                for (int i = 3; i < parser.length; i++){
                    message = parser[i];
                    if (i != parser.length - 1){
                        message = message + Flags.MESSAGE_SPLITTER;
                    }
                }
            } else {
                message = parser[3];
            }
            if (fromUser != null && toUser != null){
                return new Message(fromUser, toUser, message, time);
            }
        }
        return null;
    }

    /**
     * Sets new id.
     *
     * @param id New value of id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets id.
     *
     * @return Value of id.
     */
    public int getId() {
        return id;
    }


    /**
     * Sets new time.
     *
     * @param time New value of time.
     */
    public void setTime(String time) {
        matcher = timePatt.matcher(time);
        this.time = matcher.matches() ? time : "00:00";
    }

    /**
     * Gets time.
     *
     * @return Value of time.
     */
    public String getTime() {
        return time;
    }
}
