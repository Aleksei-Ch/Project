package com.maleshen.ssm.template;

public class Flags {
    //Authentication flags
    public static final String USER = SsmCrypt.getMD5("user_msg");
    public static final String AUTH_BAD = SsmCrypt.getMD5("auth_bad");
    public static final String AUTH_REQ = SsmCrypt.getMD5("get_auth");
    //Messages flags
    public static final String UNICAST_MSG = SsmCrypt.getMD5("unicast");
    public static final String MULTICAST_MSG = SsmCrypt.getMD5("multicast");
}
