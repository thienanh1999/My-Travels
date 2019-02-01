package com.vtca.mytravels.repository;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vtca.mytravels.base.MyConst;
import com.vtca.mytravels.base.TravelSort;
import com.vtca.mytravels.dao.TravelDao;
import com.vtca.mytravels.database.AppDatabase;
import com.vtca.mytravels.entity.Travel;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.LiveData;

public class TravelRepository {
    private static final String TAG = TravelRepository.class.getSimpleName();
    private static volatile TravelRepository INSTANCE;
    private final Application mApplication;
    private final TravelDao mTravelDao;



    private TravelRepository(Application application) {
        mApplication = application;
        AppDatabase db = AppDatabase.getDatabase(application);
        mTravelDao = db.travelDao();
    }

    public static TravelRepository getInstance(final Application application) {
        if (INSTANCE == null) {
            synchronized (TravelRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TravelRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<Travel>> getAllTravels() {
        return mTravelDao.getAllTravels();
    }

    public List<Travel> getAllTravelsUpComingWithoutLiveData(long currentTime, long range) {
        return mTravelDao.getAllTravelsUpComingWithoutLiveData(currentTime, range);
    }

    public LiveData<List<Travel>> getAllTravels(TravelSort travelSort) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mApplication.getBaseContext());
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long savedRange = sharedPreferences.getLong(MyConst.UP_COMING_DAY, 1);
        long range = savedRange*24*60*60*1000;
        Log.d(TAG, "Test shared preferences: " + sharedPreferences.getLong(MyConst.UP_COMING_DAY, 1));
        switch (travelSort) {
            case DEFAULT:
                return mTravelDao.getAllTravels();
            case TITLE_ASC:
                return mTravelDao.getAllTravelsByTitleAsc();
            case TITLE_DESC:
                return mTravelDao.getAllTravelsByTitleDesc();
            case START_ASC:
                return mTravelDao.getAllTravelsByStartAsc();
            case START_DESC:
                return mTravelDao.getAllTravelsByStartDesc();
            case UP_COMING:
                return mTravelDao.getAllTravelsUpComing(currentTime, range);
        }
        return mTravelDao.getAllTravels();
    }

    public void insert(Travel travel) {
        new insertAsyncTask(mTravelDao).execute(travel);
    }

    public void update(Travel travel) {
        new updateAsyncTask(mTravelDao).execute(travel);
    }

    public void delete(Travel... travels) {
        new deleteAsyncTask(mTravelDao).execute(travels);
    }

    private static class insertAsyncTask extends AsyncTask<Travel, Void, Void> {
        private TravelDao mAsyncTaskDao;

        insertAsyncTask(TravelDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Travel... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Travel, Void, Void> {
        private TravelDao mAsyncTaskDao;

        updateAsyncTask(TravelDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Travel... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Travel, Void, Void> {
        private TravelDao mAsyncTaskDao;

        deleteAsyncTask(TravelDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Travel... params) {
            for (Travel travel : params) {
                TravelRepository.INSTANCE.deleteTravelDirectory(travel.getId());
            }
            mAsyncTaskDao.delete(params);
            return null;
        }
    }

    /**
     * Delete the image directory of a selected travel
     *
     * @param travelId
     */
    private void deleteTravelDirectory(long travelId) {
        try {
            final File rootDir = new File(mApplication.getFilesDir(), "t" + travelId);
            if (!rootDir.exists()) return;
            // Get all files of the directory to be deleted
            File[] files = rootDir.listFiles();
            if (files != null) {
                // delete all files
                for (File file : files) file.delete();
            }
            // delete a directory
            rootDir.delete();
        } catch (Exception e) {
            Log.e("TravelRepository", e.getMessage(), e);
        }
    }
}
