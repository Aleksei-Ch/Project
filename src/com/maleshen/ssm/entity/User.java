package com.maleshen.ssm.entity;

import com.maleshen.ssm.template.Flags;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type User.
 */
public class User {
    private static final DateFormat DATEFROMSTR = new SimpleDateFormat("MMMM d, yyyy");
    private final Format DATETOSTR = new SimpleDateFormat("MMMM d, yyyy");
    private Integer id;
    private String login;
    private String pass;
    private String name;
    private String lastName;
    private Date birthDate;

    public User() {
        this.id = 0;
        this.login = "";
        this.name = "";
        this.lastName = "";
        this.birthDate = new Date();
    }

    public User(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    public User(Integer id, String login, String name, String lastName, Date birthDate) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public User(String login, String pass, String name, String lastName, Date birthDate) {
        this.login = login;
        this.pass = pass;
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public User(Integer id, String login, String pass, String name, String lastName, Date birthDate) {
        this.id = id;
        this.login = login;
        this.pass = pass;
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public User(String login, String name, String lastName, Date birthDate) {
        this.login = login;
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    /**
     * Parse user from string representation of entity
     *
     * @param user the String representation of User
     * @return null if input string not contains pattern
     * Parsed user if all ok.
     */
    public static User getFromString(String user) {

        if (user.split(Flags.USER_SPLITTER).length == 5) {

            //Try parse birthDate
            //Format: "January 2, 2010"
            Date birthdate;

            try {
                birthdate = DATEFROMSTR.parse(user.split(Flags.USER_SPLITTER)[4]);
            } catch (ParseException e) {
                birthdate = new Date();
            }

            return new User(Integer.parseInt(user.split(Flags.USER_SPLITTER)[0]), //id
                    user.split(Flags.USER_SPLITTER)[1],         //login
                    user.split(Flags.USER_SPLITTER)[2],         //name
                    user.split(Flags.USER_SPLITTER)[3],         //lastName
                    birthdate);                      //birthDate
        }
        return null;
    }

    /**
     * Parse user from string representation of regInfo
     * from upper method.
     *
     * @param regInfo String representation of regInfo
     * @return null if input string not contains pattern
     * Parsed user if all ok.
     */
    public static User getUserFromRegInfo(String regInfo) {

        if (regInfo.split(Flags.USER_SPLITTER).length == 5) {

            //Try parse birthDate
            //Format: "January 2, 2010"
            Date birthdate;

            try {
                birthdate = DATEFROMSTR.parse(regInfo.split(Flags.USER_SPLITTER)[4]);
            } catch (ParseException e) {
                birthdate = new Date();
            }

            return new User(regInfo.split(Flags.USER_SPLITTER)[0], //login
                    regInfo.split(Flags.USER_SPLITTER)[1],         //pass
                    regInfo.split(Flags.USER_SPLITTER)[2],         //name
                    regInfo.split(Flags.USER_SPLITTER)[3],         //lastName
                    birthdate);                      //birthDate
        }
        return null;
    }

    public static User getUserFromUpdateInfo(String updateInfo) {

        if (updateInfo.split(Flags.USER_SPLITTER).length == 4) {

            //Try parse birthDate
            //Format: "January 2, 2010"
            Date birthdate;

            try {
                birthdate = DATEFROMSTR.parse(updateInfo.split(Flags.USER_SPLITTER)[3]);
            } catch (ParseException e) {
                birthdate = new Date();
            }

            return new User(updateInfo.split(Flags.USER_SPLITTER)[0], //login
                    updateInfo.split(Flags.USER_SPLITTER)[1],         //name
                    updateInfo.split(Flags.USER_SPLITTER)[2],         //lastname
                    birthdate);                      //birthDate
        } else if (updateInfo.split(Flags.USER_SPLITTER).length == 5) {
            return getUserFromRegInfo(updateInfo);
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDateString() {
        return (new SimpleDateFormat("dd.MM.yyyy")).format(getBirthDate());
    }

    @Override
    public String toString() {

        return getId() +
                Flags.USER_SPLITTER +
                getLogin() +
                Flags.USER_SPLITTER +
                getName() +
                Flags.USER_SPLITTER +
                getLastName() +
                Flags.USER_SPLITTER +
                DATETOSTR.format(getBirthDate());
    }

    /**
     * Get reg info string.
     *
     * @return the string
     */
    public String getRegInfo() {

        return getLogin() +
                Flags.USER_SPLITTER +
                getPass() +
                Flags.USER_SPLITTER +
                getName() +
                Flags.USER_SPLITTER +
                getLastName() +
                Flags.USER_SPLITTER +
                DATETOSTR.format(getBirthDate());
    }

    public String getUpdateInfo() {

        return getPass() == null || getPass().equals("")
                ?
                getLogin() +
                        Flags.USER_SPLITTER +
                        getName() +
                        Flags.USER_SPLITTER +
                        getLastName() +
                        Flags.USER_SPLITTER +
                        DATETOSTR.format(getBirthDate())
                :
                getRegInfo();
    }
}
