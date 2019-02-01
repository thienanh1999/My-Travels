package com.vtca.mytravels;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.vtca.mytravels.base.BaseActivity;
import com.vtca.mytravels.base.MyApplication;
import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.base.TravelSort;
import com.vtca.mytravels.entity.Travel;
import com.vtca.mytravels.main.TravelListAdapter;
import com.vtca.mytravels.main.TravelViewModel;
import com.vtca.mytravels.minhgiang.MyAlarmService;
import com.vtca.mytravels.minhgiang.ReviewLocationActivity;
import com.vtca.mytravels.quanghung.SettingsActivity;
import com.vtca.mytravels.quanghung.UpComingTravelsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.vtca.mytravels.base.MyConst.REQCD_PLACE_AUTOCOMPLETE;

public class MainActivity extends BaseActivity implements TravelListAdapter.ListItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String USER = "user";

    public static Context contextOfApplication;
    private DrawerLayout mDrawerLayout;
    private TravelViewModel mTravelViewModel;
    private TravelListAdapter mTravelListAdapter;
    private RecyclerView recyclerView;
    private List<Travel> travelList = new ArrayList<>();
    private SharedPreferences sharedPref;
    private GeoDataClient mGeoDataClient;
    private final Observer<List<Travel>> mTravelObserver = new Observer<List<Travel>>() {
        @Override
        public void onChanged(List<Travel> travels) {
            Log.d(TAG, "onChanged: travels.size=" + travels.size());
//            mTravelListAdapter.updateTravelListItems(travels);
            mTravelListAdapter.setList(travels);
            if (!isMyServiceRunning(MyAlarmService.class)) {
                Intent intent = new Intent(getBaseContext(), MyAlarmService.class);
                startService(intent);
            }

        }
    };

    NavigationView navigationView;
    View header;

    LoginButton FbLogin;
    ImageView IvFbAvatar;
    TextView TvFbName;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextOfApplication = getApplicationContext();

        // Set up language
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lang = sharedPref.getString(MyConst.LANGUAGE, "");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        if (savedInstanceState == null) recreate();

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        // Navigation Drawer's set item click listener
        navigationView = findViewById(R.id.drawer_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

       // Geo Data Client
        mGeoDataClient = Places.getGeoDataClient (this);


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // call EditTravelActivity
//                Intent intent = new Intent(MainActivity.this
//                        , EditTravelActivity.class);
//                startActivityForResult(intent, REQCD_TRAVEL_ADD);
//            }
//        });

        mTravelListAdapter = new TravelListAdapter(this, travelList);
        mTravelListAdapter.setListItemClickListener(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(mTravelListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTravelViewModel = ViewModelProviders.of(this).get(TravelViewModel.class);
        mTravelViewModel.getAllTravels().observe(this, mTravelObserver);
        mTravelViewModel.setTravelSort(((MyApplication) getApplication()).getTravelSort().DEFAULT);

        setupFacebookLogin();
    }

    private void setupFacebookLogin() {
        //get KeyHash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.vtca.mytravels",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        header = navigationView.getHeaderView(0);

        FbLogin = header.findViewById(R.id.btLoginViaFacebook);
        IvFbAvatar = header.findViewById(R.id.ivAvatar);
        TvFbName = header.findViewById(R.id.tvName);
        FbLogin.setReadPermissions(Arrays.asList("public_profile"));
        TvFbName.setText("Name");

        final Transformation transformation = new CropCircleTransformation();

        SharedPreferences sharedPreferences = getSharedPreferences(USER, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String facebookID = sharedPreferences.getString("facebookID","");
        String userName = sharedPreferences.getString("userName", "Hello User");
        Picasso.get()
                .load("https://graph.facebook.com/" + facebookID + "/picture?type=large")
                .placeholder(R.drawable.progress_animation)
                .transform(transformation)
                .into(IvFbAvatar);
        TvFbName.setText(userName);

        callbackManager = CallbackManager.Factory.create();
        FbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Success");
                GraphRequest graphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    editor.putString("facebookID", object.getString("id")).commit();
                                    editor.putString("userName", object.getString("name")).commit();
                                    TvFbName.setText(object.getString("name"));
                                    Picasso.get().load("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large")
                                            .placeholder(R.drawable.progress_animation)
                                            .transform(transformation)
                                            .into(IvFbAvatar);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Error");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        TravelSort travelSort = ((MyApplication) getApplication()).getTravelSort();
        switch (travelSort) {
            case DEFAULT:
                menu.findItem(R.id.action_sort_default).setChecked(true);
                break;
            case TITLE_ASC:
                menu.findItem(R.id.action_sort_travel_asc).setChecked(true);
                break;
            case TITLE_DESC:
                menu.findItem(R.id.action_sort_travel_desc).setChecked(true);
                break;
            case START_ASC:
                menu.findItem(R.id.action_sort_start_asc).setChecked(true);
                break;
            case START_DESC:
                menu.findItem(R.id.action_sort_start_desc).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_default:
                mTravelViewModel.setTravelSort(TravelSort.DEFAULT);
                item.setChecked(true);
                return true;
            case R.id.action_sort_travel_asc:
                mTravelViewModel.setTravelSort(TravelSort.TITLE_ASC);
                item.setChecked(true);
                return true;
            case R.id.action_sort_travel_desc:
                mTravelViewModel.setTravelSort(TravelSort.TITLE_DESC);
                item.setChecked(true);
                return true;
            case R.id.action_sort_start_asc:
                mTravelViewModel.setTravelSort(TravelSort.START_ASC);
                item.setChecked(true);
                return true;
            case R.id.action_sort_start_desc:
                mTravelViewModel.setTravelSort(TravelSort.START_DESC);
                item.setChecked(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQCD_PLACE_AUTOCOMPLETE:
                if (data == null) return;
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (place != null) {
                    Intent intent = new Intent(MainActivity.this
                            , ReviewLocationActivity.class);
                    intent.putExtra("place_id", place.getId());
                    intent.putExtra("place_name", place.getName());
                    intent.putExtra("place_lat", place.getLatLng().latitude);
                    intent.putExtra("place_lng", place.getLatLng().longitude);
                    startActivity(intent);
                }
                break;
        }
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
        Intent intent = new Intent(MainActivity.this, TravelDetailActivity.class);
        intent.putExtra(MyConst.REQKEY_TRAVEL_ID, entity.getId());
        startActivity(intent);
    }

    public void onClick(View view) {
        showPlaceAutocomplete();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.drawer_up_coming:
                Intent intent1 = new Intent(MainActivity.this, UpComingTravelsActivity.class);
                startActivity(intent1);
        }
        return true;
    }
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
