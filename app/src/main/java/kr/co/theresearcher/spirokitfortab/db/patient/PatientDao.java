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

    @Query("SELECT * FROM Patient WHERE PATIENT_ID = :id")
    Patient selectPatientById(int id);

    @Query("SELECT * FROM PATIENT WHERE PATIENT_CHART_NUM = :chartNumber")
    Patient selectPatientByChartNumber(String chartNumber);

    @Insert
    void insertPatient(Patient patient);

    @Delete
    void deletePatient(Patient patient);

    @Update
    void updatePatient(Patient patient);

}
