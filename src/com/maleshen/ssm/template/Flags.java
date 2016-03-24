package com.maleshen.ssm.template;

public class Flags {
    //Authentication and reg flags
    public static final String USER = SsmCrypt.getMD5("user_msg");
    public static final String AUTH_BAD = SsmCrypt.getMD5("auth_bad");
    public static final String AUTH_REQ = SsmCrypt.getMD5("get_auth");
    public static final String REGME = SsmCrypt.getMD5("register_me");
    public static final String REGFAULT = SsmCrypt.getMD5("not_registered");
    public static final String MSG_BROKEN = SsmCrypt.getMD5("unknown_error");
    //Messages flags
    public static final String UNICAST_MSG = SsmCrypt.getMD5("unicast");
    public static final String MULTICAST_MSG = SsmCrypt.getMD5("multicast");
}
