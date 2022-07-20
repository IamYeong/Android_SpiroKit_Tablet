package kr.co.theresearcher.spirokitfortab.db.patient;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PatientDao {

    @Query("SELECT * FROM PATIENT WHERE (office_hashed == :officeHashed)")
    List<Patient> selectAll(String officeHashed);

    @Query("SELECT * FROM PATIENT")
    List<Patient> selectAllPatient();

    @Query("SELECT * FROM PATIENT WHERE (office_hashed == :officeHash) AND (is_deleted == 0)")
    List<Patient> selectPatientByOffice(String officeHash);

    @Query("SELECT * FROM PATIENT WHERE hashed == :hash")
    Patient selectPatientByHash(String hash);

    @Insert
    void insertPatient(Patient patient);

    @Update
    void updatePatient(Patient patient);

    @Query("UPDATE patient SET is_deleted = 1, updated_date = :updateDate WHERE hashed == :hash")
    void deletePatient(String hash, String updateDate);

    @Query("SELECT EXISTS(SELECT * FROM PATIENT WHERE hashed == :hash)")
    boolean isExists(String hash);

}
