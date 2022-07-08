package kr.co.theresearcher.spirokitfortab.db.office;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "office",
        indices = {
        @Index(value = {"hashed"}, unique = true)})
public class Office {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "hashed")
    private String hashed;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "tel")
    private String tel;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "country_code")
    private String countryCode;

    @ColumnInfo(name = "is_use")
    private int isUse;

    @ColumnInfo(name = "is_use_sync")
    private int isUseSync;

    @ColumnInfo(name = "office_id")
    private String officeID;

    @ColumnInfo(name = "office_pass")
    private String officePassword;

    @ColumnInfo(name = "is_deleted")
    private int isDeleted;

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getIsUse() {
        return isUse;
    }

    public void setIsUse(int isUse) {
        this.isUse = isUse;
    }

    public int getIsUseSync() {
        return isUseSync;
    }

    public void setIsUseSync(int isUseSync) {
        this.isUseSync = isUseSync;
    }

    public String getOfficeID() {
        return officeID;
    }

    public void setOfficeID(String officeID) {
        this.officeID = officeID;
    }

    public String getOfficePassword() {
        return officePassword;
    }

    public void setOfficePassword(String officePassword) {
        this.officePassword = officePassword;
    }
}
