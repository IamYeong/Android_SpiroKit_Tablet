package kr.co.theresearcher.spirokitfortab.db.cal_history;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import kr.co.theresearcher.spirokitfortab.db.office.Office;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;

@Entity(tableName = "cal_history", foreignKeys = {
        @ForeignKey(
                entity = Office.class,
                parentColumns = "id",
                childColumns = "office_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Operator.class,
                parentColumns = "id",
                childColumns = "operator_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Patient.class,
                parentColumns = "id",
                childColumns = "patient_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
        },
        indices = {@Index(value = "hashed", unique = true)})
public class CalHistory{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "hashed")
    private String hashed;

    @ColumnInfo(name = "office_hashed")
    private String officeHashed;

    @ColumnInfo(name = "operator_hashed")
    private String operatorHashed;

    @ColumnInfo(name = "patient_hashed")
    private String patientHashed;

    @ColumnInfo(name = "cal_finished_date")
    private String finishDate;

    @ColumnInfo(name = "cal_div")
    private String measDiv;

    @ColumnInfo(name = "device_div")
    private String deviceDiv;

    @ColumnInfo(name = "c_date")
    private long createDate;

    @ColumnInfo(name = "is_deleted")
    private int isDeleted;

    public CalHistory(String hashed, String officeHashed, String operatorHashed, String patientHashed, String finishDate, String measDiv, String deviceDiv, int isDeleted) {
        this.hashed = hashed;
        this.officeHashed = officeHashed;
        this.operatorHashed = operatorHashed;
        this.patientHashed = patientHashed;
        this.finishDate = finishDate;
        this.measDiv = measDiv;
        this.deviceDiv = deviceDiv;
        this.isDeleted = isDeleted;
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

    public String getOfficeHashed() {
        return officeHashed;
    }

    public void setOfficeHashed(String officeHashed) {
        this.officeHashed = officeHashed;
    }

    public String getOperatorHashed() {
        return operatorHashed;
    }

    public void setOperatorHashed(String operatorHashed) {
        this.operatorHashed = operatorHashed;
    }

    public String getPatientHashed() {
        return patientHashed;
    }

    public void setPatientHashed(String patientHashed) {
        this.patientHashed = patientHashed;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getMeasDiv() {
        return measDiv;
    }

    public void setMeasDiv(String measDiv) {
        this.measDiv = measDiv;
    }

    public String getDeviceDiv() {
        return deviceDiv;
    }

    public void setDeviceDiv(String deviceDiv) {
        this.deviceDiv = deviceDiv;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}
