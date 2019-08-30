package xyz.raieen.couponreaderapp.runnable;

import android.support.annotation.Nullable;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.raieen.couponreaderapp.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class CreateCouponRequest extends JsonObjectRequest {

    private final String TAG = "CreateCouponRequest";
    private String action, recipient;
    private int quantity;
    private boolean redeemable;

    public CreateCouponRequest(String url, String action, String recipient, int quantity, boolean redeemable) {
        super(Method.PUT, url, null, new Response.Listener<JSONObject>() {
            private final String TAG = "CreateCouponRequestLis";

            @Override
            public void onResponse(JSONObject response) {
                try {
                    int quantity = response.getInt("quantity");
                    String action = response.getString("action");

                    // TODO: 2019-08-30 Display this to the user.
//                    Toast.makeText(context, String.format("Successfully created coupon for %d %s", quantity, action), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onResponse: Malformed coupon response. " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            private final String TAG = "CreateCouponRequestErr";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: Volley Error " + error.getMessage());

            }
        });

        this.action = action;
        this.recipient = recipient;
        this.quantity = quantity;
        this.redeemable = redeemable;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("secret", MainActivity.APPLICATION_SECRET);
        return headers;
    }

    @Override
    public byte[] getBody() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("action", action);
            jsonObject.put("recipient", recipient);
            jsonObject.put("quantity", quantity);
            jsonObject.put("redeemable", redeemable);
        } catch (JSONException e) {
            Log.d(TAG, "getBody: " + e.getMessage());
            return super.getBody();
        }
        return jsonObject.toString().getBytes();
    }
}
