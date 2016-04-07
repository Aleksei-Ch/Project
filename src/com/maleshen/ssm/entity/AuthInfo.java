package com.maleshen.ssm.entity;

import com.maleshen.ssm.template.Flags;

public class AuthInfo {
    private String login = "";
    private String pass = "";

    public AuthInfo() {
    }

    public AuthInfo(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    public static AuthInfo getFromString(String authInfo) {
        if (authInfo.split(Flags.AUTHINFO_SPLITTER).length == 2) {
            return new AuthInfo(
                    authInfo.split(Flags.AUTHINFO_SPLITTER)[0],
                    authInfo.split(Flags.AUTHINFO_SPLITTER)[1]
            );
        }
        return null;
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
    public String toString() {
        return getLogin() +
                Flags.AUTHINFO_SPLITTER +
                getPass();
    }
}
