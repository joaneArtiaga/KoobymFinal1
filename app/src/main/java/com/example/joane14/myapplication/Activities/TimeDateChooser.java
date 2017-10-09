package com.example.joane14.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.joane14.myapplication.Adapters.TimeDateAdapter;
import com.example.joane14.myapplication.Adapters.TimeDayAdapter;
import com.example.joane14.myapplication.Fragments.Constants;
import com.example.joane14.myapplication.Model.LocationModel;
import com.example.joane14.myapplication.Model.RentalDetail;
import com.example.joane14.myapplication.Model.RentalHeader;
import com.example.joane14.myapplication.Model.SwapDetail;
import com.example.joane14.myapplication.Model.SwapHeader;
import com.example.joane14.myapplication.Model.User;
import com.example.joane14.myapplication.Model.UserDayTime;
import com.example.joane14.myapplication.R;
import com.example.joane14.myapplication.Utilities.SPUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeDateChooser extends AppCompatActivity {

    RentalDetail rentalDetail;
    List<UserDayTime> userDayTimeList;
    UserDayTime userDayTimeModel;
    RentalHeader rentalHeader;
    SwapDetail swapDetail;
    SwapHeader swapHeader;
    LocationModel locationChosen;
    User user;
    String nextDateStr, fromWhere;
    Date nextDate;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_date_chooser);

        fromWhere = "";
        @SuppressLint({"NewApi", "LocalSuppress"}) final
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Log.d("CurrentDate", date);

        userDayTimeList = new ArrayList<UserDayTime>();
        rentalHeader = new RentalHeader();
        locationChosen = new LocationModel();
        user = new User();
        nextDateStr = "";

        user = (User) SPUtility.getSPUtil(TimeDateChooser.this).getObject("USER_OBJECT", User.class);



        if(getIntent().getSerializableExtra("locationChose")!=null){
            locationChosen = (LocationModel) getIntent().getSerializableExtra("locationChose");
            Log.d("LocationChosen", locationChosen.getLocationName());
            Log.d("ChosenLat" + locationChosen.getLatitude(), "ChosenLong"+locationChosen.getLongitude());
        }else{
            Log.d("LocationChosen", "is null");
        }

        if(getIntent().getBundleExtra("confirm").getBoolean("fromSwap")==true){
            fromWhere = "swap";
            swapDetail = new SwapDetail();
            swapHeader = new SwapHeader();
            if(getIntent().getExtras().getSerializable("swapDetail")!=null){
                userDayTimeModel = new UserDayTime();
                swapDetail = (SwapDetail) getIntent().getExtras().getSerializable("swapDetail");

                for(int init=0; init<swapDetail.getBookOwner().getUserObj().getDayTimeModel().size(); init++){
                    userDayTimeModel = swapDetail.getBookOwner().getUserObj().getDayTimeModel().get(init);
                    userDayTimeList.add(userDayTimeModel);
                }
            }
        }else{
            fromWhere = "rent";
            rentalDetail = new RentalDetail();
            if(getIntent().getExtras().getSerializable("rentalDetail")!=null){
                userDayTimeModel = new UserDayTime();
                rentalDetail = (RentalDetail) getIntent().getExtras().getSerializable("rentalDetail");
                Log.d("TimeDateChooser", rentalDetail.getBookOwner().getUserObj().getDayTimeModel().toString());


                for(int init=0; init<rentalDetail.getBookOwner().getUserObj().getDayTimeModel().size(); init++){
                    userDayTimeModel = (UserDayTime) rentalDetail.getBookOwner().getUserObj().getDayTimeModel().get(init);
                    userDayTimeList.add(userDayTimeModel);
                }

            }
        }

        ListView list = (ListView) findViewById(R.id.myList);

        TimeDateAdapter mAdapter = new TimeDateAdapter(this, userDayTimeList);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if(userDayTimeList.get(position).getDay().getStrDay().equals("Monday")){
                    nextDate=getNextDate(java.util.Calendar.MONDAY);
                }else if(userDayTimeList.get(position).getDay().getStrDay().equals("Tuesday")){
                    nextDate=getNextDate(java.util.Calendar.TUESDAY);
                }else if(userDayTimeList.get(position).getDay().getStrDay().equals("Wednesday")){
                    nextDate=getNextDate(java.util.Calendar.WEDNESDAY);
                }else if(userDayTimeList.get(position).getDay().getStrDay().equals("Thursday")){
                    nextDate=getNextDate(java.util.Calendar.THURSDAY);
                }else if(userDayTimeList.get(position).getDay().getStrDay().equals("Friday")){
                    nextDate=getNextDate(java.util.Calendar.FRIDAY);
                }else if(userDayTimeList.get(position).getDay().getStrDay().equals("Saturday")){
                    nextDate=getNextDate(java.util.Calendar.SATURDAY);
                }else if(userDayTimeList.get(position).getDay().getStrDay().equals("Sunday")){
                    nextDate=getNextDate(java.util.Calendar.SUNDAY);
                }


                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                nextDateStr = format.format(nextDate);

                Log.d("ItemClicked", String.valueOf(position));
                Log.d("userDateList", userDayTimeList.get(position).getDay().getStrDay()+", "+userDayTimeList.get(position).getTime().getStrTime());
                Log.d("DateOwner", String.valueOf(userDayTimeList.get(position).getUserId()));

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TimeDateChooser.this);
                alertDialogBuilder.setTitle("Are you sure you will be available at the time selected?");
                alertDialogBuilder.setMessage("Date:\t" + nextDateStr +
                        "\n\nDay:\t"+userDayTimeList.get(position).getDay().getStrDay()+
                        "\n\nTime:\t"+userDayTimeList.get(position).getTime().getStrTime());
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                showSummary(position, date);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });



            @SuppressLint({"NewApi", "LocalSuppress"})
            Calendar mCalendar = Calendar.getInstance();
            @SuppressLint({"NewApi", "LocalSuppress"})
            int i = mCalendar.get(Calendar.WEEK_OF_MONTH);
            mCalendar.set(Calendar.WEEK_OF_MONTH, ++i);
            int dayvalues=mCalendar.get(Calendar.DAY_OF_WEEK);

            Log.d("WeekOfMonth", String.valueOf(i));
            Log.d("dayValues", String.valueOf(dayvalues));


            String stringDay="";

            switch (dayvalues){
                case Calendar.MONDAY: stringDay = "Monday";
                    break;
                case Calendar.TUESDAY: stringDay = "Tuesday";
                    break;
                case Calendar.WEDNESDAY: stringDay = "Wednesday";
                    break;
                case Calendar.THURSDAY: stringDay = "Thursday";
                    break;
                case Calendar.FRIDAY: stringDay = "Friday";
                    break;
                case Calendar.SATURDAY: stringDay = "Saturday";
                    break;
                case Calendar.SUNDAY: stringDay = "Sunday";
                    break;
            }

