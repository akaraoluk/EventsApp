package com.example.eventsapp;

import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testParse() {
        assertEquals(findUser(),"asd");
    }

    public String findUser(){

        final String[] name = new String[1];

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.whereEqualTo("title","FirstUpload");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (final ParseObject object : objects){
                    name[0] = object.getString("deadline");
                }
            }
        });

        return name[0];
    }
}