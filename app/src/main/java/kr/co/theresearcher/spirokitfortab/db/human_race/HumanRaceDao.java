package kr.co.theresearcher.spirokitfortab.db.human_race;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HumanRaceDao {

    @Query("SELECT * FROM HUMANRACE")
    List<HumanRace> selectAllHumanRace();

}
