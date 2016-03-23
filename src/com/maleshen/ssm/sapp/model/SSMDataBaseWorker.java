package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.sapp.model.interfaces.SSMDataBaseConnector;
import com.mysql.jdbc.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SSMDataBaseWorker {
    private static final String GET_USER_BY_LOGIN = "SELECT * FROM users WHERE login = ?";
    private static final String USER_REGISTRATION = "INSERT INTO users VALUES (?,?,?,?,?)";

    private static SSMDataBaseConnector ssmDataBaseConnector = new SSMSimpleDataBaseConnector();
    private static ResultSet resultSet;

    //Getting USER by login
    static User getUserByLogin(String login) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(GET_USER_BY_LOGIN);
        preparedStatement.setString(1, login);

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()){
            Integer newId = resultSet.getInt("id");
            String newLogin = resultSet.getString("login");
            String newPass = resultSet.getString("hash_pass");
            String name = resultSet.getString("name") != null ? resultSet.getString("name") : "Noname";
            String lastName = resultSet.getString("lastname") != null ? resultSet.getString("lastname") : "Nolastname";
            Date birthdate = resultSet.getDate("birthdate") != null ? resultSet.getDate("birthdate") : new Date();

            return new User(newId, newLogin, newPass, name, lastName, birthdate);
        }

        return null;
    }

    /* User registration
    *   @param User formed on registration form
    *   @return null if user not added to database
    *           user with userID if added.
     */
    public static User registerUser(User user) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(USER_REGISTRATION);
        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setString(2, user.getPass());
        preparedStatement.setString(3, user.getName());
        preparedStatement.setString(4, user.getLastName());
        preparedStatement.setDate(5, (java.sql.Date) user.getBirthDate());

        if (preparedStatement.execute()){
            return getUserByLogin(user.getLogin());
        }

        return null;
    }
}
