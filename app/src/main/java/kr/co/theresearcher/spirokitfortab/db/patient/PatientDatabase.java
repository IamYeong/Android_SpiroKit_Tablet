package kr.co.theresearcher.spirokitfortab.db.patient;

import androidx.room.Database;

@Database(entities = {Patient.class}, version = 1)
public abstract class PatientDatabase {
    public abstract PatientDao patientDao();
}
