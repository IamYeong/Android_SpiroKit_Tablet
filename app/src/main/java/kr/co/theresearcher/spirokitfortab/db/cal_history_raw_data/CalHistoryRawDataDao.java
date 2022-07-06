package kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface CalHistoryRawDataDao {

    @Query("SELECT * FROM CAL_HISTORY_RAW_DATA WHERE cal_history_hashed = :calHistoryHashed")
    List<CalHistoryRawData> selectRawDataByHistory(String calHistoryHashed);

    @Insert
    void insertRawData(CalHistoryRawData rawData);

    @Update
    void updateRawData(CalHistoryRawData rawData);

}
