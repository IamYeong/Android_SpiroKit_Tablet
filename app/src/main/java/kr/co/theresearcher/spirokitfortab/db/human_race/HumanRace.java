package kr.co.theresearcher.spirokitfortab.db.human_race;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

public enum HumanRace {

    y("yellow"), // yellow
    w("white"), // white
    b("black"); // black

    String value;

    HumanRace(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
