package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;

interface SSMAuth {
    User getAuthentication(AuthInfo authInfo);
}
