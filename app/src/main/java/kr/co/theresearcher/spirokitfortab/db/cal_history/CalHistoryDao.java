package kr.co.theresearcher.spirokitfortab.db.cal_history;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CalHistoryDao {

    @Query("SELECT * FROM CAL_HISTORY WHERE patient_hashed = :patientHashed")
    List<CalHistory> selectHistoryByPatient(String patientHashed);

    @Insert
    void insertHistory(CalHistory history);

    @Delete
    void deleteHistory(CalHistory history);

    @Update
    void update(CalHistory history);

}
