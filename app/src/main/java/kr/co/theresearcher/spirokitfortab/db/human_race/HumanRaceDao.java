package kr.co.theresearcher.spirokitfortab.db.human_race;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HumanRaceDao {

    @Query("SELECT * FROM human_race")
    List<HumanRace> selectAllHumanRace();

    @Insert
    void insert(HumanRace race);

}
