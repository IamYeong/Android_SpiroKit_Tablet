
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

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        identifierField = findViewById(R.id.et_id_login);
        passwordField = findViewById(R.id.et_password_login);
        loginButton = findViewById(R.id.btn_user_login);
        moveToJoinButton = findViewById(R.id.btn_to_register_from_login);

        String savedID = SharedPreferencesManager.getOfficeID(LoginActivity.this);
        String savedPass = SharedPreferencesManager.getOfficePass(LoginActivity.this);

        identifierField.setText(savedID);
        passwordField.setText(savedPass);

        moveToJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, ConditionAgreeActivity.class);
                startActivity(intent);

            }
        });

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

                        Office office = database.officeDao().selectOfficeByID(id);
                        SpiroKitDatabase.removeInstance();

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
                                }
                            });
                        }

                        Looper.loop();
                    }
                };
                thread.start();

                /*

                JSONObject jsonObject = new JSONObject();

                try {

                    jsonObject.put("email", "wjdrhkddud2@gmail.com");
                    jsonObject.put("pass", "1234ab");
                    jsonObject.put("from_os", "a");

                } catch (JSONException e) {

                }

                SpiroKitVolley.setVolleyListener(new VolleyResponseListener() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        Log.d(getClass().getSimpleName(), jsonResponse.toString());
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        Log.d(getClass().getSimpleName(), errorResponse.toString());
                    }
                });

                SpiroKitVolley.postJson(jsonObject);

                 */

            }
        });

    }
}