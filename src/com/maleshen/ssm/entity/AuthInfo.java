package com.maleshen.ssm.entity;

import com.maleshen.ssm.template.Flags;

import java.io.Serializable;

public class AuthInfo implements Serializable {
    private String login = "";
    private String pass = "";

    public AuthInfo(){}

    public AuthInfo(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString(){
        return Flags.AUTH_REQ + " "
                + login + " "
                + pass;
    }

    public static AuthInfo getFromString(String authInfo){
        if (authInfo.split(" ").length == 3 &&
                authInfo.split(" ")[0].equals(Flags.AUTH_REQ)){
            return new AuthInfo(
                    authInfo.split(" ")[1],
                    authInfo.split(" ")[2]
            );
        }
        return null;
    }
}
