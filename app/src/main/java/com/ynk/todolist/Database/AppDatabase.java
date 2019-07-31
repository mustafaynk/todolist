package com.ynk.todolist.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.ynk.todolist.Model.TodoList;
import com.ynk.todolist.Model.TodoListItem;
import com.ynk.todolist.Model.User;

@Database(entities = {User.class, TodoList.class, TodoListItem.class}, version = 2, exportSchema = false)
@TypeConverters({com.ynk.todolist.Database.TypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract DAO getDAO();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDb(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "todolists")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}