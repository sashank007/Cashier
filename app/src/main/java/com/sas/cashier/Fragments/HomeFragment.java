package com.sas.cashier.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
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
import com.sas.cashier.CustomAdapterSplits;
import com.sas.cashier.Data.Item;
import com.sas.cashier.Data.Splits;
import com.sas.cashier.Data.User;
import com.sas.cashier.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

import static com.sas.cashier.TabbedActivity.MY_FRAGMENT;


public class HomeFragment extends Fragment {

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView totalCost , tv_splitTotalCost , tv_splitIndex;
    private TextView tv_budget_percent;
    private Double currentCost = 0.00;
    private ImageView reset , cart;
    private JSONArray final_items = null;
    private  Double final_price = null;
    private MaterialButton cleartCart;
    private Boolean firstTransaction=true;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private static int numberOfSplits=0;
    private ListView listView;
    private  FirebaseUser user;
    private FloatingActionButton addItem;
    private static CustomAdapterSplits adapter;
    private BottomAppBar bottomAppBar;
    private SwitchMaterial isSplit;
    private static Boolean shouldSplit = false;
    private static HashMap<String,Double> splitAmounts =new HashMap<>();
    private  int currentSplitee=0;
    private ArrayList<Splits> dataModels = new ArrayList<>();
    private String[] colorPalette = {"#fffcc1" , "#f5b5fc" , "#f3826f" , "#ffd692" , "#ffcbcb" , "#f8f8f8" , "#ff0b55","#badfdb" ,"#49beb7"};
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private FirebaseUser mUser;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private ImageView imgNoSplits;
    private TextView tv_noSplits;
    private LinearLayout layoutBottomSheet;
    private ImageView add_splitee_btn;
    BottomSheetBehavior sheetBehavior;
    List<String> allSplitees = new ArrayList<String>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View v = lf.inflate ( R.layout.home_fragment, container, false );

        Log.d(TAG, "on create view");

        listView=(ListView)v.findViewById(R.id.list_splits);
//
        adapter= new CustomAdapterSplits(dataModels,getContext());

        listView.setAdapter(adapter);

        initializeVars(v);





        return v;
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, "MY_FRAGMENT")
                    .commit();
            return true;
        }
        return false;
    }

    private void initializeVars(View v)
    {
        firebaseAuth  = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        add_splitee_btn=v.findViewById(R.id.img_nosplits);
        totalCost = v.findViewById(R.id.total_cost);
        totalCost.setShadowLayer(1, 0, 0, Color.BLACK);

        user = firebaseAuth.getCurrentUser();
        tv_splitIndex = v.findViewById(R.id.tv_split_index);
        tv_splitTotalCost = v.findViewById(R.id.total_cost_split);
        tv_splitIndex.setVisibility(View.INVISIBLE);
        tv_splitTotalCost.setVisibility(View.INVISIBLE);
        cleartCart = v.findViewById(R.id.btn_clearcart);
        progressBar = v.findViewById(R.id.progressBar);
        tv_budget_percent= v.findViewById(R.id.tv_budgetpercent);
//        layoutBottomSheet=v.findViewById(R.id.bottom_sheet);
        tv_noSplits=v.findViewById(R.id.no_splits_text);
        imgNoSplits=v.findViewById(R.id.img_nosplits);


        fab = v.findViewById(R.id.fab);

        add_splitee_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadFragment(new AddSplitsFragment());

            }
        });

         bottomAppBar = v.findViewById(R.id.navigation);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_app_bar_home:
                        loadFragment(new HomeFragment());
                        return true;
                    case R.id.bottom_app_bar_cart:
                        loadFragment(new ListFragment());
                        return true;

                    case R.id.bottom_app_bar_list:
                        loadFragment(new AddSplitsFragment());
                        return true;

                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSplit.isChecked() && adapter.isEmpty())
                {
                    new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                            .setTitle("Warning!")
                            .setMessage("Please add a friend to split your bill with!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();

                }
                else {


                    Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    startActivityForResult(intent, RC_BARCODE_CAPTURE);
                }

            }

        });


        isSplit= v.findViewById(R.id.issplit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        updateTotalCostAmount(0.00);


        cleartCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAmount();

            }
        });

        tv_splitTotalCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentVisibleIndex = tv_splitIndex.getText().toString();
                int index = Integer.parseInt(currentVisibleIndex);
                Log.d(TAG,"split index in on click: " + index);

                if(index==numberOfSplits-1)
                {
                    tv_splitIndex.setText("0");
                    setSplittableLayout();
                }
                else
                {
                    tv_splitIndex.setText(Integer.toString(index+1));
                    setSplittableLayout();
                }
            }
        });
        fetchSplits();

