package com.example.weather5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.androidexception.andexalertdialog.Font;

public class SearchableActivity extends AppCompatActivity
{

    MaterialTextView main_temp, location_name, humudity_number;
    MaterialTextView wind_speed_txt, weather_description,minTempValue,maxTempValue;
    View min_temp_view, max_temp_view;
    ImageView weather_icons;
    LottieAnimationView setting;
    String api_Key = "1209f73f4fbcfd1f4ba12e26e5620759";
    double latitude;
    double longitude;
    public static final String JARGON = "com.example.searchinterface.jargon";
    String query;
    final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        handleIntent(getIntent());



        main_temp = (MaterialTextView) findViewById(R.id.main_temp);
        location_name = (MaterialTextView) findViewById(R.id.location_name);
        humudity_number = (MaterialTextView) findViewById(R.id.humidity_number);
        wind_speed_txt = (MaterialTextView) findViewById(R.id.wind_speed_txt);
        weather_description = (MaterialTextView) findViewById(R.id.weather_description);
        weather_icons = (ImageView) findViewById(R.id.weather_icons);
        setting = findViewById(R.id.setting);
        max_temp_view = findViewById(R.id.max_temp_view);
        min_temp_view = findViewById(R.id.min_temp_view);
        minTempValue = findViewById(R.id.minTempValue);
        maxTempValue = findViewById(R.id.maxTempValue);





