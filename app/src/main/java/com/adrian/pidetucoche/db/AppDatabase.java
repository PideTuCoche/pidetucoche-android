package com.adrian.pidetucoche.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Usuario.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDao appDao();

    private static AppDatabase appDatabase;

    public static AppDatabase getInstance(Context context){
        if (appDatabase == null) appDatabase = Room
                                        .databaseBuilder(context, AppDatabase.class, "pidetucoche.db")
                                        .fallbackToDestructiveMigration()
                                        .addCallback(new Callback() {
                                            @Override
                                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                                super.onCreate(db);
                                                crearAdminUsuario();
                                            }
                                        })
                                        .build();
        return appDatabase;
    }

    private static void crearAdminUsuario() {
        AsyncTask.execute(() -> appDatabase.appDao().InsertarUsuario(new Usuario("admin@example.com", "admin")));
    }
}
