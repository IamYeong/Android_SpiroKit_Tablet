package kr.co.theresearcher.spirokitfortab.db.work;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkDao {

    @Query("SELECT * FROM work_group")
    List<Work> selectAllWork();

}
