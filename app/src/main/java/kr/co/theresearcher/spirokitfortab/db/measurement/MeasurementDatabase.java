package kr.co.theresearcher.spirokitfortab.db.measurement;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Measurement.class}, version = 1)
public abstract class MeasurementDatabase extends RoomDatabase {
    public abstract MeasurementDao measurementDao();
}
