package kr.co.theresearcher.spirokitfortab.db.cal_history;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CalHistoryDao {

    @Query("SELECT * FROM CAL_HISTORY " +
            "WHERE (patient_hashed = :patientHashed) " +
            "AND (is_deleted is 0) " +
            "AND (is_deleted_reference is 0)" +
            "AND ((SELECT COUNT(id) FROM CAL_HISTORY_RAW_DATA " +
            "WHERE (CAL_HISTORY.hashed = cal_history_hashed) " +
            "AND (is_deleted is 0)) " +
            "is not 0)")
    List<CalHistory> selectHistoryByPatient(String patientHashed);

    @Insert
    void insertHistory(CalHistory history);

    @Delete
    void deleteHistory(CalHistory history);

    @Update
    void update(CalHistory history);

    @Query("UPDATE cal_history SET is_deleted = 1 WHERE hashed = :hash")
    void delete(String hash);

    @Query("UPDATE cal_history SET is_deleted_reference = 1 WHERE patient_hashed = :patientHash")
    void deleteReference(String patientHash);

}
