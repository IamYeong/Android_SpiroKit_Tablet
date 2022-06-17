package kr.co.theresearcher.spirokitfortab.db.measurement;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MeasurementDao {

    @Query("SELECT * FROM MEASUREMENT WHERE MEASUREMENT_PID = :pid")
    List<Measurement> selectByPatientID(int pid);

    @Insert
    void insertMeasurement(Measurement measurement);

    @Delete
    void deleteMeasurement(Measurement measurement);


}
