package kr.co.theresearcher.spirokitfortab.db.operator;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OperatorDao {

    @Query("SELECT * FROM OPERATOR WHERE office_hashed = :officeHashed")
    List<Operator> selectAllOperator(String officeHashed);

    @Query("SELECT * FROM OPERATOR WHERE work = :work AND office_hashed = :officeHashed")
    List<Operator> selectOperatorByWork(String officeHashed, String work);

}
