package kr.co.theresearcher.spirokitfortab.db.office;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OfficeDao {

    @Query("SELECT * FROM OFFICE WHERE office_id == :id")
    Office selectOfficeByID(String id);

    @Query("SELECT * FROM office")
    List<Office> selectAllOffices();

    @Query("UPDATE office SET is_deleted = 1")
    void deleteOffice();

}
