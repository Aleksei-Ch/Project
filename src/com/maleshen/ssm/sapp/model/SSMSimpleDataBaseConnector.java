package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.sapp.model.interfaces.SSMDataBaseConnector;

import java.sql.*;
import java.util.Properties;

public class SSMSimpleDataBaseConnector implements SSMDataBaseConnector {
    private static final String url = "jdbc:mysql://localhost:3306/ssm";
    private static final String user = "ssm";
    private static final String password = "NyQign8S";

    // JDBC variables for opening and managing connection
    private static Connection con;

    @Override
    public Connection getConnection() throws ClassNotFoundException {

        Class.forName("com.mysql.jdbc.Driver");

        try {
            // opening database connection to MySQL server
            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "UTF-8");

            con = DriverManager.getConnection(url, properties);

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return con;
    }
}
