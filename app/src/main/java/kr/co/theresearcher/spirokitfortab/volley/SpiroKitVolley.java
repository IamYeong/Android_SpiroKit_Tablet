package kr.co.theresearcher.spirokitfortab.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class SpiroKitVolley {

    private static final String USER_LOGIN_POST_URL = "http://3.35.64.150:8000/apis/user/login";


    private static RequestQueue requestQueue;
    private static VolleyResponseListener listener;

    public static void createRequestQueue(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static void setVolleyListener(VolleyResponseListener volleyListener) {
        listener = volleyListener;
    }

    public static void postJson(JSONObject jsonObject) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, USER_LOGIN_POST_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //에러코드도 해당 응답에 포함되므로 "code"키에 해당하는 값으로 에러여부를 판단해야 함.
                        if (listener != null) listener.onResponse(response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Volley 자체에 통신오류가 생겼을 경우
                if (listener != null) listener.onError(new ErrorResponse(0, error.getMessage()));
            }
        });

        requestQueue.add(jsonObjectRequest);

    }




}
