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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventsapp.PostClass.Post;
import com.example.eventsapp.PostClass.User;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ProfileDetailFragment extends Fragment {

    Button btnEdit,btnCancel,btnSave,btnBack;
    TextView txtName,txtEmail,txtPhone,txtDepartment;
    EditText editName,editEmail,editPhone,editDepartment;
    ImageView image;
    Bitmap choosenImage;
    ParseFile parseFile;
    User currentUser;

    public static ProfileDetailFragment newInstance(User user) {
        ProfileDetailFragment profileDetailFragment = new ProfileDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("user",user);
        profileDetailFragment.setArguments(args);
        return profileDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentUser = getArguments().getParcelable("user");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_detail, container, false);

        btnEdit = view.findViewById(R.id.profilefrag_edit);
        btnCancel = view.findViewById(R.id.profilefrag_cancel);
        btnBack = view.findViewById(R.id.profilefrag_back);
        btnSave = view.findViewById(R.id.profilefrag_save);
        image = view.findViewById(R.id.profilefrag_image);
        image.setEnabled(false);
        txtName = view.findViewById(R.id.profilefrag_name);
        txtEmail = view.findViewById(R.id.profilefrag_email);
        txtPhone = view.findViewById(R.id.profilefrag_phone);
        txtDepartment = view.findViewById(R.id.profilefrag_department);
        editName = view.findViewById(R.id.profilefrag_name_edit);
        editEmail = view.findViewById(R.id.profilefrag_email_edit);
        editPhone = view.findViewById(R.id.profilefrag_phone_edit);
        editDepartment = view.findViewById(R.id.profilefrag_department_edit);

        image.setImageBitmap(EventListFragment.profileImage.get(0));
        choosenImage = EventListFragment.profileImage.get(0);

        txtName.setText(currentUser.getName());
        txtEmail.setText(currentUser.getEmail());
        txtPhone.setText(currentUser.getPhone());
        txtDepartment.setText(currentUser.getDepartment());

        editName.setText(currentUser.getName());
        editEmail.setText(currentUser.getEmail());
        editPhone.setText(currentUser.getPhone());
        editDepartment.setText(currentUser.getDepartment());

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,2);
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtName.setVisibility(View.INVISIBLE);
                txtEmail.setVisibility(View.INVISIBLE);
                txtPhone.setVisibility(View.INVISIBLE);
                txtDepartment.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.INVISIBLE);

                editName.setVisibility(View.VISIBLE);
                editEmail.setVisibility(View.VISIBLE);
                editPhone.setVisibility(View.VISIBLE);
                editDepartment.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                image.setEnabled(true);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtName.setVisibility(View.VISIBLE);
                txtEmail.setVisibility(View.VISIBLE);
                txtPhone.setVisibility(View.VISIBLE);
                txtDepartment.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);

                editName.setVisibility(View.INVISIBLE);
                editEmail.setVisibility(View.INVISIBLE);
                editPhone.setVisibility(View.INVISIBLE);
                editDepartment.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventListFragment eventListFragment = new EventListFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fts = fragmentManager.beginTransaction();
                fts.replace(R.id.container,eventListFragment);
                fts.commit();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> profileImageQuery = ParseQuery.getQuery("profileImages");
                profileImageQuery.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());

                ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("User");
                userQuery.whereEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());

                ParseUser user = ParseUser.getCurrentUser();
                user.put("name",editName.getText().toString());
                user.put("email",editEmail.getText().toString());
                user.put("phone",editPhone.getText().toString());
                user.put("departmant",editDepartment.getText().toString());
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e !=  null){
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }else{
                            Log.i("AppInfo", "Profile pic Updated");

                        }
                    }
                });

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                choosenImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                parseFile = new ParseFile("image.png",bytes);

                profileImageQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null){
                            ParseObject profileImage = objects.get(0);
                            if (parseFile != null){
                                profileImage.put("image",parseFile);
                            }
                            profileImage.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null){
                                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }else{
                                        Log.i("AppInfo", "Profile pic Updated");
                                        Toast.makeText(getActivity(), "Profile Updated!", Toast.LENGTH_LONG).show();
                                        EventListFragment eventListFragment = new EventListFragment();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fts = fragmentManager.beginTransaction();
                                        fts.replace(R.id.container,eventListFragment);
                                        fts.commit();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
            if (grantResults.length >0){
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
                    image.setImageBitmap(choosenImage);
                }else{
                    choosenImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
                    image.setImageBitmap(choosenImage);
                }
            }catch (Exception e){
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
