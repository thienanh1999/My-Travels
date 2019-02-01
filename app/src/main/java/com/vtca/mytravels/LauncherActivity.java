package com.vtca.mytravels;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.quanghung.ViewPagerAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

public class LauncherActivity extends AppCompatActivity {

    ViewPager viewPager;
    PagerAdapter adapter;
    TextView tvSkip;
    int[] img;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static int currentPage;
    TextView tvStartUsingMyTravels;
    private static final String TAG = LauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        // no need
//        // setContentView(R.layout.activity_launcher);
//
//        // do something
//
//        // start MainActivity
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//
//        // finish this activity
//        finish();

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // Call Shared Preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = sharedPreferences.edit();

        if (!sharedPreferences.getBoolean(MyConst.SKIP_TUTORIALS, false)) {


            // init images
            img = new int[]{
                    R.drawable.tutorials_1,
                    R.drawable.tutorials_2,
                    R.drawable.tutorials_3,
                    R.drawable.tutorials_4,
                    R.drawable.tutorials_5,
                    R.drawable.tutorials_6,
                    R.drawable.tutorials_7};

            // View pager
            viewPager = findViewById(R.id.pager);
            adapter = new ViewPagerAdapter(LauncherActivity.this, img);
            viewPager.setAdapter(adapter);

            // Circle Indicator
            CircleIndicator indicator = findViewById(R.id.indicator);
            indicator.setViewPager(viewPager);

            // Skip
            tvSkip = findViewById(R.id.tvSkip);
            tvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    editor.putBoolean(MyConst.SKIP_TUTORIALS, true);
                    editor.apply();
                    finish();
                }
            });

            // Getting Started
            tvStartUsingMyTravels = findViewById(R.id.tvStartUsingMyTravels);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (currentPage + 1 == img.length) {
                        tvSkip.setVisibility(View.GONE);
                        tvStartUsingMyTravels.setVisibility(View.VISIBLE);
                        tvStartUsingMyTravels.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                editor.putBoolean(MyConst.SKIP_TUTORIALS, true);
                                editor.apply();
                                finish();
                            }
                        });
                    } else {
                        tvStartUsingMyTravels.setVisibility(View.GONE);
                        tvSkip.setVisibility(View.VISIBLE);
                    }
                }
            });

        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
