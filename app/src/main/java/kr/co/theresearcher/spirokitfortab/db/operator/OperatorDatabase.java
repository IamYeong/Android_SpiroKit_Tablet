package kr.co.theresearcher.spirokitfortab.db.operator;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Operator.class}, version = 1)
public abstract class OperatorDatabase extends RoomDatabase {
    public abstract OperatorDao operatorDao();
}
