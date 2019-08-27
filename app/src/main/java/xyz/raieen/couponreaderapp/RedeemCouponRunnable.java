package xyz.raieen.couponreaderapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RedeemCouponRunnable implements Runnable {

    RequestQueue requestQueue;
    String couponId;
    Context context;

    public RedeemCouponRunnable(RequestQueue requestQueue, String couponId, Context context) {
        this.requestQueue = requestQueue;
        this.couponId = couponId;
        this.context = context;
    }

    private static final String TAG = "REDEEMCOUPONRUNNABLE";

    @Override
    public void run() {
        // ANDROID EMULATOR TO LOCALHOST - 10.0.2.2
        String url = String.format("http://raieen.xyz:8080/coupon/%s/redeem", couponId);
        Log.d(TAG, "run: Coupon URL: " + url);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: Redeemed Coupon " + couponId);
                Log.d(TAG, "onResponse: JSON COUPON: " + response.toString());

                try {
                    int quantity = response.getInt("quantity");
                    String action = response.getString("action");

                    Toast.makeText(context, String.format("Successfully redeemed coupon for %d %s", quantity, action), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Malformed Coupon Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error redeeming coupon. Are you connected?", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse: error " + error.getLocalizedMessage());
                Log.d(TAG, "onErrorResponse: Volley Error " + error.networkResponse);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("secret", MainActivity.SECRET_TEMP);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
