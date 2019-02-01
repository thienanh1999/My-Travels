package com.vtca.mytravels.quanghung;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.vtca.mytravels.MainActivity;
import com.vtca.mytravels.R;
import com.vtca.mytravels.base.BaseActivity;
import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.minhgiang.MyAlarmService;

import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView tvNotificationSubtitle;
    TextView tvUpcomingTravelsSubtitle;
    TextView tvNotiFreqSub;
    TextView tvLanguageSubtitle;
    RelativeLayout languageLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        tvNotificationSubtitle = findViewById(R.id.tvNotificationSubtitle);
        tvUpcomingTravelsSubtitle = findViewById(R.id.tvUpcomingTravelsSubtitle);
        tvNotiFreqSub = findViewById(R.id.tvGetNotificationFrequencySubtitle);
        tvLanguageSubtitle = findViewById(R.id.tvLanguageSubtitle);

        // Call Shared Preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        editor = sharedPreferences.edit();
        // Language
        loadLocale();
        languageLayout = findViewById(R.id.language_setting_layout);
        languageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });
        tvLanguageSubtitle.setText(sharedPreferences.getString(MyConst.LANGUAGE_SAVED, getString(R.string.english_default)));
        tvUpcomingTravelsSubtitle.setText(setTextUpcomingTravelsSubtitle());

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Spinner Upcoming
        Spinner spinner = findViewById(R.id.up_coming_day_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.
                createFromResource(this, R.array.up_coming_day_array,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new UpcomingSpinner());
        spinner.setSelection(getSavedRange());

        // Spinner Notifications Frequency
        Spinner spinnerNotification = findViewById(R.id.notifications_frequency_spinner);
        ArrayAdapter<CharSequence> adapterNotification = ArrayAdapter.
                createFromResource(this, R.array.notification_frequency_array,
                        android.R.layout.simple_spinner_item);
        adapterNotification.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNotification.setAdapter(adapterNotification);
        spinnerNotification.setOnItemSelectedListener(new NotificationSpinner());
        spinnerNotification.setSelection(getSavedFrequency());

        // Switch
        Switch sw = findViewById(R.id.notification_settings_switch);
        Boolean isChecked = sharedPreferences.getBoolean(MyConst.NOTIFICATION_ON_OFF, false);
        sw.setChecked(isChecked);
        tvNotificationSubtitle.setText(isChecked?
                R.string.get_notifications_for_the_next_travels:
                R.string.do_not_get_notifications_for_the_next_travels);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(MyConst.NOTIFICATION_ON_OFF, isChecked);
                editor.apply();
                tvNotificationSubtitle.setText(isChecked?
                        R.string.get_notifications_for_the_next_travels:
                        R.string.do_not_get_notifications_for_the_next_travels);
            }
        });


    }

    private int getSavedFrequency() {
        if (sharedPreferences.getLong(MyConst.FREQUENCY_NOTI, 86400000) == 86400000) return 1;
        else return 0;
    }

    private void showChangeLanguageDialog() {
        final String[] languages = {"English (Default)", "Tiếng Việt", "Korean"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
        mBuilder.setTitle(getString(R.string.choose_language));
        mBuilder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    tvLanguageSubtitle.setText(getString(R.string.english_default));
                    editor.putString(MyConst.LANGUAGE_SAVED, getString(R.string.english_default));
                    editor.putString(MyConst.LANGUAGE, "en");
                    editor.apply();
                    setLocale("en");
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (i == 1) {
                    tvLanguageSubtitle.setText(getString(R.string.tieng_viet));
                    editor.putString(MyConst.LANGUAGE_SAVED, getString(R.string.tieng_viet));
                    editor.putString(MyConst.LANGUAGE, "vi");
                    editor.apply();
                    setLocale("vi");
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (i == 2) {
                    tvLanguageSubtitle.setText(getString(R.string.korean));
                    editor.putString(MyConst.LANGUAGE_SAVED, getString(R.string.korean));
                    editor.putString(MyConst.LANGUAGE, "ko");
                    editor.apply();
                    setLocale("ko");
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void loadLocale() {
        String language = sharedPreferences.getString(MyConst.LANGUAGE, "");
        Log.d(TAG, "loadLocale: " + language);
        setLocale(language);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.homeAsUp:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getSavedRange() {
        long res = sharedPreferences.getLong(MyConst.UP_COMING_DAY, 1);
        Log.d(TAG, "Test spinner, selected item: " + res);
        if (res == 1) return 0;
        else if (res == 7) return 1;
        else return 2;
    }

    private int setTextUpcomingTravelsSubtitle() {
        long res = sharedPreferences.getLong(MyConst.UP_COMING_DAY, 1);
        if (res == 1) return R.string.one_day;
        else if (res == 7) return R.string.one_week;
        else return R.string.one_month;
    }

    class UpcomingSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            // Set range of up-coming-day's list
            int res;
            if (position == 0) res = 1;
            else if (position == 1) res = 7;
            else res = 30;

            // Commit to shared preferences
            editor.putLong(MyConst.UP_COMING_DAY, res);
            editor.apply();
            tvUpcomingTravelsSubtitle.setText(setTextUpcomingTravelsSubtitle());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class NotificationSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 1) {
                editor.putLong(MyConst.FREQUENCY_NOTI, 86400000);
                editor.apply();
                tvNotiFreqSub.setText(getString(R.string.one_day));
            } else {
                editor.putLong(MyConst.FREQUENCY_NOTI, 60000);
                editor.apply();
                tvNotiFreqSub.setText(getString(R.string.one_minute));
            }

            stopService(new Intent(getBaseContext(), MyAlarmService.class));
            startService(new Intent(getBaseContext(), MyAlarmService.class));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
