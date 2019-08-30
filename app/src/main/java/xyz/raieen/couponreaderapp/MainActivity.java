package xyz.raieen.couponreaderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import xyz.raieen.couponreaderapp.runnable.CreateCouponRequest;
import xyz.raieen.couponreaderapp.runnable.GetCouponRequest;

/**
 * Main Activity
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    // Coupon API
    public static final String COUPON_ENDPOINT = "http://192.168.2.59:8080/coupon/"; // TODO: 2019-08-30 Use preferences
    public static final String APPLICATION_SECRET = "abc";

    // Volley
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Volley
         */
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();

        /*
         * Android
         */

        // Toolbar Setup
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_scan_coupon) {
                    showScanningIntent();
                }
                return true;
            }
        });

        // Create Coupon Setup
        final EditText editAction = findViewById(R.id.activity_main_edit_action);
        final EditText editRecipient = findViewById(R.id.activity_main_edit_recipient);
        final Spinner spinnerQuantity = findViewById(R.id.activity_main_spinner_quantity);
        final CheckBox checkRedeemable = findViewById(R.id.activity_main_check_redeemable);

        Button createCoupon = findViewById(R.id.activity_main_btn_create);
        createCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String action = editAction.getText().toString(), recipient = editRecipient.getText().toString();
                        int quantity = Integer.parseInt(spinnerQuantity.getSelectedItem().toString());
                        boolean redeemable = checkRedeemable.isChecked();

                        if (action.trim().isEmpty()) return;
                        requestQueue.add(new CreateCouponRequest(COUPON_ENDPOINT, action, recipient,
                                quantity, redeemable, getApplicationContext()));
                    }
                }).start();
            }
        });
    }

    /**
     * Opens zxing for scanning QR code on coupons
     *
     * @see <a href="https://github.com/zxing/zxing/wiki/Scanning-Via-Intent">https://github.com/zxing/zxing/wiki/Scanning-Via-Intent</a>
     */
    private void showScanningIntent() {
        new IntentIntegrator(this).setDesiredBarcodeFormats(IntentIntegrator.QR_CODE).setBeepEnabled(false)
                .setOrientationLocked(false).setPrompt(getText(R.string.zxing_message).toString()).initiateScan();
    }

    /**
     * Handles the result of scanning QR code from zxing.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null && intentResult.getContents() != null) {
            String result = intentResult.getContents(); // Result from scan
            Log.d(TAG, String.format("onActivityResult: Scanned %s", result));

            // Coupon QR Codes are formatted as COUPON_URL_PREFIX{id}
            if (!result.contains(COUPON_ENDPOINT)) {
                showScanningIntent(); // Not a coupon, resume scanning
                return;
            }

            final String couponId = result.replaceAll(COUPON_ENDPOINT, "");
            Log.d(TAG, String.format("onActivityResult: Coupon has id %s", couponId));
            requestQueue.add(new GetCouponRequest(COUPON_ENDPOINT + "/" + couponId, this, requestQueue, couponId));
        }
    }
}
