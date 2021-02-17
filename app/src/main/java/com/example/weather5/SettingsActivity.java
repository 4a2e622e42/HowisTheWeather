package com.example.weather5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {

            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            findPreference("email").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    String emailAddresses = "ali.shojasani@tutamail.com";
                    Intent send_mail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto: " + emailAddresses));

                    startActivity(Intent.createChooser(send_mail, "Send With"));

                    return false;
                }
            });


            String version_number = BuildConfig.VERSION_NAME;
            findPreference("Version").setSummary(version_number);



        }
    }
}