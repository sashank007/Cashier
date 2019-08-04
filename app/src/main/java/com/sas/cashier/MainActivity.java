package com.sas.cashier;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.HttpGet;
import com.sas.cashier.Data.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView totalCost;
    private TextView barcodeValue;
    private Double currentCost = 0.00;
    private Button reset;
    private  JSONArray final_items = null;
    private  Double final_price = null;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Cashier");

        totalCost = (TextView)findViewById(R.id.total_cost);
        barcodeValue = (TextView)findViewById(R.id.barcode_value);
        reset = findViewById(R.id.resetButton);

//        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
//        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        findViewById(R.id.read_barcode).setOnClickListener(this);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCost=0.0;
                totalCost.setText("$ "+currentCost.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus,true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    barcodeValue.setText(barcode.displayValue);
                    fetchUPCData(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {

                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void fetchUPCData(String barcodeValue)
    {
        if(barcodeValue!="")
        {
        barcodeValue=barcodeValue.trim();
        Log.d(TAG,"Fetching upc data for barcode "  + barcodeValue);
        new asyncFetchUPCData(barcodeValue).execute();}


    }



    public class asyncFetchUPCData extends AsyncTask<String, Void, JSONObject> {
        String barcodeValue;
        String count;
        public String TAG = "ASYNCFETCHUPC";

        public asyncFetchUPCData(String barcodevalue) {
            this.barcodeValue = barcodevalue;
        }

        private String upcUri = "https://api.upcitemdb.com/prod/trial/lookup?upc=";

        protected JSONObject doInBackground(String... params) {

            System.setProperty("https.protocols", "TLSv1.1");
            try {

                upcUri += barcodeValue;

                HttpGet httpGet = new HttpGet(upcUri);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpGet);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject jsono = new JSONObject(data);
                    Log.d(TAG, "json data:  " + jsono);

                    return jsono;
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            processResult(result);

        }

        private void setTotalCost(Double price, String merchant) {
            DecimalFormat df = new DecimalFormat("#.##");

            Log.d(TAG, "Current cost : " + currentCost);
            currentCost += price;
            Log.d(TAG, "new cost : " + currentCost);
            totalCost.setText("$ " + df.format(currentCost).toString());

            JSONArray final_items = getFinalItems();
            try {
                setItemsList(final_items , price);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void setTotalCost(HashMap<String, Double> all_prices, String merchant) {
            DecimalFormat df = new DecimalFormat("#.##");

            Log.d(TAG, "Current cost : " + currentCost);
            currentCost += all_prices.get(merchant);
            Log.d(TAG, "new cost : " + currentCost);
            totalCost.setText("$" + df.format(currentCost).toString());

            JSONArray final_items = getFinalItems();
            try {
                setItemsList(final_items , all_prices.get(merchant));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        private String[] getAllOptions(HashMap<String, Double> all_prices) {
            List<String> l = new ArrayList<String>(all_prices.keySet());
            String[] options = new String[l.size()];
            options = l.toArray(options);
            return options;
        }

        private void openShopSelector(final HashMap<String, Double> all_prices) {
            final String[] options = getAllOptions(all_prices);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Where did you get this item from?");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    Log.d(TAG, "Clicked on " + options[which]);
                    setTotalCost(all_prices, options[which]);

                }
            });
            builder.show();
        }

        public void setItemsList(JSONArray items , Double price) throws JSONException {

            Log.d(TAG,"items in setItemsList : " + items);
            JSONObject mainItem = items.getJSONObject(0);
            String title = mainItem.getString("title");
            JSONArray arrJson = mainItem.getJSONArray("images");
            String[] images = new String[arrJson.length()];
            for(int i = 0 ; i<arrJson.length();i++)
            {
                images[i]=arrJson.getString(i);
            }
            Log.d(TAG,"Images :  + " + images);
            String image = images[arrJson.length()-1];
            Log.d(TAG," main image : " + image);

            Item item = new Item(title , price , image);
            Intent i = new Intent(MainActivity.this,ListActivity.class);
            i.putExtra("title" , title);
            i.putExtra("price" , price);
            i.putExtra("image",image);
            firebaseAuth  = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String uniqueID = UUID.randomUUID().toString();
            mDatabase.child("items").child(user.getUid()).child(uniqueID).setValue(item);

            startActivity(i);



        }

        public void processResult(JSONObject result) {
            try {

                if(result!=null)
                {
                JSONArray items = result.getJSONArray("items");

                JSONArray all_offers = new JSONArray();
                HashMap<String, Double> all_prices = new HashMap<>();
                setFinalItems(items);
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    all_offers = item.getJSONArray("offers");

                    Log.d(TAG, "these are the offers : " + all_offers);
                }

                if (all_offers.length() > 1) {

                    for (int i = 0; i < all_offers.length(); i++) {
                        JSONObject currentOffer = all_offers.getJSONObject(i);
                        Double price = currentOffer.getDouble("price");
                        String merchant = currentOffer.getString("merchant");
                        if (price != 0.0) {
                            all_prices.put(merchant, price);
                        }

                    }
                    openShopSelector(all_prices);

                    Log.d(TAG, "All prices: " + all_prices);
                } else if (all_offers.length() == 1) {
                    JSONObject currentOffer = all_offers.getJSONObject(0);
                    Double price = currentOffer.getDouble("price");
                    String merchant = currentOffer.getString("merchant");
                    setTotalCost(price, merchant);

                }
            }
            else
                {
                    Toast.makeText(MainActivity.this,"Not a valid barcode" , Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    private void setFinalItems(JSONArray items)
    {
        this.final_items=items;
    }
    private JSONArray getFinalItems()
    {
        return this.final_items;
    }

    private void setFinalPrice (Double price)
    {
        this.final_price=price;
    }

    private Double getFinalPrice(Double price)
    {
        return this.final_price;

    }
}