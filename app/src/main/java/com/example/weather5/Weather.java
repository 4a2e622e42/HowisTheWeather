package com.example.weather5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.brouding.simpledialog.SimpleDialog;
import com.brouding.simpledialog.builder.General;
import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

import im.delight.android.location.SimpleLocation;
import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.androidexception.andexalertdialog.Font;

public class Weather extends AppCompatActivity
{
    MaterialTextView main_temp, location_name, humudity_number;
    MaterialTextView wind_speed_txt, weather_description,minTempValue,maxTempValue;
    ImageView weather_icons;
    TabLayout tab_layout;
    ViewPager view_pager;
    double longitude;
    double latitude;
    LottieAnimationView setting;
    SearchView search;
    View min_temp_view, max_temp_view;
    SimpleLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        main_temp = (MaterialTextView) findViewById(R.id.main_temp);
        location_name = (MaterialTextView) findViewById(R.id.location_name);
        humudity_number = (MaterialTextView) findViewById(R.id.humidity_number);
        wind_speed_txt = (MaterialTextView) findViewById(R.id.wind_speed_txt);
        weather_description = (MaterialTextView) findViewById(R.id.weather_description);
        weather_icons = (ImageView) findViewById(R.id.weather_icons);
        min_temp_view = findViewById(R.id.min_temp_view);
        max_temp_view = findViewById(R.id.max_temp_view);
        minTempValue = findViewById(R.id.minTempValue);
        maxTempValue = findViewById(R.id.maxTempValue);
        setting = findViewById(R.id.setting);
        search = findViewById(R.id.search);





        location = new SimpleLocation(this);

