package com.example.webdatabaseacess.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.webdatabaseacess.model.Clinic;

@Database(entities = {Clinic.class}, version = 1)
public abstract class ClinicDataBase extends RoomDatabase {

    private static final String DB_NAME = "clinic.db";
    private static volatile ClinicDataBase instance;

    public static ClinicDataBase getInstance(Context context){
        if(instance == null){
            instance = create(context);
        }
        return instance;
    }

    private static ClinicDataBase create(Context context){
        return Room.databaseBuilder(context, ClinicDataBase.class, DB_NAME).build();
    }

    public abstract ClinicDAO getDao();

}