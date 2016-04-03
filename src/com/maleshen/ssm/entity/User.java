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
    private final Format DATETOSTR = new SimpleDateFormat("MMMM d, yyyy");
    private static final DateFormat DATEFROMSTR = new SimpleDateFormat("MMMM d, yyyy");

    private Integer id;
    private String login;
    private String pass;
    private String name;
    private String lastName;
    private Date birthDate;

    public User() {
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

    @Override
    public String toString(){

        return Flags.USER +
                Flags.USER_SPLITTER +
                getId() +
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
     * Parse user from string representation of entity
     *
     * @param user the String representation of User
     * @return null if input string not contains pattern
     *         Parsed user if all ok.
     */
    public static User getFromString(String user){

        if (user.split(Flags.USER_SPLITTER).length == 6
                && user.split(Flags.USER_SPLITTER)[0].equals(Flags.USER)){

            //Try parse birthDate
            //Format: "January 2, 2010"
            Date birthdate;

            try {
                birthdate = DATEFROMSTR.parse(user.split(Flags.USER_SPLITTER)[5]);
            } catch (ParseException e) {
                birthdate = new Date();
            }

            return new User(Integer.parseInt(user.split(Flags.USER_SPLITTER)[1]), //id
                    user.split(Flags.USER_SPLITTER)[2],         //login
                    user.split(Flags.USER_SPLITTER)[3],         //name
                    user.split(Flags.USER_SPLITTER)[4],         //lastName
                    birthdate);                      //birthDate
        }
        return null;
    }

    /**
     * Get reg info string.
     *
     * @return the string
     */
    public String getRegInfo(){

        return Flags.REGME +
                Flags.USER_SPLITTER +
                getLogin() +
                Flags.USER_SPLITTER +
                getPass() +
                Flags.USER_SPLITTER +
                getName() +
                Flags.USER_SPLITTER +
                getLastName() +
                Flags.USER_SPLITTER +
                DATETOSTR.format(getBirthDate());
    }


    /**  Parse user from string representation of regInfo
     *   from upper method.
     *
     *   @param regInfo String representation of regInfo
     *   @return null if input string not contains pattern
     *           Parsed user if all ok.
     */
    public static User getUserFromRegInfo(String regInfo){

        if (regInfo.split(Flags.USER_SPLITTER).length == 6
                && regInfo.split(Flags.USER_SPLITTER)[0].equals(Flags.REGME)){

            //Try parse birthDate
            //Format: "January 2, 2010"
            Date birthdate;

            try {
                birthdate = DATEFROMSTR.parse(regInfo.split(Flags.USER_SPLITTER)[5]);
            } catch (ParseException e) {
                birthdate = new Date();
            }

            return new User(regInfo.split(Flags.USER_SPLITTER)[1], //login
                    regInfo.split(Flags.USER_SPLITTER)[2],         //pass
                    regInfo.split(Flags.USER_SPLITTER)[3],         //name
                    regInfo.split(Flags.USER_SPLITTER)[4],         //lastName
                    birthdate);                      //birthDate
        }
        return null;
    }
}
