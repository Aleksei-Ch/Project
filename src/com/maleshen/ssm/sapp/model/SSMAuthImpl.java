package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.sapp.model.interfaces.SSMAuth;
import com.maleshen.ssm.template.SsmCrypt;

import java.sql.SQLException;

public class SSMAuthImpl implements SSMAuth {

    @Override
    public User getAuthentication(AuthInfo authInfo) {
        //First time look existing user in DataBase
        try {
            User user = SSMDataBaseWorker.getUserByLogin(authInfo.getLogin());
            //Validate pass
            if (user == null){
                return null;
            } else {
                return SsmCrypt.check(authInfo.getPass(), user.getPass()) ? user : null;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
