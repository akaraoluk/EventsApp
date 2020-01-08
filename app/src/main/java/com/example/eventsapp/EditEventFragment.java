package com.example.eventsapp;

import android.Manifest;
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

import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventsapp.PostClass.Post;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class EditEventFragment extends Fragment {

    private int position;
    private String postUser;
    private Post currentPost;

    TextView title,location,deadline,description;
    ImageView image;
    Button save,cancel,delete;
    Bitmap choosenImage;

    public static EditEventFragment newInstance(int position,String postUser,Post post) {
        EditEventFragment fragment = new EditEventFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("postUser",postUser);
        args.putParcelable("post",post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            postUser = getArguments().getString("postUser");
            currentPost = getArguments().getParcelable("post");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        image = view.findViewById(R.id.edit_imageView);
        title = view.findViewById(R.id.edit_title);
        location = view.findViewById(R.id.edit_location);
        deadline = view.findViewById(R.id.edit_deadline);
        description = view.findViewById(R.id.edit_description);
        cancel = view.findViewById(R.id.edit_cancel);
        save = view.findViewById(R.id.edit_save);
        delete = view.findViewById(R.id.edit_delete);

        title.setText(currentPost.getEventTitle());
        location.setText(currentPost.getEventLocation());
        deadline.setText(currentPost.getEventDeadline());
        description.setText(currentPost.getEventDescription());
        image.setImageBitmap(currentPost.getEventImage());
        choosenImage = currentPost.getEventImage();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.whereEqualTo("title",title.getText().toString());

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,2);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditableEventDetailFragment editf = EditableEventDetailFragment.newInstance(position,postUser,currentPost);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fts = fragmentManager.beginTransaction();
                fts.replace(R.id.container,editf);
                fts.commit();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null){
                            ParseObject currentPost = objects.get(0);
                            currentPost.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null){
                                        Toast.makeText(getActivity(), "Post Deleted!", Toast.LENGTH_SHORT).show();
                                        EventListFragment eventListFragment = new EventListFragment();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fts = fragmentManager.beginTransaction();
                                        fts.replace(R.id.container,eventListFragment);
                                        fts.commit();
                                    }else{
                                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                choosenImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();

                final ParseFile parseFile = new ParseFile("image.png",bytes);

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null & objects.size() >0){
                            ParseObject crrPost = objects.get(0);
                            if (parseFile != null){
                                crrPost.put("image",parseFile);
                            }
                            crrPost.put("title",title.getText().toString());
                            crrPost.put("location",location.getText().toString());
                            crrPost.put("deadline",deadline.getText().toString());
                            crrPost.put("description",description.getText().toString());
                            crrPost.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null){
                                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(getActivity(), "Post Edited!", Toast.LENGTH_SHORT).show();
                                        EventListFragment eventListFragment = new EventListFragment();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fts = fragmentManager.beginTransaction();
                                        fts.replace(R.id.container,eventListFragment);
                                        fts.addToBackStack(null);
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
