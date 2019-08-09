package com.sas.cashier.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sas.cashier.CustomAdapterSplits;
import com.sas.cashier.Data.Item;
import com.sas.cashier.Data.Splits;
import com.sas.cashier.Fragments.HomeFragment;
import com.sas.cashier.Fragments.ListFragment;
import com.sas.cashier.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


public class AddSplitsFragment extends Fragment {

    ListView listView;
    private ArrayList<Splits> dataModels = new ArrayList<>();
    private static CustomAdapterSplits adapter;
    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private BottomAppBar bottomAppBar;
    private TextView addSplits_tv;
    private FirebaseUser mUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        LayoutInflater lf = getActivity().getLayoutInflater();
        View v = lf.inflate(R.layout.fragment_addsplit, container, false);

        firebaseAuth  = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        listView = (ListView) v.findViewById(R.id.list_view_addsplit);


        addSplits_tv = v.findViewById(R.id.add_splits_text);



        adapter = new CustomAdapterSplits(dataModels, getContext());

        listView.setAdapter(adapter);


        fab = v.findViewById(R.id.fab);

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
//
//                Intent intent = new Intent(AddSplitsFragment.this, BarcodeCaptureActivity.class);
//                intent.putExtra(BarcodeCaptureActivity.AutoFocus,true);
//                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
//                startActivityForResult(intent, RC_BARCODE_CAPTURE);

                Log.d("TAG", "adding new item");
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text

                                        Splits s1 = new Splits(userInput.getText().toString().trim(), 0.00);
                                        dataModels.add(s1);
                                        adapter.notifyDataSetChanged();
                                        updateSplits(s1);

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        fetchSplits();


        return v;
    }
//
//        @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_addsplit);
//        firebaseAuth  = FirebaseAuth.getInstance();
//        mUser = firebaseAuth.getCurrentUser();
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//
//        listView = (ListView) findViewById(R.id.list_view_addsplit);
//
//
//
//
//        adapter = new CustomAdapterSplits(dataModels, this);
//
//        listView.setAdapter(adapter);
//
//
//        fab = findViewById(R.id.fab);
//
//        bottomAppBar = findViewById(R.id.navigation);
//        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                startActivity(new Intent(AddSplitsFragment.this, TabbedActivity.class));
//                return false;
//            }
//        });
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////
////                Intent intent = new Intent(AddSplitsFragment.this, BarcodeCaptureActivity.class);
////                intent.putExtra(BarcodeCaptureActivity.AutoFocus,true);
////                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
////                startActivityForResult(intent, RC_BARCODE_CAPTURE);
//
//                Log.d("TAG", "adding new item");
//                LayoutInflater li = LayoutInflater.from(AddSplitsFragment.this);
//                View promptsView = li.inflate(R.layout.prompts, null);
//
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddSplitsFragment.this);
//
//                // set prompts.xml to alertdialog builder
//                alertDialogBuilder.setView(promptsView);
//
//                final EditText userInput = (EditText) promptsView
//                        .findViewById(R.id.editTextDialogUserInput);
//
//                // set dialog message
//                alertDialogBuilder
//                        .setCancelable(false)
//                        .setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        // get user input and set it to result
//                                        // edit text
//
//                                        Splits s1 = new Splits(userInput.getText().toString().trim(), 0.00);
//                                        dataModels.add(s1);
//                                        adapter.notifyDataSetChanged();
//                                        updateSplits(s1);
//
//                                    }
//                                })
//                        .setNegativeButton("Cancel",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dialog.cancel();
//                                    }
//                                });
//
//                // create alert dialog
//                AlertDialog alertDialog = alertDialogBuilder.create();
//
//                // show it
//                alertDialog.show();
//            }
//        });
//
//        fetchSplits();
//
//
//
//    }
    private void updateSplits(Splits split)
    {
        String uniqueID = UUID.randomUUID().toString();
        Log.d("TAG","split: " + split);

        mDatabase.child("splits").child(mUser.getUid()).child(uniqueID).setValue(split);

        adapter.notifyDataSetChanged();

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

    private void fetchSplits()
    {

        Query myQuery = mDatabase.child("splits").child(mUser.getUid());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Item> myList = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!adapter.isEmpty())
                {
                    addSplits_tv.setVisibility(View.INVISIBLE);
                }
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Splits item = snap.getValue(Splits.class);
                    Log.d("TAG","Splits : " + item);
                    dataModels.add(item);
//                    myList.add(item);
                }
//                updateExpensesList(myList);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
