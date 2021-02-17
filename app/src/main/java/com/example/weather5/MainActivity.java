package com.example.weather5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.animation.TimeAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import im.delight.android.location.SimpleLocation;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
{
    ImageView man_walk_under_cloud;
    Button    location_permission_btn;
    TextView  finde1,finde2;
    Animation man_walk_under_cloud_anim,location_permission_txt_anim,location_permission_btn_anim;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    SimpleLocation location;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        finde1 = findViewById(R.id.finde1);
        finde2 = findViewById(R.id.find2);
        man_walk_under_cloud = (ImageView) findViewById(R.id.man_walk_under_cloud);
        location_permission_btn = (Button) findViewById(R.id.location_permission_btn);
        man_walk_under_cloud_anim = AnimationUtils.loadAnimation(this, R.anim.man_walk_under_cloud_anim);
        location_permission_btn_anim = AnimationUtils.loadAnimation(this, R.anim.location_permission_btn_anim);
        location_permission_txt_anim = AnimationUtils.loadAnimation(this,R.anim.location_permission_txt_anim);
        location = new SimpleLocation(MainActivity.this);


        //using animations
        man_walk_under_cloud.startAnimation(man_walk_under_cloud_anim);
        location_permission_btn.startAnimation(location_permission_btn_anim);
        finde1.startAnimation(location_permission_txt_anim);
        finde2.startAnimation(location_permission_txt_anim);


        //load gif file with Glide
        Glide.with(MainActivity.this)
                .load(R.raw.man_walk_under_cloud)
                .into(man_walk_under_cloud);


        location_permission_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(location_Permission() == false)
                {
                    location_Permission();
                    if(!location.hasLocationEnabled())
                    {
                        SimpleLocation.openSettings(MainActivity.this);
                    }

                }else if(location_Permission() == true)
                {
                    Intent mainactivity_2 = new Intent( MainActivity.this, MainActivity2.class);
                    startActivity( mainactivity_2);
                }
            }
            });


        SharedPreferences preferences = getSharedPreferences("PREFERNCE",MODE_PRIVATE);

        String firstTime = preferences.getString("firstTime","");

        if(firstTime.equals(""))
        {
            play_audio();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("firstTime","No");
            editor.apply();

        }else
        {
            Intent intent = new Intent(MainActivity.this,Weather.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
            MainActivity.super.finish();
        }



        }
        //declare get location access with Easypermission liberiry
        @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
        protected boolean location_Permission ()
        {
            boolean access;
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (EasyPermissions.hasPermissions(this, perms))
            {

                access = true;

            } else
                {
                EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
                access = false;
            }

            return access;
        }



        void play_audio()
        {
            //declare timers
            Timer sneeze = new Timer();
            Timer thounder = new Timer();

            //declare sneeze timertask
            TimerTask sneeze_task = new TimerTask()
            {
                @Override
                public void run()
                {
                    MediaPlayer sneeze_audio = MediaPlayer.create(MainActivity.this, R.raw.sick_man_sneeze);
                    sneeze_audio.start();


                }
            };
            //declre thounder timertask
            TimerTask thounder_task = new TimerTask()
            {
                @Override
                public void run()
                {
                    MediaPlayer thounder = MediaPlayer.create(MainActivity.this,R.raw.thounderr);
                    thounder.start();


                }
            };

            //start task timers
            sneeze.schedule(sneeze_task,2580);
            thounder.schedule(thounder_task,4000);

        }


}



