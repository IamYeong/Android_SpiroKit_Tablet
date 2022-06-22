package kr.co.theresearcher.spirokitfortab.db.operator;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Operator {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "OPERATOR_ID")
    private int id;

    @ColumnInfo(name = "OPERATOR_NAME")
    private String name;

    @ColumnInfo(name = "OPERATOR_OFFICE_ID")
    private int officeID;

    @ColumnInfo(name = "OPERATOR_WORK_ID")
    private int workID;

    @ColumnInfo(name = "OPERATOR_CREATE_TIMESTAMP")
    private long createTimestamp;

    public Operator(String name, int officeID, int workID) {
        this.name = name;
        this.officeID = officeID;
        this.workID = workID;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
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

    public int getOfficeID() {
        return officeID;
    }

    public void setOfficeID(int officeID) {
        this.officeID = officeID;
    }

    public int getWorkID() {
        return workID;
    }

    public void setWorkID(int workID) {
        this.workID = workID;
    }
}
