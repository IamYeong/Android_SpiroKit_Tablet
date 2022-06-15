package kr.co.theresearcher.spirokitfortab.db.meas_group;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MeasGroup {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "MEAS_GROUP_ID")
    private int id;

    @ColumnInfo(name = "MEAS_NAME")
    private String name;

    public MeasGroup(String name) {
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
