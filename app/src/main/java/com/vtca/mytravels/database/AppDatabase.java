package com.vtca.mytravels.database;

import android.content.Context;

import com.vtca.mytravels.dao.TravelDao;
import com.vtca.mytravels.dao.TravelDiaryDao;
import com.vtca.mytravels.dao.TravelExpenseDao;
import com.vtca.mytravels.dao.TravelPlanDao;
import com.vtca.mytravels.entity.Travel;
import com.vtca.mytravels.entity.TravelDiary;
import com.vtca.mytravels.entity.TravelExpense;
import com.vtca.mytravels.entity.TravelPlan;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Travel.class, TravelPlan.class, TravelDiary.class, TravelExpense.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "travel_db")
                            .fallbackToDestructiveMigration()
                            /*.addMigrations(MIGRATION_1_2)*/
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract TravelDao travelDao();

    public abstract TravelPlanDao travelPlanDao();

    public abstract TravelDiaryDao travelDiaryDao();

    public abstract TravelExpenseDao travelExpenseDao();
}
