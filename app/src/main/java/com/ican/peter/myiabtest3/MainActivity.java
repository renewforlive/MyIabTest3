package com.ican.peter.myiabtest3;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ican.peter.myiabtest3.util.IabHelper;
import com.ican.peter.myiabtest3.util.IabResult;
import com.ican.peter.myiabtest3.util.Inventory;
import com.ican.peter.myiabtest3.util.Purchase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Debug tag, for logging
    static final String TAG = "IabTest";

    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static final String SKU_Product1 = "product1";
    static final String SKU_Product2 = "product2";
    static final String SKU_Product3 = "product3";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    //IabHelpler創建
    IabHelper mHelper;

    //Button
    Button btn1,btn2,btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.buy1);
        btn2 = findViewById(R.id.buy2);
        btn3 = findViewById(R.id.buy3);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7AxGwDReqG4t3Y1x/3tB5W0eOOGbU/2umTrflWtEkcjHOhrgtV0Qj+JAWEMHOFURMGUPXfNmKWCMA+SLsnQWUaqc/EOINgZ763ErvHzXO34lNOE4qgYpLSqK1KyU5yLzVA2SjZS9ioshOikgJ9aXeuQDRnK3KwMj4w3tOSETF416XcOPOICvH8ol4j21SF7A70cLt4TXa6C1CRFoKPPlZhPCX3vwmTt/XoMUjhRgnGhzobSuZV+Saksob4/edA6qZT5Xsc/CrTWw9AkfIt66yui6SmD+IqqJNrQP+2zQcwM5IrnQH6N9HEFbfTwn7aq/InTZfLJkBKmV0Vv0uI9mUQIDAQAB";

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            Log.d(TAG, "Query inventory finished.");
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buy1:
                buyproduct1();
                break;
            case R.id.buy2:
                buyproduct2();
                break;
            case R.id.buy3:
                buyproduct3();
                break;
        }
    }
    public void buyproduct1(){
        Log.d(TAG, "Buy gas button clicked.");

        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU_Product1, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
    }
    // User clicked the "Upgrade to Premium" button.
    public void buyproduct2() {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");

        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU_Product2, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
    }
    public void buyproduct3() {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");

        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU_Product3, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
    }
    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    boolean verifyDeveloperPayload(Purchase p){
        String payload = p.getDeveloperPayload();
        return true;
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_Product1)) {
                // bought product1. So consume it.
                Log.d(TAG, "Purchase is product1. Starting product1 consumption.");
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming product1. Another async operation in progress.");
                    return;
                }
            }
            else if (purchase.getSku().equals(SKU_Product2)) {
                // bought product. So consume it!
                Log.d(TAG, "Purchase is product2. Starting product2 consumption.");
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming product2. Another async operation in progress.");
                    return;
                }
            }
            else if (purchase.getSku().equals(SKU_Product3)){
                // bought product. So consume it!
                Log.d(TAG, "Purchase is product2. Starting product2 consumption.");
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming product2. Another async operation in progress.");
                    return;
                }
            }
        }
    };
    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");

            }
            else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }
}
