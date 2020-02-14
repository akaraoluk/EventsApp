package com.example.eventsapp.PostClass;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventsapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PostAdapter extends ArrayAdapter<Post>{

    private List<Post> posts;
    private Activity context;

    public PostAdapter(List<Post> posts,Activity context){
        super(context, R.layout.custom_view,posts);
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.custom_view,null,true);
        TextView usernameText = customView.findViewById(R.id.custom_username);
        TextView descriptionText = customView.findViewById(R.id.custom_description);
        TextView deadlineText = customView.findViewById(R.id.custom_deadline);
        TextView titleText = customView.findViewById(R.id.custom_title);
        TextView locationText = customView.findViewById(R.id.custom_location);
        ImageView imageView = customView.findViewById(R.id.custom_imageView);
        Post post = posts.get(position);

        usernameText.setText(post.getUsername());
        descriptionText.setText(post.getEventDescription());
        deadlineText.setText(post.getEventDeadline());
        titleText.setText(post.getEventTitle());
        locationText.setText(post.getEventLocation());
        imageView.setImageBitmap(post.getEventImage());

        return customView;
    }
}
