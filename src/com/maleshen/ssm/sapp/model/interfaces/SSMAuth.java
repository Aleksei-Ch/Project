package com.maleshen.ssm.sapp.model.interfaces;

import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;

public interface SSMAuth {
    User getAuthentication(AuthInfo authInfo);
}
