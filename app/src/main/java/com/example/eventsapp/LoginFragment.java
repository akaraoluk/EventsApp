package com.example.eventsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {

    Button btnLogin,btnRegister;
    EditText usernameText,passwordText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameText = view.findViewById(R.id.username);
        passwordText = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.login);
        btnRegister = view.findViewById(R.id.register);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LastUser",Context.MODE_PRIVATE);
        String lastUser = sharedPreferences.getString("lastUser","");
        if (lastUser != null){
            usernameText.setText(lastUser);
        }

        Intent intent = this.getActivity().getIntent();
        String registerUsername = intent.getStringExtra("username");
        if (registerUsername != null){
            usernameText.setText(registerUsername);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground(usernameText.getText().toString(), passwordText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null){
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(), "Welcome "+ user.getUsername(), Toast.LENGTH_LONG).show();
                            EventListFragment eventListFragment = new EventListFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fts = fragmentManager.beginTransaction();
                            fts.replace(R.id.container,eventListFragment);
                            fts.addToBackStack(null);
                            fts.commit();
                        }
                    }
                });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fts = fragmentManager.beginTransaction();
                fts.replace(R.id.container,registerFragment);
                fts.addToBackStack(null);
                fts.commit();
            }
        });

        return view;
    }

}
