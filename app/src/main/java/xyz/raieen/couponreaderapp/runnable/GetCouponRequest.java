package xyz.raieen.couponreaderapp.runnable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.RequestQueue;
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
 * Represents a request to get a coupon given the id.
 * Corresponds to POST: /coupon/{id}
 */
public class GetCouponRequest extends JsonObjectRequest {

    public GetCouponRequest(final String couponId, final Context context, final RequestQueue requestQueue) {
        super(Method.POST, MainActivity.COUPON_ENDPOINT + "/" + couponId, null, new Response.Listener<JSONObject>() {
            private final String TAG = "GetCouponRequestL";

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String id = response.getString("id");
                    int quantity = response.getInt("quantity");
                    String action = response.getString("action");
                    String recipient = response.getString("recipient");
                    boolean redeemable = response.getBoolean("redeemable");
                    final long redeemed = response.getLong("redeemed");

                    new AlertDialog.Builder(context).setTitle(context.getString(R.string.coupon_authorize))
                            .setMessage(context.getString(R.string.coupon_details, id, quantity, action, recipient, redeemable, redeemed))
                            .setNegativeButton(context.getText(R.string.cancel), null)
                            .setPositiveButton(context.getText(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (redeemed != 0) { // Don't allow coupons to be double-redeemed
                                        Toast.makeText(context, context.getString(R.string.coupon_redeemed), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Send redeem request
                                            requestQueue.add(new RedeemCouponRequest(couponId, context));
                                        }
                                    }).start();
                                }
                            }).create().show();
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: ", e);
                }
            }
        }, new Response.ErrorListener() {
            private final String TAG = "GetCouponRequestErr";

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: Error getting coupon.", error);
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
