package com.maleshen.ssm.template;

import com.maleshen.ssm.security.SsmCrypt;

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
    public static final String MSG_RECEIVED = SsmCrypt.getMD5("msg_received");
    public static final String GET_CONTACTS = SsmCrypt.getMD5("get_contacts");
    public static final String SET_CONTACTS = SsmCrypt.getMD5("set_contacts");

    public static final String FOUND_REQ = SsmCrypt.getMD5("found_contacts");

    //Splitters
    public static final String USER_SPLITTER = "<<USPL>>";
    public static final String MESSAGE_SPLITTER = "<<MSPL>>";
    public static final String ARRAYLISTEXT_SPLITTER = "<<ARESPL>>";
    public static final String AUTHINFO_SPLITTER = "<<AUSPL>>";
    public static final String FOUND_SPLITTER = "<<FORFOUND>>";
    public static final String SETTER_SPLITTER = "<<FORSET>>";

}
