package com.maleshen.ssm.sapp.model.interfaces;

import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;

public interface Authentication {
    User getAuthentication(AuthInfo authInfo);
}
