package com.vtca.mytravels;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vtca.mytravels.base.BaseActivity;
import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.entity.Travel;
import com.vtca.mytravels.thienanh.ExpensesChartActivity;
import com.vtca.mytravels.traveldetail.SectionsPagerAdapter;
import com.vtca.mytravels.traveldetail.TravelDetailBaseFragment;
import com.vtca.mytravels.traveldetail.TravelDetailViewModel;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class TravelDetailActivity extends BaseActivity {
    private static final String TAG = TravelDetailActivity.class.getSimpleName();

    private SectionsPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private CollapsingToolbarLayout mToolbarLayout;
    private TextView mSubtitle;
    private ImageView titleImage;
    private final Observer<Travel> mTravelObserver = new Observer<Travel>() {
        @Override
        public void onChanged(Travel travel) {
            Log.d(TAG, "onChanged: travel=" + travel);
            if (travel == null) return;
            mToolbarLayout.setTitle(travel.getTitle());
            setTitleImage(titleImage, travel.getPlaceId(), null);
            mSubtitle.setText(travel.getPlaceName() + "/" + travel.getPlaceAddr() + "\n" + travel.getDateTimeText() + "~" + travel.getEndDtText());
        }
    };
    private TravelDetailViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mViewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mViewPagerAdapter);
        titleImage = findViewById(R.id.titleImage);


        TabLayout tabLayout = findViewById(R.id.tabs);
        // Use setupWithViewPager(ViewPager) to link a TabLayout with a ViewPager.
        // The individual tabs in the TabLayout will be automatically populated
        // with the page titles from the PagerAdapter.
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TravelDetailBaseFragment fragment = (TravelDetailBaseFragment) mViewPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
                fragment.requestAddItem();
            }
        });

        mToolbarLayout = findViewById(R.id.toolbar_layout);
        mSubtitle = findViewById(R.id.subtitle_txt);

        long travelId = getIntent().getLongExtra(MyConst.REQKEY_TRAVEL_ID, 0);
        Log.d(TAG, "onCreate: travelId=" + travelId);

        mViewModel = ViewModelProviders.of(this).get(TravelDetailViewModel.class);
        mViewModel.setTravelId(travelId);
        mViewModel.getTravel().observe(this, mTravelObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_travel_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        double lng = getIntent().getDoubleExtra("place_lng", 0);
        switch (item.getItemId()) {
            case R.id.action_show_chart: {
                Intent intent = new Intent(TravelDetailActivity.this, ExpensesChartActivity.class);
                long travelId = getIntent().getLongExtra(MyConst.REQKEY_TRAVEL_ID, 0);
                intent.putExtra(MyConst.REQKEY_TRAVEL_ID, travelId);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