        setting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent setting_page = new Intent(SearchableActivity.this,SettingsActivity.class);
                startActivity(setting_page);


            }
        });
        max_temp_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String max_temp_txt = "بالاترین دمای فعلی";
                Toast.makeText(SearchableActivity.this,max_temp_txt,Toast.LENGTH_LONG).show();
            }
        });

        min_temp_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String min_temp_txt = "کمترین دمای فعلی";
                Toast.makeText(SearchableActivity.this,min_temp_txt,Toast.LENGTH_LONG).show();

            }
        });
        wind_speed_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wind_toast = "سرعت باد فعلی";
                Toast.makeText(SearchableActivity.this,wind_toast,Toast.LENGTH_LONG).show();

            }
        });
        humudity_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String humidity_toast = "رطوبت هوای فعلی";
                Toast.makeText(SearchableActivity.this,humidity_toast,Toast.LENGTH_LONG).show();

            }
        });
        main_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String main_temp_toast = "دمای هوای فعلی";
                Toast.makeText(SearchableActivity.this,main_temp_toast,Toast.LENGTH_LONG).show();


            }
        });



        search_weather(query);
        currunt_weather();
        get_location_name();
        tomorrow();
        third_day();
        fourth_day();
        fifth_day();
        sixth_day();
        seventh_day();

        Runnable refresh = new Runnable() {
            @Override
            public void run()
            {
                get_location_name();
                currunt_weather();
                tomorrow();
                third_day();
                fourth_day();
                fifth_day();
                sixth_day();
                seventh_day();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(refresh, 1000);





    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);




            Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
            if (appData != null) {
                boolean jargon = appData.getBoolean(SearchableActivity.JARGON);
            }
        }
    }

    public  int fahrenheit_to_celsius(int f)
    {
        int celsius = (int) ((f - 32) / 1.8);

        return celsius;
    }



    protected void search_weather(String cityName)
    {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q="+cityName+"&appid="+api_Key;
        RequestQueue requestQueue = Volley.newRequestQueue(SearchableActivity.this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    JSONObject city = response.getJSONObject("city");
                    JSONObject coord = city.getJSONObject("coord");

                    latitude = coord.getDouble("lat");
                    longitude = coord.getDouble("lon");




                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                if( error instanceof ServerError)
                {
                    new AndExAlertDialog.Builder(SearchableActivity.this)
                        .setTitle("خطا")
                        .setMessage("نام شهر صحیح نیست")
                        .setPositiveBtnText("بازگشت")
                        .setCancelableOnTouchOutside(true)
                        .OnPositiveClicked(new AndExAlertDialogListener()
                        {
                            @Override
                            public void OnClick(String input)
                            {
                                Intent backToWeather = new Intent(SearchableActivity.this,Weather.class);
                                startActivity(backToWeather);
                            }
                        })
                        .setFont(Font.IRAN_SANS)
                        .setTitleTextColor(Color.parseColor("#ff0000"))
                        .build();
                }
                if(error instanceof TimeoutError )
                { new AndExAlertDialog.Builder(SearchableActivity.this)
                        .setTitle("خطا")
                        .setMessage("پایین بودن سرعت اینترنت")
                        .setCancelableOnTouchOutside(true)
                        .setPositiveBtnText("اجرای مجدد")
                        .OnPositiveClicked(new AndExAlertDialogListener() {
                            @Override
                            public void OnClick(String input) {
                                SearchableActivity.this.recreate();
                            }
                        })
                        .setFont(Font.IRAN_SANS)
                        .setTitleTextColor(Color.parseColor("#ff0000"))
                        .build();
                }
                else if(error instanceof NoConnectionError || error instanceof NetworkError)
                {
                    new AndExAlertDialog.Builder(SearchableActivity.this)
                            .setTitle("خطا")
                            .setMessage("دستگاه به اینترنت متصل نیست")
                            .setCancelableOnTouchOutside(true)
                            .setFont(Font.IRAN_SANS)
                            .setPositiveBtnText("خروج")
                            .OnPositiveClicked(new AndExAlertDialogListener() {
                                @Override
                                public void OnClick(String input)
                                {
                                    finish();
                                    System.exit(0);
                                }
                            })
                            .setTitleTextColor(Color.parseColor("#ff0000"))
                            .build();

                }


            }
        });


        requestQueue.add(objectRequest);


    }


    protected boolean is_day()
    {
        boolean isDay = true;

        Calendar timeRightNow = Calendar.getInstance();
        int night = timeRightNow.get(Calendar.HOUR_OF_DAY);

        if(night >=18)
        {
            isDay = false;
        }
        else if(night>=0 && night<6)
        {
            isDay = false;
        }
        else if(night>=6)
        {
            isDay = true;

        }


        return isDay;

    }

    protected void currunt_weather()
    {

        String url = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&appid="+api_Key+"&units=imperial&lang=fa";

        RequestQueue requestQueue = Volley.newRequestQueue(SearchableActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    //get JSON current objects
                    JSONObject current = response.getJSONObject("current");
                    int currunt_temp = current.getInt("temp");
                    String humidity = current.getString("humidity");
                    double wind_speed = current.getDouble("wind_speed");
                    JSONArray weather = current.getJSONArray("weather");
                    JSONObject zero_object = weather.getJSONObject(0);
                    String main = zero_object.getString("main");
                    String description = zero_object.getString("description");
                    Long sun_rise = current.getLong("sunrise");
                    Long sun_set = current.getLong("sunset");
                    int clouds = current.getInt("clouds");


                    //get json daily objects
                    JSONArray daily = response.getJSONArray("daily");
                    JSONObject current_day = daily.getJSONObject(0);
                    JSONObject current_day_temp = current_day.getJSONObject("temp");
                    int minTemp = current_day_temp.getInt("min");
                    int maxTemp = current_day_temp.getInt("max");





                    if ( is_day())
                    {
                        switch (main)
                        {

                            case "Thunderstorm":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_thunderstorms_day)
                                        .into(weather_icons);
                                break;
                            case "Clouds":
                                if(clouds>=11 && clouds<=25 )
                                {
                                    Glide.with(getApplicationContext())
                                            .load(R.drawable.ic_weather_icons_partly_cloudy_day)
                                            .into(weather_icons);
                                }else if(clouds >=26)
                                {
                                    Glide.with(getApplicationContext())
                                            .load(R.drawable.ic_weather_icons_cloudy)
                                            .into(weather_icons);
                                }
                                break;
                            case "Clear":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_clear_day)
                                        .into(weather_icons);
                                break;
                            case " Drizzle":
                                Glide.with(SearchableActivity.this)
                                        .load(R.drawable.ic_weather_icons_partly_cloudy_day_drizzle)
                                        .into(weather_icons);
                                break;
                            case "Snow":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_snow)
                                        .into(weather_icons);
                                break;
                            case "Rain":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_rain)
                                        .into(weather_icons);
                                break;
                            case "Smoke":
                            case "Haze":
                            case "Fog":
                            case "Dust":
                            case "Mist":
                            case "Sand":
                            case "Ash":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_cloudy)
                                        .into(weather_icons);
                                break;
                            case "Tornado":
                            case "Squall":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_tornado)
                                        .into(weather_icons);
                                break;


                        }



                    }else if(!is_day())
                    {
                        switch (main)
                        {

                            case "Thunderstorm":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_thunderstorms_night)
                                        .into(weather_icons);
                                break;
                            case "Clouds":
                                if(clouds>=11 && clouds<=25)
                                {
                                    Glide.with(getApplicationContext())
                                            .load(R.drawable.ic_weather_icons_partly_cloudy_night)
                                            .into(weather_icons);
                                }else if(clouds >=26)
                                {
                                    Glide.with(getApplicationContext())
                                            .load(R.drawable.ic_weather_icons_cloudy)
                                            .into(weather_icons);
                                }
                                break;
                            case "Clear":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_clear_night)
                                        .into(weather_icons);
                                break;
                            case " Drizzle":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_partly_cloudy_night_drizzle)
                                        .into(weather_icons);
                                break;
                            case "Snow":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_snow)
                                        .into(weather_icons);
                                break;
                            case "Rain":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_rain)
                                        .into(weather_icons);
                                break;
                            case "Smoke":
                            case "Haze":
                            case "Fog":
                            case "Dust":
                            case "Mist":
                            case "Sand":
                            case "Ash":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_weather_icons_cloudy)
                                        .into(weather_icons);
                                break;
                            case "Tornado":
                            case "Squall":
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.ic_tornado)
                                        .into(weather_icons);
                                break;


                        }

                    }

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SearchableActivity.this);

                    String tempUnit = preferences.getString("TEMP","1");
                    if("1".equals(tempUnit))
                    {
                        main_temp.setText(fahrenheit_to_celsius(currunt_temp) + "°C");
                        minTempValue.setText(fahrenheit_to_celsius(minTemp)+"°");
                        maxTempValue.setText(fahrenheit_to_celsius(maxTemp)+"°");

                    }
                    else if("2".equals(tempUnit))
                    {
                        main_temp.setText(currunt_temp + "°F");
                        minTempValue.setText(minTemp+"°");
                        maxTempValue.setText(maxTemp+"°");
                    }

                    String windUnit = preferences.getString("WIND","1");
                    if("1".equals(windUnit))
                    {

                        wind_speed *= 1.6;
                        String wind_speed_km = String.format("%.1f",wind_speed);

                        wind_speed_txt.setText(wind_speed_km + "\nKm/h");

                    }
                    else if("2".equals(windUnit))
                    {
                        String wind_speed_mi = String.format("%.1f",wind_speed);
                        wind_speed_txt.setText(wind_speed_mi + "\nMi/h");
                    }


                    humudity_number.setText("  " + humidity + " %");
                    weather_description.setText("   " + description);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        requestQueue.add(request);
    }
    protected void get_location_name()
    {

        String url = "https://api.bigdatacloud.net/data/reverse-geocode-client?latitude="+latitude+"&longitude="+longitude+"&localityLanguage=fa";

        RequestQueue requestQueue = Volley.newRequestQueue(SearchableActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String city = response.getString("city");
                    location_name.setText(city);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);


    }


     protected void tomorrow()
     {
         TextView seconde_day_discription,seconde_day_max_temp,seconde_day_min_temp,seconde_day_name;
         ImageView seconde_day_image;


         seconde_day_discription = findViewById(R.id.seconde_day_discription);
         seconde_day_max_temp = findViewById(R.id.seconde_day_max_temp);
         seconde_day_min_temp = findViewById(R.id.seconde_day_min_temp);
         seconde_day_name = findViewById(R.id.seconde_day_name);
         seconde_day_image = findViewById(R.id.seonde_day_image);

         String url = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&appid="+api_Key+"&units=imperial&lang=fa";


         RequestQueue requestQueue = Volley.newRequestQueue(SearchableActivity.this);

         JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response)
             {
                 try
                 {
                     JSONArray daily = response.getJSONArray("daily");
                     JSONObject one_object = daily.getJSONObject(1);
                     JSONArray weather = one_object.getJSONArray("weather");
                     JSONObject zero_object = weather.getJSONObject(0);
                     String main = zero_object.getString("main");
                     String description = zero_object.getString("description");
                     JSONObject temp = one_object.getJSONObject("temp");
                     int min_temp = temp.getInt("min");
                     int max_temp = temp.getInt("max");
                     String clouds = one_object.getString("clouds");

                     int clouds_int = Integer.parseInt(clouds);



                     switch (main)
                     {

                         case "Thunderstorm":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_thunderstorms_day)
                                     .into(seconde_day_image);
                             break;
                         case "Clouds":
                             if(clouds_int>=11 && clouds_int<=25 )
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_partly_cloudy_day)
                                         .into(seconde_day_image);
                             }else if(clouds_int >=26)
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_cloudy)
                                         .into(seconde_day_image);
                             }
                             break;
                         case "Clear":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_clear_day)
                                     .into(seconde_day_image);
                             break;
                         case " Drizzle":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_partly_cloudy_day_drizzle)
                                     .into(seconde_day_image);
                             break;
                         case "Snow":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_snow)
                                     .into(seconde_day_image);
                             break;
                         case "Rain":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_rain)
                                     .into(seconde_day_image);
                             break;
                         case "Smoke":
                         case "Haze":
                         case "Fog":
                         case "Dust":
                         case "Sand":
                         case "Mist":
                         case "Ash":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_cloudy)
                                     .into(seconde_day_image);
                             break;
                         case "Tornado":
                         case "Squall":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_tornado)
                                     .into(seconde_day_image);
                             break;


                     }


                     SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SearchableActivity.this);

                     String tempUnit = sharedPreferences.getString("TEMP","1");
                     if("1".equals(tempUnit))
                     {
                         seconde_day_min_temp.setText(""+fahrenheit_to_celsius(min_temp)+"°C");
                         seconde_day_max_temp.setText(fahrenheit_to_celsius(max_temp)+"°");

                     }else if("2".equals(tempUnit))
                     {
                         seconde_day_min_temp.setText(min_temp+"°F");
                         seconde_day_max_temp.setText(max_temp+"°");

                     }






                     seconde_day_discription.setText(description);


                     seconde_day_name.setText("فردا");






                 }
                 catch (JSONException e)
                 {
                     e.printStackTrace();
                 }

             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error)
             {

             }
         });
         requestQueue.add(request);



     }
     protected void third_day()
     {

         TextView third_day_discription,third_day_max_temp,third_day_min_temp,third_day_name;
         ImageView third_day_image;


         third_day_discription = findViewById(R.id.third_day_discription);
         third_day_max_temp = findViewById(R.id.third_day_max_temp);
         third_day_min_temp = findViewById(R.id.third_day_min_temp);
         third_day_name = findViewById(R.id.third_day_name);
         third_day_image = findViewById(R.id.third_day_image);


         String url = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&appid="+api_Key+"&units=imperial&lang=fa";


         RequestQueue requestQueue = Volley.newRequestQueue(SearchableActivity.this);

         JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response)
             {
                 try
                 {
                     JSONArray daily = response.getJSONArray("daily");
                     JSONObject third_day = daily.getJSONObject(2);
                     JSONArray weather = third_day.getJSONArray("weather");
                     JSONObject zero_object = weather.getJSONObject(0);
                     String main = zero_object.getString("main");
                     long third_day_date = third_day.getLong("dt");
                     String description = zero_object.getString("description");
                     JSONObject temp = third_day.getJSONObject("temp");
                     int min_temp = temp.getInt("min");
                     int max_temp = temp.getInt("max");
                     String clouds = third_day.getString("clouds");


                     //convert wind speed unit to km/h
                     int clouds_int = Integer.parseInt(clouds);



                     switch (main)
                     {

                         case "Thunderstorm":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_thunderstorms_day)
                                     .into(third_day_image);
                             break;
                         case "Clouds":
                             if(clouds_int>=11 && clouds_int<=25 )
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_partly_cloudy_day)
                                         .into(third_day_image);
                             }else if(clouds_int >=26)
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_cloudy)
                                         .into(third_day_image);
                             }
                             break;
                         case "Clear":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_clear_day)
                                     .into(third_day_image);
                             break;
                         case " Drizzle":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_partly_cloudy_day_drizzle)
                                     .into(third_day_image);
                             break;
                         case "Snow":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_snow)
                                     .into(third_day_image);
                             break;
                         case "Rain":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_rain)
                                     .into(third_day_image);
                             break;
                         case "Smoke":
                         case "Haze":
                         case "Fog":
                         case "Dust":
                         case "Sand":
                         case "Mist":
                         case "Ash":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_cloudy)
                                     .into(third_day_image);
                             break;
                         case "Tornado":
                         case "Squall":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_tornado)
                                     .into(third_day_image);
                             break;


                     }

                     SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SearchableActivity.this);

                     String tempUnit = sharedPreferences.getString("TEMP","1");
                     if("1".equals(tempUnit))
                     {
                         third_day_min_temp.setText(""+fahrenheit_to_celsius(min_temp)+"°C");
                         third_day_max_temp.setText(fahrenheit_to_celsius(max_temp)+"°");

                     }else if("2".equals(tempUnit))
                     {
                         third_day_min_temp.setText(min_temp+"°F");
                         third_day_max_temp.setText(max_temp+"°");

                     }






                     third_day_discription.setText(description);
                     Format dateFormat = new SimpleDateFormat("EEEE");
                     String third_day_format =  dateFormat.format(third_day_date*1000 );
                     third_day_name.setText(changeDaynametoPersion(third_day_format));






                 }
                 catch (JSONException e)
                 {
                     e.printStackTrace();
                 }

             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error)
             {

             }
         });
         requestQueue.add(request);



     }

     protected void fourth_day()
     {

         TextView fourth_day_discription,fourth_day_max_temp,fourth_day_min_temp,fourth_day_name;
         ImageView fourth_day_image;


         fourth_day_discription = findViewById(R.id.fourth_day_discription);
         fourth_day_max_temp = findViewById(R.id.fourth_day_max_temp);
         fourth_day_min_temp = findViewById(R.id.fourth_day_min_temp);
         fourth_day_name = findViewById(R.id.fourth_day_name);
         fourth_day_image = findViewById(R.id.fourth_day_image);


         String url = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&appid="+api_Key+"&units=imperial&lang=fa";


         RequestQueue requestQueue = Volley.newRequestQueue(SearchableActivity.this);

         JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response)
             {
                 try
                 {
                     JSONArray daily = response.getJSONArray("daily");
                     JSONObject fourth_day = daily.getJSONObject(3);
                     JSONArray weather = fourth_day.getJSONArray("weather");
                     JSONObject zero_object = weather.getJSONObject(0);
                     String main = zero_object.getString("main");
                     long  fourth_day_date = fourth_day.getLong("dt");
                     String description = zero_object.getString("description");
                     JSONObject temp = fourth_day.getJSONObject("temp");
                     int min_temp = temp.getInt("min");
                     int max_temp = temp.getInt("max");
                     String clouds = fourth_day.getString("clouds");


                     //convert wind speed unit to km/h
                     int clouds_int = Integer.parseInt(clouds);



                     switch (main)
                     {

                         case "Thunderstorm":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_thunderstorms_day)
                                     .into(fourth_day_image);
                             break;
                         case "Clouds":
                             if(clouds_int>=11 && clouds_int<=25 )
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_partly_cloudy_day)
                                         .into(fourth_day_image);
                             }else if(clouds_int >=26)
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_cloudy)
                                         .into(fourth_day_image);
                             }
                             break;
                         case "Clear":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_clear_day)
                                     .into(fourth_day_image);
                             break;
                         case " Drizzle":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_partly_cloudy_day_drizzle)
                                     .into(fourth_day_image);
                             break;
                         case "Snow":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_snow)
                                     .into(fourth_day_image);
                             break;
                         case "Rain":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_rain)
                                     .into(fourth_day_image);
                             break;
                         case "Smoke":
                         case "Haze":
                         case "Fog":
                         case "Dust":
                         case "Mist":
                         case "Sand":
                         case "Ash":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_cloudy)
                                     .into(fourth_day_image);
                             break;
                         case "Tornado":
                         case "Squall":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_tornado)
                                     .into(fourth_day_image);
                             break;


                     }

                     SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SearchableActivity.this);

                     String tempUnit = sharedPreferences.getString("TEMP","1");
                     if("1".equals(tempUnit))
                     {
                         fourth_day_min_temp.setText(""+fahrenheit_to_celsius(min_temp)+"°C");
                         fourth_day_max_temp.setText(fahrenheit_to_celsius(max_temp)+"°");

                     }else if("2".equals(tempUnit))
                     {
                         fourth_day_min_temp.setText(min_temp+"°F");
                         fourth_day_max_temp.setText(max_temp+"°");

                     }






                     fourth_day_discription.setText(description);
                     Format dateFormat = new SimpleDateFormat("EEEE");
                     String fourth_day_format =  dateFormat.format( fourth_day_date*1000 );
                     fourth_day_name.setText(changeDaynametoPersion(fourth_day_format));






                 }
                 catch (JSONException e)
                 {
                     e.printStackTrace();
                 }

             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error)
             {

             }
         });
         requestQueue.add(request);


     }
     protected void fifth_day()
     {
         TextView fifth_day_discription, fifth_day_max_temp, fifth_day_min_temp, fifth_day_name;
         ImageView  fifth_day_image;


         fifth_day_discription = findViewById(R.id. fifth_day_discription);
         fifth_day_max_temp = findViewById(R.id. fifth_day_max_temp);
         fifth_day_min_temp = findViewById(R.id. fifth_day_min_temp);
         fifth_day_name = findViewById(R.id. fifth_day_name);
         fifth_day_image = findViewById(R.id. fifth_day_image);



         String url = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&appid="+api_Key+"&units=imperial&lang=fa";


         RequestQueue requestQueue = Volley.newRequestQueue(SearchableActivity.this);

         JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response)
             {
                 try
                 {
                     JSONArray daily = response.getJSONArray("daily");
                     JSONObject  fifth_day = daily.getJSONObject(4);
                     JSONArray weather = fifth_day.getJSONArray("weather");
                     JSONObject zero_object = weather.getJSONObject(0);
                     String main = zero_object.getString("main");
                     long fifth_day_date = fifth_day.getLong("dt");
                     String description = zero_object.getString("description");
                     JSONObject temp = fifth_day.getJSONObject("temp");
                     int min_temp = temp.getInt("min");
                     int max_temp = temp.getInt("max");
                     String clouds = fifth_day.getString("clouds");


                     //convert wind speed unit to km/h
                     int clouds_int = Integer.parseInt(clouds);



                     switch (main)
                     {

                         case "Thunderstorm":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_thunderstorms_day)
                                     .into(fifth_day_image);
                             break;
                         case "Clouds":
                             if(clouds_int>=11 && clouds_int<=25 )
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_partly_cloudy_day)
                                         .into(fifth_day_image);
                             }else if(clouds_int >=26)
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_cloudy)
                                         .into(fifth_day_image);
                             }
                             break;
                         case "Clear":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_clear_day)
                                     .into(fifth_day_image);
                             break;
                         case " Drizzle":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_partly_cloudy_day_drizzle)
                                     .into(fifth_day_image);
                             break;
                         case "Snow":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_snow)
                                     .into(fifth_day_image);
                             break;
                         case "Rain":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_rain)
                                     .into(fifth_day_image);
                             break;
                         case "Smoke":
                         case "Haze":
                         case "Fog":
                         case "Dust":
                         case "Sand":
                         case "Ash":
                         case "Mist":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_cloudy)
                                     .into(fifth_day_image);
                             break;
                         case "Tornado":
                         case "Squall":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_tornado)
                                     .into(fifth_day_image);
                             break;


                     }


                     SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SearchableActivity.this);

                     String tempUnit = sharedPreferences.getString("TEMP","1");
                     if("1".equals(tempUnit))
                     {
                         fifth_day_min_temp.setText(""+fahrenheit_to_celsius(min_temp)+"°C");
                         fifth_day_max_temp.setText(fahrenheit_to_celsius(max_temp)+"°");

                     }else if("2".equals(tempUnit))
                     {
                         fifth_day_min_temp.setText(min_temp+"°F");
                         fifth_day_max_temp.setText(max_temp+"°");

                     }






                     fifth_day_discription.setText(description);
                     Format dateFormat = new SimpleDateFormat("EEEE");
                     String fifth_day_format =  dateFormat.format(fifth_day_date*1000 );
                     fifth_day_name.setText(changeDaynametoPersion(fifth_day_format));






                 }
                 catch (JSONException e)
                 {
                     e.printStackTrace();
                 }

             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error)
             {

             }
         });
         requestQueue.add(request);





     }
     protected void sixth_day()
     {

         TextView sixth_day_discription,sixth_day_max_temp, sixth_day_min_temp, sixth_day_name;
         ImageView  sixth_day_image;


         sixth_day_discription = findViewById(R.id. sixth_day_discription);
         sixth_day_max_temp = findViewById(R.id. sixth_day_max_temp);
         sixth_day_min_temp = findViewById(R.id. sixth_day_min_temp);
         sixth_day_name = findViewById(R.id. sixth_day_name);
         sixth_day_image = findViewById(R.id.sixth_day_image);



         String url = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&appid="+api_Key+"&units=imperial&lang=fa";


         RequestQueue requestQueue = Volley.newRequestQueue(SearchableActivity.this);

         JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response)
             {
                 try
                 {
                     JSONArray daily = response.getJSONArray("daily");
                     JSONObject  sixth_day = daily.getJSONObject(5);
                     JSONArray weather = sixth_day.getJSONArray("weather");
                     JSONObject zero_object = weather.getJSONObject(0);
                     String main = zero_object.getString("main");
                     long sixth_day_date = sixth_day.getLong("dt");
                     String description = zero_object.getString("description");
                     JSONObject temp = sixth_day.getJSONObject("temp");
                     int min_temp = temp.getInt("min");
                     int max_temp = temp.getInt("max");
                     String clouds = sixth_day.getString("clouds");


                     //convert wind speed unit to km/h
                     int clouds_int = Integer.parseInt(clouds);



                     switch (main)
                     {

                         case "Thunderstorm":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_thunderstorms_day)
                                     .into(sixth_day_image);
                             break;
                         case "Clouds":
                             if(clouds_int>=11 && clouds_int<=25 )
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_partly_cloudy_day)
                                         .into(sixth_day_image);
                             }else if(clouds_int >=26)
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_cloudy)
                                         .into(sixth_day_image);
                             }
                             break;
                         case "Clear":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_clear_day)
                                     .into(sixth_day_image);
                             break;
                         case " Drizzle":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_partly_cloudy_day_drizzle)
                                     .into(sixth_day_image);
                             break;
                         case "Snow":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_snow)
                                     .into(sixth_day_image);
                             break;
                         case "Rain":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_rain)
                                     .into(sixth_day_image);
                             break;
                         case "Smoke":
                         case "Haze":
                         case "Fog":
                         case "Dust":
                         case "Sand":
                         case "Mist":
                         case "Ash":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_cloudy)
                                     .into(sixth_day_image);
                             break;
                         case "Tornado":
                         case "Squall":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_tornado)
                                     .into(sixth_day_image);
                             break;


                     }


                     SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SearchableActivity.this);

                     String tempUnit = sharedPreferences.getString("TEMP","1");
                     if("1".equals(tempUnit))
                     {
                         sixth_day_min_temp.setText(""+fahrenheit_to_celsius(min_temp)+"°C");
                         sixth_day_max_temp.setText(fahrenheit_to_celsius(max_temp)+"°");

                     }else if("2".equals(tempUnit))
                     {
                         sixth_day_min_temp.setText(min_temp+"°F");
                         sixth_day_max_temp.setText(max_temp+"°");

                     }






                     sixth_day_discription.setText(description);
                     Format dateFormat = new SimpleDateFormat("EEEE");
                     String sixth_day_format =  dateFormat.format(sixth_day_date*1000 );
                     sixth_day_name.setText(changeDaynametoPersion(sixth_day_format));






                 }
                 catch (JSONException e)
                 {
                     e.printStackTrace();
                 }

             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error)
             {

             }
         });
         requestQueue.add(request);


     }
     protected void seventh_day()
     {
         TextView seventh_day_discription,seventh_day_max_temp, seventh_day_min_temp, seventh_day_name;
         ImageView seventh_day_image;


         seventh_day_discription = findViewById(R.id. seventh_day_discription);
         seventh_day_max_temp = findViewById(R.id. seventh_day_max_temp);
         seventh_day_min_temp = findViewById(R.id. seventh_day_min_temp);
         seventh_day_name = findViewById(R.id. seventh_day_name);
         seventh_day_image = findViewById(R.id.seventh_day_image);



         String url = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&appid="+api_Key+"&units=imperial&lang=fa";


         RequestQueue requestQueue = Volley.newRequestQueue(SearchableActivity.this);

         JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response)
             {
                 try
                 {
                     JSONArray daily = response.getJSONArray("daily");
                     JSONObject seventh_day = daily.getJSONObject(6);
                     JSONArray weather = seventh_day.getJSONArray("weather");
                     JSONObject zero_object = weather.getJSONObject(0);
                     String main = zero_object.getString("main");
                     long seventh_day_date = seventh_day.getLong("dt");
                     String description = zero_object.getString("description");
                     JSONObject temp = seventh_day.getJSONObject("temp");
                     int min_temp = temp.getInt("min");
                     int max_temp = temp.getInt("max");
                     String clouds = seventh_day.getString("clouds");


                     //convert wind speed unit to km/h
                     int clouds_int = Integer.parseInt(clouds);



                     switch (main)
                     {

                         case "Thunderstorm":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_thunderstorms_day)
                                     .into(seventh_day_image);
                             break;
                         case "Clouds":
                             if(clouds_int>=11 && clouds_int<=25 )
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_partly_cloudy_day)
                                         .into(seventh_day_image);
                             }else if(clouds_int >=26)
                             {
                                 Glide.with(getApplicationContext())
                                         .load(R.drawable.ic_weather_icons_cloudy)
                                         .into(seventh_day_image);
                             }
                             break;
                         case "Clear":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_clear_day)
                                     .into(seventh_day_image);
                             break;
                         case " Drizzle":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_partly_cloudy_day_drizzle)
                                     .into(seventh_day_image);
                             break;
                         case "Snow":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_snow)
                                     .into(seventh_day_image);
                             break;
                         case "Rain":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_rain)
                                     .into(seventh_day_image);
                             break;
                         case "Smoke":
                         case "Haze":
                         case "Fog":
                         case "Dust":
                         case "Sand":
                         case "Mist":
                         case "Ash":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_weather_icons_cloudy)
                                     .into(seventh_day_image);
                             break;
                         case "Tornado":
                         case "Squall":
                             Glide.with(getApplicationContext())
                                     .load(R.drawable.ic_tornado)
                                     .into(seventh_day_image);
                             break;


                     }

                     SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SearchableActivity.this);

                     String tempUnit = sharedPreferences.getString("TEMP","1");
                     if("1".equals(tempUnit))
                     {
                         seventh_day_min_temp.setText(""+fahrenheit_to_celsius(min_temp)+"°C");
                         seventh_day_max_temp.setText(fahrenheit_to_celsius(max_temp)+"°");

                     }else if("2".equals(tempUnit))
                     {
                         seventh_day_min_temp.setText(min_temp+"°F");
                         seventh_day_max_temp.setText(max_temp+"°");

                     }






                     seventh_day_discription.setText(description);
                     Format dateFormat = new SimpleDateFormat("EEEE");
                     String seventh_day_format =  dateFormat.format(seventh_day_date*1000 );
                     seventh_day_name.setText(changeDaynametoPersion(seventh_day_format));






                 }
                 catch (JSONException e)
                 {
                     e.printStackTrace();
                 }

             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error)
             {

             }
         });
         requestQueue.add(request);



     }
    protected String changeDaynametoPersion(String day_name)
    {
        String persian_day = day_name;

        switch (day_name)
        {
            case "Monday":
                persian_day = "دوشنبه";
                break;
            case "Tuesday":
                persian_day = "سه شنبه";
                break;
            case "Wednesday":
                persian_day = "چهارشنبه";
                break;
            case "Thursday":
                persian_day = "پنج شنبه";
                break;
            case "Friday":
                persian_day = "جمعه";
                break;
            case "Saturday":
                persian_day = "شنبه";
                break;
            case "Sunday":
                persian_day = "یکشنبه";
                break;

        }

        return persian_day;


    }


    @Override
    protected void onResume()
    {
        currunt_weather();
        get_location_name();
        tomorrow();
        third_day();
        fourth_day();
        fifth_day();
        sixth_day();
        seventh_day();
        super.onResume();
    }

}