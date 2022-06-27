package kr.co.theresearcher.spirokitfortab.db.patient;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Patient {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "PATIENT_ID")
    private int id;

    @ColumnInfo(name = "PATIENT_CHART_NUM")
    private String chartNumber;

    @ColumnInfo(name = "PATIENT_NAME")
    private String name;

    @ColumnInfo(name = "PATIENT_GENDER")
    private boolean gender;

    @ColumnInfo(name = "PATIENT_WEIGHT")
    private int weight;

    @ColumnInfo(name = "PATIENT_HEIGHT")
    private int height;

    @ColumnInfo(name = "PATIENT_HUMAN_RACE_ID")
    private int humanRaceId;

    @ColumnInfo(name = "PATIENT_BIRTH_DATE")
    private long birthDate;

    @ColumnInfo(name = "PATIENT_START_SMOKE_DATE")
    private long startSmokeDate;

    @ColumnInfo(name = "PATIENT_STOP_SMOKE_DATE")
    private long stopSmokeDate;

    @ColumnInfo(name = "PATIENT_IS_SMOKE")
    private boolean isSmoke;

    @ColumnInfo(name = "PATIENT_SMOKE_AMOUNT_PER_PACK")
    private float smokeAmountPack;

    @ColumnInfo(name = "PATIENT_DOCTOR_NAME")
    private String doctorName;

    @ColumnInfo(name = "PATIENT_OFFICE_ID")
    private int officeID;

    //오피스 ID 빠져있음

    public Patient(int officeID, String chartNumber, String name, boolean gender, int weight, int height, int humanRaceId, long birthDate, long startSmokeDate, long stopSmokeDate, boolean isSmoke, float smokeAmountPack, String doctorName) {
        this.officeID = officeID;
        this.chartNumber = chartNumber;
        this.name = name;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.humanRaceId = humanRaceId;
        this.birthDate = birthDate;
        this.startSmokeDate = startSmokeDate;
        this.stopSmokeDate = stopSmokeDate;
        this.isSmoke = isSmoke;
        this.smokeAmountPack = smokeAmountPack;
        this.doctorName = doctorName;
    }

    public Patient() {

    }

    public int getOfficeID() {
        return officeID;
    }

    public void setOfficeID(int officeID) {
        this.officeID = officeID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChartNumber() {
        return chartNumber;
    }

    public void setChartNumber(String chartNumber) {
        this.chartNumber = chartNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHumanRaceId() {
        return humanRaceId;
    }

    public void setHumanRaceId(int humanRaceId) {
        this.humanRaceId = humanRaceId;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    public long getStartSmokeDate() {
        return startSmokeDate;
    }

    public void setStartSmokeDate(long startSmokeDate) {
        this.startSmokeDate = startSmokeDate;
    }

    public long getStopSmokeDate() {
        return stopSmokeDate;
    }

    public void setStopSmokeDate(long stopSmokeDate) {
        this.stopSmokeDate = stopSmokeDate;
    }

    public boolean isSmoke() {
        return isSmoke;
    }

    public void setSmoke(boolean smoke) {
        isSmoke = smoke;
    }

    public float getSmokeAmountPack() {
        return smokeAmountPack;
    }

    public void setSmokeAmountPack(float smokeAmountPack) {
        this.smokeAmountPack = smokeAmountPack;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
