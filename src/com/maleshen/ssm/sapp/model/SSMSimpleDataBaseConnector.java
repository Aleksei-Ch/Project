package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.sapp.model.interfaces.SSMDataBaseConnector;

import java.sql.*;

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
            con = DriverManager.getConnection(url, user, password);

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return con;
    }
}
