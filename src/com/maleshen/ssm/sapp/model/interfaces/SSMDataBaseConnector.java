package com.maleshen.ssm.sapp.model.interfaces;

import java.sql.Connection;

public interface SSMDataBaseConnector {
    /* Return connection for
    *   SSM DataBase
     */
    Connection getConnection() throws ClassNotFoundException;
}
