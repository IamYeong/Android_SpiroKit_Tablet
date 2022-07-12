package kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CalHistoryRawDataDao {

    @Query("SELECT * FROM CAL_HISTORY_RAW_DATA WHERE (cal_history_hashed = :calHistoryHashed) AND (is_deleted is 0) AND (is_deleted_reference is 0)")
    List<CalHistoryRawData> selectRawDataByHistory(String calHistoryHashed);

    @Query("SELECT * FROM CAL_HISTORY_RAW_DATA")
    List<CalHistoryRawData> selectAll();

    @Insert
    void insertRawData(CalHistoryRawData rawData);

    @Update
    void updateRawData(CalHistoryRawData rawData);

    @Query("UPDATE CAL_HISTORY_RAW_DATA SET cal_history_hashed = :historyHashed WHERE (cal_history_hashed is null) AND (is_deleted is 0)")
    void fillHistoryHash(String historyHashed);

    @Query("DELETE FROM cal_history_raw_data WHERE cal_history_hashed = null")
    void deleteNotCompleteData();

    @Query("UPDATE cal_history_raw_data SET is_deleted = 1 WHERE hashed = :hash")
    void delete(String hash);

    @Query("UPDATE cal_history_raw_data SET is_deleted_reference = 1 WHERE cal_history_hashed = :historyHashed")
    void deleteReference(String historyHashed);

}
