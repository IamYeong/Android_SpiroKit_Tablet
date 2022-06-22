package kr.co.theresearcher.spirokitfortab.db.operator;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OperatorDao {

    @Query("SELECT * FROM OPERATOR WHERE OPERATOR_OFFICE_ID = :officeID")
    List<Operator> selectByOfficeID(int officeID);

    @Query("SELECT * FROM OPERATOR WHERE OPERATOR_WORK_ID = :workID")
    List<Operator> selectByWorkID(int workID);

    @Insert
    void insertOperator(Operator operator);

    @Delete
    void deleteOperator(Operator operator);

    @Update
    void updateOperator(Operator operator);

}
