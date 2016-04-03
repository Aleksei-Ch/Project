package com.maleshen.ssm.sapp.model;

class SQLQueries {
    static final String GET_USER_BY_LOGIN = "SELECT * FROM users WHERE login = ?";
    static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    static final String USER_REGISTRATION = "INSERT INTO users(login, hash_pass, name, lastname, birthdate) " +
                                            "VALUES (?,?,?,?,?)";
    static final String GET_CONTACT_LIST = "SELECT users.* FROM users, contacts " +
                                           "WHERE users.id = contacts.id_contact AND contacts.id_user = ?";
    static final String PUT_MESSAGE = "INSERT INTO messages_queue(fromUserID, toUser, time, msg) " +
                                      "VALUES (?, ?, ?, ?)";
    static final String GET_MESSAGES_FOR_USER = "SELECT * FROM messages_queue " +
                                                "WHERE toUser = ?";
    static final String DELETE_DELIVERED_MSGS = "DELETE FROM messages_queue WHERE toUser = ?";
    static final String FOUND_USERS = "SELECT * FROM users WHERE login LIKE ? OR name LIKE ? OR lastname LIKE ?";
    static final String SET_CONTACTS = "INSERT INTO contacts(id_user, id_contact) " +
                                        "VALUES (?, ?)";
}
