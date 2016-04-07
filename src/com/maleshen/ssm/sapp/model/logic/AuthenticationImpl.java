package com.maleshen.ssm.sapp.model.logic;

import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.sapp.model.interfaces.Authentication;
import com.maleshen.ssm.sapp.model.security.Crypt;

import java.sql.SQLException;

public class AuthenticationImpl implements Authentication {

    @Override
    public User getAuthentication(AuthInfo authInfo) {
        //First time look existing user in DataBase
        try {
            User user = DBWorker.getUserByLogin(authInfo.getLogin());
            //Validate pass
            if (user == null) {
                return null;
            } else {
                return Crypt.check(authInfo.getPass(), user.getPass()) ? user : null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
