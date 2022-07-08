package kr.co.theresearcher.spirokitfortab.db.patient;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PatientDao {

    @Query("SELECT * FROM PATIENT")
    List<Patient> selectAllPatient();

    @Query("SELECT * FROM PATIENT WHERE office_hashed = :officeHash")
    List<Patient> selectPatientByOffice(String officeHash);

    @Insert
    void insertPatient(Patient patient);

    @Update
    void updatePatient(Patient patient);

    @Delete
    void deletePatient(Patient patient);

}
