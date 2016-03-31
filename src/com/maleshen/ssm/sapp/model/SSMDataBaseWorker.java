package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.entity.ArrayListExt;
import com.maleshen.ssm.entity.Message;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.sapp.model.interfaces.SSMDataBaseConnector;
import com.maleshen.ssm.security.SsmCrypt;
import com.mysql.jdbc.PreparedStatement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SSMDataBaseWorker {

    private static SSMDataBaseConnector ssmDataBaseConnector = new SSMSimpleDataBaseConnector();
    private static ResultSet resultSet;

    public static boolean putMessage(Message m) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(SQLQueries.PUT_MESSAGE);
        preparedStatement.setString(1, m.getFromUser());
        preparedStatement.setString(2, m.getToUser());
        preparedStatement.setString(3, m.getTime());
        preparedStatement.setString(4, m.getMsg());

        return preparedStatement.execute();
    }

    public static ObservableList<Message> getMessagesForUser(String userLogin) throws ClassNotFoundException, SQLException {
        ObservableList<Message> messages = FXCollections.observableArrayList();

        PreparedStatement preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(SQLQueries.GET_MESSAGES_FOR_USER);
        preparedStatement.setString(1, userLogin);

        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            String from = resultSet.getString("fromUserID");
            String time = resultSet.getString("time");
            String msg = resultSet.getString("msg");

            messages.add(new Message(from, userLogin, msg, time, false));
        }

        preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(SQLQueries.DELETE_DELIVERED_MSGS);
        preparedStatement.setString(1, userLogin);

        preparedStatement.execute();

        return messages;
    }


    //Getting contact list
    public static ArrayListExt<User> getContactList(int userID) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(SQLQueries.GET_CONTACT_LIST);
        preparedStatement.setInt(1, userID);

        resultSet = preparedStatement.executeQuery();

        ArrayListExt<User> contacts = new ArrayListExt<>();

        while (resultSet.next()){
            Integer id = resultSet.getInt("id");
            String login = resultSet.getString("login");
            String name = resultSet.getString("name") != null ? resultSet.getString("name") : "Noname";
            String lastName = resultSet.getString("lastname") != null ? resultSet.getString("lastname") : "Nolastname";
            Date birthdate = resultSet.getDate("birthdate") != null ? resultSet.getDate("birthdate") : new Date();

            contacts.add(new User(id, login, name, lastName, birthdate));
        }

        return contacts;
    }

    //Getting USER by login
    static User getUserByLogin(String login) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(SQLQueries.GET_USER_BY_LOGIN);
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

    static User getUserByID(Integer id) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(SQLQueries.GET_USER_BY_ID);
        preparedStatement.setInt(1, id);

        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()){
            String newLogin = resultSet.getString("login");
            String newPass = resultSet.getString("hash_pass");
            String name = resultSet.getString("name") != null ? resultSet.getString("name") : "Noname";
            String lastName = resultSet.getString("lastname") != null ? resultSet.getString("lastname") : "Nolastname";
            Date birthdate = resultSet.getDate("birthdate") != null ? resultSet.getDate("birthdate") : new Date();

            return new User(id, newLogin, newPass, name, lastName, birthdate);
        }
        return null;
    }

    /** User registration
    *   @param user User formed on registration form
    *   @return null if user not added to database
    *           user with userID if added.
     */
    public static User registerUser(User user) throws ClassNotFoundException, SQLException {

        PreparedStatement preparedStatement = (PreparedStatement) ssmDataBaseConnector.getConnection()
                .prepareStatement(SQLQueries.USER_REGISTRATION);
        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setString(2, SsmCrypt.getHashPass(user.getPass()));
        preparedStatement.setString(3, user.getName());
        preparedStatement.setString(4, user.getLastName());
        preparedStatement.setDate(5, new java.sql.Date(user.getBirthDate().getTime()));

        preparedStatement.execute();

        return getUserByLogin(user.getLogin());
    }
}
