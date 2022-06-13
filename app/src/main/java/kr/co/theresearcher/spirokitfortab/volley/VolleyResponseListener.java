package kr.co.theresearcher.spirokitfortab.volley;

import org.json.JSONObject;

public interface VolleyResponseListener {
    void onResponse(JSONObject jsonResponse);
    void onError(ErrorResponse errorResponse);
}
