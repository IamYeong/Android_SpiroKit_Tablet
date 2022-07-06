package kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data;

import androidx.room.Database;

@Database(entities = {CalHistoryRawData.class}, version = 1)
public abstract class CalHistoryRawDataDatabase {
    public abstract CalHistoryRawDataDao calHistoryRawDataDao();
}
