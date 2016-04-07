package com.maleshen.ssm.entity;

import com.maleshen.ssm.template.Flags;

import java.util.ArrayList;

public class ArrayListExt<User> extends ArrayList<User> {

    public static ArrayListExt<com.maleshen.ssm.entity.User> getFromString(String s) {

        ArrayListExt<com.maleshen.ssm.entity.User> contacts = new ArrayListExt<>();

        String[] entities = s.split(Flags.ARRAYLISTEXT_SPLITTER);

        for (String entity : entities) {
            contacts.add(com.maleshen.ssm.entity.User.getFromString(entity));
        }
        return contacts;
    }

    /**
     * All of we need here -
     * get string representation of contact list.
     * It's a holly shit, but work fun.
     */
    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < this.size(); i++) {
            result.append(this.get(i).toString());
            if (i != this.size() - 1)
                result.append(Flags.ARRAYLISTEXT_SPLITTER);
        }

        return result.toString();
    }
}
