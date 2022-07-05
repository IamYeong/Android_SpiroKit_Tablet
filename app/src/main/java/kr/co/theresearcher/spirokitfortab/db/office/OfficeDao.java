package kr.co.theresearcher.spirokitfortab.db.office;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface OfficeDao {

    @Query("SELECT * FROM OFFICE WHERE office_id = :id")
    Office selectOfficeByID(String id);

}
