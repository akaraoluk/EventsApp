package com.example.eventsapp.PostClass;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventsapp.R;

import java.util.ArrayList;

public class Post implements Parcelable {

    private String username;
    private String eventDescription;
    private String eventDeadline;
    private String eventTitle;
    private String eventLocation;
    private Bitmap eventImage;
    private Double latitude;
    private Double longitude;

    public Post(String username,String eventDescription,String eventDeadline,String eventTitle,String eventLocation,Bitmap eventImage,Double latitude,Double longitude){
        this.username = username;
        this.eventDescription = eventDescription;
        this.eventDeadline = eventDeadline;
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.eventImage = eventImage;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDeadline() {
        return eventDeadline;
    }

    public void setEventDeadline(String eventDeadline) {
        this.eventDeadline = eventDeadline;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Bitmap getEventImage() {
        return eventImage;
    }

    public void setEventImage(Bitmap eventImage) {
        this.eventImage = eventImage;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
