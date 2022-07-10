package kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CalHistoryRawDataDao {

    @Query("SELECT * FROM CAL_HISTORY_RAW_DATA WHERE cal_history_hashed = :calHistoryHashed")
    List<CalHistoryRawData> selectRawDataByHistory(String calHistoryHashed);

    @Insert
    void insertRawData(CalHistoryRawData rawData);

    @Update
    void updateRawData(CalHistoryRawData rawData);

    @Query("UPDATE CAL_HISTORY_RAW_DATA SET cal_history_hashed = :historyHashed WHERE cal_history_hashed = null")
    void fillHistoryHash(String historyHashed);

    @Query("DELETE FROM cal_history_raw_data WHERE cal_history_hashed = null")
    void deleteNotCompleteData();

}
