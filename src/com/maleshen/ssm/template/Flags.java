package com.maleshen.ssm.template;

public class Flags {
    //Authentication flags
    public static final String AUTH_BAD = SmmCrypt.getMD5("auth_bad");
    public static final String AUTH_GOOD = SmmCrypt.getMD5("auth_good");
    public static final String AUTH_REQ = SmmCrypt.getMD5("get_auth");
    //Messages flags
    public static final String UNICAST_MSG = SmmCrypt.getMD5("unicast");
    public static final String MULTICAST_MSG = SmmCrypt.getMD5("multicast");
}
