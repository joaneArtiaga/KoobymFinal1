package com.example.joane14.myapplication.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.joane14.myapplication.Model.LocationModel;
import com.example.joane14.myapplication.Model.RentalDetail;
import com.example.joane14.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewMeetUp extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    List<LocationModel> locationModelList;
    RentalDetail rentalDetailModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meet_up);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rentalDetailModel = new RentalDetail();

        if(getIntent().getExtras().getSerializable("rentalDetail")!=null){
            rentalDetailModel = (RentalDetail) getIntent().getExtras().getSerializable("rentalDetail");
            Log.d("MeetUpChooser", String.valueOf(rentalDetailModel.getBookOwner().getUserObj().getUserId()));
            locationModelList = rentalDetailModel.getBookOwner().getUserObj().getLocationArray();
            for(int init = 0; init<locationModelList.size(); init++){
                Log.d("MeetUpChooser Location", locationModelList.get(init).getLocationName());
            }
        }



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Marker mapMarker;


        LatLng location = null;
        LinearLayout tv = (LinearLayout) this.getLayoutInflater().inflate(R.layout.marker_dialog, null, false);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.setDrawingCacheEnabled(true);
        tv.buildDrawingCache();
        Bitmap bm = tv.getDrawingCache();

        for (int init=0; init<locationModelList.size(); init++){

            String lati, longi;
            Double latitude, longitude;

            lati = locationModelList.get(init).getLatitude();
            longi = locationModelList.get(init).getLongitude();
            latitude = Double.parseDouble(lati);
            longitude = Double.parseDouble(longi);
            Log.d("Latitude "+latitude.toString(), "Longitude "+longitude.toString());
            Log.d("LocationName "+locationModelList.get(init).getLocationName(), "ArrayPosition "+init);
            location = new LatLng(latitude,longitude);
            mapMarker = mMap.addMarker(new MarkerOptions().position(location).title(locationModelList.get(init).getLocationName()));

            mapMarker.showInfoWindow();
            mHashMap.put(mapMarker, init);
            Log.d("MarkerPosition", String.valueOf(mapMarker.getPosition()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }
}
