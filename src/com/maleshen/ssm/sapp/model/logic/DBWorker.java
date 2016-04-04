package com.maleshen.ssm.sapp.model.logic;

import com.maleshen.ssm.entity.ArrayListExt;
import com.maleshen.ssm.entity.Message;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.sapp.model.interfaces.DBConnector;
import com.maleshen.ssm.sapp.model.security.Crypt;
import com.mysql.jdbc.PreparedStatement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DBWorker {

    private static DBConnector DBConnector = new DBConnectorImpl();
    private static ResultSet resultSet;

    public static boolean putMessage(Message m) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
                .prepareStatement(SQLQueries.PUT_MESSAGE);
        preparedStatement.setString(1, m.getFromUser());
        preparedStatement.setString(2, m.getToUser());
        preparedStatement.setString(3, m.getTime());
        preparedStatement.setString(4, m.getMsg());

        return preparedStatement.execute();
    }

    public static ObservableList<Message> getMessagesForUser(String userLogin) throws ClassNotFoundException, SQLException {
        ObservableList<Message> messages = FXCollections.observableArrayList();

        PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
                .prepareStatement(SQLQueries.GET_MESSAGES_FOR_USER);
        preparedStatement.setString(1, userLogin);

        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            String from = resultSet.getString("fromUserID");
            String time = resultSet.getString("time");
            String msg = resultSet.getString("msg");

            messages.add(new Message(from, userLogin, msg, time));
        }

        preparedStatement = (PreparedStatement) DBConnector.getConnection()
                .prepareStatement(SQLQueries.DELETE_DELIVERED_MSGS);
        preparedStatement.setString(1, userLogin);

        preparedStatement.execute();

        return messages;
    }

    //Getting contact list
    public static ArrayListExt<User> getContactList(int userID) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
                .prepareStatement(SQLQueries.GET_CONTACT_LIST);
        preparedStatement.setInt(1, userID);

        resultSet = preparedStatement.executeQuery();

        ArrayListExt<User> contacts = new ArrayListExt<>();

        while (resultSet.next()){
            contacts.add(getUserFromResultSet(resultSet));
        }

        return contacts;
    }

    //Getting USER by login
    static User getUserByLogin(String login) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
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
        PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
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

        PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
                .prepareStatement(SQLQueries.USER_REGISTRATION);
        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setString(2, Crypt.getHashPass(user.getPass()));
        preparedStatement.setString(3, user.getName());
        preparedStatement.setString(4, user.getLastName());
        preparedStatement.setDate(5, new java.sql.Date(user.getBirthDate().getTime()));

        preparedStatement.execute();

        return getUserByLogin(user.getLogin());
    }

    public static ArrayListExt<User> foundUsersByKeyword(String keywords) throws ClassNotFoundException, SQLException {
        ArrayListExt<User> resuilts = new ArrayListExt<>();

        for (String keyword : keywords.split(" ")) {
            keyword = "%"+keyword+"%";
            PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
                    .prepareStatement(SQLQueries.FOUND_USERS);
            preparedStatement.setString(1, keyword);
            preparedStatement.setString(2, keyword);
            preparedStatement.setString(3, keyword);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                resuilts.add(getUserFromResultSet(resultSet));
            }
        }

        return resuilts;
    }

    private static User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name") != null ? resultSet.getString("name") : "Noname";
        String lastName = resultSet.getString("lastname") != null ? resultSet.getString("lastname") : "Nolastname";
        Date birthdate = resultSet.getDate("birthdate") != null ? resultSet.getDate("birthdate") : new Date();

        return new User(id, login, name, lastName, birthdate);
    }

    public static void setContacts(int firstID, int secondID) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
                .prepareStatement(SQLQueries.SET_CONTACTS);
        preparedStatement.setInt(1, firstID);
        preparedStatement.setInt(2, secondID);

        preparedStatement.execute();
    }

    public static void removeContacts(int firstID, int secondID) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
                .prepareStatement(SQLQueries.REMOVE_CONTACTS);
        preparedStatement.setInt(1, firstID);
        preparedStatement.setInt(2, secondID);

        preparedStatement.execute();
    }

    public static void updateUser(User user) throws ClassNotFoundException, SQLException {
        if (user.getPass() != null && !user.getPass().equals("")) {
            PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
                    .prepareStatement(SQLQueries.UPDATE_USER_PWD);
            preparedStatement.setString(5, user.getLogin());
            preparedStatement.setString(1, Crypt.getHashPass(user.getPass()));
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setDate(4, new java.sql.Date(user.getBirthDate().getTime()));

            preparedStatement.execute();
        } else {
            PreparedStatement preparedStatement = (PreparedStatement) DBConnector.getConnection()
                    .prepareStatement(SQLQueries.UPDATE_USER);
            preparedStatement.setString(4, user.getLogin());
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setDate(3, new java.sql.Date(user.getBirthDate().getTime()));

            preparedStatement.execute();
        }
    }
}
