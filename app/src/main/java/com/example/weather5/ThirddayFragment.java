package com.example.weather5;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import im.delight.android.location.SimpleLocation;

public class ThirddayFragment extends Fragment
{
    MaterialTextView low_temp,high_temp,weather_description,humidity_value,pressure_value;
    MaterialTextView wind_value,uvi_value;
    ImageView weather_icons;
    double  longitude,latitude;
    SimpleLocation location;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_thirdday, container, false);

        low_temp = rootView.findViewById(R.id.low_temp);
        high_temp = rootView.findViewById(R.id.high_temp);
        weather_icons = rootView.findViewById(R.id.weather_icons);
        weather_description = rootView.findViewById(R.id.weather_description);
        humidity_value = rootView.findViewById(R.id.humidity_value);
        pressure_value = rootView.findViewById(R.id.pressure_value);
        wind_value = rootView.findViewById(R.id.wind_value);
        uvi_value = rootView.findViewById(R.id.uvi_value);

        location = new SimpleLocation(getActivity().getApplicationContext());

        latitude = location.getLatitude();
        longitude = location.getLongitude();



        third_day_weather();

        low_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String min_temp_txt = "کمترین دما";
                Toast.makeText(getActivity().getApplicationContext(),min_temp_txt,Toast.LENGTH_LONG).show();
            }
        });
        high_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String max_temp_txt = "بالاترین دما";
                Toast.makeText(getActivity().getApplicationContext(),max_temp_txt,Toast.LENGTH_LONG).show();

            }
        });



        return  rootView;
    }
    public static int fahrenheit_to_celsius(int f)
    {
        int celsius = (int) ((f - 32) / 1.8);


        return celsius;
    }


    protected void  third_day_weather()
    {
        String api_Key = "1209f73f4fbcfd1f4ba12e26e5620759";
        double lat = latitude;
        double lon = longitude;
        final String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&appid=" + api_Key + "&units=imperial&lang=fa";

        Context context;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray daily = jsonObject.getJSONArray("daily");
                    JSONObject two_object = daily.getJSONObject(2);
                    JSONArray weather = two_object.getJSONArray("weather");
                    JSONObject zero_object = weather.getJSONObject(0);
                    String main = zero_object.getString("main");
                    String description = zero_object.getString("description");
                    JSONObject temp = two_object.getJSONObject("temp");
                    String pressure = two_object.getString("pressure");
                    String humidity = two_object.getString("humidity");
                    String wind_speed = two_object.getString("wind_speed");
                    int uvi = two_object.getInt("uvi");
                    int min_temp = temp.getInt("min");
                    int max_temp = temp.getInt("max");
                    String clouds = two_object.getString("clouds");




                    int clouds_int = Integer.parseInt(clouds);




                    switch (main)
                    {

                        case "Thunderstorm":
                            Glide.with(getActivity().getApplicationContext())
                                    .load(R.drawable.ic_thunderstorms_day)
                                    .into(weather_icons);
                            break;
                        case "Clouds":
                            if(clouds_int>=11 && clouds_int<=25 )
                            {
                                Glide.with(getActivity().getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_partly_cloudy_day)
                                        .into(weather_icons);
                            }else if(clouds_int >=26)
                            {
                                Glide.with(getActivity().getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_cloudy)
                                        .into(weather_icons);
                            }
                            break;
                        case "Clear":
                            Glide.with(getActivity().getApplicationContext())
                                    .load(R.drawable.ic_weather_icons_clear_day)
                                    .into(weather_icons);
                            break;
                        case " Drizzle":
                            Glide.with(getActivity().getApplicationContext())
                                    .load(R.drawable.ic_weather_icons_partly_cloudy_day_drizzle)
                                    .into(weather_icons);
                            break;
                        case "Snow":
                            Glide.with(getActivity().getApplicationContext())
                                    .load(R.drawable.ic_weather_icons_snow)
                                    .into(weather_icons);
                            break;
                        case "Rain":
                            Glide.with(getActivity().getApplicationContext())
                                    .load(R.drawable.ic_weather_icons_rain)
                                    .into(weather_icons);
                            break;
                        case "Smoke":
                        case "Haze":
                        case "Fog":
                        case "Dust":
                        case "Sand":
                        case "Mist":
                        case "Ash":
                            Glide.with(getActivity().getApplicationContext())
                                    .load(R.drawable.ic_weather_icons_cloudy)
                                    .into(weather_icons);
                            break;
                        case "Tornado":
                        case "Squall":
                            Glide.with(getActivity().getApplicationContext())
                                    .load(R.drawable.ic_tornado)
                                    .into(weather_icons);
                            break;


                    }

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                    String tempUnit = sharedPreferences.getString("TEMP","1");
                    if("1".equals(tempUnit))
                    {
                        low_temp.setText(fahrenheit_to_celsius(min_temp)+"°");
                        high_temp.setText(fahrenheit_to_celsius(max_temp)+"°");

                    }else if("2".equals(tempUnit))
                    {
                        low_temp.setText(min_temp+"°");
                        high_temp.setText(max_temp+"°");

                    }
                    String windUnit = sharedPreferences.getString("WIND","1");
                    if("1".equals(windUnit))
                    {
                        double wind_speed_to_double = Double.parseDouble(wind_speed) * 1.6;
                        String wind_speed_km = String.format("%.1f", wind_speed_to_double );
                        wind_value.setText(wind_speed_km+" km/h");

                    }
                    else if("2".equals(windUnit))
                    {
                        double wind_speed_to_double = Double.parseDouble(wind_speed) * 1.6;
                        String wind_speed_mi =String.format("%.1f",wind_speed_to_double );
                        wind_value.setText(wind_speed_mi+" mi/h");
                    }





                    weather_description.setText(description);
                    humidity_value.setText(humidity+" %");
                    pressure_value.setText(pressure+" hPa");

                    uvi_result(uvi);





                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)

            {

            }
        });
        requestQueue.add(stringRequest);
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
        third_day_weather();
        super.onResume();
    }
}