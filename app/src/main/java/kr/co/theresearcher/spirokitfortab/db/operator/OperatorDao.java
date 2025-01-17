package kr.co.theresearcher.spirokitfortab.db.operator;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OperatorDao {

    @Query("SELECT * FROM OPERATOR WHERE (office_hashed == :officeHashed)")
    List<Operator> selectAll(String officeHashed);

    @Query("SELECT * FROM OPERATOR WHERE (office_hashed == :officeHashed)")
    List<Operator> selectAllOperator(String officeHashed);

    @Query("SELECT * FROM OPERATOR WHERE (work == :work) AND (office_hashed == :officeHashed) AND (is_deleted == 0)")
    List<Operator> selectOperatorByWork(String officeHashed, String work);

    @Query("SELECT * FROM operator WHERE (hashed == :hash)")
    Operator selectOperatorByHash(String hash);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOperator(Operator operator);

    @Update
    void updateOperator(Operator operator);

    @Query("UPDATE operator SET is_deleted = 1, updated_date = :updateDate WHERE hashed == :hash")
    void delete(String hash, String updateDate);

    @Query("SELECT EXISTS(SELECT * FROM operator where hashed == :hash)")
    boolean isExists(String hash);

}
