package kr.co.theresearcher.spirokitfortab.db.human_race;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {HumanRace.class}, version = 1)
public abstract class HumanRaceDatabase extends RoomDatabase {

    public abstract HumanRaceDao humanRaceDao();
}
