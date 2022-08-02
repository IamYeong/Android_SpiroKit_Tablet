package kr.co.theresearcher.spirokitfortab.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class SpiroKitVolley {

    //private static final String USER_LOGIN_POST_URL = "http://192.168.0.104:4500/apis/spirokit/e/sync/01";
    private static final String USER_LOGIN_POST_URL = "http://43.200.35.255:4500/apis/spirokit/e/sync/01";

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
                Log.e(getClass().getSimpleName(), error.toString());
                //Volley 자체에 통신오류가 생겼을 경우
                if (listener != null) {

                    listener.onError(new ErrorResponse(0, error.getMessage()));
                }
            }
        });

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 600000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.e(getClass().getSimpleName(), error.toString());
            }
        });

        jsonObjectRequest.setTag("POST");

        requestQueue.add(jsonObjectRequest);

    }

    public static void cancelAllRequest() {
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {

                //if(request.getTag().equal("POST)) return true;

                return true;
            }
        });

    }


}