        if(!location.hasLocationEnabled())
        {
            SimpleLocation.openSettings(Weather.this);
            Intent restart = new Intent(this,Weather.class);
            int pending = 123456;
            PendingIntent pendingIntent = PendingIntent.getActivity(this, pending, restart, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC,System.currentTimeMillis()+100,pendingIntent);
            System.exit(0);

        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        //declare tabslayout and viewPager
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        view_pager = (ViewPager) findViewById(R.id.view_pager);





        setting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent setting_page = new Intent(Weather.this,SettingsActivity.class);
                startActivity(setting_page);


            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                Intent searchIntent = new Intent(Weather.this, SearchableActivity.class);
                searchIntent.putExtra(SearchManager.QUERY, query);

                Bundle appData = new Bundle();
                appData.putBoolean(SearchableActivity.JARGON, true); // put extra data to Bundle
                searchIntent.putExtra(SearchManager.APP_DATA, appData); // pass the search context data
                searchIntent.setAction(Intent.ACTION_SEARCH);
                startActivity(searchIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        max_temp_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String max_temp_txt = "بالاترین دمای فعلی";
                Toast.makeText(Weather.this,max_temp_txt,Toast.LENGTH_LONG).show();
            }
        });

        min_temp_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String min_temp_txt = "کمترین دمای فعلی";
                Toast.makeText(Weather.this,min_temp_txt,Toast.LENGTH_LONG).show();

            }
        });

        wind_speed_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wind_toast = "سرعت باد فعلی";
                Toast.makeText(Weather.this,wind_toast,Toast.LENGTH_LONG).show();

            }
        });
        humudity_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String humidity_toast = "رطوبت هوای فعلی";
                Toast.makeText(Weather.this,humidity_toast,Toast.LENGTH_LONG).show();

            }
        });
        main_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String main_temp_toast = "دمای هوای فعلی";
                Toast.makeText(Weather.this,main_temp_toast,Toast.LENGTH_LONG).show();


            }
        });


        //use functions
        currunt_weather();
        get_location_name();
        addItemtoTab();



    }



    public  int fahrenheit_to_celsius(int f)
    {
        int celsius = (int) ((f - 32) / 1.8);

        return celsius;
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
        else if(night >=0 && night<6)
        {
            isDay = false;
        }
        else if(night>=6)
        {
            isDay = true;

        }


        return isDay;

    }


    protected  void currunt_weather() {
        String api_Key = "1209f73f4fbcfd1f4ba12e26e5620759";

        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&APPID=" + api_Key + "&units=imperial&lang=fa";


        RequestQueue queue = Volley.newRequestQueue(Weather.this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    JSONObject current = response.getJSONObject("current");
                    int currunt_temp = current.getInt("temp");
                    String humidity = current.getString("humidity");
                    double wind_speed = current.getDouble("wind_speed");
                    JSONArray weather = current.getJSONArray("weather");
                    JSONObject zero_object = weather.getJSONObject(0);
                    String main = zero_object.getString("main");
                    String description = zero_object.getString("description");
                    int clouds = current.getInt("clouds");

                    //get json daily objects
                    JSONArray daily = response.getJSONArray("daily");
                    JSONObject current_day = daily.getJSONObject(0);
                    JSONObject current_day_temp = current_day.getJSONObject("temp");
                    int minTemp = current_day_temp.getInt("min");
                    int maxTemp = current_day_temp.getInt("max");




                    if (is_day())
                    {
                        switch (main)
                        {

                            case "Thunderstorm":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_thunderstorms_day)
                                        .into(weather_icons);
                                break;
                            case "Clouds":
                                if(clouds>=11 && clouds<=25 )
                                {
                                    Glide.with(Weather.this)
                                            .load(R.drawable.ic_weather_icons_partly_cloudy_day)
                                            .into(weather_icons);
                                }else if(clouds >=26)
                                {
                                    Glide.with(Weather.this)
                                            .load(R.drawable.ic_weather_icons_cloudy)
                                            .into(weather_icons);
                                }
                                break;
                            case "Clear":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_weather_icons_clear_day)
                                        .into(weather_icons);
                                break;
                            case " Drizzle":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_weather_icons_partly_cloudy_day_drizzle)
                                        .into(weather_icons);
                                break;
                            case "Snow":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_weather_icons_snow)
                                        .into(weather_icons);
                                break;
                            case "Rain":
                                Glide.with(Weather.this)
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
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_weather_icons_cloudy)
                                        .into(weather_icons);
                                break;
                            case "Tornado":
                            case "Squall":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_tornado)
                                        .into(weather_icons);
                                break;


                        }

                    }else if(!is_day())
                    {
                        switch (main)
                        {

                            case "Thunderstorm":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_thunderstorms_night)
                                        .into(weather_icons);
                                break;
                            case "Clouds":
                                if(clouds>=11 && clouds<=25)
                                {
                                    Glide.with(Weather.this)
                                            .load(R.drawable.ic_weather_icons_partly_cloudy_night)
                                            .into(weather_icons);
                                }else if(clouds >=26)
                                {
                                    Glide.with(Weather.this)
                                            .load(R.drawable.ic_weather_icons_cloudy)
                                            .into(weather_icons);
                                }
                                break;
                            case "Clear":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_weather_icons_clear_night)
                                        .into(weather_icons);
                                break;
                            case " Drizzle":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_weather_icons_partly_cloudy_night_drizzle)
                                        .into(weather_icons);
                                break;
                            case "Snow":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_weather_icons_snow)
                                        .into(weather_icons);
                                break;
                            case "Rain":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_weather_icons_rain)
                                        .into(weather_icons);
                                break;
                            case "Smoke":
                            case "Haze":
                            case "Fog":
                            case "Mist":
                            case "Dust":
                            case "Sand":
                            case "Ash":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_weather_icons_cloudy)
                                        .into(weather_icons);
                                break;
                            case "Tornado":
                            case "Squall":
                                Glide.with(Weather.this)
                                        .load(R.drawable.ic_tornado)
                                        .into(weather_icons);
                                break;


                        }

                    }

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Weather.this);

                    String tempUnit = preferences.getString("TEMP","1");
                    if("1".equals(tempUnit))
                    {
                        main_temp.setText(fahrenheit_to_celsius(currunt_temp) + "°C");
                        minTempValue.setText(fahrenheit_to_celsius(minTemp)+"°");
                        maxTempValue.setText(fahrenheit_to_celsius(maxTemp)+"°");



                    }else if("2".equals(tempUnit))
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
            public void onErrorResponse(VolleyError error)
            {
                if(error instanceof TimeoutError )
                { new AndExAlertDialog.Builder(Weather.this)
                        .setTitle("خطا")
                        .setMessage("پایین بودن سرعت اینترنت")
                        .setCancelableOnTouchOutside(true)
                        .setPositiveBtnText("اجرای مجدد")
                        .OnPositiveClicked(new AndExAlertDialogListener() {
                            @Override
                            public void OnClick(String input) {
                                Weather.this.recreate();
                            }
                        })
                        .setFont(Font.IRAN_SANS)
                        .setTitleTextColor(Color.parseColor("#ff0000"))
                        .build();
                }
                else if(error instanceof NoConnectionError || error instanceof NetworkError)
                {
                    new AndExAlertDialog.Builder(Weather.this)
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
                else if(error instanceof ServerError || error instanceof AuthFailureError)
                {
                    new AndExAlertDialog.Builder(Weather.this)
                            .setTitle("خطای سرور")
                            .setMessage("لطفا ساعاتی بعد مجددا امتحان کنید")
                            .setFont(Font.IRAN_SANS)
                            .setPositiveBtnText("خروج")
                            .OnPositiveClicked(new AndExAlertDialogListener() {
                                @Override
                                public void OnClick(String input) {
                                    finish();
                                    System.exit(0);
                                }
                            })
                            .setTitleTextColor(Color.parseColor("#ff0000"))
                            .build();
                }



            }

        });

        queue.add(request);
    }


    protected void get_location_name() {

        final String url = "https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=" + latitude + "&longitude=" + longitude + "&localityLanguage=fa";

        RequestQueue requestQueue = Volley.newRequestQueue(Weather.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String locality = response.getString("locality");
                    location_name.setText(locality);


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

    protected void addItemtoTab()
    {
        String api_Key = "1209f73f4fbcfd1f4ba12e26e5620759";
        double lat = latitude;
        double lon = longitude;
        final String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&appid=" + api_Key + "&units=imperial";


        RequestQueue requestQueue = Volley.newRequestQueue(Weather.this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    JSONArray daily = response.getJSONArray("daily");
                    JSONObject seconde_day = daily.getJSONObject(2);
                    JSONObject third_day = daily.getJSONObject(3);
                    JSONObject fourth_day = daily.getJSONObject(4);
                    JSONObject fifth_day = daily.getJSONObject(5);
                    JSONObject sixth_day = daily.getJSONObject(6);

                    long seconde_day_date = seconde_day.getLong("dt");
                    long third_day_date = third_day.getLong("dt");
                    long fourth_day_date = fourth_day.getLong("dt");
                    long fifth_day_date = fifth_day.getLong("dt");
                    long sixth_day_date =sixth_day.getLong("dt");

                    Format dateFormat = new SimpleDateFormat("EEEE");
                    String sconde_day_format = dateFormat.format(seconde_day_date*1000);
                    String third_day_format =  dateFormat.format(third_day_date*1000 );
                    String fourth_day_format =  dateFormat.format(fourth_day_date*1000);
                    String fifth_day_format =  dateFormat.format(fifth_day_date*1000);
                    String sixth_day_format =  dateFormat.format(sixth_day_date*1000);




                    ViewPageAdapter pageAdapter = new ViewPageAdapter(getSupportFragmentManager());
                    pageAdapter.addFragment(new SeventhdayFragment(), changeDaynametoPersion(sixth_day_format));
                    pageAdapter.addFragment(new SixthdayFragment(),   changeDaynametoPersion(fifth_day_format));
                    pageAdapter.addFragment(new FifthdayFragment(),   changeDaynametoPersion(fourth_day_format));
                    pageAdapter.addFragment(new FourthdayFragment(),  changeDaynametoPersion(third_day_format));
                    pageAdapter.addFragment(new ThirddayFragment(),   changeDaynametoPersion(sconde_day_format));
                    pageAdapter.addFragment(new SecondedayFragment(),"فردا");
                    pageAdapter.addFragment(new FirstdayFragment(),"امروز");




                    tab_layout.setupWithViewPager(view_pager);
                    view_pager.setAdapter(pageAdapter);
                    tab_layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    view_pager.setCurrentItem(pageAdapter.getCount());
                    view_pager.getFitsSystemWindows();




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
        location.beginUpdates();
        currunt_weather();
        get_location_name();
        addItemtoTab();
        super.onResume();
    }


}
