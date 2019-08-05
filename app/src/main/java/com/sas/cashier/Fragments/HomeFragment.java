package com.sas.cashier.Fragments;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.HttpGet;
import com.sas.cashier.BarcodeCaptureActivity;
import com.sas.cashier.Data.Item;
import com.sas.cashier.Data.User;
import com.sas.cashier.ListActivity;
import com.sas.cashier.MainActivity;
import com.sas.cashier.R;
import com.sas.cashier.TabbedActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class HomeFragment extends Fragment {

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView totalCost;
    private TextView tv;
    private Double currentCost = 0.00;
    private ImageView reset , cart;
    private JSONArray final_items = null;
    private  Double final_price = null;
    private MaterialButton barcodeReader;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private static int numberOfSplits=0;
    private CheckBox isSplit;
    private static Boolean shouldSplit;
    private static HashMap<Integer,Double> splitAmounts =new HashMap<>();
    private static int currentSplitee=0;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    User currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View v = lf.inflate ( R.layout.home_fragment, container, false );

        initializeVars(v);

        return v;
    }

    private void initializeVars(View v)
    {


        totalCost = (TextView)v.findViewById(R.id.total_cost);
        totalCost.setShadowLayer(1, 0, 0, Color.BLACK);

        isSplit= v.findViewById(R.id.split);

//        reset = v.findViewById(R.id.reset_btn);


        mDatabase = FirebaseDatabase.getInstance().getReference();
//        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
//        useFlash = (CompoundButton) findViewById(R.id.use_flash);
        barcodeReader = v.findViewById(R.id.read_barcode);

        isSplit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG,"check changed" + b);
                if(b)
                {
                    Log.d(TAG,"IsSplit is now true");
                    showNumberPicker();
                    shouldSplit=true;

                    //display option for how many people
                    //create dialog box
                }
              else
                {
                    shouldSplit=false;
                    numberOfSplits=0;
                }
            }
        });

        barcodeReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity().getApplicationContext(), BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus,true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

//
//        reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                currentCost=0.0;
//                totalCost.setText("$ "+currentCost.toString());
//            }
//        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

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
    //    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    public void showNumberPicker()
    {

        final Dialog d = new Dialog(getContext());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog);
//        Button b1 = (Button) d.findViewById(R.id.set_number);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(10);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
              Log.d(TAG,"number changed " + i + i1);
              numberOfSplits=i1;
            }
        });
//        b1.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG,"number of splits : " + numberOfSplits);
//                d.dismiss();
//            }
//        });

        d.show();


    }


    private void fetchUPCData(String barcodeValue)
    {
        //set all all split amoutns to 0 when fetching
        if(shouldSplit)
        {
            for(int i = 0 ; i<numberOfSplits;i++)
            {
                splitAmounts.put(i,0.0);
            }
        }
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

            if(shouldSplit)
            {
                Log.d(TAG,"splits : " + numberOfSplits);

                //open dialog for selecting currentSplitee
                currentSplitee=getCurrentSplitee();//2
                Double currentCost = splitAmounts.get(currentSplitee);
                price+=currentCost;
                Log.d(TAG,"getting cost from current index : "  +  currentCost + price);
                Log.d(TAG,"current cost : " + splitAmounts.get(currentSplitee));
                //if its split, then get the count of that person and add to that value
                splitAmounts.put(currentSplitee,price);
                Log.d(TAG,"Split amounts is updated  " + splitAmounts);
            }
            else {
                Log.d(TAG, "Current cost : " + currentCost);
                currentCost += price;
                Log.d(TAG, "new cost : " + currentCost);
                totalCost.setText("$ " + df.format(currentCost).toString());

            }
                JSONArray final_items = getFinalItems();
                try {
                    setItemsList(final_items, price);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


        }

        private void setTotalCost(HashMap<String, Double> all_prices, String merchant) {
            DecimalFormat df = new DecimalFormat("#.##");

            Log.d(TAG, "Current cost : " + currentCost);
            Double costToAdd= all_prices.get(merchant);
            if(shouldSplit)
            {
                ArrayList<Double> tempAmounts = new ArrayList<>(numberOfSplits);
                //open dialog for selecting currentSplitee
                currentSplitee=getCurrentSplitee();
                //if its split, then get the count of that person and add to that value
                Double currentCost = splitAmounts.get(currentSplitee);
                currentCost+=costToAdd;
                //if its split, then get the count of that person and add to that value
                splitAmounts.put(currentSplitee,currentCost);
                Log.d(TAG,"Split amounts is updated  " + splitAmounts);
            }
            else {
                currentCost += all_prices.get(merchant);
                Log.d(TAG, "new cost : " + currentCost);
                totalCost.setText("$" + df.format(currentCost).toString());
            }
                JSONArray final_items = getFinalItems();
                try {
                    setItemsList(final_items, all_prices.get(merchant));
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
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

        private int getCurrentSplitee()
        {
            //show dialog and return the value selected
            return 2;
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

            firebaseAuth  = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String uniqueID = UUID.randomUUID().toString();
            mDatabase.child("items").child(user.getUid()).child(uniqueID).setValue(item);




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
                    Toast.makeText(getActivity().getApplicationContext(),"Not a valid barcode" , Toast.LENGTH_SHORT).show();
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
