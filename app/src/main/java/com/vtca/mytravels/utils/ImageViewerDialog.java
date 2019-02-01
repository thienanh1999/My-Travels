package com.vtca.mytravels.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vtca.mytravels.R;
import com.vtca.mytravels.base.MyConst;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ImageViewerDialog extends DialogFragment {
    public static final String TAG = ImageViewerDialog.class.getSimpleName();

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private float mScaleFactor = 1.f;

    private ImageView imageView;
    private Uri imgUri;
    private String title;
    private String subtitle;
    private String desc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_DialogImageViewer);
        Bundle b = getArguments();
        String img = b.getString(MyConst.KEY_ID, null);
        title = b.getString(MyConst.KEY_TITLE, null);
        subtitle = b.getString(MyConst.KEY_SUBTITLE, null);
        desc = b.getString(MyConst.KEY_DESC, null);
        if (MyString.isNotEmpty(img)) imgUri = Uri.parse(img);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return dialog;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_image_viewer, container, false);
        imageView = rootView.findViewById(R.id.imageView);
        ((TextView) rootView.findViewById(R.id.title_txt)).setText(title);
        ((TextView) rootView.findViewById(R.id.subtitle_txt)).setText(subtitle);
        ((TextView) rootView.findViewById(R.id.desc_txt)).setText(desc);
        if (imgUri != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(imgUri);
            mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
            mGestureDetector = new GestureDetector(getContext(), new GestureListener());
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGestureDetector.onTouchEvent(event);
                    mScaleGestureDetector.onTouchEvent(event);
                    return true;
                }
            });

        } else {
            imageView.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float x = imageView.getX() - (e1.getX() - e2.getX());
            float y = imageView.getY() - (e1.getY() - e2.getY());
            imageView.setTranslationX(x);
            imageView.setTranslationY(y);
            return true;
        }

    }


}