//        if (mCalendar.get(Calendar.DAY_OF_WEEK) != dayvalues) {
//            mCalendar.add(Calendar.DAY_OF_MONTH, (dayvalues + 7 - mCalendar.get(Calendar.DAY_OF_WEEK)) % 7);
//        } else {
//            int minOfDay = mCalendar.get(Calendar.HOUR_OF_DAY) * 60 + mCalendar.get(Calendar.MINUTE);
//            if (minOfDay >= hour * 60 + minute)
//                cal.add(Calendar.DAY_OF_MONTH, 7); // Bump to next week
//        }

        TextView mTitle = (TextView) findViewById(R.id.tdMyDate);
        mTitle.setText("Today is "+stringDay+", "+date);

    }

    @SuppressLint("NewApi")
    public Date getNextDate(int dayOfWeek) {
        @SuppressLint({"NewApi", "LocalSuppress"})
        java.util.Calendar c = java.util.Calendar.getInstance();
        for ( int i = 0; i < 7; i++ ) {
            if ( c.get(java.util.Calendar.DAY_OF_WEEK) == dayOfWeek ) {
                return c.getTime();
            } else {
                c.add(java.util.Calendar.DAY_OF_WEEK, 1);
            }
        }
        return c.getTime();
    }



    public void showSummary(final int position, final String date){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TimeDateChooser.this);
        alertDialogBuilder.setTitle("Meet Up Summary");
        alertDialogBuilder.setMessage("Date:\t" +nextDateStr+
                "\n\nDay:\t"+userDayTimeList.get(position).getDay().getStrDay()+
                "\n\nTime:\t"+userDayTimeList.get(position).getTime().getStrTime()+
                "\n\nLocation:\t"+locationChosen.getLocationName());
        alertDialogBuilder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if(fromWhere.equals("rent")){

                            rentalHeader.setStatus("Confirmation");
                            rentalHeader.setRentalDetail(rentalDetail);
                            rentalHeader.setUserId(user);
                            rentalHeader.setRentalTimeStamp(nextDateStr);
                            rentalHeader.setTotalPrice((float) rentalDetail.getCalculatedPrice());
                            rentalHeader.setLocation(locationChosen);

//
                            Log.d("ONClickTime", "inside");
                            Log.d("RentalHeaderRent", rentalHeader.toString());


                            addRentalHeader();
                            Intent intent = new Intent(TimeDateChooser.this, RequestActivity.class);
                            startActivity(intent);
                        }else{
                            swapHeader.setStatus("Approved");
                            swapHeader.setSwapDetail(swapDetail);
                            swapHeader.setUser(user);
                            swapHeader.setDateTimeStamp(nextDateStr);
                            swapHeader.setLocation(locationChosen);
                            swapHeader.setUserDayTime(userDayTimeList.get(position));

                            addSwapHeader();
                            Log.d("SwapHeader dialog", "inside");
                        }

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void addSwapHeader(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String URL = "http://104.197.4.32:8080/Koobym/user/add";
        String URL = Constants.POST_SWAP_HEADER;

        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").registerTypeAdapter(Date.class, GsonDateDeserializer.getInstance()).create();
        final String mRequestBody = gson.toJson(swapHeader);



        Log.d("LOG_VOLLEY", mRequestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("onResponse addSwapH", "inside");
                Log.i("AddSwapHeader", response);
//                Intent intent = new Intent(TimeDateChooser.this, RequestActivity.class);
//                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_VOLLEY", error.toString());
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
        };

        requestQueue.add(stringRequest);
    }


    public void addRentalHeader(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String URL = "http://104.197.4.32:8080/Koobym/user/add";
        String URL = Constants.POST_RENTAL_HEADER;

        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").registerTypeAdapter(Date.class, GsonDateDeserializer.getInstance()).create();
        final String mRequestBody = gson.toJson(rentalHeader);

        Log.d("RentalHeaderAdd", rentalHeader.toString());


        Log.d("LOG_VOLLEY", mRequestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("onResponse addRentalH", "inside");
                Log.i("AddRentalHeader", response);
                Intent intent = new Intent(TimeDateChooser.this, RequestActivity.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_VOLLEY", error.toString());
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
        };

        requestQueue.add(stringRequest);
    }
}
