package xyz.raieen.couponreaderapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import xyz.raieen.couponreaderapp.runnable.RedeemCouponRequest;

public class MainActivity extends AppCompatActivity {

//    public static final String HOST = "http://raieen.xyz:8080/coupon"; // TODO: 2019-08-30 Use preferences
    public static String TAG = "MainActivity";
    public static final String APPLICATION_SECRET = "abc";

    // Coupon API
    public String HOST = "raieen.xyz";
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

        // TODO: 2019-08-30 This can probably be done better.
        Button createCoupon = findViewById(R.id.activity_main_btn_create);
        createCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editAction = findViewById(R.id.activity_main_edit_action);
                final EditText editRecipient = findViewById(R.id.activity_main_edit_recipient);
                final Spinner spinnerQuantity = findViewById(R.id.activity_main_spinner_quantity);
                final CheckBox checkRedeemable = findViewById(R.id.activity_main_check_redeemable);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // Result from qr scan
        if (intentResult != null && intentResult.getContents() != null) {
            String result = intentResult.getContents();

            Log.d(TAG, "onActivityResult: Scanned " + result);

            // QR Codes are 'http://raieen.xyz/coupon/{id}
            if (!result.contains("raieen.xyz/coupon/")) {
                showScanningIntent(); // Not a coupon, resume scanning
                return;
            }

            final String couponId = result.replaceAll("raieen.xyz/coupon/", "")
                    .replaceAll("http://raieen.xyz/coupon/", "")
                    .replaceAll("https://raieen.xyz/coupon/", "");
            Toast.makeText(this, "Coupon ID: " + result, Toast.LENGTH_SHORT).show();

            // TODO: 17/05/19 Check if it was redeemed, if it was redeemed, display error message.

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Authorize this coupon?")
                    .setMessage("Have a message about the coupon here... basic coupon info.")
                    .setNegativeButton("Cancel", null).
                    setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    requestQueue.add(new RedeemCouponRequest(String.format(REDEEM_ENDPOINT, couponId)));
                                }
                            }).start();
                        }
                    }).create().show();
        }
    }
}
