package com.vtca.mytravels.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.vtca.mytravels.R;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    private static final String TRAVEL_SORT_NAME = "TRAVEL_SORT_NAME";
    private static final String TRAVEL_SORT_KEY = "TRAVEL_SORT_KEY";
    private static final String TRAVEL_LONGCLICK = "TRAVEL_LONGCLICK";
    private static final String TRAVEL_LONGCLICK_DONT_SHOW = "TRAVEL_LONGCLICK_DONT_SHOW";
    private Boolean mMainActivityShowInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        String[] itemKey = getResources().getStringArray(R.array.currency_key);
        String[] itemLabel = getResources().getStringArray(R.array.currency_label);
        for (int i = 0; i < itemKey.length; i++) {
            MyConst.CURRENCY_CODE.put(itemKey[i], new MyKeyValue(i, itemKey[i], itemLabel[i]));
        }

        itemKey = getResources().getStringArray(R.array.expense_key);
        itemLabel = getResources().getStringArray(R.array.expense_label);
        for (int i = 0; i < itemKey.length; i++) {
            MyConst.BUDGET_CODE.put(itemKey[i], new MyKeyValue(i, itemKey[i], itemLabel[i]));
        }
    }

    /**
     * Returns the sorting option selected by the user from SharedPreferences.
     *
     * @return the selected sorting option.
     */
    public TravelSort getTravelSort() {
        SharedPreferences sharedPref = getSharedPreferences(TRAVEL_SORT_NAME, Context.MODE_PRIVATE);
        String name = sharedPref.getString(TRAVEL_SORT_KEY, TravelSort.DEFAULT.name());
        TravelSort travelSort = TravelSort.DEFAULT;
        try {
            travelSort = TravelSort.valueOf(name);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return travelSort;
    }


    /**
     * Saves the sorting option selected in SharedPreferences.
     *
     * @param travelSort the sorting option.
     */
    public void setTravelSort(TravelSort travelSort) {
        SharedPreferences sharedPref = getSharedPreferences(TRAVEL_SORT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TRAVEL_SORT_KEY, travelSort.name());
        editor.commit();
    }

    public boolean getMainActivityShowInfo() {
        if (mMainActivityShowInfo == null) {
            SharedPreferences sharedPref = getSharedPreferences(TRAVEL_LONGCLICK, Context.MODE_PRIVATE);
            mMainActivityShowInfo = sharedPref.getBoolean(TRAVEL_LONGCLICK_DONT_SHOW, true);
        }
        return mMainActivityShowInfo;
    }

    public void setMainActivityShowInfo(boolean dontShow) {
        SharedPreferences sharedPref = getSharedPreferences(TRAVEL_LONGCLICK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(TRAVEL_LONGCLICK_DONT_SHOW, dontShow);
        editor.apply();
        mMainActivityShowInfo = dontShow;
    }
}
