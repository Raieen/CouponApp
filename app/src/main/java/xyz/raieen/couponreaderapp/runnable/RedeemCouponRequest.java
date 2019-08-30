package xyz.raieen.couponreaderapp.runnable;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.raieen.couponreaderapp.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class RedeemCouponRequest extends JsonObjectRequest {

    private final String TAG = "RedeemCouponRequest";

    public RedeemCouponRequest(String url, final Context context) {
        super(Method.POST, url, null, new Response.Listener<JSONObject>() {
            private final String TAG = "RedeemCouponRequestL";

            @Override
            public void onResponse(JSONObject response) {
                try {
                    int quantity = response.getInt("quantity");
                    String action = response.getString("action");

                    Log.d(TAG, String.format("onResponse: Redeemed coupon for %d %s", quantity, action));
                    Toast.makeText(context, "Successfully redeemed coupon.", Toast.LENGTH_SHORT).show();
                    // TODO: 2019-08-30 display for user
//                    Toast.makeText(context, String.format("Successfully redeemed coupon for %d %s", quantity, action), Toast.LENGTH_SHORT).show();
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
