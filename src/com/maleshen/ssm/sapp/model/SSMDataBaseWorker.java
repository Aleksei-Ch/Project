package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.sapp.model.interfaces.SSMDataBaseConnector;
import com.mysql.jdbc.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SSMDataBaseWorker {
    private static final String GET_USER_BY_LOGIN = "SELECT * FROM users WHERE login = ?";

    private static SSMDataBaseConnector ssmDataBaseConnector = new SSMSimpleDataBaseConnector();
    private static ResultSet resultSet;

    //Getting USER by login
    public static User getUserByLogin(String login) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(GET_USER_BY_LOGIN);
        preparedStatement.setString(1, login);

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()){
            Integer newId = resultSet.getInt("id");
            String newLogin = resultSet.getString("login");
            String newPass = resultSet.getString("hash_pass");
            return new User(newId, newLogin, newPass);
        }

        return null;
    }
}