//        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
//
//        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                switch (newState) {
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                        break;
//                    case BottomSheetBehavior.STATE_EXPANDED: {
//
//                    }
//                    break;
//                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                    }
//                    break;
//                    case BottomSheetBehavior.STATE_DRAGGING:
//                        break;
//                    case BottomSheetBehavior.STATE_SETTLING:
//                        break;
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });




        isSplit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG,"check changed" + b);
                if(b)
                {
                    Log.d(TAG,"IsSplit is now true");

//                    showNumberPicker();
                    shouldSplit=true;
//                    loadFragment(new AddSplitsFragment());

                }
              else
                {
                    shouldSplit=false;
                    numberOfSplits=0;
                    firstTransaction=true;
                }
            }
        });

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

    public void refreshFragment()
    {
        Fragment currentFragment = getFragmentManager().findFragmentByTag("YourFragmentTag");
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
    }
    public void setSplittableLayout()
    {
        //inside this we hide the old dollar ammount and set number of dollar amounts to be equal to numberOfSplits
        //show each index each time on click
        //on click change the value
        totalCost.setVisibility(View.INVISIBLE);
        tv_splitTotalCost.setVisibility(View.VISIBLE);
        tv_splitIndex.setVisibility(View.VISIBLE);
        Log.d(TAG,"Split amounts in splittable layout : " + splitAmounts );
        final String currentVisibleIndex =(tv_splitIndex.getText().toString());
        Log.d(TAG," split index : " + tv_splitIndex.getText().toString());
        tv_splitTotalCost.setTextColor(Color.parseColor(colorPalette[Integer.parseInt(currentVisibleIndex)]));
        tv_splitTotalCost.setText("$ " + Double.toString(splitAmounts.get(currentVisibleIndex)));


    }

    public void showNumberPicker()
    {

        final Dialog d = new Dialog(getContext());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog);
//        Button b1 = (Button) d.findViewById(R.id.set_number);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(8);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
              Log.d(TAG,"number changed " + i + i1);
              numberOfSplits=i1;
