package kr.co.theresearcher.spirokitfortab.db.cal_history;

import androidx.room.Database;

@Database(entities = {CalHistory.class}, version = 1)
public abstract class CalHistoryDatabase {
    public abstract CalHistoryDao calHistoryDao();
}
