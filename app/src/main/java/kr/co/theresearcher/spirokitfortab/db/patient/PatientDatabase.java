package kr.co.theresearcher.spirokitfortab.db.patient;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Patient.class}, version = 1)
public abstract class PatientDatabase extends RoomDatabase {
    public abstract PatientDao patientDao();
}
