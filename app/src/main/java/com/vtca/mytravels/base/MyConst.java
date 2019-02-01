package com.vtca.mytravels.base;

import java.util.HashMap;
import java.util.Map;

public class MyConst {
    public static final int REQCD_PLACE_AUTOCOMPLETE = 1000;
    public static final int REQCD_PLACE_PICKER = 1001;
    public static final int REQCD_TRAVEL_ADD = 2000;
    public static final int REQCD_TRAVEL_EDIT = 2001;
    public static final int REQCD_IMAGE_CAMERA = 3000;
    public static final int REQCD_IMAGE_GALLERY = 3001;
    public static final int REQCD_IMAGE_CROP = 3002;
    public static final int REQCD_ACCESS_GALLERY = 9000;
    public static final int REQCD_ACCESS_CAMERA = 9001;
    public static final int REQCD_ACCESS_FINE_LOCATION = 9002;
    public static final String REQKEY_TRAVEL = "REQKEY_TRAVEL";
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_DATE = "KEY_DATE";
    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_SUBTITLE = "KEY_SUBTITLE";
    public static final String KEY_DESC = "KEY_DESC";
    public static final String REQKEY_TRAVEL_ID = "REQKEY_TRAVEL_ID";
    public static final String REQACTION_EDIT_TRAVEL = "REQACTION_EDIT_TRAVEL";
    public static final String REQACTION_DEL_TRAVEL = "REQACTION_DEL_TRAVEL";
    public static final String THUMBNAIL_PREFIX = "thumb";
    public static final String LONG_CLICK_NOTI = "LONG_CLICK_NOTI";
    public static final String LONG_CLICK_PRE = "LONG_CLICK_PRE";
    public static final int THUMBNAIL_SIZE = 135;
    public static final String USER_SETTINGS = "USER_SETTINGS";
    public static final String NOTIFICATION_ON_OFF = "NOTIFICATION_ON_OFF";
    public static final String UP_COMING_DAY = "UP_COMING_DAY";
    public static final String FREQUENCY_NOTI = "frequency_noti";
    public static final String SKIP_TUTORIALS = "SKIP_TUTORIALS";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String LANGUAGE_SAVED = "LANGUAGE_SAVED";

    public static final Map<String, MyKeyValue> CURRENCY_CODE = new HashMap<>();
    public static final Map<String, MyKeyValue> BUDGET_CODE = new HashMap<>();

    public static MyKeyValue getCurrencyCode(String key) {
        if (CURRENCY_CODE.containsKey(key)) return CURRENCY_CODE.get(key);
        return new MyKeyValue(0, "NA", "NA");
    }

    public static MyKeyValue getBudgetCode(String key) {
        if (BUDGET_CODE.containsKey(key)) return BUDGET_CODE.get(key);
        return new MyKeyValue(0, "NA", "NA");
    }

}
