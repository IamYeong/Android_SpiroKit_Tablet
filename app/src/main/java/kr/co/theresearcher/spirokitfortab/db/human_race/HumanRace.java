package kr.co.theresearcher.spirokitfortab.db.human_race;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HumanRace {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "HUMAN_RACE_ID")
    private int id;

    @ColumnInfo(name = "HUMAN_RACE_NAME")
    private String name;

    public HumanRace(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
