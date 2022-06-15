package kr.co.theresearcher.spirokitfortab.db.job;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Job {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "JOB_ID")
    private int id;

    @ColumnInfo(name = "JOB_NAME")
    private String name;

    public Job(String name) {
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
