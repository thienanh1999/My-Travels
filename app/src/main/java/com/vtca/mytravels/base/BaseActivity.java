package com.vtca.mytravels.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.vtca.mytravels.BuildConfig;
import com.vtca.mytravels.R;
import com.vtca.mytravels.entity.TravelBaseEntity;
import com.vtca.mytravels.minhgiang.SuccessCallback;
import com.vtca.mytravels.utils.ImageViewerDialog;
import com.vtca.mytravels.utils.MyString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private Uri mCurrentPhotoUri;

    protected Uri getCropImagePath() {
        return mCurrentPhotoUri;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    /**
     * Displays a common alert dialog with Ok and Cancel buttons.
     *
     * @param titleId             title string resource id
     * @param messageId           message string resource id
     * @param okClickListener     the callback when the ok button is clicked
     * @param cancelClickListener the callback when the cancel button is clicked
     */
    protected void showAlertOkCancel(@StringRes int titleId
            , @StringRes int messageId
            , final DialogInterface.OnClickListener okClickListener
            , final DialogInterface.OnClickListener cancelClickListener) {
        new AlertDialog.Builder(this)
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButton(android.R.string.ok, okClickListener)
                .setNegativeButton(android.R.string.cancel, cancelClickListener)
                .show();
    }

    protected void showPlacePicker() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), MyConst.REQCD_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed
            // or not up to date.
            // Prompt the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this,
                    e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available
            // and the problem is not easily resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message, e);
            Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    protected void showPlaceAutocomplete() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, MyConst.REQCD_PLACE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed
            // or not up to date.
            // Prompt the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this,
                    e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available
            // and the problem is not easily resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message, e);
            Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Take a photo with a gallery app
     */
    protected void takePhotoFromGallery() {
        mCurrentPhotoUri = null;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (intent.resolveActivity(getPackageManager()) == null) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Your device does not have a gallery.", Snackbar.LENGTH_LONG).show();
            return;
        }
        startActivityForResult(intent, MyConst.REQCD_IMAGE_GALLERY);
    }

    /**
     * Take a photo with a camera app
     */
    protected void takePhotoFromCamera() {
        mCurrentPhotoUri = null;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            Log.d(TAG, "takePhotoFromCamera: mCurrentPhotoUri=" + mCurrentPhotoUri);
            // use a FileProvider
            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
            Log.d(TAG, "takePhotoFromCamera: photoURI=" + photoURI);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            if (intent.resolveActivity(getPackageManager()) == null) {
                Snackbar.make(getWindow().getDecorView().getRootView(), "Your device does not have a camera.", Snackbar.LENGTH_LONG).show();
                return;
            }
            // grant read/write permissions to other apps.
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, MyConst.REQCD_IMAGE_CAMERA);
        } catch (IOException e) {
            // Error occurred while creating the File
            Log.e(TAG, e.getMessage(), e);
            Snackbar.make(getWindow().getDecorView().getRootView(), "Cannot create an image file.", Snackbar.LENGTH_LONG).show();
            return;
        }
    }

    /**
     * Create an image file name
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory(), "mytravel");
        if (!storageDir.exists()) storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoUri = Uri.fromFile(image);
        Log.d(TAG, "createImageFile: mCurrentPhotoUri=" + mCurrentPhotoUri);
        return image;
    }

    protected void cropImage(Uri srcUri) {
        mCurrentPhotoUri = null;
        if (srcUri == null) return;
        Log.d(TAG, "cropImage: srcUri=" + srcUri);
        try {
            File outputFile = createImageFile();
            Log.d(TAG, "cropImage: outputFile=" + outputFile.getAbsolutePath());
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(srcUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("outputX", 1080);
            intent.putExtra("outputY", 1080);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("max-width", 1080);
            intent.putExtra("max-height", 1080);
//            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, MyConst.REQCD_IMAGE_CROP);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    protected Uri copyCropImageForTravel(long travelId) {
        if (mCurrentPhotoUri == null) return null;
        final File srcFile = new File(mCurrentPhotoUri.getPath());
        mCurrentPhotoUri = null;
        if (!srcFile.exists()) {
            Log.e(TAG, "Not Exist: " + srcFile.getAbsolutePath());
            return null;
        }
        final File rootDir = new File(getFilesDir(), "t" + travelId);
        if (!rootDir.exists()) rootDir.mkdirs();
        final File targetFile = new File(rootDir, srcFile.getName());
        final File thumbFile = new File(rootDir, MyConst.THUMBNAIL_PREFIX + srcFile.getName());
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        FileOutputStream thumbFos = null;
        try {
            // copy file
            sourceChannel = new FileInputStream(srcFile).getChannel();
            destChannel = new FileOutputStream(targetFile).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            // make a thumbnail image
            thumbFos = new FileOutputStream(thumbFile);
            Bitmap imageBitmap = BitmapFactory.decodeFile(srcFile.getAbsolutePath());
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, MyConst.THUMBNAIL_SIZE, MyConst.THUMBNAIL_SIZE, false);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, thumbFos);
            thumbFos.flush();

            mCurrentPhotoUri = Uri.fromFile(targetFile);
            return Uri.fromFile(thumbFile);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (sourceChannel != null) sourceChannel.close();
                srcFile.delete();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            try {
                if (destChannel != null) destChannel.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            try {
                if (thumbFos != null) thumbFos.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode);
        if (resultCode != RESULT_OK) {
            if (mCurrentPhotoUri != null) {
                // delete unused temporary files
                File file = new File(mCurrentPhotoUri.getPath());
                file.delete();
            }
            return;
        }
        switch (requestCode) {
            case MyConst.REQCD_IMAGE_CAMERA: {
                if (mCurrentPhotoUri == null) return;
                Log.d(TAG, "onActivityResult: mCurrentPhotoUri=" + mCurrentPhotoUri);
                // use a FileProvider
                Uri photoURI = FileProvider.getUriForFile(this
                        , BuildConfig.APPLICATION_ID + ".provider"
                        , new File(mCurrentPhotoUri.getPath()));
                cropImage(photoURI);
            }
            break;
            case MyConst.REQCD_IMAGE_CROP: {
                Log.d(TAG, "onActivityResult: mCurrentPhotoUri=" + mCurrentPhotoUri);
            }
            break;
            case MyConst.REQCD_IMAGE_GALLERY: {
                mCurrentPhotoUri = null;
                Log.d(TAG, "onActivityResult: getData=" + data.getData());
                if (data.getData() == null) return;
                try {
                    Uri uri = data.getData();
                    if (uri == null) return;
                    cropImage(uri);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Failed to load a image.", Snackbar.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    /**
     * Hide the keyboard.
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Opens the full screen image viewer dialog.
     *
     * @param imgUri
     * @param title
     * @param subtitle
     * @param desc
     */
    public void showImageViewer(String imgUri, String title, String subtitle, String desc) {
        Bundle b = new Bundle();
        b.putString(MyConst.KEY_ID, imgUri);
        b.putString(MyConst.KEY_TITLE, title);
        b.putString(MyConst.KEY_SUBTITLE, subtitle);
        b.putString(MyConst.KEY_DESC, desc);
        ImageViewerDialog dialog = new ImageViewerDialog();
        dialog.setArguments(b);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialog.show(ft, ImageViewerDialog.TAG);
    }

    protected void postRequestPermissionsResult(final int reqCd, final boolean result) {
        Log.d(TAG, "postRequestPermissionsResult: reqCd=" + reqCd + ", result=" + result);
    }

    /**
     * Prompts the user for permission to use APIs.
     */
    protected void requestPermissions(final int reqCd) {
        switch (reqCd) {
            case MyConst.REQCD_ACCESS_GALLERY:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    postRequestPermissionsResult(reqCd, true);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // If we should give explanation of requested permissions
                        // Show an alert dialog here with request explanation
                        showAlertOkCancel(R.string.permission_dialog_title, R.string.permission_camera_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(BaseActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        reqCd);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postRequestPermissionsResult(reqCd, false);
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                reqCd);
                    }

                }
                break;
            case MyConst.REQCD_ACCESS_CAMERA:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        + ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    postRequestPermissionsResult(reqCd, true);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // If we should give explanation of requested permissions
                        // Show an alert dialog here with request explanation
                        showAlertOkCancel(R.string.permission_dialog_title, R.string.permission_camera_msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(BaseActivity.this,
                                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        reqCd);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postRequestPermissionsResult(reqCd, false);
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                reqCd);
                    }
                }
                break;
            case MyConst.REQCD_ACCESS_FINE_LOCATION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    postRequestPermissionsResult(reqCd, true);
                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // If we should give explanation of requested permissions
                        // Show an alert dialog here with request explanation
                        showAlertOkCancel(R.string.permission_dialog_title, R.string.permission_camera_msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(BaseActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        reqCd);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postRequestPermissionsResult(reqCd, false);
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                reqCd);
                    }
                }
                break;
        }

    }

    /**
     * Handles the result of the request for permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    postRequestPermissionsResult(requestCode, false);
                    return;
                }
            }
            postRequestPermissionsResult(requestCode, true);
            return;
        }

        postRequestPermissionsResult(requestCode, false);
    }

    public void openGoogleMap(View v, TravelBaseEntity item) {
        final String appPackageName = "com.google.android.apps.maps";
        Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f", item.getPlaceLat(), item.getPlaceLng()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(appPackageName);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            if (v != null) Snackbar.make(v, R.string.no_google_map, Snackbar.LENGTH_SHORT).show();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    public void openGoogleMap(View v, double lat, double lng, String name, String query) {
        final String appPackageName = "com.google.android.apps.maps";
        Uri gmmIntentUri;
        if (MyString.isNotEmpty(query)) {
            gmmIntentUri = Uri.parse(String.format(Locale.ENGLISH, "geo:%f,%f?q=%s", lat, lng, Uri.encode(query)));
        } else {
            gmmIntentUri = Uri.parse(String.format(Locale.ENGLISH, "geo:0,0?q=%f,%f(%s)", lat, lng, Uri.encode(name)));
        }
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(appPackageName);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            if (v != null) Snackbar.make(v, R.string.no_google_map, Snackbar.LENGTH_SHORT).show();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    public void writeBitmapToCache(Bitmap bitmap, String placeID) {
        File cacheDir = getApplicationContext().getCacheDir();
        File f = new File(cacheDir, placeID);

        try {
            FileOutputStream out = new FileOutputStream(
                    f);
            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100, out);
            out.flush();
            out.close();
            Log.d(TAG, "writeBitmapToCache: " + f.getName() + " Success");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap readBitmapFromCache(String placeID) {
        File cacheDir = getApplicationContext().getCacheDir();
        File f = new File(cacheDir, placeID);
        if (!f.exists()) return null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(fis);

    }

    public void setTitleImage(final ImageView titleImage, final String placeId, final SuccessCallback callback) {
        Bitmap bitmap = readBitmapFromCache(placeId);
        if (bitmap == null) {
            final GeoDataClient mGeoDataClient = Places.getGeoDataClient(this);
            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            writeBitmapToCache(bitmap, placeId);
                            titleImage.setImageBitmap(bitmap);
                            if (callback != null) callback.onSuccess();
                        }
                    });
                }
            });
        } else {
            titleImage.setImageBitmap(bitmap);
            if (callback != null) callback.onSuccess();
        }

    }

}
