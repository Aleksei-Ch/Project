package com.maleshen.ssm.sapp.model.logic;

import com.maleshen.ssm.sapp.model.interfaces.DBConnector;
import com.maleshen.ssm.template.Flags;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class DBConnectorProp implements DBConnector {
    private static final String propURL = "./resources/server/ssms.properties";
    private static final Properties properties = new Properties();
    private static Connection con;

    @Override
    public Connection getConnection() throws ClassNotFoundException {
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(propURL));

            Class.forName(properties.getProperty(Flags.DRIVER));

            con = DriverManager.getConnection(properties.getProperty(Flags.URL), properties);

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
        return con;
    }
}
