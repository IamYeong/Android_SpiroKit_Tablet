package kr.co.theresearcher.spirokitfortab.db.operator;

import androidx.room.Database;

@Database(entities = {Operator.class}, version = 1)
public abstract class OperatorDatabase {
    public abstract OperatorDao operatorDao();
}
