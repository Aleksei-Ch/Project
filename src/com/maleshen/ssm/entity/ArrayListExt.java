package com.maleshen.ssm.entity;

import com.maleshen.ssm.template.Flags;

import java.util.ArrayList;

public class ArrayListExt<User> extends ArrayList<User> {

    /**  All of we need here -
    *   get string representation of contact list.
    *   It's a holly shit, but work fun.
     */
    @Override
    public String toString(){

        StringBuilder result = new StringBuilder();

        result.append(Flags.GET_CONTACTS);

        for(User u : this){
            result.append(Flags.ARRAYLISTEXT_SPLITTER)
                    .append(u.toString());
        }

        return result.toString();
    }

    public static ArrayListExt<com.maleshen.ssm.entity.User> getFromString(String s){

        ArrayListExt<com.maleshen.ssm.entity.User> contacts = new ArrayListExt<>();

        String[] tmp = s.split(Flags.ARRAYLISTEXT_SPLITTER);

        if (tmp[0].equals(Flags.GET_CONTACTS) && tmp.length > 1){
            for (int i = 1; i < tmp.length; i++) {
                contacts.add(com.maleshen.ssm.entity.User.getFromString(tmp[i]));
            }
        }

        return contacts;
    }
}
