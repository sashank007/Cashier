package com.sas.cashier;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sas.cashier.Fragments.HomeFragment;
import com.sas.cashier.Fragments.ListFragment;


import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TabbedActivity extends AppCompatActivity {

    public static String TAG = "Debug";
    public static String MY_FRAGMENT = "MY_FRAGMENT";

    ColorStateList myStateList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Button showSheet;

    private LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;



    private BottomAppBar bottomAppBar;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private static final int RC_BARCODE_CAPTURE = 9001;

    public static int CurrentUserMaxSpendingAmount = 0;
    private FirebaseAuth firebaseAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(new HomeFragment());
                    return true;
                case R.id.navigation_dashboard:
                    loadFragment(new ListFragment());
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //@TODO:remove
        setContentView(R.layout.tabbed_activity);
        loadFragment(new HomeFragment());
        firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//        showSheet = findViewById(R.id.show_bottomsheet);

//        layoutBottomSheet=findViewById(R.id.bottom_sheet);

        //setting action bar
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
        Intent i = getIntent();
        Log.d("MAINACTIVITY","GETTING INTENT");

        if(i.hasExtra("FragmentCall"))
        {

            Log.d("MAINACTIVITY","inside hasExtra");
            String amount = this.getIntent().getExtras().getString("amount");
            callRequiredFragment(this.getIntent().getExtras().getString("FragmentCall"),amount);
        }


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

//        showSheet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//
//                }
//                else {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//
//                }
//            }
//        });



//         bottomAppBar = findViewById(R.id.navigation);
//        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.bottom_app_bar_home:
//                        loadFragment(new HomeFragment());
//                        return true;
//                    case R.id.bottom_app_bar_cart:
//                        loadFragment(new ListFragment());
//                        return true;
//
//                }
//                return false;
//            }
//        });

//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
//        navigation.setBackgroundColor(getResources().getColor(R.color.white));


    }





    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, "MY_FRAGMENT")
                    .commit();
            return true;
        }
        return false;
    }
//
//    private ColorStateList getColorStateList() {
//        int[][] states = new int[][]{
//                new int[]{android.R.attr.state_enabled}, // enabled
//                new int[]{-android.R.attr.state_enabled}, // disabled
//                new int[]{android.R.attr.state_pressed},
//                new int[]{-android.R.attr.state_pressed} // pressed
//
//        };
//
////        int[] colors = new int[]{
////                getColor(R.color.colorGrey),
////                Color.GRAY,
////                getColor(R.color.colorPrimaryDark),
////                getColor(R.color.colorPrimaryDark)
////        };
////        ColorStateList myList = new ColorStateList(states, colors);
//        return myList;
//    }


//    public void pushNotification(String msgText, String msgTitle) {
//        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        String NOTIFICATION_CHANNEL_ID = "my_channel_id_02";
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
//
//            // Configure the notification channel.
//            notificationChannel.setDescription("Channel description");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
//            notificationChannel.enableVibration(true);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//
//        Intent sfIntent = new Intent(this, MainActivity.class);
//        //dismiss the dialog
//        Intent noIntent = new Intent(this, DismissReceiver.class);
//        Intent yesIntent = new Intent(this, DismissReceiver.class);
//        noIntent.putExtra("gotoFragment", "Close");
//        yesIntent.putExtra("gotoFragment", "ExpenseTrackerFragment");
//
//        //Create the PendingIntent
//        PendingIntent btPendingIntentNo = PendingIntent.getBroadcast(this, 1234, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent btPendingIntentYes = PendingIntent.getBroadcast(this, 1234, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        NotificationCompat.Action actionno = new NotificationCompat.Action.Builder(R.drawable.ic_thumb_down_grey600_48dp, "No", btPendingIntentNo).build();
//        NotificationCompat.Action actionyes = new NotificationCompat.Action.Builder(R.drawable.ic_thumb_up_grey600_48dp, "Yes", btPendingIntentYes).build();
//
//        notificationBuilder.setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.mom_logo)
//                .setTicker(getString(R.string.app_name))
//                //     .setPriority(Notification.PRIORITY_MAX)
//                .setContentTitle(msgTitle)
//                .setContentText(msgText)
////                .addAction(R.drawable.ic_thumb_up_grey600_48dp , "No" , btPendingIntentNo)
////                .addAction(R.drawable.ic_thumb_down_grey600_48dp , "Yes" , btPendingIntentYes )
//                .addAction(actionno)
//                .addAction(actionyes)
//                .setContentInfo("Info");
//
//        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
//    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // User chose the "Settings" item, show the app settings UI...

                return true;
            case R.id.action_logout:
                firebaseAuth.signOut();
                startActivity(new Intent(this,LoginActivity.class));

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void callRequiredFragment(String extra , String amount)
    {
        Log.d("MAINACTIVITY","sindie call required fragment");
        if(extra.equals("listFragment"))
        {
            Fragment fragment = new ListFragment();
            Bundle args = new Bundle();

            args.putString("amount",amount);
            args.putString("FragmentCall","ExpenseTrackerFragment");
            Log.d("STARTFRAGMENTACTIVITY","received amount from start fragment activity"  + amount);
            args.putLong("DateSelected", System.currentTimeMillis());
            fragment.setArguments(args);

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();

            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

//                    fetchUPCData(barcode.displayValue);
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


}


