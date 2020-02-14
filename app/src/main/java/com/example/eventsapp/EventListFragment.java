package com.example.eventsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventsapp.PostClass.Post;
import com.example.eventsapp.PostClass.PostAdapter;
import com.example.eventsapp.PostClass.User;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment {

    static PostAdapter adapter;
    ListView listView;
    TextView usernameText;
    ImageView imageView,addEvent,logoutImage,myPosts,generalPost;
    Runnable runnable;
    static ArrayList<Bitmap> profileImage = new ArrayList<>();
    static List<Post> posts = new ArrayList<>();
    User currentUser;
    Post post;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //((AppCompatActivity) getActivity()).getSupportActionBar().show();
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        profileImage = new ArrayList<>();

        listView = view.findViewById(R.id.listView);
        usernameText = view.findViewById(R.id.username);
        usernameText.setText(ParseUser.getCurrentUser().getUsername());
        imageView = view.findViewById(R.id.eventlist_image);
        addEvent = view.findViewById(R.id.eventlist_addevent);
        logoutImage = view.findViewById(R.id.eventlist_logout);
        myPosts = view.findViewById(R.id.eventlist_mypost);
        generalPost = view.findViewById(R.id.eventlist_world);

        currentUser = new User(ParseUser.getCurrentUser().getString("name"),ParseUser.getCurrentUser().getString("phone"),
                ParseUser.getCurrentUser().getEmail(),ParseUser.getCurrentUser().getString("departmant"));

        //Get Last Username
        String lastUser = ParseUser.getCurrentUser().getUsername();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("LastUser",Context.MODE_PRIVATE).edit();
        editor.putString("lastUser",lastUser);
        editor.apply();

        //Add New Event
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadPostFragment uploadPostFragment = new UploadPostFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fts = fragmentManager.beginTransaction();
                fts.replace(R.id.container,uploadPostFragment);
                fts.addToBackStack(null);
                fts.commit();
            }
        });

        //Logout
        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent(getActivity(),MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        //User Detail Fragment
        usernameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDetailFragment profileDetailFragment = ProfileDetailFragment.newInstance(currentUser);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fts = fragmentManager.beginTransaction();
                fts.replace(R.id.container,profileDetailFragment);
                fts.commit();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDetailFragment profileDetailFragment = ProfileDetailFragment.newInstance(currentUser);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fts = fragmentManager.beginTransaction();
                fts.replace(R.id.container,profileDetailFragment);
                fts.commit();
            }
        });


        final DownloadPosts downloadPosts = new DownloadPosts();
        downloadPosts.execute("start");

        DownloadPic downloadPic = new DownloadPic();
        downloadPic.execute("start");

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DownloadMyPosts downloadMyPosts = new DownloadMyPosts();
                downloadMyPosts.execute("start");

                //Set Adapter
                adapter = new PostAdapter(posts,getActivity());
                listView.setAdapter(adapter);

                myPosts.setVisibility(View.INVISIBLE);
                generalPost.setVisibility(View.VISIBLE);
            }
        });

        generalPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DownloadPosts downloadPosts = new DownloadPosts();
                downloadPosts.execute("start");

                //Set Adapter
                adapter = new PostAdapter(posts,getActivity());
                listView.setAdapter(adapter);

                myPosts.setVisibility(View.VISIBLE);
                generalPost.setVisibility(View.INVISIBLE);
            }
        });

        //Set Adapter
        adapter = new PostAdapter(posts,getActivity());
        listView.setAdapter(adapter);


        //List Object Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //Assign current post and post username to send fragment
            Post post = posts.get(position);
            String postUser = post.getUsername();

            EditableEventDetailFragment editf = EditableEventDetailFragment.newInstance(position,postUser,post);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fts = fragmentManager.beginTransaction();
            fts.replace(R.id.container,editf);
            fts.addToBackStack(null);
            fts.commit();
            }
        });

        return view;
    }

    class DownloadPosts extends AsyncTask<String, Integer, ArrayList<Bitmap>> {

        @Override
        protected ArrayList<Bitmap> doInBackground(String... strings) {
            posts.clear();
            //Get infos from Parse
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e != null){
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        if (objects.size() > 0){
                            for (final ParseObject object : objects){
                                ParseFile parseFile = (ParseFile) object.get("image");
                                parseFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null & data != null){
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                            post = new Post(object.getString("username"),object.getString("description"),object.getString("deadline"),
                                                    object.getString("title"),object.getString("location"),bitmap,object.getDouble("latitude"),object.getDouble("longitude"));
                                            posts.add(post);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });

            return null;
        }
    }

    class DownloadPic extends AsyncTask<String, Integer, ArrayList<Bitmap>> {

        @Override
        protected ArrayList<Bitmap> doInBackground(String... strings) {
            profileImage.clear();
            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("profileImages");
            query2.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
            query2.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e != null){
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        if (objects.size() >0){
                            Log.i("AppInfo", "Profile pic find");
                            for (ParseObject object : objects){
                                ParseFile parseFile = object.getParseFile("profilepic");
                                parseFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null & data != null){
                                            Log.i("AppInfo", "Profile pic find2");
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                            profileImage.add(bitmap);
                                            imageView.setImageBitmap(profileImage.get(0));
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
            return null;
        }
    }

    class DownloadMyPosts extends AsyncTask<String, Integer, ArrayList<Bitmap>> {

        @Override
        protected ArrayList<Bitmap> doInBackground(String... strings) {
            posts.clear();
            //Get infos from Parse
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e != null){
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        if (objects.size() > 0){
                            for (final ParseObject object : objects){
                                ParseFile parseFile = (ParseFile) object.get("image");
                                parseFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null & data != null){
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                            post = new Post(object.getString("username"),object.getString("description"),object.getString("deadline"),
                                                    object.getString("title"),object.getString("location"),bitmap,object.getDouble("latitude"),object.getDouble("longitude"));
                                            posts.add(post);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });

            return null;
        }
    }

}
