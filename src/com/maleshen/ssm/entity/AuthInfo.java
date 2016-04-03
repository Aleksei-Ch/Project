package com.maleshen.ssm.entity;

import com.maleshen.ssm.template.Flags;

public class AuthInfo {
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
        return Flags.AUTH_REQ +
                Flags.AUTHINFO_SPLITTER +
                getLogin() +
                Flags.AUTHINFO_SPLITTER +
                getPass();
    }

    public static AuthInfo getFromString(String authInfo){
        if (authInfo.split(Flags.AUTHINFO_SPLITTER).length == 3 &&
                authInfo.split(Flags.AUTHINFO_SPLITTER)[0].equals(Flags.AUTH_REQ)){
            return new AuthInfo(
                    authInfo.split(Flags.AUTHINFO_SPLITTER)[1],
                    authInfo.split(Flags.AUTHINFO_SPLITTER)[2]
            );
        }
        return null;
    }
}
