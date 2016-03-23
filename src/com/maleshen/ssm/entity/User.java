package com.maleshen.ssm.entity;

import com.maleshen.ssm.template.Flags;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
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


    /*  Return user string implementation
    *   Pattern:
    *   FLAG<SPL>id<<SPL>login<SPL>name<SPL>lastName<SPL>birthDate
    *   <SPL> - splitter
     */
    @Override
    public String toString(){

        String splitter = "<SPL>";

        Format formatter = new SimpleDateFormat("MMMM d, yyyy");

        return Flags.USER +
                splitter +
                getId() +
                splitter +
                getLogin() +
                splitter +
                getName() +
                splitter +
                getLastName() +
                splitter +
                formatter.format(getBirthDate());
    }

    /*  Parse user from string representation of entity
    *   from upper metod.
    *   @param String representation of User
    *       User.getFromString((new User()).toString) must equals new User();
    *   @return null if input string not contains pattern
    *           Parsed user if all ok.
     */
    public static User getFromString(String user){

        String splitter = "<SPL>"; //Pattern like in toString()

        if (user.split(splitter).length == 6
                && user.split(splitter)[0].equals(Flags.USER)){

            //Try parse birthDate
            //Format: "January 2, 2010"
            DateFormat format = new SimpleDateFormat("MMMM d, yyyy");
            Date birthdate;

            try {
                birthdate = format.parse(user.split(splitter)[5]);
            } catch (ParseException e) {
                birthdate = new Date();
            }

            return new User(Integer.parseInt(user.split(splitter)[1]), //id
                    user.split(splitter)[2],         //login
                    user.split(splitter)[3],         //name
                    user.split(splitter)[4],         //lastName
                    birthdate);                      //birthDate
        }
        return null;
    }
}
