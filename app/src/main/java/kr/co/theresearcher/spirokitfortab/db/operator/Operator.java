package kr.co.theresearcher.spirokitfortab.db.operator;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.RewriteQueriesToDropUnusedColumns;

import kr.co.theresearcher.spirokitfortab.db.office.Office;

@Entity(tableName = "operator", foreignKeys = {
        @ForeignKey(
                entity = Office.class,
                parentColumns = "hashed",
                childColumns = "office_hashed",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )},
        indices = {@Index(value = "hashed", unique = true)})
public class Operator {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "hashed")
    private String hashed;

    @ColumnInfo(name = "office_hashed")
    private String officeHashed;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "work")
    private String work;

    @ColumnInfo(name = "c_date")
    private long createTimestamp;

    @ColumnInfo(name = "is_deleted")
    private int isDeleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHashed() {
        return hashed;
    }

    public void setHashed(String hashed) {
        this.hashed = hashed;
    }

    public String getOfficeHashed() {
        return officeHashed;
    }

    public void setOfficeHashed(String officeHashed) {
        this.officeHashed = officeHashed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
