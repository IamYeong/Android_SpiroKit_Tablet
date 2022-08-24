
package kr.co.theresearcher.spirokitfortab.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;

import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRace;
import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRaceDao;

import kr.co.theresearcher.spirokitfortab.db.office.Office;
import kr.co.theresearcher.spirokitfortab.db.office.OfficeDao;

import kr.co.theresearcher.spirokitfortab.db.work.Work;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.join.ConditionAgreeActivity;
import kr.co.theresearcher.spirokitfortab.join.JoinUserActivity;
import kr.co.theresearcher.spirokitfortab.main.MainActivity;
import kr.co.theresearcher.spirokitfortab.volley.ErrorResponse;
import kr.co.theresearcher.spirokitfortab.volley.SpiroKitVolley;
import kr.co.theresearcher.spirokitfortab.volley.VolleyResponseListener;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, moveToJoinButton;
    private EditText identifierField, passwordField;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        identifierField = findViewById(R.id.et_id_login);
        passwordField = findViewById(R.id.et_password_login);
        loginButton = findViewById(R.id.btn_user_login);
        //moveToJoinButton = findViewById(R.id.btn_to_register_from_login);

        String savedID = SharedPreferencesManager.getOfficeID(LoginActivity.this);
        String savedPass = SharedPreferencesManager.getOfficePass(LoginActivity.this);

        if (savedID == null) identifierField.setText("");
        else identifierField.setText("");

        if (savedPass == null) passwordField.setText("");
        else passwordField.setText("");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        String id = "";
                        String password = "";
                        id += identifierField.getText().toString();
                        password += passwordField.getText().toString();

                        //Instant instant = Instant.now().truncatedTo(ChronoUnit.MICROS);
                        SpiroKitDatabase database = SpiroKitDatabase.getInstance(getApplicationContext());

                        List<Office> offices = database.officeDao().selectAllOffices();
                        for (Office o : offices) {
                            Log.e(getClass().getSimpleName(), o.getName() + ", " + o.getOfficeID() + ", " + o.getOfficePassword() + ", " + o.getHashed() + "\n");
                        }

                        Office office = database.officeDao().selectOfficeByID(id);
                        //SpiroKitDatabase.removeInstance();

                        if (office == null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    ConfirmDialog confirmDialog = new ConfirmDialog(LoginActivity.this);
                                    confirmDialog.setTitle(getString(R.string.request_confirm_id_or_password));
                                    confirmDialog.show();

                                }
                            });

                            return;
                        }

                        if (!office.getOfficePassword().equals(password)) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    ConfirmDialog confirmDialog = new ConfirmDialog(LoginActivity.this);
                                    confirmDialog.setTitle(getString(R.string.request_confirm_id_or_password));
                                    confirmDialog.show();

                                }
                            });

                            return;
                        }

                        SharedPreferencesManager.setOfficeID(LoginActivity.this, id);
                        SharedPreferencesManager.setOfficePassword(LoginActivity.this, password);
                        SharedPreferencesManager.setOfficeHashed(LoginActivity.this, office.getHashed());
                        SharedPreferencesManager.setOfficeName(LoginActivity.this, office.getName());

                        if ((office.getOfficeID().equals(id)) && (office.getOfficePassword().equals(password))) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                        Looper.loop();
                    }
                };
                thread.start();



            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (autoSignIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private boolean autoSignIn() {

        //1. 박힌 ID 있는지 확인
        //2. 박힌 PW 있는지 확인
        //3. ID DB에 존재하는지 확인
        //4. ID 사용가능한지 확인
        //5. 비밀번호 맞는지 확인

        String id = null;
        String password = null;

        id = SharedPreferencesManager.getOfficeID(LoginActivity.this);
        password = SharedPreferencesManager.getOfficePass(LoginActivity.this);

        if (SharedPreferencesManager.getOfficeHash(LoginActivity.this) == null) return false;
        if (id == null) return false;
        if (password == null) return false;

        SpiroKitDatabase database = SpiroKitDatabase.getInstance(getApplicationContext());
        Office office = database.officeDao().selectOfficeByID(id);

        if (office == null) return false;
        if (office.getIsUse() == 0) return false;
        if (!office.getOfficePassword().equals(password)) return false;

        return true;
    }

}