package xyz.raieen.couponreaderapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    public static final String HOST = "http://raieen.xyz:8080/coupon";
    public static String TAG = "YEEEEEEEEEET";
    public static final String SECRET_TEMP = "abc";

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Volley
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();

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

        Button tmpRedeem = findViewById(R.id.redeem_tmp);
        tmpRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new RedeemCouponRunnable(requestQueue, "5cdf046852aa352112fab5fc", getBaseContext())).run();
            }
        });

        Button buttonCreateCoupon = findViewById(R.id.activity_main_btn_create);
        buttonCreateCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editAction = findViewById(R.id.activity_main_edit_action);
                EditText editRecipient = findViewById(R.id.activity_main_edit_recipient);
                Spinner spinnerQuantity = findViewById(R.id.activity_main_spinner_quantity);
                CheckBox checkRedeemable = findViewById(R.id.activity_main_check_redeemable);

                new Thread(
                        new CreateCouponRunnable(requestQueue, editAction.getText().toString(),
                                editRecipient.getText().toString(),
                                Integer.parseInt(spinnerQuantity.getSelectedItem().toString()),
                                checkRedeemable.isChecked(), getBaseContext())).run();
            }
        });

        Spinner spinner = findViewById(R.id.activity_main_spinner_quantity);
        String[] arr = {"1", "2", "3", "4", "5"};
        spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                R.layout.support_simple_spinner_dropdown_item, arr));

    }

    protected void showScanningIntent() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.setPrompt(getText(R.string.scan_coupon).toString());
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }   

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
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
                        new Thread(new RedeemCouponRunnable(requestQueue, couponId, getBaseContext())).run();
                }
            });

            builder.create().show();
        }
    }
}
