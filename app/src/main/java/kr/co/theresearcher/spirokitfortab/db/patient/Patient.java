package kr.co.theresearcher.spirokitfortab.db.patient;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import kr.co.theresearcher.spirokitfortab.db.office.Office;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;

@Entity(tableName = "patient", foreignKeys = {
        @ForeignKey(
                entity = Office.class,
                parentColumns = "hashed",
                childColumns = "office_hashed",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )},
        indices = {@Index(value = "hashed", unique = true)
}
)
public class Patient {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "hashed")
    private String hashed;

    @ColumnInfo(name = "office_hashed")
    private String officeHashed;

    @ColumnInfo(name = "chart_no")
    private String chartNumber;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "height")
    private int height;

    @ColumnInfo(name = "weight")
    private int weight;

    @ColumnInfo(name = "birthday")
    private String birthDay;

    @ColumnInfo(name = "human_race")
    private String humanRace;

    @ColumnInfo(name = "smoking_period")
    private int smokingPeriod;

    @ColumnInfo(name = "smoking_is_now")
    private int nowSmoking;

    @ColumnInfo(name = "smoking_no_when")
    private String stopSmokingDay;

    @ColumnInfo(name = "smoking_start_day")
    private String startSmokingDay;

    @ColumnInfo(name = "smoking_amount_per_day")
    private String smokingAmountPerDay;

    @ColumnInfo(name = "os")
    private String os;

    @ColumnInfo(name = "latest_day")
    private String latestDay;

    @ColumnInfo(name = "c_date")
    private long createDate;

    @ColumnInfo(name = "is_deleted")
    private int isDeleted;

    public Patient(String hashed, String officeHashed, String chartNumber, String name, String gender, int height, int weight, String birthDay, String humanRace, int nowSmoking, String stopSmokingDay, String startSmokingDay, String smokingAmountPerDay) {
        this.hashed = hashed;
        this.officeHashed = officeHashed;
        this.chartNumber = chartNumber;
        this.name = name;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.birthDay = birthDay;
        this.humanRace = humanRace;
        this.nowSmoking = nowSmoking;
        this.stopSmokingDay = stopSmokingDay;
        this.startSmokingDay = startSmokingDay;
        this.smokingAmountPerDay = smokingAmountPerDay;
        os = "a";
        isDeleted = 0;
        createDate = 0;
        smokingPeriod = 0;
        latestDay = null;

    }

    private Patient(Patient.Builder builder) {

        hashed = builder.hashed;
        officeHashed = builder.officeHashed;
        chartNumber = builder.chartNumber;
        name = builder.name;
        gender = builder.gender;
        height = builder.height;
        weight = builder.weight;
        birthDay = builder.birthDay;
        humanRace = builder.humanRace;
        smokingAmountPerDay = builder.smokingAmountPerDay;
        smokingPeriod = builder.smokingPeriod;
        nowSmoking = builder.nowSmoking;
        startSmokingDay = builder.startSmokingDay;
        stopSmokingDay = builder.stopSmokingDay;
        latestDay = builder.latestDay;
        os = builder.os;
        createDate = builder.createDate;
        isDeleted = builder.isDeleted;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOfficeHashed() {
        return officeHashed;
    }

    public void setOfficeHashed(String officeHashed) {
        this.officeHashed = officeHashed;
    }

    public String getHashed() {
        return hashed;
    }

    public void setHashed(String hashed) {
        this.hashed = hashed;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getHumanRace() {
        return humanRace;
    }

    public void setHumanRace(String humanRace) {
        this.humanRace = humanRace;
    }

    public int getSmokingPeriod() {
        return smokingPeriod;
    }

    public void setSmokingPeriod(int smokingPeriod) {
        this.smokingPeriod = smokingPeriod;
    }

    public int getNowSmoking() {
        return nowSmoking;
    }

    public void setNowSmoking(int nowSmoking) {
        this.nowSmoking = nowSmoking;
    }

    public String getStopSmokingDay() {
        return stopSmokingDay;
    }

    public void setStopSmokingDay(String stopSmokingDay) {
        this.stopSmokingDay = stopSmokingDay;
    }

    public String getStartSmokingDay() {
        return startSmokingDay;
    }

    public void setStartSmokingDay(String startSmokingDay) {
        this.startSmokingDay = startSmokingDay;
    }

    public String getSmokingAmountPerDay() {
        return smokingAmountPerDay;
    }

    public void setSmokingAmountPerDay(String smokingAmountPerDay) {
        this.smokingAmountPerDay = smokingAmountPerDay;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getLatestDay() {
        return latestDay;
    }

    public void setLatestDay(String latestDay) {
        this.latestDay = latestDay;
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

    public static class Builder {

        private String os = "a";
        private String hashed;
        private String name;
        private String officeHashed;
        private String operatorWorkHashed;
        private String operatorDoctorHashed;
        private String chartNumber;
        private String gender;
        private int height;
        private int weight;
        private String birthDay;
        private String humanRace;
        private String smokingAmountPerDay;
        private int nowSmoking;

        //Deprecated
        private int smokingPeriod = 0;
        private String startSmokingDay;
        private String stopSmokingDay;

        //Deprecated
        private String latestDay = "";
        private long createDate = 0;
        private int isDeleted = 0;

        public Builder hashed(String hash) {
            hashed = hash;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder officeHashed(String officeHash) {
            officeHashed = officeHash;
            return this;
        }

        public Builder operatorWorkHashed(String operatorWorkHash) {
            operatorWorkHashed = operatorWorkHash;
            return this;
        }

        public Builder operatorDoctorHashed(String operatorDoctorHash) {
            operatorDoctorHashed = operatorDoctorHash;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder chartNumber(String chartNumber) {
            this.chartNumber = chartNumber;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder birthDay(String birthDay) {
            this.birthDay = birthDay;
            return this;
        }

        public Builder humanRace(String humanRace) {
            this.humanRace = humanRace;
            return this;
        }

        public Builder smokingAmountDay(String smokingAmountPerDay) {
            this.smokingAmountPerDay = smokingAmountPerDay;
            return this;
        }

        public Builder nowSmoking(int nowSmoking) {
            this.nowSmoking = nowSmoking;
            return this;
        }

        public Patient build() {
            return new Patient(this);
        }


    }

}
