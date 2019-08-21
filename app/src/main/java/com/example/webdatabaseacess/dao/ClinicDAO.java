package com.example.webdatabaseacess.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.webdatabaseacess.model.Clinic;

import java.util.List;

@Dao
public interface ClinicDAO {

    @Query("SELECT * FROM clinic")
    List<Clinic> getAllClinics();

    @Query("DELETE FROM clinic")
    void clearDB ();

    @Insert
    void insert (Clinic... clinic);

    @Delete
    void delete (Clinic... clinic);

    @Update
    void update (Clinic... clinic);

}