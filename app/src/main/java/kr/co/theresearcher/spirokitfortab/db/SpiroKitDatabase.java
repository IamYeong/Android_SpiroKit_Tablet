package kr.co.theresearcher.spirokitfortab.db;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

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

    private static Migration migration_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("INSERT INTO office (hashed, name, code, tel, address, country_code, is_use, is_use_sync, office_id, office_pass, is_deleted, updated_date) VALUES('e3abb70e6c9b6de197661aa26113486d7d164c019ea785e41d4cb9c9237d23da','thien nhan','XohRKst7','0000000', 'Vietnam', 'VN', 1, 1, 'thien nhan', '1234', 0, '2022-08-24 00:00:00')");
            database.execSQL("INSERT INTO office (hashed, name, code, tel, address, country_code, is_use, is_use_sync, office_id, office_pass, is_deleted, updated_date) VALUES('a2c9f1ad7b97c7464a0df6788a066576650c369ea7ef71c8ffa0b56b973c71e3', 'hoavang', 'nIf8jV3e', '0000000', 'Vietnam', 'VN', 1, 1, 'hoavang', '1234',0, '2022-08-24 00:00:00')");

        }
    };

    public synchronized static SpiroKitDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(context, SpiroKitDatabase.class, "theresearcher_spirokit.db")
                    .allowMainThreadQueries()
                    .addMigrations(migration_1_2)
                    .addCallback(new Callback() {

                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);

                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {

                                    //try {

                                        db.execSQL("INSERT INTO office (hashed, name, code, tel, address, country_code, is_use, is_use_sync, office_id, office_pass, is_deleted, updated_date) VALUES('1f10668b5f1cb897d57faf08cfe58c668060f14ce32077c43011c862fea5f5c7', 'TR1', '0rri5IHz','12121212', '대한민국', 'KR', 1, 1, 'tr1', '1234', 0,'0000-00-00 00:00:00')");
                                        db.execSQL("INSERT INTO office (hashed, name, code, tel, address, country_code, is_use, is_use_sync, office_id, office_pass, is_deleted, updated_date) VALUES('e3abb70e6c9b6de197661aa26113486d7d164c019ea785e41d4cb9c9237d23da','thien nhan','XohRKst7','0000000', 'Vietnam', 'VN', 1, 1, 'thien nhan', '1234', 0, '2022-08-24 00:00:00')");
                                        db.execSQL("INSERT INTO office (hashed, name, code, tel, address, country_code, is_use, is_use_sync, office_id, office_pass, is_deleted, updated_date) VALUES('a2c9f1ad7b97c7464a0df6788a066576650c369ea7ef71c8ffa0b56b973c71e3', 'hoavang', 'nIf8jV3e', '0000000', 'Vietnam', 'VN', 1, 1, 'hoavang', '1234',0, '2022-08-24 00:00:00')");

                                        db.execSQL("INSERT INTO work_group (work) VALUES('doctor')");
                                        db.execSQL("INSERT INTO work_group (work) VALUES('nurse')");
                                        db.execSQL("INSERT INTO work_group (work) VALUES('pathologist')");

                                        db.execSQL("INSERT INTO human_race (race) VALUES('y')");
                                        db.execSQL("INSERT INTO human_race (race) VALUES('w')");
                                        db.execSQL("INSERT INTO human_race (race) VALUES('b')");

                                        //db.close();

                                    //}


                                }
                            });

                        }
                    })


                    .build();

        }

        return instance;

    }

    public static void removeInstance() {

        instance = null;

    }

}
