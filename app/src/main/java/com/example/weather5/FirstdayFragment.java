package com.example.weather5;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.tianma8023.model.Time;
import com.github.tianma8023.ssv.SunriseSunsetView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import im.delight.android.location.SimpleLocation;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.getSystemServiceName;

public class FirstdayFragment extends Fragment
{
    TextView pressure_value,wind_direction,uvi_value,uvi_txt;
    SimpleLocation location;
    double longitude, latitude;
    int sunriseHour,sunriseMinute,sunsetHour,sunsetMinute;
    final Handler handler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_firstday, container, false);

        pressure_value = (TextView) view.findViewById(R.id.pressure_value);
        wind_direction = (TextView)view.findViewById(R.id.wind_direction);
        uvi_value = (TextView)view.findViewById(R.id.uvi_value);
        uvi_txt = (TextView) view.findViewById(R.id.uvi_txt);

        location = new SimpleLocation(getActivity());

        latitude = location.getLatitude();
        longitude = location.getLongitude();



        String api_Key = "1209f73f4fbcfd1f4ba12e26e5620759";
        double lat = latitude;
        double lon = longitude;
        final String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&appid=" + api_Key +"&units=imperial";
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject current = jsonObject.getJSONObject("current");
                            String sunrise  = current.getString("sunrise");
                            String sunset = current.getString("sunset");
                            String pressure = current.getString("pressure");
                            String wind_deg = current.getString("wind_deg");
                            int uvi = current.getInt("uvi");




                            //get sunrise and sunset time and set them to variables
                            long sunriseUnix = Long.parseLong(sunrise);
                            long sunsetUnix = Long.parseLong(sunset);

                            Date dateSunRise = new Date(sunriseUnix*1000);
                            Date dateSunSet = new Date(sunsetUnix*1000);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H");
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("m");
                            //set timezone to default
                            simpleDateFormat.setTimeZone(TimeZone.getDefault());
                            simpleDateFormat1.setTimeZone(TimeZone.getDefault());

                            String sunRiseH = simpleDateFormat.format(dateSunRise);
                            String sunRiseM = simpleDateFormat1.format(dateSunRise);
                            String sunSetH = simpleDateFormat.format(dateSunSet);
                            String sunSetM = simpleDateFormat1.format(dateSunSet);


                            sunriseHour = Integer.parseInt(sunRiseH);
                            sunriseMinute = Integer.parseInt(sunRiseM);

                            sunsetHour = Integer.parseInt(sunSetH);
                            sunsetMinute = Integer.parseInt(sunSetM);


                            int wind_dirction = Integer.parseInt(wind_deg);
                            pressure_value.setText(pressure+" hPa");
                            wind_direction.setText(finde_Wind_Direction(wind_dirction));

                            uvi_result(uvi);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

            }
        });
        queue.add(stringRequest);




        //set  houre and minute for sun animation

        SunriseSunsetView SunriseSunsetView = (SunriseSunsetView) view.findViewById(R.id.ssv);
        SunriseSunsetView.setSunriseTime(new com.github.tianma8023.model.Time(sunriseHour,sunriseMinute));
        SunriseSunsetView.setSunsetTime(new com.github.tianma8023.model.Time(sunsetHour,sunsetMinute));
        SunriseSunsetView.startAnimate();

        Runnable refresh = new Runnable()
        {
            @Override
            public void run()
            {
                SunriseSunsetView SunriseSunsetView = (SunriseSunsetView) view.findViewById(R.id.ssv);
                SunriseSunsetView.setSunriseTime(new com.github.tianma8023.model.Time(sunriseHour,sunriseMinute));
                SunriseSunsetView.setSunsetTime(new com.github.tianma8023.model.Time(sunsetHour,sunsetMinute));
                SunriseSunsetView.startAnimate();

                handler.postDelayed(FirstdayFragment.this::onResume,1000);

            }
        };
        handler.postDelayed(refresh,1000);




        return view;

    }




    protected String finde_Wind_Direction(int direction) {
        String windDirection;

        if (direction >= 350 && direction <= 360) {
            windDirection = "شمال";
            return windDirection;
        } else if(direction >=0 && direction<=10)
        {
            windDirection = "شمال";
            return windDirection;
        }

        else if (direction >= 20 && direction <= 70) {
            windDirection = "شمال شرقی";
            return windDirection;
        } else if (direction >= 80 && direction <= 100) {
            windDirection = "شرق";
            return windDirection;
        } else if (direction >= 110 && direction <= 160) {
            windDirection = "جنوب شرقی";
            return windDirection;
        } else if (direction >= 170 && direction <= 190) {
            windDirection = "جنوب";
            return windDirection;

        } else if (direction >= 200 && direction <= 250) {
            windDirection = "جنوب غربی";
            return windDirection;
        } else if (direction >= 260 && direction <= 280) {
            windDirection = "غرب";
            return windDirection;
        } else if (direction >= 290 && direction <= 340) {
            windDirection = "شمال غربی";
            return windDirection;
        }

        return "";

    }
    protected int uvi_result(int uvi)
    {
        if(uvi>=0 &&uvi<=2)
        {
            uvi_value.setTextColor(Color.parseColor("#02f20e"));
            uvi_value.setText(uvi+"");
        }
        else if(uvi>=3 && uvi<=5)
        {
            uvi_value.setTextColor(Color.parseColor("#fff308"));
            uvi_value.setText(uvi+"");
        }
        else if(uvi>=6 && uvi<=7)
        {
            uvi_value.setTextColor(Color.parseColor("#ffa600"));
            uvi_value.setText(uvi+"");
        }
        else if(uvi>=8 && uvi<=10)
        {
            uvi_value.setTextColor(Color.parseColor("#ff0015"));
            uvi_value.setText(uvi+"");
        }
        else if(uvi >=11)
        {
            uvi_value.setTextColor(Color.parseColor("#ab0a85"));
            uvi_value.setText(uvi+"");

        }

        return 0;




    }

    @Override
    public void onResume()
    {
        location.beginUpdates();
        super.onResume();
    }
}





