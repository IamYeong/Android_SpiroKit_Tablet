
package kr.co.theresearcher.spirokitfortab.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.Map;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.join.ConditionAgreeActivity;
import kr.co.theresearcher.spirokitfortab.join.JoinUserActivity;
import kr.co.theresearcher.spirokitfortab.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, moveToJoinButton;
    private EditText emailField, passwordField;

    private Handler handler = new Handler(Looper.getMainLooper());

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.et_email_login);
        passwordField = findViewById(R.id.et_password_login);
        loginButton = findViewById(R.id.btn_user_login);
        moveToJoinButton = findViewById(R.id.btn_to_register_from_login);

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



                String url = "http://3.35.64.150:8000/apis/user/login";
                JSONObject jsonObject = new JSONObject();

                try {

                    jsonObject.put("email", "wjdrhkddud2@gmail.com");
                    jsonObject.put("pass", "1234ab");
                    jsonObject.put("from_os", "a");

                } catch (JSONException e) {

                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                System.out.println(response.toString());

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("error");
                    }
                });

                requestQueue.add(jsonObjectRequest);
                /*
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                //finish();

                 */
            }
        });

        requestQueue = Volley.newRequestQueue(LoginActivity.this);

    }
}