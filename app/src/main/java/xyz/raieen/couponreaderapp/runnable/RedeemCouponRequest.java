package xyz.raieen.couponreaderapp.runnable;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.raieen.couponreaderapp.MainActivity;
import xyz.raieen.couponreaderapp.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a request to redeem a coupon given the coupon id
 * Corresponds to POST: /coupon/{id}/redeem
 */
public class RedeemCouponRequest extends JsonObjectRequest {

    public RedeemCouponRequest(String couponId, final Context context) {
        super(Method.POST, MainActivity.COUPON_ENDPOINT + couponId + "/redeem", null, new Response.Listener<JSONObject>() {
            private final String TAG = "RedeemCouponRequestL";

            @Override
            public void onResponse(JSONObject response) {
                try {
                    int quantity = response.getInt("quantity");
                    String action = response.getString("action");

                    Log.d(TAG, String.format("onResponse: Redeemed coupon for %d %s", quantity, action));
                    Toast.makeText(context, context.getString(R.string.redeem_success, quantity, action), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: Malformed coupon response.", e);
                }
            }
        }, new Response.ErrorListener() {
            private final String TAG = "RedeemCouponRequestErr";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: Error redeeming coupon.", error);
            }
        });
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("secret", MainActivity.APPLICATION_SECRET);
        return headers;
    }
}
