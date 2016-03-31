package com.maleshen.ssm.entity;

import com.maleshen.ssm.template.Flags;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    private final String msg;
    private final User fromUser;
    private final String toUser;
    private boolean delivered;
    private int id = 0;
    private String time;

    private static final String regexp = "^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$";
    private final Pattern timePatt = Pattern.compile(regexp);
    private Matcher matcher;

    private static final String splitter = "<<SPL<<";

    public Message(User fromUser, String toUser, String msg, String time, boolean delivered) {
        this.msg = msg;
        this.fromUser = fromUser;
        this.toUser = toUser;
        matcher = timePatt.matcher(time);
        this.time = matcher.matches() ? time : "00:00";
        this.delivered = delivered;
    }

    /**
     * Sets delivered status.
     *
     * @param delivered New value of delivered.
     */
    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    /**
     * Gets delivered.
     *
     * @return Value of delivered.
     */
    public boolean isDelivered() {
        return delivered;
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
    public User getFromUser() {
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
        return Flags.UNICAST_MSG +
                splitter +
                getTime() +
                splitter +
                getFromUser().toString() +
                splitter +
                getToUser() +
                splitter +
                getMsg();
    }

    public static Message getFromString(String msg){
        String[] parser = msg.split(splitter);

        if (parser.length >= 5 &&
                parser[0].equals(Flags.UNICAST_MSG)){
            String time = parser[1];
            User fromUser = User.getFromString(parser[2]);
            String toUser = parser[3];
            String message = "";
            if (parser.length > 5){
                for (int i = 4; i < parser.length; i++){
                    message = parser[i];
                    if (i != parser.length - 1){
                        message = message + splitter;
                    }
                }
            } else {
                message = parser[4];
            }
            if (fromUser != null && toUser != null){
                return new Message(fromUser, toUser, message, time, false);
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
