package com.example.eventsapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

public class RegisterFragment extends Fragment{

    Button btnCancel,btnRegister;
    EditText nameSurnameText,usernameText,passwordText,emailText,phoneText,departmantText;
    ImageView imageView;
    Bitmap choosenImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        btnCancel = view.findViewById(R.id.cancel);
        btnRegister = view.findViewById(R.id.register);
        usernameText = view.findViewById(R.id.username);
        passwordText = view.findViewById(R.id.password);
        nameSurnameText = view.findViewById(R.id.name);
        emailText = view.findViewById(R.id.email);
        phoneText = view.findViewById(R.id.phone);
        departmantText =  view.findViewById(R.id.departmant);
        imageView = view.findViewById(R.id.register_imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else{
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container,loginFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                user.setUsername(usernameText.getText().toString());
                user.setPassword(passwordText.getText().toString());
                user.setEmail(emailText.getText().toString());
                user.put("name",nameSurnameText.getText().toString());
                user.put("phone",phoneText.getText().toString());
                user.put("departmant",departmantText.getText().toString());

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                if (choosenImage != null){
                    choosenImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();

                    ParseFile file = new ParseFile("image.png",bytes);
                    ParseObject profile_images = new ParseObject("profileImages");

                    profile_images.put("profilepic",file);
                    profile_images.put("username",usernameText.getText().toString());

                    profile_images.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                e.printStackTrace();
                            }else{
                                Log.i("AppInfo", "Profile pic Success");
                            }
                        }
                    });
                }

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e !=  null){
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext(), "User Created", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(),MainActivity.class);
                            intent.putExtra("username",usernameText.getText().toString());
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 & resultCode == getActivity().RESULT_OK & data != null){
            Uri uri = data.getData();

            try{
                if (Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(),uri);
                    choosenImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(choosenImage);
                }else{
                    choosenImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                    imageView.setImageBitmap(choosenImage);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
