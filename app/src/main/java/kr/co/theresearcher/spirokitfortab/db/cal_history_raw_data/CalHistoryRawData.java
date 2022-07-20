package kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;

@Entity(tableName = "cal_history_raw_data",  foreignKeys = {
        @ForeignKey(
                entity = CalHistory.class,
                parentColumns = "hashed",
                childColumns = "cal_history_hashed",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
            )
        },
        indices = {@Index(value = "hashed", unique = true)}
)
public class CalHistoryRawData {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "hashed")
    private String hashed;

    @ColumnInfo(name = "cal_history_hashed")
    private String calHistoryHashed;

    @ColumnInfo(name = "order_num")
    private String orderNumber;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "cal_date")
    private String calDate;

    @ColumnInfo(name = "is_post")
    private int isPost;

    @ColumnInfo(name = "c_date")
    private String createDate;

    @ColumnInfo(name = "updated_date")
    private String updatedDate;

    @ColumnInfo(name = "is_deleted")
    private int isDeleted;

    @ColumnInfo(name = "is_deleted_reference")
    private int isDeletedReference;

    public int getIsDeletedReference() {
        return isDeletedReference;
    }

    public void setIsDeletedReference(int isDeletedReference) {
        this.isDeletedReference = isDeletedReference;
    }

    @Ignore
    private boolean isSelected;

    public CalHistoryRawData(String hashed, String calHistoryHashed, String orderNumber, String data, String calDate, int isPost) {
        this.hashed = hashed;
        this.calHistoryHashed = calHistoryHashed;
        this.orderNumber = orderNumber;
        this.data = data;
        this.calDate = calDate;
        this.isPost = isPost;
        isDeleted = 0;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

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

    public String getCalHistoryHashed() {
        return calHistoryHashed;
    }

    public void setCalHistoryHashed(String calHistoryHashed) {
        this.calHistoryHashed = calHistoryHashed;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCalDate() {
        return calDate;
    }

    public void setCalDate(String calDate) {
        this.calDate = calDate;
    }

    public int getIsPost() {
        return isPost;
    }

    public void setIsPost(int isPost) {
        this.isPost = isPost;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
