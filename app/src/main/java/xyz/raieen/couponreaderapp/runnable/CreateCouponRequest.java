package xyz.raieen.couponreaderapp.runnable;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.raieen.couponreaderapp.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a request to create a coupon given the desired coupon in JSON.
 * Corresponds to PUT: /coupon/ with input coupon
 */
public class CreateCouponRequest extends JsonObjectRequest {

    private final String TAG = "CreateCouponRequest";
    private String action, recipient;
    private int quantity;
    private boolean redeemable;

    public CreateCouponRequest(String url, String action, String recipient, int quantity, boolean redeemable, final Context context) {
        super(Method.PUT, url, null, new Response.Listener<JSONObject>() {
            private final String TAG = "CreateCouponRequestLis";

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String id = response.getString("id");
                    int quantity = response.getInt("quantity");
                    String action = response.getString("action");
                    String recipient = response.getString("recipient");
                    boolean redeemable = response.getBoolean("redeemable");
                    final long redeemed = response.getLong("redeemed");

                    Log.d(TAG, String.format("onResponse: Created coupon for %d %s", quantity, action));
                    new AlertDialog.Builder(context).setTitle("Successfully Created Coupon")
                            .setMessage(String.format("Id: %s\nQuantity: %s\nAction: %s\nRecipient: %s\nRedeemable: %s\nRedeemed: %s", id, quantity, action, recipient, redeemable, redeemed))
                            .create().show();
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

    /**
     * Writes coupon object to body
     */
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
