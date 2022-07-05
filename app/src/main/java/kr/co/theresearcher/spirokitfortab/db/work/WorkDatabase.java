package kr.co.theresearcher.spirokitfortab.db.work;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Work.class}, version = 1)
public abstract class WorkDatabase extends RoomDatabase {
    public abstract WorkDao workDao();
}
