package kr.co.theresearcher.spirokitfortab.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistoryDao;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawDataDao;
import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRace;
import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRaceDao;
import kr.co.theresearcher.spirokitfortab.db.office.Office;
import kr.co.theresearcher.spirokitfortab.db.office.OfficeDao;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.operator.OperatorDao;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.db.patient.PatientDao;
import kr.co.theresearcher.spirokitfortab.db.work.Work;
import kr.co.theresearcher.spirokitfortab.db.work.WorkDao;

@Database(entities = {Office.class, Operator.class, Patient.class, CalHistory.class, CalHistoryRawData.class, Work.class, HumanRace.class},
        version = 1
)
public abstract class SpiroKitDatabase extends RoomDatabase {

    private static SpiroKitDatabase instance = null;

    public abstract OfficeDao officeDao();
    public abstract OperatorDao operatorDao();
    public abstract PatientDao patientDao();
    public abstract CalHistoryDao calHistoryDao();
    public abstract CalHistoryRawDataDao calHistoryRawDataDao();
    public abstract WorkDao workDao();
    public abstract HumanRaceDao humanRaceDao();

    public synchronized static SpiroKitDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context, SpiroKitDatabase.class, "theresearcher_spirokit.db")
                    .createFromAsset("database/spirokit.db")
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;

    }

    public static void removeInstance() {

        if (instance.isOpen()) instance.close();
        if (instance != null) instance = null;

    }

}
