package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;

import java.util.ArrayList;
import java.util.List;

public class SSMAuthNotDB implements SSMAuth {

    private List<User> authList = new ArrayList<>();

    @Override
    public User getAuthentication(AuthInfo authInfo) {
        authList.add(new User("login", "pass"));
        authList.add(new User("logen", "poss"));
        authList.add(new User("ligon", "pess"));

        for (User u: authList) {
            if (authInfo.getLogin().equals(u.getLogin()) &&
                    authInfo.getPass().equals(u.getPass()))
                return u;
        }
        return null;
    }
}
