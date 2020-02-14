package com.example.eventsapp;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class CheckData {

    public String findUser(String username){

        final String[] name = new String[1];

        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("username",username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (final ParseObject object : objects){
                    name[0] = object.getString("name");
                }
            }
        });

        return name[0];
    }

}
