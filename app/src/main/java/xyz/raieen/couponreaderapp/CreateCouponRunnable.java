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

public class CreateCouponRunnable implements Runnable {

    RequestQueue requestQueue;
    String action, receipient;
    int quantity;
    boolean redeemable;
    Context context;

    public CreateCouponRunnable(RequestQueue requestQueue, String action, String receipient, int quantity, boolean redeemable, Context context) {
        this.requestQueue = requestQueue;
        this.action = action;
        this.receipient = receipient;
        this.quantity = quantity;
        this.redeemable = redeemable;
        this.context = context;
    }

    @Override
    public void run() {
        // ANDROID EMULATOR TO LOCALHOST - 10.0.2.2
//        String url = "http://10.0.2.2:8080/coupon/";
        String url = "http://raieen.xyz:8080/coupon/";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int quantity = response.getInt("quantity");
                    String action = response.getString("action");

                    Log.d("YEETEETMEEP", "onResponse: response " + response.toString());
                    Toast.makeText(context, String.format("Successfully created coupon for %d %s", quantity, action), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Malformed Coupon Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ASKDNKLASDA", "onErrorResponse: eeee " + error.getMessage());
                Toast.makeText(context, "Error creating coupon. Are you connected?", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("secret", MainActivity.SECRET_TEMP);

                return headers;
            }

            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", action);
                    jsonObject.put("receipient", receipient);
                    jsonObject.put("quantity", quantity);
                    jsonObject.put("redeemable", redeemable);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return super.getBody();
                }

                // TODO: 24/05/19 Check if I need to specify UTF-8... this is Android (so no?)
                return jsonObject.toString().getBytes();

            }
        };

        requestQueue.add(jsonObjectRequest);

    }
}
