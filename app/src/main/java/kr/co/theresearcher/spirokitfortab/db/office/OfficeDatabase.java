package kr.co.theresearcher.spirokitfortab.db.office;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Office.class}, version = 1)
public abstract class OfficeDatabase extends RoomDatabase {
    public abstract OfficeDao officeDao();
}