//              setSplittableLayout();
            }
        });


        d.show();


    }


    private void fetchUPCData(String barcodeValue)
    {
        //set all all split amoutns to 0 when fetching
        if(shouldSplit&&firstTransaction)
        {
            for(int i = 0 ; i<=numberOfSplits;i++)
            {
                DecimalFormat df = new DecimalFormat("#.##");
                splitAmounts.put(Integer.toString(i),Double.parseDouble(df.format(0.00)));
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

        private void getCurrentValues() {

            FirebaseUser user = firebaseAuth.getCurrentUser();


            Query myQuery = mDatabase.child("splits").child(user.getUid());
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                List<Item> myList = new ArrayList<>();

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                       Double value = snap.getValue(Double.class);
                        Log.d(TAG, "Snap value : " + snap.getValue());
                        Log.d(TAG, "SNap key : " + snap.getKey());
                        splitAmounts.put(snap.getKey(), Double.parseDouble(snap.getValue().toString()));

                    }
                    Log.d(TAG, "SPlit amounts in get current vaue s; " + splitAmounts);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        private void setTotalCost(Double price, String merchant) {
            DecimalFormat df = new DecimalFormat("#.##");

            if (shouldSplit) {
                Log.d(TAG, "splits : " + numberOfSplits);

                //open dialog for selecting currentSplitee
                getAllSplitees(price);

            } else {
                Log.d(TAG, "Current cost : " + currentCost);
                updateTotalCostAmount(price);
//                currentCost += price;
//                Log.d(TAG, "new cost : " + currentCost);
//                totalCost.setText("$ " + df.format(currentCost).toString());

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
            Double costToAdd = all_prices.get(merchant);
            if (shouldSplit) {

                //open dialog for selecting currentSplitee
                getAllSplitees(costToAdd);

            } else {
                updateTotalCostAmount(costToAdd);
//                currentCost += all_prices.get(merchant);
//                Log.d(TAG, "new cost : " + currentCost);
//                totalCost.setText("$" + df.format(currentCost).toString());
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        //@TODO: Convert this to get all splitNames from splits table
        private void getAllSplitees(final Double price) {

            allSplitees.clear();

            //get values from the splits query
            Query myQuery = mDatabase.child("splits").child(mUser.getUid());
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        Log.d(TAG,"new split list : "  + allSplitees);
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Splits item = snap.getValue(Splits.class);
                            allSplitees.add(item.getName());
                        }

                        Log.d(TAG,"all splitees: " + allSplitees);
                        String[] options = new String[allSplitees.size()];
                        options = allSplitees.toArray(options);
                        Log.d(TAG,"options : " + options.toString());
                        getCurrentSplitee(price , options);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });



        }





        private void getCurrentSplitee(final Double price ,final String[] options)
        {
//            final String[] options = getAllSplitees();

            //@TODO: update this to update db amount whenever that name is selected in splits

            //show dialog and return the value selected
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Who does this item belong to?");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    Log.d(TAG, "Clicked on " + options[which]);

                    setCurrentSplitee(options[which] , price);
                }
            });
            builder.show();

        }

        //final step in setting split values
        private void setCurrentSplitee(String val ,Double price)

        {
            Log.d(TAG,"Currnet splitee value changd " + val + price);
//
//            getCurrentValues();
//            Double currentCost = splitAmounts.get(Integer.toString(val));
//            currentCost+=price;
            //set the given price to that name
            setNewSplitAmount(val , price);
//            splitAmounts.put(Integer.toString(val),currentCost);




//            mDatabase.child("splits").child(user.getUid()).setValue(splitAmounts);

            Log.d(TAG,"Split amounts is updated  " + splitAmounts);

            firstTransaction=false;

            //update visible text based on index
//            updateVisibleAmounts();

        }

        public void setNewSplitAmount(final String name , final Double price)
        {

            Query myQuery = mDatabase.child("splits").child(mUser.getUid());
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
//                        String id  = dataSnapshot.getChildren.getValu(String.class);

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Log.d(TAG,"id : "  +  snap.getKey());
                            Splits item = snap.getValue(Splits.class);
                            if(item.getName().equals(name))
                            {
                                String id = snap.getKey();
                                Double currentCost = item.getAmount();
                                currentCost+=price;
                                Log.d(TAG,"updating price for " + name + " " + currentCost);
                                mDatabase.child("splits").child(user.getUid()).child(id).child("AmountSpent").setValue(currentCost);

                            }
                        }
                        fetchSplits();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });


        }

        public void updateVisibleAmounts()
        {
            setSplittableLayout();
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
                            if (price != 0.00) {
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

    private void updateTotalCostAmount(final Double price)
    {
        final FirebaseUser user = firebaseAuth.getCurrentUser();


        Query myQuery = mDatabase.child("users").child(user.getUid());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                    User u = snap.getValue(User.class);

                    Double budget = u.getBudget();
                    Double exp = u.getExpenditure();

                        currentCost=exp;
                        currentCost+=price;
                        Log.d(TAG,"Current expendtiure : " + currentCost);
                        totalCost.setText("$ " + currentCost.toString());
                        Double percent =(currentCost/budget)*100;
                        int perc = percent.intValue();

                        updateProgressBar(perc);
                        mDatabase.child("users").child(user.getUid()).child("expenditure").setValue(currentCost);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void updateProgressBar(int percent)
    {
        progressBar.setProgress(percent);
        tv_budget_percent.setText(Integer.toString(percent)+"%");
    }




    public void resetAmount() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to clear your cart?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Fragment frg = null;
                        frg = getFragmentManager().findFragmentByTag(MY_FRAGMENT);
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(frg);
                        ft.attach(frg);
                        ft.commit();

                        //delete all items in cart
                        DatabaseReference dbNode = FirebaseDatabase.getInstance().getReference().getRoot().child("items").child(mUser.getUid());
                        dbNode.setValue(null);
                        DatabaseReference dbNodeExp = FirebaseDatabase.getInstance().getReference().getRoot().child("users").child(mUser.getUid()).child("expenditure");
                        dbNodeExp.setValue(0);
                        currentCost=0.00;
                        totalCost.setText("$ "+currentCost.toString());
                        //delete all splits
                        DatabaseReference dbNodeSplits = FirebaseDatabase.getInstance().getReference().getRoot().child("splits").child(mUser.getUid());
                        dbNodeSplits.setValue(null);
                        //uncheck splits
                        isSplit.setChecked(false);



                    }
                }).show();



    }

    private void fetchSplits()
    {

        Query myQuery = mDatabase.child("splits").child(mUser.getUid());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Item> myList = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double currentCost=0.0;
                if (dataSnapshot.hasChildren())
                {
                  if(!adapter.isEmpty())
                  {
                      adapter.clear();
                  }
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        Splits item = snap.getValue(Splits.class);
                        currentCost+=item.getAmount();
                        Log.d("TAG", "Splits : " + snap);
                        dataModels.add(item);
//                    myList.add(item);
                    }
//                updateExpensesList(myList);
                    Log.d(TAG,"has children");

                    listView.setVisibility(View.VISIBLE);
                    tv_noSplits.setVisibility(View.INVISIBLE);
                    imgNoSplits.setVisibility(View.INVISIBLE);
                    isSplit.setChecked(true);

            }
            else
                {
                    tv_noSplits.setVisibility(View.VISIBLE);
                    imgNoSplits.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    isSplit.setChecked(false);
                }
                adapter= new CustomAdapterSplits(dataModels,getContext());

                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                listView.invalidateViews();
                listView.refreshDrawableState();
                Log.d(TAG,"current cost in fetchSplit : "  +currentCost);
                updateTotalSplitAmount(currentCost);
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void updateTotalSplitAmount(final Double currentCost)
    {

        Query myQuery = mDatabase.child("users").child(user.getUid());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                User u = snap.getValue(User.class);

                Double budget = u.getBudget();

                Log.d(TAG,"Current expendtiure : " + currentCost);
                totalCost.setText("$ " + currentCost.toString());
                Double percent =(currentCost/budget)*100;
                int perc = percent.intValue();

                updateProgressBar(perc);
                mDatabase.child("users").child(user.getUid()).child("expenditure").setValue(currentCost);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }




}
