package com.vtca.mytravels.quanghung;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.vtca.mytravels.EditTravelActivity;
import com.vtca.mytravels.MainActivity;
import com.vtca.mytravels.R;
import com.vtca.mytravels.TravelDetailActivity;
import com.vtca.mytravels.base.BaseActivity;
import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.base.TravelSort;
import com.vtca.mytravels.entity.Travel;
import com.vtca.mytravels.main.TravelListAdapter;
import com.vtca.mytravels.main.TravelViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class UpComingTravelsActivity extends BaseActivity implements TravelListAdapter.ListItemClickListener {

    private static final String TAG = UpComingTravelsActivity.class.getSimpleName();
    private TravelViewModel mTravelViewModel;
    private TravelListAdapter mTravelListAdapter;
    private RecyclerView recyclerView;
    private List<Travel> travelList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private GeoDataClient mGeoDataClient;
    int optionMenuChecked = 0;
    private final Observer<List<Travel>> mTravelObserver = new Observer<List<Travel>>() {
        @Override
        public void onChanged(List<Travel> travels) {
            Log.d(TAG, "onChanged: travels.size=" + travels.size());
            mTravelListAdapter.updateTravelListItems(travels);
            recyclerView.smoothScrollToPosition(0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_coming_travels);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Call Shared Preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = sharedPreferences.edit();

        // Geo Data Client
        mGeoDataClient = Places.getGeoDataClient (this);
        mTravelListAdapter = new TravelListAdapter(this, travelList);
        mTravelListAdapter.setListItemClickListener(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(mTravelListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTravelViewModel = ViewModelProviders.of(this).get(TravelViewModel.class);
        mTravelViewModel.getAllTravels().observe(this, mTravelObserver);
        mTravelViewModel.setTravelSort(TravelSort.UP_COMING);
    }

    @Override
    public void onDeleteItemClick(final Travel entity) {
        showAlertOkCancel(R.string.travel_del_title, R.string.travel_del_msg
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        entity.setBitmap(null);
                        mTravelViewModel.delete(entity);
                    }
                }
                , null);
    }

    @Override
    public void onEditItemClick(Travel entity) {
        Intent intent = new Intent(this, EditTravelActivity.class);
        intent.putExtra(MyConst.REQKEY_TRAVEL, entity);
        intent.setAction(MyConst.REQACTION_EDIT_TRAVEL);
        startActivityForResult(intent, MyConst.REQCD_TRAVEL_EDIT);
    }

    @Override
    public void onListItemClick(Travel entity) {
        Intent intent = new Intent(UpComingTravelsActivity.this, TravelDetailActivity.class);
        intent.putExtra(MyConst.REQKEY_TRAVEL_ID, entity.getId());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.one_day_display:
                optionMenuChecked = 1;
                long tmp = sharedPreferences.getLong(MyConst.UP_COMING_DAY, 1);
                editor.putLong(MyConst.UP_COMING_DAY, 1);
                editor.apply();
                mTravelViewModel.setTravelSort(TravelSort.UP_COMING);
                editor.putLong(MyConst.UP_COMING_DAY, tmp);
                editor.apply();
                item.setChecked(true);
                return true;
            case R.id.one_week_display:
                optionMenuChecked = 2;
                long tmp1 = sharedPreferences.getLong(MyConst.UP_COMING_DAY, 1);
                editor.putLong(MyConst.UP_COMING_DAY, 7);
                editor.apply();
                mTravelViewModel.setTravelSort(TravelSort.UP_COMING);
                editor.putLong(MyConst.UP_COMING_DAY, tmp1);
                editor.apply();
                item.setChecked(true);
                return true;
            case R.id.one_month_display:
                optionMenuChecked = 3;
                long tmp2 = sharedPreferences.getLong(MyConst.UP_COMING_DAY, 1);
                editor.putLong(MyConst.UP_COMING_DAY, 30);
                editor.apply();
                mTravelViewModel.setTravelSort(TravelSort.UP_COMING);
                editor.putLong(MyConst.UP_COMING_DAY, tmp2);
                editor.apply();
                item.setChecked(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upcoming_travels, menu);
        switch (optionMenuChecked) {
            case 0:
                long defaultValue = sharedPreferences.getLong(MyConst.UP_COMING_DAY, 1);
                if (defaultValue == 1) menu.findItem(R.id.one_day_display).setChecked(true);
                else if (defaultValue == 7) menu.findItem(R.id.one_week_display).setChecked(true);
                else menu.findItem(R.id.one_month_display).setChecked(true);
                break;
            case 1:
                menu.findItem(R.id.one_day_display).setChecked(true);
                break;
            case 2:
                menu.findItem(R.id.one_week_display).setChecked(true);
                break;
            case 3:
                menu.findItem(R.id.one_month_display).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.isTaskRoot())
            startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }
}
