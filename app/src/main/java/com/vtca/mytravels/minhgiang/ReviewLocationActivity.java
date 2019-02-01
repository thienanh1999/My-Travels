package com.vtca.mytravels.minhgiang;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vtca.mytravels.EditTravelActivity;
import com.vtca.mytravels.R;
import com.vtca.mytravels.base.BaseActivity;
import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.entity.SuggestLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReviewLocationActivity extends BaseActivity {
    private String id = "";
    private String address = "";
    private List<SuggestLocation> suggestLocations = new ArrayList<>();
    private Comparator<SuggestLocation> comparator = new Comparator<SuggestLocation>() {
        @Override
        public int compare(SuggestLocation left, SuggestLocation right) {
            return right.getUserRatingTotal() - left.getUserRatingTotal(); // use your logic
        }
    };
    private RecyclerView recyclerView;
    private SuggestAdapter suggestAdapter;
    private GeoDataClient mGeoDataClient;
    private ImageView titleImage;
    private LinearLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("place_id");
        mGeoDataClient = Places.getGeoDataClient(this);
        address = getIntent().getStringExtra("place_name");
        findViewById(R.id.tvCreateTrip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), EditTravelActivity.class);
                intent.putExtra("place_id", id);
                intent.putExtra("place_name", address);
                startActivityForResult(intent, MyConst.REQCD_TRAVEL_ADD);
            }
        });
        recyclerView = findViewById(R.id.recyclerview);
        titleImage = findViewById(R.id.titleImage);
        progress = findViewById(R.id.progress);
        suggestAdapter = new SuggestAdapter(suggestLocations, getBaseContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(suggestAdapter);
        try {

            setTitle(address);
            getJsonFromPlaceAPI(address, new SuccessCallback() {
                @Override
                public void onSuccess() {
                    setTitleImage(titleImage, id, new SuccessCallback() {
                        @Override
                        public void onSuccess() {
                            progress.setVisibility(View.GONE);
                        }
                    });
                    suggestAdapter.notifyDataSetChanged();
                    synchronized (suggestLocations) {
                        for (int i = 0; i < suggestLocations.size(); i++) {

                            Bitmap bitmap = readBitmapFromCache(suggestLocations.get(i).getPlaceID());
                            if (bitmap == null) {
                                final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(suggestLocations.get(i).getPlaceID());
                                final int finalI = i;
                                photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                                        // Get the list of photos.
                                        PlacePhotoMetadataResponse photos = task.getResult();
                                        // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                                        // Get the first photo in the list.
                                        if (photoMetadataBuffer.getCount() > 0) {
                                            PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                                            // Get the attribution text.
                                            CharSequence attribution = photoMetadata.getAttributions();
                                            // Get a full-size bitmap for the photo.
                                            Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                                            photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                                @Override
                                                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                                    PlacePhotoResponse photo = task.getResult();
                                                    writeBitmapToCache(photo.getBitmap(), suggestLocations.get(finalI).getPlaceID());
                                                    suggestLocations.get(finalI).setPhoto(photo.getBitmap());
                                                    suggestAdapter.notifyItemChanged(finalI);
                                                }
                                            });
                                        }

                                    }
                                });
                            } else {
                                suggestLocations.get(i).setPhoto(bitmap);
                                suggestAdapter.notifyItemChanged(i);
                            }


                        }
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getJsonFromPlaceAPI(String location, final SuccessCallback callback) throws IOException {
        String URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=point+of+interest+in+" + location + "&language=en&key=AIzaSyA8eGA4gU2cchy17yRlr2qIl90TVtuzKzo";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                ReviewLocationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject result = new JSONObject(myResponse);
                            JSONArray jsonArray = result.getJSONArray("results");
                            SuggestLocation location;
                            JSONObject object;
                            JSONObject geometryObj;
                            JSONObject locatObj;
                            int count = jsonArray.length() > 7 ? 7 : jsonArray.length();
                            for (int i = 0; i < count; i++) {
                                object = (JSONObject) jsonArray.get(i);
                                location = new SuggestLocation(object.getString("place_id"));
                                location.setName(object.getString("name"));
                                location.setRating(object.getDouble("rating"));
                                location.setUserRatingTotal(object.getInt("user_ratings_total"));
                                location.setFormatedAddress(object.getString("formatted_address"));
                                String geometry = object.getString("geometry");
                                geometryObj = new JSONObject(geometry);
                                String locat = geometryObj.getString("location");
                                locatObj = new JSONObject(locat);
                                location.setPlaceLat(locatObj.getDouble("lat"));
                                location.setPlaceLng(locatObj.getDouble("lng"));
                                suggestLocations.add(location);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Collections.sort(suggestLocations, comparator);
                        callback.onSuccess();
                        Log.d("giangtm1", "run: " + myResponse);
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MyConst.REQCD_TRAVEL_ADD:
                finish();
                break;
        }
    }

    public void onClick(View view) {
        double lat = getIntent().getDoubleExtra("place_lat", 0);
        double lng = getIntent().getDoubleExtra("place_lng", 0);
        switch (view.getId()) {
            case R.id.btn_restaurants:
                openGoogleMap(view, lat, lng, address, "restaurants");
                break;
            case R.id.btn_hotels:
                openGoogleMap(view, lat, lng, address, "hotels");

                break;
            case R.id.btn_attractions:
                openGoogleMap(view, lat, lng, address, "attractions");
                break;
        }
    }
}
