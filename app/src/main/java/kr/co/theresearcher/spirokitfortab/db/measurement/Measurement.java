package kr.co.theresearcher.spirokitfortab.db.measurement;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Measurement {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "MEASUREMENT_ID")
    private int id;

    @ColumnInfo(name = "MEASUREMENT_PID")
    private int patientID;

    @ColumnInfo(name = "MEASUREMENT_GROUP_ID")
    private int measurementID;

    @ColumnInfo(name = "MEASUREMENT_DATE")
    private long measDate;

    @ColumnInfo(name = "MEASUREMENT_PATH_NAME")
    private String path;

    @ColumnInfo(name = "MEASUREMENT_OFFICE_ID")
    private int officeID;

    @ColumnInfo(name = "MEASUREMENT_OPERATOR_ID")
    private int measOperatorID;

    @Ignore
    private boolean isSelected;

    public Measurement(int officeID, int patientID, int measurementID, long measDate, String path, int measOperatorID) {
        this.officeID = officeID;
        this.patientID = patientID;
        this.measurementID = measurementID;
        this.measDate = measDate;
        this.path = path;
        this.measOperatorID = measOperatorID;
    }

    public int getMeasOperatorID() {
        return measOperatorID;
    }

    public void setMeasOperatorID(int measOperatorID) {
        this.measOperatorID = measOperatorID;
    }

    public int getOfficeID() {
        return officeID;
    }

    public void setOfficeID(int officeID) {
        this.officeID = officeID;
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

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public int getMeasurementID() {
        return measurementID;
    }

    public void setMeasurementID(int measurementID) {
        this.measurementID = measurementID;
    }

    public long getMeasDate() {
        return measDate;
    }

    public void setMeasDate(long measDate) {
        this.measDate = measDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
