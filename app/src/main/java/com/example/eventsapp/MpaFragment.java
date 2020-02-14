package com.example.eventsapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MpaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationListener locationListener;
    LocationManager locationManager;
    Button getLoc,cancelLoc;
    String address = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mpa, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLoc = view.findViewById(R.id.getLoc);
        cancelLoc = view.findViewById(R.id.cancelLoc);

        if (address != null){
            getLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        String info = "new";

        if (info.matches("new")){

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("abdurrahman",getActivity().MODE_PRIVATE);
                    boolean firstTimeCheck = sharedPreferences.getBoolean("notFirstTime",false);

                    if (!firstTimeCheck){ // false ise
                        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                        sharedPreferences.edit().putBoolean("notFirstTime",true).apply();
                    }


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if (Build.VERSION.SDK_INT >= 23){
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                    Location lastLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                    if (lastLocation != null){
                        LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                        mMap.addMarker(new MarkerOptions().title("It's Me!").position(lastUserLocation));
                    }
                }
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                if (lastLocation != null){
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                    mMap.addMarker(new MarkerOptions().title("It's Me!").position(lastUserLocation));
                }
            }
        }else{
            /*
            mMap.clear();
            int position = intent.getIntExtra("position",0);
            LatLng location = new LatLng(MainActivity.locations.get(position).latitude,MainActivity.locations.get(position).longitude);
            String placeName = MainActivity.names.get(position);

            mMap.addMarker(new MarkerOptions().title(placeName).position(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));

             */
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 ){
            if (requestCode == 1){
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                    String info = "new";

                    if (info.matches("new")){
                        Location lastLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                        if (lastLocation != null){
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                        }
                    }else{
                        /*
                        mMap.clear();
                        int position = intent.getIntExtra("position",0);
                        LatLng location = new LatLng(MainActivity.locations.get(position).latitude,MainActivity.locations.get(position).longitude);
                        String placeName = MainActivity.names.get(position);

                        mMap.addMarker(new MarkerOptions().title(placeName).position(location));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));

                         */
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if (addressList != null && addressList.size() > 0){
                if (addressList.get(0).getThoroughfare() != null){
                    address += addressList.get(0).getThoroughfare();

                    if (addressList.get(0).getSubThoroughfare() != null){
                        address += addressList.get(0).getSubThoroughfare();
                    }
                }
            }else{
                address = "New Place";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().title(address).position(latLng));



    }
}
