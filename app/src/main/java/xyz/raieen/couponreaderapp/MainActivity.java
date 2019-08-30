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

public class MainActivity extends AppCompatActivity {

    //    public static final String HOST = "http://raieen.xyz:8080/coupon"; // TODO: 2019-08-30 Use preferences
    public static String TAG = "MainActivity";
    public static final String APPLICATION_SECRET = "abc";

    // Coupon API
    public String HOST = "raieen.xyz";
    public String COUPON_URL_PREFIX = "";
    public String CREATE_ENDPOINT = "...";
    public String REDEEM_ENDPOINT = "...";

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
                        // TODO: 2019-08-30 Probably some check for validity here.
                        requestQueue.add(new CreateCouponRequest(CREATE_ENDPOINT, action, recipient, quantity, redeemable));
                    }
                }).start();
            }
        });

        // TODO: 2019-08-30 Do this in xml.
        Spinner spinner = findViewById(R.id.activity_main_spinner_quantity);
        String[] arr = {"1", "2", "3", "4", "5"};
        spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                R.layout.support_simple_spinner_dropdown_item, arr));
    }

    /**
     * Opens zxing
     * https://github.com/zxing/zxing/wiki/Scanning-Via-Intent
     */
    protected void showScanningIntent() {
        new IntentIntegrator(this).setDesiredBarcodeFormats(IntentIntegrator.QR_CODE).setBeepEnabled(false)
                .setOrientationLocked(false).setPrompt(getText(R.string.scan_coupon).toString()).initiateScan();
    }

    public String getCouponId(String string) {
        return string.replaceAll("http://", "").replaceAll("https://", "").replaceAll(COUPON_URL_PREFIX, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // Result from qr scan
        if (intentResult != null && intentResult.getContents() != null) {
            String result = intentResult.getContents();
            Log.d(TAG, String.format("onActivityResult: Scanned %s", result));

            // Coupon QR Codes are formatted as COUPON_URL_PREFIX{id}
            if (!result.contains(COUPON_URL_PREFIX)) {
                showScanningIntent(); // Not a coupon, resume scanning
                return;
            }

            final String couponId = getCouponId(result);
            Log.d(TAG, String.format("onActivityResult: Coupon has id %s", couponId));

            requestQueue.add(new GetCouponRequest("getcoupon", this, requestQueue, "coupon/xxx"));
        }
    }
}
