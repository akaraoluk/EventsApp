package com.example.eventsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventsapp.PostClass.Post;
import com.parse.ParseUser;

public class EditableEventDetailFragment extends Fragment {

    private int position;
    private String postUser;
    private Post currentPost;

    TextView title,location,deadline,description;
    ImageView image;
    Button back,edit;
    Double latitude,longitude;
    String address;

    public static EditableEventDetailFragment newInstance(int position, String postUser,Post post) {
        EditableEventDetailFragment editf = new EditableEventDetailFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("postUser", postUser);
        args.putParcelable("post",post);
        editf.setArguments(args);
        return editf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        if (getArguments() != null) {
            position = getArguments().getInt("position");
            postUser = getArguments().getString("postUser");
            currentPost = getArguments().getParcelable("post");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editable_event_detail, container, false);

        final String currentUser = ParseUser.getCurrentUser().getUsername();

        back = view.findViewById(R.id.eventedit_back);
        edit = view.findViewById(R.id.eventedit_edit);
        title = view.findViewById(R.id.eventedit_title);
        location = view.findViewById(R.id.eventedit_location);
        deadline = view.findViewById(R.id.eventedit_deadline);
        description = view.findViewById(R.id.eventedit_description);
        image = view.findViewById(R.id.eventedit_imageView);

        title.setText(currentPost.getEventTitle());
        location.setText(currentPost.getEventLocation());
        deadline.setText(currentPost.getEventDeadline());
        description.setText(currentPost.getEventDescription());
        image.setImageBitmap(currentPost.getEventImage());
        address = currentPost.getEventLocation();
        latitude = currentPost.getLatitude();
        longitude = currentPost.getLongitude();

        if (currentUser.equals(postUser)){
            edit.setVisibility(View.VISIBLE);
        }else{
            edit.setVisibility(View.INVISIBLE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventListFragment eventListFragment = new EventListFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fts = fragmentManager.beginTransaction();
                fts.replace(R.id.container,eventListFragment);
                fts.commit();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditEventFragment editEventFragment = EditEventFragment.newInstance(position,postUser,currentPost);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fts = fragmentManager.beginTransaction();
                fts.replace(R.id.container,editEventFragment);
                fts.commit();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MapsActivity2.class);
                intent.putExtra("info","old");
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("loc",address);
                startActivityForResult(intent,7);
            }
        });

        return view;
    }
}
